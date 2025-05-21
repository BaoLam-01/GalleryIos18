precision mediump float;
varying mediump vec2 textureCoordinate;
uniform sampler2D inputImageTexture;
uniform sampler2D inputImageTexture2;
uniform lowp float progress;

vec4 getFromColor(vec2 uv){
    return texture2D(inputImageTexture, uv);
}

vec4 getToColor(vec2 uv){
    return texture2D(inputImageTexture2, uv);
}

float steps = 50.0;
float ratio = 1.0;
float horizontalHexagons = 20.0;

struct Hexagon {
    float q;
    float r;
    float s;
};

Hexagon createHexagon(float q, float r){
    Hexagon hex;
    hex.q = q;
    hex.r = r;
    hex.s = -q - r;
    return hex;
}

Hexagon roundHexagon(Hexagon hex){
    float q = floor(hex.q + 0.5);
    float r = floor(hex.r + 0.5);
    float s = floor(hex.s + 0.5);

    float deltaQ = abs(q - hex.q);
    float deltaR = abs(r - hex.r);
    float deltaS = abs(s - hex.s);

    if (deltaQ > deltaR && deltaQ > deltaS)
    q = -r - s;
    else if (deltaR > deltaS)
    r = -q - s;
    else
    s = -q - r;

    return createHexagon(q, r);
}

Hexagon hexagonFromPoint(vec2 point, float size) {

    point.y /= ratio;
    point = (point - 0.5) / size;

    float q = (sqrt(3.0) / 3.0) * point.x + (-1.0 / 3.0) * point.y;
    float r = 0.0 * point.x + 2.0 / 3.0 * point.y;

    Hexagon hex = createHexagon(q, r);
    return roundHexagon(hex);

}

vec2 pointFromHexagon(Hexagon hex, float size) {

    float x = (sqrt(3.0) * hex.q + (sqrt(3.0) / 2.0) * hex.r) * size + 0.5;
    float y = (0.0 * hex.q + (3.0 / 2.0) * hex.r) * size + 0.5;

    return vec2(x, y * ratio);
}

vec4 transition (vec2 uv) {
    float dist = 2.0 * min(progress, 1.0 - progress);
    dist = steps > 0.0 ? ceil(dist *steps) / steps : dist;
    float size = (sqrt(3.0) / 3.0) * dist / horizontalHexagons;
    vec2 point = dist > 0.0 ? pointFromHexagon(hexagonFromPoint(uv, size), size) : uv;
    return mix(getFromColor(point), getToColor(point), progress);
}

void main() {
    gl_FragColor = transition(textureCoordinate);
}
