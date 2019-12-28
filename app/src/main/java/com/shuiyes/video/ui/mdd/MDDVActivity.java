package com.shuiyes.video.ui.mdd;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.shuiyes.video.ui.base.BasePlayActivity;
import com.shuiyes.video.bean.ListVideo;
import com.shuiyes.video.bean.PlayVideo;
import com.shuiyes.video.ui.qq.QQStream;
import com.shuiyes.video.util.Utils;
import com.shuiyes.video.widget.Tips;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MDDVActivity extends BasePlayActivity implements Callback {

    private List<QQStream> mUrlList = new ArrayList<QQStream>();
    private List<PlayVideo> mSourceList = new ArrayList<PlayVideo>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBatName = "埋堆堆";
        playVideo();
    }

    @Override
    protected void playVideo() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    String uuid;
                    String vodUuid = null;
                    String[] uuids = mIntentUrl.split("/");
                    if(uuids.length > 2){
                        vodUuid = uuids[1];
                        uuid = uuids[2];
                    }else{
                        uuid = uuids[1];
                    }

                    if(uuid != null){
                        mHandler.sendEmptyMessage(MSG_FETCH_VIDEOINFO);
                        MDDUtils.getSectionInfo(uuid, MDDVActivity.this);
                    }

                    if(vodUuid != null && mVideoList.isEmpty()){
                        MDDUtils.listVodSections(vodUuid, MDDVActivity.this);
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

    @Override
    public void onFailure(Call call, IOException e) {
        Log.e(TAG, "onFailure: "+call.request().url().url().getPath());
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        try {
            String action = call.request().url().url().getPath();
            ResponseBody responseBody = response.body();
            if (responseBody == null) {
                Log.e(TAG, "onResponse(null): "+action);
                return;
            }else{
//                Log.e(TAG, "onResponse: "+action);
            }

            String result = responseBody.string();
            Utils.setFile("mdd3", result);

            JSONObject obj = new JSONObject(result);
            if(obj.getInt("msgType") == 0){
                if(MDDUtils.GetSectionInfoAction.equals(action)){
                    getSectionInfo(obj);
                }else if(MDDUtils.ListVodAction.equals(action)){
                    listVodResult(call, obj);
                }else{
                    Log.e(TAG, "onResponse unkown url.");
                }
            }else{
                Log.e(TAG, result);
                if(MDDUtils.GetSectionInfoAction.equals(action)){
                    fault(obj.getString("msg"));
                }else {
                    Tips.show(mContext, call.request().url().url().getPath()+" "+obj.getString("msg"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getSectionInfo(JSONObject obj) throws Exception {
        JSONObject section = obj.getJSONObject("data");
        mHandler.sendMessage(mHandler.obtainMessage(MSG_CACHE_URL, section.getString("oriUrl")));
    }

    private void listVodResult(Call call, JSONObject obj) throws Exception {
        JSONArray dataList = obj.getJSONArray("data");

        String vodUuid = call.request().header("vodUuid");
        if(TextUtils.isEmpty(vodUuid)){
            return;
        }

        for (int j = 0; j < dataList.length(); j++) {
            JSONObject data = dataList.getJSONObject(j);
            mVideoList.add(j, new ListVideo(j+1, data.getString("name"),MDDUtils.getPlayUrl(vodUuid, data.getString("uuid"))));
        }
        mHandler.sendEmptyMessage(MSG_UPDATE_SELECT);
    }

}
