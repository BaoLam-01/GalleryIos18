precision mediump float;

varying mediump vec2 textureCoordinate;

uniform sampler2D inputImageTexture;
uniform sampler2D inputImageTextureOverlay;
uniform lowp float progress;

void main()
{
    mediump vec4 base = texture2D(inputImageTexture, textureCoordinate);
    mediump vec4 overlay = texture2D(inputImageTextureOverlay, textureCoordinate);
    mediump float ra;
        ra = overlay.a * base.a - 2.0 * (base.a - base.r) * (overlay.a - overlay.r) + overlay.r * (1.0 - base.a) + base.r * (1.0 - overlay.a);

    mediump float ga;
        ga = overlay.a * base.a - 2.0 * (base.a - base.g) * (overlay.a - overlay.g) + overlay.g * (1.0 - base.a) + base.g * (1.0 - overlay.a);
    mediump float ba;
        ba = overlay.a * base.a - 2.0 * (base.a - base.b) * (overlay.a - overlay.b) + overlay.b * (1.0 - base.a) + base.b * (1.0 - overlay.a);

    gl_FragColor = vec4(ra, ga, ba,progress);
}
