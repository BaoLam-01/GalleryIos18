precision mediump float;

varying mediump vec2 textureCoordinate;
uniform sampler2D inputImageTexture;

vec3 iResolution = vec3(1.0);

#define T texture2D(inputImageTexture,.5+(p.xy*=.992)).rgb

//#define radialLength 0.80     //0.5 - 1.0
#define imageBrightness 10.0   //0 - 10
#define flareBrightness 8.0   // 0 - 10
uniform lowp float progress; //0.5 - 1.0

void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
    vec3 p = vec3(fragCoord / iResolution.xy, max(0.0, (imageBrightness/10.0)-0.5)) - 0.5;
    vec3 o = T;
    float base = pow(max(0.0, 0.5-length(T)), 10.0/flareBrightness);
    float radial = 1.0 - progress;

    if (base > 0.0) {
        for (float i=0.0; i<100.0; i++)
        {
            p.z += base * exp(-i * radial);
        }
    }

    vec3 flare = p.z * vec3(59.0/255.0, 113.0/255.0, 255.0/255.0); //tint

    fragColor = vec4(o*o+flare, 1.0);
}


void main() {
    mainImage(gl_FragColor, textureCoordinate*iResolution.xy);
}

