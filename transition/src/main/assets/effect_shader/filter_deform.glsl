precision mediump float;

varying mediump vec2 textureCoordinate;
uniform sampler2D inputImageTexture;
uniform float progress;

vec3 iResolution = vec3(1.0);

void mainImage(out vec4 fragColor, in vec2 fragCoord)
{
    float stongth = 5.3;
    vec2 uv = fragCoord.xy;
    float waveu = sin((0.4*uv.y + progress*0.2) * 20.0) * 0.5 * 0.05 * stongth;
    fragColor = texture2D(inputImageTexture, uv + vec2(waveu, 0));
}

void main() {
    mainImage(gl_FragColor, textureCoordinate);
}
