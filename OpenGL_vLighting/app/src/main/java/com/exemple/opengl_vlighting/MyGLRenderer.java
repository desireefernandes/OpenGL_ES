package com.exemple.opengl_vlighting;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyGLRenderer implements GLSurfaceView.Renderer {
    private static final String TAG = "Shape";
    private Shape mShape;

    private float[] modelMatrix = new float[16];

    private float[] viewMatrix = new float[16];
    private float[] projectionMatrix = new float[16];
    private float[] mVPMatrix = new float[16];

    private float[] lightModelMatrix = new float[16];

    private final float[] lightPosInModelSpace = new float[]{0.0f, 0.0f, 0.0f, 1.0f};
    private final float[] lightPosInWorldSpace = new float[4];
    private final float[] lightPosInEyeSpace = new float[4];

    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        mShape = new Shape(
                new float[]{ //Coordenadas
                        // Front face
                       -1.0f,  1.0f, 1.0f,
                       -1.0f, -1.0f, 1.0f,
                        1.0f,  1.0f, 1.0f,
                       -1.0f, -1.0f, 1.0f,
                        1.0f, -1.0f, 1.0f,
                        1.0f,  1.0f, 1.0f,

                        // Right face
                        1.0f,  1.0f,  1.0f,
                        1.0f, -1.0f,  1.0f,
                        1.0f,  1.0f, -1.0f,
                        1.0f, -1.0f,  1.0f,
                        1.0f, -1.0f, -1.0f,
                        1.0f,  1.0f, -1.0f,

                        // Back face
                        1.0f,  1.0f, -1.0f,
                        1.0f, -1.0f, -1.0f,
                       -1.0f,  1.0f, -1.0f,
                        1.0f, -1.0f, -1.0f,
                       -1.0f, -1.0f, -1.0f,
                       -1.0f,  1.0f, -1.0f,

                        // Left face
                       -1.0f,  1.0f, -1.0f,
                       -1.0f, -1.0f, -1.0f,
                       -1.0f,  1.0f,  1.0f,
                       -1.0f, -1.0f, -1.0f,
                       -1.0f, -1.0f,  1.0f,
                       -1.0f,  1.0f,  1.0f,

                        // Top face
                       -1.0f,  1.0f, -1.0f,
                       -1.0f,  1.0f,  1.0f,
                        1.0f,  1.0f, -1.0f,
                       -1.0f,  1.0f,  1.0f,
                        1.0f,  1.0f,  1.0f,
                        1.0f,  1.0f, -1.0f,

                        // Bottom face
                        1.0f, -1.0f, -1.0f,
                        1.0f, -1.0f,  1.0f,
                       -1.0f, -1.0f, -1.0f,
                        1.0f, -1.0f,  1.0f,
                       -1.0f, -1.0f,  1.0f,
                       -1.0f, -1.0f, -1.0f},

                new float[]{ //Colors
                        // Front face
                        0.0f, 0.0f, 1.0f, 1.0f,
                        1.0f, 0.0f, 1.0f, 1.0f,
                        0.0f, 1.0f, 1.0f, 1.0f,
                        1.0f, 0.0f, 1.0f, 1.0f,
                        1.0f, 0.0f, 0.0f, 1.0f,
                        0.0f, 1.0f, 1.0f, 1.0f,

                        // Right face
                        0.0f, 1.0f, 1.0f, 1.0f,
                        1.0f, 0.0f, 0.0f, 1.0f,
                        1.0f, 1.0f, 0.0f, 1.0f,
                        1.0f, 0.0f, 0.0f, 1.0f,
                        1.0f, 0.0f, 0.0f, 1.0f,
                        1.0f, 1.0f, 0.0f, 1.0f,

                        // Back face
                        1.0f, 1.0f, 0.0f, 1.0f,
                        1.0f, 0.0f, 0.0f, 1.0f,
                        0.0f, 1.0f, 0.0f, 1.0f,
                        1.0f, 0.0f, 0.0f, 1.0f,
                        1.0f, 0.0f, 1.0f, 1.0f,
                        0.0f, 1.0f, 0.0f, 1.0f,

                        // Left face
                        0.0f, 1.0f, 0.0f, 1.0f,
                        1.0f, 0.0f, 1.0f, 1.0f,
                        0.0f, 0.0f, 1.0f, 1.0f,
                        1.0f, 0.0f, 1.0f, 1.0f,
                        1.0f, 0.0f, 1.0f, 1.0f,
                        0.0f, 0.0f, 1.0f, 1.0f,

                        // Top face
                        0.0f, 1.0f, 0.0f, 1.0f,
                        0.0f, 0.0f, 1.0f, 1.0f,
                        1.0f, 1.0f, 0.0f, 1.0f,
                        0.0f, 0.0f, 1.0f, 1.0f,
                        0.0f, 1.0f, 1.0f, 1.0f,
                        1.0f, 1.0f, 0.0f, 1.0f,

                        // Bottom face
                        1.0f, 0.0f, 0.0f, 1.0f,
                        1.0f, 0.0f, 0.0f, 1.0f,
                        1.0f, 0.0f, 1.0f, 1.0f,
                        1.0f, 0.0f, 0.0f, 1.0f,
                        1.0f, 0.0f, 1.0f, 1.0f,
                        1.0f, 0.0f, 1.0f, 1.0f},

                new float[]{ //Normal
                        // Front face
                        0.0f, 0.0f, 1.0f,
                        0.0f, 0.0f, 1.0f,
                        0.0f, 0.0f, 1.0f,
                        0.0f, 0.0f, 1.0f,
                        0.0f, 0.0f, 1.0f,
                        0.0f, 0.0f, 1.0f,

                        // Right face
                        1.0f, 0.0f, 0.0f,
                        1.0f, 0.0f, 0.0f,
                        1.0f, 0.0f, 0.0f,
                        1.0f, 0.0f, 0.0f,
                        1.0f, 0.0f, 0.0f,
                        1.0f, 0.0f, 0.0f,

                        // Back face
                        0.0f, 0.0f, -1.0f,
                        0.0f, 0.0f, -1.0f,
                        0.0f, 0.0f, -1.0f,
                        0.0f, 0.0f, -1.0f,
                        0.0f, 0.0f, -1.0f,
                        0.0f, 0.0f, -1.0f,

                        // Left face
                       -1.0f, 0.0f, 0.0f,
                       -1.0f, 0.0f, 0.0f,
                       -1.0f, 0.0f, 0.0f,
                       -1.0f, 0.0f, 0.0f,
                       -1.0f, 0.0f, 0.0f,
                       -1.0f, 0.0f, 0.0f,

                        // Top face
                        0.0f, 1.0f, 0.0f,
                        0.0f, 1.0f, 0.0f,
                        0.0f, 1.0f, 0.0f,
                        0.0f, 1.0f, 0.0f,
                        0.0f, 1.0f, 0.0f,
                        0.0f, 1.0f, 0.0f,

                        // Bottom face
                        0.0f, -1.0f, 0.0f,
                        0.0f, -1.0f, 0.0f,
                        0.0f, -1.0f, 0.0f,
                        0.0f, -1.0f, 0.0f,
                        0.0f, -1.0f, 0.0f,
                        0.0f, -1.0f, 0.0f}
        );

    }

    public void onDrawFrame(GL10 unused) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        long time = SystemClock.uptimeMillis() % 10000L;
        float angleInDegrees = (360.0f / 10000.0f) * ((int) time);

        final float eyeX = 0.0f;
        final float eyeY = 0.0f;
        final float eyeZ = -0.5f;

        final float lookX = 0.0f;
        final float lookY = 0.0f;
        final float lookZ = -5.0f;

        final float upX = 0.0f;
        final float upY = 1.0f;
        final float upZ = 0.0f;

        Matrix.setLookAtM(viewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);

        // Draw light
        Matrix.setIdentityM(lightModelMatrix, 0);
        Matrix.translateM(lightModelMatrix, 0, 0.0f, 0.0f, -5.0f);
        Matrix.rotateM(lightModelMatrix, 0, angleInDegrees, 0.0f, 1.0f, 0.0f);
        Matrix.translateM(lightModelMatrix, 0, 0.0f, -1.3f, 1.0f);

        Matrix.multiplyMV(lightPosInWorldSpace, 0, lightModelMatrix, 0, lightPosInModelSpace, 0);
        Matrix.multiplyMV(lightPosInEyeSpace, 0, viewMatrix, 0, lightPosInWorldSpace, 0);

        Matrix.multiplyMM(mVPMatrix, 0, viewMatrix, 0, lightModelMatrix, 0);
        Matrix.multiplyMM(mVPMatrix, 0, projectionMatrix, 0, mVPMatrix, 0);

        mShape.drawLight(mVPMatrix, lightPosInModelSpace);

        // Draw cube
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, 0.0f, 0.0f, -5.0f);
        Matrix.rotateM(modelMatrix, 0, angleInDegrees, 1.0f, 1.0f, 0.0f);

        Matrix.multiplyMM(mVPMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(mVPMatrix, 0, projectionMatrix, 0, mVPMatrix, 0);

        mShape.draw(mVPMatrix, lightPosInEyeSpace);

    }

    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        final float ratio = (float) width / height;
        final float left = -ratio;
        final float right = ratio;
        final float bottom = -1.0f;
        final float top = 1.0f;
        final float near = 1.0f;
        final float far = 10.0f;

        Matrix.frustumM(projectionMatrix, 0, left, right, bottom, top, near, far);

    }

    public static int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);

        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    public static int createAndLinkProgram(final int vertexShaderHandle, final int fragmentShaderHandle, final String[] attributes) {
        int programHandle = GLES20.glCreateProgram();

        if (programHandle != 0) {
            GLES20.glAttachShader(programHandle, vertexShaderHandle);

            GLES20.glAttachShader(programHandle, fragmentShaderHandle);

            if (attributes != null) {
                final int size = attributes.length;
                for (int i = 0; i < size; i++) {
                    GLES20.glBindAttribLocation(programHandle, i, attributes[i]);
                }
            }

            GLES20.glLinkProgram(programHandle);

            final int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, linkStatus, 0);

            if (linkStatus[0] == 0) {
                Log.e(TAG, "Error compiling program: " + GLES20.glGetProgramInfoLog(programHandle));
                GLES20.glDeleteProgram(programHandle);
                programHandle = 0;
            }
        }

        if (programHandle == 0) {
            throw new RuntimeException("Error creating program.");
        }

        return programHandle;
    }

}