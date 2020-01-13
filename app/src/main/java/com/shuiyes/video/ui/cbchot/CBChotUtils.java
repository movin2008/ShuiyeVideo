package com.shuiyes.video.ui.cbchot;

import android.util.Base64;

import com.shuiyes.video.util.MD5;

public class CBChotUtils {

    private final static String TAG = "CBChotUtils";

    // AndroidManifest.xml <meta-data android:name="dopool_appkey" android:value="cA3MHIx6Y60H"/>
    public static final String APPKEY = "cA3MHIx6Y60H";

    // com.wasu.authcode.AuthCode.class
    public static int EXPIRE_TIME = 315360000;
    public static final String KEY = "zgrdy123!@#";
    public static final String LIVE_KEY = "zgrdy123!@#";
    public static final String SAFE_KEY = "WASUapp@)!#$vod12";

    // com.wasu.config.Constants
    public static String CHANNEL_CODE = "10062";
    public static String CHANNEL_NAME = "zgsj";
    public static String SDK_VERSION = "4.6.2";
    public static String USER_AGENT_NAME = "zgsj2";

    // packet capture
    public static final String XCLIENT = "x-client: sdk=9;screenSize=1080*1920;type=MI+6;imei=99001026335132;imsi=460110871769871;cell_id=;version=1.2.63.1.501;mac=02:00:00:00:00:00;rootPath=%2Fstorage%2Femulated%2F0;rn=0705065283;";
    public static final String SEARCH_URL = "http://and.cbchot.com/android/search?type=video&keyword=";
    public static final String VIDEO_URL = "http://and.cbchot.com/video?seriesNum=0&resCP=&resType=%s&id=%s";
    public static final String ONE_API = "http://cbc-sdk-web.dopool.com/api_cms/one/";
    public static final String VIDEO_DETAIL = "http://and.cbchot.com/api/video_detail/pay/";

    public static String getUrlParams(String url) {
        if (url.contains("?")) {
            return url.substring(url.indexOf("?") + 1);
        } else {
            return "";
        }
    }

    // com.wasu.utils.AuthCode.class authCode()
    public static String authCode(String wasuCodeStr, String key) {
        wasuCodeStr = b(wasuCodeStr);
        String keyMD5 = MD5.encode(key);
        String keyMD5Front = MD5.encode(keyMD5.substring(0, 16));
        String keyMD5After = MD5.encode(keyMD5.substring(16, 32));

        String wasuCode = wasuCodeStr.substring(0, 4);

        StringBuilder localBuffer2 = new StringBuilder();
        localBuffer2.append(keyMD5Front);
        StringBuilder localBuffer3 = new StringBuilder();
        localBuffer3.append(keyMD5Front);
        localBuffer3.append(wasuCode);
        localBuffer2.append(MD5.encode(localBuffer3.toString()));

        String localString2 = localBuffer2.toString();

//        final Base64.Decoder decoder = Base64.getDecoder();
//        byte[] decodeBytes = decoder.decode(wasuCodeStr.substring(4));
        byte[] decodeBytes = Base64.decode(wasuCodeStr.substring(4), Base64.DEFAULT);
        String wasuCodeDecodeStr = new String(decodeBytes);

        int n = wasuCodeDecodeStr.length();
        int[] localByte1 = new int[128];
        int[] localByte3 = new int[128];
        byte[] localByte2 = localString2.getBytes();
        int i = 0, j = localBuffer2.length();
        while (i < 128) {
            localByte1[i] = i;
            localByte3[i] = (localByte2[(i % j)] & 0xFF);
            i += 1;
        }
        i = 0;
        j = 0;
        while (i < 128) {
            j = (j + localByte1[i] + localByte3[i]) % 128;
            int k = localByte1[i];
            localByte1[i] = localByte1[j];
            localByte1[j] = k;
            i += 1;
        }
        localByte2 = wasuCodeDecodeStr.getBytes();
        byte[] paramByte2 = new byte[n];
        int k = 0, m = 0;
        i = 0;
        j = 0;
        while (i < n) {
            m = (m + 1) % 128;
            j = (j + localByte1[m]) % 128;
            k = localByte1[m];
            localByte1[m] = localByte1[j];
            localByte1[j] = k;
            k = localByte2[i];
            paramByte2[i] = ((byte) (localByte1[((localByte1[m] + localByte1[j]) % 128)] ^ k & 0xFF));
            i += 1;
        }

        String paramString1 = new String(paramByte2);
//        Log.e(TAG, paramString1);
        if ((Long.parseLong(paramString1.substring(0, 10)) == 0L) || (Long.parseLong(paramString1.substring(0, 10)) - (int) (System.currentTimeMillis() / 1000L) > 0L)) {
            StringBuilder paramBuffer3 = new StringBuilder();
            paramBuffer3.append(paramString1.substring(26));
            paramBuffer3.append(keyMD5After);
            if (paramString1.substring(10, 26).equals(MD5.encode(paramBuffer3.toString()).substring(0, 16))) {
                return paramString1.substring(26);
            }
        }
        return "";
    }

