package com.shuiyes.video.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager.LayoutParams;

import com.shuiyes.video.R;
import com.zhy.view.flowlayout.TagFlowLayout;


public class FullScreenDialog extends Dialog implements View.OnClickListener {

    private Context mContext = null;
    protected TagFlowLayout mView;


    public FullScreenDialog(Context context) {
        super(context, R.style.FullScreenDialog);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_screen_dialog);
        mView = (TagFlowLayout) this.findViewById(R.id.root);
    }

    @Override
    public void show() {
        super.show();

        final LayoutParams layoutParams = getWindow().getAttributes();
        // align bottom
        layoutParams.gravity = Gravity.BOTTOM;
        // fullscreen
        layoutParams.width = LayoutParams.MATCH_PARENT;
        layoutParams.height = LayoutParams.WRAP_CONTENT;

        getWindow().getDecorView().setPadding(0, 0, 0, 0);
        getWindow().setAttributes(layoutParams);

        View firstChild = mView.getChildAt(0);
        if(firstChild != null){
            firstChild.requestFocus();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
        dismiss();
    }

    protected View.OnClickListener mListener;

    public void setOnClickListener(View.OnClickListener l) {
        mListener = l;
    }

}
