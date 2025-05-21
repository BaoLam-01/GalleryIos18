precision mediump float;

varying mediump vec2 textureCoordinate;
uniform sampler2D inputImageTexture;
uniform float progress;

void main()
{
    /*
    lowp vec3 rgb = texture2D(inputImageTexture, textureCoordinate).rgb;
    lowp float d = distance(textureCoordinate, vec2(0.5,0.5));
    rgb *= (1.0 - smoothstep(vignetteStart, vignetteEnd, d));
    gl_FragColor = vec4(vec3(rgb),1.0);
     */
    vec3 vignetteColor = vec3(0.0);
    float vignetteStart = progress-0.05;
    float vignetteEnd = progress+0.05;
    vec2 vignetteCenter = vec2(0.5);

    lowp vec3 rgb = texture2D(inputImageTexture, textureCoordinate).rgb;
    lowp float d = distance(textureCoordinate, vec2(vignetteCenter.x, vignetteCenter.y));
    lowp float percent = smoothstep(vignetteStart, vignetteEnd, d);
    gl_FragColor = vec4(mix(rgb.x, vignetteColor.x, percent), mix(rgb.y, vignetteColor.y, percent), mix(rgb.z, vignetteColor.z, percent), 1.0);
}
