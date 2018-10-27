package com.shuiyes.video.qq;

import android.text.format.Formatter;

import com.shuiyes.video.ui.SVApplication;
import com.shuiyes.video.bean.PlayVideo;

public class QQStream extends PlayVideo{

    /**
     {
         id: 321001,
         name: "sd",
         lmt: 0,
         sb: 1,
         cname: "标清;(270P)",
         br: 45,
         profile: 4,
         drm: 0,
         video: 1,
         audio: 1,
         fs: 150393044,
         sl: 1
     },
     {
         id: 321002,
         name: "hd",
         lmt: 0,
         sb: 1,
         cname: "高清;(480P)",
         br: 50,
         profile: 4,
         drm: 0,
         video: 1,
         audio: 1,
         fs: 328843960,
         sl: 0
     },
     {
         id: 321003,
         name: "shd",
         lmt: 0,
         sb: 1,
         cname: "超清;(720P)",
         br: 55,
         profile: 4,
         drm: 0,
         video: 1,
         audio: 1,
         fs: 649252548,
         sl: 0
     },
     {
         id: 321004,
         name: "fhd",
         lmt: 1,
         sb: 1,
         cname: "蓝光;(1080P)",
         br: 60,
         profile: 4,
         drm: 0,
         video: 1,
         audio: 1,
         fs: 1018120956,
         sl: 0
     }
     */

    // 321002
    private int stream;
    // 328843960
    private int size;
    // 高清;(480P)
    private String cname;

    public QQStream(int stream, String streamStr, String cname, int size) {
        super(cname+"(" + Formatter.formatFileSize(SVApplication.getAppContext(), size) + ")", streamStr);
        this.size = size;
        this.cname = cname;
        this.stream = stream;
    }

    public int getStreams() {
        return stream;
    }

    public int getSize() {
        return size;
    }

    public String getCname() {
        return cname;
    }

    @Override
    public String toStr() {
        return "QQStream{" + stream
                + ", stream=" + cname
                + ", size=" + Formatter.formatFileSize(SVApplication.getAppContext(), size)
                + '}';
    }

}
