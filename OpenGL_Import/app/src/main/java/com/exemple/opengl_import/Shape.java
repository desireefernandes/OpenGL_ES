package com.exemple.opengl_import;

import android.content.Context;
import android.opengl.GLES20;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Shape {
    private FloatBuffer verticesBuffer;
    private ShortBuffer facesBuffer;
    private List<String> verticesList;
    private List<String> facesList;

    private int mVPMatrixHandle;

    private int mVMatrixHandle;
    private int positionHandle;

    private int mProgram;

    public Shape(Context c){
        verticesList = new ArrayList<>();
        facesList = new ArrayList<>();

        try {
            Scanner scanner = new Scanner(c.getAssets().open("shape.obj"));
            while(scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if(line.startsWith("v ")) {
                    verticesList.add(line);
                } else if(line.startsWith("f ")) {
                    facesList.add(line);
                }
            }
            scanner.close();

            ByteBuffer buffer1 = ByteBuffer.allocateDirect(verticesList.size() * 3 * 4);
            buffer1.order(ByteOrder.nativeOrder());
            verticesBuffer = buffer1.asFloatBuffer();

            ByteBuffer buffer2 = ByteBuffer.allocateDirect(facesList.size() * 3 * 2);
            buffer2.order(ByteOrder.nativeOrder());
            facesBuffer = buffer2.asShortBuffer();

            for(String vertex: verticesList) {
                String coords[] = vertex.split(" ");
                float x = Float.parseFloat(coords[1]);
                float y = Float.parseFloat(coords[2]);
                float z = Float.parseFloat(coords[3]);
                verticesBuffer.put(x);
                verticesBuffer.put(y);
                verticesBuffer.put(z);
            }
            verticesBuffer.position(0);

            for(String face: facesList) {
                String vertexIndices[] = face.split(" ");
                short vertex1 = Short.parseShort(vertexIndices[1]);
                short vertex2 = Short.parseShort(vertexIndices[2]);
                short vertex3 = Short.parseShort(vertexIndices[3]);
                facesBuffer.put((short)(vertex1 - 1));
                facesBuffer.put((short)(vertex2 - 1));
                facesBuffer.put((short)(vertex3 - 1));
            }
            facesBuffer.position(0);

            InputStream vertexShaderStream = c.getResources().openRawResource(R.raw.vertex_shader);
            String vertexShaderCode = IOUtils.toString(vertexShaderStream, Charset.defaultCharset());
            vertexShaderStream.close();

            InputStream fragmentShaderStream = c.getResources().openRawResource(R.raw.fragment_shader);
            String fragmentShaderCode = IOUtils.toString(fragmentShaderStream, Charset.defaultCharset());
            fragmentShaderStream.close();

            int vertexShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
            GLES20.glShaderSource(vertexShader, vertexShaderCode);

            int fragmentShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
            GLES20.glShaderSource(fragmentShader, fragmentShaderCode);

            GLES20.glCompileShader(vertexShader);
            GLES20.glCompileShader(fragmentShader);

            mProgram = GLES20.glCreateProgram();
            GLES20.glAttachShader(mProgram, vertexShader);
            GLES20.glAttachShader(mProgram, fragmentShader);
            GLES20.glLinkProgram(mProgram);

            GLES20.glUseProgram(mProgram);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void draw(float[] mVPMatrix){
        GLES20.glUseProgram(mProgram);

        positionHandle = GLES20.glGetAttribLocation(mProgram, "Position");
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(positionHandle, 3,
                GLES20.GL_FLOAT, false, 3 * 4, verticesBuffer);

        mVMatrixHandle = GLES20.glGetUniformLocation(mProgram, "u_MVPMatrix");
        GLES20.glUniformMatrix4fv(mVPMatrixHandle, 1, false, mVPMatrix, 0);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, facesList.size() * 3, GLES20.GL_UNSIGNED_SHORT, facesBuffer);
        GLES20.glDisableVertexAttribArray(positionHandle);

    }

}