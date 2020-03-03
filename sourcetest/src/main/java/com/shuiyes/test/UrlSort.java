package com.shuiyes.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

public class UrlSort {

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
		String filename = "test.list";
		FileInputStream in = new FileInputStream(filename);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));

		SimpleDateFormat sdf = new SimpleDateFormat("MMddHHmmss");
		String out = sdf.format(new Date()) + ".list";
		String error = sdf.format(new Date()) + "_e.list";
		System.out.println(out);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(out)));
		BufferedWriter bw2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(error)));

		TreeMap<String, String> maps = new TreeMap<String, String>();
		
		String text = null;
		while ((text = br.readLine()) != null) {
			String split = ",";
			if (text.contains(split)) {
				String[] tmp = text.split(split);
				String title = tmp[0];
				String url = tmp[1];
				
				// 去重
				if(maps.containsKey(url)){
					System.err.println(maps.get(url) +" - "+title);
					
					bw2.write(text + "\n");
					bw2.flush();
				}else{
					maps.put(url,  title);
				}
			}else{
				bw2.write(text + "\n");
				bw2.flush();
			}
		}
		
		Set<String> sets = maps.keySet();
		
		Iterator<String> iterator = sets.iterator();
		while(iterator.hasNext()){
			String url = iterator.next();
			bw.write(maps.get(url) +","+url + "\n");
			bw.flush();
		}

		br.close();
		bw.close();
		bw2.close();
		
		System.out.println("end");
	}

}
