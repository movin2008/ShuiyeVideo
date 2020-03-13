package com.shuiyes.test;

public class Log {

	public static void e(String text) {
		System.out.println(text);
	}
	
	public static void e(String tag, String text) {
		System.out.println(tag+", "+text);
	}

}
