package com.shuiyes.video.ui.youku;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.shuiyes.video.util.HttpUtils;
import com.shuiyes.video.util.Utils;

public class YoukuUtils {

    private final static String TAG = "YoukuUtils";

    /**
     # Found in http://g.alicdn.com/player/beta-ykplayer/0.6.2/youku-player.min.js
     # grep -oE '"[0-9a-zA-Z+/=]{256}"' youku-player.min.js
     */
    public static String CCODE = "0511";//0511 0517 0521 0590 0519
    public static String VERSION = "0.5.85";

    public static void updateCCodeIfNeed(Context context){
        YoukuUtils.CCODE = PreferenceManager.getDefaultSharedPreferences(context).getString("CCODE", YoukuUtils.CCODE);
    }

    /**
     * Found in http://g.alicdn.com/player/beta-ykplayer/0.6.2/youku-player.min.js
     * grep -oE '"[0-9a-zA-Z+/=]{256}"' youku-player.min.js
     */
    public static String CKEY = "DIl58SLFxFNndSV1GFNnMQVYkx1PP5tKe1siZu/86PR1u/Wh1Ptd+WOZsHHWxysSfAOhNJpdVWsdVJNsfJ8Sxd8WKVvNfAS8aS8fAOzYARzPyPc3JvtnPHjTdKfESTdnuTW6ZPvk2pNDh4uFzotgdMEFkzQ5wZVXl2Pf1/Y6hLK0OnCNxBj3+nb0v72gZ6b0td+WOZsHHWxysSo/0y9D2K42SaB8Y/+aD2K42SaB8Y/+ahU+WOZsHcrxysooUeND";

    // 网页端 CCODE=0502, 而 CKEY 是动态的，加密规则未知
    // https://acs.youku.com/h5/mtop.youku.play.ups.appinfo.get
    // public static String CKEY = "112#Fi74GW4WoHA+4BdXFWqaTAEPoz8pDk+eqMX154DIp+sAijGgYtWDfdP7/zRVNHnGkX9OS+WsmfbwBBYqwh6Jr35o4622Xqxdk/cA+AEpe8//4c7ATAlwUVkLtKZIIqxD97ZEV/+hvRKPMl1VpYetEgVsaKcnu76Fn71mJ0hRHsjwy3iN8ClrJHHISXTTF8IlzuVFGBgJzGTxkbCRXJiZwbPzNFhEXL8vISKOHlOV+bqBCGuF7624kdxle8odQAELza4rGkRb0W5Xz4DHfXiyYgBqaP4aksTB2VJ0yxkqIjfxt7tSLWEdwzaczAsz1eXn1fwSK/kL3+Z9pAGSJL6m76+CV6Y9Q4cf2+HxPKUGJfQWW9Gz/cOCCnZqpOMgMKZnuA3eUnJ0Eg6XseAwGOWjjDeXVoGu2cEI1IDOUpcYOnj+E37gtTF8PAMW9eh7dZZdDlN7EWi2Mj+9CTkCI6gkRB67WSk3PwkwNKbE13+2zsMMiYyde1YYW5scylK24y3v/NYMYxzuBvPgirTj4VywXYNklQrTH4C+LHB8B4DiGXT/bT8rSGIM2NJBvwk+LAN=";

    public static String getVideoUrl(String vid, String cna) {
        String url = "https://ups.youku.com/ups/get.json?vid=" + vid;
        url += "&ccode=" + CCODE;
        url += "&version=" + VERSION;
        url += "&client_ip=192.168.1.1";
        url += "&utid=" + URLEncoder.encode(cna);
        url += "&client_ts=" + Utils.timestamp();
        url += "&ckey=" + URLEncoder.encode(CKEY);

        return url;
    }

    public static String testCCode(String ccode, String cna) {
        // 恋慕
        String url = "https://ups.youku.com/ups/get.json?vid=XOTMzOTMxMjI0";
        url += "&ccode=" + ccode;
        url += "&version=" + VERSION;
        url += "&client_ip=192.168.1.1";
        url += "&utid=" + URLEncoder.encode(cna);
        url += "&client_ts=" + Utils.timestamp();
        url += "&ckey=" + URLEncoder.encode(CKEY);

        return HttpUtils.get(url);
    }

    public static String fetchVideo(String vid, String cna) {
        return HttpUtils.get(YoukuUtils.getVideoUrl(vid, cna));
    }

    public static String fetchCna() {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL("http://log.mmstat.com/eg.js").openConnection();
            HttpUtils.setURLConnection(conn, null);
            conn.connect();

            String ret = conn.getHeaderField("ETag");
            if(ret != null){
                ret = ret.substring(1, ret.length() - 1);
            }else{
                Map<String, List<String>> headers = conn.getHeaderFields();
                Set<String> keys = headers.keySet();
                Iterator<String> iterator = keys.iterator();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    Log.e(TAG, key+"="+headers.get(key).get(0));

                    if ("Set-Cookie".equals(key)) {
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
            }
            return ret;
        }catch (ArrayIndexOutOfBoundsException e){
            e.printStackTrace();
            try {
                Thread.sleep(999);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            return YoukuUtils.fetchCna();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(conn != null){
                conn.disconnect();
            }
        }
        return null;
    }

    public static String CToken;
    public static String fetchCToken(){
        return HttpUtils.get("https://youku.com/", true);
    }

    public static String search(String keyword, String cookie) throws Exception {
        return HttpUtils.get("https://so.youku.com/search_video/q_" + URLEncoder.encode(keyword,"utf-8"), "Cookie: "+cookie, false);
    }

    public static String listAlbums(String vid) throws Exception {
        return HttpUtils.get("https://v.youku.com/page/playlist?videoEncodeId="+URLEncoder.encode(vid,"utf-8")+"&page=1&videoCategoryId=96&componentid=38011&isSimple=false");
    }

    public static String getPlayUrlByVid(String vid){
        return "https://v.youku.com/v_show/id_"+vid+".html";
    }

    public static String getPlayVid(String url) {
        String vid = "unkown";
        String key = "show/id_";
        int index = url.indexOf(key);
        if (url.indexOf(".html") != -1) {
            vid = url.substring(index + key.length(), url.indexOf(".html"));
        } else {
            vid = url.substring(index + key.length());
        }
        Log.e(TAG, "getPlayVid(" + url + ")=" + vid);
        return vid;
    }

}
