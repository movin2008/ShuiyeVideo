package com.shuiyes.video.letv;

import android.content.Context;
import android.text.format.Formatter;

public class LetvSource {

    private String type;
    private String source;
    private String url;

    public LetvSource(int stream, String source, String url) {
        this.type = stream+"P";
        this.source = source;
        this.url = url;
    }

    public void setProfile(int stream) {
        this.type = stream+"P";
    }

    public String getProfile() {
        return type;
    }


    public String getUrl() {
        return url;
    }

    public String toStr(Context ctx) {
        return "LetvSource{" + type +
                ", source=" + source +
//                ", url='" + url + '\'' +
                '}';
    }

}
