package com.shuiyes.video.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.shuiyes.video.R;
import com.shuiyes.video.ui.base.BaseActivity;
import com.shuiyes.video.util.PreferenceUtil;
import com.shuiyes.video.util.Utils;
import com.shuiyes.video.ui.youku.YoukuUtils;
import com.shuiyes.video.widget.Tips;

import org.json.JSONObject;

import java.util.Date;

public class SettingsActivity extends BaseActivity {

    private Spinner mSpinner;
    private EditText mEditText;
    private TextView mTextView;

    private final String[] ccodes = SVApplication.getAppContext().getResources().getStringArray(R.array.CCODE);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mTextView = (TextView) this.findViewById(R.id.tv_text);
        mEditText = (EditText) this.findViewById(R.id.et_ccode);
        mEditText.setOnEditorActionListener(new EditText.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Log.e("HAHA", "actionId=" + actionId);
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                    InputMethodManager imm = (InputMethodManager) v.getContext()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    return true;
                }
                return false;
            }

        });

        mSpinner = (Spinner) this.findViewById(R.id.spin_ccode);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String ccode = ccodes[i];
                updateCCODE(ccode);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


        updateCCODE(PreferenceUtil.getCCODE(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP) {
            return super.dispatchKeyEvent(event);
        }
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_BACK:
                if (threadRunning) {
                    Tips.show(this, "请等待测试结束...");
                    return true;
                } else {
                    return super.dispatchKeyEvent(event);
                }
        }

        return super.dispatchKeyEvent(event);
    }

    private void updateCCODE(String ccode) {
        if (!ccode.equals(YoukuUtils.CCODE)) {
            YoukuUtils.CCODE = ccode;
            PreferenceUtil.setCCODE(this, ccode);
        }
        mEditText.setText(ccode);

        int tmp = -1;
        for (int i = 0; i < ccodes.length; i++) {
            if (ccodes[i].equals(ccode)) {
                tmp = i;
                break;
            }
        }

        final int index = tmp;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (index != -1) {
                    mSpinner.setSelection(index);
                    mSpinner.setVisibility(View.VISIBLE);
                } else {
                    mSpinner.setVisibility(View.GONE);
                }
            }
        });
    }

    public void systeminfo(View view) {
        String text = Utils.getAndroidInc()
                + "\n型号：" + Build.MODEL
                + "\n主板：" + Build.BOARD
                + "\n设备：" + Build.DEVICE
                + "\n产品：" + Build.PRODUCT
                + "\n制造商：" + Build.BRAND
                + "\nCPU核数：" + Build.CPU_ABI
                + "\nCPU型号：" + Utils.getCpuInc()
                + "\n编译用户：" + Build.USER
                + "\n编译版本：" + Build.ID
                + "\n内核版本：" + Utils.getLinuxCoreVer()
                + "\n编译时间：" + new Date(Build.TIME)
                + "\n内存：" + Utils.getMemoryInfo(this)
                + "\n存储：" + Utils.getStorageInfo(this)
                + "\n屏幕尺寸：" + Utils.getDisplayMetrics(this)
                + "\n开机时间：" + Utils.elapsedRealtime();

        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
        dialog.setTitle("系统信息");
        dialog.setMessage(text);
        dialog.show();
    }

    void text(final String text) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mTextView.setText(mTextView.getText() + text);
            }
        });
    }

    boolean threadRunning;

    public void testAllCCode(View view) {
        if (threadRunning) return;
        threadRunning = true;
        mTextView.setText("获取优酷鉴权CNA...");
        new Thread(new Runnable() {
            @Override
            public void run() {

                String cna = YoukuUtils.fetchCna();
                if (TextUtils.isEmpty(cna)) {
                    text("[失败]\n请稍后重试...");
                    return;
                }

                text("[成功]\n");

                for (int i = 1; i < 41; ) {
                    String ccode = "0" + (500 + i);
                    text("测试" + ccode + "...");
                    String ret;
                    text(ret = testCCode(ccode, cna));
                    if (!ret.startsWith("[Exception:")) {
                        i++;
                    }
                }

                threadRunning = false;
            }
        }).start();
    }

    public void testCCode(View view) {
        if (threadRunning) return;
        threadRunning = true;

        mTextView.setText("获取优酷鉴权CNA...");
        new Thread(new Runnable() {
            @Override
            public void run() {

                String cna = YoukuUtils.fetchCna();
                if (TextUtils.isEmpty(cna)) {
                    text("[失败]\n请稍后重试...");
                    return;
                }

                String ccode = mEditText.getText().toString();
                text("[成功]\n测试" + ccode + "...");
                String ret;
                text(ret = testCCode(ccode, cna));
                if (!ret.startsWith("[Exception:")) {
                    updateCCODE(ccode);
                }

                threadRunning = false;
            }
        }).start();
    }

    private String testCCode(String ccode, String cna) {
        try {
            Thread.sleep(567);

            String html = YoukuUtils.testCCode(ccode, cna);

            if (TextUtils.isEmpty(html)) {
                return "[Exception: HttpResponse is null]\n";
            }
            if (html.startsWith("Exception:")) {
                return "[" + html + "]\n";
            }

            JSONObject data = new JSONObject(html).getJSONObject("data");
            if (data.has("error")) {
                return "[" + data.getJSONObject("error").getString("note") + "]\n";
            }

            return "[有效]\n";
        } catch (Exception e) {
            e.printStackTrace();
            return "[Exception: " + e.getLocalizedMessage() + "]\n";
        }
    }
}
