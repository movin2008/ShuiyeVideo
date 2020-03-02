package com.shuiyes.test;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

// 源有效性测试
public class GuessTVUrl {

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
//					b();
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

		for(int i=9000; i< 10000; i++){
			String id;
			if(i < 10){
				id = "00"+i;
			}else if(i < 100){
				id = "0"+i;
			}else{
				id = ""+i;
			}
			System.out.print(id+" ");
//			String url = "http://httpdvb.slave.ttcatv.tv:13164/playurl?playtype=live&protocol=hls&accesstoken=R5CA2B7CAU3090C010K77540044IFB84556FPBM3220A5DV1044EZ33519WE22942B42A1&playtoken=&programid=4200000"+id+".m3u8";
//			String url = "http://59.46.18.134:8114/Fsv_otype=1?FvSeid=5bf181c25f226bb2&Fsv_filetype=1&Fsv_ctype=LIVES&Fsv_cid=0&Fsv_chan_hls_se_idx="+id+"&Fsv_rate_id=0&Fsv_SV_PARAM1=0&Fsv_ShiftEnable=0&Fsv_ShiftTsp=0&Provider_id=&Pcontent_id=&Fsv_CMSID=&Fsv_otype=1";
//			String url = "http://59.46.18.133:8114/lntvch/01000000000000000000000000000"+id+"?Fsv_otype=1&FvSeid=&Fsv_filetype=&Fsv_ctype=&Fsv_cid=&Fsv_chan_hls_se_idx=&Fsv_rate_id=&Fsv_TBt=&Fsv_ShiftEnable=&Fsv_ShiftTsp=&Fsv_SV_PARAM1=&Provider_id=&Pcontent_id=";
			String url = "http://hwltc.tv.cdn.zj.chinamobile.com/pltv/88888888/224/322122"+id+"/index.m3u8";

			
			if(HttpUtils.get(url)){
				bw.write("待定" + i + "," + url + "\n");
				bw.flush();
			}else{
				bw2.write("待定" + i + "," + url + "\n");
				bw2.flush();
			}
			Thread.sleep(100);
		}
		bw.close();
		bw2.close();

		System.out.println("end");
	}
	
	static void b() throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("MMddHHmmss");
		String out = sdf.format(new Date()) + ".list";
		String error = sdf.format(new Date()) + "_e.list";
		System.out.println(out);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(out)));
		BufferedWriter bw2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(error)));
		
		for(int i=0; i<= 2000; i++){
			String url = "http://z9.syasn.com/c/c"+i;
			
			System.out.println();
			System.out.println(url);
			if(HttpUtils.get(url)){
				bw.write("美女车模" + i + "," + url + "\n");
				bw.flush();
			}else{
				bw2.write("美女热舞" + i + "," + url + "\n");
				bw2.flush();
			}
			Thread.sleep(100);
		}
		bw.close();
		bw2.close();
		
		System.out.println("end");
	}

}
