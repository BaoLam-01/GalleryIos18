precision mediump float;

varying mediump vec2 textureCoordinate;
uniform sampler2D inputImageTexture;
uniform float progress;
float brightness=0.2;
float contrast=1.0;


float random2d(vec2 n) {
    return fract(sin(dot(n, vec2(12.9898, 4.1414))) * 45.5453);
}

float SPEED = 1.0;//0 - 1 speed

void main() {
    float time = floor(progress * SPEED * 60.0);
    float rnd = random2d(vec2(time, 954.0));
    if (progress>0.1){
        if (rnd < 0.5){
            vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);
            gl_FragColor = vec4(((textureColor.rgb - vec3(0.5)) * contrast + vec3(0.5)) + vec3(brightness), textureColor.w);
        } else {
            gl_FragColor = texture2D(inputImageTexture, textureCoordinate);
        }
    } else {
        gl_FragColor = texture2D(inputImageTexture, textureCoordinate);
    }
}