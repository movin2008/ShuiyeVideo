package com.shuiyes.video.ui.qq;

import com.shuiyes.video.bean.PlayVideo;

public class QQSection extends PlayVideo{

    int index;
    private String formatid;
    private String vid;

    public QQSection(int index, String formatid, String vid, String fname) {
        super("章节"+index, fname);
        this.index = index;
        this.formatid = formatid;
        this.vid = vid;
    }

    public int getIndex(){
        return index - 1;
    }

    public String getFormatid(){
        return formatid;
    }

    public String getVid(){
        return vid;
    }

    @Override
    public String toStr() {
        return "QQSection{" + formatid
                + ", vid=" + vid
                + ", url=" + url
                + '}';
    }

}
