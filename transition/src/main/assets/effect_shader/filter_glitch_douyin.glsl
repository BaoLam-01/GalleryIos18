precision mediump float;

varying mediump vec2 textureCoordinate;

uniform sampler2D inputImageTexture;
uniform lowp float progress;

vec4 getFromColor(vec2 uv){
    return texture2D(inputImageTexture, uv);
}


void main() {
    float duration = 2.0;
    //放大图片的上限
    float maxScale = 1.1;
    //颜色偏移的步长
    float offset = 0.02;

    //进度 0 ~ 1
//    float progress = mod(progress, duration) / duration;
    float progressed = 1.0;
    if(progress>0.0){
        progressed=20.0/progress;
    }
    progressed = mod(progressed, duration);
    //颜色偏移值0 ~ 0.02
    vec2 offsetCoords = vec2(offset, offset) * progressed;
    //缩放比例 1.0 ~ 1.1
    float scale = 1.0 + (maxScale - 1.0) * progressed;

    //放大后的纹理坐标
    //下面这种向量相加减的方式 等价于 灵魂出窍滤镜中的单个计算x、y坐标再组合的为纹理坐标的方式
    vec2 ScaleTextureCoords = vec2(0.5, 0.5) + (textureCoordinate - vec2(0.5, 0.5)) / scale;

    //获取三组颜色：颜色偏移计算可以随意，只要偏移量很小即可
    //原始颜色 + offset
    vec4 maskR = texture2D(inputImageTexture, ScaleTextureCoords + offsetCoords);
    //原始颜色 - offset
    vec4 maskB = texture2D(inputImageTexture, ScaleTextureCoords - offsetCoords);
    //原始颜色
    vec4 mask = texture2D(inputImageTexture, ScaleTextureCoords);

    gl_FragColor = vec4(maskR.r, maskB.g, mask.b, mask.a);
}
