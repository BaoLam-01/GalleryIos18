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


//layout(location = 0) out vec4 outColor;
vec2 u_TouchXY= vec2(0.5, 0.5);//点击的位置（归一化）
//float u_Time=0.5;//归一化的时间
float u_Boundary=0.1;//边界 0.1
float ratio=1.0;
float duration = 1.0;

vec4 transition(vec2 uv) {
    return mix(
    getFromColor(uv),
    getToColor(uv),
    progress
    );
}


void main() {
    vec2 texCoord = textureCoordinate * vec2(1.0, ratio);//根据纹理尺寸，对采用坐标进行转换
    vec2 touchXY = u_TouchXY * vec2(1.0, ratio);//根据纹理尺寸，对中心点坐标进行转换
    float distance = distance(texCoord, touchXY);//采样点坐标与中心点的距离
    float progressed = mod(progress, duration) / duration;
    if ((progressed - u_Boundary) > 0.0
    && (distance <= (progressed + u_Boundary))
    && (distance >= (progressed - u_Boundary))) {

        float x = (distance - progressed);//输入 diff
//        float moveDis =  - pow(8 * x, 3.0);//平滑函数 -(8x)^3 采样坐标移动距离
        float moveDis =  20.0 * x * (x - 0.1)*(x + 0.1);
        vec2 unitDirectionVec = normalize(texCoord - touchXY);//单位方向向量
        texCoord = texCoord + (unitDirectionVec * moveDis);//采样坐标偏移（实现放大和缩小效果）
        texCoord = texCoord / vec2(1.0, ratio);
        gl_FragColor = texture2D(inputImageTexture, texCoord);
    } else {
        gl_FragColor = transition(textureCoordinate);
    }
}
