precision mediump float;

varying vec2 textureCoordinate;

uniform sampler2D inputImageTexture, inputImageTexture2;
uniform float progress;
vec2 playerResolution = vec2(1.0);

// Slide Down: translateX = 0, translateY = -1
// Slide Left: translateX = -1, translateY = 0
// Slide Right: translateX = 1, translateY = 0
// Slide Up: translateX = 0, translateY = 1
float translateX = 1.0;
float translateY = 0.0;

void main() {
    vec2 texCoord = textureCoordinate;
    float x = progress * translateX;
    float y = progress * translateY;

    if (x >= 0.0 && y >= 0.0) {
        if (texCoord.x >= x && texCoord.y >= y) {
            gl_FragColor = texture2D(inputImageTexture, texCoord - vec2(x, y));
        }
        else {
            vec2 uv;
            if (x > 0.0)
            uv = vec2(x - 1.0, y);
            else if (y > 0.0)
            uv = vec2(x, y - 1.0);
            gl_FragColor = texture2D(inputImageTexture2, texCoord - uv);
        }
    }
    else if (x <= 0.0 && y <= 0.0) {
        if (texCoord.x <= (1.0 + x) && texCoord.y <= (1.0 + y))
        gl_FragColor = texture2D(inputImageTexture, texCoord - vec2(x, y));
        else {
            vec2 uv;
            if (x < 0.0)
            uv = vec2(x + 1.0, y);
            else if (y < 0.0)
            uv = vec2(x, y + 1.0);
            gl_FragColor = texture2D(inputImageTexture2, texCoord - uv);
        }
    }
    else
    gl_FragColor = vec4(0.0);
}
	