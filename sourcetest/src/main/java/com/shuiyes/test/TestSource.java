package com.shuiyes.test;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;

// 源有效性测试
public class TestSource {

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
        String filename = "sourcetest/src/main/assets/test.list";
        FileInputStream in = new FileInputStream(filename);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));

        SimpleDateFormat sdf = new SimpleDateFormat("MMddHHmmss");
        String out = "tmp/" + sdf.format(new Date()) + ".list";
        String error = "tmp/" + sdf.format(new Date()) + "_e.list";
        System.out.println(out);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(out)));
        BufferedWriter bw2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(error)));

        int flag = 0;
        if (filename.endsWith(".list")) {

            String text = null;
            while ((text = br.readLine()) != null) {
                if (text.startsWith("##") || text.trim().length() == 0) {
                    // 注释
                    continue;
                }

                String split = ",";
                if (text.contains(split)) {
                    System.out.println();
                    System.out.println(flag++ + " " + text);

                    String[] tmp = text.split(split);
                    String title = tmp[0];

                    String[] urls = tmp[1].split("#");
                    for (String url : urls) {
                        url = url.trim();
                        url = url.replace(":80/", "/");
                        url = url.replace(":443/", "/");

                        if (HttpUtils.get(url)) {
                            bw.write(title + "," + url + "\n");
                            bw.flush();
                        } else {
                            bw2.write(title + "," + url + "\n");
                            bw2.flush();
                        }
                    }
                } else {
                    bw.write("\n" + text + "\n");
                    bw.flush();

                    bw2.write("\n" + text + "\n");
                    bw2.flush();
                }
            }
        } else if (filename.endsWith(".dpl")) {
            String text = null;
            String url = null;
            while ((text = br.readLine()) != null) {
                if (text.startsWith("##")) {
                    // 注释
                    continue;
                }

                if (text.contains("*")) {
                    String[] tmp = text.split("\\*");
                    if ("file".equals(tmp[1])) {
                        System.out.println();
                        System.out.println(flag++ + "" + text);

                        url = tmp[2];
                    } else if ("title".equals(tmp[1])) {
                        System.out.println(text);

                        String title = tmp[2];
                        if (HttpUtils.get(url)) {
                            bw.write(title + "," + url + "\n");
                            bw.flush();
                        } else {
                            bw2.write(title + "," + url + "\n");
                            bw2.flush();
                        }

                    }
                }
            }
        } else if (filename.endsWith(".m3u")) {
            String text = null;
            String title = null;
            String groupTitle = "";
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
                    text = text.replace(":80/", "/");
                    text = text.replace(":443/", "/");
                    if (text.startsWith("hhttp")) {
                        text = text.replace("hhttp", "http");
                    }
//					if(text.contains(",")){
//						text = text.substring(text.lastIndexOf(",") + 1);
//					}

                    if (HttpUtils.get(text)) {
                        bw.write(title + "," + text + "\n");
                        bw.flush();
                    } else {
                        bw2.write(title + "," + text + "\n");
                        bw2.flush();
                    }
                    title = null;
                }

            }
        } else if (filename.endsWith(".qtitv")) {
            String text = null;
            StringBuffer buffer = new StringBuffer();
            while ((text = br.readLine()) != null) {
                buffer.append(text);
            }
            br.close();

            System.out.println(buffer.length());
            JSONArray arr = new JSONArray(buffer.toString());

            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);

                bw.write("\n[Group]" + obj.getString("name") + "\n");
                bw.flush();

                JSONArray data = obj.getJSONArray("data");
                for (int j = 0; j < data.length(); j++) {
                    JSONObject tmp = data.getJSONObject(j);
                    JSONArray source = tmp.getJSONArray("source");
                    for (int k = 0; k < source.length(); k++) {
                        bw.write(tmp.getString("name") + "," + source.getString(k) + "\n");
                        bw.flush();
                    }
                }
            }
        }

        br.close();
        bw.close();
        bw2.close();

        System.out.println(out + " end");
    }

}