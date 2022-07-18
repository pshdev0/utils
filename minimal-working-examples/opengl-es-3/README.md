# opengl-es-3

A ready made OpenGL Renderer shell written in Kotlin, OpenGL ES 3.0 and GLSL.

It's a Hello World for OpenGL ES 3.0 but with all the advanced features at your fingertips, ready to be moulded for your use. See the inline comment on using  glMapBufferRange over glBufferData to reduce data transfer.

* Texture Array bundles all your textures into one object.
* Use any atlas you like inside your shader easily.
* Blit your transformation data to a single Buffer Object.
* Assign attributes to your Buffer Object for convenient shader access.
* Instanced rendering for efficiency.
* Can easily use glMapBuffer for direct GPU memory access.
