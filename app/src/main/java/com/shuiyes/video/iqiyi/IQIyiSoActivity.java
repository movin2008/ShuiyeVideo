package com.shuiyes.video.iqiyi;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;

import com.shuiyes.video.base.SearchActivity;
import com.shuiyes.video.bean.Album;
import com.shuiyes.video.bean.ListVideo;
import com.shuiyes.video.util.Constants;
import com.shuiyes.video.util.PlayUtils;
import com.shuiyes.video.util.Utils;
import com.shuiyes.video.widget.Tips;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class IQIyiSoActivity extends SearchActivity {

    private final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSearch.setHint("爱奇艺搜索");
        mSearch.setText("延禧攻略");
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void searchVideos(String keyword) {
        mSearchAsyncTask = new SearchAsyncTask();
        mSearchAsyncTask.execute(keyword);
    }

    @Override
    protected void playVideo(int position) {
        PlayUtils.play(mContext, mAlbums.get(position));
    }

    private JSONObject albumDocInfo;

    private class SearchAsyncTask extends AsyncTask<String, Integer, Boolean> {

        private boolean mCancelled = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mCancelled = false;
        }

        @Override
        protected void onCancelled(Boolean aBoolean) {
            super.onCancelled(aBoolean);
            Log.e(TAG, "onCancelled.");
            mCancelled = true;
        }

        @Override
        protected Boolean doInBackground(String... args) {
            String keyword = args[0];
            try {
                if(mCancelled){
                    Log.e(TAG, "doInBackground has Cancelled.");
                    return false;
                }

                String result = IQiyiUtils.search(keyword);
                //Log.e(TAG, result);

                if (TextUtils.isEmpty(result)) {
                    Log.e(TAG, "Search "+keyword+" is empty.");
                    return false;
                }

                if(mCancelled){
                    Log.e(TAG, "Will list albums has Cancelled.");
                    return false;
                }


                Utils.setFile("/sdcard/iqiyi", result);

                JSONObject obj = new JSONObject(result);
                if (!"A00000".equals(obj.getString("code"))) {
                    //Log.e(TAG, result);

                    if(obj.has("msg")){
                        String msg = obj.getString("msg");
                        Log.e(TAG, msg);
                        mHandler.obtainMessage(Constants.MSG_SHOW_TIPS, msg);
                    }else{
                        String msg = obj.getJSONObject("ctl").getString("msg");
                        Log.e(TAG, msg);
                        mHandler.obtainMessage(Constants.MSG_SHOW_TIPS, msg);
                    }
                    return false;
                }

                if(obj.get("data") instanceof String){
                    String msg = obj.getString("data");
                    Log.e(TAG, msg);
                    mHandler.obtainMessage(Constants.MSG_SHOW_TIPS, msg);
                    return false;
                }

                JSONObject data = obj.getJSONObject("data");
                JSONArray docinfos = data.getJSONArray("docinfos");

                int docinfosLen = docinfos.length();
                if(docinfosLen > 30) docinfosLen = 30;

                int flag = 1;
                mAlbums.clear();
                for (int i = 0; i < docinfosLen; i++) {
                    JSONObject docinfo = (JSONObject) docinfos.get(i);

                    albumDocInfo = docinfo.getJSONObject("albumDocInfo");

                    if(albumDocInfo.getInt("videoDocType") == 9){
                        // 小说
                        continue;
                    }

                    String albumTitle = null;
                    String albumImg = null;
                    String albumUrl = null;
                    if(albumDocInfo.has("albumTitle")){
                        albumTitle = albumDocInfo.getString("albumTitle");
                        albumImg = albumDocInfo.getString("albumImg");
                        albumUrl = "";
                        if(albumDocInfo.has("albumLink")){
                            albumUrl = albumDocInfo.getString("albumLink");
                        }
                    }else if(albumDocInfo.has("video_lib_meta")){
                        JSONObject video_lib_meta = albumDocInfo.getJSONObject("video_lib_meta");
                        if(video_lib_meta.has("title")){
                            albumTitle = video_lib_meta.getString("title");
                            albumImg = video_lib_meta.getString("poster");
                            albumUrl = video_lib_meta.getString("link");
                        }
                    }else{
                        throw new Exception("albumDocInfo error.");
                    }
                    String albumSummary = "暂无简介";
                    if(albumDocInfo.has("bookSummary")){
                        albumSummary = albumDocInfo.getJSONObject("bookSummary").getString("description");
                    }

                    List<ListVideo> listVideos = new ArrayList<ListVideo>();

                    // 正片
                    if(albumDocInfo.has("videoinfos")) {
                        JSONArray videoinfos = albumDocInfo.getJSONArray("videoinfos");
                        if(videoinfos.length() > 0){
                            if(TextUtils.isEmpty(albumTitle)){
                                albumTitle = ((JSONObject) videoinfos.get(0)).getString("itemTitle");
                            }
                            if(TextUtils.isEmpty(albumImg)){
                                albumImg = ((JSONObject) videoinfos.get(0)).getString("itemVImage");
                            }
                            if(TextUtils.isEmpty(albumUrl)){
                                albumUrl = ((JSONObject) videoinfos.get(0)).getString("itemLink");
                            }
                        }
                        listVideos(listVideos, videoinfos, albumTitle);
                    }

                    // 预告
                    if(albumDocInfo.has("prevues")){
                        JSONArray prevues = albumDocInfo.getJSONArray("prevues");
                        if(prevues.length() > 0){
                            if(TextUtils.isEmpty(albumTitle)){
                                albumTitle = ((JSONObject) prevues.get(0)).getString("itemTitle");
                            }
                            if(TextUtils.isEmpty(albumImg)){
                                albumImg = ((JSONObject) prevues.get(0)).getString("itemVImage");
                            }
                            if(TextUtils.isEmpty(albumUrl)){
                                albumUrl = ((JSONObject) prevues.get(0)).getString("itemLink");
                            }
                        }
                        listVideos(listVideos, prevues, albumTitle);
                    }

                    if(TextUtils.isEmpty(albumUrl)){
                        throw new Exception("albumUrl error.");
                    }

//                    Log.e(TAG, videoinfosLen+", 《"+albumTitle+"》 "+albumUrl);
                    if(PlayUtils.isSurpportUrl(albumUrl)){
                        Album album = new Album(flag++, albumTitle, albumSummary, albumImg, albumUrl, listVideos);
                        if(PlayUtils.isSurpportUrl(album.getPlayurl())){
                            mAlbums.add(album);
                        }else{
                            Log.e(TAG, "暂不支持视频 《"+albumTitle+"》" + album.getPlayurl());
                        }
                    }else{
                        Log.e(TAG, "暂不支持播放 《"+albumTitle+"》" + albumUrl);
                    }
                }

                Log.e(TAG, "mAlbums ===== " + mAlbums.size());
                return keyword.equals(mSearchText);
            } catch (Exception e) {
                if(albumDocInfo != null){
                    Log.e(TAG, "albumDocInfo ===== " + albumDocInfo.toString());
                }
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                mHandler.sendEmptyMessage(Constants.MSG_LIST_ALBUM);
            }
        }
    }

    private void listVideos(List<ListVideo> listVideos, JSONArray jsonArray, String albumTitle) throws Exception {
        int len = jsonArray.length();
        for (int j = 0; j < len; j++) {
            JSONObject obj = (JSONObject) jsonArray.get(j);

            String name = obj.getString("itemTitle");
            String url = obj.getString("itemLink");

            if(obj.has("tvId") && obj.has("vid")){
                url += "?tvId="+obj.getString("tvId")+"&vid="+obj.getString("vid");
            }

            String id = String.valueOf(j+1);
            if(obj.has("itemshortTitle")){
                String sTitle = obj.getString("itemshortTitle");
                if(!sTitle.contains("第"+id+"集")){
                    id = sTitle.replaceAll(albumTitle, "");
                }
            }

            ListVideo listVideo = new ListVideo(id, name, url);
            listVideos.add(listVideo);
        }
    }
}
