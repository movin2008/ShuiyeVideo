package com.shuiyes.video.ui.letv;

import android.util.Log;

import com.shuiyes.video.util.HttpUtils;
import com.shuiyes.video.util.SHA1;
import com.shuiyes.video.util.Utils;

import java.net.URLEncoder;

public class LetvUtils {

    private final static String TAG = "LetvUtils";

    /**
     * #@DEPRECATED
     * def get_timestamp():
     * tn = random.random()
     * url = 'http://api.letv.com/time?tn={}'.format(tn)
     * result = get_content(url)
     * return json.loads(result)['stime']
     * <p>
     * #@DEPRECATED
     * def get_key(t):
     * for s in range(0, 8):
     * e = 1 & t
     * t >>= 1
     * e <<= 31
     * t += e
     * return t ^ 185025305
     * <p>
     * def calcTimeKey(t):
     * ror = lambda val, r_bits, : ((val & (2**32-1)) >> r_bits%32) |  (val << (32-(r_bits%32)) & (2**32-1))
     * magic = 185025305
     * return ror(t, magic % 17) ^ magic
     * #return ror(ror(t,773625421%13)^773625421,773625421%17)
     */
    public static long calcTimeKey() {
        long magic = 185025305;
        long tm = Utils.timestamp();
        return ror(tm, magic % 17) ^ magic;
    }


    public static long ror(long val, long r_bits) {
        long l = Integer.MAX_VALUE;
        l = l * 2 + 1; //4294967295
        return ((val & l) >> r_bits % 32) | (val << (32 - (r_bits % 32)) & l);
    }

    public static String searchVideos(String keyword) throws Exception {
        return HttpUtils.open("http://so.le.com/s?wd=" + URLEncoder.encode(keyword, "utf-8"));
//        return HttpUtils.open("http://so.le.com/s?wd="+keyword+"&from=pc&index=0&ref=click&click_area=search_button&query="+keyword+"&is_default_query=0&module=suggest_list&eid=undefined&experiment_id=undefined&is_trigger=undefined");
    }

    public static String searchUploadVideos(String keyword) throws Exception {
        return HttpUtils.open("http://search.lekan.letv.com/lekan/apisearch_json.so?wd=" + URLEncoder.encode(keyword, "utf-8") + "&from=pc&jf=1&hl=1&dt=1,2&ph=420001,420002&show=4&pn=1&ps=30");
    }

    public static String searchStarVideos(String leId) throws Exception {
        return HttpUtils.open("http://search.lekan.letv.com/lekan/apisearch_json.so?leIds=" + leId + "&from=pc&jf=3&dt=album,video&pn=1&ps=21&ph=420001,420002&stype=1");
    }

    public static String splatid = "105";

    public static String getVideoInfoUrl(String vid) {
        String url = "http://player-pc.le.com/mms/out/video/playJson?id=" + vid
                + "&platid=1"
                + "&splatid=" + splatid
                + "&format=1"
                + "&tkey=" + calcTimeKey()
                + "&domain=www.le.com&region=cn&source=1000&accesyx=1";
        return url;
    }

    public static String getVideoPlayUrl(String url, String vid) {

        String uuid = SHA1.encode(url) + "_0";
        url = url.replace("tss=0", "tss=ios");

//        double tn = System.currentTimeMillis();
//        tn = tn / 10000000;
//        // TODO if failure, timestamp maybe changed
//        tn = tn / 10000000;
//        url += "&termid=1&m3v=1&format=1&expect=3&hwtype=un&ostype=Windows10&p1=1&p2=10&p3=-";
//        url += "&tn=" + tn;
//        url += "&vid=" + vid;
//        url += "&uuid=" + uuid;
//        url += "&sign=letv";

        url += "&format=1&jsonp=jsonp&expect=3&p1=0&p2=06&termid=2&ostype=macos&hwtype=un&appid=800&ajax=1&m3v=1";
        url += "&vid=" + vid;
        url += "&uuid=" + uuid;
        return url;
    }

    public static String getVideoPlayUrlFromVid(String vid) {
        return "http://www.le.com/ptv/vplay/" + vid + ".html";
    }

    public static String getAlbumUrlFromVid(String aid) {
        return "http://www.le.com/tv/" + aid + ".html";
    }

    public static String fetchAlbum(String vid) {
        String url = "http://d-api-m.le.com/card/dynamic?platform=pc&isvip=1&type=episode&cid=2&page=1&pagesize=100";
        url += "&vid=" + vid;
        return HttpUtils.open(url);
    }

    public static String getPlayVid(String url) {
        String vid = url;
        String key = "/vplay/";
        int index = url.indexOf(key);
        if (index != -1) {
            if (url.indexOf(".html") != -1) {
                vid = url.substring(index + key.length(), url.indexOf(".html"));
                String[] vids = vid.split("_");
                vid = vids[vids.length - 1];
            } else {
                vid = url.substring(index + key.length());
            }
        }
        Log.e(TAG, "getPlayVid(" + url + ")=" + vid);
        return vid;
    }


}
