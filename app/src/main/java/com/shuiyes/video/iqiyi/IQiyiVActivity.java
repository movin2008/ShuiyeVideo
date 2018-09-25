package com.shuiyes.video.iqiyi;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.shuiyes.video.R;
import com.shuiyes.video.base.PlayActivity;
import com.shuiyes.video.bean.ListVideo;
import com.shuiyes.video.bean.PlayVideo;
import com.shuiyes.video.dialog.AlbumDialog;
import com.shuiyes.video.dialog.MiscDialog;
import com.shuiyes.video.letv.LetvSource;
import com.shuiyes.video.letv.LetvStream;
import com.shuiyes.video.util.HttpUtils;
import com.shuiyes.video.util.Utils;
import com.shuiyes.video.widget.MiscView;
import com.shuiyes.video.widget.NumberView;
import com.shuiyes.video.widget.Tips;
import com.shuiyes.video.youku.YoukuVideo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class IQiyiVActivity extends PlayActivity implements View.OnClickListener {

    private final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSourceView.setOnClickListener(this);
        mClarityView.setOnClickListener(this);
        mClarityView.setEnabled(false);
        mSelectView.setOnClickListener(this);
        mNextView.setOnClickListener(this);

        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                Log.e(TAG, " =========================== onPrepared");
                mediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);

                mPrepared = true;
                mHandler.sendEmptyMessage(MSG_PALY_VIDEO);
                mediaPlayer.start();
            }
        });

        mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                Log.e(TAG, " =========================== onError(" + i + "," + i1 + ")");
                String err = "视频无法播放(" + i + "," + i1 + ")";
                Tips.show(mContext, err, 0);
                fault(err);
                return false;
            }
        });

        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.e(TAG, " =========================== onCompletion");
                if (!mIsError) {
                    mLoadingProgress.setVisibility(View.VISIBLE);
                    playVideo();
                }
            }
        });

        playVideo();
    }

    private List<ListVideo> mVideoList = new ArrayList<ListVideo>();
    private List<IQiyiVideo> mUrlList = new ArrayList<IQiyiVideo>();

    private void playVideo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    mHandler.sendEmptyMessage(MSG_FETCH_VIDEOINFO);
                    String html = HttpUtils.open(mUrl);

                    String tvid = null;
                    String vid = null;
                    // page-info 见附录1
                    String key = ":page-info='";
                    if(html.contains(key)){
                        int len = html.indexOf(key);
                        String tmp = html.substring(len + key.length());
                        len = tmp.indexOf("'");
                        String pageInfo = tmp.substring(0, len);

                        try{
                            JSONObject obj = new JSONObject(pageInfo);

                            tvid = obj.getString("tvId");
                            Log.e(TAG, "tvId="+tvid);

                            vid = obj.getString("vid");

                            mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_TITLE, obj.getString("tvName")));
                        }catch (Exception e){
                        }
                    }

                    key = "param['tvid'] = \"";
                    if(tvid == null && html.contains(key)){
                        int len = html.indexOf(key);
                        String tmp = html.substring(len + key.length());
                        len = tmp.indexOf("\"");
                        tvid = tmp.substring(0, len);
                        Log.e(TAG, "param['tvid'] ="+tvid);
                    }

                    key = "param['vid'] = \"";
                    if(tvid == null && html.contains(key)){
                        int len = html.indexOf(key);
                        String tmp = html.substring(len + key.length());
                        len = tmp.indexOf("\"");
                        vid = tmp.substring(0, len);
                        Log.e(TAG, "param['vid'] ="+vid);
                    }

                    String video = IQiyiUtils.getVMS(tvid, vid);

