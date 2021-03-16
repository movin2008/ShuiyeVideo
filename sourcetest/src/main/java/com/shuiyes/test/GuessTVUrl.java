package com.shuiyes.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// 源有效性测试
public class GuessTVUrl {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    aaa("D:\\Android\\AndroidStudioProjects\\SYVideo\\appTelevision\\src\\main\\assets\\tvlive\\移动源\\移动.湖北.ip");
//                  aa();
//					b();
//					c();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    static void aaa(String filepath) throws Exception {

        List<String> urls = new ArrayList<>();
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filepath)));
        String text = null;
        while ((text = br.readLine()) != null) {
            if(text.startsWith("http")){
                urls.add(text);
            }
        }

        SimpleDateFormat sdf = new SimpleDateFormat("MMddHHmmss");
        String out = sdf.format(new Date()) + ".list";
        String error = sdf.format(new Date()) + "_e.list";
        System.out.println(out);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("tmp/" + out)));
        BufferedWriter bw2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("tmp/" + error)));

        for (int i = 0; i < urls.size(); i++) {
            // 5600
            for (int k = 7000; k < 10000; k++) {
                for (int l = 1; l < 5; l++) {
                    String id;
                    if (l == 4) {
                        id = "index";
                    } else {
                        id = "" + l;
                    }
                    String url = urls.get(i) + "huaweicdn.hb.chinamobile.com/PLTV/88888888/224/322122" + k + "/" + id + ".m3u8";

                    System.out.println();
                    if (HttpUtils.get(url)) {
                        System.out.println("ok");
                        bw.write("待定" + i + "," + url + "\n");
                        bw.flush();
                    } else {
                        System.err.println(HttpUtils.E);
                        bw2.write("待定" + i + "," + url + "\n");
                        bw2.flush();

                        if(HttpUtils.E.contains("Exception")){
                            break;
                        }
                    }
                    Thread.sleep(50);
                }
            }
        }

        bw.close();
        bw2.close();
        System.out.println("end");
    }

    static void aa() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("MMddHHmmss");
        String out = sdf.format(new Date()) + ".list";
        String error = sdf.format(new Date()) + "_e.list";
        System.out.println(out);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("tmp/" + out)));
        BufferedWriter bw2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("tmp/" + error)));

        for (int i = 248; i < 254; i++) {
            for (int j = 1; j < 255; j++) {
                boolean jumpout = false;
                // 5600
                for (int k = 5000; k < 5600; k++) {
                    if(jumpout){
                        break;
                    }
                    for (int l = 1; l < 5; l++) {
                        String id;
                        if (l == 4) {
                            id = "index";
                        } else {
                            id = "" + l;
                        }
                        String url = "http://39.135." + i + "." + j + "/huaweicdn.hb.chinamobile.com/PLTV/88888888/224/322122" + k + "/" + id + ".m3u8";

                        System.out.println();
                        if (HttpUtils.get(url)) {
                            System.out.println("ok");
                            bw.write("待定" + i + "," + url + "\n");
                            bw.flush();
                        } else {
                            System.err.println(HttpUtils.E);
                            bw2.write("待定" + i + "," + url + "\n");
                            bw2.flush();

                            if(HttpUtils.E.contains("Exception")){
                                jumpout = true;
                                break;
                            }
                        }
                        Thread.sleep(50);
                    }
                }
            }
        }
        bw.close();
        bw2.close();
        System.out.println("end");
    }

    static void a() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("MMddHHmmss");
        String out = sdf.format(new Date()) + ".list";
        String error = sdf.format(new Date()) + "_e.list";
        System.out.println(out);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(out)));
        BufferedWriter bw2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(error)));

        for (int i = 1; i < 255; i++) {
            String id;
            if (i < 10) {
                id = "00" + i;
            } else if (i < 100) {
                id = "0" + i;
            } else {
                id = "" + i;
            }
//			System.out.print(id+" ");
//			String url = "http://httpdvb.slave.ttcatv.tv:13164/playurl?playtype=live&protocol=hls&accesstoken=R5CA2B7CAU3090C010K77540044IFB84556FPBM3220A5DV1044EZ33519WE22942B42A1&playtoken=&programid=4200000"+id+".m3u8";
//			String url = "http://59.46.18.134:8114/Fsv_otype=1?FvSeid=5bf181c25f226bb2&Fsv_filetype=1&Fsv_ctype=LIVES&Fsv_cid=0&Fsv_chan_hls_se_idx="+id+"&Fsv_rate_id=0&Fsv_SV_PARAM1=0&Fsv_ShiftEnable=0&Fsv_ShiftTsp=0&Provider_id=&Pcontent_id=&Fsv_CMSID=&Fsv_otype=1";
//			String url = "http://59.46.18.133:8114/lntvch/01000000000000000000000000000"+id+"?Fsv_otype=1&FvSeid=&Fsv_filetype=&Fsv_ctype=&Fsv_cid=&Fsv_chan_hls_se_idx=&Fsv_rate_id=&Fsv_TBt=&Fsv_ShiftEnable=&Fsv_ShiftTsp=&Fsv_SV_PARAM1=&Provider_id=&Pcontent_id=";
//			String url = "http://hwltc.tv.cdn.zj.chinamobile.com/pltv/88888888/224/322122"+id+"/index.m3u8";
//			String url = "http://otttv.bj.chinamobile.com/PLTV/88888888/224/322122"+id+"/1.m3u8";
//			String url = "http://124.47.33.211/PLTV/88888888/224/322122"+id+"/index.m3u8";
//			String url = "http://60.255.149."+i+"/tslive/c211_scc_20190617_t0x01q5a_original_r10/c211_scc_20190617_t0x01q5a_original_r10.m3u8&";
//			String url = "http://180.96.1."+i+"/tlivecloud-cdn.ysp.cctv.cn/001/2000266303.m3u8";
//			String url = "http://httpdvb.slave.homed.hunancatv.com:13164/playurl?playtype=live&amp;protocol=hls&amp;accesstoken=R5DEE27D0U309B3089K7735A23DIC56EFF0APBM2FFCD29V0Z69015W15DEAE8ECE8A23EB&amp;playtoken=ABCDEFGH&amp;" +
//					"programid=420000"+id+".m3u8";
//			String url = "http://cctvcnch5c.v.wscdns.com/live/cctv"+i+"_3/index.m3u8";
//			String url = "https://gccncc.v.wscdns.com/gc/xiongmao"+id+"_1/index.m3u8";
//			String url = "http://60.255.149."+i+"/tslive/c211_scc_20190617_t0x01q5a_original_r10/c211_scc_20190617_t0x01q5a_original_r10.m3u8&";
//			String url = "http://39.135.253.48/huaweicdn.hb.chinamobile.com/PLTV/88888888/224/3221225"+id+"/1.m3u8";
//			String url = "http://39.135.250."+i+"/huaweicdn.hb.chinamobile.com/PLTV/88888888/224/3221225965/index.m3u8";
            String url = "http://39.135.250." + i + "/PLTV/88888888/224/3221226063/99180001000000060000000000000440.smil";

            System.out.println();
            System.out.println(url);
            if (HttpUtils.get(url)) {
                bw.write("待定" + i + "," + url + "\n");
                bw.flush();
            } else {
                bw2.write("待定" + i + "," + url + "\n");
                bw2.flush();
            }
            Thread.sleep(50);
        }
        bw.close();
        bw2.close();
        System.out.println("end");
    }

    static void b() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("MMddHHmmss");
        String out = sdf.format(new Date()) + ".list";
        String error = sdf.format(new Date()) + "_e.list";
        System.out.println(out);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(out)));
        BufferedWriter bw2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(error)));

        for (int i = 0; i <= 2000; i++) {
            String url = "http://z9.syasn.com/c/c" + i;

            System.out.println();
            System.out.println(url);
            if (HttpUtils.get(url)) {
                bw.write("美女车模" + i + "," + url + "\n");
                bw.flush();
            } else {
                bw2.write("美女热舞" + i + "," + url + "\n");
                bw2.flush();
            }
            Thread.sleep(100);
        }
        bw.close();
        bw2.close();

        System.out.println("end");
    }

}
