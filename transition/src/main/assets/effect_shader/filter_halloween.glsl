precision mediump float;

varying mediump vec2 textureCoordinate;
uniform sampler2D inputImageTexture;
uniform float progress;
vec3 iResolution = vec3(1.0);

void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
    // Normalized pixel coordinates (from 0 to 1)
    vec2 uv = fragCoord/iResolution.xy;
    vec3 col = vec3(1.+(sin(3.*progress)),1.*sin(3.*progress),1.*sin(3.*progress));

//    if(abs(sin(2.*progress))>0.9){
        fragColor = 0.99*vec4(1.*abs(sin(14.*progress))*col,1.0)+texture2D(inputImageTexture,uv);
//    }else{
//        fragColor = texture2D(inputImageTexture,uv);
//    }
}


void main() {
    mainImage(gl_FragColor, textureCoordinate*iResolution.xy);
}
