package com.shuiyes.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class FoldDeDuplication {

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

        HashMap<String, String> maps = new HashMap<String, String>();
        scanFold("app/src/main/assets", maps);

        String filename = "sourcetest/src/main/assets/test.list";
        FileInputStream in = new FileInputStream(filename);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));

        SimpleDateFormat sdf = new SimpleDateFormat("MMddHHmmss");
        String out = "tmp/" + sdf.format(new Date()) + ".list";
        String error = "tmp/" + sdf.format(new Date()) + "_e.list";
        System.out.println(out);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(out)));
        BufferedWriter bw2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(error)));

        if (filename.endsWith(".list")) {

            String text = null;
            while ((text = br.readLine()) != null) {
                String split = ",";
                if (text.contains(split)) {
                    String[] tmp = text.split(split);
                    String title = tmp[0];
                    String url = tmp[1].trim();

                    if (maps.containsKey(url)) {
                        System.err.println(maps.get(url) + " - " + title);

                        bw2.write(maps.get(url) + " - " + title + "\n");
                        bw2.write(text + "\n");
                        bw2.flush();
                    } else {

                        bw.write(text + "\n");
                        bw.flush();
                    }
                } else {
                    bw.write(text + "\n");
                    bw.flush();

                    bw2.write(text + "\n");
                    bw2.flush();
                }
            }
        }

        System.out.println(out + " end");

        br.close();
        bw.close();
        bw2.close();
    }

    static void scanFold(String path, HashMap<String, String> maps) throws Exception {
        File file = new File(path);
        File[] files = file.listFiles();
        for (File tmpFile : files) {

            if (tmpFile.isDirectory()) {
                scanFold(tmpFile.getAbsolutePath(), maps);
                continue;
            }
            if (tmpFile.getName().endsWith("apk") || tmpFile.getName().endsWith("css")) {
                continue;
            }

            FileInputStream in2 = new FileInputStream(tmpFile);
            BufferedReader br2 = new BufferedReader(new InputStreamReader(in2));
            String text2 = null;
            while ((text2 = br2.readLine()) != null) {
                String split = ",";
                if (text2.contains(split)) {
                    String[] tmp = text2.split(split);
                    String title = tmp[0];
                    String url = tmp[1];

                    maps.put(url, title);
                }
            }
            br2.close();
        }
    }

}
