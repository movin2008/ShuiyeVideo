package com.shuiyes.video.util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

public class HttpUtils {

    private final static String TAG = "HttpUtils";

    public static String FormateUrl(String url) {
        if (url.startsWith("//")) {
            url = "http:" + url;
        }
        return url;
    }

    public static void setURLConnection(URLConnection conn) {
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(10000);
//        conn.setRequestProperty("Charset", "UTF-8");
        conn.setRequestProperty("Host", conn.getURL().getHost());
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36");
    }

    public static void setURLConnection(URLConnection conn, String Referer) {
        HttpUtils.setURLConnection(conn);
        conn.setRequestProperty("Referer",Referer);
    }

    public static void printHeaders(URLConnection conn){
        Map<String, List<String>> headers = conn.getHeaderFields();
        Set<String> keys = headers.keySet();
        Iterator<String> iterator = keys.iterator();

        StringBuffer buf = new StringBuffer();
        while (iterator.hasNext()) {
            String key = iterator.next();

            buf.setLength(0);
            List<String> values = headers.get(key);
            for (String value : values) {
                buf.append(value+",");
            }
            Log.e(TAG, key+"=" + buf.toString());
        }
    }

    public static String open(String url){
        return HttpUtils.open(url ,false);
    }

    public static String open(String url, boolean print){
        Log.e(TAG, "open "+url);

//        HttpURLConnection conn = (HttpURLConnection) new URL("http://www.shuiyes.com/test/header.php").openConnection();
////        conn.setRequestProperty("Cookie", "cna=" + cna);
//        conn.setRequestProperty("Upgrade-Insecure-Requests", "1");
//        conn.setRequestProperty("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
//        conn.setRequestProperty("accept-encoding", "gzip, deflate, br");
//        conn.setRequestProperty("accept-language", "zh-CN,zh;q=0.9");
//        conn.setRequestProperty("cache-control", "max-age=0");
//        conn.setRequestProperty(":authority", conn.getURL().getAuthority());
//        conn.setRequestProperty(":path", conn.getURL().getPath()+"?"+conn.getURL().getQuery());
//        conn.setRequestProperty(":scheme", "https");
//        conn.setRequestProperty(":method", "GET");


        if(url.startsWith("https://")){
            HttpsURLConnection conn = null;
            try {
                conn = (HttpsURLConnection) new URL(url).openConnection();
                HttpUtils.setURLConnection(conn);
                conn.setRequestMethod("GET");
                conn.connect();

                int code = conn.getResponseCode();
                if (code == 200) {
                    StringBuffer ret = new StringBuffer();
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                    String read = null;
                    while ((read = in.readLine()) != null) {
                        ret.append(read);
                    }
                    in.close();
                    return ret.toString();
                } else {
                    Log.e(TAG, "open("+url+") ResponseCode="+code);
                    printHeaders(conn);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if(conn != null){
                    conn.disconnect();
                }
            }
        }else{
            HttpURLConnection conn = null;
            try {
                conn = (HttpURLConnection) new URL(url).openConnection();
                HttpUtils.setURLConnection(conn);
                conn.setRequestMethod("GET");
                conn.connect();

                int code = conn.getResponseCode();
                if (code == 200) {
                    StringBuffer ret = new StringBuffer();
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                    String read = null;
                    while ((read = in.readLine()) != null) {
                        ret.append(read);
                    }
                    in.close();
                    return ret.toString();
                } else {
                    Log.e(TAG, "open("+url+") ResponseCode="+code);
                    printHeaders(conn);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if(conn != null){
                    conn.disconnect();
                }
            }
        }


        return null;
    }

}
