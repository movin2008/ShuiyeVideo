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
public class ScanUrl {

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

        for (int i = 248; i < 254; i++) {
            for (int j = 1; j < 255; j++) {
                String url = "http://39.135." + i + "." + j + "/";
                System.out.println();
                if (HttpUtils.get(url)) {
                    sets.add(url);
                    System.out.println("ok");
                    bw.write("待定" + i + "," + url + "\n");
                    bw.flush();
                } else {
                    System.err.println(HttpUtils.E);
                    bw2.write("待定" + i + "," + url + "\n");
                    bw2.flush();
                }
                Thread.sleep(50);
            }
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

}
