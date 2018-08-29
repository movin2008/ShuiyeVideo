package com.shuiyes.video;

import java.util.ArrayList;
import java.util.List;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.shuiyes.video.adapter.AlbumAdapter;
import com.shuiyes.video.bean.Album;
import com.shuiyes.video.bean.ListVideo;
import com.shuiyes.video.constants.ResourceDef;
import com.shuiyes.video.util.HttpUtils;
import com.shuiyes.video.util.YoukuUtils;
import com.shuiyes.video.widget.Tips;
import com.zhy.view.flowlayout.TagFlowLayout;

public class MainActivity extends Activity {

    private String[] mPermissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };


    public static final int MSG_LIST_ALBUM = 0;
    public static final int MSG_SET_IMAGE = 1;
    public static final int MSG_CHECK_PERMISSION = 2;

    Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 100:
                    View view = getWindow().getDecorView().findFocus();
                    int focusId = 0;
                    if (view != null) {
                        focusId = view.getId();
                    }
                    Log.i("HAHA", "id = 0x" + Integer.toHexString(focusId));
                    mHandler.sendEmptyMessageDelayed(100, 1111);
                    break;
                case MSG_LIST_ALBUM:
                    mPosition = -1;
                    mAlbumAdapter.listAlbums(mAlbums);
                    break;
                case MSG_SET_IMAGE:
                    // 通过tag找到ImageView
                    ImageView imageView = (ImageView) mListView.findViewWithTag(msg.getData().getString("imageUrl"));
                    if (imageView != null) {
                        imageView.setImageBitmap((Bitmap) msg.obj);
                    }
                    break;
                case MSG_CHECK_PERMISSION:
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                        List<String> permissions = new ArrayList<String>();
//                        for (int i = 0; i < mPermissions.length; i++) {
//                            String permission = mPermissions[i];
//                            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
//                                permissions.add(permission);
//                            }
//                        }
//                        if (!permissions.isEmpty()) {
//                            requestPermissions(permissions.toArray(new String[permissions.size()]), 100);
//                        }
//                    }
                    break;
                default:
                    break;
            }
        }

        ;
    };

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        if (requestCode == 100) {
//            for (int i = 0; i < grantResults.length; i++) {
//                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                        //判断是否勾选禁止后不再询问
//                        boolean showRequestPermission = shouldShowRequestPermissionRationale(permissions[i]);
//                        String permission = permissions[i];
//                        if (showRequestPermission) {
//                            Tips.show(mContext, "水也视频需要 "+permission, 0);
//                            finish();
//                        }else{
//                            requestPermissions(new String[]{permission}, 100);
//                        }
//                    }
//                }
//            }
//        }
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    }

    private Context mContext;
    private ListView mListView;
    private EditText mSearch;
    private AlbumAdapter mAlbumAdapter;
    private int mPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.e("HAHA", "onCreate ========================= ");

        mContext = this;
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
                if (TextUtils.isEmpty(keyword)) {
                    mAlbums.clear();
                    mHandler.sendEmptyMessage(MSG_LIST_ALBUM);
                    return;
                }

                search(keyword);
            }
        });

        mSearch.setText("wangwangduilidagong");
//		TextView test = (TextView) this.findViewById(R.id.test);
//		test.setText(R.string.app_name);

//		Tips.show(this, Build.VERSION.SDK_INT+"/"+Build.MANUFACTURER, 1);

