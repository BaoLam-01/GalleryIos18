precision mediump float;

varying vec2 textureCoordinate;
uniform sampler2D inputImageTexture;
uniform sampler2D inputImageTexture2;
uniform float progress;
vec2 playerResolution = vec2(1.0);
uniform int isHorizontal;
uniform int hasTiles;

vec2 xskew (vec2 p, float persp, float center) {
    float x = mix(p.x, 1.0-p.x, center);
    return (
    (vec2( x, (p.y - 0.5*(1.0-persp) * x) / (1.0+(persp-1.0)*x) )- vec2(0.5-distance(center, 0.5), 0.0))
    * vec2(0.5 / distance(center, 0.5) * (center<0.5 ? 1.0 : -1.0), 1.0)
    + vec2(center<0.5 ? 0.0 : 1.0, 0.0)
    );
}

vec2 yskew (vec2 p, float persp, float center) {
    float y = mix(p.y, 1.0-p.y, center);
    return (
    (vec2( (p.x - 0.5*(1.0-persp) * y) / (1.0+(persp-1.0)*y) , y)
    - vec2( 0.0, 0.5-distance(center, 0.5))
    )
    * vec2(1.0, 0.5 / distance(center, 0.5) * (center<0.5 ? 1.0 : -1.0))
    + vec2(0.0, center<0.5 ? 0.0 : 1.0)
    );
}

bool inBounds (vec2 p) {
    return all(lessThan(vec2(0.0), p)) && all(lessThan(p, vec2(1.0)));
}

void main(void) {
    float persp = 0.5;
    float unzoom = 0.0;
    vec2 op = textureCoordinate;

    float inv = 1.0 - progress;

    /* Go at twice the speed, but with an initial delay */
    float done = 0.0;
    if(hasTiles > 0){
        float val = op.y;

        if(isHorizontal == 0){
            val = op.x;
        }

        float limit = 0.0;
        if(val > 0.90){
            limit = 0.0;
        }else if(val > 0.8){
            limit = 0.05;
        }else if(val > 0.7){
            limit = 0.1;
        }else if(val > 0.6){
            limit = 0.15;
        }else if(val > 0.5){
            limit = 0.2;
        }else if(val > 0.4){
            limit = 0.25;
        }else if(val > 0.3){
            limit = 0.3;
        }else if(val > 0.2){
            limit = 0.35;
        }else if(val > 0.1){
            limit = 0.4;
        }else if(val > 0.0){
            limit = 0.45;
        }

        if(progress < limit){
            gl_FragColor = texture2D(inputImageTexture, op);
            done = 1.0;
        }else {
            inv = (1.0 - (progress - limit) * 2.0);
        }

        if(inv < 0.0){
            gl_FragColor = texture2D(inputImageTexture2, op);
            done = 1.0;
        }
    }

    if(done == 0.0){
        float uz = unzoom * 2.0*(0.5-distance(0.5, inv));
        vec2 p = -uz * 0.5 + (1.0 + uz) * op;

        vec2 toP = vec2(0.0);
        vec2 fromP = vec2(0.0);


        if(isHorizontal == 1){
            toP = xskew((p - vec2(inv, 0.0)) / vec2(1.0-inv, 1.0), 1.0-mix(inv, 0.0, persp), 0.0);
            fromP = xskew(p / vec2(inv, 1.0), mix(pow(inv, 2.0), 1.0, persp), 1.0);
        }else{
            toP = yskew((p - vec2(0.0, inv)) / vec2(1.0, 1.0-inv), 1.0-mix(inv, 0.0, persp), 0.0);
            fromP = yskew(p / vec2(1.0, inv), mix(pow(inv, 2.0), 1.0, persp), 1.0);
        }

        if (inBounds (fromP)) {
            gl_FragColor = texture2D(inputImageTexture, fromP);
        } else if (inBounds (toP)) {
            gl_FragColor = texture2D(inputImageTexture2, toP);
        } else {
            gl_FragColor = vec4(0.0, 0.0, 0.0, 0.0);
        }
    }
}
