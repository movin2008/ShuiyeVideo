package com.shuiyes.video.ui;

import android.app.Instrumentation;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.shuiyes.video.R;
import com.shuiyes.video.bean.PlayVideo;
import com.shuiyes.video.dialog.MiscDialog;
import com.shuiyes.video.ui.base.BaseActivity;
import com.shuiyes.video.util.HttpUtils;
import com.shuiyes.video.util.SLog;
import com.shuiyes.video.widget.MiscView;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class VipActivity extends BaseActivity implements View.OnClickListener {

    private final String TAG = this.getClass().getSimpleName();

    public static void launch(Context context, String url) {
        context.startActivity(new Intent(context, VipActivity.class).putExtra("url", url));
    }

    private TextView mTitle, mProgress;
    private WebView mWebView;
    protected Button mRefresh, mSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip);

        mSourceList.add(new PlayVideo("administratorw.com", "https://www.administratorw.com/video.php?url="));
        mSourceList.add(new PlayVideo("mt2t.com", "http://mt2t.com/lines?url="));
        mSourceList.add(new PlayVideo("vipjiexi.com", "http://vipjiexi.com/yun.php?url="));
        mSourceList.add(new PlayVideo("vipvideo.github.io", "http://vipvideo.github.io/lines?url="));

        mRefresh = (Button) findViewById(R.id.btn_refresh);
        mRefresh.setOnClickListener(this);
        mSource = (Button) findViewById(R.id.btn_source);
        mSource.setOnClickListener(this);
        mSource.setOnLongClickListener(new View.OnLongClickListener(){

            @Override
            public boolean onLongClick(View view) {
                if(TextUtils.isEmpty(mPlayUrl)){
                    Toast.makeText(mContext, "暂无播放地址", 0).show();
                }else{
                    ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    cm.setPrimaryClip(ClipData.newPlainText("Label", mPlayUrl));
                    Toast.makeText(mContext, "播放地址已复制", 0).show();
                }
                return true;
            }
        });

        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        if (!TextUtils.isEmpty(url)) {
            mIntentUrl = url;
        }

        mProgress = (TextView) this.findViewById(R.id.tv_progress);
        mTitle = (TextView) this.findViewById(R.id.tv_title);
        mTitle.setText("VIP视频破解");

        mWebView = (WebView) findViewById(R.id.webview);

        WebSettings settings = mWebView.getSettings();
        Log.e(TAG, settings.getUserAgentString());
        settings.setJavaScriptEnabled(true);
        // chromium: [INFO:CONSOLE(1)] "Uncaught TypeError: Cannot read property 'getItem' of null"
        settings.setDomStorageEnabled(true);

        if (Build.VERSION.SDK_INT >= 19) {
            settings.setLoadsImagesAutomatically(true);
        } else {
            settings.setLoadsImagesAutomatically(false);
        }

        mWebView.loadUrl(mSourceList.get(0).getUrl() + mIntentUrl);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.e(TAG, "shouldOverrideUrlLoading " + url);

//                view.loadUrl("about:blank");
                view.loadUrl(url);

                return true;
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                try {
                    if (url.endsWith(".m3u8") || url.contains(".mp4?")) {
                        mPlayUrl = url;
                        mockWebViewClick();
                    }else if(url.contains("zyzjpx.cn")){
                        // 广告
                        return new WebResourceResponse("text/css", "utf-8", null);
                    }else if(url.contains("google-analytics.com/") || url.contains("cnzz.com/")){
                        // google-analytics
                        return new WebResourceResponse("text/css", "utf-8", null);
                    }else{
                        SLog.e(TAG, "shouldInterceptRequest " + url);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return super.shouldInterceptRequest(view, url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {

                if (!view.getSettings().getLoadsImagesAutomatically()) {
                    view.getSettings().setLoadsImagesAutomatically(true);
                }

                Toast.makeText(getApplicationContext(), "线路名称："+view.getTitle(), 0).show();

                super.onPageFinished(view, url);
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
//                Log.e(TAG, "onProgressChanged " + newProgress);
                mProgress.setText(newProgress + "%");
                super.onProgressChanged(view, newProgress);
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                String html = HttpUtils.get(mIntentUrl);

                if(TextUtils.isEmpty(html)) return;
                int i1 = html.indexOf("<title>");
                int i2 = html.indexOf("</title>");
                if(i1 > 0 && i2 > 0){
                    mTitle.setText("VIP视频破解 - " + html.substring(i1 + 7, i2));
                }
            }
        }).start();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP) {
            return super.dispatchKeyEvent(event);
        }
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_DEL:
            case KeyEvent.KEYCODE_MENU:
                Log.e(TAG, "Refresh requestFocus");
                mRefresh.requestFocus();
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                if (!mWebView.hasFocus()) {
                    Log.e(TAG, "WebView requestFocus");
                    mWebView.requestFocus();
                }
                break;
            default:
                break;
        }

        return super.dispatchKeyEvent(event);
    }

    private MiscDialog mSourceDialog;
    private List<PlayVideo> mSourceList = new ArrayList<PlayVideo>();
    // test for 暗战
    private String mIntentUrl = "https://v.qq.com/x/cover/4zhgrc6vcikqw0p/e0017ah5b20.html";
    private String mPlayUrl = "";

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_refresh:
                mockWebViewClicked = false;
                mWebView.reload();
                break;
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

                        PlayVideo playVideo = ((MiscView) view).getPlayVideo();
                        mockWebViewClicked = false;
                        mWebView.loadUrl(playVideo.getUrl() + URLEncoder.encode(mIntentUrl));
                    }
                });
                mSourceDialog.show();
                break;
        }
    }

    private boolean mockWebViewClicked;

    private void mockWebViewClick() {
        if (mockWebViewClicked) return;
        mockWebViewClicked = true;

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Display display = getWindowManager().getDefaultDisplay();
                DisplayMetrics dm = new DisplayMetrics();
                display.getMetrics(dm);
                Toast.makeText(getApplicationContext(), "Mock Click " + dm.widthPixels + "x" + dm.heightPixels + "", 0).show();

                Log.e(TAG, "mockWebViewClick. " + dm.widthPixels + "x" + dm.heightPixels);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        long time = SystemClock.uptimeMillis();
                        Instrumentation inst = new Instrumentation();
                        inst.sendPointerSync(MotionEvent.obtain(time, time, MotionEvent.ACTION_DOWN, dm.widthPixels / 2, dm.heightPixels / 2, 0));
                        inst.sendPointerSync(MotionEvent.obtain(time, time, MotionEvent.ACTION_UP, dm.widthPixels / 2, dm.heightPixels / 2, 0));
                    }
                }).start();

            }
        }, 3333);

    }
}
