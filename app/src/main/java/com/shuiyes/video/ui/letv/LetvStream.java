package com.shuiyes.video.ui.letv;

import com.shuiyes.video.bean.PlayVideo;

public class LetvStream extends PlayVideo {

    private int stream;
    private String streamStr;

    public LetvStream(int stream, String url) {
        super(stream + "P", url);
        this.streamStr = stream + "P";
        this.stream = stream;
        this.url = url;
    }

    public int getStream() {
        return stream;
    }

    public String getStreamStr() {
        return streamStr;
    }

    @Override
    public String toStr() {
        return "LetvStream{" +
                "stream=" + streamStr +
//                ", url='" + url + '\'' +
                '}';
    }

}
