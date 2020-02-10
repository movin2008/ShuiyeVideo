package com.shuiye.video.util;

import android.content.Context;

public class ResourceUtils {

    public static int flowBtnMargin(Context context) {
        return ResourceUtils.dip2px(context, 10);
    }

    public static int flowBtnPadding(Context context) {
        return ResourceUtils.dip2px(context, 10);
    }

    public static int flowBtnWH(Context context) {
        return ResourceUtils.dip2px(context, 60);
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
