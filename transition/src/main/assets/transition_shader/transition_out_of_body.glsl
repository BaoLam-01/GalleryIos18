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

//vec4 transition(vec2 uv) {
//    return mix(
//    getFromColor(uv),
//    getToColor(uv),
//    progress
//    );
//}


void main() {
    float duration = 1.0;
    //透明度上限值
    float maxAlpha = 0.8;
    //图片放大的上限
    float maxScale = 1.8;

    //当前进度（时间戳与时长使用mod取模）,再除以时长 得到【0， 1】，即百分比
    float progress = mod(progress, duration) / duration;// 0~1
    //当前透明度 【0.4， 0】
    float alpha = maxAlpha * (1.0 - progress);
    //当前缩放比例 【1.0， 1.8】
    float scale = 1.0 + (maxScale - 1.0) * progress;

    //获取放大后的纹理坐标
    //将顶点坐标对应的纹理坐标的x/y值到中心点的距离，缩小一定的比例，仅仅只是改变了纹理坐标，而保持顶点坐标不变，从而达到拉伸效果
    float weakX = 0.5 + (textureCoordinate.x - 0.5) / scale;
    float weakY = 0.5 + (textureCoordinate.y - 0.5) / scale;
    vec2 weakTextureCoords = vec2(weakX, weakY);

    //获取当前像素点纹理坐标，放大后的纹理坐标
    vec4 weakMask = texture2D(inputImageTexture, weakTextureCoords);
    vec4 mask = texture2D(inputImageTexture2, textureCoordinate);
    //    vec4 mask = transition(textureCoordinate);
    if(progress>0.0){
        gl_FragColor = mask * (1.0 - alpha) + weakMask * alpha;
    }else{
        gl_FragColor = getFromColor(textureCoordinate);
    }
}
