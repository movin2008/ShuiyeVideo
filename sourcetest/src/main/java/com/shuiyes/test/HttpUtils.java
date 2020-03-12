package com.shuiyes.test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class HttpUtils {

    public static final String UA_MAC = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.79 Safari/537.36";
    public static final String UA_WIN = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36";
    public static final String UA_ANDROID = "Mozilla/5.0 (Linux; U; Android 9; zh-cn; MI 6 Build/PKQ1.190118.001) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/66.0.3359.126 MQQBrowser/10.0 Mobile Safari/537.36  ";
    public static final String UA_WX = "Mozilla/5.0 (Linux; Android 8.0; MI 6 Build/OPR1.170623.027; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/57.0.2987.132 MQQBrowser/6.2 TBS/044208 Mobile Safari/537.36 MicroMessenger/6.7.2.1340(0x2607023A) NetType/4G Language/zh_CN";
    public static final String UA = UA_WIN;

    public static void setURLConnection(HttpURLConnection conn, String headers) {
        conn.setConnectTimeout(3333);
        conn.setReadTimeout(9999);
        conn.setRequestProperty("Host", conn.getURL().getHost());
        conn.setRequestProperty("User-Agent", UA);
//        conn.setRequestProperty("Charset", "UTF-8");

        if (!isEmpty(headers)) {
            // Cookie: a=123; b=234; Referer: url
            String[] header = headers.split(": ");
            conn.setRequestProperty(header[0], header[1]);
        }
    }

    static boolean isEmpty(String s) {
        return s == null || s.length() == 0;
    }

    public static boolean get(String url) {
        url = url.trim();
        if (url.startsWith("vjms") || url.startsWith("mitv") || url.toLowerCase().startsWith("p2p") || url.startsWith("p8p")) {
            return false;
        }
        if (url.startsWith("rtmp") || url.startsWith("rtsp")
                || url.startsWith("tvbus")
//    			|| url.startsWith("http")
                || url.startsWith("mms")) {
            return true;
        }

        return HttpUtils.get(url, false);
    }

    public static boolean get(String url, boolean forCookie) {
        return HttpUtils.get(url, null, forCookie);
    }

    public static String E = "";

    public static boolean get(String url, String headers, boolean forCookie) {

        boolean ret = false;
        HttpURLConnection conn = null;
        try {
            if (url.startsWith("https://")) {
                conn = initHttpsURLConnection(url);
            } else {
                conn = (HttpURLConnection) new URL(url).openConnection();
            }

            HttpUtils.setURLConnection(conn, headers);
            conn.setRequestMethod("GET");
            conn.connect();
            int code = conn.getResponseCode();
            Log.e("ResponseCode", "" + code);
            E = "" + code;

            if (code == 200) {
                InputStream in = conn.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String text = br.readLine();
                if (text != null && text.startsWith("#EXTM3U")) {
                    ret = true;
                } else {
                    E = "302";
                    System.err.println("readLine " + text);
                }
                br.close();
            } else if (code == 301 || code == 302) {
                Thread.sleep(500);
                return HttpUtils.get(conn.getHeaderField("Location"), headers, forCookie);
            } else {
                E = "" + code;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e1) {
            }
            return HttpUtils.get(url, headers, forCookie);
        } catch (Exception e) {
            E = e.getLocalizedMessage();
            System.err.println(e.getLocalizedMessage());
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        return ret;
    }

    private static HttpsURLConnection initHttpsURLConnection(String url) throws Exception {
        HttpsURLConnection conn = (HttpsURLConnection) new URL(url).openConnection();
        SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, new TrustManager[]{new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        }}, new SecureRandom());
        conn.setSSLSocketFactory(sslContext.getSocketFactory());
        conn.setHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
        return conn;
    }

}