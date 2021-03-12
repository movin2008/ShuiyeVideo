package com.shuiyes.video.ui.yinyang;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;

import com.devlin_n.yinyangplayer.R;
import com.devlin_n.yinyangplayer.controller.StandardVideoController;
import com.devlin_n.yinyangplayer.player.YinYangPlayer;
import com.shuiyes.video.bean.PlayVideo;
import com.shuiyes.video.dialog.MiscDialog;
import com.shuiyes.video.util.PreferenceUtil;
import com.shuiyes.video.widget.MiscView;
import com.shuiyes.video.widget.Tips;

import java.util.ArrayList;

public class ShuiyeVideoController extends StandardVideoController {

    public ShuiyeVideoController(@NonNull Context context) {
        super(context);
    }

    public ShuiyeVideoController(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ShuiyeVideoController(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initView() {
        super.initView();
        popupMenu.getMenu().findItem(R.id.video_source).setVisible(true);
    }

    private int index;
    private MiscDialog mTVSourceDialog;
    private ArrayList<PlayVideo> mSourceList;

    protected void setSourceList(ArrayList<PlayVideo> sourceList) {
        mSourceList = (ArrayList<PlayVideo>) sourceList.clone();

        String urlHistory = PreferenceUtil.getTVUrl(getContext());
        if (!TextUtils.isEmpty(urlHistory)) {
            for (int i = 0; i < mSourceList.size(); i++) {
                if (urlHistory.contains(mSourceList.get(i).getUrl())) {
                    index = i;
                    break;
                }
            }
        }
    }

    @Override
    protected void menuItemClick(MenuItem item) {
        super.menuItemClick(item);
        if (item.getItemId() == R.id.video_source) {
            if (mTVSourceDialog != null && mTVSourceDialog.isShowing()) {
                mTVSourceDialog.dismiss();
            }
            mTVSourceDialog = new MiscDialog(getContext(), mSourceList);
            mTVSourceDialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mTVSourceDialog != null && mTVSourceDialog.isShowing()) {
                        mTVSourceDialog.dismiss();
                    }
                    start(((MiscView) view).getPlayVideo());
                }
            });
            mTVSourceDialog.show();
        }
    }

    private void start(PlayVideo playVideo) {
        ((YinYangPlayer) mediaPlayer).setTitle(playVideo.getText());
        mediaPlayer.start(playVideo.getUrl());
        index = mSourceList.indexOf(playVideo);
        PreferenceUtil.setTVUrl(getContext(), playVideo.getText() + ";" + playVideo.getUrl());
    }

    private int mBufferedCount;
    private SparseArray<Long> mBufferCountTime = new SparseArray();

    private Handler mHandler = new Handler();
    private Runnable mBufferTimeoutRunnable = new Runnable() {
        @Override
        public void run() {
            onBufferTimeout();
        }
    };

    @Override
    public void setPlayState(int playerState) {
        super.setPlayState(playerState);

        switch (playerState) {
            case YinYangPlayer.STATE_PREPARING:
                Tips.show(getContext(), "播放：" + mediaPlayer.getTitle());
                break;
            case YinYangPlayer.STATE_PREPARED:
                mBufferedCount = 0;
                mBufferCountTime.clear();
                break;
            case YinYangPlayer.STATE_ERROR:
                onBufferTimeout();
                break;
            case YinYangPlayer.STATE_BUFFERING:
                mBufferCountTime.put(++mBufferedCount, System.currentTimeMillis());
                if (mBufferedCount >= 5) {
                    long timeDiff = mBufferCountTime.get(mBufferedCount) - mBufferCountTime.get(mBufferedCount - 4);
                    if (timeDiff < 60 * 1000) {
                        // 1分钟内缓冲5次
                        onBufferTimeout();
                        return;
                    }
                }
                // 一次缓冲时间大于 15s
                mHandler.postDelayed(mBufferTimeoutRunnable, 15 * 1000);
                break;
            case YinYangPlayer.STATE_BUFFERED:
                mHandler.removeCallbacks(mBufferTimeoutRunnable);
                break;
        }
    }

    // 缓冲超时，换下一源播放
    protected void onBufferTimeout() {
        if (++index == mSourceList.size()) {
            index = 0;
        }
        start(mSourceList.get(index));
    }
}