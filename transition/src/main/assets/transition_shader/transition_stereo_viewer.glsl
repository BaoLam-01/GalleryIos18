precision mediump float;

varying mediump vec2 textureCoordinate;
uniform lowp float progress;

uniform sampler2D inputImageTexture;
uniform sampler2D inputImageTexture2;


vec4 getFromColor(vec2 uv){
    return texture2D(inputImageTexture, uv);
}

vec4 getToColor(vec2 uv){
    return texture2D(inputImageTexture2, uv);
}
float corner_radius=0.22;// = 0.22

float zoom=0.88;// = 0.88
float ratio=1.0;

const vec4 black = vec4(0.0, 0.0, 0.0, 1.0);
const vec2 c00 = vec2(0.0, 0.0);// the four corner points
const vec2 c01 = vec2(0.0, 1.0);
const vec2 c11 = vec2(1.0, 1.0);
const vec2 c10 = vec2(1.0, 0.0);

bool in_corner(vec2 p, vec2 corner, vec2 radius) {
    // determine the direction we want to be filled
    vec2 axis = (c11 - corner) - corner;

    // warp the point so we are always testing the bottom left point with the
    // circle centered on the origin
    p = p - (corner + axis * radius);
    p *= axis / radius;
    return (p.x > 0.0 && p.y > -1.0) || (p.y > 0.0 && p.x > -1.0) || dot(p, p) < 1.0;
}

// Check all four corners
// return a float for v2 for anti-aliasing?
bool test_rounded_mask(vec2 p, vec2 corner_size) {
    return
    in_corner(p, c00, corner_size) &&
    in_corner(p, c01, corner_size) &&
    in_corner(p, c10, corner_size) &&
    in_corner(p, c11, corner_size);
}

// Screen blend mode - https://en.wikipedia.org/wiki/Blend_modes
// This more closely approximates what you see than linear blending
vec4 screen(vec4 a, vec4 b) {
    return 1.0 - (1.0 - a) * (1.0 -b);
}

// Given RGBA, find a value that when screened with itself
// will yield the original value.
vec4 unscreen(vec4 c) {
    return 1.0 - sqrt(1.0 - c);
}

// Grab a pixel, only if it isn't masked out by the rounded corners
vec4 sample_with_corners_from(vec2 p, vec2 corner_size) {
    p = (p - 0.5) / zoom + 0.5;
    if (!test_rounded_mask(p, corner_size)) {
        return black;
    }
    return unscreen(getFromColor(p));
}

vec4 sample_with_corners_to(vec2 p, vec2 corner_size) {
    p = (p - 0.5) / zoom + 0.5;
    if (!test_rounded_mask(p, corner_size)) {
        return black;
    }
    return unscreen(getToColor(p));
}

vec4 simple_sample_with_corners_from(vec2 p, vec2 corner_size, float zoom_amt) {
    p = (p - 0.5) / (1.0 - zoom_amt + zoom * zoom_amt) + 0.5;
    if (!test_rounded_mask(p, corner_size)) {
        return black;
    }
    return getFromColor(p);
}

vec4 simple_sample_with_corners_to(vec2 p, vec2 corner_size, float zoom_amt) {
    p = (p - 0.5) / (1.0 - zoom_amt + zoom * zoom_amt) + 0.5;
    if (!test_rounded_mask(p, corner_size)) {
        return black;
    }
    return getToColor(p);
}

mat3 rotate2d(float angle, float ratio) {
    float s = sin(angle);
    float c = cos(angle);
    return mat3(
    c, s, 0.0,
    -s, c, 0.0,
    0.0, 0.0, 1.0);
}

mat3 translate2d(float x, float y) {
    return mat3(
    1.0, 0.0, 0,
    0.0, 1.0, 0,
    -x, -y, 1.0);
}

mat3 scale2d(float x, float y) {
    return mat3(
    x, 0.0, 0,
    0.0, y, 0,
    0, 0, 1.0);
}

