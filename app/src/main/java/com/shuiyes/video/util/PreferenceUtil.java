package com.shuiyes.video.util;

import android.content.Context;
import android.preference.PreferenceManager;

import com.shuiyes.video.ui.youku.YoukuUtils;

public class PreferenceUtil {

    static String getString(Context context, String key, String defaultValue) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(key, defaultValue);
    }

    static void putString(Context context, String key, String value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(key, value).commit();
    }

    static final String KEY_CCODE = "youku_ccode";

    public static String getCCODE(Context context) {
        return PreferenceUtil.getString(context, KEY_CCODE, YoukuUtils.CCODE);
    }

    public static void setCCODE(Context context, String ccode) {
        PreferenceUtil.putString(context, KEY_CCODE, ccode);
    }

    static final String KEY_SO_KEYWORDS = "so_keywords";

    public static String getSearchKeywords(Context context) {
        return PreferenceUtil.getString(context, KEY_SO_KEYWORDS, "庐剧");
    }

    public static void setSearchKeywords(Context context, String keywords) {
        PreferenceUtil.putString(context, KEY_SO_KEYWORDS, keywords);
    }

    static final String KEY_PLAY_URL = "play_url";

    public static String getPlayUrl(Context context) {
        return PreferenceUtil.getString(context, KEY_PLAY_URL, "");
    }

    public static void setPlayUrl(Context context, String url) {
        PreferenceUtil.putString(context, KEY_PLAY_URL, url);
    }

    static final String KEY_TV_URL = "tv_url";

    public static String getTVUrl(Context context) {
        return PreferenceUtil.getString(context, KEY_TV_URL, "");
    }

    public static void setTVUrl(Context context, String url) {
        PreferenceUtil.putString(context, KEY_TV_URL, url);
    }

    static final String KEY_HUYA_INVAILD_URL = "huya_invaild_url";

    public static String getHuyaInvaildUrl(Context context) {
        return PreferenceUtil.getString(context, KEY_HUYA_INVAILD_URL, "");
    }

    public static void setHuyaInvaildUrl(Context context, String url) {
        PreferenceUtil.putString(context, KEY_HUYA_INVAILD_URL, url);
    }

}
