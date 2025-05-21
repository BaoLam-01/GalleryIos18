precision mediump float;

varying vec2 textureCoordinate;
uniform sampler2D inputImageTexture;
uniform sampler2D inputImageTexture2;
uniform float progress;
vec2 playerResolution = vec2(1.0);

uniform int isHorizontal;

bool inBounds (vec2 p) {
    return all(lessThan(vec2(0.0), p)) && all(lessThan(p, vec2(1.0)));
}

void main(void) {
    vec2 p = textureCoordinate;

    vec2 q = p;
    float a1 = p.x;
    float b1 = p.y;

    float a2 = q.x;
    float b2 = q.y;

    if(isHorizontal == 0){
        a1 = p.y;
        b1 = p.x;

        a2 = q.y;
        b2 = q.x;
    }

    a1 = (a1 - 0.5) / abs(progress - 0.5) * 0.5 + 0.5;

    float center = 0.5;
    float delta = b1 - center;

    if(progress < 0.5){
        float res = b1 + (10.0 * delta * (0.5 - a2) * progress) / (1.0 + a1);
        b1 = res;
    }else{
        float res = b1 + (10.0 * delta * (0.5 - a2) * (1.0-progress) * -1.0) / (1.0 + a1);
        b1 = res;
    }

    if(isHorizontal == 0){
        p.x = b1;
        p.y = a1;
    }else{
        p.x = a1;
        p.y = b1;
    }

    if (inBounds(p)) {
        vec4 a = texture2D(inputImageTexture, p);
        vec4 b = texture2D(inputImageTexture2, p);
        gl_FragColor = mix(a, b, step(0.5, progress)).rgba * step(abs(a2 - 0.5), abs(progress - 0.5));
    }else {
        gl_FragColor = vec4(0.0, 0.0, 0.0, 0.0);
    }
}
	