precision mediump float;

varying mediump vec2 textureCoordinate;

uniform sampler2D inputImageTexture;
uniform lowp float progress;
float sampleHeight(in vec2 coord)
{
    return ((progress)/220.0-0.48) *
    dot(texture2D(inputImageTexture, coord), vec4(1.0/3.0, 1.0/3.0, 1.0/3.0, 0.0));
}

void main()
{
    vec2 uv = textureCoordinate;
    vec2 du = vec2(1.0 / 1024.0, 0.0);
    vec2 dv = vec2(0.0, 1.0 / 1024.0);

    float h0 = sampleHeight(uv);
    float hpx = sampleHeight(uv + du);
    float hmx = sampleHeight(uv - du);
    float hpy = sampleHeight(uv + dv);
    float hmy = sampleHeight(uv - dv);

    float dHdU = (hmx - hpx) / (2.0 * du.x);
    float dHdV = (hmy - hpy) / (2.0 * dv.y);

    vec3 normal = normalize(vec3(dHdU, dHdV, 1.0));

    gl_FragColor = vec4(0.5 + 0.5 * normal, 1.0);
}
