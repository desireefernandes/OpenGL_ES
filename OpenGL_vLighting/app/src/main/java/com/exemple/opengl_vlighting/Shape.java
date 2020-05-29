package com.exemple.opengl_vlighting;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Shape {
    private final FloatBuffer cubePositionsBuffer;
    private final FloatBuffer cubeColorsBuffer;
    private final FloatBuffer cubeNormalsBuffer;

    private int mVPMatrixHandle;
    private int mVMatrixHandle;
    private int lightPosHandle;
    private int mPositionHandle;
    private int colorHandle;
    private int normalHandle;

    private final int bytesPerFloat = 4;
    private final int positionDataSize = 3;
    private final int colorDataSize = 4;
    private final int normalDataSize = 3;

    private int perVertexProgramHandle;
    private int pointProgramHandle;

    private void setCubePositionData(float[] cubePositionData) {
    }
    private void setCubeColorData(float[] cubeColorData) {
    }
    private void setCubeNormalData(float[] cubeNormalData) {
    }

    public Shape(float[] cubePositionData, float[] cubeColorData, float[] cubeNormalData){
        this.setCubePositionData(cubePositionData);
        this.setCubeColorData(cubeColorData);
        this.setCubeNormalData(cubeNormalData);

        final String vertexShader = getVertexShader();
        final String fragmentShader = getFragmentShader();

        final int vertexShaderHandle = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShader);
        final int fragmentShaderHandle = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader);

        perVertexProgramHandle = MyGLRenderer.createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle,
                new String[] {"a_Position",  "a_Color", "a_Normal"});

        final String pointVertexShader =
                "uniform mat4 u_MVPMatrix;      \n"
                        +	"attribute vec4 a_Position;     \n"
                        + "void main()                    \n"
                        + "{                              \n"
                        + "   gl_Position = u_MVPMatrix   \n"
                        + "               * a_Position;   \n"
                        + "   gl_PointSize = 5.0;         \n"
                        + "}                              \n";

        final String pointFragmentShader =
                "precision mediump float;       \n"
                        + "void main()                    \n"
                        + "{                              \n"
                        + "   gl_FragColor = vec4(1.0,    \n"
                        + "   1.0, 1.0, 1.0);             \n"
                        + "}                              \n";

        final int pointVertexShaderHandle =  MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, pointVertexShader);
        final int pointFragmentShaderHandle = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, pointFragmentShader);
        pointProgramHandle = MyGLRenderer.createAndLinkProgram(pointVertexShaderHandle, pointFragmentShaderHandle,
                new String[] {"a_Position"});

        cubePositionsBuffer = ByteBuffer.allocateDirect(cubePositionData.length * bytesPerFloat)
                           .order(ByteOrder.nativeOrder()).asFloatBuffer();
        cubePositionsBuffer.put(cubePositionData).position(0);

        cubeColorsBuffer = ByteBuffer.allocateDirect(cubeColorData.length * bytesPerFloat)
                        .order(ByteOrder.nativeOrder()).asFloatBuffer();
        cubeColorsBuffer.put(cubeColorData).position(0);

        cubeNormalsBuffer = ByteBuffer.allocateDirect(cubeNormalData.length * bytesPerFloat)
                         .order(ByteOrder.nativeOrder()).asFloatBuffer();
        cubeNormalsBuffer.put(cubeNormalData).position(0);

    }

    protected String getVertexShader() {
        final String vertexShader =
                "uniform mat4 u_MVPMatrix;      \n"
                        + "uniform mat4 u_MVMatrix;       \n"
                        + "uniform vec3 u_LightPos;       \n"

                        + "attribute vec4 a_Position;     \n"
                        + "attribute vec4 a_Color;        \n"
                        + "attribute vec3 a_Normal;       \n"

                        + "varying vec4 v_Color;          \n"

                        + "void main()                    \n"
                        + "{                              \n"
                        + "   vec3 modelViewVertex = vec3(u_MVMatrix * a_Position);              \n"
                        + "   vec3 modelViewNormal = vec3(u_MVMatrix * vec4(a_Normal, 0.0));     \n"

                        + "   float distance = length(u_LightPos - modelViewVertex);             \n"

                        + "   vec3 lightVector = normalize(u_LightPos - modelViewVertex);        \n"

                        + "   float diffuse = max(dot(modelViewNormal, lightVector), 0.1);       \n"

                        + "   diffuse = diffuse * (1.0 / (1.0 + (0.25 * distance * distance)));  \n"

                        + "   v_Color = a_Color * diffuse;                                       \n"
                        + "   gl_Position = u_MVPMatrix * a_Position;                            \n"
                        + "}                                                                     \n";

        return vertexShader;
    }

    protected String getFragmentShader() {
        final String fragmentShader =
                "precision mediump float;       \n"

                        + "varying vec4 v_Color;          \n"

                        + "void main()                    \n"
                        + "{                              \n"
                        + "   gl_FragColor = v_Color;     \n"
                        + "}                              \n";

        return fragmentShader;
    }

    public void draw(float[] mVPMatrix, float[] lightPosInEyeSpace){
        GLES20.glUseProgram(perVertexProgramHandle);

        lightPosHandle = GLES20.glGetUniformLocation(perVertexProgramHandle, "u_LightPos");

        mVPMatrixHandle = GLES20.glGetUniformLocation(perVertexProgramHandle, "u_MVPMatrix");

        mVMatrixHandle = GLES20.glGetUniformLocation(perVertexProgramHandle, "u_MVMatrix");

        mPositionHandle = GLES20.glGetAttribLocation(perVertexProgramHandle, "a_Position");
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mPositionHandle, positionDataSize,
                GLES20.GL_FLOAT, false,
                0, cubePositionsBuffer);
        cubePositionsBuffer.position(0);

        colorHandle = GLES20.glGetAttribLocation(perVertexProgramHandle, "a_Color");
        GLES20.glEnableVertexAttribArray(colorHandle);
        GLES20.glVertexAttribPointer(colorHandle, colorDataSize,
                GLES20.GL_FLOAT, false,
                0, cubeColorsBuffer);
        cubeColorsBuffer.position(0);

        normalHandle = GLES20.glGetAttribLocation(perVertexProgramHandle, "a_Normal");
        GLES20.glEnableVertexAttribArray(normalHandle);
        GLES20.glVertexAttribPointer(normalHandle, normalDataSize,
                GLES20.GL_FLOAT, false,
                0, cubeNormalsBuffer);
        cubeNormalsBuffer.position(0);

        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        GLES20.glUniformMatrix4fv(mVMatrixHandle, 1, false, mVPMatrix, 0);
        GLES20.glUniformMatrix4fv(mVPMatrixHandle, 1, false, mVPMatrix, 0);
        GLES20.glUniform3f(lightPosHandle, lightPosInEyeSpace[0], lightPosInEyeSpace[1], lightPosInEyeSpace[2]);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 36);

    }

    public void drawLight(float[] mMVPMatrix, float[] lightPosInModelSpace){
        GLES20.glUseProgram(pointProgramHandle);

        final int pointMVPMatrixHandle = GLES20.glGetUniformLocation(pointProgramHandle, "u_MVPMatrix");
        GLES20.glUniformMatrix4fv(pointMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        final int pointPositionHandle = GLES20.glGetAttribLocation(pointProgramHandle, "a_Position");
        GLES20.glVertexAttrib3f(pointPositionHandle, lightPosInModelSpace[0], lightPosInModelSpace[1], lightPosInModelSpace[2]);

        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 1);
        GLES20.glDisableVertexAttribArray(pointPositionHandle);
    }

}