package com.shuiyes.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

public class UrlSort {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    b();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    //待定249,http://39.135.249.20/huaweicdn.hb.chinamobile.com/PLTV/88888888/224/3221226098/index.m3u8
    //待定253,http://39.135.253.5/huaweicdn.hb.chinamobile.com/PLTV/88888888/224/3221226116/1.m3u8
    static void b() throws Exception {
        String filename = "D:\\Android\\AndroidStudioProjects\\ShuiyeVideo\\tmp\\0312142552.list";
        FileInputStream in = new FileInputStream(filename);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));

        SimpleDateFormat sdf = new SimpleDateFormat("MMddHHmmss");
        String out = sdf.format(new Date()) + ".list";
        System.out.println(out);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("tmp/" + out)));

        ArrayList<String> ret = new ArrayList<>();
        String text = null;
        while ((text = br.readLine()) != null) {
            ret.add(text);
        }

        Collections.sort(ret, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {

                String[] ss1 = s1.split("/");
                String[] ss2 = s2.split("/");

                return ss1[ss1.length - 2].compareTo(ss2[ss2.length - 2]);
            }
        });

        for (String s : ret) {
            bw.write(s + "\n");
        }

        br.close();
        bw.close();

        System.out.println("end");
    }

    static void a() throws Exception {
        String filename = "test.list";
        FileInputStream in = new FileInputStream(filename);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));

        SimpleDateFormat sdf = new SimpleDateFormat("MMddHHmmss");
        String out = sdf.format(new Date()) + ".list";
        String error = sdf.format(new Date()) + "_e.list";
        System.out.println(out);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("tmp/" + out)));
        BufferedWriter bw2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("tmp/" + error)));

        TreeMap<String, String> maps = new TreeMap<String, String>();

        String text = null;
        while ((text = br.readLine()) != null) {
            String split = ",";
            if (text.contains(split)) {
                String[] tmp = text.split(split);
                String title = tmp[0];
                String url = tmp[1];

                // 去重
                if (maps.containsKey(url)) {
                    System.err.println(maps.get(url) + " - " + title);

                    bw2.write(text + "\n");
                    bw2.flush();
                } else {
                    maps.put(url, title);
                }
            } else {
                bw2.write(text + "\n");
                bw2.flush();
            }
        }

        Set<String> sets = maps.keySet();

        Iterator<String> iterator = sets.iterator();
        while (iterator.hasNext()) {
            String url = iterator.next();
            bw.write(maps.get(url) + "," + url + "\n");
            bw.flush();
        }

        br.close();
        bw.close();
        bw2.close();

        System.out.println("end");
    }


}
