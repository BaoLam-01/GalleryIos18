precision mediump float;

varying mediump vec2 textureCoordinate;

uniform sampler2D inputImageTexture;
uniform lowp float progress;

void main()
{
    vec3 orig = texture2D(inputImageTexture, textureCoordinate).xyz;
    vec3 col = orig * orig * 1.4;
    float bri = (col.x+col.y+col.z)/3.0;
    float v = smoothstep(.0, .7, bri);
    col = mix(vec3(0., 1., 1.2) * bri, col, v);
    v = smoothstep(.2, 1.1, bri);
    col = mix(col, min(vec3(1.0, .55, 0.) * col * 1.2, 1.0), v);
    col = mix(orig, col, 1.);
    gl_FragColor = vec4(min(col, 1.0),progress);
}
