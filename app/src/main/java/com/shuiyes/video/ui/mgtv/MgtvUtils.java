package com.shuiyes.video.ui.mgtv;

import android.util.Base64;
import android.util.Log;

import com.shuiyes.video.util.HttpUtils;
import com.shuiyes.video.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class MgtvUtils {

    private final static String TAG = "MgtvUtils";

    private static final String DID = "151d9882-d680-4a30-86d8-bf174e74ec28";
//    private static final String DID = "698bc987-e1a0-461a-b9c4-34a5fb4d118e";
//    private static final String DID = "802c0ec0-1df9-4dd3-8ec4-5a0806cdda88";
    private static final String VER = "0.3.0301";
    private static final String PNO = "1030";


    /*
    to = "/player/video?",
    eo = /iPad/i.test(navigator.userAgent),
    io = function(t) {
        var e = eo ? "1121": "1030",
        i = eo ? "pad": "pch5",
        n = (t || 0).lobparam || {},
        r = n.did,
        a = n.suuid,
        o = n.cxid,
        s = ~~ ( + new Date / 1e3);
        return {
            did: r,
            suuid: a,
            cxid: o,
            type: i,
            pno: e,
            tk2: btoa("did=" + r + "|pno=" + e + "|ver=0.3.0301|clit=" + s).replace(/\+/g, "_").replace(/\//g, "~").replace(/=/g, "-").split("").reverse().join(""),
            timestamp: s
        }
    },
     */
    public static String tk2() {
        String tk2 = "did=" + DID + "|ver=" + VER + "|pno=" + PNO + "|clit=" + Utils.timestamp();
        try {
            tk2 = Base64.encodeToString(tk2.getBytes("utf-8"), Base64.DEFAULT);
        } catch (UnsupportedEncodingException e) {
        }
        tk2 = tk2.replaceAll("\n", "");
        tk2 = tk2.replaceAll("\\+", "_");
        tk2 = tk2.replaceAll("/", "~");
        tk2 = tk2.replaceAll("=", "-");
        tk2 = new StringBuffer(tk2).reverse().toString();

        Log.e(TAG, "tk2("+tk2.length()+")=" + tk2);

        return tk2;
    }

    private static final String API_VIDEO = "https://pcweb.api.mgtv.com/player/video?video_id=%s&tk2=%s";
    public static String getVideo(String vid) {
        return HttpUtils.get(String.format(API_VIDEO, vid, MgtvUtils.tk2()));
    }

    private static final String API_PM2 = "https://web.da.mgtv.com/pc/player?id=%s&v=%s&p=%s&au=%s&auver=v1&callback=cb";
    public static String getPm2(String vid, String cxid, String pm2) throws Exception {
        JSONObject obj = new JSONObject();
        obj.put("id", cxid);
        String id = URLEncoder.encode(obj.toString(), "utf-8");

        String v = URLEncoder.encode(MgtvUtils.getPlayerParmV(vid), "utf-8");
        String p = URLEncoder.encode(MgtvUtils.getPlayerParmP(vid, cxid, pm2), "utf-8");
        String au = URLEncoder.encode("78lVBaZjuOp6FjaaoQBdGEk7QAP5HMK/VieWDnKywFQCPfyQJPVEdbW1Xn+MqQ5hmF7cSw==", "utf-8");

        return HttpUtils.get(String.format(API_PM2, id, v, p, au));
    }

    private static final String API_SOURCE = "https://pstream.api.mgtv.com/player/getSource?video_id=%s&tk2=%s&pm2=%s";
    public static String getSource(String vid, String pm2) {
        return HttpUtils.get(String.format(API_SOURCE, vid, tk2(), pm2));
    }

    public static String getPlayerParmV(String vid) throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("hid",303114);
        obj.put("id",vid);
        obj.put("rid",2);
        obj.put("url","https://www.mgtv.com/s/"+vid+".html");
        obj.put("on_date","");
        obj.put("clip_type",1);
        obj.put("vtt",1968);
        obj.put("ispreview",0);
        obj.put("ispay",0);
        obj.put("vip",0);
        obj.put("uname","");
        obj.put("ucode","");

        return obj.toString();
    }



    public static String getPlayerParmP(String vid,  String cxid, String pm2) throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("sdkversion", "PCWEBSDK_V2.1.4_20181127");
        obj.put("_v", "1");
        obj.put("fmt", "vast3");
        obj.put("parameter", 191);
        obj.put("float_ex", 3);
        obj.put("retry", 0);

        JSONObject m = new JSONObject();
        m.put("p", 4388);
        m.put("id", null);
        m.put("time", 0);
        m.put("allowad", 110110);
        m.put("ptype", "front");
        m.put("pu", "https://www.mgtv.com/s/"+vid+".html");
        obj.put("m", m);

        JSONObject u = new JSONObject();
        m.put("cxid", "");
        m.put("ck", DID);
        m.put("sid", cxid);
        m.put("cookie", DID);
        m.put("vip", 1);
        m.put("isContinue", 0);
        obj.put("u", u);

        JSONObject c = new JSONObject();
        m.put("type", 1);
        m.put("os", "Windows");
        m.put("ua", "mozilla/5.0 (windows nt 10.0; wow64) applewebkit/537.36 (khtml, like gecko) chrome/70.0.3538.102 safari/537.36");
        m.put("version", "pcweb-5.6.33.h5");
        m.put("rs", "1920*1080");
        m.put("bs", "870*489");
        m.put("mac", DID);
        obj.put("c", c);

        JSONObject atc = new JSONObject();
        m.put("pm2", pm2);
        m.put("tk2", MgtvUtils.tk2());
        obj.put("atc", atc);

        return obj.toString();
    }

    public static String getPlayVid(String url) {
        String vid = "unkown";
        String key = "/";
        int index = url.lastIndexOf(key);
        if (url.indexOf(".html") != -1) {
            vid = url.substring(index + key.length(), url.indexOf(".html"));
        } else {
            vid = url.substring(index + key.length());
        }
        Log.e(TAG, "getPlayVid(" + url + ")=" + vid);
        return vid;
    }


}