//        mHandler.sendEmptyMessageDelayed(100, 1111);
    }


    @Override
    protected void onResume() {
        super.onResume();
        mHandler.sendEmptyMessage(MSG_CHECK_PERMISSION);
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        mHandler.removeMessages(100);
    }

    private void search(final String keyword) {
        new Thread(new Runnable() {

            @Override
            public void run() {

                try {
                    String result = YoukuUtils.search(keyword);
//					Log.e("HAHA", result);

                    if (TextUtils.isEmpty(result)) {
                        return;
                    }

                    listAlbums(result);
                    Log.e("HAHA", "mAlbums ===== " + mAlbums.size());

                    if (keyword.equals(mSearchText)) {
                        mHandler.sendEmptyMessage(MSG_LIST_ALBUM);
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


            }
        }).start();
    }

    private String mSearchText;
    private List<Album> mAlbums = new ArrayList<Album>();

    private void listAlbums(String result) throws Exception {
        String start = "<div class=\\\"sk-mod\\\">";
        mAlbums.clear();
        int flag = 1;
        while (result.contains(start)) {
            int startIndex = result.indexOf(start);
            int endIndex = result.indexOf(start, startIndex + start.length());
            String data = null;
            if (endIndex != -1) {
                data = result.substring(startIndex, endIndex);
            } else {
                data = result.substring(startIndex);
            }
//			Log.e("HAHA", "data ===== "+data);


            String key = "data-spm=\\\"dtitle\\\" title=\\\"";
            int len = data.indexOf(key);
            String tmp = data.substring(len + key.length());

            len = tmp.indexOf("\\\"");
            String albumTitle = tmp.substring(0, len);
//			Log.e("HAHA", "albumTitle ==================== "+albumTitle);

            if (TextUtils.isEmpty(albumTitle)) {
//				Log.e("HAHA", "albumTitle ?????????????? "+tmp);

                len = tmp.indexOf(">");
                albumTitle = Html.fromHtml(tmp.substring(len + 1, tmp.indexOf("</a>"))).toString();
//				albumTitle = tmp.substring(len+1, tmp.indexOf("</a>"));
            }

            key = "href=\\\"";
            len = data.indexOf(key);
            data = data.substring(len + key.length());

            len = data.indexOf("\\\"");
            String albumUrl = HttpUtils.FormateUrl(data.substring(0, len));
//            Log.e("HAHA", flag + " albumUrl ===================== " + albumUrl);


            String albumSummary = "暂无简介";
            key = "<label>简介:</label>";
            len = data.indexOf(key);
            if (len != -1) {
                tmp = data.substring(len + key.length());

                len = tmp.indexOf("</span>\\n\\t");
                albumSummary = tmp.substring(0, len);
//				Log.e("HAHA", "albumSummary ===================== "+albumSummary);
            }


            key = "\\n\\t\\t<img";
            len = data.indexOf(key);
            tmp = data.substring(len + key.length());

            key = "src=\\\"";
            if (tmp.indexOf("alt=\\\"") != -1 && tmp.indexOf("alt=\\\"") < 10) {
                key = "alt=\\\"";
            }
            len = tmp.indexOf(key);
            tmp = tmp.substring(len + key.length());

            len = tmp.indexOf("\\\"");
            String albumImg = HttpUtils.FormateUrl(tmp.substring(0, len));
//			Log.e("HAHA", flag+" albumImg ===================== "+albumImg);


            String titleKey = "<a title=\\\"";
            int prev = 0;

            List<ListVideo> listVideos = new ArrayList<ListVideo>();
            while (data.contains(titleKey)) {
                len = data.indexOf(titleKey);
                data = data.substring(len + titleKey.length());

                len = data.indexOf("\\\"");
                String name = data.substring(0, len);
                if ("查看更多".equals(name)) {
                    continue;
                }

                key = "href=\\\"";
                len = data.indexOf(key);
                data = data.substring(len + key.length());

                len = data.indexOf("\\\"");
                String url = data.substring(0, len);

                key = ">";
                len = data.indexOf(key);
                data = data.substring(len + key.length());

                len = data.indexOf("</a>");
                String index = data.substring(0, len);


                int id = 0;
                try {
                    id = Integer.parseInt(index);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                if (id != 0 && id - prev != 1) {
                    break;
                } else if (id == 0) {
//                    Log.e("HAHA", "4 ?????????????? " + name);
//                    Log.e("HAHA", "5 ??????????????  " + url);
//                    Log.e("HAHA", "6 ?????????????? " + index);
                    break;
                }
                prev = id;

                ListVideo listVideo = new ListVideo(id, name, url);
                listVideos.add(listVideo);

//				Log.e("HAHA", "++++++++++++++++ "+listVideo);
            }
            result = result.substring(result.indexOf(start) + start.length());

            if(albumUrl.contains("youku.com") && !albumUrl.contains("list.youku.com")){
                mAlbums.add(new Album(flag++, albumTitle, albumSummary, albumImg, albumUrl, listVideos));
            }else{
                Log.e("HAHA", listVideos.size()+", 暂不支持播放 " + albumUrl);
            }
        }
    }

    private void playVideo(int position) {
        Album album = mAlbums.get(position);
        YoukuUtils.playUrl(mContext, album.getPlayurl(), album.getTitle());
    }

    private long mPrevBackTime;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        /**
         public static final int KEYCODE_DPAD_UP = 19;
         public static final int KEYCODE_DPAD_DOWN = 20;
         public static final int KEYCODE_DPAD_LEFT = 21;
         public static final int KEYCODE_DPAD_RIGHT = 22;
         public static final int KEYCODE_DPAD_CENTER = 23;
         */

        Log.e("HAHA", "onKeyDown=" + keyCode);


        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                long time = System.currentTimeMillis();
                if (time - mPrevBackTime < 2000) {
                    finish();
                } else {
                    Tips.show(this, "再按一次退出应用", 0);
                }
                mPrevBackTime = time;
                return false;
            default:
                break;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP) {
            return super.dispatchKeyEvent(event);
        }
        Log.e("HAHA", "dispatchKeyEvent=" + event.getKeyCode());

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                Toast.makeText(this, "settings", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
