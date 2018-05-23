package com.shuiyes.video.widget;

import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ScrollView;

public class ReboundScrollView  extends ScrollView{

    /** 滑动距离为 屏幕的 1/5 */
    private static final float SCROLL_RATIO = 0.25f;
    private static final int ANIM_TIME = 500;
    private float mStartY = 0;
    private int mDeltaY = 0;

    private boolean mIsTouchDown = false;
    private boolean mIsOverScrolled = false;

    private View mContentView;
    private Rect mOriginalRect = new Rect();

    public ReboundScrollView(Context context) {
        super(context);
    }

    public ReboundScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ReboundScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() > 0) {
            mContentView = getChildAt(0);
        }
    }

    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX,
                                   int scrollY, int scrollRangeX, int scrollRangeY,
                                   int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        mDeltaY = deltaY;
        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX,
                scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX,
                                  boolean clampedY) {
        if(clampedY && !mIsTouchDown){
            animation();
        }
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
    }

    // 开启动画移动
    public void animation() {
        if(mContentView == null){
            return;
        }
        if (mOriginalRect.isEmpty()) {
            // 保存正常的布局位置
            mOriginalRect.set(mContentView.getLeft(), mContentView.getTop(),mContentView.getRight(), mContentView.getBottom());
            int newTop = mContentView.getTop() - mDeltaY;
            int newBottom = mContentView.getBottom() - mDeltaY;
            // 移动布局
            mContentView.layout(mContentView.getLeft(), newTop, mContentView.getRight(), newBottom);
            mIsOverScrolled = true;
            mHandler.sendEmptyMessageDelayed(0, ANIM_TIME);
        }

        // 开启Y轴平移动画
        TranslateAnimation ta = new TranslateAnimation(0, 0, mContentView.getTop(), mOriginalRect.top);
        ta.setDuration(ANIM_TIME);
        mContentView.startAnimation(ta);

        // 回到正常的布局位置
        mContentView.layout(mOriginalRect.left, mOriginalRect.top, mOriginalRect.right, mOriginalRect.bottom);
        mOriginalRect.setEmpty();
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            mIsOverScrolled = false;
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (mContentView != null) {
            commOnTouchEvent(e);
        }
        return super.onTouchEvent(e);
    }

    public void commOnTouchEvent(MotionEvent e) {
        mIsTouchDown = true;
        int action = e.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mStartY = e.getY();
                break;
            case MotionEvent.ACTION_UP:
                mIsTouchDown = false;
                if (isNeedAnimation() && !mIsOverScrolled) {
                    animation();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                final float preY = mStartY;
                float nowY = e.getY();
                int offset = (int)((preY - nowY)*SCROLL_RATIO);

                mStartY = nowY;
                if (isNeedMove()) {
                    if (mOriginalRect.isEmpty()) {
                        // 保存正常的布局位置
                        mOriginalRect.set(mContentView.getLeft(), mContentView.getTop(),mContentView.getRight(), mContentView.getBottom());
                        return;
                    }
                    int newTop = mContentView.getTop() - offset;
                    int newBottom = mContentView.getBottom() - offset;

                    // 移动布局
                    mContentView.layout(mContentView.getLeft(), newTop, mContentView.getRight(), newBottom);
                }
                break;
            default:
                break;
        }
    }

    public boolean isNeedAnimation() {
        return !mOriginalRect.isEmpty();
    }

    public boolean isNeedMove() {
        int scrollRangeY = mContentView.getMeasuredHeight() - getHeight();
        int scrollY = getScrollY();
        // 顶部/底部位置
        if (scrollY == 0 || scrollY == scrollRangeY) {
            return true;
        }
        return false;
    }

}
