package com.shuiyes.video;

import com.shuiyes.video.util.YoukuUtils;

import android.app.Activity;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
        //  http://v.youku.com/v_show/id_XMTQ3NzgxNjgwMA==.html";
//        this.startActivity(new Intent(this, PlayActivity.class).putExtra("vid", "XMTQ3NzgxNjgwMA==").putExtra("title", "汪汪立功队"));
		
		
		
		
		ListView lv = (ListView) this.findViewById(R.id.lv_result);
		
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {

				try {
					String result = YoukuUtils.search("汪汪队立大功");
					Log.e("HAHA", result);

					if(!TextUtils.isEmpty(result) && result.contains("data-spm=\\\"dtitle\\\" title=\\\"")){
						String key1 = "data-spm=\\\"dtitle\\\" title=\\\"";
						int len = result.indexOf(key1);
						String tmp = result.substring(len+key1.length());
						
						len = result.indexOf("\\\"");
						
						Log.e("HAHA", tmp.substring(len));
						
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
						
			}
		}).start();
		
		
	}

}
