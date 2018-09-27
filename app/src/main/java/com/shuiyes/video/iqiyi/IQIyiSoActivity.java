package com.shuiyes.video.iqiyi;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.shuiyes.video.base.BaseSearchActivity;
import com.shuiyes.video.bean.Album;
import com.shuiyes.video.bean.ListVideo;
import com.shuiyes.video.util.Constants;
import com.shuiyes.video.util.PlayUtils;
import com.shuiyes.video.util.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class IQIyiSoActivity extends BaseSearchActivity {

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
            notice("onCancelled.");
            mCancelled = true;
        }

        @Override
        protected Boolean doInBackground(String... args) {
            String keyword = args[0];
            try {
                if (mCancelled) {
                    notice("doInBackground has Cancelled.");
                    return false;
                }

                String result = IQiyiUtils.search(keyword);
                //notice(result);

                if (TextUtils.isEmpty(result)) {
                    notice("Search " + keyword + " is empty.");
                    return false;
                }

                if (mCancelled) {
                    notice("Will list albums has Cancelled.");
                    return false;
                }


                Utils.setFile("/sdcard/iqiyi", result);

                JSONObject obj = new JSONObject(result);
                if (!"A00000".equals(obj.getString("code"))) {
                    //notice(result);

                    if (obj.has("msg")) {
                        String msg = obj.getString("msg");
                        mHandler.sendMessage(mHandler.obtainMessage(Constants.MSG_SHOW_TIPS, msg));
                        notice(msg);
                    } else {
                        String msg = obj.getJSONObject("ctl").getString("msg");
                        mHandler.sendMessage(mHandler.obtainMessage(Constants.MSG_SHOW_TIPS, msg));
                        notice(msg);
                    }
                    return false;
                }

                if (obj.get("data") instanceof String) {
                    String msg = obj.getString("data");
                    mHandler.sendMessage(mHandler.obtainMessage(Constants.MSG_SHOW_TIPS, msg));
                    notice(msg);
                    return false;
                }

                JSONObject data = obj.getJSONObject("data");
                JSONArray docinfos = data.getJSONArray("docinfos");

                int docinfosLen = docinfos.length();
                if (docinfosLen > 30) {
                    docinfosLen = 30;
                }

                int flag = 1;
                mAlbums.clear();
                for (int i = 0; i < docinfosLen; i++) {
                    JSONObject docinfo = (JSONObject) docinfos.get(i);

                    albumDocInfo = docinfo.getJSONObject("albumDocInfo");

                    if (albumDocInfo.getInt("videoDocType") == 9) {
                        // 小说
                        continue;
                    }

                    String albumTitle = null;
                    String albumImg = null;
                    String albumUrl = null;
                    if (albumDocInfo.has("albumTitle")) {
                        albumTitle = albumDocInfo.getString("albumTitle");
                        albumImg = albumDocInfo.getString("albumImg");
                        albumUrl = "";
                        if (albumDocInfo.has("albumLink")) {
                            albumUrl = albumDocInfo.getString("albumLink");
                        }
                    } else if (albumDocInfo.has("video_lib_meta")) {
                        JSONObject video_lib_meta = albumDocInfo.getJSONObject("video_lib_meta");
                        if (video_lib_meta.has("title")) {
                            albumTitle = video_lib_meta.getString("title");
                            albumImg = video_lib_meta.getString("poster");
                            albumUrl = video_lib_meta.getString("link");
                        }
                    } else {
                        throw new Exception("albumDocInfo error.");
                    }
                    String albumSummary = "暂无简介";
                    if (albumDocInfo.has("bookSummary")) {
                        albumSummary = albumDocInfo.getJSONObject("bookSummary").getString("description");
                    }

                    List<ListVideo> listVideos = new ArrayList<ListVideo>();

                    // 正片
                    if (albumDocInfo.has("videoinfos")) {
                        JSONArray videoinfos = albumDocInfo.getJSONArray("videoinfos");
                        if (videoinfos.length() > 0) {
                            if (TextUtils.isEmpty(albumTitle)) {
                                albumTitle = ((JSONObject) videoinfos.get(0)).getString("itemTitle");
                            }
                            if (TextUtils.isEmpty(albumImg)) {
                                albumImg = ((JSONObject) videoinfos.get(0)).getString("itemVImage");
                            }
                            if (TextUtils.isEmpty(albumUrl)) {
                                albumUrl = ((JSONObject) videoinfos.get(0)).getString("itemLink");
                            }
                        }
                        listVideos(listVideos, videoinfos, albumTitle);
                    }

                    // 预告
                    if (albumDocInfo.has("prevues")) {
                        JSONArray prevues = albumDocInfo.getJSONArray("prevues");
                        if (prevues.length() > 0) {
                            if (TextUtils.isEmpty(albumTitle)) {
                                albumTitle = ((JSONObject) prevues.get(0)).getString("itemTitle");
                            }
                            if (TextUtils.isEmpty(albumImg)) {
                                albumImg = ((JSONObject) prevues.get(0)).getString("itemVImage");
                            }
                            if (TextUtils.isEmpty(albumUrl)) {
                                albumUrl = ((JSONObject) prevues.get(0)).getString("itemLink");
                            }
                        }
                        listVideos(listVideos, prevues, albumTitle);
                    }

                    if (TextUtils.isEmpty(albumUrl)) {
                        throw new Exception("albumUrl error.");
                    }

//                    notice(videoinfosLen+", 《"+albumTitle+"》 "+albumUrl);
                    if (PlayUtils.isSurpportUrl(albumUrl)) {
                        Album album = new Album(flag++, albumTitle, albumSummary, albumImg, albumUrl, listVideos);
                        if (PlayUtils.isSurpportUrl(album.getPlayurl())) {
                            mAlbums.add(album);
                        } else {
                            Log.e(TAG, "暂不支持视频 《" + albumTitle + "》" + album.getPlayurl());
                        }
                    } else {
                        Log.e(TAG, "暂不支持播放 《" + albumTitle + "》" + albumUrl);
                    }
                }

                Log.e(TAG, "mAlbums ===== " + mAlbums.size());
                return keyword.equals(mSearchText);
            } catch (Exception e) {
                if (albumDocInfo != null) {
                    Utils.setFile("/sdcard/iqiyi", albumDocInfo.toString());
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

            //w_19s0ptu0id
            String url = "https://www.iqiyi.com/v_19a1b2c3d4.html";
            if(obj.has("itemLink")){
                url = obj.getString("itemLink");
            }

            if (obj.has("tvId") && obj.has("vid")) {
                url += "?tvid=" + obj.getString("tvId") + "&vid=" + obj.getString("vid");
            }

            String id = String.valueOf(j + 1);
            if (obj.has("itemshortTitle")) {
                String sTitle = obj.getString("itemshortTitle");
                if (!sTitle.contains("第" + id + "集")) {
                    id = sTitle.replaceAll(albumTitle, "");
                }
            }

            ListVideo listVideo = new ListVideo(id, name, url);
            listVideos.add(listVideo);
        }
    }
}
