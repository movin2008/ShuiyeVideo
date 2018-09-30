package com.shuiyes.video.iqiyi;

import com.shuiyes.video.bean.PlayVideo;

public class IQiyiVideo extends PlayVideo{

    /**
     stream_types = [
     {'id': '4k', 'container': 'm3u8', 'video_profile': '4k'},
     {'id': 'BD', 'container': 'm3u8', 'video_profile': '1080p'},
     {'id': 'TD', 'container': 'm3u8', 'video_profile': '720p'},
     {'id': 'TD_H265', 'container': 'm3u8', 'video_profile': '720p H265'},
     {'id': 'HD', 'container': 'm3u8', 'video_profile': '540p'},
     {'id': 'HD_H265', 'container': 'm3u8', 'video_profile': '540p H265'},
     {'id': 'SD', 'container': 'm3u8', 'video_profile': '360p'},
     {'id': 'LD', 'container': 'm3u8', 'video_profile': '210p'},
     ]
     '''
     supported_stream_types = [ 'high', 'standard']
     stream_to_bid = {  '4k': 10, 'fullhd' : 5, 'suprt-high' : 4, 'super' : 3, 'high' : 2, 'standard' :1, 'topspeed' :96}
     '''
     ids = ['4k','BD', 'TD', 'HD', 'SD', 'LD']
     id_2_profile = {'4k':'4k', 'BD': '1080p','TD': '720p', 'HD': '540p', 'SD': '360p', 'LD': '210p', 'HD_H265': '540p H265', 'TD_H265': '720p H265'}
     */

    /**
     vd_2_id = {10: '4k', 19: '4k', 5:'BD', 18: 'BD', 21: 'HD_H265', 2: 'HD', 4: 'TD', 17: 'TD_H265', 96: 'LD', 1: 'SD', 14: 'TD'}
     * @param vd
     * @return
     */
    public static VideoType formateVideoType(int vd) {
        VideoType type = null;
        switch (vd){
            case 10:
            case 19:
                type = VideoType.UHD;
                break;
            case 5:
            case 18:
                type = VideoType.BD;
                break;
            case 4:
            case 14:
                type = VideoType.TD;
                break;
//            case 17:
//                type = VideoType.TD_H265;
//                break;
            case 2:
                type = VideoType.HD;
                break;
//            case 21:
//                type = VideoType.HD_H265;
//                break;
            case 1:
                type = VideoType.SD;
                break;
            case 96:
                type = VideoType.LD;
                break;
        }
        return type;
    }

    public static final int UHD_SZ = 4096*2160;

    public enum VideoType {
        UHD("4k", "m3u8", "4K", 4096*2160),
        BD("BD", "m3u8", "1080P", 1920*1080),
        TD("TD", "m3u8", "720P", 1080*720),
        TD_H265("TD_H265", "m3u8", "720P H265", 1280*720),

        HD("HD", "m3u8", "540P", 896*504),
        HD_H265("HD_H265", "m3u8", "540P H265", 896*504),
        SD("SD", "m3u8", "360P", 640*360),
        LD("LD", "m3u8", "210P", 384*216);

        private String type, ext, profile;
        private int screenSize;

        VideoType(String type, String ext, String profile, int screenSize) {
            this.type = type;
            this.ext = ext;
            this.profile = profile;
            this.screenSize = screenSize;
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

        public int getScreenSize() {
            return screenSize;
        }

        @Override
        public String toString() {
            return "type='" + type + '\'' +
                    ", ext='" + ext + '\'' +
                    ", profile='" + profile + '\'';
        }
    }

    private VideoType type;

    public IQiyiVideo(VideoType type, String url) {
        super(type.getProfile(), url);
        this.type = type;
    }

    public VideoType getType() {
        return type;
    }

    public String toStr() {
        return "IQiyiVideo{" + type +
//                ", url='" + url + '\'' +
                '}';
    }
}
