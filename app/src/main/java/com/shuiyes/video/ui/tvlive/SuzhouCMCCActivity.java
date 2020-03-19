package com.shuiyes.video.ui.tvlive;

import android.os.Bundle;

import com.shuiyes.video.R;
import com.shuiyes.video.ui.base.BaseTVLiveActivity;
import com.shuiyes.video.bean.ListVideo;
import com.shuiyes.video.util.Utils;

import org.json.JSONObject;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

public class SuzhouCMCCActivity extends BaseTVLiveActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.sz_cmcc);
    }

    @Override
    public String getApi() {
        //      http://looktvepg.jsa.bcs.ottcn.com:8080/ysten-lvoms-epg/epg/getNewPrograms.shtml?actionType=OpenNew&deviceGroupId=1697&districtCode=320500&templateId=02523
        return "http://looktvepg.jsa.bcs.ottcn.com:8080/ysten-lvoms-epg/epg/getChannelIndexs.shtml?deviceGroupId=1697";
    }

    @Override
    public String getPlayUrl(int tv) {
        //http://183.207.248.71/cntv/live1/HD-2500k-1080P-cctv1/HD-2500k-1080P-cctv1
        return HOST + "cctv-" + tv + "/cctv-" + tv;
    }

    private final String HOST = "http://183.207.248.71/cntv/live1/";

    @Override
    public void refreshVideos(String result) throws Exception {
        Utils.setFile("suzhou.cmcc.iptv", result);

        // 按名字排序
        TreeMap<String, String> maps = new TreeMap<String, String>();

        JSONObject obj = new JSONObject(result);
        Iterator<String> iterator = obj.keys();
        mVideos.clear();
        while (iterator.hasNext()) {
            String key = iterator.next();
            JSONObject channel = obj.getJSONObject(key);

            String channelName = channel.getString("channelName");
            String uuid = channel.getString("uuid");

            // http://183.207.248.71/cntv/live1/channelName/uuid
            maps.put(channelName, HOST + channelName + "/" + uuid);
        }


        Set<String> sets = maps.keySet();

        Iterator<String> iterator2 = sets.iterator();
        while (iterator2.hasNext()) {
            String name = iterator2.next();
            mVideos.add(new ListVideo(name, name, maps.get(name)));
        }
    }

}