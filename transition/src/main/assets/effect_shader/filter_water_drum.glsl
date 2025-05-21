precision mediump float;

varying mediump vec2 textureCoordinate;
uniform sampler2D inputImageTexture;
uniform float progress;

vec3 iResolution = vec3(1.0);

void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
    // Normalized pixel coordinates (from 0 to 1)
    vec2 uv = fragCoord/iResolution.xy;
    vec2 radio = vec2(iResolution.y/iResolution.x, 1.0);
    vec2 center = vec2(0.5-0.5*abs(sin(1.2*progress)), 0.5);
    vec2 dir = uv - center;
    float d = length(dir);
    float effect_d = mod(3.5*progress, 2.0);
    float effect_range = 0.25;

    float effect_x = smoothstep(effect_d-effect_range, effect_d+effect_range, d);
    vec2 delta = normalize(dir) * sin(effect_x*3.1415926) * (effect_x-0.5)*0.1;

    // Output to screen
    fragColor = texture2D(inputImageTexture, uv+delta);
}

void main() {
    mainImage(gl_FragColor, textureCoordinate*iResolution.xy);
}
