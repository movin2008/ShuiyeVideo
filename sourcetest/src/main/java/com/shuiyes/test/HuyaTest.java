package com.shuiyes.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HuyaTest {

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
		BufferedWriter bw2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(out.replace(".list", "_e.list"))));

		if (filename.endsWith(".list")) {

			String text = null;
			while ((text = br.readLine()) != null) {
				String split = ",";
				if (text.contains(split)) {
					String[] tmp = text.split(split);
					String title = tmp[0];
					String url = tmp[1];
					
					boolean run = true;
			        String[] sources = {"aldirect", "jsdirect", "txdirect", "al", "js", "tx"};
			        for (String source : sources) {
			            String nurl = url.replace("aldirect", source);
			            String nurl2 = url.replace("huyalive", "backsrc");
			            
			            String[] tmps = {nurl, nurl.replace(".m3u8", "_1200.m3u8"), nurl.replace(".m3u8", "_2000.m3u8"), nurl.replace(".m3u8", "_2500.m3u8"),
			            		nurl2, nurl2.replace(".m3u8", "_1200.m3u8"), nurl2.replace(".m3u8", "_2000.m3u8"), nurl2.replace(".m3u8", "_2500.m3u8")};
			            
			            for (String s : tmps) {
			            	System.out.println();
			            	System.out.println(s);
			            	if(HttpUtils.get(s)){
			            		run = false;
			            		break;
			            	}
			            }
			            
			            if(!run){
			            	break;
			            }
			        }
					
			        if(!run){
			        	bw.write( title+","+url + "\n");
						bw.flush();
			        }else{
			        	bw2.write( title+","+url + "\n");
						bw2.flush();
			        }
					
				}else{
					bw.write( text + "\n");
					bw.flush();
				}
			}
		} 

		br.close();
		bw.close();
		bw2.close();
		
		System.out.println("end");
	}
}