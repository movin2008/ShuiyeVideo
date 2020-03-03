package com.shuiyes.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UrlFormater {

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
					if(url.contains("baiducdnct.inter.iqiyi.com")){
						url = "http://"+url.substring(url.indexOf("baiducdnct.inter.iqiyi.com"));
						bw.write( title+","+url + "\n");
					}else if(url.contains("ottrrs.hl.chinamobile.com")){
						url = url.substring(url.indexOf("ottrrs.hl.chinamobile.com"));
						url = "http://"+url.substring(0, url.indexOf("?"));
						bw.write( title+","+url + "\n");
					}else if(url.startsWith("http://39.135.34")){
						int i1 = url.toLowerCase().indexOf("&contentid=");
						if(i1 == -1){
							System.err.println("i1? "+url);
						}
						
						url = url.substring(0, i1);
						bw.write( title+","+url + "\n");
					}else if(url.startsWith("http://39.135.36")){
						int i1 = url.toLowerCase().indexOf("&contentid=");
						if(i1 == -1){
							System.err.println("i1? "+url);
						}
						
						url = "http://39.135.34.8"+url.substring(url.indexOf(":18890/"), i1);
						bw.write( title+","+url + "\n");
					}else{
						bw.write( text + "\n");
					}

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