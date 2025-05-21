precision mediump float;
varying mediump vec2 textureCoordinate;

uniform sampler2D inputImageTexture;

const int lookupSize = 64;
const float errorCarry = 0.3;
vec2 sketchSize = vec2(1.0);
vec2 texOffset =vec2(1.0);
float kernelSize = 2.0;
int horizontalPass=0; // 0 or 1 to indicate vertical or horizontal pass
float strength = 3.0;     // The strength value for the gaussian function: higher value means more blur
// A good value for 9x9 is around 3 to 5
// A good value for 7x7 is around 2.5 to 4
// A good value for 5x5 is around 2 to 3.5
// ... play around with this based on what you need <span class="Emoticon Emoticon1"><span>:)</span></span>

const float pi = 3.14159265;

void main() {
        float numBlurPixelsPerSide = kernelSize / 2.0;

        vec2 blurMultiplyVec = 0 < horizontalPass ? vec2(1.0, 0.0) : vec2(0.0, 1.0);

        // Incremental Gaussian Coefficent Calculation (See GPU Gems 3 pp. 877 - 889)
        vec3 incrementalGaussian;
        incrementalGaussian.x = 1.0 / (sqrt(2.0 * pi) * strength);
        incrementalGaussian.y = exp(-0.5 / (strength * strength));
        incrementalGaussian.z = incrementalGaussian.y * incrementalGaussian.y;

        vec4 avgValue = vec4(0.0, 0.0, 0.0, 0.0);
        float coefficientSum = 0.0;

        // Take the central sample first...
        avgValue += texture2D(inputImageTexture, textureCoordinate.st) * incrementalGaussian.x;
        coefficientSum += incrementalGaussian.x;
        incrementalGaussian.xy *= incrementalGaussian.yz;

        // Go through the remaining 8 vertical samples (4 on each side of the center)
        for (float i = 1.0; i <= numBlurPixelsPerSide; i++) {
                avgValue += texture2D(inputImageTexture, textureCoordinate.st - i * texOffset *
                blurMultiplyVec) * incrementalGaussian.x;
                avgValue += texture2D(inputImageTexture, textureCoordinate.st + i * texOffset *
                blurMultiplyVec) * incrementalGaussian.x;
                coefficientSum += 2.0 * incrementalGaussian.x;
                incrementalGaussian.xy *= incrementalGaussian.yz;
        }

        gl_FragColor = avgValue / coefficientSum;
}