package com.shuiyes.video.base;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.shuiyes.video.R;
import com.shuiyes.video.adapter.AlbumAdapter;
import com.shuiyes.video.bean.Album;
import com.shuiyes.video.constants.ResourceDef;
import com.shuiyes.video.util.Constants;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.List;

public abstract class SearchActivity extends BaseActivity {

    private final String TAG = this.getClass().getSimpleName();

    protected ListView mListView;
    protected EditText mSearch;
    protected AlbumAdapter mAlbumAdapter;
    protected int mPosition = -1;

    protected AsyncTask<String, Integer, Boolean> mSearchAsyncTask = null;

    @Override
    public void handleOtherMessage(Message msg) {
        switch (msg.what) {
            case Constants.MSG_LIST_ALBUM:
                mPosition = -1;
                mAlbumAdapter.listAlbums(mAlbums);
                break;
            case Constants.MSG_SET_IMAGE:
                // 通过tag找到ImageView
                ImageView imageView = (ImageView) mListView.findViewWithTag(msg.getData().getString("imageUrl"));
                if (imageView != null) {
                    imageView.setImageBitmap((Bitmap) msg.obj);
                }
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youku_so);

        Log.e(TAG, "onCreate ========================= ");

        mAlbumAdapter = new AlbumAdapter(this, mHandler);
        mListView = (ListView) this.findViewById(R.id.lv_result);
        mListView.setItemsCanFocus(true);
        mListView.setFocusable(true);
        mListView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mPosition = position;

                if (mListView.getLastVisiblePosition() == position || mListView.getFirstVisiblePosition() == position) {
//            		mListView.scrollListBy(view.getHeight());
                    mListView.setSelection(position);
                }

                int size = parent.getCount();
//            	for (int i = 0; i < size; i++) {
//            		View child = parent.getChildAt(i);
//            		if(child != null){
//            			child.setBackgroundResource(R.drawable.btn_rect_transparent);
//            		}
//                }

                TagFlowLayout album = (TagFlowLayout) view.findViewById(R.id.album_list);
                size = album.getChildCount();
                if (size > 0) {
                    for (int i = 0; i < size; i++) {
                        album.getChildAt(i).setSelected(false);
                    }

                    View focusView = getWindow().getDecorView().findFocus();
                    if (focusView != null && focusView.getId() == (ResourceDef.ID_SEARCH_VIDEO + position)) {
                        album.getChildAt(0).requestFocus();
                    }
                } else {
//                	view.setBackgroundResource(R.drawable.rect_blue);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        // 焦点给子view后无效
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                playVideo(position);
            }
        });
        mListView.setAdapter(mAlbumAdapter);


        mSearch = (EditText) this.findViewById(R.id.et_seach);
        mSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                final String keyword = s.toString();
                mSearchText = keyword;

                if(mSearchAsyncTask != null){
                    mSearchAsyncTask.cancel(false);
                }

                if (TextUtils.isEmpty(keyword)) {
                    mAlbums.clear();
                    mHandler.sendEmptyMessage(Constants.MSG_LIST_ALBUM);
                    return;
                }

                searchVideos(keyword);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    protected String mSearchText;
    protected List<Album> mAlbums = new ArrayList<Album>();
    protected Object LOCK = new Object();

    protected abstract void searchVideos(String keyword);
    protected abstract void playVideo(int position);

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP) {
            return super.dispatchKeyEvent(event);
        }

        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_ENTER:
            case KeyEvent.KEYCODE_DPAD_CENTER:
                View focusView = getWindow().getDecorView().findFocus();
                if (focusView != null && focusView.getId() == (ResourceDef.ID_SEARCH_VIDEO + mPosition)) {
                    playVideo(mPosition);
                    return false;
                }
            default:
                break;
        }

        return super.dispatchKeyEvent(event);
    }

    public void clearFocus(View view){
        mSearch.clearFocus();
        view.requestFocus();
    }

}
