package com.shuiyes.video.mdd;

import java.security.MessageDigest;

public class Sign {
    private static String[] charArray = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};

    public static String sign(byte[] byteArray) {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : byteArray) {
            stringBuilder.append(compute(b));
        }
        return stringBuilder.toString();
    }

    private static String compute(byte b) {
        int a = b;
        if (a < 0) {
            a += 256;
        }
        return charArray[a / 16] + charArray[a % 16];
    }

    public static String md5(String str) {
        try {
            MessageDigest instance = MessageDigest.getInstance("MD5");
            instance.update(str.getBytes("UTF-8"));
            return sign(instance.digest());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
