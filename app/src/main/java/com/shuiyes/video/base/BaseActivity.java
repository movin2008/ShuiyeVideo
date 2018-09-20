package com.shuiyes.video.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;

import com.shuiyes.video.widget.Tips;

public abstract class BaseActivity extends Activity {

    protected Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private long mPrevBackTime;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        /**
         public static final int KEYCODE_DPAD_UP = 19;
         public static final int KEYCODE_DPAD_DOWN = 20;
         public static final int KEYCODE_DPAD_LEFT = 21;
         public static final int KEYCODE_DPAD_RIGHT = 22;
         public static final int KEYCODE_DPAD_CENTER = 23;
         */
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                long time = System.currentTimeMillis();
                if ((time - mPrevBackTime) < 2000) {
                    finish();
                } else {
                    Tips.show(this, "再按一次退出播放", 0);
                }
                mPrevBackTime = time;
                return false;
            default:
//                Tips.show(this, "onKeyDown=" + keyCode, 0);
//                Log.e("HAHA", "onKeyDown=" + keyCode);
                break;
        }

        return super.onKeyDown(keyCode, event);
    }

}
