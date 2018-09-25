package com.shuiyes.video.letv;

import com.shuiyes.video.util.HttpUtils;
import com.shuiyes.video.util.SHA1;

public class LetvUtils {

    private final static String TAG = "LetvUtils";

    /**
     #@DEPRECATED
     def get_timestamp():
         tn = random.random()
         url = 'http://api.letv.com/time?tn={}'.format(tn)
         result = get_content(url)
         return json.loads(result)['stime']

     #@DEPRECATED
     def get_key(t):
         for s in range(0, 8):
         e = 1 & t
         t >>= 1
         e <<= 31
         t += e
         return t ^ 185025305

     def calcTimeKey(t):
         ror = lambda val, r_bits, : ((val & (2**32-1)) >> r_bits%32) |  (val << (32-(r_bits%32)) & (2**32-1))
         magic = 185025305
         return ror(t, magic % 17) ^ magic
         #return ror(ror(t,773625421%13)^773625421,773625421%17)
     */
    public static long calcTimeKey(){
        long magic = 185025305;
        long tm = System.currentTimeMillis()/1000;
        return ror(tm, magic % 17) ^ magic;
    }


    public static long ror(long val, long r_bits){
        long l = Integer.MAX_VALUE;
        l = l*2+1; //4294967295
        return ((val & l) >> r_bits%32) |  (val << (32-(r_bits%32)) & l);
    }

    public static String searchVideos(String keyword) throws Exception {
        return HttpUtils.open("http://so.le.com/s?wd=" + keyword);
//        return HttpUtils.open("http://so.le.com/s?wd="+keyword+"&from=pc&index=0&ref=click&click_area=search_button&query="+keyword+"&is_default_query=0&module=suggest_list&eid=undefined&experiment_id=undefined&is_trigger=undefined");
    }

    public static String searchUploadVideos(String keyword) throws Exception {
        return HttpUtils.open("http://search.lekan.letv.com/lekan/apisearch_json.so?wd="+keyword+"&from=pc&jf=1&hl=1&dt=1,2&ph=420001,420002&show=4&pn=1&ps=30");
    }

    public static String searchStarVideos(String leId) throws Exception {
        return HttpUtils.open("http://search.lekan.letv.com/lekan/apisearch_json.so?leIds="+leId+"&from=pc&jf=3&dt=album,video&pn=1&ps=21&ph=420001,420002&stype=1");
    }

    public static String splatid = "105";

    public static String getVideoInfoUrl(String vid) {
        String url = "http://player-pc.le.com/mms/out/video/playJson?id="+vid
                + "&platid=1"
                + "&splatid=" + splatid
                + "&format=1"
                + "&tkey=" + calcTimeKey()
                + "&domain=www.le.com&region=cn&source=1000&accesyx=1";
        return url;
    }

    public static String getVideoPlayUrl(String url, String vid) {
        // 1536840193220
        double tn = System.currentTimeMillis();
        tn = tn/10000000;
        tn = tn/1000000;
        String uuid =  SHA1.encode(url) + "_0";

        url = url.replace("tss=0", "tss=ios");
        url += "&m3v=1&termid=1&format=1&hwtype=un&ostype=MacOS10.12.4&p1=1&p2=10&p3=-&expect=3";
        url += "&tn=" + tn;
        url += "&vid=" + vid;
        url += "&uuid=" + uuid;
        url += "&sign=letv";
        return url;
    }

    public static String getVideoPlayUrlFromVid(String vid) {
        return "http://www.le.com/ptv/vplay/"+vid+".html";
    }

    public static String getAlbumUrlFromVid(String aid) {
        return "http://www.le.com/tv/"+aid+".html";
    }

}
