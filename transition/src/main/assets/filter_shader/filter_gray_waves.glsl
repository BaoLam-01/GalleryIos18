precision mediump float;

varying mediump vec2 textureCoordinate;

uniform sampler2D inputImageTexture;
uniform lowp float progress;

void main()
{
    float amplitud = 0.03;
    float frecuencia = 10.0;
    float gris = 1.0;
    float divisor = 7.2 / 1280.;
    float grosorInicial = divisor * progress;
    const int kNumPatrones = 6;
    vec3 datosPatron[kNumPatrones];
    datosPatron[0] = vec3(-0.7071, 0.7071, 3.0);
    datosPatron[1] = vec3(0.0, 1.0, 0.6);
    datosPatron[2] = vec3(0.0, 1.0, 0.5);
    datosPatron[3] = vec3(1.0, 0.0, 0.4);
    datosPatron[4] = vec3(1.0, 0.0, 0.3);
    datosPatron[5] = vec3(0.0, 1.0, 0.2);
    vec4 color = texture2D(inputImageTexture, textureCoordinate);
    for(int i = 0; i < kNumPatrones; i++)
    {
        float coseno = datosPatron[i].x;
        float seno = datosPatron[i].y;
        vec2 punto = vec2(
        textureCoordinate.x * coseno - textureCoordinate.y * seno,
        textureCoordinate.x * seno + textureCoordinate.y * coseno
        );
        float grosor = grosorInicial * float(i + 1);
        float dist = mod(punto.y + grosor * 0.5 - sin(punto.x * frecuencia) * amplitud, divisor);
        float brillo = 0.3 * color.r + 0.4 * color.g + 0.3 * color.b;
        if(dist < grosor && brillo < 0.75 - 0.12 * float(i))
        {
            float k = datosPatron[i].z;
            float x = (grosor - dist) / grosor;
            float fx = abs((x - 0.5) / k) - (0.5 - k) / k;
            gris = min(fx, gris);
        }
    }
    gl_FragColor = vec4(gris, gris, gris, 1.0);
}