    private static String b(String paramString) {
        String str = "";
        int i = 0;
        while (i < paramString.length()) {
            int j = paramString.charAt(i);
            StringBuilder localStringBuilder;
            if ((j >= 19968) && (j <= 171941)) {
                localStringBuilder = new StringBuilder();
                localStringBuilder.append(str);
                localStringBuilder.append("\\u");
                localStringBuilder.append(Integer.toHexString(j));
                str = localStringBuilder.toString();
            } else {
                localStringBuilder = new StringBuilder();
                localStringBuilder.append(str);
                localStringBuilder.append(paramString.charAt(i));
                str = localStringBuilder.toString();
            }
            i += 1;
        }
        return str;
    }


}

/**
 * // POST http://and.cbchot.com/api/video_detail/pay/
 * // seriesNum=0&resType=transcoded&id=323309&resCP=-4&
 * {
 * "code": "100",
 * "msg": "操作成功",
 * "data": "",
 * "videoInfoPay": {
 * "videoId": "32309",
 * "videoUrl": "",
 * "videoName": "梅西一战成名",
 * "imgUrl": "http://cbc-web.dopool.com//imgdata/tiyu/1447142690_5641a5227d5f7_image.jpg",
 * "offset": "0",
 * "state": false,
 * "download_state": true,
 * "pay_state": false,
 * "payUrl": "",
 * "vipUrl": "",
 * "payText": "",
 * "vipText": "",
 * "seriesNum": "",
 * "detailUrl": "/android/detail/transcoded/32309",
 * "startOffset": "0",
 * "endOffset": "0",
 * "favId": "",
 * "cp": "3",
 * "fdn_code": "",
 * "shareTitle": "梅西一战成名",
 * "shareContent": "盘点西甲德比经典战役梅西帽子戏法一战成名。",
 * "shareUrl": "http://and.cbchot.com/android/share/32309?vt\u003d1\u0026cg\u003d0\u0026v\u003d1.2.63.1.501",
 * "wasuCode": "",
 * "fav": false
 * },
 * "videoInfo": {
 * "videoId": "32309",
 * "videoName": "梅西一战成名",
 * "commentState": true,
 * "favState": false,
 * "favId": "",
 * "shareState": true,
 * "shareTitle": "梅西一战成名",
 * "shareContent": "盘点西甲德比经典战役梅西帽子戏法一战成名。",
 * "shareUrl": "http://and.cbchot.com/android/share/32309?vt\u003d1\u0026cg\u003d0\u0026v\u003d1.2.63.1.501",
 * "shareImage": "http://cbc-web.dopool.com//imgdata/tiyu/1447142690_5641a5227d5f7_image.jpg",
 * "downloadState": true
 * }
 * }
 * <p>
 * // GET http://cbc-sdk-web.dopool.com/api_cms/one/32309?appkey=cA3MHIx6Y60H
 * [
 * {
 * weight: 0,
 * id: 32309,
 * profile_id: 2,
 * tag_id: "24156,13,9725",
 * filesize: 177034900,
 * tag_obj: {
 * 声明: "素材内容来源于网络 版权归原作者所有",
 * 类型: "足球",
 * 分类: "体育"
 * },
 * cpinfo: [
 * {
 * cp_name: "网友_老萝莉"
 * }
 * ],
 * user_id: 21,
 * res_type: 80,
 * epg_id: 0,
 * description: "盘点西甲德比经典战役梅西帽子戏法一战成名。",
 * image_hor_url: "http://cbc-stor.dopool.com/imgdata/tiyu/1447142690_5641a5227d5f7_image.jpg",
 * ori_name: "天才梅西 一战成名.mp4",
 * duration: 1497,
 * pinyin2: "",
 * status: 92000,
 * res_name: "梅西一战成名",
 * msg_id: 211900,
 * createtime: "2015-11-10 16:03:58",
 * pinyin: "meixiyizhanchengming",
 * parent_id: 32299,
 * image_ver_url: "",
 * py: "mxyzcm",
 * cp_id: 1021,
 * location: "http://cbc-stor.dopool.com/cp_name/xiazai/2015-11-10/3862/2/index_2.m3u8",
 * series_id: 0,
 * updatetime: "2016-10-31 13:42:22"
 * }
 * ]
 */