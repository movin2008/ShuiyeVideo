package com.shuiyes.video.ui.vip;

import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.shuiyes.video.bean.PlayVideo;
import com.shuiyes.video.util.HttpUtils;
import com.shuiyes.video.util.Utils;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

// http://chujian.xiaoyule-app.cn/api.php?url=https%3A%2F%2Fm.iqiyi.com%2Fv_19ruzj8gv0.html&cb=jQuery
public class VipUtils {

    private final static String TAG = "VipUtils";

//    private static final String API_YLYBZ_DATA = "https://www.administratorm.com/";
    // 相比上面的api 这个会根据名字查找其他网站资源
    private static final String API_YLYBZ_DATA = "https://www.administratorm.com/WMXZ.WANG/";
    private static final String REFER_YLYBZ_DATA = "https://www.administratorm.com/index.php?url=";
    private static final String URL_YLYBZ_DATA = "https://www.administratorv.com/iqiyi/index.php?url=";

    private static final String API_YLYBZ_8090 = "https://8090.ylybz.cn/jiexi2019/api.php";
    private static final String REFER_YLYBZ_8090 = "https://8090.ylybz.cn/jiexi2019/?url=";
    private static final String URL_YLYBZ_8090 = "https://www.8090g.cn/?url=";

    private static final String API_YLYBZ_WOCAO = "https://wocao.ylybz.cn/api.php";
    private static final String REFER_YLYBZ_WOCAO = "https://wocao.ylybz.cn/vip.php?url=";
    private static final String URL_YLYBZ_WOCAO = "https://www.wocao.xyz/?url=";

    private static final String API_YLYBZ_97KYS = "https://vip.97kys.com/vip/api.php";
    private static final String REFER_YLYBZ_97KYS = "https://vip.97kys.com/vip/?url=";
    private static final String URL_YLYBZ_97KYS = "https://vip.97kys.com/vip/?url=";

    public static List<PlayVideo> getPlaySourceList() {
        List<PlayVideo> sourceList = new ArrayList<PlayVideo>();
        sourceList.add(new PlayVideo("无名小站 VIP视频解析", "administratorw.com"));
        sourceList.add(new PlayVideo("8090g VIP视频解析", "8090g.cn"));
        sourceList.add(new PlayVideo("WoCao VIP视频解析", "wocao.xyz"));
        sourceList.add(new PlayVideo("97kys VIP视频解析", "97kys.com"));
        sourceList.add(new PlayVideo("618G VIP视频解析", "607p.com"));
        return sourceList;
    }

    public static String getVipUrl(String url, String type) throws Exception {
        Log.e(TAG, "JX "+ url);
        // 免嗅探
        if ("607p.com".equals(type)) {
            //https://607p.com/?url=https%3A%2F%2Fm.iqiyi.com%2Fv_19ruzj8gv0.html
            String html = HttpUtils.get("https://607p.com/?url=" + URLEncoder.encode(url));

            if (TextUtils.isEmpty(html)) {
                return "{\"msg\":\"Http response is null.\"}";
            }

            if (html.startsWith("Exception:")) {
                return "{\"msg\":\"" + html + "\"}";
            }

            String key = "src=\"/m3u8.php?url=";
            if (html.contains(key)) {
                html = html.substring(html.indexOf(key) + key.length());
                html = html.substring(0, html.indexOf("\""));
                if (TextUtils.isEmpty(html)) {
                    return "{\"msg\":\"None url.\"}";
                } else {
                    return "{\"url\":\"" + html + "\"}";
                }
            } else {
                return "{\"msg\":\"No url.\"}";
            }
        } else {
            String api, referer, router;
            switch (type) {
                case "administratorw.com":
//                    String html = HttpUtils.get("https://www.administratorm.com/index.php?url=https://m.iqiyi.com/v_19ruzj8gv0.html", "referer: https://www.administratorv.com/iqiyi/index.php?url=https://m.iqiyi.com/v_19ruzj8gv0.html", false);
//                    String key = "$.post(\"";
//                    if (!TextUtils.isEmpty(html) && html.contains(key)) {
//                        html = html.substring(html.indexOf(key) + key.length());
//                        // .php
//                        API = html.substring(0, html.indexOf("\""));
//                        API = API_YLYBZ_DATA + API;
//                    } else {
//                        throw new Exception("Api acquisition failed.");
//                    }

                    Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8:00"));
                    long day = calendar.get(Calendar.DAY_OF_YEAR) - 1;
                    long hour = calendar.get(Calendar.HOUR_OF_DAY);
                    long year = calendar.get(Calendar.YEAR);
                    // 6896515874 16896515882 16896515890
                    long tmp = year * 8364610 + day * 192 + hour * 8 - 86;
                    Log.e("HAHA", year + "/" + hour + "/" + day + " -> " + tmp);

                    api = API_YLYBZ_DATA + tmp + ".php";
                    referer = REFER_YLYBZ_DATA;
                    router = URL_YLYBZ_DATA;
                    break;
                case "8090g.cn":
                    api = API_YLYBZ_8090;
                    referer = REFER_YLYBZ_8090;
                    router = URL_YLYBZ_8090;
                    break;
                case "wocao.xyz":
                    api = API_YLYBZ_WOCAO;
                    referer = REFER_YLYBZ_WOCAO;
                    router = URL_YLYBZ_WOCAO;
                    break;
                case "97kys.com":
                    api = API_YLYBZ_97KYS;
                    referer = REFER_YLYBZ_97KYS;
                    router = URL_YLYBZ_97KYS;
                    break;
                default:
                    throw new Exception("Unkown vip router " + type);
            }

            String data = "url=" + URLEncoder.encode(url)
                    + "&referer=" + URLEncoder.encode(Base64.encodeToString((router + URLEncoder.encode(url)).getBytes(), Base64.DEFAULT))
                    + "&time=" + Utils.timestamp()
                    + "other=" + URLEncoder.encode(Base64.encodeToString((url).getBytes(), Base64.DEFAULT))
                    + "&ref=0&type=&&ios=0";
            String refer = "Referer: " + referer + URLEncoder.encode(url);
            return HttpUtils.post(api, data, refer);
        }
    }

