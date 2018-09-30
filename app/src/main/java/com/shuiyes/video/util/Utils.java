package com.shuiyes.video.util;

import android.content.Context;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.security.MessageDigest;

public class Utils {

    public static InputStream isTransparentHighlightCss(Context context, String url) {
        try {
            String cssPath = context.getCacheDir().getAbsolutePath()+"/tmp.css";
            File file = new File(cssPath);
            if (file.exists()) {
                file.delete();
            }

            String css = HttpUtils.open(url);
            if(css != null && css.contains("-webkit-tap-highlight-color:transparent")){
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
            File file = new File("/sdcard/.shuiyes/"+filename);
            if (file.exists()) {
                file.delete();
            }else{
                if (!file.getParentFile().exists()){
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

}
