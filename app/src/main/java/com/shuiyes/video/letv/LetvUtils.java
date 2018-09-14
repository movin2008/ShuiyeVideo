package com.shuiyes.video.letv;

import android.util.Log;

import com.shuiyes.video.util.HttpUtils;
import com.shuiyes.video.util.SHA1;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LetvUtils {


    /**
     #@DEPRECATED
     def get_timestamp():
         tn = random.random()
         url = 'http://api.letv.com/time?tn={}'.format(tn)
         result = get_content(url)
         return json.loads(result)['stime']

     #@DEPRECATED
     def get_key(t):
         for s in range(0, 8):
         e = 1 & t
         t >>= 1
         e <<= 31
         t += e
         return t ^ 185025305

     def calcTimeKey(t):
         ror = lambda val, r_bits, : ((val & (2**32-1)) >> r_bits%32) |  (val << (32-(r_bits%32)) & (2**32-1))
         magic = 185025305
         return ror(t, magic % 17) ^ magic
         #return ror(ror(t,773625421%13)^773625421,773625421%17)
     */
    public static long calcTimeKey(){
        long magic = 185025305;
        long tm = System.currentTimeMillis()/1000;
        return ror(tm, magic % 17) ^ magic;
    }


    public static long ror(long val, long r_bits){
        long l = Integer.MAX_VALUE;
        l = l*2+1; //4294967295
        return ((val & l) >> r_bits%32) |  (val << (32-(r_bits%32)) & l);
    }

    public static String fetchVideo(String url, boolean print) throws Exception {
        Log.e("HAHA", "url=" + url);

        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod("GET");
        HttpUtils.setURLConnection(conn);
        conn.connect();

        if (conn.getResponseCode() == 200) {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String read = null;
            StringBuffer ret = new StringBuffer();
            while ((read = in.readLine()) != null) {
                ret.append(read);
                if(print){
                    Log.e("HAHA", read);
                }
            }
            in.close();
            return ret.toString();
        } else {
            Log.i("https", "fetchVideo error");
            Map<String, List<String>> headers = conn.getHeaderFields();
            Set<String> keys = headers.keySet();
            Iterator<String> iterator = keys.iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                Log.e("HAHA", "key=" + key);

                List<String> values = headers.get(key);
                for (String value : values) {
                    Log.e("HAHA", "value=" + value);
                }
            }
        }
        return null;
    }

    public static String splatid = "105";

    public static String getVideoInfoUrl(String vid) {
        String url = "http://player-pc.le.com/mms/out/video/playJson?id="+vid
                + "&platid=1"
                + "&splatid=" + splatid
                + "&format=1"
                + "&tkey=" + calcTimeKey()
                + "&domain=www.le.com&region=cn&source=1000&accesyx=1";
        return url;
    }

    public static String getVideoPlayUrl(String url, String vid) {
        // 1536840193220
        double tn = System.currentTimeMillis();
        tn = tn/10000000;
        tn = tn/1000000;
        String uuid =  SHA1.encode(url) + "_0";

        url = url.replace("tss=0", "tss=ios");
        url += "&m3v=1&termid=1&format=1&hwtype=un&ostype=MacOS10.12.4&p1=1&p2=10&p3=-&expect=3";
        url += "&tn=" + tn;
        url += "&vid=" + vid;
        url += "&uuid=" + uuid;
        url += "&sign=letv";
        return url;
    }

}
