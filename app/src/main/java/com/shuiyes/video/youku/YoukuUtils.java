package com.shuiyes.video.youku;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

import com.shuiyes.video.util.HttpUtils;

public class YoukuUtils {

    private final static String TAG = "YoukuUtils";

    /**
     # Found in http://g.alicdn.com/player/ykplayer/0.5.64/youku-player.min.js
     # grep -oE '"[0-9a-zA-Z+/=]{256}"' youku-player.min.js
     */
    public static String CCODE = "0511";

    /**
     * Found in http://g.alicdn.com/player/ykplayer/0.5.28/youku-player.min.js
     * grep -oE '"[0-9a-zA-Z+/=]{256}"' youku-player.min.js
     */
    public static String CKEY = "DIl58SLFxFNndSV1GFNnMQVYkx1PP5tKe1siZu/86PR1u/Wh1Ptd+WOZsHHWxysSfAOhNJpdVWsdVJNsfJ8Sxd8WKVvNfAS8aS8fAOzYARzPyPc3JvtnPHjTdKfESTdnuTW6ZPvk2pNDh4uFzotgdMEFkzQ5wZVXl2Pf1/Y6hLK0OnCNxBj3+nb0v72gZ6b0td+WOZsHHWxysSo/0y9D2K42SaB8Y/+aD2K42SaB8Y/+ahU+WOZsHcrxysooUeND";
//    public static String CKEY = "112#Fi74GW4WoHA+4BdXFWqaTAEPoz8pDk+eqMX154DIp+sAijGgYtWDfdP7/zRVNHnGkX9OS+WsmfbwBBYqwh6Jr35o4622Xqxdk/cA+AEpe8//4c7ATAlwUVkLtKZIIqxD97ZEV/+hvRKPMl1VpYetEgVsaKcnu76Fn71mJ0hRHsjwy3iN8ClrJHHISXTTF8IlzuVFGBgJzGTxkbCRXJiZwbPzNFhEXL8vISKOHlOV+bqBCGuF7624kdxle8odQAELza4rGkRb0W5Xz4DHfXiyYgBqaP4aksTB2VJ0yxkqIjfxt7tSLWEdwzaczAsz1eXn1fwSK/kL3+Z9pAGSJL6m76+CV6Y9Q4cf2+HxPKUGJfQWW9Gz/cOCCnZqpOMgMKZnuA3eUnJ0Eg6XseAwGOWjjDeXVoGu2cEI1IDOUpcYOnj+E37gtTF8PAMW9eh7dZZdDlN7EWi2Mj+9CTkCI6gkRB67WSk3PwkwNKbE13+2zsMMiYyde1YYW5scylK24y3v/NYMYxzuBvPgirTj4VywXYNklQrTH4C+LHB8B4DiGXT/bT8rSGIM2NJBvwk+LAN=";

    public static String getVideoUrl(String vid, String cna) {
        String url = "https://ups.youku.com/ups/get.json?vid=" + vid;
        url += "&ccode=" + CCODE;
        url += "&client_ip=192.168.1.1";
        url += "&utid=" + URLEncoder.encode(cna);
        url += "&client_ts=" + System.currentTimeMillis() / 1000;
        url += "&ckey=" + URLEncoder.encode(CKEY);
//        url += "&version=0.5.79";
        return url;
    }

    public static String fetchCna() {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL("http://log.mmstat.com/eg.js").openConnection();
            HttpUtils.setURLConnection(conn);
            conn.connect();

            String ret = conn.getHeaderField("ETag");
            if(conn.getHeaderField("ETag") != null){
               return ret.substring(1, ret.length() - 1);
            }

            Map<String, List<String>> headers = conn.getHeaderFields();
            Set<String> keys = headers.keySet();
            Iterator<String> iterator = keys.iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                Log.e(TAG, key+"="+headers.get(key).get(0));

                if ("ETag".equals(key)) {
                    String cna = headers.get(key).get(0);
                    ret = cna.substring(1, cna.length() - 1);
                    break;
                } else if ("Set-Cookie".equals(key)) {
                    List<String> l = headers.get(key);
                    if (l.size() > 0) {
                        String v = l.get(0);
                        if (v.contains("cna=")) {
                            String cna = v.split(";")[0];
                            if ("cna".equals(cna.split("=")[0])) {
                                ret = cna.split("=")[1];
                                break;
                            }
                        }
                    }
                }
            }
            return ret;
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(conn != null){
                conn.disconnect();
            }
        }
        return null;
    }


    public static String fetchVideo(String vid, String cna, String videoUrl) {
        String url = YoukuUtils.getVideoUrl(vid, cna);
        Log.e(TAG, "url=" + url);

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

        HttpsURLConnection conn = null;
        try {
            conn = (HttpsURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");
            HttpUtils.setURLConnection(conn, videoUrl);
            conn.connect();

            int code = conn.getResponseCode();
            if (code == 200) {
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), "UTF-8"));
                String read = null;
                StringBuffer ret = new StringBuffer();
                while ((read = in.readLine()) != null) {
                    ret.append(read);
//                Log.e(TAG, read);
                }
                in.close();
                return ret.toString();
            } else {
                Log.e(TAG, "fetchVideo("+url+") ResponseCode="+code);
                HttpUtils.printHeaders(conn);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(conn != null){
                conn.disconnect();
            }
        }

        return null;
    }

    public static String search(String keyword) throws Exception {
        return HttpUtils.open("http://so.youku.com/search_video/q_" + keyword);
    }

}