    static List<PlayVideo> getSourceList() {
        List<PlayVideo> sourceList = new ArrayList<PlayVideo>();

        // https://www.administratorm.com/16896513490.php
        // https://data.ylybz.cn/video/qyplay.php?url=https%3A%2F%2Fdata.ylybz.cn%2Fdata%2Fiqiyi%2F6d9b571f4828b0d6941c22c046749a49.m3u8
        sourceList.add(new PlayVideo("administratorw.com(data.ylybz.cn)【无名小站】", "https://www.administratorw.com/video.php?url="));
        // https://8090.ylybz.cn/jiexi2019/api.php
        // https://ykm3u8.ylybz.cn/video/qyplay.php?url=https://ykm3u8.ylybz.cn/data/iqiyi/6d9b571f4828b0d6941c22c046749a49.m3u8
        sourceList.add(new PlayVideo("8090g.cn(ykm3u8.ylybz.cn)【8090g解析】", "https://www.8090g.cn/?url="));
        // https://wocao.ylybz.cn/api.php
        // https://ykm3u8.ylybz.cn/video/qyplay.php?url=https%3A%2F%2Fykm3u8.ylybz.cn%2Fdata%2Fiqiyi%2F6d9b571f4828b0d6941c22c046749a49.m3u8
        sourceList.add(new PlayVideo("wocao.xyz(ykm3u8.ylybz.cn)【WoCao视频】", "https://www.wocao.xyz/?url="));
        // https://vip.97kys.com/vip/api.php
        // https://ykm3u8.ylybz.cn/video/qyplay.php?url=https%3A%2F%2Fykm3u8.ylybz.cn%2Fdata%2Fiqiyi%2F6d9b571f4828b0d6941c22c046749a49.m3u8
        sourceList.add(new PlayVideo("97kys.com(ykm3u8.ylybz.cn)【97解析平台】", "https://vip.97kys.com/vip/?url="));
        //
        sourceList.add(new PlayVideo("607p.com【618G免费解析】", "https://607p.com/?url="));
        sourceList.add(new PlayVideo("mt2t.com【云播放】", "http://mt2t.com/lines?url="));
        sourceList.add(new PlayVideo("vipvideo.github.io【水也】(CNAME mt2t.com)", "http://vipvideo.github.io/lines?url="));
        sourceList.add(new PlayVideo("api.jaoyun.com【简傲云】", "https://api.jaoyun.com/?url="));
        sourceList.add(new PlayVideo("gagq.cn【内嵌 api.jaoyun.com】", "https://www.gagq.cn/?url="));
        sourceList.add(new PlayVideo("sp.6080jx.com【云解析】", "http://sp.6080jx.com/?url="));
        sourceList.add(new PlayVideo("jx.yaohuaxuan.com【免费解析客户端】", "https://jx.yaohuaxuan.com/1717/?url="));
        sourceList.add(new PlayVideo("jiexila.com【内嵌 jx.yaohuaxuan.com】", "https://jiexila.com/?url="));
        sourceList.add(new PlayVideo("jx.99yyw.com【内嵌 vvv.yaohuaxuan.com】", "https://jx.99yyw.com/99/?url="));
        sourceList.add(new PlayVideo("playm3u8.cn【Playm3u8无广告视频解析】", "https://www.playm3u8.cn/jiexi.php?url="));
        sourceList.add(new PlayVideo("playm3u8.ylybz.cn【内嵌 playm3u8.cn】", "https://playm3u8.ylybz.cn/playm3u8.php?url="));
        sourceList.add(new PlayVideo("jx.wsy666.site【内嵌 playm3u8.ylybz.cn】", "http://jx.wsy666.site/wsy/a.php?url="));
        sourceList.add(new PlayVideo("ys.8oc.cn【云梦解析】(SSL -201)", "https://ys.8oc.cn/jx/?url="));
        sourceList.add(new PlayVideo("qwerdf.5ifree.top【云智能视频解析】(免费暂停开启收费)", "https://qwerdf.5ifree.top/?vvv="));
        return sourceList;
    }

    public static String formateBytes(float bytes) {
        bytes /= 1024;
        if (bytes > 1024) {
            bytes /= 1024;
            if (bytes > 1024) {
                bytes /= 1024;
                return String.format("%.2f", bytes) + "G";
            } else {
                return String.format("%.2f", bytes) + "M";
            }
        } else {
            return String.format("%.2f", bytes) + "K";
        }
    }

}
