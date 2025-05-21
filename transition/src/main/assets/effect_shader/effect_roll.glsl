precision mediump float;

varying mediump vec2 textureCoordinate;
uniform lowp float progress;
uniform sampler2D inputImageTexture;
vec3 iResolution = vec3(1.0);

float triangle(float x) {
    return 1.0 - abs(fract(x) - 0.5) * 2.0;
}

float smootherstep(float x) {
    return x * x * x * (x * (x * 6.0 - 15.0) + 10.0);
}

float endPosition(float radius, float offset, float rollPosition) {
    float rollLength = radius * 3.141592 / 2.0;
    float onRoll = min(offset + 1.0 - rollPosition, rollLength);
    return rollPosition + radius * sin(onRoll / radius);
}

float findRollPosition(float radius, float offset) {
    float lower = 1.0 - radius;
    float upper = 1.0;

    float mid = 0.0;
    for(int i = 0; i < 16; i++) {
        mid = (upper + lower) / 2.0;
        float position = endPosition(radius, offset, mid);
        if(position > 1.0) {
            upper = mid;
        } else {
            lower = mid;
        }
    }
    return mid;
}

void main() {
    vec2 p = textureCoordinate/iResolution.xy;
    float t = progress / 2.0;
    float offset = smootherstep(triangle(t));

    float radius = 0.2;
    float flatLength = 1.0 - radius;
    float rollLength = radius * 3.141592 / 2.0;
    float fullLength = flatLength + rollLength;

    offset *= fullLength;

    float rollPosition = findRollPosition(radius, offset);

    vec2 uv = p;
    float shadow;

    if(p.x < rollPosition) {
        uv.x -= offset;
        shadow = 1.0;
    } else {
        float a = asin((p.x - rollPosition) / radius);
        uv.x = a * radius - offset + rollPosition;
        uv.y = (p.y - 0.5) / (0.2 * cos(a) + 0.8) + 0.5;
        shadow = 0.5 * cos(a) + 0.5;
    }

    vec4 col = texture2D(inputImageTexture, uv);
    col *= step(-uv.x, 0.0);
    col *= step(-uv.y, 0.0);
    col *= step(uv.y, 1.0);
    col *= shadow;

    gl_FragColor = col;
}

