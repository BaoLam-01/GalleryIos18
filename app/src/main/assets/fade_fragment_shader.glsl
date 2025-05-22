#version 120

precision mediump float;

uniform sampler2D u_Texture1; // current
uniform sampler2D u_Texture2; // next
uniform float u_Alpha;        // 0.0 -> 1.0

varying vec2 v_TexCoord;

void main() {
    vec4 color1 = texture2D(u_Texture1, v_TexCoord);
    vec4 color2 = texture2D(u_Texture2, v_TexCoord);
    gl_FragColor = mix(color1, color2, u_Alpha);
}
