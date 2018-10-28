package com.shuiyes.video.ui;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.shuiyes.video.R;
import com.shuiyes.video.base.BaseActivity;
import com.shuiyes.video.youku.YoukuUtils;

import java.util.List;
import java.util.Map;

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
                if (actionId == EditorInfo.IME_ACTION_DONE) {
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
}
