package com.shuiyes.video.util;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.util.TypedValue;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;

import static com.shuiyes.video.os.WindowManagerPolicyConstants.NAV_BAR_BOTTOM;
import static com.shuiyes.video.os.WindowManagerPolicyConstants.NAV_BAR_LEFT;
import static com.shuiyes.video.os.WindowManagerPolicyConstants.NAV_BAR_RIGHT;

/**
 * Window工具类
 * Created by Devlin_n on 2017/4/10.
 */

public class WindowUtil {

    /**
     * 获取状态栏高度
     */
    public static double getStatusBarHeight(Context context) {
        Resources resources = context.getResources();
        int resid = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (resid > 0) {
            return resources.getDimensionPixelSize(resid);
        } else {
            return Math.ceil(25 * context.getResources().getDisplayMetrics().density);
        }
    }

    /**
     * 获取NavigationBar的高度
     */
    public static int getNavigationBarHeight(Context context) {
        if (!hasNavigationBar(context)) {
            return 0;
        }

        if (WindowUtil.navigationBarPosition(context) == NAV_BAR_BOTTOM) {
            Resources resources = context.getResources();
            int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    // cp from PhoneWindowManager
//    public static int navigationBarPosition(int displayWidth, int displayHeight, int displayRotation) {
    public static int navigationBarPosition(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);

        int displayWidth = size.x;
        int displayHeight = size.y;
        int displayRotation = display.getRotation();

        if (displayWidth > displayHeight) {
            if (displayRotation == Surface.ROTATION_270) {
                return NAV_BAR_LEFT;
            } else {
                return NAV_BAR_RIGHT;
            }
        }
        return NAV_BAR_BOTTOM;
    }

    /**
     * 是否存在NavigationBar
     */
    public static boolean hasNavigationBar(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Display display = getWindowManager(context).getDefaultDisplay();
            Point size = new Point();
            Point realSize = new Point();
            display.getSize(size);
            display.getRealSize(realSize);
            return realSize.x != size.x || realSize.y != size.y;
        } else {
            boolean menu = ViewConfiguration.get(context).hasPermanentMenuKey();
            boolean back = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
            return !(menu || back);
        }
    }

    /**
     * 获取屏幕宽度
     */
    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 获取屏幕高度
     */
    public static int getScreenHeight(Context context, boolean isIncludeNav) {
        if (isIncludeNav) {
            return context.getResources().getDisplayMetrics().heightPixels + getNavigationBarHeight(context);
        } else {
            return context.getResources().getDisplayMetrics().heightPixels;
        }
    }

    /**
     * 隐藏ActionBar和StatusBar
     */
    public static void hideSupportActionBar(Context context, boolean actionBar, boolean statusBar) {
        if (actionBar) {
            AppCompatActivity appCompatActivity = getAppCompActivity(context);
            if (appCompatActivity != null) {
                ActionBar ab = appCompatActivity.getSupportActionBar();
                if (ab != null) {
                    ab.setShowHideAnimationEnabled(false);
                    ab.hide();
                }
            }
        }
        if (statusBar) {
            if (context instanceof FragmentActivity) {
                FragmentActivity fragmentActivity = (FragmentActivity) context;
                fragmentActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
                fragmentActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            } else {
                scanForActivity(context).getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
                scanForActivity(context).getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            }
        }
    }

    /**
     * 显示ActionBar和StatusBar
     */
    public static void showSupportActionBar(Context context, boolean actionBar, boolean statusBar) {
        if (actionBar) {
            AppCompatActivity appCompatActivity = getAppCompActivity(context);
            if (appCompatActivity != null) {
                ActionBar ab = appCompatActivity.getSupportActionBar();
                if (ab != null) {
                    ab.setShowHideAnimationEnabled(false);
                    ab.show();
                }
            }
        }

        if (statusBar) {
            if (context instanceof FragmentActivity) {
                FragmentActivity fragmentActivity = (FragmentActivity) context;
                fragmentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                fragmentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            } else {
                scanForActivity(context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                scanForActivity(context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            }
        }
    }

    /**
     * 获取Activity
     */
    public static Activity scanForActivity(Context context) {
        return context == null ? null : (context instanceof Activity ? (Activity) context : (context instanceof ContextWrapper ? scanForActivity(((ContextWrapper) context).getBaseContext()) : null));
    }

    /**
     * 隐藏NavigationBar
     */
    public static void hideNavKey(Context context) {
        int flag;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //       设置屏幕始终在前面，不然点击鼠标，重新出现虚拟按键
            flag = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        } else {
            flag = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION; // hide nav
        }
        scanForActivity(context).getWindow().getDecorView().setSystemUiVisibility(flag);
    }

    /**
     * 显示NavigationBar
     */
    public static void showNavKey(Context context) {
        scanForActivity(context).getWindow().getDecorView().setSystemUiVisibility(0);
    }

    /**
     * 隐藏状态栏
     */
    public static void hideStatusBar(Context context) {
        WindowManager.LayoutParams attrs = scanForActivity(context).getWindow().getAttributes();
        attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        scanForActivity(context).getWindow().setAttributes(attrs);
    }

    /**
     * 显示状态栏
     */
    public static void showStatusBar(Context context) {
        WindowManager.LayoutParams attrs = scanForActivity(context).getWindow().getAttributes();
        attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
        scanForActivity(context).getWindow().setAttributes(attrs);
    }


    /**
     * Get AppCompatActivity from context
     */
    public static AppCompatActivity getAppCompActivity(Context context) {
        if (context == null) return null;
        if (context instanceof AppCompatActivity) {
            return (AppCompatActivity) context;
        } else if (context instanceof ContextThemeWrapper) {
            return getAppCompActivity(((ContextThemeWrapper) context).getBaseContext());
        }
        return null;
    }

    /**
     * dp转为px
     */
    public static int dp2px(Context context, float dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, context.getResources().getDisplayMetrics());
    }

    /**
     * sp转为px
     */
    public static int sp2px(Context context, float dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, dpValue, context.getResources().getDisplayMetrics());
    }

    /**
     * 如果WindowManager还未创建，则创建一个新的WindowManager返回。否则返回当前已创建的WindowManager。
     */
    public static WindowManager getWindowManager(Context context) {
        return (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }
}
