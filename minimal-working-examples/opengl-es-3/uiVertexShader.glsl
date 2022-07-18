#version 300 es

in vec4 vPosition;
in vec2 vTransformPosition;
in vec4 vColorMain;

out vec2 texCoords;
out vec4 fvColorMain;

void main() {
    // you'd do your transformations here before setting gl_Position
    // we'll just add an x and y to the position for now
    gl_Position = vec4(vPosition.x + vTransformPosition.x, vPosition.y + vTransformPosition.y, vPosition.z, vPosition.w);

    // set the color to pass to the fragment shader
    fvColorMain = vColorMain;

    // you'd usually send the texCoords up with the transformation data, but here we specify it depending on vertex id
    switch(gl_VertexID) {
        case 0:
            texCoords = vec2(0, 0);
            break;

        case 1:
            texCoords = vec2(1, 0);
            break;

        case 2:
            texCoords = vec2(1, 1);
            break;
    }
}