// Split an image and rotate one up and one down along off screen pivot points
vec4 get_cross_rotated(vec3 p3, float angle, vec2 corner_size, float ratio) {
    angle = angle * angle;// easing
    angle /= 2.4;// works out to be a good number of radians

    mat3 center_and_scale = translate2d(-0.5, -0.5) * scale2d(1.0, ratio);
    mat3 unscale_and_uncenter = scale2d(1.0, 1.0/ratio) * translate2d(0.5, 0.5);
    mat3 slide_left = translate2d(-2.0, 0.0);
    mat3 slide_right = translate2d(2.0, 0.0);
    mat3 rotate = rotate2d(angle, ratio);

    mat3 op_a = center_and_scale * slide_right * rotate * slide_left * unscale_and_uncenter;
    mat3 op_b = center_and_scale * slide_left * rotate * slide_right * unscale_and_uncenter;

    vec4 a = sample_with_corners_from((op_a * p3).xy, corner_size);
    vec4 b = sample_with_corners_from((op_b * p3).xy, corner_size);

    return screen(a, b);
}

// Image stays put, but this time move two masks
vec4 get_cross_masked(vec3 p3, float angle, vec2 corner_size, float ratio) {
    angle = 1.0 - angle;
    angle = angle * angle;// easing
    angle /= 2.4;

    vec4 img;

    mat3 center_and_scale = translate2d(-0.5, -0.5) * scale2d(1.0, ratio);
    mat3 unscale_and_uncenter = scale2d(1.0 / zoom, 1.0 / (zoom * ratio)) * translate2d(0.5, 0.5);
    mat3 slide_left = translate2d(-2.0, 0.0);
    mat3 slide_right = translate2d(2.0, 0.0);
    mat3 rotate = rotate2d(angle, ratio);

    mat3 op_a = center_and_scale * slide_right * rotate * slide_left * unscale_and_uncenter;
    mat3 op_b = center_and_scale * slide_left * rotate * slide_right * unscale_and_uncenter;

    bool mask_a = test_rounded_mask((op_a * p3).xy, corner_size);
    bool mask_b = test_rounded_mask((op_b * p3).xy, corner_size);

    if (mask_a || mask_b) {
        img = sample_with_corners_to(p3.xy, corner_size);
        return screen(mask_a ? img : black, mask_b ? img : black);
    } else {
        return black;
    }
}

vec4 transition(vec2 uv) {
    float a;
    vec2 p=uv.xy/vec2(1.0).xy;
    vec3 p3 = vec3(p.xy, 1.0);// for 2D matrix transforms

    // corner is warped to represent to size after mapping to 1.0, 1.0
    vec2 corner_size = vec2(corner_radius / ratio, corner_radius);

    if (progress <= 0.0) {
        // 0.0: start with the base frame always
        return getFromColor(p);
    } else if (progress < 0.1) {
        // 0.0-0.1: zoom out and add rounded corners
        a = progress / 0.1;
        return simple_sample_with_corners_from(p, corner_size * a, a);
    } else if (progress < 0.48) {
        // 0.1-0.48: Split original image apart
        a = (progress - 0.1)/0.38;
        return get_cross_rotated(p3, a, corner_size, ratio);
    } else if (progress < 0.9) {
        // 0.48-0.52: black
        // 0.52 - 0.9: unmask new image
        return get_cross_masked(p3, (progress - 0.52)/0.38, corner_size, ratio);
    } else if (progress < 1.0) {
        // zoom out and add rounded corners
        a = (1.0 - progress) / 0.1;
        return simple_sample_with_corners_to(p, corner_size * a, a);
    } else {
        // 1.0 end with base frame
        return getToColor(p);
    }
}

void main() {
    if(progress>0.0){
        gl_FragColor = transition(textureCoordinate);
    }else{
        gl_FragColor = getFromColor(textureCoordinate);
    }
}
