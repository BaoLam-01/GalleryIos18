precision mediump float;

varying mediump vec2 textureCoordinate;
uniform sampler2D inputImageTexture;

vec3 iResolution = vec3(1.0);
const float delta = 0.002;
const float LUMINANCE_VALUES =4.5;
uniform lowp float progress;

float luminance(vec3 col) {
    float l = (col.r + col.g + col.b) / 6.0;
    return l;
}


void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
    vec2 uv = fragCoord/iResolution.xy;

    vec3 here_col = texture2D(inputImageTexture, uv).rgb;
    float here = luminance(here_col);

    float right = luminance(texture2D(inputImageTexture, uv + vec2(delta, 0.0)).rgb);
    float left = luminance(texture2D(inputImageTexture, uv + vec2(-delta, 0.0)).rgb);
    float above = luminance(texture2D(inputImageTexture, uv + vec2(0.0, delta)).rgb);
    float below = luminance(texture2D(inputImageTexture, uv + vec2(0.0, -delta)).rgb);


    float edges = here * 4.0 - right - left - above - below;
    float blur = (right + left + above + below + here) / 4.0;
    edges = clamp(edges*2.0, -1.0, progress);

    float luminance = floor((blur) * LUMINANCE_VALUES) / LUMINANCE_VALUES;

    vec3 hue = (here_col - here);

    float outval = luminance + edges;
    vec3 col = hue + vec3(outval);//vec3(luminance);

    // Output to screen
    fragColor = vec4(vec3(col),1.0);
}


void main() {
    mainImage(gl_FragColor, textureCoordinate*iResolution.xy);
}
