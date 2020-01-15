package com.shuiyes.video.ui.cbchot;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.shuiyes.video.bean.ListVideo;
import com.shuiyes.video.bean.PlayVideo;
import com.shuiyes.video.ui.base.BasePlayActivity;
import com.shuiyes.video.util.HttpUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;

public class CBChotVActivity extends BasePlayActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBatName = "中广热点云";
        playVideo();
    }

    @Override
    protected void playVideo() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {

                    String vid = mIntentUrl.substring(mIntentUrl.indexOf("&id=") + 4);
                    mHandler.sendEmptyMessage(MSG_FETCH_VIDEOINFO);
                    String html = HttpUtils.post(CBChotUtils.VIDEO_DETAIL, CBChotUtils.getUrlParams(mIntentUrl), CBChotUtils.XCLIENT);
                    Log.e(TAG, html);

                    if (html.startsWith("Exception:")) {
                        fault(html);
                        return;
                    }

                    JSONObject obj = new JSONObject(html);
                    String code = obj.getString("code");
                    if (!"100".equals(code)) {
                        if ("121".equals(code)) {
                            fault("VIP 视频暂不支持播放");
                        } else {
                            fault(obj.getString("msg"));
                        }
                        return;
                    }

                    JSONObject videoInfoPay = obj.getJSONObject("videoInfoPay");
                    mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_TITLE, videoInfoPay.getString("videoName")));

                    String cp = videoInfoPay.getString("cp");
                    if ("3".equals(cp)) {
                        mHandler.sendEmptyMessage(MSG_FETCH_VIDEO);
                        html = HttpUtils.get(String.format("%s%s?appkey=%s", CBChotUtils.ONE_API, vid, CBChotUtils.APPKEY), CBChotUtils.XCLIENT, false);
                        Log.e(TAG, html);

                        if (TextUtils.isEmpty(html)) {
                            fault("请稍后再试");
                            return;
                        }
                        if (html.startsWith("Exception:")) {
                            fault(html);
                            return;
                        }
                        JSONArray arr = new JSONArray(html);

                        if (arr.length() == 0) {
                            fault("暂无视频数据");
                            return;
                        }

                        mVideoList.clear();
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject video = arr.getJSONObject(i);
                            mVideoList.add(new ListVideo(video.getString("res_name"), video.getString("res_name"), video.getString("location")));
                        }

                        mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_TITLE, mVideoList.get(0).getTitle()));
                        mHandler.sendMessage(mHandler.obtainMessage(MSG_CACHE_URL, mVideoList.get(0).getUrl()));
                    } else if ("-4".equals(cp)) {

                        String wasuCode = videoInfoPay.getString("wasuCode");
                        if (TextUtils.isEmpty(wasuCode)) {
                            fault("未知错误");
                            return;
                        }

                        mHandler.sendEmptyMessage(MSG_FETCH_VIDEO);
                        String url = CBChotUtils.authCode(wasuCode, CBChotUtils.KEY);
                        if (url.endsWith(".mp4")) {
                            url = url.replace(".mp4", "/playlist.m3u8");
                            Log.e(TAG, url);

                            URL newUrl = new URL(url);
                            // TODO com.wasu.utils.ToolUtil.class getUrl(String, String, String, String)
                            // invoke-static {p0, p1, p5, p4}, Lcom/wasu/cores/WasuSdk;->getPlayUrlParams(Landroid/content/Context;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
                            url = newUrl.getProtocol() + "://" + newUrl.getHost() + "/5e1c0544/34300ce976485e70a30cd8bf72aafe95" + newUrl.getPath() + "?k=9eded5a9ec1f45f5936c33f5b11d45ad&su=afcBYJ4GYBaof8Nw1uaDxA==&uid=a8c043f1f61e504eec53013a41a88211&tn=90379170&t=160e9418c8fd3637b6738ee0d8930ba3&v=2";

                            url = String.format("%s&src=%s&cid=%s&vid=%s&WS00002=%s&em=3", url, CBChotUtils.CHANNEL_NAME, "", vid, CBChotUtils.CHANNEL_CODE);
                            Log.e(TAG, url);

//                            mHandler.sendMessage(mHandler.obtainMessage(MSG_CACHE_URL, url));
                            fault("完善中...");
                        }else{
                            mHandler.sendMessage(mHandler.obtainMessage(MSG_CACHE_URL, url));
                        }
                    } else {
                        fault("待完善");
                    }
                } catch (Exception e) {
                    fault(e);
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void cacheVideo(PlayVideo video) {
    }

}