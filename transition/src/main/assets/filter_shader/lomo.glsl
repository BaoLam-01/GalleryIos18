precision mediump float;
 
 varying mediump vec2 textureCoordinate;
 
 uniform sampler2D inputImageTexture;
 uniform sampler2D inputImageTexture2;
 uniform sampler2D inputImageTexture3;
 
 uniform float strength;

 void main()
 {
     gl_FragColor = mix(texture2D(inputImageTexture, textureCoordinate),texture2D(inputImageTexture2, textureCoordinate),0.3);
 }