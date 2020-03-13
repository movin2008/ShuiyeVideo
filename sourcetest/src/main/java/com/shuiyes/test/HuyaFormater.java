package com.shuiyes.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HuyaFormater {

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
		System.out.println(out);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(out)));

		if (filename.endsWith(".list")) {

			String text = null;
			while ((text = br.readLine()) != null) {
				String split = ",";
				if (text.contains(split)) {
					String[] tmp = text.split(split);
					String title = tmp[0];
					String url = tmp[1];
					
					if(url.contains("huya.com")){
						url = url.replace("http://", "https://");
						url = url.replace("txdirect.hls", "aldirect.hls");
						url = url.replace("jsdirect.hls", "aldirect.hls");
						url = url.replace("js.hls", "aldirect.hls");
						url = url.replace("tx.hls", "aldirect.hls");
						url = url.replace("al.hls", "aldirect.hls");
						url = url.replace("1_1200.m3u8", "1.m3u8");
						url = url.replace("1_2000.m3u8", "1.m3u8");
						
						url = url.replace("/backsrc/", "/huyalive/");
						url = url.replace("aldirect.hls", "aldirect.rtmp");
					}
					
					bw.write( title+","+url + "\n");
				}else{
					bw.write( text + "\n");
					bw.flush();
				}
			}
		} 

		br.close();
		bw.close();
		
		System.out.println("end");
	}
}