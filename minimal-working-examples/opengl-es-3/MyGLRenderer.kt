import android.opengl.GLES10.glActiveTexture
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

import android.opengl.GLES30.*
import android.opengl.GLSurfaceView
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import java.nio.Buffer
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import kotlin.system.exitProcess

class MyGLRenderer() : GLSurfaceView.Renderer {

    // number of coordinates per vertex in this array
    val COORDS_PER_VERTEX = 3
    var triangleCoords = floatArrayOf(     // in counterclockwise order:
        0.0f, 0.622008459f, 0.0f,      // top
        -0.5f, -0.311004243f, 0.0f,    // bottom left
        0.5f, -0.311004243f, 0.0f      // bottom right
    )

    // Set color with red, green, blue and alpha (opacity) values
    val color = floatArrayOf(1.0f, 1.0f, 1.0f, 1.0f)

    private var vertexBuffer: FloatBuffer = makeBuffer(triangleCoords)
    private var constFloatsPerInstance = 6
    private var transformBuffer: FloatBuffer = makeBuffer(constFloatsPerInstance * 100)

    private var uiProgram: Int = 0

    private var myTexture: Int = -1
    private var vboBuffer: Int = 0

    private var transformPositionHandle: Int = 0
    private var transformColorHandle: Int = 0
    private var positionHandle: Int = 0
    private var mColorHandle: Int = 0

    private val vertexCount: Int = triangleCoords.size / COORDS_PER_VERTEX
    private val vertexStride: Int = COORDS_PER_VERTEX * 4 // 4 bytes per vertex

    private var glErrorCount: Int = 0

