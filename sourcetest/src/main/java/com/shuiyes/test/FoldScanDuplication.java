package com.shuiyes.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class FoldScanDuplication {

    public static void main(String[] args) {
        // TODO Auto-generated method stub

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    HashMap<String, String> maps = new HashMap<String, String>();
                    scanFold("app/src/main/assets", maps);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

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

                    if (maps.containsKey(url)) {
                        System.out.println();
                        System.out.println(url);
                        System.out.println(maps.get(url) + " - " + title);
                    } else {
                        maps.put(url, title);
                    }
                }
            }
            br2.close();
        }
    }

}
