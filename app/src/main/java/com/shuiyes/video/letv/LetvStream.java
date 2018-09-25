package com.shuiyes.video.letv;

import android.content.Context;

import com.shuiyes.video.bean.PlayVideo;

public class LetvStream extends PlayVideo{

    private String stream;

    public LetvStream(int stream, String url) {
        super(stream + "P", url);
        this.stream = stream + "P";
        this.url = url;
    }

    public String getStream() {
        return stream;
    }

    public String toStr(Context ctx) {
        return "LetvSource{" +
                "stream=" + stream +
//                ", url='" + url + '\'' +
                '}';
    }

}
