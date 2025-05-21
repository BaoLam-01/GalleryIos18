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

vec4 transition(vec2 uv) {
    return mix(
    getFromColor(uv),
    getToColor(uv),
    progress
    );
}


void main() {
    if (progress>0.0){
        float rate= 2264.0 / 1080.0;
        float length = progress/20.0;
        float TR = 0.866025;

        //纹理坐标值
        float x = textureCoordinate.x;
        float y = textureCoordinate.y;

        //转化为矩阵中的坐标
        int wx = int(x / 1.5 / length);
        int wy = int(y / TR / length);
        vec2 v1, v2, vn;

        //分析矩阵中的坐标是在奇数还是在偶数行，根据奇数偶数值来确定我们的矩阵的角标坐标值
        if (wx/2 * 2 == wx) {
            if (wy/2 * 2 == wy) {
                //(0,0),(1,1)
                v1 = vec2(length * 1.5 * float(wx), length * TR * float(wy));
                v2 = vec2(length * 1.5 * float(wx + 1), length * TR * float(wy + 1));
            } else {
                //(0,1),(1,0)
                v1 = vec2(length * 1.5 * float(wx), length * TR * float(wy + 1));
                v2 = vec2(length * 1.5 * float(wx + 1), length * TR * float(wy));
            }
        } else {
            if (wy/2 * 2 == wy) {
                //(0,1),(1,0)
                v1 = vec2(length * 1.5 * float(wx), length * TR * float(wy + 1));
                v2 = vec2(length * 1.5 * float(wx + 1), length * TR * float(wy));
            } else {
                //(0,0),(1,1)
                v1 = vec2(length * 1.5 * float(wx), length * TR * float(wy));
                v2 = vec2(length * 1.5 * float(wx + 1), length * TR * float(wy + 1));
            }
        }

        //获取距离
        float s1 = sqrt(pow(v1.x - x, 2.0) + pow(v1.y - y, 2.0));
        float s2 = sqrt(pow(v2.x - x, 2.0) + pow(v2.y - y, 2.0));

        //设置具体的纹理坐标
        if (s1 < s2) {
            vn = v1;
        } else {
            vn = v2;
        }
        vec4 color = texture2D(inputImageTexture, vn);
        gl_FragColor = color;
    } else {
        gl_FragColor=texture2D(inputImageTexture, textureCoordinate);
    }
}