//                    Log.e(TAG, "video ="+video);
                    Utils.setFile("/sdcard/iqiyi", video);

                    JSONObject obj = new JSONObject(video);
                    if (!"A00000".equals(obj.getString("code"))) {
//                        Log.e(TAG, info);
                        fault(obj.getString("msg"));
                        return;
                    }

                    JSONArray vidl = obj.getJSONObject("data").getJSONArray("vidl");
                    int vidlLen = vidl.length();

                    mUrlList.clear();
                    for (int i = 0; i < vidlLen; i++) {
                        JSONObject stream = (JSONObject) vidl.get(i);

                        int vd = stream.getInt("vd");
                        String m3u8Url = stream.getString("m3u");

                        mUrlList.add(new IQiyiVideo(IQiyiVideo.formateVideoType(vd), m3u8Url));
                    }
                    Log.e(TAG, "UrlList=" + mUrlList.size() + "/" + vidlLen);

                    if (mUrlList.isEmpty()) {
                        fault("无视频地址");
                    } else {
                        Collections.sort(mUrlList, new Comparator<IQiyiVideo>() {
                            @Override
                            public int compare(IQiyiVideo v1, IQiyiVideo v2) {
                                return v2.getType().getScreenSize() - v1.getType().getScreenSize();
                            }
                        });

                        IQiyiVideo playVideo = null;
                        for (IQiyiVideo v : mUrlList) {
                            Log.i(TAG, v.toStr(mContext));

                            // TODO 4K 较卡，要改成可配置
                            if(playVideo == null && v.getType().getScreenSize() < IQiyiVideo.UHD_SZ){
                                playVideo = v;
                            }
                        }

                        mCurrentPosition = 0;
                        mHandler.sendMessage(mHandler.obtainMessage(MSG_CACHE_VIDEO, playVideo));
                    }
                } catch (Exception e) {
                    fault(e);
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
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
                        mHandler.sendMessage(mHandler.obtainMessage(MSG_CACHE_VIDEO, ((MiscView) view).getPlayVideo()));
                    }
                });
                mClarityDialog.show();
                break;
            case R.id.btn_select:
                if (mAlbumDialog != null && mAlbumDialog.isShowing()) {
                    mAlbumDialog.dismiss();
                }
                mAlbumDialog = new AlbumDialog(this, mVideoList);
                mAlbumDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mAlbumDialog != null && mAlbumDialog.isShowing()) {
                            mAlbumDialog.dismiss();
                        }

                        NumberView v = (NumberView) view;
                        mTitleView.setText(v.getTitle());
                        mVid = v.getUrl();

                        mVideoView.stopPlayback();

                        playVideo();
                    }
                });
                mAlbumDialog.show();
                break;
            case R.id.btn_next:
                playVideo();
                break;
        }
    }

    @Override
    protected void cacheVideo(PlayVideo video) {
        if (mUrlList.size() < 2) {
            mClarityView.setEnabled(false);
        }else{
            mClarityView.setEnabled(true);
        }

        mClarityView.setText(((IQiyiVideo)video).getType().getProfile());
    }

}

/**
 附录1 :page-info='
 {
 "albumId": 205014501,
 "albumName": "凉生我们可不可以不忧伤",
 "albumUrl": "//www.iqiyi.com/a_19rrh9zbm1.html",
 "imageUrl": "//pic8.iqiyipic.com/image/20180917/6e/3a/v_119065977_m_601.jpg",
 "tvId": 1342023800,
 "vid": "2da89a1366f64a809705ebbef07b8d58",
 "cid": 2,
 "isSource": false,
 "contentType": 1,
 "vType": "video",
 "pageNo": "1",
 "pageType": "player",
 "userId": 0,
 "pageUrl": "http://www.iqiyi.com/v_19rqzvbyms.html",
 "tvName": "凉生我们可不可以不忧伤第1集",
 "isfeizhengpian": "true",
 "categoryName": "电视剧",
 "categories": "言情,内地,偶像,都市,青春,",
 "downloadAllowed": 1,
 "publicLevel": "PUBLIC",
 "payMarkUrl": "",
 "vipType": [0],
 "qiyiProduced": 0,
 "exclusive": 0,
 "tvYear": "0",
 "duration": "54:41",
 "wallId": 211693147,
 "rewardAllowed": 0,
 "commentAllowed": 1,
 "heatShowTypes": "auto",
 "videoTemplate": 2,
 "issueTime": "2018-09-18"
 }
 '
 */

