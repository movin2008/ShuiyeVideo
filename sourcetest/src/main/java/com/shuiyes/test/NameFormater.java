package com.shuiyes.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NameFormater {

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
		String filename = "test.list";
		FileInputStream in = new FileInputStream(filename);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));

		SimpleDateFormat sdf = new SimpleDateFormat("MMddHHmmss");
		String out = sdf.format(new Date()) + ".list";
		String error = sdf.format(new Date()) + "_e.list";
		System.out.println(out);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(out)));
		BufferedWriter bw2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(error)));

		if (filename.endsWith(".list")) {

			String text = null;
			while ((text = br.readLine()) != null) {
				String split = ",";
				if (text.contains(split)) {
					
					// 分类
					if(text.contains("·")){
						
						String[] tmp = text.split(",");
						String title = tmp[0];
						String url = tmp[tmp.length - 1];
						
						title = title.substring(0, title.indexOf("·"));
						
						bw.write(title +","+url + "\n");
						bw.flush();
					}else if(text.contains("【")){
						
						String[] tmp = text.split(",");
						String title = tmp[0];
						String url = tmp[tmp.length - 1];
						
						title = text.substring(text.indexOf("【") + 1);
						title = title.substring(0, title.indexOf("】"));
						
						bw.write(title +","+url + "\n");
						bw.flush();
					}else if(text.contains("《")){
						
						String[] tmp = text.split(",");
						String title = tmp[0];
						String url = tmp[tmp.length - 1];
						
						title = text.substring(text.indexOf("《") + 1);
						title = title.substring(0, title.indexOf("》"));
						
						bw.write(title +","+url + "\n");
						bw.flush();
					}else if(text.contains(" ")){
						
						String[] tmp = text.split(",");
						String title = tmp[0];
						String url = tmp[tmp.length - 1];
						
						title = title.substring(0, title.indexOf(" "));
						
						bw.write(title +","+url + "\n");
						bw.flush();
					}else{
						bw2.write(text + "\n");
						bw2.flush();
					}
				}else{
					bw.write( text + "\n");
					bw.flush();
					
					bw2.write(text + "\n");
					bw2.flush();
				}
			}
		} 

		br.close();
		bw.close();
		bw2.close();
		
		System.out.println("end");
	}
}