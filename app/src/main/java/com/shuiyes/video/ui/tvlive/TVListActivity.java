package com.shuiyes.video.ui.tvlive;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.shuiyes.video.R;
import com.shuiyes.video.bean.ListVideo;
import com.shuiyes.video.ui.base.BaseTVListActivity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;

public class TVListActivity extends BaseTVListActivity implements View.OnClickListener {

    public final static String EXTRA = "file";
    private String FileList = "default.list";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA)) {
            FileList = intent.getStringExtra(EXTRA);
        }
        refreshTvlist();
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_tvlist;
    }

    protected void addListVideo(String text){
        if (text.startsWith("tvbus://")) {
            mVideos.add(new ListVideo(text, text, text));
        } else if (text.contains(",")) {
            String[] tmp = text.split(",");
            mVideos.add(new ListVideo(tmp[0], tmp[0], tmp[1]));
        } else {
            mVideos.add(new ListVideo(text, null, null));
        }
    }

    private void refreshTvlist() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    InputStream in = mContext.getAssets().open(FileList);
                    BufferedReader br = new BufferedReader(new InputStreamReader(in));
                    if (FileList.startsWith("film")) {
                        isHLS = false;

                        String text;
                        while ((text = br.readLine()) != null) {
                            if (text.startsWith("##")) {
                                // 注释
                                continue;
                            }

                            addListVideo(text);
                        }
                    }else if (FileList.startsWith("tvlive")) {
                        String text;
                        while ((text = br.readLine()) != null) {
                            if (text.startsWith("##")) {
                                // 注释
                                continue;
                            }

                            addListVideo(text);
                        }
                    } else if (FileList.startsWith("fm")) {
                        isFM = true;

                        String text;
                        while ((text = br.readLine()) != null) {
                            if (text.startsWith("##")) {
                                // 注释
                                continue;
                            }

                            addListVideo(text);
                        }
                    } else if (FileList.endsWith(".dpl")) {
                        String text, url = null;
                        while ((text = br.readLine()) != null) {
                            if (text.startsWith("##")) {
                                // 注释
                                continue;
                            }

                            if (text.contains("*")) {
                                String[] tmp = text.split("\\*");
                                if ("file".equals(tmp[1])) {
                                    url = tmp[2];
                                } else if ("title".equals(tmp[1])) {
                                    mVideos.add(new ListVideo(tmp[2], tmp[2], url));
                                }
                            }
                        }
                    } else if (FileList.endsWith(".m3u")) {
                        String text, title = null, groupTitle = "";
                        while ((text = br.readLine()) != null) {
                            if (text.startsWith("#EXTM3U")) {
                                // head
                                continue;
                            }

                            if (text.startsWith("#EXTINF")) {
                                String[] tmp = text.split(",");
                                title = tmp[tmp.length - 1];
                                if (title.contains("%2") || title.contains("%3")) {
                                    title = URLDecoder.decode(title);
                                }

                                String key = "group-title=\"";
                                if (text.contains(key)) {
                                    String gTitle = text.substring(text.indexOf(key) + key.length(), text.lastIndexOf("\""));
                                    if (!groupTitle.equals(gTitle)) {
                                        groupTitle = gTitle;

                                        mVideos.add(new ListVideo("", null, null));
                                        mVideos.add(new ListVideo(groupTitle, null, null));
                                    }
                                }
                            } else if (title != null) {
                                mVideos.add(new ListVideo(title, title, text));
                            }
                        }
                    } else {
                        onFailure("更多源加载失败：未知后缀 " + FileList);
                    }
                    br.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure("更多源加载失败：" + e.getLocalizedMessage());
                    return;
                } finally {
                }

                if (mVideos.isEmpty()) {
                    onFailure("更多源加载为空.");
                    return;
                }

                onSuccess();
            }
        }).start();
    }
}