package com.shuiyes.video.mdd;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.shuiyes.video.base.BaseSearchActivity;
import com.shuiyes.video.bean.Album;
import com.shuiyes.video.bean.ListVideo;
import com.shuiyes.video.util.Constants;
import com.shuiyes.video.util.PlayUtils;
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

public class MDDSoActivity extends BaseSearchActivity implements Callback {

    private final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSearch.setHint("埋堆堆");
        mSearch.setText("天龙八部");
    }

    @Override
    protected void searchVideos(String keyword) {
        try {
            MDDUtils.search(keyword, MDDSoActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void playVideo(int position) {
        PlayUtils.play(mContext, mAlbums.get(position));
    }

    @Override
    public void onFailure(Call call, IOException e) {
        Log.e(TAG, "onFailure: " + call.request().url().url().getPath());
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        try {
            String action = call.request().url().url().getPath();
            ResponseBody responseBody = response.body();
            if (responseBody == null) {
                Log.e(TAG, "onResponse(null): " + action);
                return;
            } else {
                Log.e(TAG, "onResponse: " + action);
            }

            String result = responseBody.string();
            Utils.setFile("mdd2", result);
            JSONObject obj = new JSONObject(result);

            if (obj.getInt("msgType") == 0) {
                JSONArray dataList = obj.getJSONArray("data");
                if (MDDUtils.SearchAction.equals(action)) {
                    listSearchResult(dataList);
                } else if (MDDUtils.ListVodAction.equals(action)) {
                    listVodResult(call, dataList);
                } else {
                    Log.e(TAG, "onResponse unkown url.");
                }
            } else {
                Tips.show(mContext, call.request().url().url().getPath() + " " + obj.getString("msg"));
                Log.e(TAG, result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void listSearchResult(JSONArray dataList) throws Exception {
        mAlbums.clear();
        for (int j = 0; j < dataList.length(); j++) {
            JSONObject data = dataList.getJSONObject(j);
            JSONArray vodList = data.getJSONArray("vodList");
            for (int i = 0; i < vodList.length(); i++) {
                JSONObject vod = vodList.getJSONObject(i);
                String albumTitle = vod.getString("name");
                String albumSummary = vod.getString("introduction");
                String albumImg = vod.getString("coverImage");
                String albumUrl = vod.getString("uuid");;
                MDDUtils.listVodSections(albumUrl, MDDSoActivity.this);

                Album album = new Album(j, albumTitle, albumSummary, albumImg, albumUrl, null);
                mAlbums.add(album);
            }
        }
        mHandler.sendEmptyMessage(Constants.MSG_LIST_ALBUM);
    }

    private void listVodResult(Call call, JSONArray dataList) throws Exception {
        String vodUuid = call.request().header("vodUuid");
        if (TextUtils.isEmpty(vodUuid)) {
            return;
        }

        List<ListVideo> listVideos = new ArrayList<ListVideo>();
        for (int j = 0; j < dataList.length(); j++) {
            JSONObject data = dataList.getJSONObject(j);
            listVideos.add(j, new ListVideo(j + 1, data.getString("name"), MDDUtils.getPlayUrl(vodUuid, data.getString("uuid"))));
        }

        for (int i = 0; i < mAlbums.size(); i++) {
            Album album = mAlbums.get(i);
            if (vodUuid.equals(album.getAlbumUrl())) {
                album.setListVideos(listVideos);
                Log.e(TAG, "listVodResult setListVideos index=" + i+" vodUuid=" + vodUuid);
                mHandler.sendEmptyMessage(Constants.MSG_LIST_ALBUM);
                break;
            }
        }
    }

}
