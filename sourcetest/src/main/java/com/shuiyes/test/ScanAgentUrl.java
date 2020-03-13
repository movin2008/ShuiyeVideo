package com.shuiyes.test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ScanAgentUrl {
	
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
		String filename = "test.list";
		FileInputStream in = new FileInputStream(filename);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		
		List<String> sets = new ArrayList<String>();
		
		String text = null;
		while ((text = br.readLine()) != null) {
			if (text.startsWith("##") || text.trim().length() == 0) {
				// 注释
				continue;
			}
			
			String split = ",";
			if (text.contains(split)) {
				String[] tmp = text.split(split);
				
				HttpURLConnection conn = null;
				try {
		            conn = (HttpURLConnection) new URL(tmp[1]).openConnection();
		            HttpUtils.setURLConnection(conn, null);
		            conn.setRequestMethod("GET");
		            conn.setInstanceFollowRedirects(false);
		            conn.connect();
		            int code = conn.getResponseCode();
		            Log.e(tmp[1]+" ResponseCode " + code);
		            
		            if (code == 301 || code == 302) {
		            	System.err.println(conn.getHeaderField("Location"));
		            	sets.add(tmp[0]+","+conn.getHeaderField("Location"));
		            }else if(code == 200){
		            	BufferedReader br2 = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		            	System.err.println("readLine " + br2.readLine()+".");
		            	br2.close();
		            }
		        } catch (Exception e) {
		        	e.printStackTrace();
		        } finally {
		            if (conn != null) {
		                conn.disconnect();
		            }
		        }
			}
		}
		br.close();
		
		System.out.println("end");

		System.out.println();
		System.out.println();
		Iterator<String> iterator = sets.iterator();
		while(iterator.hasNext()){
			System.out.println(iterator.next());
		}
	}

}
