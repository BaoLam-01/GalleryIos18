precision mediump float;

varying mediump vec2 textureCoordinate;

uniform sampler2D inputImageTexture;

vec4 getFromColor(vec2 uv){
    return texture2D(inputImageTexture, uv);
}

void main() {
    vec2 pos = textureCoordinate.xy;
    vec4 result;

    if (pos.x <= 0.5 && pos.y<= 0.5){ //左上
        pos.x = pos.x * 2.0;
        pos.y = pos.y * 2.0;
        vec4 color = texture2D(inputImageTexture, pos);
        result = vec4(color.g, color.g, color.g, 1.0);
    } else if (pos.x > 0.5 && pos.y< 0.5){ //右上
        pos.x = (pos.x - 0.5) * 2.0;
        pos.y = (pos.y) * 2.0;
        vec4 color= texture2D(inputImageTexture, pos);
        float arg = 1.5;
        float r = color.r;
        float g = color.g;
        float b = color.b;
        b = sqrt(b)*arg;
        if (b>1.0) b = 1.0;
        result = vec4(r, g, b, 1.0);
    } else if (pos.y> 0.5 && pos.x < 0.5) { //左下
        pos.y = (pos.y - 0.5) * 2.0;
        pos.x = pos.x * 2.0;
        vec4 color= texture2D(inputImageTexture, pos);
        float r = color.r;
        float g = color.g;
        float b = color.b;
        r = 0.393* r + 0.769 * g + 0.189* b;
        g = 0.349 * r + 0.686 * g + 0.168 * b;
        b = 0.272 * r + 0.534 * g + 0.131 * b;
        result = vec4(r, g, b, 1.0);
    } else if (pos.y> 0.5 && pos.x > 0.5){ //右下
        pos.y = (pos.y - 0.5) * 2.0;
        pos.x = (pos.x - 0.5) * 2.0;
        vec4 color= texture2D(inputImageTexture, pos);
        float r = color.r;
        float g = color.g;
        float b = color.b;
        b = 0.393* r + 0.769 * g + 0.189* b;
        g = 0.349 * r + 0.686 * g + 0.168 * b;
        r = 0.272 * r + 0.534 * g + 0.131 * b;
        result = vec4(r, g, b, 1.0);
    }
    gl_FragColor = result;
}
