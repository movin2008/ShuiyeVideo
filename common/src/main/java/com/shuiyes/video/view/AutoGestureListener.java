package com.shuiyes.video.view;

import android.content.Context;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.shuiyes.video.util.WindowUtil;

public class AutoGestureListener extends GestureDetector.SimpleOnGestureListener {

    private Context context;
    public AutoGestureListener(Context context){
        this.context = context;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        if (!gestureEnabled) return super.onDown(e);
        return true;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        if (mShowing) {
            hide();
        } else {
            show();
        }
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (!gestureEnabled) return super.onScroll(e1, e2, distanceX, distanceY);
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        return true;
    }

    //控制器是否处于显示状态
    protected boolean mShowing, gestureEnabled;
    protected int sDefaultTimeout = 3000;
    private Handler mHandler = new Handler();

    public void hide() {
        if (mShowing) {
            mShowing = false;
            WindowUtil.hideStatusBar(context);
            WindowUtil.hideNavKey(context);
        }
    }

    public void show() {
        show(sDefaultTimeout);
    }

    private void show(int timeout) {
        if (!mShowing) {
            mShowing = true;
            WindowUtil.showStatusBar(context);
            WindowUtil.showNavKey(context);

            mHandler.removeCallbacks(mFadeOut);
            if (timeout != 0) {
                mHandler.postDelayed(mFadeOut, timeout);
            }
        }
    }

    protected final Runnable mFadeOut = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
}
