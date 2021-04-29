package com.shuiyes.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.io.InputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

public class HuyaTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					//a();
					String[] ret = huya(11342412);
					System.err.println(Arrays.toString(ret));

					String url = newHuyaUrl(ret[1]);
					System.err.println("newHuyaUrl: " + url);
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
	
	static String[] huya(int rid) throws Exception {

		final String[] rets = new String[2];
		final URL url = new URL("https://m.huya.com/" + rid);
		final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		setURLConnection(conn, null);
		conn.setRequestMethod("GET");
		conn.connect();

		int code = conn.getResponseCode();
		if (code == 200) {
			String text;
			InputStream in = conn.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(in,"utf-8"));
			while ((text = br.readLine()) != null) {
				System.out.println(text);
				if (text.contains("var liveLineUrl =")) {
					rets[1] = "https:" + new String(Base64.getDecoder().decode(getVariableValue(text, "\"")));
				} else if (text.contains("var liveRoomName = ")) {
					rets[0] = getVariableValue(text, "'");
				}
			}
			br.close();
		} else {
			System.err.println("Response code: " + code);
		}
		conn.disconnect();
		return rets;
	}

	public static final String UA = "Mozilla/5.0 (Linux; U; Android 9; zh-cn; MI 6 Build/PKQ1.190118.001) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/66.0.3359.126 MQQBrowser/10.0 Mobile Safari/537.36";

	static void setURLConnection(HttpURLConnection conn, String headers) {
		conn.setConnectTimeout(3333);
		conn.setReadTimeout(5555);
		conn.setRequestProperty("Host", conn.getURL().getHost());
		conn.setRequestProperty("User-Agent", UA);

		if (!isEmpty(headers)) {
			// Cookie: a=123; b=234; Referer: url
			String[] header = headers.split(": ");
			conn.setRequestProperty(header[0], header[1]);
		}
	}

	static boolean isEmpty(String s) {
		return s == null || s.length() == 0;
	}

	static String getVariableValue(String text, String marks) {
		return text.substring(text.indexOf(marks) + marks.length(), text.lastIndexOf(marks));
	}
	
	static String getUrlParam(String url, String param) {
		param = param + "=";
		int index = url.indexOf(param) + param.length();
		return URLDecoder.decode(url.substring(index, url.indexOf("&", index)));
	}
	
	static String stringToMD5(String text) {
        byte[] secretBytes = null;
        try {
            secretBytes = MessageDigest.getInstance("md5").digest(text.getBytes());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("no such md5.");
        }
        String md5code = new BigInteger(1, secretBytes).toString(16);
        for (int i = 0; i < 32 - md5code.length(); i++) {
            md5code = "0" + md5code;
        }
        return md5code;
    }
	
	static String newHuyaUrl(String url) {
		String[] tmp = url.split("\\?");
		String[] tmp2 = tmp[0].split("/");
		
		String ayyuid = tmp2[tmp2.length - 1].replace(".m3u8", "").replace(".flv", "");
		//System.err.println("ayyuid: " + ayyuid);
		
		String fm = new String(Base64.getDecoder().decode(getUrlParam(url, "fm")));
		//System.err.println("fm: " + fm);
		
		// 16195946561773750
		long currentTimeMillis = System.currentTimeMillis() * 10000;
		//System.err.println("currentTimeMillis: " + currentTimeMillis);
		
		String wsTime = getUrlParam(url, "wsTime");
		//System.err.println("wsTime: " + wsTime);
		
		fm = fm.replace("$0", "0");
		fm = fm.replace("$1", ayyuid);
		fm = fm.replace("$2", String.valueOf(currentTimeMillis));
		fm = fm.replace("$3", wsTime);
		//System.err.println("fm: " + fm);
		
		String wsSecret = getUrlParam(url, "wsSecret");
		
		// tx.hls.huya.com
		// bd.hls.huya.com
		// migu-bd.hls.huya.com
		String newurl = url + "&seqid=" + currentTimeMillis;
		newurl = newurl.replace("ratio=2000", "u=0");
		newurl = newurl.replace(wsSecret, stringToMD5(fm));
		
		return newurl;
	}
}
