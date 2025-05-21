precision mediump float;

varying mediump vec2 textureCoordinate;
uniform sampler2D inputImageTexture;
uniform float progress;
vec3 iResolution = vec3(1.0);

void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
    // Normalized pixel coordinates (from 0 to 1)
    vec2 uv = fragCoord/iResolution.xy;

    // Time varying pixel color
    vec3 col = 0.5 + 0.5*cos(progress+uv.xyx+vec3(0,2,4));

    float stheta = sin(0.5 * progress);
    float ctheta = cos(0.4 * progress);
    vec2 rot_uv = mat2(ctheta, stheta, -stheta, ctheta) * uv;

    vec2 warped_uv = uv + vec2(0.25 * sin(3.0 * rot_uv.x), 0.05 * cos(4.5 * rot_uv.y));

    float wiggle = sin(600.0 * dot(vec2(0.5, 0.7), warped_uv));

    float intense = dot(vec3(0.48), texture2D(inputImageTexture, vec2(uv.x, uv.y), 2.0).rgb);

    // Output to screen
    fragColor = vec4(col * clamp(wiggle - 1.0 + 2.0 * intense, 0.0, 1.0),1.0);
}


void main() {
    mainImage(gl_FragColor, textureCoordinate*iResolution.xy);
}

