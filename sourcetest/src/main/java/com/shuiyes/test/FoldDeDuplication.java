package com.shuiyes.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URLDecoder;
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
        scanFold("D:\\Android\\AndroidStudioProjects\\ShuiyeVideo\\app\\src\\main\\assets", maps);

        String filename = "D:\\Android\\AndroidStudioProjects\\SYVideo\\tmp\\风筝2月25日21点整理.txt";
        FileInputStream in = new FileInputStream(filename);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));

        SimpleDateFormat sdf = new SimpleDateFormat("MMddHHmmss");
        String out = "tmp/" + sdf.format(new Date()) + ".list";
        String error = "tmp/" + sdf.format(new Date()) + "_e.list";
        System.out.println(out);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(out)));
        BufferedWriter bw2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(error)));


        if (filename.endsWith(".list") || filename.endsWith(".tv") || filename.endsWith(".fm")|| filename.endsWith(".txt")) {
            String text;
            while ((text = br.readLine()) != null) {
                String split = ",";
                if (text.contains(split)) {
                    String[] tmp = text.split(split);
                    if(tmp.length < 2) continue;
                    String title = tmp[0];

                    String[] urls = tmp[1].trim().split("#");
                    for (String url : urls) {
                        url = url.trim();
                        url = url.replace(":80/", "/");
                        url = url.replace(":443/", "/");

                        if (maps.containsKey(url)) {
                            System.err.println(maps.get(url) + " - " + title);

                            bw2.write(maps.get(url) + " - " + title + "\n");
                            bw2.write(title + "," + url + "\n");
                            bw2.flush();
                        } else {

                            bw.write(title + "," + url + "\n");
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
        }else if (filename.endsWith(".dpl")) {
        } else if (filename.endsWith(".m3u")) {
            String title = null;
            String groupTitle = "";

            String text;
            while ((text = br.readLine()) != null) {
                if (text.startsWith("#EXTM3U") || text.trim().length() == 0) {
                    // head
                    continue;
                }

                if (text.startsWith("#EXTINF")) {
//					System.out.println();
//					System.out.println(flag+++""+text);

                    String[] tmp = text.split(",");
                    title = tmp[tmp.length - 1];
                    if (title.contains("%")) {
                        title = URLDecoder.decode(title);
                    }

                    String key = "group-title=\"";
                    if (text.contains(key)) {
                        String gTitle = text.substring(text.indexOf(key) + key.length(), text.lastIndexOf("\""));
                        if (!groupTitle.equals(gTitle)) {
                            groupTitle = gTitle;

                            bw.write("\n[Group]" + groupTitle + "\n");
                            bw.flush();

                            bw2.write("\n[Group]" + groupTitle + "\n");
                            bw2.flush();
                        }
                    }
                } else if (title != null && !text.startsWith("#EXT")) {
//					System.out.println(text);

                    text = text.trim();
                    if (text.startsWith("hhttp")) {
                        text = text.replace("hhttp", "http");
                    }

                    String[] urls = text.split("#");
                    for (String url : urls) {
                        url = url.trim();
                        url = url.replace(":80/", "/");
                        url = url.replace(":443/", "/");

                        if (maps.containsKey(url)) {
                            System.err.println(maps.get(text) + " - " + title);

                            bw2.write(maps.get(text) + " - " + title + "\n");
                            bw2.write(title + "," + text + "\n");
                            bw2.flush();
                        } else {

                            bw.write(title + "," + text + "\n");
                            bw.flush();
                        }
                    }
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
