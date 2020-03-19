package com.devlin_n.yinyangplayer.controller;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.devlin_n.yinyangplayer.R;
import com.devlin_n.yinyangplayer.player.YinYangPlayer;
import com.devlin_n.yinyangplayer.widget.PlayProgressButton;

/**
 * 悬浮播放控制器
 * Created by Devlin_n on 2017/6/1.
 */

public class FloatController extends BaseVideoController implements View.OnClickListener {

    private PlayProgressButton playProgressButton;
    private TextView mTitleView;
    private ImageView mCloseView;

    public FloatController(@NonNull Context context) {
        super(context);
    }

    public FloatController(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_float_controller;
    }

    @Override
    protected void initView() {
        super.initView();
        this.setOnClickListener(this);
        controllerView.findViewById(R.id.btn_close).setOnClickListener(this);
        playProgressButton = (PlayProgressButton) controllerView.findViewById(R.id.play_progress_btn);
        playProgressButton.setPlayButtonClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                doPauseResume();
            }
        });
        mTitleView = controllerView.findViewById(R.id.tv_title);
        mCloseView = controllerView.findViewById(R.id.btn_close);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_close) {
            mediaPlayer.stopFloatWindow();
        }
    }

    @Override
    public void setPlayState(int playState) {
        super.setPlayState(playState);
        switch (playState) {
            case YinYangPlayer.STATE_IDLE:
                break;
            case YinYangPlayer.STATE_PLAYING:
                playProgressButton.setState(PlayProgressButton.STATE_PLAYING);
                show();
                break;
            case YinYangPlayer.STATE_PAUSED:
                playProgressButton.setState(PlayProgressButton.STATE_PAUSE);
                show(0);
                break;
            case YinYangPlayer.STATE_PREPARING:
                playProgressButton.setState(PlayProgressButton.STATE_LOADING);
                break;
            case YinYangPlayer.STATE_PREPARED:
                playProgressButton.setVisibility(GONE);
                mTitleView.setText(title);
                break;
            case YinYangPlayer.STATE_ERROR:
                break;
            case YinYangPlayer.STATE_BUFFERING:
                playProgressButton.setState(PlayProgressButton.STATE_LOADING);
                playProgressButton.setVisibility(VISIBLE);
                break;
            case YinYangPlayer.STATE_BUFFERED:
                playProgressButton.setState(PlayProgressButton.STATE_LOADING_END);
                if (!mShowing) playProgressButton.setVisibility(GONE);
                break;
            case YinYangPlayer.STATE_PLAYBACK_COMPLETED:
                playProgressButton.setState(PlayProgressButton.STATE_PAUSE);
                show(0);
                break;
        }
    }


    @Override
    public void show() {
        show(sDefaultTimeout);
    }

    private void show(int timeout) {
        if (!mShowing) {
            mShowing = true;
            playProgressButton.show();
            mTitleView.setVisibility(View.VISIBLE);
            mCloseView.setVisibility(View.VISIBLE);
        }
        removeCallbacks(mFadeOut);
        if (timeout != 0) {
            postDelayed(mFadeOut, timeout);
        }
    }

    private String title;

    public FloatController setTitle(String title) {
        this.title = title;
        return this;
    }

    public FloatController setCover(int coverRes) {
        controllerView.findViewById(R.id.iv_cover).setBackgroundResource(coverRes);
        return this;
    }

    @Override
    public void hide() {
        if (mShowing) {
            mShowing = false;
            playProgressButton.hide();
            mTitleView.setVisibility(View.GONE);
            mCloseView.setVisibility(View.GONE);
        }
    }
}
