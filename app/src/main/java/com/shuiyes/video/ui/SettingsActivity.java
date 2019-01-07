package com.shuiyes.video.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.shuiyes.video.base.BaseActivity;
import com.shuiyes.video.util.Utils;
import com.shuiyes.video.youku.YoukuUtils;

import java.util.Date;

public class SettingsActivity extends BaseActivity {

    private Spinner mSpinner;
    private EditText mEditText;

    private SharedPreferences mPreferences;

    private String[] ccodes = SVApplication.getAppContext().getResources().getStringArray(R.array.CCODE);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        mEditText = (EditText) this.findViewById(R.id.et_ccode);
        mEditText.setOnEditorActionListener(new EditText.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Log.e("HAHA", "actionId="+actionId);
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                    InputMethodManager imm = (InputMethodManager)v.getContext()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                    updateCCODE(mEditText.getText().toString());

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


        updateCCODE(mPreferences.getString("CCODE", YoukuUtils.CCODE));
    }


    private void updateCCODE(String ccode){
        if(!ccode.equals(YoukuUtils.CCODE)){
            YoukuUtils.CCODE = ccode;
            mPreferences.edit().putString("CCODE", ccode).commit();
        }
        mEditText.setText(ccode);

        int index = -1;
        for(int i=0; i<ccodes.length; i++){
            if(ccodes[i].equals(ccode)){
                index = i;
                break;
            }
        }

        if(index != -1){
//            mEditText.setEnabled(false);
//            mSpinner.setEnabled(true);
            mSpinner.setSelection(index);
            mSpinner.setVisibility(View.VISIBLE);
        }else{
//            mSpinner.setEnabled(false);
            mSpinner.setVisibility(View.GONE);
        }
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
}
