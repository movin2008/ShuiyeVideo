package com.shuiyes.video.youku;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

import com.shuiyes.video.util.HttpUtils;
import com.shuiyes.video.widget.Tips;

public class YoukuUtils {

    public static void playUrl(Context context, String url, String title) {
        if (url.contains("youku.com")) {
            context.startActivity(new Intent(context, YoukuActivity.class).putExtra("url", url).putExtra("title", title));
        } else {
            Tips.show(context, "暂不支持播放 " + url + "\n请等待完善...", 0);
        }
    }

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
//    public static String CKEY = "109#RqUa7mdFapsp9apEpCqGCVSRznvERMZcCqX0Iln5dHyu91719bAkcBShwJzGaAGer8cDwCIW77A+zGpFD6xdqyLg5gdglcDpgCCazWDjHpJ5KpS91YOrUXRrFNcRlB+d7T227SjyR5aeGacBf4CmSHUG6iLHVpNyb5+NoPM3bmLNS0YVHYCqXNZMXMOgMilyGirsYGqFTwSCK7jKZhGThu+wgO7mQI7fE7e4u61KKvydc+9OOsJDODaVdSa7GC8LDEm+7Wb17YgCFsPpmyT+ypTw1rq8m8VjWit0t+mX2xG6l3BYZl+n1yNiGWunFXKOHMxI869ZKgEN23XBBKuzgHJSMQqDnTo5glSrO+CWlxFR/B6oWS5Tyvyl6iW7ymMYag+jkA8Atk1V+HtAlSurnji0B83ifeodyJH9QpxRY3aWGkUf7Ycpoz4rJ71YvoPw/oqt+jpDK7CZT8C7YifAzogzsoegZtJIrXKmuAks4pJ36QOSKsNqnGEw61zvUt9kD1nrY0PXLvqlKYGYY7F7b87pbQvdXO2Yh7l4";
//    public static String CKEY = "110#KjBkAUkfkHf20K59gUcUMuy2kMZXmTkQTyHOFUK2hQ7/8xbhlUwzcMdyjwucSltV8MKGhKc2hnpfJ9BYEUKghlFt1gRqzgjx81K1k7umbToEbcrhy+aWcuGWx0PNYDvZo3J3cdUijTkiLMgnP25is9kk4EKQs3gKb444GOsJaAKeVB2LvWq/eLr4gyaeDAkiskkkG9kiCTcOwyUUsLvwsTRU9zgSgqhwzJxkKOmw4Kf+TTzs2wHakT6UlDqlNcrptIJqynGMX4d1eb/e/TUDuGIYRVs8M9TeMCcxUoujInc2gyGsrgA34aIDAlZNN4gCw/BJRZ6+3TWGXXbixs2T5KkcAJxCszJDMzb7R5Ovt+4TgDNLYealzP+PF9FnhwvcyyqxqxYGdSRpmTgnwUaLkq70c63418E7cwfYH/FSJNEn3OXZexSbWoLVXALXHcy3SSEXePQ2pzv7CnM6WohrQK/MTs0pWkBh5zxIYedFDqNqY3AFLAB0OPC5pu/Np6EgZ7dh7NVJVTLJ/mehtF5gswA7bRVuwKve87zI/+lUJ2jZBZyFqdUr4b5DaAxi";

    public static String getVideoUrl(String vid, String cna) {
        String url = "https://ups.youku.com/ups/get.json?vid=" + vid;
        url += "&ccode=" + CCODE;
        url += "&client_ip=192.168.1.1";
        url += "&utid=" + URLEncoder.encode(cna);
        url += "&client_ts=" + System.currentTimeMillis() / 1000;
        url += "&ckey=" + URLEncoder.encode(CKEY);
//        url += "&version=0.5.68";
        return url;
    }

    public static String fetchCna() throws Exception {

        HttpURLConnection conn = (HttpURLConnection) new URL("http://log.mmstat.com/eg.js").openConnection();
        HttpUtils.setURLConnection(conn);
//        conn.setRequestProperty("Referer", "https://v.youku.com/v_show/id_XMzU5OTkwMzM4OA.html");
        conn.connect();

        try {
            String ret = conn.getHeaderField("ETag");
            if(conn.getHeaderField("ETag") != null){
               return ret;
            }

            Map<String, List<String>> headers = conn.getHeaderFields();
            Set<String> keys = headers.keySet();
            Iterator<String> iterator = keys.iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
//            Log.e("HAHA", key+"="+headers.get(key).get(0));

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
        }catch (ArrayIndexOutOfBoundsException e){
            e.printStackTrace();
        }finally {
//            conn.getInputStream().close();
            conn.disconnect();
        }
        return null;
    }


    public static String fetchVideo(String vid, String cna) throws Exception {
        String url = YoukuUtils.getVideoUrl(vid, cna);
        Log.e("HAHA", "url=" + url);

        HttpsURLConnection conn = (HttpsURLConnection) new URL(url).openConnection();
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

        conn.setRequestMethod("GET");
        HttpUtils.setURLConnection(conn);
        conn.connect();

        if (conn.getResponseCode() == 200) {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String read = null;
            StringBuffer ret = new StringBuffer();
            while ((read = in.readLine()) != null) {
                ret.append(read);
//                Log.e("HAHA", read);
            }
            in.close();
            return ret.toString();
        } else {
            Log.i("https", "fetchVideo error");
            Map<String, List<String>> headers = conn.getHeaderFields();
            Set<String> keys = headers.keySet();
            Iterator<String> iterator = keys.iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                Log.e("HAHA", "key=" + key);

                List<String> values = headers.get(key);
                for (String value : values) {
                    Log.e("HAHA", "value=" + value);
                }
            }
        }
        return null;
    }


    public static String search(String keyword) throws Exception {
        Log.e("HAHA", "keyword=" + keyword);

        HttpURLConnection conn = (HttpURLConnection) new URL("http://so.youku.com/search_video/q_" + keyword).openConnection();
        HttpUtils.setURLConnection(conn);
        conn.setRequestMethod("GET");
        conn.connect();

        if (conn.getResponseCode() == 200) {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String read = null;
            StringBuffer ret = new StringBuffer();
            while ((read = in.readLine()) != null) {
                ret.append(read);
            }
            in.close();
            return ret.toString();
        } else {
            Log.i("https", "search error");
            Map<String, List<String>> headers = conn.getHeaderFields();
            Set<String> keys = headers.keySet();
            Iterator<String> iterator = keys.iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                Log.e("HAHA", "key=" + key);

                List<String> values = headers.get(key);
                for (String value : values) {
                    Log.e("HAHA", "value=" + value);
                }
            }
        }
        return null;
    }


}
