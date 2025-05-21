precision mediump float;

varying mediump vec2 textureCoordinate;
uniform sampler2D inputImageTexture;
uniform float progress;

vec3 iResolution = vec3(1.0);


float random2d(vec2 n) {
    return fract(sin(dot(n, vec2(12.9898, 4.1414))) * 45.5453);
}

float randomRange (in vec2 seed, in float min, in float max) {
    return min + random2d(seed) * (max - min);
}

// return 1 if v inside 1d range
float insideRange(float v, float bottom, float top) {
    return step(bottom, v) - step(top, v);
}

//inputs
float AMT = 0.2; //0 - 1 glitch amount
float SPEED = 0.5; //0 - 1 speed

vec2 zoom(vec2 uv, float amount) {
    return 0.5 + ((uv - 0.5) * (1.0-amount));
}

void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
    float time = floor(progress * SPEED * 60.0);
    fragColor = texture2D(inputImageTexture, zoom(fragCoord,progress));
}

void main() {
    mainImage(gl_FragColor, textureCoordinate);
}