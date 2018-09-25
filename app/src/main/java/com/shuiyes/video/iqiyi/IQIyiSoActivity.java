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
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class IQIyiSoActivity extends SearchActivity {

    private final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSearch.setHint("爱奇艺搜索");
        mSearch.setText("橙红年代");
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
                    Log.e(TAG, "Seach "+keyword+" is empty.");
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
                        Tips.show(mContext, obj.getString("msg"));
                    }else{
                        Tips.show(mContext, obj.getJSONObject("ctl").getString("msg"));
                    }
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
                        albumTitle = video_lib_meta.getString("title");
                        albumImg = video_lib_meta.getString("poster");
                        albumUrl = video_lib_meta.getString("link");
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
                        int videoinfosLen = videoinfos.length();

                        for (int j = 0; j < videoinfosLen; j++) {
                            JSONObject videoinfo = (JSONObject) videoinfos.get(j);

                            String name = videoinfo.getString("itemTitle");
                            String url = videoinfo.getString("itemLink");
                            if(TextUtils.isEmpty(albumUrl)){
                                albumUrl = url;
                            }

                            if(videoinfo.has("tvId") && videoinfo.has("vid")){
                                url += "?tvId="+videoinfo.getString("tvId")+"&vid="+videoinfo.getString("vid");
                            }

                            String id = String.valueOf(j+1);
                            if(videoinfo.has("itemshortTitle")){
                                String sTitle = videoinfo.getString("itemshortTitle");
                                if(!sTitle.contains("第"+id+"集")){
                                    id = sTitle;
                                }
                            }

                            ListVideo listVideo = new ListVideo(id, name, url);
                            listVideos.add(listVideo);
                        }
                    }

                    // 预告
                    if(albumDocInfo.has("prevues")){
                        JSONArray prevues = albumDocInfo.getJSONArray("prevues");
                        int prevuesLen = prevues.length();

                        for (int j = 0; j < prevuesLen; j++) {
                            JSONObject prevue = (JSONObject) prevues.get(j);

                            String name = prevue.getString("itemTitle");
                            String url = prevue.getString("itemLink");
                            if(TextUtils.isEmpty(albumUrl)){
                                albumUrl = url;
                            }

                            if(prevue.has("tvId") && prevue.has("vid")){
                                url += "?tvId="+prevue.getString("tvId")+"&vid="+prevue.getString("vid");
                            }

                            String id = String.valueOf(j+1);
                            if(prevue.has("itemshortTitle")){
                                String sTitle = prevue.getString("itemshortTitle");
                                if(!sTitle.contains("第"+id+"集")){
                                    id = sTitle;
                                }
                            }

                            ListVideo listVideo = new ListVideo(id, name, url);
                            listVideos.add(listVideo);
                        }
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
}
