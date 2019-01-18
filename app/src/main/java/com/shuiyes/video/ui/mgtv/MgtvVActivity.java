package com.shuiyes.video.ui.mgtv;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.shuiyes.video.R;
import com.shuiyes.video.base.BasePlayActivity;
import com.shuiyes.video.bean.PlayVideo;
import com.shuiyes.video.dialog.MiscDialog;
import com.shuiyes.video.ui.qq.QQStream;
import com.shuiyes.video.util.Utils;
import com.shuiyes.video.widget.MiscView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MgtvVActivity extends BasePlayActivity {

    private List<QQStream> mUrlList = new ArrayList<QQStream>();
    private List<PlayVideo> mSourceList = new ArrayList<PlayVideo>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBatName = "芒果视频";
        mVid = MgtvUtils.getPlayVid(mIntentUrl);
        playVideo();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_source:
                if (mSourceDialog != null && mSourceDialog.isShowing()) {
                    mSourceDialog.dismiss();
                }
                mSourceDialog = new MiscDialog(this, mSourceList);
                mSourceDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mSourceDialog != null && mSourceDialog.isShowing()) {
                            mSourceDialog.dismiss();
                        }

                        mStateView.setText("初始化...");
                        mHandler.sendMessage(mHandler.obtainMessage(MSG_CACHE_VIDEO, ((MiscView) view).getPlayVideo()));
                    }
                });
                mSourceDialog.show();
                break;
            case R.id.btn_clarity:
                if (mClarityDialog != null && mClarityDialog.isShowing()) {
                    mClarityDialog.dismiss();
                }
                mClarityDialog = new MiscDialog(this, mUrlList);
                mClarityDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mClarityDialog != null && mClarityDialog.isShowing()) {
                            mClarityDialog.dismiss();
                        }

                        mStateView.setText("初始化...");
                        MiscView v = (MiscView) view;

                        QQStream stream = (QQStream) v.getPlayVideo();

                        mClarityView.setText(stream.getCname());

                    }
                });
                mClarityDialog.show();
                break;
            default:
                super.onClick(view);
                break;
        }
    }

    @Override
    protected void playVideo() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    mHandler.sendEmptyMessage(MSG_FETCH_VIDEOINFO);
                    String video = MgtvUtils.getVideo(mVid);
                    Utils.setFile("mgtv", video);

                    if(TextUtils.isEmpty(video)){
                        fault("请稍后重试...");
                        return;
                    }

                    JSONObject json = new JSONObject(video);
                    if (json.getInt("code") != 200) {
                        fault(json.getString("msg"));
                        return;
                    }

                    JSONObject data = json.getJSONObject("data");
                    JSONObject atc = data.getJSONObject("atc");
                    JSONObject user = data.getJSONObject("user");
                    String pm2 = atc.getString("pm2");
                    String cxid = user.getString("cxid");

                    mHandler.sendEmptyMessage(MSG_FETCH_TOKEN);
                    String vast = MgtvUtils.getPm2(mVid, cxid, pm2);
                    Utils.setFile("mgtv", vast);

                    if(vast.contains("<pm2>") & vast.contains("</pm2>")){
                        pm2 = vast.substring(vast.indexOf("<Pm2>")+5, vast.indexOf("</Pm2>"));
                    }else{
                        fault("授权异常...");
                        return;
                    }

                    mHandler.sendEmptyMessage(MSG_FETCH_VIDEO);
                    String source = MgtvUtils.getSource(mVid, pm2);
                    Utils.setFile("mgtv", source);

                    json = new JSONObject(source);
                    if (json.getInt("code") != 200) {
                        fault(json.getString("msg"));
                        return;
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
        if (mSourceList.size() > 1) {
            mSourceView.setVisibility(View.VISIBLE);
        } else {
            mSourceView.setVisibility(View.GONE);
        }
    }

}
