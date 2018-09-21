package com.shuiyes.video.letv;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;

import com.shuiyes.video.base.SearchActivity;
import com.shuiyes.video.bean.Album;
import com.shuiyes.video.bean.ListVideo;
import com.shuiyes.video.util.Constants;
import com.shuiyes.video.util.HttpUtils;
import com.shuiyes.video.util.PlayUtils;
import com.shuiyes.video.youku.YoukuUtils;

import java.util.ArrayList;
import java.util.List;

public class LetvSoActivity extends SearchActivity {

    private final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSearch.setText("绝地战警");
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

                String result = LetvUtils.search(keyword);
//					Log.e(TAG, result);

                if (TextUtils.isEmpty(result)) {
                    Log.e(TAG, "Seach "+keyword+" is empty.");
                    return false;
                }

                if(mCancelled){
                    Log.e(TAG, "Will list albums has Cancelled.");
                    return false;
                }

                mAlbums.clear();

                int flag = 1;
                String start = "<div class=\"So-detail Movie-so\"";
                while (result.contains(start)) {

                    if (mCancelled) {
                        Log.e(TAG, "Listing albums has Cancelled.");
                        return false;
                    }

                    int startIndex = result.indexOf(start);
                    int endIndex = result.indexOf(start, startIndex + start.length());
                    String data = null;
                    if (endIndex != -1) {
                        data = result.substring(startIndex, endIndex);
                    } else {
                        data = result.substring(startIndex);
                    }

                    flag++;
//                    Log.e(TAG, "data"+flag+" ===== "+data);
                    result = result.substring(startIndex+start.length());

                }
                Log.e(TAG, "mAlbums ===== " + flag);
                return keyword.equals(mSearchText);
            } catch (Exception e) {
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
