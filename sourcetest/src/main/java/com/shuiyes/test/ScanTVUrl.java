package com.shuiyes.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class ScanTVUrl {

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
		SimpleDateFormat sdf = new SimpleDateFormat("MMddHHmmss");
		String out = sdf.format(new Date()) + ".list";
		String error = sdf.format(new Date()) + "_e.list";
		System.out.println(out);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(out)));
		BufferedWriter bw2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(error)));

		List<String> sets = new ArrayList<String>();
		
		for(int i = 10; i < 256; i++){
			String ip = "117.184.45."+i;
			String iptext = "上海移动,"+ip;
			String url = "http://"+ip+"/tlivecloud-cdn.ysp.cctv.cn/001/2000266303.m3u8";

			System.out.println();
			System.out.println(url);
			if(HttpUtils.get(url)){
				sets.add(iptext);
				bw.write("CCTV-4K," + url + "\n");
				bw.flush();
			}else{
				if("403".equals(HttpUtils.E)){
					sets.add(iptext);
				}
				bw2.write("待定" + i + "," + url + "\n");
				bw2.flush();
			}
			Thread.sleep(50);
		}
		bw.close();
		bw2.close();
		System.out.println("end");

		System.out.println();
		System.out.println();
		Iterator<String> iterator = sets.iterator();
		while(iterator.hasNext()){
			System.out.println(iterator.next());
		}
	}
	
	static void c() throws Exception {
		String filename = "test.list";
		FileInputStream in = new FileInputStream(filename);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));

		if (filename.endsWith(".list")) {

			String text = null;
			while ((text = br.readLine()) != null) {
				String split = ",";
				if (text.contains(split)) {
					String[] tmp = text.split(",");
					if(HttpUtils.get("http://"+tmp[1]+"/tlivecloud-cdn.ysp.cctv.cn/001/2000266303.m3u8")){
						System.out.println(tmp[1]);
					}else{
						System.err.println(tmp[1]);
					}
				}else{
				}
			}
		} 

		br.close();
		
		System.out.println("end");
	}

}
