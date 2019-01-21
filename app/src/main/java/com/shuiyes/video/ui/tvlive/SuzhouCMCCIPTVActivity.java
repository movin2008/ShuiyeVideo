package com.shuiyes.video.ui.tvlive;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.shuiyes.video.R;
import com.shuiyes.video.util.OkHttpManager;
import com.shuiyes.video.util.Utils;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class SuzhouCMCCIPTVActivity extends Activity implements Callback {

    private final String TAG = this.getClass().getSimpleName();

    private final String HOST = "http://183.207.248.71:80/cntv/live1/";

    private ListView mListView;

    private Handler mHandler = new Handler();
    private ArrayList<String> mTitles = new ArrayList<String>();
    private HashMap<String, String> mUrls = new HashMap<String, String>();
    private OkHttpClient Client = OkHttpManager.getNormalClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bupt_ivi);

        mListView = (ListView) this.findViewById(R.id.lv_result);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String title = mTitles.get(position);
                startActivity(new Intent(SuzhouCMCCIPTVActivity.this, TVPlayActivity.class)
                        .putExtra("name", title)
                        .putExtra("url", mUrls.get(title)));
            }
        });

        String api = "http://looktvepg.jsa.bcs.ottcn.com:8080/ysten-lvoms-epg/epg/getChannelIndexs.shtml?deviceGroupId=1697";
        Request request = new Request.Builder().url(api).build();
        Client.newCall(request).enqueue(this);
    }

    public void cctv1(View view) {
        startActivity(new Intent(this, TVPlayActivity.class)
                .putExtra("name", "CCTV1")
                .putExtra("url", "http://183.207.248.71:80/cntv/live1/cctv-1/cctv-1"));
    }
    public void cctv2(View view) {
        startActivity(new Intent(this, TVPlayActivity.class)
                .putExtra("name", "CCTV12")
                .putExtra("url", "http://183.207.248.71:80/cntv/live1/cctv-2/cctv-2"));
    }
    public void cctv3(View view) {
        startActivity(new Intent(this, TVPlayActivity.class)
                .putExtra("name", "CCTV3")
                .putExtra("url", "http://183.207.248.71:80/cntv/live1/cctv-3/cctv-3"));
    }
    public void cctv4(View view) {
        startActivity(new Intent(this, TVPlayActivity.class)
                .putExtra("name", "CCTV4")
                .putExtra("url", "http://183.207.248.71:80/cntv/live1/cctv-4/cctv-4"));
    }
    public void cctv5(View view) {
        startActivity(new Intent(this, TVPlayActivity.class)
                .putExtra("name", "CCTV5")
                .putExtra("url", "http://183.207.248.71:80/cntv/live1/cctv-5/cctv-5"));
    }
    public void cctv6(View view) {
        startActivity(new Intent(this, TVPlayActivity.class)
                .putExtra("name", "CCTV6")
                .putExtra("url", "http://183.207.248.71:80/cntv/live1/cctv-6/cctv-6"));
    }

    @Override
    public void onFailure(Call call, IOException e) {
        fail("更多源加载失败: " + e.getLocalizedMessage());
    }

    private void fail(String error) {
        ((TextView) this.findViewById(R.id.tv_result)).setText(error);
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        try {
            String action = call.request().url().url().getPath();
            ResponseBody responseBody = response.body();
            if (responseBody == null) {
                Log.e(TAG, "onResponse(null): " + action);
                fail("更多源请求失败.");
            }

            String html = responseBody.string();
            Utils.setFile("suzhou.cmcc.iptv", html);

            JSONObject obj = new JSONObject(html);
            Iterator<String> iterator = obj.keys();
            while(iterator.hasNext()){
                String key = iterator.next();
                JSONObject channel = obj.getJSONObject(key);

                String channelName = channel.getString("channelName");
                mTitles.add(channelName);

                // http://183.207.248.71:80/cntv/live1/channelName/uuid
                String uuid = channel.getString("uuid");
                mUrls.put(channelName, HOST + channelName+"/"+uuid);
            }

            if (mTitles.isEmpty()) {
                fail("更多源加载为空.");
                return;
            }

            final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, mTitles.toArray());
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mListView.setVisibility(View.VISIBLE);
                    mListView.setAdapter(adapter);
                }
            });
        } catch (Exception e) {
            fail("更多源请求失败："+e.getLocalizedMessage());
        }

    }
}

