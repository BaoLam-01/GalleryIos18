precision mediump float;

varying mediump vec2 textureCoordinate;
uniform lowp float progress;

uniform sampler2D inputImageTexture;
vec2 uTexel = vec2(0.2);
#define DX 0.8
#define DY 0.5
#define BORDERRADIUS (3)
#define GAMMA       (2.2)
#define PI           (3.14159265359)
#define LUMWEIGHT    (vec3(0.2126,0.7152,0.0722))
#define pow3(x,y)      (pow( max(x,0.) , vec3(y) ))
#define BORDERRADIUSf float(BORDERRADIUS)
#define BORDERRADIUS22f float(BORDERRADIUS*BORDERRADIUS)
vec3 hsv2rgb_smooth( in vec3 c )
{
    vec3 rgb = clamp( abs(mod(c.x*6.0+vec3(0.0,4.0,2.0),6.0)-3.0)-1.0, 0.0, 1.0 );
    return c.z * mix( vec3(1.0), rgb, c.y);
}

vec3 sampleImage(vec2 coord){
    return pow3(texture2D(inputImageTexture,coord).rgb,GAMMA);
}
float kernel(int a,int b){
    return float(a)*exp(-float(a*a + b*b)/BORDERRADIUS22f)/BORDERRADIUSf;
}
void main(){
    vec4 outColor;
    outColor.rgb = sampleImage(textureCoordinate);

    vec3 col;
    vec3 colX = vec3(0.);
    vec3 colY = vec3(0.);
    float coeffX,coeffY;

    for( int i = -BORDERRADIUS ; i <= BORDERRADIUS ; i++ ){
        for( int j = -BORDERRADIUS ; j <= BORDERRADIUS ; j++ ){
            coeffX = kernel(i,j);
            coeffY = kernel(j,i);
            col = sampleImage(textureCoordinate+vec2(i,j)*uTexel);
            colX += coeffX*col;
            colY += coeffY*col;
        }
    }
    vec3 derivative = sqrt( (colX*colX + colY*colY) )/(BORDERRADIUSf*BORDERRADIUSf);
    float angle = atan(dot(colY,LUMWEIGHT),dot(colX,LUMWEIGHT))/(2.*PI) + progress*100.0*(1. - DX)/2.;
    vec3 derivativeWithAngle = hsv2rgb_smooth(vec3(angle,1.,pow(dot(derivative,LUMWEIGHT)*3.,3.)*5.));
    outColor.rgb = mix(derivative,outColor.rgb,DX);
    outColor.rgb = derivativeWithAngle+outColor.rgb;
    gl_FragColor = vec4(pow3(outColor.rgb,1./GAMMA), 1.);
}
