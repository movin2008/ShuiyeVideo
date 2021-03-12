package com.shuiyes.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class DeDuplication {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

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
        String filename = "D:\\Android\\AndroidStudioProjects\\SYVideo\\appTelevision\\src\\main\\assets\\tvlive\\cctv.wscdns.com.tv";
        FileInputStream in = new FileInputStream(filename);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));

        SimpleDateFormat sdf = new SimpleDateFormat("MMddHHmmss");
        String out = sdf.format(new Date()) + ".list";
        String error = sdf.format(new Date()) + "_e.list";
        System.out.println(out);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("tmp/" + out)));
        BufferedWriter bw2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("tmp/" + error)));

        HashMap<String, String> maps = new HashMap<String, String>();

        String text = null;
        while ((text = br.readLine()) != null) {
            String split = ",";
            if (text.contains(split)) {
                String[] tmp = text.split(split);
                String title = tmp[0];
                String[] urls = tmp[1].split("#");
                for (String url : urls) {
                    url = url.trim();
                    // 去重
                    if (maps.containsKey(url)) {
                        System.err.println(maps.get(url) + " - " + title);

                        bw2.write(text + "\n");
                        bw2.flush();
                    } else {
                        maps.put(url, title);

                        bw.write(maps.get(url) + "," + url + "\n");
                        bw.flush();
                    }
                }
            } else {
                bw.write(text + "\n");
                bw.flush();

                bw2.write(text + "\n");
                bw2.flush();
            }
        }

        br.close();
        bw.close();
        bw2.close();

        System.out.println("end");
    }

}
