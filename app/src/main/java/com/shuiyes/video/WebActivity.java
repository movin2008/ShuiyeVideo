package com.shuiyes.video;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;

import com.shuiyes.video.base.BaseActivity;
import com.shuiyes.video.util.HttpUtils;
import com.shuiyes.video.util.PlayUtils;
import com.shuiyes.video.util.Utils;
import com.shuiyes.video.widget.Tips;

import java.io.IOException;
import java.io.InputStream;

public class WebActivity extends BaseActivity implements View.OnClickListener {

    private final String TAG = this.getClass().getSimpleName();

    private EditText mEditText;
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

        String url = getIntent().getStringExtra("url");

        mEditText = (EditText) this.findViewById(R.id.et_url);
        mEditText.setText(url);

        mWebView = (WebView) findViewById(R.id.webview);

        WebSettings settings = mWebView.getSettings();

        //url.contains("m.iqiyi.com")
        if(url.contains("m.youku.com")){
            settings.setUserAgentString(HttpUtils.UA_WX);
        }else{
            settings.setUserAgentString(HttpUtils.UA_WIN);
        }
        Log.e(TAG, settings.getUserAgentString());
        settings.setJavaScriptEnabled(true);

        mWebView.loadUrl(url);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                mCssUrl = null;
                mOverlayCss = false;

                Log.e(TAG, "shouldOverrideUrlLoading " + url);

                mEditText.setText(url);
                view.loadUrl(url);

                // 返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                return true;
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {

                // 爱奇艺没有高亮状态

                try{
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
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }


//                if (url.contains("iqiyi") && (url.endsWith(".css") || url.endsWith(".jpg"))) {
////                    Log.e(TAG, "shouldInterceptRequest " + url);
//                    try {
//                        if(TextUtils.isEmpty(mCssUrl)){
//                            Log.e(TAG, "load webkit_tap_highlight.css");
//                            mCssUrl = url;
//
//                            return new WebResourceResponse("text/css", "utf-8", mContext.getAssets().open("css/webkit_tap_highlight.css"));
//                        }else{
//                            if(!mOverlayCss){
//                                mOverlayCss = true;
////                                return new WebResourceResponse("text/css", "utf-8", HttpUtils.openInputStream(mCssUrl));
//                            }
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }

                return super.shouldInterceptRequest(view, url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {

//                view.loadUrl("javascript:(function() {" +
//                        "var parent = document.getElementsByTagName('head').item(0);" +
//                        "var style = document.createElement('style');" +
//                        "style.type = 'text/css';" +
//                        "style.innerHTML = window.atob('html{-webkit-tap-highlight-color: grey;}');" +
//                        "parent.appendChild(style)" +
//                        "})()");

                super.onPageFinished(view, url);
            }
        });
    }

    private boolean mOverlayCss = false;
    private String mCssUrl = null;

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(event.getAction() == KeyEvent.ACTION_UP){
            return super.dispatchKeyEvent(event);
        }
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_BACK:
                long time = System.currentTimeMillis();
                if ((time - mPrevBackTime) < 2000) {
                    finish();
                } else {
                    if(mWebView.canGoBack()){
                        mWebView.goBack();
                    }else{
                        Tips.show(this, "再按一次返回", 0);
                        mPrevBackTime = time;
                    }
                }
                return false;
            case KeyEvent.KEYCODE_DEL:
            case KeyEvent.KEYCODE_MENU:
                Log.e(TAG, "Refresh requestFocus");
                mRefresh.requestFocus();
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
//                time = System.currentTimeMillis();
//                if ((time - mPrevBackTime) < 500) {
//                    mWebView.requestFocus();
//                    Log.e(TAG, "WebView  requestFocus");
//                }
//                mPrevBackTime = time;
                if(!mWebView.hasFocus()){
                    Log.e(TAG, "WebView requestFocus");
                    mWebView.requestFocus();
                }
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                break;
            default:
//                Tips.show(this, "onKeyDown=" + keyCode, 0);
                Log.e(TAG, event.toString());
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
                mWebView.reload();
                break;
            case R.id.btn_play:
                PlayUtils.play(this, mWebView.getUrl(), "播放网页");
                break;
        }
    }
}
