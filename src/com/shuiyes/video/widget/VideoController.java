package com.shuiyes.video.widget;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.MediaController;

import com.shuiyes.video.R;

import java.lang.reflect.Method;

public class VideoController extends MediaController{
    public VideoController(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VideoController(Context context, boolean useFastForward) {
        super(context, useFastForward);
    }

    public VideoController(Context context) {
        super(context);
    }

    @Override
    public void setAnchorView(View view) {
        super.setAnchorView(view);

        Log.e("HAHA", " =========================== Build.VERSION.SDK_INT="+Build.VERSION.SDK_INT);
        if(Build.VERSION.SDK_INT == 19){
            FrameLayout.LayoutParams frameParams = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            );

            removeAllViews();
            LayoutInflater inflate = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflate.inflate(R.layout.media_controller, null);

            try {
                Method m_initControllerView = MediaController.class.getDeclaredMethod("initControllerView", View.class);
                m_initControllerView.setAccessible(true);
                m_initControllerView.invoke(this, v);
            }catch (Exception e){
                e.printStackTrace();
            }

            addView(v, frameParams);
        }else if(Build.VERSION.SDK_INT == 26){

        }
    }
}
