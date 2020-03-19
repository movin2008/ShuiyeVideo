package com.shuiyes.video.ui.youku;

import android.text.format.Formatter;

import com.shuiyes.video.ui.SVApplication;
import com.shuiyes.video.bean.PlayVideo;

public class YoukuVideo extends PlayVideo {

    /**
     * # Last updated: 2017-10-13
     * stream_types = [
     * {'id': 'hd3',      'container': 'flv', 'video_profile': '1080P'},
     * {'id': 'hd3v2',    'container': 'flv', 'video_profile': '1080P'},
     * {'id': 'mp4hd3',   'container': 'mp4', 'video_profile': '1080P'},
     * {'id': 'mp4hd3v2', 'container': 'mp4', 'video_profile': '1080P'},
     * <p>
     * {'id': 'hd2',      'container': 'flv', 'video_profile': '超清'},
     * {'id': 'hd2v2',    'container': 'flv', 'video_profile': '超清'},
     * {'id': 'mp4hd2',   'container': 'mp4', 'video_profile': '超清'},
     * {'id': 'mp4hd2v2', 'container': 'mp4', 'video_profile': '超清'},
     * <p>
     * {'id': 'mp4hd',    'container': 'mp4', 'video_profile': '高清'},
     * # not really equivalent to mp4hd
     * {'id': 'flvhd',    'container': 'flv', 'video_profile': '渣清'},
     * {'id': '3gphd',    'container': 'mp4', 'video_profile': '渣清'},
     * <p>
     * {'id': 'mp4sd',    'container': 'mp4', 'video_profile': '标清'},
     * # obsolete?
     * {'id': 'flv',      'container': 'flv', 'video_profile': '标清'},
     * {'id': 'mp4',      'container': 'mp4', 'video_profile': '标清'},
     * ]
     */

    public enum VideoType {
        HD3("hd3", "flv", "超清"),
        HD3V2("hd3v2", "flv", "超清"),
        MP4HD3("mp4hd3", "mp4", "超清"),
        MP4HD3V2("mp4hd3v2", "mp4", "超清"),

        HD2("hd2", "flv", "高清"),
        HD2V2("hd2v2", "flv", "高清"),
        MP4HD2("mp4hd2", "mp4", "高清"),
        MP4HD2V2("mp4hd2v2", "mp4", "高清"),

        MP4HD("mp4hd", "mp4", "标清"),
        FLVHD("flvhd", "flv", "标清"),

        MP4SD("mp4sd", "mp4", "流畅"),
        FLV("flv", "flv", "流畅"),
        MP4("mp4", "mp4", "流畅"),
        TH3GPHD("3gphd", "mp4", "流畅");


        private String type;
        private String ext;
        private String profile;

        VideoType(String type, String ext, String profile) {
            this.type = type;
            this.ext = ext;
            this.profile = profile;
        }

        public String getExtention() {
            return ext;
        }

        public String getProfile() {
            return profile;
        }

        public String getType() {
            return type;
        }

        @Override
        public String toString() {
            return "type='" + type + '\'' +
                    ", ext='" + ext + '\'' +
                    ", profile='" + profile + '\'';
        }
    }

    ;

    public static VideoType formateVideoType(String type) {
        VideoType[] vts = VideoType.values();
        for (VideoType vt : vts) {
            if (type.equals(vt.getType())) {
                return vt;
            }
        }
        return VideoType.FLV;
    }

    private VideoType type;
    private long size;

    public YoukuVideo(VideoType type, long size, String url) {
        super(type.getProfile() + "(" + Formatter.formatFileSize(SVApplication.getAppContext(), size) + ")", url);
        this.type = type;
        this.size = size;
    }

    public VideoType getType() {
        return type;
    }

    public long getSize() {
        return size;
    }

    @Override
    public String toStr() {
        return "YoukuVideo{" + type
                + ", size=" + Formatter.formatFileSize(SVApplication.getAppContext(), Long.valueOf(size))
                + '}';
    }
}
