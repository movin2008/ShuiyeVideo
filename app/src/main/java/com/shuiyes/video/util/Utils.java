package com.shuiyes.video.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;

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

    public static Bitmap scaleImage(Bitmap b) {
        int width = b.getWidth();
        int height = b.getHeight();
        float scaleWidth = 0;
        float scaleHeight = 0;
        if (width > height) {
            if (width <= 400 && height <= 300) {
                // 小图片扩放到 320*240
                scaleWidth = 320;
                scaleHeight = 240;
            }else{
                // 大图片缩放到 400x300
                scaleWidth = 400;
                scaleHeight = 300;
            }
        } else {
            if (width <= 300 && height <= 400) {
                // 小图片扩放到 240*320
                scaleWidth = 240;
                scaleHeight = 320;
            }else{
                // 大图片缩放到 300x400
                scaleWidth = 300;
                scaleHeight = 400;
            }
        }

        float aspectW = scaleWidth / ((float) width);
        float aspectH = scaleHeight / ((float) height);
        float aspect = aspectW > aspectH ? aspectH : aspectW;

        Matrix matrix = new Matrix();
        matrix.postScale(aspect, aspect);
        return Bitmap.createBitmap(b, 0, 0, width, height, matrix, true);
    }

}
