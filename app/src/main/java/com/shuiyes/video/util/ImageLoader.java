package com.shuiyes.video.util;

import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

public class ImageLoader {
    protected static final String TAG = "ImageLoader";
    // 定义一个软引用（缓存机制）
    private HashMap<String, SoftReference<Bitmap>> imageCaches;

    public ImageLoader() {
        imageCaches = new HashMap<String, SoftReference<Bitmap>>();
    }

    public Bitmap getBitmap(final String imageUrl, final Handler handler) {
        if(TextUtils.isEmpty(imageUrl)){
            return null;
        }

        // 判断缓存中是否存在
        if (imageCaches.containsKey(imageUrl)) {

            SoftReference<Bitmap> softReference = imageCaches.get(imageUrl);
            Bitmap bitmap = softReference.get();
            // 如果缓存中存在bitmap，则直接返回，不再开线程获取bitmap
            if (bitmap != null) {
//				Log.d(TAG, "cached bitmap =" + bitmap);
                return bitmap;
            } else {
                imageCaches.remove(imageUrl);
            }
        }

        // //判断本地缓存中是否存在bitmap
        // if(BitmapUtil.bitmapExists(imageUrl)){
        // Bitmap bitmap = BitmapUtil.getBitmapFromSDCard(md5(imageUrl));
        // Log.d(TAG, "bitmap=="+bitmap);
        // return bitmap;
        // }

        // 如果两个缓存中都没有bitmap，则开启线程下载获取bitmap

        ThreadPoolUtil.execute(new Runnable() {

            @Override
            public void run() {
                Bitmap bitmap = BitmapFactory.decodeStream(getInputStreamFromUrl(imageUrl));
                if (bitmap != null) {
                    bitmap = scaleImage(bitmap);
//                    Log.i(TAG, bitmap.getWidth()+"x"+bitmap.getHeight());
                    // 将bitmap放入缓存
                    imageCaches.put(imageUrl, new SoftReference<Bitmap>(bitmap));
                    // //保存图片至SD卡，文件名为图片名称加密串
                    // BitmapUtil.saveBitmap(bitmap, md5(imageUrl));
                    // 发送携带了bitmap的消息，通知handler更新UI
                    Message message = handler.obtainMessage(Constants.MSG_SET_IMAGE);
                    message.obj = bitmap;
                    Bundle bundle = new Bundle();
                    bundle.putString("imageUrl", imageUrl);
//					System.out.println("imageUrl ===========" + imageUrl);
                    message.setData(bundle);
                    handler.sendMessage(message);
                }
            }
        });
        return null;

    }

    /**
     * 网络连接获取到一个输入流
     */
    public InputStream getInputStreamFromUrl(String urlStr) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlStr);
            connection = (HttpURLConnection) url.openConnection();
             connection.setConnectTimeout(6666);
             connection.setReadTimeout(6666);
            connection.setRequestMethod("GET");
            connection.connect();
            if(connection.getResponseCode() == 200){
                return connection.getInputStream();
            }
        } catch (Exception e) {
        }
        return null;
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
