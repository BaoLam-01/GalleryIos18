precision mediump float;

varying mediump vec2 textureCoordinate;
uniform sampler2D inputImageTexture;
uniform lowp float progress;

const float intensity1 = 0.1; // = 0.1
const int passes = 2;

vec4 getFromColor(vec2 uv) {
    return texture2D(inputImageTexture, vec2(uv.x,  uv.y));
}

void main() {
    vec4 c1 = vec4(0.0);
    vec4 c2 = vec4(0.0);

    float disp = intensity1*(1.0-distance(1.0, progress));
    for (int xi=0; xi<passes; xi++)
    {
        float x = float(xi) / float(passes) - 0.5;
        for (int yi=0; yi<passes; yi++)
        {
            float y = float(yi) / float(passes) - 0.5;
            vec2 v = vec2(x,y);
            float d = disp;
            c1 += getFromColor( textureCoordinate + d*v);
//            c2 += getToColor( uv + d*v);
        }
    }
    c1 /= float(passes*passes);
//    c2 /= float(passes*passes);
//    return mix(c1, c2, progress);
    gl_FragColor  =mix(c1,c2, 0.0);
}


