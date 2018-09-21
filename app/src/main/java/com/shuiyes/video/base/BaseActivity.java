package com.shuiyes.video.base;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;

import com.shuiyes.video.util.Constants;
import com.shuiyes.video.widget.Tips;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseActivity extends Activity {

    private final String TAG = this.getClass().getSimpleName();

    protected Context mContext;

    private String[] mPermissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };

    protected Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHandler.sendEmptyMessage(Constants.MSG_CHECK_PERMISSION);
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
//                Log.e(TAG, "onKeyDown=" + keyCode);
                break;
        }

        return super.onKeyDown(keyCode, event);
    }

}
