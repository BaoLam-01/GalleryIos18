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


const float PI = 3.14159265;

const float uD = 80.0;
const float uR = 1.0;

vec4 transition(vec2 uv) {
    return mix(
    getFromColor(uv),
    getToColor(uv),
    progress
    );
}


void main() {
    //周期
    float duration = 1.0;
    //放大的最大比例
    float maxScale = 1.1;
    //颜色偏移值
    float offset = 0.02;
    if(progress>0.0){
        float progressed = mod(progress, duration) / duration; // 0~1
        //具体的偏移量
        vec2 offsetCoords = vec2(offset, offset) * progressed;
        //图层放大缩小的比例
        float scale = 1.0 + (maxScale - 1.0) * progressed;

        //获取缩放之后实际纹理坐标
        vec2 ScaleTextureCoords = vec2(0.5, 0.5) + (textureCoordinate - vec2(0.5, 0.5)) / scale;

        //设置缩放之后的纹理坐标和经过具体的颜色偏移坐标
        //三组分别代表RGB不同方向的纹理像素值
        vec4 maskR = texture2D(inputImageTexture, ScaleTextureCoords + offsetCoords);
        vec4 maskB = texture2D(inputImageTexture, ScaleTextureCoords - offsetCoords);
        vec4 mask = texture2D(inputImageTexture, ScaleTextureCoords);

        //根据不同的纹理坐标值得到经过颜色偏移之后的颜色
        gl_FragColor = vec4(maskR.r, mask.g, maskB.b, mask.a);
    }else{
        gl_FragColor = texture2D(inputImageTexture, textureCoordinate);
    }
}
