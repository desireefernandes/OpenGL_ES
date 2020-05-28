package com.example.opengl_colors;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Shape {
    private final FloatBuffer vertexBuffer;

    private int mPositionHandle;
    private int colorHandle;
    private int vPMatrixHandle;

    private final int bytesPerFloat = 4;
    private final int strideBytes = 7 * bytesPerFloat;
    private final int positionOffset = 0;
    private final int positionDataSize = 3;
    private final int colorOffset = 3;
    private final int colorDataSize = 4;

    private void setShapeCoords(float[] shapeCoords) {
    }

    public Shape(float[] shapeCoords) {
        this.setShapeCoords(shapeCoords);

        vertexBuffer = ByteBuffer.allocateDirect(
                shapeCoords.length * bytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();

        vertexBuffer.put(shapeCoords).position(0);

    }

    private final String vertexShaderCode =
            "uniform mat4 u_MVPMatrix;      \n"

                    + "attribute vec4 a_Position;     \n"
                    + "attribute vec4 a_Color;        \n"

                    + "varying vec4 v_Color;          \n"

                    + "void main()                    \n"
                    + "{                              \n"
                    + "   v_Color = a_Color;          \n"

                    + "   gl_Position = u_MVPMatrix   \n"
                    + "               * a_Position;   \n"
                    + "}                              \n";

    private final String fragmentShaderCode =
            "precision mediump float;       \n"

                    + "varying vec4 v_Color;          \n"

                    + "void main()                    \n"
                    + "{                              \n"
                    + "   gl_FragColor = v_Color;     \n"
                    + "}";

    public void draw(float[] mvpMatrix) {
        int vertexShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);

        if (vertexShader != 0) {
            GLES20.glShaderSource(vertexShader, vertexShaderCode);
            GLES20.glCompileShader(vertexShader);

            final int[] compileStatus = new int[1];
            GLES20.glGetShaderiv(vertexShader, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

            if (compileStatus[0] == 0) {
                GLES20.glDeleteShader(vertexShader);
                vertexShader = 0;
            }
        }

        if (vertexShader == 0) {
            throw new RuntimeException("Error creating vertex shader.");
        }

        int fragmentShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);

        if (fragmentShader != 0) {
            GLES20.glShaderSource(fragmentShader, fragmentShaderCode);
            GLES20.glCompileShader(fragmentShader);

            final int[] compileStatus = new int[1];
            GLES20.glGetShaderiv(fragmentShader, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

            if (compileStatus[0] == 0) {
                GLES20.glDeleteShader(fragmentShader);
                fragmentShader = 0;
            }
        }

        if (fragmentShader == 0) {
            throw new RuntimeException("Error creating fragment shader.");
        }

        int programHandle = GLES20.glCreateProgram();

        if (programHandle != 0) {
            // Bind the vertex shader to the program.
            GLES20.glAttachShader(programHandle, vertexShader);

            // Bind the fragment shader to the program.
            GLES20.glAttachShader(programHandle, fragmentShader);

            // Bind attributes
            GLES20.glBindAttribLocation(programHandle, 0, "a_Position");
            GLES20.glBindAttribLocation(programHandle, 1, "a_Color");

            // Link the two shaders together into a program.
            GLES20.glLinkProgram(programHandle);

            // Get the link status.
            final int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, linkStatus, 0);

            // If the link failed, delete the program.
            if (linkStatus[0] == 0) {
                GLES20.glDeleteProgram(programHandle);
                programHandle = 0;
            }
        }

        if (programHandle == 0) {
            throw new RuntimeException("Error creating program.");
        }

        vPMatrixHandle = GLES20.glGetUniformLocation(programHandle, "u_MVPMatrix");
        mPositionHandle = GLES20.glGetAttribLocation(programHandle, "a_Position");
        colorHandle = GLES20.glGetAttribLocation(programHandle, "a_Color");

        GLES20.glUseProgram(programHandle);

        vertexBuffer.position(positionOffset);
        GLES20.glVertexAttribPointer(mPositionHandle, positionDataSize, GLES20.GL_FLOAT, false,
                strideBytes, vertexBuffer);

        GLES20.glEnableVertexAttribArray(mPositionHandle);

        vertexBuffer.position(colorOffset);
        GLES20.glVertexAttribPointer(colorHandle, colorDataSize, GLES20.GL_FLOAT, false,
                strideBytes, vertexBuffer);

        GLES20.glEnableVertexAttribArray(colorHandle);

        GLES20.glUniformMatrix4fv(vPMatrixHandle, 1, false, mvpMatrix, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);

    }

}