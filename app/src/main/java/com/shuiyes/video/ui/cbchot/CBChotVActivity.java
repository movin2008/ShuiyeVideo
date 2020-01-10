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
                    mHandler.sendEmptyMessage(MSG_FETCH_VIDEOINFO);
                    String vid = mIntentUrl.substring(mIntentUrl.lastIndexOf("/") + 1);
                    String html = HttpUtils.open(CBChotUtils.ONE_URL + vid + "?appkey=" + CBChotUtils.APPKEY, CBChotUtils.XCLIENT, false);
                    Log.e(TAG, html);

                    if(TextUtils.isEmpty(html)){
                        fault("请稍后再试");
                        return;
                    }
                    if(html.startsWith("Exception:")){
                        fault(html);
                        return;
                    }
                    JSONArray arr = new JSONArray(html);

                    if(arr.length() == 0){
                        fault("暂无视频数据");
                        return;
                    }
                    for (int i = 0; i<arr.length(); i++){

                    }

                    JSONObject obj = arr.getJSONObject(0);
                    mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_TITLE, obj.getString("res_name")));
                    mHandler.sendMessage(mHandler.obtainMessage(MSG_CACHE_URL, obj.getString("location")));

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