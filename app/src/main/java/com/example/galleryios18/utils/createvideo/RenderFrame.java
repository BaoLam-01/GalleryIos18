package com.example.galleryios18.utils.createvideo;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLES32;
import android.opengl.GLUtils;
import android.os.Build;

import java.nio.FloatBuffer;

public class RenderFrame {

    private static final String VERTEX_SHADER =
            "attribute vec4 a_position;\n" +
                    "attribute vec2 a_texcoord;\n" +
                    "varying vec2 v_texcoord;\n" +
                    "void main() {\n" +
                    "  gl_Position = a_position;\n" +
                    "  v_texcoord = a_texcoord;\n" +
                    "}\n";
    private static final String FRAGMENT_SHADER =
            "precision mediump float;\n" +
                    "uniform sampler2D tex_sampler;\n" +
                    "varying vec2 v_texcoord;\n" +
                    "void main() {\n" +
                    "  gl_FragColor = texture2D(tex_sampler, v_texcoord);\n" +
                    "}\n";


    public RenderFrame() {

    }

    public void renderTexture(int texture, int viewWidth, int viewHeight, FloatBuffer cubeBuffer, FloatBuffer textureBuffer) {
        // Use our shader program
        if (shaderProgram == 0) {
            createProgram();
        }
        GLES20.glUseProgram(shaderProgram);

        // Set viewport
        GLES20.glViewport(0, 0, viewWidth, viewHeight);

        // Set the vertex attributes
        textureBuffer.position(0);
        GLES20.glVertexAttribPointer(texCoordinateHandle, 2, GLES20.GL_FLOAT, false, 0, textureBuffer);
        GLES20.glEnableVertexAttribArray(texCoordinateHandle);

        cubeBuffer.position(0);
        GLES20.glVertexAttribPointer(posCoordinateHandle, 2, GLES20.GL_FLOAT, false, 0, cubeBuffer);
        GLES20.glEnableVertexAttribArray(posCoordinateHandle);

        // Set the input texture
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);
        GLES20.glUniform1i(texSamplerHandle, 0);

        // Draw!
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        int[] x = new int[]{texture};
        GLES20.glDeleteTextures(1, x, 0);
    }

    public int loadTexture(Bitmap img) {
        int[] textures = new int[1];
        if (img != null) {
            GLES20.glGenTextures(1, textures, 0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                        GLES20.GL_TEXTURE_WRAP_S, GLES32.GL_CLAMP_TO_BORDER);
            } else {
                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                        GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_MIRRORED_REPEAT);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                        GLES20.GL_TEXTURE_WRAP_T, GLES32.GL_CLAMP_TO_BORDER);
            } else {
                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                        GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_MIRRORED_REPEAT);
            }
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, img, 0);
        }
        return textures[0];
    }

    private void createProgram() {
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER);
        if (vertexShader == 0) {
            throw new RuntimeException("Could not load vertex shader");
        }
        int pixelShader = loadShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER);
        if (pixelShader == 0) {
            throw new RuntimeException("Could not load fragment shader");
        }
        int program = GLES20.glCreateProgram();
        if (program != 0) {
            GLES20.glAttachShader(program, vertexShader);
            GLES20.glAttachShader(program, pixelShader);
            GLES20.glLinkProgram(program);
            int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
            if (linkStatus[0] != GLES20.GL_TRUE) {
                String info = GLES20.glGetProgramInfoLog(program);
                GLES20.glDeleteProgram(program);
                throw new RuntimeException("Could not link program: " + info);
            }
        }
        // Bind attributes and uniforms
        shaderProgram = program;
        texSamplerHandle = GLES20.glGetUniformLocation(program, "tex_sampler");
        texCoordinateHandle = GLES20.glGetAttribLocation(program, "a_texcoord");
        posCoordinateHandle = GLES20.glGetAttribLocation(program, "a_position");
    }

    private int texSamplerHandle;
    private int texCoordinateHandle;
    private int posCoordinateHandle;
    private int shaderProgram;

    private int loadShader(int shaderType, String source) {
        int shader = GLES20.glCreateShader(shaderType);
        if (shader != 0) {
            GLES20.glShaderSource(shader, source);
            GLES20.glCompileShader(shader);
            int[] compiled = new int[1];
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
            if (compiled[0] == 0) {
                String info = GLES20.glGetShaderInfoLog(shader);
                GLES20.glDeleteShader(shader);
                throw new RuntimeException("Could not compile shader " + shaderType + ":" + info);
            }
        }
        return shader;
    }
}