/*
<!-- 20190121 -->
{
channel1: {
uuid: "hdcctv01-lowdelay",
channelName: "CCTV-1",
channelIcon: "http://images.center.bcs.ottcn.com:8080/images/ysten/images/ysten/TV/hdcctv01-lowdelay/111C1JSKDXTB.png"
},
channel2: {
uuid: "cctv-2",
channelName: "CCTV-2",
channelIcon: "http://images.center.bcs.ottcn.com:8080/images/ysten/images/ysten/TV/cctv-2/C2JSKDXTB.png"
},
channel3: {
uuid: "cctv-3",
channelName: "CCTV-3",
channelIcon: "http://images.center.bcs.ottcn.com:8080/images/ysten/images/ysten/TV/cctv-3/C3JSKDXTB.png"
},
channel4: {
uuid: "cctv-4",
channelName: "CCTV-4",
channelIcon: "http://images.center.bcs.ottcn.com:8080/images/ysten/images/ysten/TV/cctv-4/CJSKDXTB.png"
},
channel5: {
uuid: "cctv-5",
channelName: "CCTV-5",
channelIcon: "http://images.center.bcs.ottcn.com:8080/images/ysten/images/ysten/TV/cctv-5/C5JSKDXTB.png"
},
channel6: {
uuid: "hdcctv05plus",
channelName: "CCTV5+",
channelIcon: "http://images.center.bcs.ottcn.com:8080/images/ysten/images/ysten/TV/hdcctv05plus/C55JSKDXTB.png"
},
channel7: {
uuid: "cctv-6",
channelName: "CCTV-6",
channelIcon: "http://images.center.bcs.ottcn.com:8080/images/ysten/images/ysten/TV/cctv-6/C6JSKDXTB.png"
},
channel8: {
uuid: "cctv-7",
channelName: "CCTV-7",
channelIcon: "http://images.center.bcs.ottcn.com:8080/images/ysten/images/ysten/TV/cctv-7/C7JSKDXTB.png"
},
channel9: {
uuid: "cctv-8",
channelName: "CCTV-8",
channelIcon: "http://images.center.bcs.ottcn.com:8080/images/ysten/images/ysten/TV/cctv-8/C8JSKDXTB.png"
},
channel10: {
uuid: "cctv-news",
channelName: "CCTV-9",
channelIcon: "http://images.center.bcs.ottcn.com:8080/images/ysten/images/ysten/TV/cctv-news/C9JSKDXTB.png"
},
channel11: {
uuid: "cctv-10",
channelName: "CCTV-10",
channelIcon: "http://images.center.bcs.ottcn.com:8080/images/ysten/images/ysten/TV/cctv-10/C10JSKDXTB.png"
},
channel12: {
uuid: "cctv-11",
channelName: "CCTV-11",
channelIcon: "http://images.center.bcs.ottcn.com:8080/images/ysten/images/ysten/TV/cctv-11/C11JSKDXTB.png"
},
channel13: {
uuid: "cctv-12",
channelName: "CCTV-12",
channelIcon: "http://images.center.bcs.ottcn.com:8080/images/ysten/images/ysten/TV/cctv-12/C12JSKDXTB.png"
},
channel14: {
uuid: "cctv-13",
channelName: "CCTV-13",
channelIcon: "http://images.center.bcs.ottcn.com:8080/images/ysten/images/ysten/TV/cctv-13/C13JSKDXTB.png"
},
channel15: {
uuid: "cctv-14",
channelName: "CCTV-14",
channelIcon: "http://images.center.bcs.ottcn.com:8080/images/ysten/images/ysten/TV/cctv-14/C14JSKDXTB.png"
},
channel16: {
uuid: "cctv-15",
channelName: "CCTV-15",
channelIcon: "http://images.center.bcs.ottcn.com:8080/images/ysten/images/ysten/TV/cctv-15/C15JSKDXTB.png"
},
channel17: {
uuid: "cctv-9",
channelName: "CCTV-9(英)",
channelIcon: "http://images.center.bcs.ottcn.com:8080/images/ysten/images/ysten/TV/cctv-9/C99JSKDXTB.png"
},
channel18: {
uuid: "jiangsustv",
channelName: "江苏卫视",
channelIcon: "http://images.center.bcs.ottcn.com:8080/images/ysten/images/ysten/TV/jiangsustv/njsaf.png"
},
channel19: {
uuid: "zhejiangstv",
channelName: "浙江卫视",
channelIcon: "http://images.center.bcs.ottcn.com:8080/images/ysten/images/ysten/TV/zhejiangstv/lgjd.png"
},
channel20: {
uuid: "beijingstv",
channelName: "北京卫视",
channelIcon: "http://images.center.bcs.ottcn.com:8080/images/ysten/images/ysten/TV/beijingstv/B1JSDSKDTB.png"
},
channel21: {
uuid: "hunanstv",
channelName: "湖南卫视",
channelIcon: "http://images.center.bcs.ottcn.com:8080/images/ysten/images/ysten/TV/hunanstv/jkgsdjg.png"
},
channel22: {
uuid: "guangdongstv",
channelName: "广东卫视",
channelIcon: "http://images.center.bcs.ottcn.com:8080/images/ysten/images/ysten/TV/guangdongstv/sfagfa.png"
},
channel23: {
uuid: "dongfangstv",
channelName: "东方卫视",
channelIcon: "http://images.center.bcs.ottcn.com:8080/images/ysten/images/ysten/TV/dongfangstv/jgjsfsh.png"
},
channel24: {
uuid: "HD-2500k-1080P-heilongjiangstv",
channelName: "黑龙江卫视",
channelIcon: "http://images.center.bcs.ottcn.com:8080/images/ysten/images/ysten/TV/HD-2500k-1080P-heilongjiangstv/hljjskdzy1.png"
},
channel25: {
uuid: "shenzhenstv",
channelName: "深圳卫视",
channelIcon: "http://images.center.bcs.ottcn.com:8080/images/ysten/images/ysten/TV/shenzhenstv/jffa.png"
},
channel26: {
uuid: "shandongstv",
channelName: "山东卫视",
channelIcon: "http://images.center.bcs.ottcn.com:8080/images/ysten/images/ysten/TV/shandongstv/jflafh.png"
},
channel27: {
uuid: "tianjinstv",
channelName: "天津卫视",
channelIcon: "http://images.center.bcs.ottcn.com:8080/images/ysten/images/ysten/TV/tianjinstv/kgsg.png"
},
channel28: {
uuid: "hubeistv",
channelName: "湖北卫视",
channelIcon: "http://images.center.bcs.ottcn.com:8080/images/ysten/images/ysten/TV/hubeistv/fasf.png"
},
channel29: {
uuid: "chongqingstv",
channelName: "重庆卫视",
channelIcon: "http://images.center.bcs.ottcn.com:8080/images/ysten/images/ysten/TV/chongqingstv/cqwejszy123.png"
},
channel30: {
uuid: "anhuistv",
channelName: "安徽卫视",
channelIcon: "http://images.center.bcs.ottcn.com:8080/images/ysten/images/ysten/TV/anhuistv/hfsdi.png"
},
channel31: {
uuid: "jiangxistv",
channelName: "江西卫视",
channelIcon: "http://images.center.bcs.ottcn.com:8080/images/ysten/images/ysten/TV/jiangxistv/kdhf.png"
},
channel32: {
uuid: "liaoningstv",
channelName: "辽宁卫视超高清",
channelIcon: "http://images.center.bcs.ottcn.com:8080/images/ysten/images/ysten/TV/liaoningstv/mnfha.png"
},
channel33: {
uuid: "dongnanstv",
channelName: "东南卫视",
channelIcon: "http://images.center.bcs.ottcn.com:8080/images/ysten/images/ysten/TV/dongnanstv/jsfjghjsdf.png"
},
channel34: {
uuid: "sichuanstv",
channelName: "四川卫视",
channelIcon: "http://images.center.bcs.ottcn.com:8080/images/ysten/images/ysten/TV/sichuanstv/jsdjsg.png"
},
channel35: {
uuid: "guangxistv",
channelName: "广西卫视",
channelIcon: "http://images.center.bcs.ottcn.com:8080/images/ysten/images/ysten/TV/guangxistv/kafj.png"
},
channel36: {
uuid: "hebeistv",
channelName: "河北卫视",
channelIcon: "http://images.center.bcs.ottcn.com:8080/images/ysten/images/ysten/TV/hebeistv/kfsj.png"
},
channel37: {
uuid: "shanxistv",
channelName: "山西卫视",
channelIcon: "http://images.center.bcs.ottcn.com:8080/images/ysten/images/ysten/TV/shanxistv/hjfda.png"
},
channel38: {
uuid: "shanxi1stv",
channelName: "陕西卫视",
channelIcon: "http://images.center.bcs.ottcn.com:8080/images/ysten/images/ysten/TV/shanxi1stv/fsasf.png"
},
channel39: {
uuid: "guizhoustv",
channelName: "贵州卫视",
channelIcon: "http://images.center.bcs.ottcn.com:8080/images/ysten/images/ysten/TV/guizhoustv/hdfsu.png"
},
channel40: {
uuid: "qinghaistv",
channelName: "青海卫视",
channelIcon: "http://images.center.bcs.ottcn.com:8080/images/ysten/images/ysten/TV/qinghaistv/jsfdfdgj.png"
},
channel41: {
uuid: "ningxiastv",
channelName: "宁夏卫视",
channelIcon: "http://images.center.bcs.ottcn.com:8080/images/ysten/images/ysten/TV/ningxiastv/fafjk.png"
},
channel42: {
uuid: "gansustv",
channelName: "甘肃卫视",
channelIcon: "http://images.center.bcs.ottcn.com:8080/images/ysten/images/ysten/TV/gansustv/sdg.png"
},
channel43: {
uuid: "neimenggustv",
channelName: "内蒙古卫视",
channelIcon: "http://images.center.bcs.ottcn.com:8080/images/ysten/images/ysten/TV/neimenggustv/hjzdsjf.png"
},
channel44: {
uuid: "xjtv1",
channelName: "新疆卫视",
channelIcon: "http://images.center.bcs.ottcn.com:8080/images/ysten/images/ysten/TV/xjtv1/sgsd.png"
},
channel45: {
uuid: "yntv1",
channelName: "云南卫视",
channelIcon: "http://images.center.bcs.ottcn.com:8080/images/ysten/images/ysten/TV/yntv1/sg.png"
},
channel46: {
uuid: "xizangstv",
channelName: "西藏卫视",
channelIcon: "http://images.center.bcs.ottcn.com:8080/images/ysten/images/ysten/TV/xizangstv/hdhgs.png"
},
channel47: {
uuid: "jiaoyutv",
channelName: "中国教育-1",
channelIcon: "http://images.center.bcs.ottcn.com:8080/images/ysten/images/ysten/TV/jiaoyutv/nhjfjh.png"
},
channel48: {
uuid: "HD-8000k-1080P-beijingjishi",
channelName: "北京纪实频道超高清",
channelIcon: "http://images.center.bcs.ottcn.com:8080/images/ysten/images/ysten/TV/HD-8000k-1080P-beijingjishi/bjjsjszy.png"
},
channel49: {
uuid: "HD-8000k-1080P-shanghaijishi",
channelName: "上海纪实频道超高清",
channelIcon: "http://images.center.bcs.ottcn.com:8080/images/ysten/images/ysten/TV/HD-8000k-1080P-shanghaijishi/shjsjszy.png"
},
channel50: {
uuid: "jinyingkaton",
channelName: "金鹰卡通",
channelIcon: "http://images.center.bcs.ottcn.com:8080/images/ysten/images/ysten/TV/jinyingkaton/JYJSKDXTB.png"
},
channel51: {
uuid: "kakukaton",
channelName: "卡酷少儿",
channelIcon: "http://images.center.bcs.ottcn.com:8080/images/ysten/images/ysten/TV/kakukaton/JKJSKDXTB.png"
},
channel52: {
uuid: "shandongjy",
channelName: "山东教育",
channelIcon: "http://images.center.bcs.ottcn.com:8080/images/ysten/images/ysten/TV/shandongjy/SDJSKDXTB.png"
},
channel53: {
uuid: "youmankaton",
channelName: "优漫卡通",
channelIcon: "http://images.center.bcs.ottcn.com:8080/images/ysten/images/ysten/TV/youmankaton/ymktjszy.png"
},
channel54: {
uuid: "xuandongkaton",
channelName: "炫动卡通",
channelIcon: "http://images.center.bcs.ottcn.com:8080/images/ysten/images/ysten/TV/xuandongkaton/xdktjszy.png"
},
channel55: {
uuid: "HD-8000k-1080P-Supermovie",
channelName: "黑莓电影",
channelIcon: ""
},
channel56: {
uuid: "HD-8000k-1080P-Supercctv14",
channelName: "黑莓动画",
channelIcon: ""
},
channel57: {
uuid: "fengshanggw",
channelName: "风尚购物",
channelIcon: "http://images.center.bcs.ottcn.com:8080/images/ysten/images/ysten/TV/fengshanggw/fsgwjszy.png"
},
channel58: {
uuid: "SD-1500k-576P-jiayougw",
channelName: "家有购物",
channelIcon: "http://images.center.bcs.ottcn.com:8080/images/ysten/images/ysten/TV/SD-1500k-576P-jiayougw/jygwjszy123.png"
},
channel59: {
uuid: "guzhuangjc",
channelName: "古装剧场",
channelIcon: "http://images.center.bcs.ottcn.com:8080/images/ysten/images/ysten/TV/guzhuangjc/NgzjcJSKDXTB.png"
},
channel60: {
uuid: "dongzuody",
channelName: "动作电影",
channelIcon: "http://images.center.bcs.ottcn.com:8080/images/ysten/images/ysten/TV/dongzuody/NdzdyJSKDXTB.png"
},
channel61: {
uuid: "junlvjc",
channelName: "军旅剧场",
channelIcon: "http://images.center.bcs.ottcn.com:8080/images/ysten/images/ysten/TV/junlvjc/NjljcJSKDXTB.png"
},
channel62: {
uuid: "jiatingjc",
channelName: "家庭剧场",
channelIcon: "http://images.center.bcs.ottcn.com:8080/images/ysten/images/ysten/TV/jiatingjc/NjtjcJSKDXTB.png"
},
channel63: {
uuid: "HD-4000k-1080P-xwwl",
channelName: "炫舞未来",
channelIcon: "http://images.center.bcs.ottcn.com:8080/images/ysten/images/ysten/TV/HD-4000k-1080P-xwwl/NxwwlJSKDXTB.png"
},
channel64: {
uuid: "jingsongxy",
channelName: "惊悚悬疑",
channelIcon: "http://images.center.bcs.ottcn.com:8080/images/ysten/images/ysten/TV/jingsongxy/NjsxyJSKDXTB.png"
},
channel65: {
uuid: "xiqumd",
channelName: "海外剧场",
channelIcon: "http://images.center.bcs.ottcn.com:8080/images/ysten/images/ysten/TV/xiqumd/NhwjcJSKDXTB.png"
},
channel66: {
uuid: "SD-1500k-576P-bokesen",
channelName: "世界搏击",
channelIcon: "http://images.center.bcs.ottcn.com:8080/images/ysten/images/ysten/TV/SD-1500k-576P-bokesen/NsjbjJSKDXTB.png"
},
channel67: {
uuid: "mingxingdp",
channelName: "明星大片",
channelIcon: "http://images.center.bcs.ottcn.com:8080/images/ysten/images/ysten/TV/mingxingdp/NmxdpJSKDXTB.png"
},
channel68: {
uuid: "jdianying",
channelName: "精品电影",
channelIcon: "http://images.center.bcs.ottcn.com:8080/images/ysten/images/ysten/TV/jdianying/NjpdyJSKDXTB.png"
},
channel69: {
uuid: "aiqingxj",
channelName: "爱情喜剧",
channelIcon: "http://images.center.bcs.ottcn.com:8080/images/ysten/images/ysten/TV/aiqingxj/NaqxjJSKDXTB.png"
},
channel70: {
uuid: "jdaju",
channelName: "精品大剧",
channelIcon: "http://images.center.bcs.ottcn.com:8080/images/ysten/images/ysten/TV/jdaju/NjpdjJSKDXTB.png"
},
channel71: {
uuid: "SD-1500k-576P-gzkongfu",
channelName: "中国功夫",
channelIcon: "http://images.center.bcs.ottcn.com:8080/images/ysten/images/ysten/TV/SD-1500k-576P-gzkongfu/NzggfJSKDXTB.png"
},
channel72: {
uuid: "saishijx",
channelName: "金牌综艺",
channelIcon: "http://images.center.bcs.ottcn.com:8080/images/ysten/images/ysten/TV/saishijx/NjpzyJSKDXTB.png"
},
channel73: {
uuid: "junshipl",
channelName: "军事评论",
channelIcon: "http://images.center.bcs.ottcn.com:8080/images/ysten/images/ysten/TV/junshipl/NjsplJSKDXTB.png"
},
channel74: {
uuid: "nongyezf",
channelName: "农业致富",
channelIcon: "http://images.center.bcs.ottcn.com:8080/images/ysten/images/ysten/TV/nongyezf/NnyzfJSKDXTB.png"
},
channel75: {
uuid: "HD-8000k-1080P-Superwmyx",
channelName: "黑莓电竞之夜",
channelIcon: ""
},
channel76: {
uuid: "jingpinjl",
channelName: "精品纪录",
channelIcon: "http://images.center.bcs.ottcn.com:8080/images/ysten/images/ysten/TV/jingpinjl/NjpjlJSKDXTB.png"
},
channel77: {
uuid: "ljiankangyouyue",
channelName: "健康有约",
channelIcon: "http://images.center.bcs.ottcn.com:8080/images/ysten/images/ysten/TV/ljiankangyouyue/NjkyyJSKDXTB.png"
},
channel78: {
uuid: "jtiyu",
channelName: "精品体育",
channelIcon: "http://images.center.bcs.ottcn.com:8080/images/ysten/images/ysten/TV/jtiyu/NjptyJSKDXTB.png"
},
channel79: {
uuid: "HD-1500k-720P-cmlapo",
channelName: "潮妈辣婆",
channelIcon: "http://images.center.bcs.ottcn.com:8080/images/ysten/images/ysten/TV/HD-1500k-720P-cmlapo/NcmlpJSKDXTB.png"
},
channel80: {
uuid: "cctv-1",
channelName: "CCTV-1",
channelIcon: "http://images.center.bcs.ottcn.com:8080/images/ysten/images/ysten/TV/cctv-1/C1JSKDXTB.png"
}
}
*/