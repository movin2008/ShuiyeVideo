package com.shuiyes.video.letv;

import com.shuiyes.video.bean.PlayVideo;

public class LetvSource extends PlayVideo {

    private String stream;
    private String source;

    public LetvSource(String stream, String source, String url) {
        super(source, url);
        this.stream = stream;
        this.source = source;
        this.url = url;
    }

    public String getStream() {
        return stream;
    }

    public String getSource() {
        return source;
    }

    public String toStr() {
        return "LetvSource{" + stream +
                ", source=" + source +
//                ", url='" + url + '\'' +
                '}';
    }

}
