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
import java.util.Date;
import java.util.Iterator;

public class TextSort {

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
        String filename = "D:\\Android\\AndroidStudioProjects\\SYVideo\\tmp\\0312100540.list";
        FileInputStream in = new FileInputStream(filename);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));

        SimpleDateFormat sdf = new SimpleDateFormat("MMddHHmmss");
        String out = sdf.format(new Date()) + ".list";
        System.out.println(out);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("tmp/" + out)));

        ArrayList<String> list = new ArrayList<String>();
        String text = null;
        while ((text = br.readLine()) != null) {
            if (text.isEmpty()) {
                continue;
            }
            list.add(text);
        }

        Collections.sort(list);
        Iterator<String> iterator = list.iterator();
        while (iterator.hasNext()) {
            text = iterator.next();
            bw.write(text + "\n");
            bw.flush();
        }

        br.close();
        bw.close();

        System.out.println("end");
    }

}
