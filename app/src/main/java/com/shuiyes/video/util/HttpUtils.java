package com.shuiyes.video.util;

import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class HttpUtils {

    private final static String TAG = "HttpUtils";

    public static String FormateUrl(String url) {
        if (url.startsWith("//")) {
            url = "http:" + url;
        }
        return url;
    }

    public static final String UA_MAC = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.79 Safari/537.36";
    public static final String UA_WIN = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36";
    public static final String UA_ANDROID = "Mozilla/5.0 (Linux; U; Android 9; zh-cn; MI 6 Build/PKQ1.190118.001) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/66.0.3359.126 MQQBrowser/10.0 Mobile Safari/537.36  ";
    public static final String UA_WX = "Mozilla/5.0 (Linux; Android 8.0; MI 6 Build/OPR1.170623.027; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/57.0.2987.132 MQQBrowser/6.2 TBS/044208 Mobile Safari/537.36 MicroMessenger/6.7.2.1340(0x2607023A) NetType/4G Language/zh_CN";
    public static final String UA = UA_WIN;

    public static void setURLConnection(HttpURLConnection conn, String headers) {
        conn.setConnectTimeout(15000);
        conn.setReadTimeout(15000);
        conn.setRequestProperty("Host", conn.getURL().getHost());
        conn.setRequestProperty("User-Agent", UA);
//        conn.setRequestProperty("Charset", "UTF-8");

        if (!TextUtils.isEmpty(headers)) {
            // Cookie: a=123;
            // Referer: url
            String[] header = headers.split(": ");
            conn.setRequestProperty(header[0], header[1]);
        }
    }

    public static String printHeaders(URLConnection conn) {
        Map<String, List<String>> headers = conn.getHeaderFields();
        Set<String> keys = headers.keySet();
        Iterator<String> iterator = keys.iterator();

        String code = "HTTP/1.1 unkown";
        StringBuffer buf = new StringBuffer();
        while (iterator.hasNext()) {
            String key = iterator.next();

            buf.setLength(0);
            List<String> values = headers.get(key);
            for (String value : values) {
                buf.append(value + ",");
            }
            if (key == null || "null".equals(key)) code = buf.toString();
            Log.e(TAG, key + ": " + buf.toString());
        }
        return code;
    }

    public static String get(String url) {
        return HttpUtils.get(url, false);
    }

    public static String get(String url, boolean forCookie) {
        return HttpUtils.get(url, null, forCookie);
    }

    public static String get(String url, String headers, boolean forCookie) {
//        return debugHeader(headers);

        String ret = "Exception: ";
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
            Log.e(TAG, "GET " + url + " " + code);

            if (code == 200) {
                StringBuffer buffer = new StringBuffer();
                if (forCookie) {
                    Map<String, List<String>> headermMap = conn.getHeaderFields();
                    List<String> setCookies = headermMap.get("set-cookie");
                    if (setCookies != null && setCookies.size() > 0) {
                        String setCookie = setCookies.get(0);
                        buffer.append(setCookie.substring(0, setCookie.indexOf(";") + 1));
                    } else {
                        ret += printHeaders(conn);
                        buffer.append(ret);
                    }
                } else {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                    String read;
                    while ((read = in.readLine()) != null) {
                        buffer.append(read);
                    }
                    in.close();
                }

                ret = buffer.toString();
            } else if (code == 301 || code == 302) {
                Thread.sleep(500);
                return HttpUtils.get(conn.getHeaderField("Location"), headers, forCookie);
            } else if (code == 400) {
                Thread.sleep(500);
                return HttpUtils.get(url, headers, forCookie);
            } else {
                ret += printHeaders(conn);
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e1) {
            }
            return HttpUtils.get(url, headers, forCookie);
        } catch (Exception e) {
            Log.e(TAG, "GET " + url);
            e.printStackTrace();
            ret += e.getLocalizedMessage();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        return ret;
    }

    public static String post(String url, String params, String headers) {
//        return debugHeader(headers);

        String ret = "Exception: ";
        HttpURLConnection conn = null;
        try {
            if (url.startsWith("https://")) {
                conn = initHttpsURLConnection(url);
            } else {
                conn = (HttpURLConnection) new URL(url).openConnection();
            }
            HttpUtils.setURLConnection(conn, headers);
            conn.setRequestMethod("POST");
            conn.connect();

            //建立输入流，向指向的URL传入参数
            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
            dos.writeBytes(params);
            dos.flush();
            dos.close();

            int code = conn.getResponseCode();
            Log.e(TAG, "POST " + url + " " + code + "\n" + params);

            if (code == 200) {
                StringBuffer buffer = new StringBuffer();
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                String read;
                while ((read = in.readLine()) != null) {
                    buffer.append(read);
                }
                in.close();

                ret = buffer.toString();
            } else {
                ret += printHeaders(conn);
            }
        } catch (Exception e) {
            Log.e(TAG, "POST " + url + "\n" + params);
            e.printStackTrace();
            ret += e.getLocalizedMessage();
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
                //do nothing，接受任意客户端证书
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {
                //do nothing，接受任意客户端证书
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

    public static String debugHeader(String headers) {
        try {
            HttpURLConnection conn2 = (HttpURLConnection) new URL("http://www.shuiyes.com/test/header.php").openConnection();
            HttpUtils.setURLConnection(conn2, headers);
            conn2.connect();

            StringBuffer buffer = new StringBuffer();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn2.getInputStream(), "UTF-8"));
            String read;
            while ((read = in.readLine()) != null) {
                buffer.append(read);
            }

            Log.e(TAG, buffer.toString());

            in.close();
            conn2.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String testPost(String url) {
        Log.e(TAG, "open " + url);

//        HttpURLConnection conn = (HttpURLConnection) new URL("http://www.shuiyes.com/test/header.php").openConnection();
////        conn.setRequestProperty("Cookie", "cna=" + cna);
//        conn.setRequestProperty("Upgrade-Insecure-Requests", "1");
//        conn.setRequestProperty("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
//        conn.setRequestProperty("accept-encoding", "gzip, deflate, br");
//        conn.setRequestProperty("accept-language", "zh-CN,zh;q=0.9");
//        conn.setRequestProperty("cache-control", "max-age=0");
//        conn.setRequestProperty(":authority", conn.getURL().getAuthority());
//        conn.setRequestProperty(":path", conn.getURL().getPath()+"?"+conn.getURL().getQuery());
//        conn.setRequestProperty(":scheme", "https");
//        conn.setRequestProperty(":method", "GET");


        HttpsURLConnection conn = null;
        try {
            conn = (HttpsURLConnection) new URL(url).openConnection();
            HttpUtils.setURLConnection(conn, null);
            conn.setRequestMethod("POST");

            conn.setRequestProperty("Accept", "application/json, text/javascript, */*; q=0.01");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.84 Safari/537.36\n");
            conn.setRequestProperty("Referer", "https://v.qq.com/x/cover/lcpwn26degwm7t3/c00278xguy9.html");
            conn.setRequestProperty("Cookie", "_ga=GA1.2.1628600297.1527315996; pgv_pvi=2334756864; pgv_si=s9509040128; ptisp=cnc; RK=xVrpy2agZ9; ptcz=e3e9835e4da6ce6bb8d06990bb9fea731e0a36e1222f4c263dcefb48efe441ca; pt2gguin=o1970047742; uin=o1970047742; tvfe_boss_uuid=64d4afe2b2513bd8; pgv_pvid=4434933095; pgv_info=ssid=s8913420944; o_cookie=1970047742; appuser=B645031DFFF1C99F; o_minduid=I6ixoEmmP1WgU0euWuvH9M2UyzlQva9s; psessionid=7084ef14_1538398685_1970047742_80455; lv_play_indexl.=52; adid=1970047742; cm_cookie=V1,110065&6F3iukuzjF&AQEBP1tlKW44gBaieEN4bwkbNGK8aXdVa73q&180925&180925,110055&s01899943cc459b7dfe&AQEBjzQOHoY1-RfwzXkKwMuzUseqxrYIlnVk&180925&180925,110066&4Ed3g0gJkp30&AQEBeywwy_yLp98U2282FqvlKsGoz2KuaL9o&181001&181001; psessiontime=1538400677; LKBturn=667; LPVLturn=705; LVMturn=945; Lturn=592; LBSturn=552; LCZCturn=416; LPSJturn=820; LZCturn=387; LZIturn=913; localfcs_B645031DFFF1C99F=55bqis=1538409053_2&m_8h088b=1538409053_2&m_8zlwwm=1538409053_2&m_9ubu8h=1538409053_1&m_dawe0q=1538409053_2&m_k3mk5u=1538409053_1&m_ntq0o0=1538409053_2; ptui_loginuin=1970047742; skey=@8eqMJUUFP");

            conn.connect();

            String vinfoparam = "charge=0&defaultfmt=auto&otype=ojson&sdtfrom=v1010&defnpayver=1&sphttps=1&spwm=4&defn=&fhdswitch=0&show1080p=1&isHLS=1&dtype=3&sphls=1&spgzip=&dlver=&drm=32&spau=1&spaudio=15&defsrc=1&encryptVer=7.1&fp2p=1";
            url += "&appVer=3.6.1";
//        url += "&platform=10901";
            url += "&platform=11";
            url += "&ehost=" + URLEncoder.encode("https://v.qq.com/x/cover/lcpwn26degwm7t3/a002708679j.html", "utf-8");
            url += "&vid=a002708679j";
            url += "&timestamp=" + Utils.timestamp();

            String param = "vinfoparam=" + URLEncoder.encode(vinfoparam, "UTF-8");
            param += ";adparam=" + URLEncoder.encode("pf=in&ad_type=LD%7CKB%7CPVL&pf_ex=pc&url=https%3A%2F%2Fv.qq.com%2Fx%2Fcover%2Flcpwn26degwm7t3%2Fz0027injhcq.html&refer=https%3A%2F%2Fv.qq.com%2Ftv%2F&ty=web&plugin=1.0.0&v=3.6.1&coverid=lcpwn26degwm7t3&vid=c00278xguy9&pt=&flowid=91bd8fcf1c0ebd25b8c1d5aea73230d5_10901&vptag=www_baidu_com%7Cvideolist%3Aclick&pu=-1&chid=0&adaptor=2&dtype=1&live=0&resp_type=json&guid=7bc7120ffc74caf9985f7f3a7a829312&req_type=1&from=0&appversion=1.0.130&platform=10901&tpid=2&rfid=669eb114150738d23aa55ff35d49f05c_1538408946", "utf-8");
            param += ";buid=vinfoad";

            //建立输入流，向指向的URL传入参数
            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
            dos.writeBytes(param);
            dos.flush();
            dos.close();

            int code = conn.getResponseCode();
            if (code == 200) {
                StringBuffer ret = new StringBuffer();
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                String read;
                while ((read = in.readLine()) != null) {
                    ret.append(read);
                    Log.e(TAG, read);
                }
                in.close();

                Utils.setFile("test", ret.toString());

                return ret.toString();
            } else {
                printHeaders(conn);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return null;
    }

}
