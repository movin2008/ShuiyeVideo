package com.shuiyes.video.widget;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.Toast;

import com.shuiyes.common.R;

import java.util.Arrays;

public class Tips {

    /**
     * Toast align windows top
     *
     * @param context
     * @return
     */
    public static Toast createToast(Context context) {
        Toast toast = new Toast(context);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.setView(LayoutInflater.from(context).inflate(R.layout.transient_notification, null));
        return toast;
    }

    public static void show(Context context, CharSequence text) {
        show(context, text, 1);
    }

    private static Toast mToast;

    public static void show(Context context, CharSequence text, int duration) {
        if (mToast == null) {
            mToast = createToast(context);
        }
        mToast.setDuration(duration);
        mToast.setText(text);
        mToast.show();
    }

    public static void show(Context context, String[] permissions) {
        if (mToast == null) {
            mToast = createToast(context);
        }
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.setText(Arrays.toString(permissions));
        mToast.show();
    }

    public static void show(Context context, int resid, int duration) {
        if (mToast == null) {
            mToast = createToast(context);
        }
        mToast.setDuration(duration);
        mToast.setText(resid);
        mToast.show();
    }

}
