precision mediump float;

varying mediump vec2 textureCoordinate;
uniform sampler2D inputImageTexture;
vec3 iResolution = vec3(1.0);
uniform lowp float progress;

const vec4 iMouse = vec4(0.5, 0.5, 0., 1.);

void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
    float ChromaticAberration = 7.0 * progress;
    vec2 uv = fragCoord.xy / iResolution.xy;

    vec2 texel = 3.0 / iResolution.xy;

    vec2 coords = (uv - 0.5) * 1.5;
    float coordDot = dot (coords, coords);

    vec2 precompute = ChromaticAberration * coordDot * coords;
    vec2 uvR = uv - texel.xy * precompute;
    vec2 uvB = uv + texel.xy * precompute;

    vec4 color = vec4(1.0);
    color.r = texture2D(inputImageTexture, uvR).r;
    color.g = texture2D(inputImageTexture, uv).g;
    color.b = texture2D(inputImageTexture, uvB).b;

    fragColor = color;
}


void main() {
    if (progress > 0.0){
        mainImage(gl_FragColor, textureCoordinate);
    } else {
        gl_FragColor = texture2D(inputImageTexture, textureCoordinate);
    }
}