    private var mSamplerLoc: Int = 0

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f) // Set the background frame color

        val uiVertexShaderCode =
            context.assets.open("uiVertexShader.glsl").bufferedReader().use { it.readText() }
        val uiFragmentShaderCode =
            context.assets.open("uiFragmentShader.glsl").bufferedReader().use { it.readText() }

        val uiVertexShader: Int = loadShader(GL_VERTEX_SHADER, uiVertexShaderCode)
        val uiFragmentShader: Int = loadShader(GL_FRAGMENT_SHADER, uiFragmentShaderCode)

        // create empty OpenGL ES Program
        uiProgram = glCreateProgram().also {
            glAttachShader(it, uiVertexShader) // add the vertex shader to program
            glAttachShader(it, uiFragmentShader)   // add the fragment shader to program
            glLinkProgram(it)    // creates OpenGL ES program executables
        }

        // LOAD TEXTURES

        val texWidth = 1024
        val texHeight = 1024
        val numAtlases = 2 // how many texture atlases are we using

        // MAKE THE ARRAY TEXTURE

        val textureUnit = 0  // For example.
        glActiveTexture(GL_TEXTURE0 + textureUnit);

        // load pixel data and hand it over to OpenGL into a 2d array.
        val textures = IntArray(1)
        glGenTextures(1, textures, 0);

        myTexture = textures[0]
        glBindTexture(GL_TEXTURE_2D_ARRAY, myTexture);

        // create space for the texture array
        glTexImage3D(
            GL_TEXTURE_2D_ARRAY,
            0,
            GL_RGBA8,
            texWidth,
            texHeight,
            numAtlases,
            0,
            GL_RGBA,
            GL_UNSIGNED_BYTE,
            null
        );

        // populate the texture array
        for (index in 0 until numAtlases) {
            glTexSubImage3D(
                GL_TEXTURE_2D_ARRAY,
                0,
                0,
                0,
                index,
                texWidth,
                texHeight,
                1,
                GL_RGBA,
                GL_UNSIGNED_BYTE,
                ByteBuffer.wrap(getMyAtlasBytes(index))
            )
        }

        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)

        // connect the texture to the shader
        transformColorHandle = glGetAttribLocation(uiProgram, "vColorMain")
        transformPositionHandle = glGetAttribLocation(uiProgram, "vTransformPosition")
        positionHandle = glGetAttribLocation(uiProgram, "vPosition")
        mColorHandle = glGetUniformLocation(uiProgram, "vColor")
        mSamplerLoc = glGetUniformLocation(uiProgram, "texArray")

        glUseProgram(uiProgram) // seems this needs to be set for set uniforms to work !

        glUniform1i(mSamplerLoc, textureUnit) // this was a bit tricky, e.g. using myTexture failed but textyreUnit works (not quite sure if 0 is just not working by default though)
        glUniform4fv(mColorHandle, 1, color, 0) // Set color for drawing the triangle

        // make the transform buffer (x, y, r, g, b, a)
        transformBuffer.put(floatArrayOf(-0.5f, -0.5f, 1f, 1f, 1f, 1f,
            0f, 0f, 0f, 1f, 0f, 1f))
        transformBuffer.position(0) // must reposition the buffer or it won't work !

        val buffers = IntArray(1)
        glGenBuffers(1, buffers, 0)
        vboBuffer = buffers[0]

        glBindBuffer(GL_ARRAY_BUFFER, vboBuffer)
        glBufferData(GL_ARRAY_BUFFER, constFloatsPerInstance*100, transformBuffer, GL_STATIC_DRAW) // use GL_DYNAMIC_DRAW hint for a frequently updated buffer
        glBindBuffer(GL_ARRAY_BUFFER, 0) // unbind the buffer object
	    
        /*
		Above we use glBufferData which copies a CPU side buffer over to the GPU.
		
		This is usually quite fast, but does involve an extra copy which we can
		avoid by instead using glMapBufferRange which returns a pointer which maps
		directly to the GPU memory. We can then just populate this memory directly
		and remove the glBufferData completely. This is good when sprite batching,
		but whether or not you want to use it depends entirely on your use case.

		If you do use memory mapping, just bind you buffer to GL_ARRAY_BUFFER, map
		it using glMapBufferRange to get a pointer to the data, update your sprite
		transform buffers (or whatever), then glUnmapBuffer. Note - you can easily
		map multiple buffers by just rebinding and mapping, but don't forget to
		unbind before GL renders.

		You can avoid mapping and unmapping altogether by using EXT_buffer_storage
		extension and GL_MAP_PERSISTENT which returns a persistent memory pointer
		which does not need to be unmapped. I haven't tested this yet but in general
		it would mean less GL commands in your main loop. CAVEAT: to use PERSISTENT
		mapping requires you create your buffers using glBufferStorage with the
		GL_MAP_PERSISTENT_BIT flag set, but OpenGL ES does not support glBufferStorage,
		which is really confusing considering e.g. Android shows EXT_buffer_storage
		as present ! So it seems this would only be possible on desktop Open GL, and
		I believe it's standard on desktop OepnGL from version 4.?. Anyway, you can
		stick with mapping/unmapping every frame for OpenGL ES for the time being and
		tests show this takes practically zero time at all. I'll do a Vulkan port
		eventually and ditch OpenGL altogether, which will probably fix this anyway.
		
		Anyway, I think it's probably best to use glMapBufferRange to avoid the
		copy required from CPU to GPU via glBufferData, although I think it would
		only really matter for much larger data processing than I'm using in my 2D
		games at the moment. Will have to experiment with much larger game arenas
		and find out !
		
	*/

        // set its vertex attribute pointer and enable the vertex attribute:
        glBindBuffer(GL_ARRAY_BUFFER, vboBuffer)

	// vec2 for position
        glVertexAttribPointer(
            transformPositionHandle,
            2,
            GL_FLOAT,
            false,
            constFloatsPerInstance * 4, // 4 bytes float
            0, // initial offset into the data for this attribute
        )
        glEnableVertexAttribArray(transformPositionHandle)

	// vec4 for RGBA
        glVertexAttribPointer(
            transformColorHandle,
            4,
            GL_FLOAT,
            false,
            constFloatsPerInstance * 4, // 4 bytes per float
            8, // initial offset into the data for this attribute
        )
        glEnableVertexAttribArray(transformColorHandle)

        glBindBuffer(GL_ARRAY_BUFFER, 0); // unbind
        glVertexAttribDivisor(transformPositionHandle, 1);
        glVertexAttribDivisor(transformColorHandle, 1);
    }

    fun showGLError(str: String = "") {
        Log.v("mydebug", "error $glErrorCount ($str) = ${glGetError()}")
        this.glErrorCount++
    }

    private fun makeBuffer(data: FloatArray) : FloatBuffer {
        val size = data.size * 4 // # float elements * 4 bytes
        return ByteBuffer.allocateDirect(size).run {
            order(ByteOrder.nativeOrder()) // use the device hardware's native byte order
            // create a floating point buffer from the ByteBuffer
            asFloatBuffer().apply {
                put(data) // add the coordinates to the FloatBuffer
                position(0) // set the buffer to read the first coordinate
            }
        }
    }

    private fun makeBuffer(numFloats: Int) : FloatBuffer {
        val size = numFloats * 4 // # float elements * 4 bytes
        return ByteBuffer.allocateDirect(size).run {
            order(ByteOrder.nativeOrder()) // use the device hardware's native byte order
            // create a floating point buffer from the ByteBuffer
            asFloatBuffer().apply {
                position(0) // set the buffer to read the first coordinate
            }
        }
    }

    private fun loadShader(type: Int, shaderCode: String): Int {
        val rtn = glCreateShader(type).also { shader ->
            glShaderSource(shader, shaderCode)
            glCompileShader(shader)
        }

        val shaderDebug = glGetShaderInfoLog(rtn)
        if(shaderDebug.compareTo("") != 0) {
            context.db("\n\n* Shader Error *\n\n${shaderDebug}")
            exitProcess(-1)
        }

        return rtn
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    override fun onDrawFrame(unused: GL10) {
        glClear(GL_COLOR_BUFFER_BIT) // Redraw background color
        glUseProgram(uiProgram) // Add program to OpenGL ES environment

        glEnableVertexAttribArray(positionHandle) // Enable a handle to the triangle vertices
        glEnableVertexAttribArray(transformPositionHandle) // Enable a handle to the triangle vertices

        glVertexAttribPointer(
            positionHandle,
            COORDS_PER_VERTEX,
            GL_FLOAT,
            false,
            vertexStride,
            vertexBuffer
        ) // prepare the triangle coordinate data

        glDrawArraysInstanced(GL_TRIANGLES, 0, vertexCount, 2)
        glDisableVertexAttribArray(positionHandle) // Disable vertex array
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        glViewport(0, 0, width, height)
    }
}