/**
 视频详情：http://mixer.video.iqiyi.com/jp/mixin/videos/tvid
 Exeample：http://mixer.video.iqiyi.com/jp/mixin/videos/1342023800

 var tvInfoJs = {
 "name": "凉生我们可不可以不忧伤第1集",
 "description": " 那年，四岁的小女孩姜生和母亲在家等着爸爸姜凉之返乡，在省城当记者的姜凉之和同事程卿接到矿场杨老板的下属北国雄和何满厚的举报，特意来调查矿场的安全问题。结果意外发生了矿难，姜母急匆匆赶到医院，见到了瘫痪的姜凉之，也见到了程卿的儿子凉生。程卿为救姜凉之受重伤，送回省城抢救无效去世。姜凉之为报答程卿，提出收养凉生，从此凉生就是姜家人，是姜生的哥哥。村里人都觉得凉生是姜凉之的私生子，时常到姜家闹事。一日北小武和凉生打架，北妈上门兴师问罪，姜母只能责问姜生，凉生更加心疼过妹妹。姜生带凉生到花田，凉生告诉姜生城里有花店，姜生说长大后想开家花店。岁月飞逝，两人进入青少年时期。\n",
 "tvId": 1342023800,
 "vid": "2da89a1366f64a809705ebbef07b8d58",
 "url": "http://www.iqiyi.com/v_19rqzvbyms.html",
 "playCount": 163197989,
 "albumId": 205014501,
 "videoType": 1,
 "crumbList": [{
 "title": "爱奇艺首页",
 "level": 1,
 "url": "http://www.iqiyi.com"
 }, {
 "title": "电视剧",
 "level": 2,
 "url": "http://www.iqiyi.com/dianshiju/"
 }, {
 "title": "凉生我们可不可以不忧伤",
 "level": 3,
 "url": "http://www.iqiyi.com/a_19rrh9zbm1.html"
 }],
 "user": {
 "id": 2,
 "name": "电视剧",
 "avatar": "http://pic4.iqiyipic.com/common/lego/20150119/5e5f227c953b4f7b96055212b592443f.png",
 "description": "最丰富的影视剧资源、海内外最新影视剧抢先看，享受追剧快感，尽在爱奇艺电视剧频道。",
 "profileUrl": "http://www.iqiyi.com/dianshiju/index.html",
 "ppsUrl": "http://www.pps.tv/tv/"
 },
 "duration": 3281,
 "upCount": 21798,
 "downCount": 4396,
 "imageUrl": "http://pic8.iqiyipic.com/image/20180918/6a/c1/a_100035270_m_601_m10.jpg",
 "issueTime": 1537201508000,
 "categories": [{
 "name": "内地",
 "url": "http://list.iqiyi.com/www/2/15------------------.html",
 "id": 15,
 "subName": "地区",
 "subType": 1,
 "level": "0",
 "qipuId": 1533,
 "parentId": 18002
 }, {
 "name": "言情剧",
 "url": "http://list.iqiyi.com/www/2/-20-----------------.html",
 "id": 20,
 "subName": "类型",
 "subType": 2,
 "level": "0",
 "qipuId": 2033,
 "parentId": 18003
 }, {
 "name": "偶像剧",
 "url": "http://list.iqiyi.com/www/2/-30-----------------.html",
 "id": 30,
 "subName": "类型",
 "subType": 2,
 "level": "0",
 "qipuId": 3033,
 "parentId": 18003
 }, {
 "name": "青春剧",
 "url": "http://list.iqiyi.com/www/2/-1653-----------------.html",
 "id": 1653,
 "subName": "类型",
 "subType": 2,
 "level": "0",
 "qipuId": 165333,
 "parentId": 18003
 }, {
 "name": "普通话",
 "url": "http://list.iqiyi.com/www/2/------------------.html",
 "id": 20101,
 "subName": "配音语种",
 "subType": 0,
 "level": "0",
 "qipuId": 2010133,
 "parentId": 20100
 }, {
 "name": "都市",
 "url": "http://list.iqiyi.com/www/2/-24064-----------------.html",
 "id": 24064,
 "subName": "类型",
 "subType": 2,
 "level": "1",
 "qipuId": 2406433,
 "parentId": 18003
 }, {
 "name": "当代",
 "url": "http://list.iqiyi.com/www/2/--29651----------------.html",
 "id": 29651,
 "subName": "时代",
 "subType": 3,
 "level": "1",
 "qipuId": 2965133,
 "parentId": 29608
 }],
 "channelId": 2,
 "latestOrder": 12,
 "updateFlag": 0,
 "subtitle": "姜家收养凉生",
 "isPurchase": 0,
 "commentCount": 3577,
 "shareCount": 1355,
 "downloadAllowed": 1,
 "logoId": 1,
 "logoPosition": 0,
 "userId": 0,
 "season": 0,
 "period": "20180917",
 "exclusive": 0,
 "albumName": "凉生我们可不可以不忧伤",
 "qitanId": 13651939,
 "order": 1,
 "baikeUrl": "http://baike.baidu.com/item/åçæä»¬å¯ä¸å¯ä»¥ä¸å¿§ä¼¤/19522556",
 "mode1080p": 1,
 "mode720p": 1,
 "dolby": 1,
 "albumImageUrl": "http://pic8.iqiyipic.com/image/20180918/6a/c1/a_100035270_m_601_m10.jpg",
 "posterUrl": "http://pic7.iqiyipic.com/image/20180913/be/e1/a_100035270_m_600_m5.jpg",
 "series": 1,
 "filmId": 218586214,
 "playlistReason": "",
 "effective": 1,
 "qiyiProduced": 0,
 "albumUrl": "http://www.iqiyi.com/a_19rrh9zbm1.html",
 "sourceId": 0,
 "focus": "十年凉生 此情天佑",
 "videoCount": 70,
 "videoImageUrl": "http://pic8.iqiyipic.com/image/20180917/6e/3a/v_119065977_m_601.jpg",
 "albumQipuId": 205014501,
 "qipuId": 1342023800,
 "platforms": [11, 1, 2, 3, 15, 4, 5],
 "crFreeStartDate": "",
 "dimension": 2,
 "videoPageStatus": 1,
 "copyrightStatus": 1,
 "shortTitle": "凉生我们可不可以不忧伤第1集",
 "solo": 0,
 "latestUrl": "http://www.iqiyi.com/v_19rr4tlkuk.html",
 "latestId": 1360330400,
 "albumFocus": "十年凉生 此情天佑",
 "purchaseType": 0,
 "displayBulletHell": 1,
 "ppsBase": {
 "name": "凉生我们可不可以不忧伤第1集",
 "channelId": 2,
 "pageStatus": 0,
 "focus": "十年凉生 此情天佑",
 "imageUrl": "http://pic8.iqiyipic.com/image/20180917/6e/3a/v_119065977_m_601.jpg",
 "posterImageUrl": "",
 "pageUrl": ""
 },
 "contentType": 1,
 "fgtwVideo": 0,
 "topChart": 0,
 "isPpsExclusiveStatus": 0,
 "topicIds": [],
 "notices": [],
 "commentAllowed": 1,
 "score": 7.7,
 "seoKeywords": [],
 "panorama": {
 "videoType": 1,
 "videwAngleX": 0.0,
 "viewAangleY": 0.0,
 "zoomRate": 1.0
 },
 "bossMixerAlbum": 0,
 "ppsUrl": "",
 "publicLevel": 0,
 "relatedKeyword": "凉生我们可不可以不忧伤",
 "featureKeyword": "凉生我们可不可以不忧伤",
 "votes": [],
 "featureAlbumId": 205014501,
 "resolution": "",
 "editorInfo": "",
 "paikeType": 0,
 "startTime": -1,
 "endTime": -1,
 "circle": {
 "id": 211693147,
 "type": 2
 },
 "displayCircle": 1,
 "payMark": 0,
 "rewardAllowed": 0,
 "rewardMessage": "",
 "albumBossStatus": 0,
 "supportedDrmTypes": [],
 "ablumQipuId": 205014501,
 "ipId": 0,
 "intellectual": {
 "id": 210540770,
 "deleted": 0,
 "books": [224663239],
 "tickets": [],
 "games": [],
 "comicbooks": []
 },
 "qualityImageUrl": "http://m.iqiyipic.com/image/20180917/6e/3a/v_119065977_m_601.jpg",
 "feedIds": [103919019448],
 "programId": 59214,
 "irChannelId": 2,
 "displayUpDown": 1,
 "publishTime": 1537199708000,
 "starTotal": 0,
 "fiveStar": 0,
 "fourStar": 0,
 "threeStar": 0,
 "twoStar": 0,
 "oneStar": 0,
 "payMarkUrl": "",
 "vipType": [0],
 "tvImageUrl": ""
 }
 */