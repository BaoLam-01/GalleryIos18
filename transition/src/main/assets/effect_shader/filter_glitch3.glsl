precision mediump float;

varying mediump vec2 textureCoordinate;
uniform sampler2D inputImageTexture;
uniform float progress;

vec3 iResolution = vec3(1.0);


float rand () {
    return fract(sin(progress)*1e4);
}
void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
    //    vec2 uv = fragCoord.xy / iResolution.xy;
    vec2 uv = fragCoord.xy;

    vec2 uvR = uv;
    vec2 uvB = uv;

    uvR.x = uv.x * 1.0 - rand() * 0.02 * 0.8;
    uvB.y = uv.y * 1.0 + rand() * 0.02 * 0.8;

    //
    if(uv.y < rand() && uv.y > rand() -0.1 && sin(progress) < 0.0)
    {
        uv.x = (uv + 0.02 * rand()).x;
    }

    //
    vec4 c = vec4(0.0, 0.0, 0.0, 1.0);
    c.r = texture2D(inputImageTexture, uvR).r;
    c.g = texture2D(inputImageTexture, uv).g;
    c.b = texture2D(inputImageTexture, uvB).b;

    //
    float scanline = sin( uv.y * 800.0 * rand())/30.0;
    c *= (1.0 - scanline);

    //vignette
    float vegDist = length(( 0.5 , 0.5 ) - uv);
    c *= 1.0 - vegDist * 0.6;

    fragColor = c;
}

void main() {
    mainImage(gl_FragColor, textureCoordinate);
}