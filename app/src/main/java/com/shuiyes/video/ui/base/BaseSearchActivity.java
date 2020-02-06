package com.shuiyes.video.ui.base;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.shuiye.video.util.ResourceDef;
import com.shuiyes.video.R;
import com.shuiyes.video.adapter.AlbumAdapter;
import com.shuiyes.video.bean.AlbumList;
import com.shuiyes.video.ui.SettingsActivity;
import com.shuiyes.video.util.Constants;
import com.shuiyes.video.util.PreferenceUtil;
import com.zhy.view.flowlayout.TagFlowLayout;

public abstract class BaseSearchActivity extends BaseActivity {

    protected ListView mListView;
    protected EditText mSearch;
    protected TextView mNotice;
    protected AlbumAdapter mAlbumAdapter;
    protected int mPosition = -1;
    private InputMethodManager mInputMethodManager = null;
    protected AsyncTask<String, Integer, Boolean> mSearchAsyncTask = null;

    protected static final int MSG_SHOW_NOTICE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Log.e(TAG, "onCreate ========================= ");

        mInputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);

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

        mNotice = (TextView) this.findViewById(R.id.tv_notice);
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

                if (mSearchAsyncTask != null) {
                    mSearchAsyncTask.cancel(false);
                }

                if (TextUtils.isEmpty(keyword)) {
                    mAlbums.clear();
                    mHandler.sendEmptyMessage(Constants.MSG_LIST_ALBUM);
                    return;
                }

                notice("searchVideos=" + keyword);
                searchVideos(keyword);

                PreferenceUtil.setSearchKeywords(mContext, keyword);
            }
        });

//        mHandler.post(mTextRunnable);

        mSearch.setText(PreferenceUtil.getSearchKeywords(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                this.startActivity(new Intent(this, SettingsActivity.class));
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private Runnable mTextRunnable = new Runnable() {
        @Override
        public void run() {
            mHandler.postDelayed(mTextRunnable, 5555);

            View focusView = getWindow().getDecorView().findFocus();

            Log.e(TAG, "" + focusView);
        }
    };

    @Override
    public void handleOtherMessage(Message msg) {
        switch (msg.what) {
            case Constants.MSG_LIST_ALBUM:

                mNotice.setText("");
                if (mAlbums.isEmpty()) {
                    mNotice.setVisibility(View.VISIBLE);
                } else {
                    mNotice.setVisibility(View.GONE);
                }

                mPosition = -1;
                mAlbumAdapter.listAlbums(mAlbums);

                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        clearFocus(findViewById(R.id.btn_clear_focus));
                    }
                }, 999);
                break;
            case Constants.MSG_SET_IMAGE:
                // 通过tag找到ImageView
                ImageView imageView = (ImageView) mListView.findViewWithTag(msg.getData().getString("imageUrl"));
                if (imageView != null) {
                    imageView.setImageBitmap((Bitmap) msg.obj);
                }
                break;
            case MSG_SHOW_NOTICE:
                mNotice.setText(mNotice.getText() + "\n" + (String) msg.obj);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

//        if(mInputMethodManager.isActive(mSearch)){
//            mInputMethodManager.hideSoftInputFromWindow(mSearch.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
//        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    protected String mSearchText;
    protected AlbumList mAlbums = new AlbumList();
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

    public void clearFocus(View view) {
//        if(mInputMethodManager.isActive(mSearch)){
//            mInputMethodManager.hideSoftInputFromWindow(mSearch.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
//        }else{
//            mSearch.requestFocus();
//            //自动弹出键盘
//            mInputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
//        }

//        if(mInputMethodManager.isActive(mSearch)){
//            mInputMethodManager.hideSoftInputFromWindow(mSearch.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
//        }

        mSearch.clearFocus();
        view.requestFocus();
//        mInputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    protected void notice(String text) {
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SHOW_NOTICE, text));
        Log.e(TAG, text);
    }

    protected boolean checkHtmlValid(String html) {
        if (TextUtils.isEmpty(html)) {
            notice("Http response is null.");
            return false;
        }

        if (html.startsWith("Exception: ")) {
            notice(html);
            return false;
        }

        return true;
    }
}
