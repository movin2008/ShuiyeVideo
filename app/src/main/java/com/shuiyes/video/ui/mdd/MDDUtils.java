package com.shuiyes.video.ui.mdd;

import com.shuiyes.video.util.MD5;
import com.shuiyes.video.util.OkHttpManager;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class MDDUtils {

    private static OkHttpClient Client = OkHttpManager.getNormalClient();

    private final static String TAG = "MDDUtils";

    // v1.18.4 sign 密钥
    private static final String VALUE_PRIVATE_KEY = "e1be6b4cf4021b3d181170d1879a530a9e4130b69032144d5568abfd6cd6c1c2";

    private static final String OS = "Android";
    // 登陆后获取的 appToken, 账号每登陆一次刷新
    private static final String AppToken = "1d4130ab506d40ec969452f14cd7b13d424ed476b8cdbbea62f46fe997cbc5d0";

    /*
     sign 算法 v3.2.3 与 v1.8.4 不同, 反编译apk失败
     data:
     "keyWord": "天龙八部",
     "rows": 20,
     "startRow": 0,
     "type": 3 // type 1-视频 2-文章 3-剧集 4-综艺
     */
//    private static final String Version = "3.2.3";
//    private static final String SearchAction = "/searchApi/search/getSearchResult.action";
//    private static final String DeviceNum = "A7C45A9B8EBDC6466A4315D5019D7971";
//    private static final String SearchUrl = "https://mob.mddcloud.com.cn/searchApi/search/getSearchResult.action";

    private static final String Version = "1.18.4";
    public static final String SearchAction = "/api/module/search.action";
    private static final String SearchUrl = "https://api.mddcloud.com.cn/api/module/search.action";

    /**
     * 关键字搜索
     *
     * @param keyword
     * @param callback
     */
    public static void search(String keyword, Callback callback) {
        JSONObject data = null;
        try {
            data = new JSONObject().put("keyword", keyword);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String postData = MDDUtils.getPostData(data, SearchAction, "keyword=" + keyword + "&");
        Request.Builder builder = new Request.Builder();
        builder.url(SearchUrl);
        builder.header("keyword", MD5.encode(keyword));
        builder.post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), postData));
        Request request = builder.build();
        Client.newCall(request).enqueue(callback);

    }

    public static final String ListVodAction = "/api/vod/listVodSactions.action";
    private static final String ListVodUrl = "https://api.mddcloud.com.cn/api/vod/listVodSactions.action";

    /**
     * 剧集列表
     *
     * @param vodUuid
     * @param callback
     */
    public static void listVodSections(String vodUuid, Callback callback) {
        JSONObject data = null;
        try {
            data = new JSONObject().put("hasIntroduction", 1).put("rows", 5000).put("startRow", 0).put("vodUuid", vodUuid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String postData = MDDUtils.getPostData(data, ListVodAction, "hasIntroduction=1&rows=5000&startRow=0&vodUuid=" + vodUuid + "&");
        Request.Builder builder = new Request.Builder();
        builder.url(ListVodUrl);
        builder.header("vodUuid", vodUuid);
        builder.post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), postData));
        Request request = builder.build();
        Client.newCall(request).enqueue(callback);
    }

    public static final String GetSectionInfoAction = "/api/vod/getSactionInfo.action";
    private static final String GetSectionInfoUrl = "https://api.mddcloud.com.cn/api/vod/getSactionInfo.action";

    /**
     * 剧集信息包括视频路径
     *
     * @param uuid
     * @param callback
     */
    public static void getSectionInfo(String uuid, Callback callback) {
        JSONObject data = null;
        try {
            data = new JSONObject().put("sactionUuid", uuid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String postData = MDDUtils.getPostData(data, GetSectionInfoAction, "sactionUuid=" + uuid + "&");
        Request.Builder builder = new Request.Builder();
        builder.url(GetSectionInfoUrl);
        builder.post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), postData));
        Request request = builder.build();
        Client.newCall(request).enqueue(callback);
    }

    public static String getPlayUrl(String vodUuid, String uuid) {
        return "mdd.com/" + vodUuid + "/" + uuid;
    }

    private static String getPostData(JSONObject data, String action, String signData) {
        JSONObject jsonData = new JSONObject();
        try {
            long timestamp = System.currentTimeMillis();
            jsonData.put("appToken", AppToken);
            jsonData.put("channel", "1000");
            jsonData.put("data", data);
            jsonData.put("os", OS);
            jsonData.put("sign", MDDUtils.sign(timestamp, action, signData));
            jsonData.put("time", timestamp);
            jsonData.put("version", Version);

            // v3.2.3
//            jsonData.put("channel", "1003");
//            jsonData.put("deviceNum", deviceNum);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonData.toString();
    }

    private static String sign(long curTime, String action, String data) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("os");
        stringBuilder.append(":");
        stringBuilder.append(OS);
        stringBuilder.append("|");

        stringBuilder.append("version");
        stringBuilder.append(":");
        stringBuilder.append(Version);
        stringBuilder.append("|");

        stringBuilder.append("action");
        stringBuilder.append(":");
        stringBuilder.append(action);
        stringBuilder.append("|");

        stringBuilder.append("time");
        stringBuilder.append(":");
        stringBuilder.append(curTime);
        stringBuilder.append("|");

        stringBuilder.append("appToken");
        stringBuilder.append(":");
        stringBuilder.append(AppToken);
        stringBuilder.append("|");

        stringBuilder.append("privateKey");
        stringBuilder.append(":");
        stringBuilder.append(VALUE_PRIVATE_KEY);
        stringBuilder.append("|");

        stringBuilder.append("data");
        stringBuilder.append(":");
        stringBuilder.append(data);

        return Sign.md5(stringBuilder.toString());
    }

}