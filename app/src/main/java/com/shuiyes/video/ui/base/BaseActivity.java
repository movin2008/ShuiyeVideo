package com.shuiyes.video.ui.base;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.shuiyes.video.util.Constants;
import com.shuiyes.video.util.WindowUtil;
import com.shuiyes.video.view.AutoGestureListener;
import com.shuiyes.video.widget.Tips;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseActivity extends AppCompatActivity {

    protected final String TAG = this.getClass().getSimpleName();

    protected Context mContext;

    private String[] mPermissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };

    protected Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MSG_SHOW_TIPS:
                    Tips.show(mContext, (String)msg.obj);
                    break;
                case Constants.MSG_CHECK_PERMISSION:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        List<String> permissions = new ArrayList<String>();
                        for (int i = 0; i < mPermissions.length; i++) {
                            String permission = mPermissions[i];
                            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                                permissions.add(permission);
                            }
                        }
                        if (!permissions.isEmpty()) {
                            requestPermissions(permissions.toArray(new String[permissions.size()]), 100);
                        }
                    }
                    break;
                default:
                    handleOtherMessage(msg);
                    break;
            }
        };
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && requestCode == 100) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    //判断是否勾选禁止后不再询问
                    boolean showRequestPermission = shouldShowRequestPermissionRationale(permissions[i]);
                    String permission = permissions[i];
                    if (showRequestPermission) {
                        Tips.show(mContext, permissions);
                        finish();
                        break;
                    }else{
                        requestPermissions(new String[]{permission}, 100);
                    }
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    protected void handleOtherMessage(Message msg){}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;

        mGetector = new GestureDetector(this, new AutoGestureListener(this));

        WindowUtil.hideStatusBar(this);
        WindowUtil.hideNavKey(this);

        WindowUtil.scanForActivity(this).getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        WindowUtil.scanForActivity(this).getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
    }

    private GestureDetector mGetector;

    @Override
    protected void onResume() {
        super.onResume();
        mHandler.sendEmptyMessage(Constants.MSG_CHECK_PERMISSION);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    protected long mPrevBackTime;

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(event.getAction() == KeyEvent.ACTION_UP){
            return super.dispatchKeyEvent(event);
        }

        /**
         public static final int KEYCODE_DPAD_UP = 19;
         public static final int KEYCODE_DPAD_DOWN = 20;
         public static final int KEYCODE_DPAD_LEFT = 21;
         public static final int KEYCODE_DPAD_RIGHT = 22;
         public static final int KEYCODE_DPAD_CENTER = 23;
         */

        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_BACK:
                long time = System.currentTimeMillis();
                if ((time - mPrevBackTime) < 2000) {
                    finish();
                } else {
                    Tips.show(this, "再按一次返回", 0);
                }
                mPrevBackTime = time;
                return false;
            case KeyEvent.KEYCODE_DEL:
            case KeyEvent.KEYCODE_MENU:
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
            case KeyEvent.KEYCODE_DPAD_DOWN:
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                break;
            default:
//                Tips.show(this, "onKeyDown=" + keyCode, 0);
//                Log.e(TAG, event.toString());
                break;
        }

        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        mGetector.onTouchEvent(event);
        return super.dispatchTouchEvent(event);
    }

}
