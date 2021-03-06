package com.exemple.opengl_basictexturing;

import android.content.Context;
import android.opengl.GLES20;

import com.exemple.opengl_basictexturing.common.RawResourceReader;
import com.exemple.opengl_basictexturing.common.ShaderHelper;
import com.exemple.opengl_basictexturing.common.TextureHelper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Shape {
    private final FloatBuffer cubePositionsBuffer;
    private final FloatBuffer cubeColorsBuffer;
    private final FloatBuffer cubeNormalsBuffer;
    private final FloatBuffer cubeTextureCoordinatesBuffer;

    private int mVPMatrixHandle;
    private int mVMatrixHandle;
    private int lightPosHandle;
    private int mPositionHandle;
    private int textureCoordinateHandle;
    private int textureUniformHandle;
    private int colorHandle;
    private int normalHandle;

    private final int bytesPerFloat = 4;
    private final int positionDataSize = 3;
    private final int colorDataSize = 4;
    private final int normalDataSize = 3;
    private final int textureCoordinateDataSize = 2;

    private int mProgramHandle;
    private int pointProgramHandle;
    private int textureDataHandle;

    private void setCubePositionData(float[] cubePositionData) {
    }
    private void setCubeColorData(float[] cubeColorData) {
    }
    private void setCubeNormalData(float[] cubeNormalData) {
    }
    public void setCubeTextureCoordinateData(float[] cubeTextureCoordinateData) {
    }

    public Shape(Context c, float[] cubePositionData, float[] cubeColorData, float[] cubeNormalData, float[] cubeTextureCoordinateData){
        this.setCubePositionData(cubePositionData);
        this.setCubeColorData(cubeColorData);
        this.setCubeNormalData(cubeNormalData);
        this.setCubeTextureCoordinateData(cubeTextureCoordinateData);

        final String vertexShader = getVertexShader(c);
        final String fragmentShader = getFragmentShader(c);

        final int vertexShaderHandle = ShaderHelper.compileShader(GLES20.GL_VERTEX_SHADER, vertexShader);
        final int fragmentShaderHandle = ShaderHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader);

        mProgramHandle = ShaderHelper.createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle,
                new String[] {"a_Position",  "a_Color", "a_Normal", "a_TexCoordinate"});

        final String pointVertexShader = RawResourceReader.readTextFileFromRawResource(c, R.raw.point_vertex_shader);
        final String pointFragmentShader = RawResourceReader.readTextFileFromRawResource(c, R.raw.point_fragment_shader);

        final int pointVertexShaderHandle = ShaderHelper.compileShader(GLES20.GL_VERTEX_SHADER, pointVertexShader);
        final int pointFragmentShaderHandle = ShaderHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, pointFragmentShader);
        pointProgramHandle = ShaderHelper.createAndLinkProgram(pointVertexShaderHandle, pointFragmentShaderHandle,
                new String[] {"a_Position"});

        textureDataHandle = TextureHelper.loadTexture(c, R.drawable.bumpy_bricks_public_domain);

        cubePositionsBuffer = ByteBuffer.allocateDirect(cubePositionData.length * bytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        cubePositionsBuffer.put(cubePositionData).position(0);

        cubeColorsBuffer = ByteBuffer.allocateDirect(cubeColorData.length * bytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        cubeColorsBuffer.put(cubeColorData).position(0);

        cubeNormalsBuffer = ByteBuffer.allocateDirect(cubeNormalData.length * bytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        cubeNormalsBuffer.put(cubeNormalData).position(0);

        cubeTextureCoordinatesBuffer = ByteBuffer.allocateDirect(cubeNormalData.length * bytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        cubeTextureCoordinatesBuffer.put(cubeTextureCoordinateData).position(0);

    }

    protected String getVertexShader(Context activityContext) {
        final Context c;
        c = activityContext;

        return RawResourceReader.readTextFileFromRawResource(c, R.raw.per_pixel_vertex_shader);
    }

    protected String getFragmentShader(Context activityContext) {
        final Context c;
        c = activityContext;

        return RawResourceReader.readTextFileFromRawResource(c, R.raw.per_pixel_fragment_shader);
    }

    public void draw(float[] mVPMatrix, float[] lightPosInEyeSpace){
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        GLES20.glUseProgram(mProgramHandle);

        mPositionHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Position");
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mPositionHandle, positionDataSize,
                GLES20.GL_FLOAT, false,
                0, cubePositionsBuffer);
        cubePositionsBuffer.position(0);

        colorHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Color");
        GLES20.glEnableVertexAttribArray(colorHandle);
        GLES20.glVertexAttribPointer(colorHandle, colorDataSize,
                GLES20.GL_FLOAT, false,
                0, cubeColorsBuffer);
        cubeColorsBuffer.position(0);

        normalHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Normal");
        GLES20.glEnableVertexAttribArray(normalHandle);
        GLES20.glVertexAttribPointer(normalHandle, normalDataSize,
                GLES20.GL_FLOAT, false,
                0, cubeNormalsBuffer);
        cubeNormalsBuffer.position(0);

        cubeTextureCoordinatesBuffer.position(0);
        GLES20.glEnableVertexAttribArray(textureCoordinateHandle);
        GLES20.glVertexAttribPointer(textureCoordinateHandle, textureCoordinateDataSize,
                GLES20.GL_FLOAT, false,
                0, cubeTextureCoordinatesBuffer);

        mVPMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_MVPMatrix");

        mVMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_MVMatrix");

        lightPosHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_LightPos");

        textureUniformHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_Texture");

        textureCoordinateHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_TexCoordinate");

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureDataHandle);
        GLES20.glUniform1i(textureUniformHandle, 0);

        GLES20.glUniformMatrix4fv(mVMatrixHandle, 1, false, mVPMatrix, 0);
        GLES20.glUniformMatrix4fv(mVPMatrixHandle, 1, false, mVPMatrix, 0);
        GLES20.glUniform3f(lightPosHandle, lightPosInEyeSpace[0], lightPosInEyeSpace[1], lightPosInEyeSpace[2]);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 36);

    }

    public void drawLight(float[] mMVPMatrix, float[] lightPosInModelSpace){
        GLES20.glUseProgram(pointProgramHandle);

        final int pointMVPMatrixHandle = GLES20.glGetUniformLocation(pointProgramHandle, "u_MVPMatrix");
        final int pointPositionHandle = GLES20.glGetAttribLocation(pointProgramHandle, "a_Position");

        GLES20.glVertexAttrib3f(pointPositionHandle, lightPosInModelSpace[0], lightPosInModelSpace[1], lightPosInModelSpace[2]);
        GLES20.glUniformMatrix4fv(pointMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 1);
        GLES20.glDisableVertexAttribArray(pointPositionHandle);

    }

}