package com.shuiyes.video.ui;

import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
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

import com.shuiyes.video.R;
import com.shuiyes.video.ui.base.BaseActivity;
import com.shuiyes.video.util.Constants;
import com.shuiyes.video.util.HttpUtils;
import com.shuiyes.video.util.PlayUtils;
import com.shuiyes.video.util.Utils;
import com.shuiyes.video.util.VipUtil;

import java.io.InputStream;

public class WebActivity extends BaseActivity implements View.OnClickListener {

    private final String TAG = this.getClass().getSimpleName();

    public static void launch(Context context, String url){
        context.startActivity(new Intent(context, WebActivity.class).putExtra("url", url));
    }

    private TextView mTitle, mUrl, mProgress;
    private WebView mWebView;
    protected Button mBack, mForward, mRefresh, mPlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        mBack = (Button) findViewById(R.id.btn_back);
        mBack.setOnClickListener(this);
        mForward = (Button) findViewById(R.id.btn_forward);
        mForward.setOnClickListener(this);
        mRefresh = (Button) findViewById(R.id.btn_refresh);
        mRefresh.setOnClickListener(this);
        mPlay = (Button) findViewById(R.id.btn_play);
        mPlay.setOnClickListener(this);

        Intent intent = getIntent();
        String url = intent.getStringExtra("url");

        mProgress = (TextView) this.findViewById(R.id.tv_progress);
        mTitle = (TextView) this.findViewById(R.id.tv_title);
        mUrl = (TextView) this.findViewById(R.id.tv_url);
        mUrl.setText(url);

        if (VipUtil.isVipPojie(url)) {
            mTitle.setText("VIP 视频破解");
            mBack.setVisibility(View.GONE);
            mForward.setVisibility(View.GONE);
            mPlay.setVisibility(View.GONE);
            mUrl.setVisibility(View.GONE);
        }

        mWebView = (WebView) findViewById(R.id.webview);

        WebSettings settings = mWebView.getSettings();

        //url.contains("m.iqiyi.com")
        if (url.contains("m.youku.com")) {
            settings.setUserAgentString(HttpUtils.UA_WX);
        } else {
            settings.setUserAgentString(HttpUtils.UA_WIN);
        }
        Log.e(TAG, settings.getUserAgentString());
        settings.setJavaScriptEnabled(true);
        // chromium: [INFO:CONSOLE(1)] "Uncaught TypeError: Cannot read property 'getItem' of null"
        settings.setDomStorageEnabled(true);

        if (Build.VERSION.SDK_INT >= 19) {
            settings.setLoadsImagesAutomatically(true);
        } else {
            settings.setLoadsImagesAutomatically(false);
        }

        mWebView.loadUrl(url);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.e(TAG, "shouldOverrideUrlLoading " + url);

                mUrl.setText(url);

//                view.loadUrl("about:blank");
                view.loadUrl(url);

                // 返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                return true;
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {

                // 爱奇艺没有高亮状态

                try {
//                    if(url.equals("https://www.iqiyipic.com/common/fix/site-v4/sprite-headLogo-index.png")){
//                        return new WebResourceResponse("text/css", "utf-8", mContext.getAssets().open("css/test.css"));
//                    }

//                    Log.e(TAG, "shouldInterceptRequest " + url);
                    if (url.startsWith("https://stc.iqiyipic.com/gaze/uniqy/main/css/")) {
                        InputStream in = Utils.isTransparentHighlightCss(mContext, url);
                        if (in != null) {
                            Log.e(TAG, "inject webkit_tap_highlight.css");
                            return new WebResourceResponse("text/css", "utf-8", in);
                        }
                    } else if (url.endsWith("index.m3u8")) {
                        mockWebViewClick();
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

//                view.loadUrl("javascript:(function() {" +
//                        "var parent = document.getElementsByTagName('head').item(0);" +
//                        "var style = document.createElement('style');" +
//                        "style.type = 'text/css';" +
//                        "style.innerHTML = window.atob('html{-webkit-tap-highlight-color: grey;}');" +
//                        "parent.appendChild(style)" +
//                        "})()");

                mUrl.setText(view.getUrl());
                mTitle.setText(view.getTitle());

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
    }

    @Override
    protected void onResume() {
        super.onResume();

        Constants.WEB_FOEGROUND = true;
    }

    @Override
    protected void onPause() {
        super.onPause();

        Constants.WEB_FOEGROUND = false;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP) {
            return super.dispatchKeyEvent(event);
        }
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_BACK:
                if (mWebView.canGoBack()) {
                    mWebView.goBack();
                } else {
                    return super.dispatchKeyEvent(event);
                }
                return false;
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
            case KeyEvent.KEYCODE_DPAD_DOWN:
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                break;
            default:
                break;
        }

        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                mWebView.goBack();
                break;
            case R.id.btn_forward:
                mWebView.goForward();
                break;
            case R.id.btn_refresh:
                mockWebViewClicked = false;
                mWebView.reload();
                break;
            case R.id.btn_play:
                PlayUtils.play(this, mWebView.getUrl(), mWebView.getTitle());
                break;
        }
    }

    private boolean mockWebViewClicked;

    private void mockWebViewClick() {
        if (mockWebViewClicked) return;
        mockWebViewClicked = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(3333);
                Display display = getWindowManager().getDefaultDisplay();
                DisplayMetrics dm = new DisplayMetrics();
                display.getMetrics(dm);

                Log.e(TAG, "mockWebViewClick. " + dm.widthPixels + "x" + dm.heightPixels);

                long time = SystemClock.uptimeMillis();
                Instrumentation inst = new Instrumentation();
                inst.sendPointerSync(MotionEvent.obtain(time, time, MotionEvent.ACTION_DOWN, dm.widthPixels / 2, dm.heightPixels / 2, 0));
                inst.sendPointerSync(MotionEvent.obtain(time, time, MotionEvent.ACTION_UP, dm.widthPixels / 2, dm.heightPixels / 2, 0));
            }
        }).start();
    }
}
