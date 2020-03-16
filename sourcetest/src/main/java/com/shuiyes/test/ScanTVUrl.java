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
import java.util.Iterator;
import java.util.List;

// 央视频 IP 扫描
public class ScanTVUrl {

    public static void main(String[] args) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    a();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    static void a() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("MMddHHmmss");
        String out = sdf.format(new Date()) + ".list";
        String error = sdf.format(new Date()) + "_e.list";
        System.out.println(out + "start.");
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("tmp/" + out)));
        BufferedWriter bw2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("tmp/" + error)));

        List<String> sets = new ArrayList<String>();

        for (int i = 0; i < 255; i++) {
            //
            String ip = "221.228.67." + i;
            String iptext = "无锡电信," + ip;
            String url = "http://" + ip + "/tlivecloud-cdn.ysp.cctv.cn/001/2000266303.m3u8";

            System.out.println();
            System.out.println(sets.size() + ", " + url);
            if (HttpUtils.get(url)) {
                sets.add(iptext);
                bw.write("CCTV-4K," + url + "\n");
                bw.flush();
            } else {
                if ("403".equals(HttpUtils.E)) {
                    sets.add(iptext);
                }
                bw2.write("CCTV-4K," + url + "\n");
                bw2.flush();
            }
            Thread.sleep(50);
        }
        bw.close();
        bw2.close();
        System.out.println(out + "end.");

        System.out.println();
        System.out.println();
        Iterator<String> iterator = sets.iterator();
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }
    }

    static void aa() throws Exception {
        String filename = "test.list";
        FileInputStream in = new FileInputStream(filename);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));

        List<String> sets = new ArrayList<String>();
        if (filename.endsWith(".list")) {
            String text;
            while ((text = br.readLine()) != null) {
                String split = ",";
                if (text.contains(split)) {
                    String[] tmp = text.split(",");
//					if(HttpUtils.get("http://"+tmp[1]+"/tlivecloud-cdn.ysp.cctv.cn/001/2000266303.m3u8")){
                    if (HttpUtils.get(tmp[1])) {
                        sets.add(text);
                    }
                } else {
                    sets.add(text);
                }
            }
        }

        br.close();

        System.out.println("end");

        System.out.println();
        System.out.println();
        Iterator<String> iterator = sets.iterator();
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }
    }

}
