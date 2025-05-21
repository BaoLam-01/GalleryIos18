precision mediump float;

varying mediump vec2 textureCoordinate;
uniform sampler2D inputImageTexture;
uniform float progress;
vec3 iResolution = vec3(1.0);

vec2 glitchEfect(vec2 uv, float shift) {
    float glitchBlock = 3.0;
    vec2 uv2 = fract(uv*glitchBlock)-0.5;
    vec2 id = floor(uv2);

    vec2 n2 = fract(sin(id*123.456)*789.125);
    n2+=dot(id.x,id.y*567.89);
    float n = fract(n2.x+n2.y);

    if(mod(progress,1.0)<0.5){
        float glitchDist = 0.01;
        uv.x-=(fract(floor(uv.y+n2.y*glitchBlock)*progress)*glitchDist);
        uv.y-=(fract(floor(uv.x+n2.x*glitchBlock)*progress*4.0)*glitchDist);
        uv.x+=sin(progress*2.0)*shift;
        uv.x+=sin(floor(uv.y*glitchBlock*1.2)*progress*20.)*glitchDist;
        uv.y+=sin(floor(uv.x*glitchBlock*1.2)*progress*21.)*glitchDist;
    }
    return uv;
}

vec2 scaleFromCenter(vec2 coord, float scale) {
    if (scale > 1.0 || scale < 0.0) { return coord; }
    vec2 scaleCenter = vec2(0.5);
    return (coord - scaleCenter) * scale + scaleCenter;
}

void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
    vec2 uv = fragCoord/iResolution.xy;
    vec2 p = (fragCoord-0.5*iResolution.xy)/iResolution.y;

    vec2 r_uv = glitchEfect(uv,0.0);
    vec2 g_uv = glitchEfect(uv,0.015);
    vec2 b_uv = glitchEfect(uv,0.025);


    float r = texture2D(inputImageTexture,r_uv).r;
    float g = texture2D(inputImageTexture,g_uv).g;
    float b = texture2D(inputImageTexture,b_uv).b;

    vec3 col = vec3(r,g,b);
    vec2 size = vec2(1.0,0.03);

    float t = mod(progress,1.0);
    if(t<0.5){
        for(float i = 1.;i<6.0; i+=1.0){
            vec2 pos = p;
            pos.y+=sin(floor(progress*2.1*i))*1.0;
            size.x = 0.5+abs(cos(floor(progress*2.1*i))*1.5);
            size.y = 0.02+sin(floor(progress*2.1*i))*0.03;
            float d = smoothstep(0.,0.001,-max(abs(pos.x)-size.x,abs(pos.y)-size.y));
            col = mix(col,vec3(b,r,g)*1.05,d);
        }
    }


    float scale = 1.0 - mod(progress * 1.3, 0.8) + 0.4;
    if (scale < 0.0) {
        fragColor = texture2D(inputImageTexture, uv);
        return;
    }
    vec2 newCoord = scaleFromCenter(uv, scale);
    float colorScale = scale * 0.5;
    vec4 resultColor = texture2D(inputImageTexture, uv) * (1.0 - colorScale + 0.2);
    vec4 newCoordColor = texture2D(inputImageTexture, newCoord) * (colorScale - 0.2);
    vec4 result = (resultColor + newCoordColor);
    fragColor = (sin(4.*progress)+0.5)*vec4(col,1.0)+(-sin(4.*progress)+0.5)*result;
}

void main() {
    mainImage(gl_FragColor, textureCoordinate*iResolution.xy);
}