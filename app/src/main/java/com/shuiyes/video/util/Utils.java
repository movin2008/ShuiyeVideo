package com.shuiyes.video.util;

import android.graphics.Bitmap;
import android.graphics.Matrix;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

public class Utils {

    public static void setFile(String path, String info) {
        try {
            File file = new File(path);
            if (file.exists()) {
                file.delete();
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
