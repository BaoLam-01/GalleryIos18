#version 120

precision mediump float;

uniform sampler2D u_Texture1;
uniform sampler2D u_Texture2;
uniform float u_Alpha;

varying vec2 v_TexCoord;

void main() {
    vec2 center = vec2(0.5, 0.5);

    vec2 uv1 = (v_TexCoord - center) * (1.0 - 0.3 * u_Alpha) + center;
    vec2 uv2 = (v_TexCoord - center) * (1.0 + 0.3 * (1.0 - u_Alpha)) + center;

    vec4 color1 = texture2D(u_Texture1, uv1);
    vec4 color2 = texture2D(u_Texture2, uv2);

    gl_FragColor = mix(color1, color2, u_Alpha);
}
