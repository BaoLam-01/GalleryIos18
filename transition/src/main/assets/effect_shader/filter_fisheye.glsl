precision mediump float;

varying mediump vec2 textureCoordinate;

uniform sampler2D inputImageTexture;
uniform lowp float progress;
#define BLUR_ON true
#define POSTPROCESS true

const float range = 0.0150;
const float offsetIntensity = 0.0075;

//scratch section taked from shader by Vladimir Storm
//https://twitter.com/vladstorm_

#define t progress

//random hash
vec4 hash42(vec2 p){

    vec4 p4 = fract(vec4(p.xyxy) * vec4(443.8975,397.2973, 491.1871, 470.7827));
    p4 += dot(p4.wzxy, p4+19.19);
    return fract(vec4(p4.x * p4.y, p4.x*p4.z, p4.y*p4.w, p4.x*p4.w));
}


float hash( float n ){
    return fract(sin(n)*43758.5453123);
}

// 3d noise function (iq's)
float n( in vec3 x ){
    vec3 p = floor(x);
    vec3 f = fract(x);
    f = f*f*(3.0-2.0*f);
    float n = p.x + p.y*57.0 + 113.0*p.z;
    float res = mix(mix(mix( hash(n+  0.0), hash(n+  1.0),f.x),
    mix( hash(n+ 57.0), hash(n+ 58.0),f.x),f.y),
    mix(mix( hash(n+113.0), hash(n+114.0),f.x),
    mix( hash(n+170.0), hash(n+171.0),f.x),f.y),f.z);
    return res;
}

//tape noise
float nn(vec2 p){


    float y = p.y;
    float s = t*2.;

    float v = (n( vec3(y*.1 +s,             2., 2.) ) + .0)
    //*(n( vec3(y*.011+1000.0+s,   2., 2.) ) + .0)
    *(n( vec3(y*.51+421.0+s,       2., 2.) ) + .0)
    ;
    //v*= n( vec3( (fragCoord.xy + vec2(s,0.))*100.,1.0) );
    v*= hash42(   vec2(p.x +t*0.01, p.y) ).x +.3 ;


    v = pow(v+.3, 1.);
    if(v<.7) v = 0.;  //threshold
    return v;
}

float rand(vec2 co){
    return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453);
}

float brightness (vec3 color)
{
    return ( 0.2126 * color.r + 0.7152 * color.g + 0.0722 * color.b );
}

float verticalBar(float pos, float uvY, float offset) {
    float edge0 = (pos - range);
    float edge1 = (pos + range);

    float x = smoothstep(edge0, pos, uvY) * offset;
    x -= smoothstep(pos, edge1, uvY) * offset;
    return x;
}

float normpdf(in float x, in float sigma)
{
    return 0.39894*exp(-0.5*x*x/(sigma*sigma))/sigma;
}

vec3 scanline(vec2 coord, vec3 screen){
    const float scale = .0036;
    const float amt = 0.02;// intensity of effect
    const float spd = 1.0;//speed of scrolling rows transposed per second

    screen.rgb += sin((coord.y / scale - (progress * spd * 6.28))) * amt;
    return screen;
}

vec2 fisheye(vec2 uv, float str )
{
    vec2 neg1to1 = uv;
    neg1to1 = (neg1to1 - 0.5) * 2.0;

    vec2 offset;
    offset.x = ( pow(neg1to1.y,2.0)) * str * (neg1to1.x);
    offset.y = ( pow(neg1to1.x,2.0)) * str * (neg1to1.y);

    return uv + offset;
}

void main()
{
    vec2 p = textureCoordinate;

    if ( !POSTPROCESS ) {
        gl_FragColor = texture2D( inputImageTexture, p );
        return;
    }
    vec2 uv = p - progress / 100.0;
    p = fisheye(p, 0.03);
    float barOffset;
    for (float i = 0.0; i < 0.71; i += 0.1313) {
        float d = mod(progress * i, 1.7);
        float o = sin(1.0 - tan(progress * 0.24 * i));
        o *= offsetIntensity;
        p.x += verticalBar(d, p.y, o);
    }

    float scale = rand( p + progress );
    vec2 rOffset = vec2( 0.0, -0.005 ) * scale;
    vec2 gOffset = vec2( 0.0, 0.005 ) * scale;
    vec2 bOffset = vec2( 0.005, 0. ) * scale;
    vec4 rValue = texture2D( inputImageTexture, p - rOffset );
    vec4 gValue = texture2D( inputImageTexture, p - gOffset );
    vec4 bValue = texture2D( inputImageTexture, p - bOffset );
    vec3 abber = ( vec3( rValue.r, gValue.g, bValue.b ) + texture2D( inputImageTexture, p ).xyz ) / 2.;


    vec3 col;
    float rim = .020;
    if( p.x < rim )
    col = vec3( mix( 0., abber.r, min(1., 3. * p.x / rim - 1.) ),
    mix( 0., abber.g, min(1., 3. * p.x / rim ) ),
    mix( 0., abber.b, min(1., 3. * p.x / rim - 2. ) ) );
    else if ( p.x > 1. - rim )
    col = vec3( mix( 0., abber.r, min(1., 3. * ( 1. - p.x ) / rim - 1.) ),
    mix( 0., abber.g, min(1., 3. * ( 1. - p.x ) / rim - 2.) ),
    mix( 0., abber.b, min(1., 3. * ( 1. - p.x ) / rim ) ) );
    else
    col = abber;

    if(BLUR_ON)
    {
        //declare stuff
        const int mSize = 9;
        const int kSize = (mSize-1)/2;
        float kernel[mSize];
        mat3 sobelX = mat3(-1.,0.,1.,
        -2.,0.,2.,
        -1.,0.,1);
        mat3 sobelY = mat3(-1.,-2.,-1.,
        0.,0.,0.,
        1.,2.,1);
        float gx = 0.;
        float gy = 0.;

        vec3 final_colour = vec3(0.0);

        //create the 1-D kernel
        float sigma = 2.0;
        float Z = 0.0;
        for (int j = 0; j <= kSize; ++j)
        {
            kernel[kSize+j] = kernel[kSize-j] = normpdf(float(j), sigma);
        }

        //get the normalization factor (as the gaussian has been clamped)
        for (int j = 0; j < mSize; ++j)
        {
            Z += kernel[j];
        }

        //read out the texels
        for (int i=-kSize; i <= kSize; ++i)
        {
            for (int j=-kSize; j <= kSize; ++j)
            {
                vec3 texcol = texture2D(inputImageTexture, p + vec2(float(i),float(j)) / textureCoordinate.xy).rgb;
                if(i<=1 && i>=-1 && j>=-1 && j<=1)
                {
                    gx += sobelX[i+1][j+1] * brightness(texcol);
                    gy += sobelY[i+1][j+1] * brightness(texcol);
                }
                final_colour += kernel[kSize+j]*kernel[kSize+i]*texcol;
            }
        }

        col = 0.5 * col + 0.5 * final_colour / ( Z * Z ) + 0.025 * ( gx*gx + gy*gy );
    }

    float scratchWidth = .1;
    if ( uv.y < scratchWidth && uv.y > -scratchWidth)
    {
        float linesN = 240.; //fields per seconds
        float one_y = textureCoordinate.y / linesN; //field line
        uv = floor(uv*textureCoordinate.xy/one_y)*one_y;
        col += vec3( nn(uv) );
    }

    gl_FragColor = vec4( scanline( p, col ), 1.0 );
}
