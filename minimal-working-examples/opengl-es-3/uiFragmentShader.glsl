#version 300 es

precision mediump float;

uniform vec4 vColor; // we don't actually use this, but this is how you'd send a uniform up

uniform mediump sampler2DArray texArray; // seems to need mediump explicitly set here

in vec2 texCoords; // uv coordinates (sent out from the vertex shader)
in vec4 fvColorMain;

out vec4 fragColor; // fragment shader output (color !)

void main() {
    vec4 texel = texture(texArray, vec3(texCoords, 0)); // use texture array "slice" 0
    fragColor = texel * fvColorMain;
}
