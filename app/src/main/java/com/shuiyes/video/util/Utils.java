package com.shuiyes.video.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.Display;

import com.shuiyes.video.widget.Tips;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.security.MessageDigest;

public class Utils {

    public static long timestamp() {
        return System.currentTimeMillis() / 1000;
    }

    public static String timestamps() {
        return System.currentTimeMillis() / 1000 + "";
    }

    public static void installTVBus(Context context) {
        try {
            InputStream in = context.getResources().getAssets().open("TVBus.apk");
            byte[] buffer = new byte[in.available()];
            in.read(buffer);
            in.close();

            File apkFile = new File("/sdcard/.shuiyes/tvbus.apk");
            apkFile.delete();

            OutputStream outStream = new FileOutputStream(apkFile);
            outStream.write(buffer);
            outStream.close();

            Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
            intent.setData(Uri.fromFile(apkFile));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Tips.show(context, "TVBus 播放插件安装失败 " + e.getLocalizedMessage());
        }
    }

    public static InputStream isTransparentHighlightCss(Context context, String url) {
        try {
            String cssPath = context.getCacheDir().getAbsolutePath() + "/tmp.css";
            File file = new File(cssPath);
            if (file.exists()) {
                file.delete();
            }

            String css = HttpUtils.get(url);
            if (css != null && css.contains("-webkit-tap-highlight-color:transparent")) {
                css = css.replace("-webkit-tap-highlight-color:transparent", "-webkit-tap-highlight-color:rgb(0,0,255,0.1)");
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
                bw.write(css);
                bw.close();

                return new FileInputStream(cssPath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void setFile(String filename, String info) {
        try {
            File file = new File("/sdcard/.shuiyes/" + filename);
            if (file.exists()) {
                file.delete();
            } else {
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
            }

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            bw.write(info);
            bw.close();
        } catch (Exception e) {
        }
    }

    /**
     * MD5加密路径
     */
    public static String md5(String paramString) {
        String returnStr;
        try {
            MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
            localMessageDigest.update(paramString.getBytes());
            returnStr = byteToHexString(localMessageDigest.digest());
            return returnStr;
        } catch (Exception e) {
            return paramString;
        }
    }

    /*
     * 将指定byte数组转换成16进制字符串
     */
    public static String byteToHexString(byte[] b) {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            hexString.append(hex.toUpperCase());
        }
        return hexString.toString();
    }

    public static String getMemoryInfo(Context ctx) {
        ActivityManager activityManager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memInfo);
        return "共 " + Formatter.formatFileSize(ctx, memInfo.totalMem) + "，" + Formatter.formatFileSize(ctx, memInfo.availMem) + (memInfo.lowMemory ? " 可用，低内存状态" : " 可用");
    }

    public static String getStorageInfo(Context ctx) {
        long[] mems = MemoryUtil.getInternalMemorySize();
        return "共 " + Formatter.formatFileSize(ctx, mems[1]) + "，" + Formatter.formatFileSize(ctx, mems[0]) + " 可用";
    }

    /**
     * Double类型保留指定位数的小数，返回double类型（四舍五入）
     * newScale 为指定的位数
     */
    private static double formatDouble(double d, int newScale) {
        BigDecimal bd = new BigDecimal(d);
        return bd.setScale(newScale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static String getDisplayMetrics(Activity context) {
        Display display = context.getWindowManager().getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        display.getMetrics(dm);
        Point size = new Point();
        display.getRealSize(size);

        int realWidth = size.x;
        int realHeight = size.y;
        float xdpi = dm.xdpi;
        float ydpi = dm.ydpi;
        double inch = formatDouble(Math.sqrt((realWidth / xdpi) * (realWidth / xdpi) + (realHeight / ydpi) * (realHeight / ydpi)), 1);

        return dm.widthPixels + " * " + dm.heightPixels + " px / " + dm.densityDpi + " dp / " + inch + " 英寸";
    }

    public static String getCpuInc() {
        String cpu = Build.HARDWARE;
        if ("freescale".equals(cpu)) {
            cpu = "Freescale Semiconductor Inc.";
        } else if ("qcom".equals(cpu)) {
            cpu = "Qualcomm Technologies Inc.";
        }
        return cpu;
    }

    public static String getAndroidInc() {
        Field[] fields = Build.VERSION_CODES.class.getDeclaredFields();
        SparseArray<String> fieldArr = new SparseArray<String>();
        for (int i = 0; i < fields.length; i++) {
            try {
                fieldArr.put(fields[i].getInt(null), fields[i].getName());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        int sdk = Build.VERSION.SDK_INT;
        switch (sdk) {
            case 26:

                break;
        }
        String cpu = "Android " + Build.VERSION.RELEASE + " / " + fieldArr.get(sdk) + " / API " + sdk;
        return cpu;
    }

    public static String getLinuxCoreVer() {
        Process process = null;
        String kernelVersion = "unkown";
        try {
            process = Runtime.getRuntime().exec("cat /proc/version");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // get the output line
        InputStream outs = process.getInputStream();
        InputStreamReader isrout = new InputStreamReader(outs);
        BufferedReader brout = new BufferedReader(isrout, 8 * 1024);

        String result = "";
        String line;
        // get the whole standard output string
        try {
            while ((line = brout.readLine()) != null) {
                result += line;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            if (result != "") {
                String Keyword = "version ";
                int index = result.indexOf(Keyword);
                line = result.substring(index + Keyword.length());
                index = line.indexOf(" ");
                if (index != -1) {
                    kernelVersion = line.substring(0, index);
                } else {
                    kernelVersion = result;
                }
            }
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return kernelVersion;
    }

    public static String elapsedRealtime() {
        long time = SystemClock.elapsedRealtime() / 1000;
        long s = time % 60;
        long m = time / 60;

        if (m < 60) {
            return m + " 分钟 " + s + " 秒";
        } else {
            long h = m / 60;
            m = m % 60;
            return h + " 小时 " + m + " 分钟 " + s + " 秒";
        }
    }

}
