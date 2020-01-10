package com.shuiyes.video.util;

import android.util.Base64;

import com.shuiyes.video.R;
import com.shuiyes.video.ui.SVApplication;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.sql.Date;

public class CbchotUtil {

    // com\cbchot\android\b\as.class
    // com\cbchot\android\common\c\r.class
    // auth_key 入口
    public static String getAuthUrl(String url){
        int i = a(new Date(System.currentTimeMillis() + 3600L));
        String str = null;
        try {
            str = new URL(url).getFile();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Object localObject = z();
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append(str);
        localStringBuilder.append("-");
        localStringBuilder.append(i);
        localStringBuilder.append("-");
        localStringBuilder.append("0");
        localStringBuilder.append("-");
        localStringBuilder.append("0");
        localStringBuilder.append("-");
        localStringBuilder.append((String)localObject);

        str = j(localStringBuilder.toString());
        localObject = new StringBuilder();
        ((StringBuilder)localObject).append(url);
        ((StringBuilder)localObject).append("?auth_key=");
        ((StringBuilder)localObject).append(i);
        ((StringBuilder)localObject).append("-");
        ((StringBuilder)localObject).append("0");
        ((StringBuilder)localObject).append("-");
        ((StringBuilder)localObject).append("0");
        ((StringBuilder)localObject).append("-");
        ((StringBuilder)localObject).append(str);

        return localObject.toString();
    }

    public static int a(Date paramDate)
    {
        if (paramDate == null) {
            return 0;
        }
        return Integer.valueOf(String.valueOf(paramDate.getTime() / 1000L)).intValue();
    }

    public static String a(int paramInt)
    {
        return SVApplication.getAppContext().getString(paramInt);
    }

    public static String z()
    {
        String str = null;
        try {
            str = new String(Base64.decode(a(R.string.logo_no_update), 0), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String j(String paramString)
    {

        try {
            MessageDigest localObject = MessageDigest.getInstance("MD5");
            ((MessageDigest)localObject).reset();
            ((MessageDigest)localObject).update(paramString.getBytes("UTF-8"));

            byte[] paramBytes = ((MessageDigest)localObject).digest();
            Object buffer = new StringBuffer();
            int i = 0;
            while (i < paramBytes.length)
            {
                if (Integer.toHexString(paramBytes[i] & 0xFF).length() == 1) {
                    ((StringBuffer)buffer).append("0");
                }
                ((StringBuffer)buffer).append(Integer.toHexString(paramBytes[i] & 0xFF));
                i += 1;
            }
            return ((StringBuffer)buffer).substring(0, ((StringBuffer)buffer).length()).toString();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
