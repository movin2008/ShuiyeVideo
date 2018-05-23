package com.shuiyes.video.widget;

import com.shuiyes.video.R;
import com.shuiyes.video.bean.ListVideo;
import com.zhy.view.flowlayout.TagView;

import android.content.Context;

public class NumberView extends TagView {
	
    private String url;
    private String title;
    public NumberView(Context context, ListVideo video)
    {
        super(context);
        
        this.url = video.getUrl();
        this.title = video.getTitle();
        
        this.setText(String.valueOf(video.getId()));
        this.setPadding(0, 0, 0, 0);
        this.setBackgroundResource(R.drawable.btn_rect);
//        this.setGravity(Gravity.CENTER);
    }

    public String getUrl() {
		return url;
	}

	public String getTitle() {
		return title;
	}

}
