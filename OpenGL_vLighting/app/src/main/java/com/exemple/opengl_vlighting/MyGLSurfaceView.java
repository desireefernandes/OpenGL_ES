package com.exemple.opengl_vlighting;

import android.content.Context;
import android.opengl.GLSurfaceView;

class MyGLSurfaceView extends GLSurfaceView {

    private final MyGLRenderer renderer;

    public MyGLSurfaceView(Context context){
        super(context);

        setEGLContextClientVersion(2);

        renderer = new MyGLRenderer();

        setRenderer(renderer);

        //setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

    }
}