package com.shuiyes.video.ui.vip;

import android.text.TextUtils;
import android.util.Log;

import com.shuiyes.video.util.HttpUtils;

import java.util.Calendar;
import java.util.TimeZone;

class VipSource {

    private static final String VIP_8090 = "8090g";
    private static final String URL_YLYBZ_8090 = "https://www.8090g.cn/?url=";
    private static final String API_YLYBZ_8090 = "https://8090.ylybz.cn/jiexi2019/api.php";
    private static final String REFER_YLYBZ_8090 = "https://8090.ylybz.cn/jiexi2019/?url=";

    private static final String VIP_WOCAO = "WoCao";
    private static final String URL_YLYBZ_WOCAO = "https://www.wocao.xyz/?url=";
    private static final String API_YLYBZ_WOCAO = "https://wocao.ylybz.cn/api.php";
    private static final String REFER_YLYBZ_WOCAO = "https://wocao.ylybz.cn/vip.php?url=";

    private static final String VIP_97KYS = "97kys";
    private static final String URL_YLYBZ_97KYS = "https://vip.97kys.com/vip/?url=";
    private static final String API_YLYBZ_97KYS = "https://vip.97kys.com/vip/api.php";
    private static final String REFER_YLYBZ_97KYS = "https://vip.97kys.com/vip/?url=";

    private static final String VIP_ADMIN = "Admin";
    private static final String URL_YLYBZ_DATA = "https://www.administratorv.com/iqiyi/index.php?url=";
    private static final String API_YLYBZ_DATA = "https://www.administratorm.com/";
    private static final String REFER_YLYBZ_DATA = "https://www.administratorm.com/index.php?url=";

    // 相比上面的api 这个会根据名字查找其他网站资源
    private static final String VIP_WMXZ = "WMXZ";
    private static final String URL_YLYBZ_WANG = "https://www.administratorm.com/ADMIN/index.php?url=";
    private static final String API_YLYBZ_WANG = "https://www.administratorm.com/WMXZ.WANG/";
    private static final String REFER_YLYBZ_WANG = "https://www.administratorm.com/WANG/index.php?url=";

    private static final String VIP_618G = "618G";
    private static final String URL_618G = "https://607p.com/?url=";
    private static final String EXT = " VIP视频解析...";

    enum VipRouter{

        V8090g(VIP_8090, VIP_8090 + EXT, URL_YLYBZ_8090, API_YLYBZ_8090, REFER_YLYBZ_8090),
        Vwocao(VIP_WOCAO, VIP_WOCAO + EXT, URL_YLYBZ_WOCAO, API_YLYBZ_WOCAO, REFER_YLYBZ_WOCAO),
        V97kys(VIP_97KYS, VIP_97KYS + EXT, URL_YLYBZ_97KYS, API_YLYBZ_97KYS, REFER_YLYBZ_97KYS),
        Vadmin(VIP_ADMIN, VIP_ADMIN + EXT, URL_YLYBZ_DATA, API_YLYBZ_DATA, REFER_YLYBZ_DATA),
        Vwmxz(VIP_WMXZ, VIP_WMXZ + EXT, URL_YLYBZ_WANG, API_YLYBZ_WANG, REFER_YLYBZ_WANG),
        V618G(VIP_618G, VIP_618G + EXT, URL_618G, null, null);


        String type, text, url, api, refer;

        VipRouter(String type,  String text, String url, String api, String refer) {
            this.type = type;
            this.text = text;
            this.url = url;
            this.api = api;
            this.refer = refer;
        }

    }

    static VipRouter parse(String type) throws Exception {
        switch (type){
            case VIP_ADMIN:
                return VipRouter.Vadmin;
            case VIP_WMXZ:
                return VipRouter.Vwmxz;
            case VIP_8090:
                return VipRouter.V8090g;
            case VIP_WOCAO:
                return VipRouter.Vwocao;
            case VIP_97KYS:
                return VipRouter.V97kys;
            case VIP_618G:
                return VipRouter.V618G;

        }

        throw new Exception("Unkown vip router " + type);
    }

    static String adminApi() {
        String html = HttpUtils.get("https://www.administratorm.com/index.php?url=https://m.iqiyi.com/v_19ruzj8gv0.html", "referer: https://www.administratorv.com/iqiyi/index.php?url=https://m.iqiyi.com/v_19ruzj8gv0.html", false);
        String key = "$.post(\"";
        if (!TextUtils.isEmpty(html) && html.contains(key)) {
            html = html.substring(html.indexOf(key) + key.length());
            // 6896515874.php
            return html.substring(0, html.indexOf("\""));
        } else {
            return adminApi2();
        }
    }

    /**
     *  计算错误 2020/20/10 -> 16896516642
     * @return
     */
    @Deprecated
    private static String adminApi2(){
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8:00"));
        long day = calendar.get(Calendar.DAY_OF_YEAR) - 1;
        long hour = calendar.get(Calendar.HOUR_OF_DAY);
        long year = calendar.get(Calendar.YEAR);
        // 6896515874 16896515882 16896515890
        // 2020/19/17 -> 16896515890
        long tmp = year * 8364610 + day * 192 + hour * 8 - 86;
        Log.e("HAHA", year + "/" + day + "/" + hour + " -> " + tmp);
        return tmp + ".php";
    }

}
