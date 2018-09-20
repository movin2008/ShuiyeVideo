package com.shuiyes.video.util;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.util.HashMap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.shuiyes.video.youku.SoYoukuActivity;

public class ImageLoader {
    protected static final String TAG = "ImageLoader";
    // 定义一个软引用（缓存机制）
    private HashMap<String, SoftReference<Bitmap>> imageCaches;

    public ImageLoader() {
        imageCaches = new HashMap<String, SoftReference<Bitmap>>();
    }

    public Bitmap getBitmap(final String imageUrl, final Handler handler) {
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
                    // 将bitmap放入缓存
                    imageCaches.put(imageUrl, new SoftReference<Bitmap>(bitmap));
                    // //保存图片至SD卡，文件名为图片名称加密串
                    // BitmapUtil.saveBitmap(bitmap, md5(imageUrl));
                    // 发送携带了bitmap的消息，通知handler更新UI
                    Message message = handler.obtainMessage(SoYoukuActivity.MSG_SET_IMAGE);
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
        InputStream is = null;
        try {
            URL url = new URL(urlStr);
            connection = (HttpURLConnection) url.openConnection();
             connection.setConnectTimeout(6666);
             connection.setReadTimeout(6666);
            connection.setRequestMethod("GET");
            connection.connect();
            is = connection.getInputStream();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return is;
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
