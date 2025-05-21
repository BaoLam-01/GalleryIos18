precision mediump float;

varying mediump vec2 textureCoordinate;

uniform sampler2D inputImageTexture;
uniform lowp float progress;

vec4 getFromColor(vec2 uv){
    return texture2D(inputImageTexture, uv);
}

//vec4 getToColor(vec2 uv){
//    return texture2D(inputImageTexture2, uv);
//}

//vec4 transition(vec2 uv) {
//    return mix(
//    getFromColor(uv),
//    getToColor(uv),
//    progress
//    );
//}
const float PI = 3.1415926;

const float duration = 1.0;

//这个函数可以计算出，在某个时刻图片的具体位置，通过它可以每经过一段时间，去生成一个新的mask
//转圈产生幻影的单个像素点的颜色值
vec4 getMask(float time, vec2 textureCoords, float padding) {
    //圆心坐标
    vec2 translation = vec2(sin(time * (PI * 2.0 / duration)),cos(time * (PI * 2.0 / duration)));

    //新的纹理坐标 = 原始纹理坐标 + 偏移量 * 圆周坐标（新的图层与图层之间是有间距的，所以需要偏移）
    vec2 translationTextureCoords = textureCoords + padding * translation;

    //根据新的纹理坐标获取新图层的纹素
    vec4 mask = texture2D(inputImageTexture, translationTextureCoords);

    return mask;
}

//这个函数可以计算出，某个时刻创建的层，在当前时刻的透明度
//进度：
float maskAlphaProgress(float currentTime, float hideTime, float startTime) {
    //mod（时长+持续时间 - 开始时间，时长）得到一个周期内的time
    float time = mod(duration + currentTime - startTime, duration);
    //如果小于0.9，返回time，反之，返回0.9
    return min(time, hideTime);
}

void main(){
    //将传入的时间戳转换到一个周期内，time的范围是【0，2】
    //获得时间周期
    float time = mod(progress, duration);
    //放大后的倍数
    float scale = 1.2;
    //偏移量 = 0.083
    float padding = 0.5 * (1.0 - 1.0 / scale);
    //放大后的纹理坐标
    vec2 textureCoords = vec2(0.5, 0.5) + (textureCoordinate - vec2(0.5, 0.5)) / scale;

    //新建层的隐藏时间 即新建层什么时候隐藏
    float hideTime = 0.9;
    //时间间隔：隔0.2s创建一个新层
    float timeGap = 0.2;

    //注意：只保留了红色的透明的通道值，因为幻觉效果残留红色
    //幻影残留数据
    //max RGB alpha
    //新图层的 R透明度
    float maxAlphaR = 0.5;
    //新图层的 G透明度
    float maxAlphaG = 0.05;
    //新图层的 B透明度
    float maxAlphaB = 0.05;

    //获取新的图层的坐标，需要传入时间、纹理坐标、偏移量
    vec4 mask = getMask(time, textureCoords, padding);
    //RGB ：for循环中使用
    float alphaR = 1.0;
    float alphaG = 1.0;
    float alphaB = 1.0;

    //最终图层颜色：初始化
    vec4 resultMask = vec4(0, 0, 0, 0);

    //循环：每一层循环都会得到新的图层的颜色，即幻影颜色
    //一次循环只是计算一个像素点的纹素，需要在真机运行。模拟器会卡，主要是模拟器上是CPU模拟GPU的
    for (float f = 0.0; f < duration; f += timeGap) {
        float tmpTime = f;
        //获取到【0，2】s内所获取的运动后的纹理坐标
        //获得幻影当前时间的颜色值
        vec4 tmpMask = getMask(tmpTime, textureCoords, padding);

        //某个时刻创建的层，在当前时刻的红绿蓝的透明度
        //临时的透明度 = 根据时间推移RGB的透明度发生变化
        //获得临时的红绿蓝透明度
        float tmpAlphaR = maxAlphaR - maxAlphaR * maskAlphaProgress(time, hideTime, tmpTime) / hideTime;
        float tmpAlphaG = maxAlphaG - maxAlphaG * maskAlphaProgress(time, hideTime, tmpTime) / hideTime;
        float tmpAlphaB = maxAlphaB - maxAlphaB * maskAlphaProgress(time, hideTime, tmpTime) / hideTime;

        //累计每一层临时RGB * RGB的临时透明度
        //结果 += 临时颜色 * 透明度，即刚产生的图层的颜色
        resultMask += vec4(tmpMask.r * tmpAlphaR,tmpMask.g * tmpAlphaG,tmpMask.b * tmpAlphaB,1.0);

        //透明度递减
        alphaR -= tmpAlphaR;
        alphaG -= tmpAlphaG;
        alphaB -= tmpAlphaB;
    }

    //最终颜色 += 原始纹理的RGB * 透明度
    resultMask += vec4(mask.r * alphaR, mask.g * alphaG, mask.b * alphaB, 1.0);

    //将最终颜色填充到像素点里
    gl_FragColor = resultMask;
}
