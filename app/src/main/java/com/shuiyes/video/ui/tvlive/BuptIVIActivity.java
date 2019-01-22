package com.shuiyes.video.ui.tvlive;

import android.os.Bundle;
import com.shuiyes.video.base.BaseTVLiveActivity;
import com.shuiyes.video.bean.ListVideo;

public class BuptIVIActivity extends BaseTVLiveActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public String getApi() {
        return "http://ivi.bupt.edu.cn";
    }

    @Override
    public String getPlayUrl(String tv) {
        return "http://ivi.bupt.edu.cn/hls/cctv" + tv + "hd.m3u8";
    }

    @Override
    public void refreshVideos(String result) throws Exception {
        String start = "<div class=\"2u";
        while (result.contains(start)) {

            int startIndex = result.indexOf(start);
            int endIndex = result.indexOf("</div>", startIndex + start.length());
            String data = null;
            if (endIndex != -1) {
                data = result.substring(startIndex, endIndex);
            } else {
                data = result.substring(startIndex);
            }

            // 直播源名称
            String key = "<p>";
            int len = data.indexOf(key);
            String tmp = data.substring(len + key.length());
            len = tmp.indexOf("</p>");
            String title = tmp.substring(0, len);

            // 直播源地址
            key = "href=\"";
            len = data.indexOf(key);
            len = data.indexOf(key, len + key.length());
            tmp = data.substring(len + key.length());
            len = tmp.indexOf("\"");
            String href = tmp.substring(0, len);
            mVideos.add(new ListVideo(title, title, getApi() + href));

            result = result.substring(startIndex + start.length());
        }
    }

}

/*
<!-- 20190121 -->
<!DOCTYPE HTML>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
      <title>ivi测试频道列表</title>
      <script src="skel.min.js"></script>
      <script>
        skel.init();
      </script>
	<script>
		var _hmt = _hmt || [];
		(function() {
		  var hm = document.createElement("script");
		  hm.src = "//hm.baidu.com/hm.js?a4204bc23465785669145ff9c36e7232";
		  var s = document.getElementsByTagName("script")[0];
		  s.parentNode.insertBefore(hm, s);
		})();
	</script>

    </head>
    <style>
    body{
        font-family: "ff-tisa-web-pro-1","ff-tisa-web-pro-2","Lucida Grande","Helvetica Neue",Helvetica,Arial,"Hiragino Sans GB","Hiragino Sans GB W3","WenQuanYi Micro Hei",sans-serif;
        width: 100%;
        height: 100%;
        margin: auto;
        background-repeat: repeat-x;
        background-image: -webkit-linear-gradient(45deg, #ADCAC8, #082274);
        background-image: -moz-linear-gradient(45deg, #ADCAC8, #082274);
        background-image: linear-gradient(45deg, #ADCAC8, #082274);
        background-attachment: fixed;
    }
    .container{
        margin-top:100px;
    }
    .well {
        min-height: 20px;
        padding: 19px;
        padding-bottom:40px;
        margin-bottom: 20px;
        background-color: #f5f5f5;
        border: 1px solid #e3e3e3;
        -webkit-border-radius: 4px;
        -moz-border-radius: 4px;
        border-radius: 4px;
        -webkit-box-shadow: inset 0 1px 1px rgba(0,0,0,0.05);
        -moz-box-shadow: inset 0 1px 1px rgba(0,0,0,0.05);
        box-shadow: inset 0 1px 1px rgba(0,0,0,0.05);
    }
    a{
        text-decoration:none;
        color:blue;
    }
    </style>
    <body>
      <div class="container well">
          <div class="row">
              <div class="8u -2u">
                  <h1 style="text-align:center">
			IVI测试（注意：此页面计算校外流量）
                  </h1>
              </div>
          </div>
          <div class="row" style="margin-top:50px">
              <div class="2u -2u">
                  <p>CCTV-1高清</p>
                  <a class="icon1" href="/player.html?channel=cctv1hd" target="_blank">PC端</a>&nbsp;
                  <a class="icon1" href="/hls/cctv1hd.m3u8" target="_blank">移动端</a>
              </div>
              <div class="2u">
                  <p>CCTV-3高清</p>
                  <a class="icon1" href="/player.html?channel=cctv3hd" target="_blank" >PC端</a>&nbsp;
                  <a class="icon1" href="/hls/cctv3hd.m3u8" target="_blank" >移动端</a>
              </div>
              <div class="2u">
                  <p>CCTV-5+高清</p>
                  <a class="icon1" href="/player.html?channel=cctv5phd" target="_blank" >PC端</a>&nbsp;
                  <a class="icon1" href="/hls/cctv5phd.m3u8" target="_blank" >移动端</a>
              </div>
          </div>
          <div class="row" style="margin-top:50px">
              <div class="2u -2u">
                  <p>CCTV-6高清</p>
                  <a class="icon1" href="/player.html?channel=cctv6hd" target="_blank" >PC端</a>&nbsp;
                  <a class="icon1" href="/hls/cctv6hd.m3u8" target="_blank" >移动端</a>
              </div>
              <div class="2u">
                  <p>CCTV-8高清</p>
                  <a class="icon1" href="/player.html?channel=cctv8hd" target="_blank" >PC端</a>&nbsp;
                  <a class="icon1" href="/hls/cctv8hd.m3u8" target="_blank" >移动端</a>
              </div>
              <div class="2u">
                  <p>CHC高清电影</p>
                  <a class="icon1" href="/player.html?channel=chchd" target="_blank" >PC端</a>&nbsp;
                  <a class="icon1" href="/hls/chchd.m3u8" target="_blank" >移动端</a>
              </div>
          </div>

          <div class="row" style="margin-top:50px">
              <div class="2u -2u">
                  <p>北京卫视高清</p>
                  <a class="icon1" href="/player.html?channel=btv1hd" target="_blank">PC端</a>&nbsp;
                  <a class="icon1" href="/hls/btv1hd.m3u8" target="_blank">移动端</a>
              </div>
              <div class="2u">
                  <p>北京文艺高清</p>
                  <a class="icon1" href="/player.html?channel=btv2hd" target="_blank" >PC端</a>&nbsp;
                  <a class="icon1" href="/hls/btv2hd.m3u8" target="_blank" >移动端</a>
              </div>
              <div class="2u">
                  <p>北京体育高清</p>
                  <a class="icon1" href="/player.html?channel=btv6hd" target="_blank" >PC端</a>&nbsp;
                  <a class="icon1" href="/hls/btv6hd.m3u8" target="_blank" >移动端</a>
              </div>
              <div class="2u">
                  <p>北京纪实高清</p>
                  <a class="icon1" href="/player.html?channel=btv11hd" target="_blank" >PC端</a>&nbsp;
                  <a class="icon1" href="/hls/btv11hd.m3u8" target="_blank" >移动端</a>
              </div>
          </div>


          <div class="row" style="margin-top:50px">
              <div class="2u -2u">
                  <p>湖南卫视高清</p>
                  <a class="icon1" href="/player.html?channel=hunanhd" target="_blank">PC端</a>&nbsp;
                  <a class="icon1" href="/hls/hunanhd.m3u8" target="_blank">移动端</a>
              </div>
              <div class="2u">
                  <p>浙江卫视高清</p>
                  <a class="icon1" href="/player.html?channel=zjhd" target="_blank" >PC端</a>&nbsp;
                  <a class="icon1" href="/hls/zjhd.m3u8" target="_blank" >移动端</a>
              </div>
              <div class="2u">
                  <p>江苏卫视高清</p>
                  <a class="icon1" href="/player.html?channel=jshd" target="_blank" >PC端</a>&nbsp;
                  <a class="icon1" href="/hls/jshd.m3u8" target="_blank" >移动端</a>
              </div>
              <div class="2u">
                  <p>东方卫视高清</p>
                  <a class="icon1" href="/player.html?channel=dfhd" target="_blank" >PC端</a>&nbsp;
                  <a class="icon1" href="/hls/dfhd.m3u8" target="_blank" >移动端</a>
              </div>
          </div>

          <div class="row" style="margin-top:50px">
              <div class="2u -2u">
                  <p>安徽卫视高清</p>
                  <a class="icon1" href="/player.html?channel=ahhd" target="_blank">PC端</a>&nbsp;
                  <a class="icon1" href="/hls/ahhd.m3u8" target="_blank">移动端</a>
              </div>
              <div class="2u">
                  <p>黑龙江卫视高清</p>
                  <a class="icon1" href="/player.html?channel=hljhd" target="_blank" >PC端</a>&nbsp;
                  <a class="icon1" href="/hls/hljhd.m3u8" target="_blank" >移动端</a>
              </div>
              <div class="2u">
                  <p>辽宁卫视高清</p>
                  <a class="icon1" href="/player.html?channel=lnhd" target="_blank" >PC端</a>&nbsp;
                  <a class="icon1" href="/hls/lnhd.m3u8" target="_blank" >移动端</a>
              </div>
              <div class="2u">
                  <p>深圳卫视高清</p>
                  <a class="icon1" href="/player.html?channel=szhd" target="_blank" >PC端</a>&nbsp;
                  <a class="icon1" href="/hls/szhd.m3u8" target="_blank" >移动端</a>
              </div>
          </div>

          <div class="row" style="margin-top:50px">
              <div class="2u -2u">
                  <p>广东卫视高清</p>
                  <a class="icon1" href="/player.html?channel=gdhd" target="_blank">PC端</a>&nbsp;
                  <a class="icon1" href="/hls/gdhd.m3u8" target="_blank">移动端</a>
              </div>
              <div class="2u">
                  <p>天津卫视高清</p>
                  <a class="icon1" href="/player.html?channel=tjhd" target="_blank" >PC端</a>&nbsp;
                  <a class="icon1" href="/hls/tjhd.m3u8" target="_blank" >移动端</a>
              </div>
              <div class="2u">
                  <p>湖北卫视高清</p>
                  <a class="icon1" href="/player.html?channel=hbhd" target="_blank" >PC端</a>&nbsp;
                  <a class="icon1" href="/hls/hbhd.m3u8" target="_blank" >移动端</a>
              </div>
              <div class="2u">
                  <p>山东卫视高清</p>
                  <a class="icon1" href="/player.html?channel=sdhd" target="_blank" >PC端</a>&nbsp;
                  <a class="icon1" href="/hls/sdhd.m3u8" target="_blank" >移动端</a>
              </div>
          </div>

          <div class="row" style="margin-top:50px">
              <div class="2u -2u">
                  <p>重庆卫视高清</p>
                  <a class="icon1" href="/player.html?channel=cqhd" target="_blank">PC端</a>&nbsp;
                  <a class="icon1" href="/hls/cqhd.m3u8" target="_blank">移动端</a>
              </div>
          </div>

          <div class="row" style="margin-top:50px">
              <div class="2u -2u">
                  <p>CCTV-1综合</p>
                  <a class="icon1" href="/player.html?channel=cctv1" target="_blank">PC端</a>&nbsp;
                  <a class="icon1" href="/hls/cctv1.m3u8" target="_blank">移动端</a>
              </div>
              <div class="2u">
                  <p>CCTV-2财经</p>
                  <a class="icon1" href="/player.html?channel=cctv2" target="_blank" >PC端</a>&nbsp;
                  <a class="icon1" href="/hls/cctv2.m3u8" target="_blank" >移动端</a>
              </div>
              <div class="2u">
                  <p>CCTV-3综艺</p>
                  <a class="icon1" href="/player.html?channel=cctv3" target="_blank" >PC端</a>&nbsp;
                  <a class="icon1" href="/hls/cctv3.m3u8" target="_blank" >移动端</a>
              </div>
              <div class="2u">
                  <p>CCTV-4中文国际</p>
                  <a class="icon1" href="/player.html?channel=cctv4" target="_blank" >PC端</a>&nbsp;
                  <a class="icon1" href="/hls/cctv4.m3u8" target="_blank" >移动端</a>
              </div>
          </div>
          <div class="row" style="margin-top:50px">
              <div class="2u">
                  <p>CCTV-6电影</p>
                  <a class="icon1" href="/player.html?channel=cctv6" target="_blank" >PC端</a>&nbsp;
                  <a class="icon1" href="/hls/cctv6.m3u8" target="_blank" >移动端</a>
              </div>
              <div class="2u">
                  <p>CCTV-7军事农业</p>
                  <a class="icon1" href="/player.html?channel=cctv7" target="_blank" >PC端</a>&nbsp;
                  <a class="icon1" href="/hls/cctv7.m3u8" target="_blank" >移动端</a>
              </div>
              <div class="2u">
                  <p>CCTV-8电视剧</p>
                  <a class="icon1" href="/player.html?channel=cctv8" target="_blank" >PC端</a>&nbsp;
                  <a class="icon1" href="/hls/cctv8.m3u8" target="_blank" >移动端</a>
              </div>
          </div>
          <div class="row" style="margin-top:50px">
              <div class="2u -2u">
                  <p>CCTV-9纪录</p>
                  <a class="icon1" href="/player.html?channel=cctv9" target="_blank">PC端</a>&nbsp;
                  <a class="icon1" href="/hls/cctv9.m3u8" target="_blank">移动端</a>
              </div>
              <div class="2u">
                  <p>CCTV-10科教</p>
                  <a class="icon1" href="/player.html?channel=cctv10" target="_blank" >PC端</a>&nbsp;
                  <a class="icon1" href="/hls/cctv10.m3u8" target="_blank" >移动端</a>
              </div>
              <div class="2u">
                  <p>CCTV-11戏曲</p>
                  <a class="icon1" href="/player.html?channel=cctv11" target="_blank" >PC端</a>&nbsp;
                  <a class="icon1" href="/hls/cctv11.m3u8" target="_blank" >移动端</a>
              </div>
              <div class="2u">
                  <p>CCTV-12社会与法</p>
                  <a class="icon1" href="/player.html?channel=cctv12" target="_blank" >PC端</a>&nbsp;
                  <a class="icon1" href="/hls/cctv12.m3u8" target="_blank" >移动端</a>
              </div>
          </div>
          <div class="row" style="margin-top:50px">
              <div class="2u -2u">
                  <p>CCTV-13新闻</p>
                  <a class="icon1" href="/player.html?channel=cctv13" target="_blank">PC端</a>&nbsp;
                  <a class="icon1" href="/hls/cctv13.m3u8" target="_blank">移动端</a>
              </div>
              <div class="2u">
                  <p>CCTV-14少儿</p>
                  <a class="icon1" href="/player.html?channel=cctv14" target="_blank" >PC端</a>&nbsp;
                  <a class="icon1" href="/hls/cctv14.m3u8" target="_blank" >移动端</a>
              </div>
              <div class="2u">
                  <p>CCTV-15音乐</p>
                  <a class="icon1" href="/player.html?channel=cctv15" target="_blank" >PC端</a>&nbsp;
                  <a class="icon1" href="/hls/cctv15.m3u8" target="_blank" >移动端</a>
              </div>
              <div class="2u">
                  <p>CCTV-NEWS</p>
                  <a class="icon1" href="/player.html?channel=cctv16" target="_blank" >PC端</a>&nbsp;
                  <a class="icon1" href="/hls/cctv16.m3u8" target="_blank" >移动端</a>
              </div>
          </div>

          <div class="row" style="margin-top:50px">
              <div class="2u -2u">
                  <p>北京卫视</p>
                  <a class="icon1" href="/player.html?channel=btv1" target="_blank">PC端</a>&nbsp;
                  <a class="icon1" href="/hls/btv1.m3u8" target="_blank">移动端</a>
              </div>
              <div class="2u">
                  <p>北京文艺</p>
                  <a class="icon1" href="/player.html?channel=btv2" target="_blank" >PC端</a>&nbsp;
                  <a class="icon1" href="/hls/btv2.m3u8" target="_blank" >移动端</a>
              </div>
              <div class="2u">
                  <p>北京科教</p>
                  <a class="icon1" href="/player.html?channel=btv3" target="_blank" >PC端</a>&nbsp;
                  <a class="icon1" href="/hls/btv3.m3u8" target="_blank" >移动端</a>
              </div>
              <div class="2u">
                  <p>北京影视</p>
                  <a class="icon1" href="/player.html?channel=btv4" target="_blank" >PC端</a>&nbsp;
                  <a class="icon1" href="/hls/btv4.m3u8" target="_blank" >移动端</a>
              </div>
          </div>
          <div class="row" style="margin-top:50px">
              <div class="2u -2u">
                  <p>北京财经</p>
                  <a class="icon1" href="/player.html?channel=btv5" target="_blank">PC端</a>&nbsp;
                  <a class="icon1" href="/hls/btv5.m3u8" target="_blank">移动端</a>
              </div>
              <div class="2u">
                  <p>北京体育</p>
                  <a class="icon1" href="/player.html?channel=btv6" target="_blank" >PC端</a>&nbsp;
                  <a class="icon1" href="/hls/btv6.m3u8" target="_blank" >移动端</a>
              </div>
              <div class="2u">
                  <p>北京生活</p>
                  <a class="icon1" href="/player.html?channel=btv7" target="_blank" >PC端</a>&nbsp;
                  <a class="icon1" href="/hls/btv7.m3u8" target="_blank" >移动端</a>
              </div>
              <div class="2u">
                  <p>北京青年</p>
                  <a class="icon1" href="/player.html?channel=btv8" target="_blank" >PC端</a>&nbsp;
                  <a class="icon1" href="/hls/btv8.m3u8" target="_blank" >移动端</a>
              </div>
          </div>
          <div class="row" style="margin-top:50px">
              <div class="2u -2u">
                  <p>北京新闻</p>
                  <a class="icon1" href="/player.html?channel=btv9" target="_blank">PC端</a>&nbsp;
                  <a class="icon1" href="/hls/btv9.m3u8" target="_blank">移动端</a>
              </div>
              <div class="2u">
                  <p>北京卡酷少儿</p>
                  <a class="icon1" href="/player.html?channel=btv10" target="_blank" >PC端</a>&nbsp;
                  <a class="icon1" href="/hls/btv10.m3u8" target="_blank" >移动端</a>
              </div>
          </div>
          <div class="row" style="margin-top:50px">
              <div class="2u -2u">
                  <p>深圳卫视</p>
                  <a class="icon1" href="/player.html?channel=sztv" target="_blank">PC端</a>&nbsp;
                  <a class="icon1" href="/hls/sztv.m3u8" target="_blank">移动端</a>
              </div>
              <div class="2u">
                  <p>安徽卫视</p>
                  <a class="icon1" href="/player.html?channel=ahtv" target="_blank" >PC端</a>&nbsp;
                  <a class="icon1" href="/hls/ahtv.m3u8" target="_blank" >移动端</a>
              </div>
              <div class="2u">
                  <p>河南卫视</p>
                  <a class="icon1" href="/player.html?channel=hntv" target="_blank" >PC端</a>&nbsp;
                  <a class="icon1" href="/hls/hntv.m3u8" target="_blank" >移动端</a>
              </div>
              <div class="2u">
                  <p>陕西卫视</p>
                  <a class="icon1" href="/player.html?channel=sxtv" target="_blank" >PC端</a>&nbsp;
                  <a class="icon1" href="/hls/sxtv.m3u8" target="_blank" >移动端</a>
              </div>
          </div>
          <div class="row" style="margin-top:50px">
              <div class="2u -2u">
                  <p>吉林卫视</p>
                  <a class="icon1" href="/player.html?channel=jltv" target="_blank">PC端</a>&nbsp;
                  <a class="icon1" href="/hls/jltv.m3u8" target="_blank">移动端</a>
              </div>
              <div class="2u">
                  <p>广东卫视</p>
                  <a class="icon1" href="/player.html?channel=gdtv" target="_blank" >PC端</a>&nbsp;
                  <a class="icon1" href="/hls/gdtv.m3u8" target="_blank" >移动端</a>
              </div>
              <div class="2u">
                  <p>山东卫视</p>
                  <a class="icon1" href="/player.html?channel=sdtv" target="_blank" >PC端</a>&nbsp;
                  <a class="icon1" href="/hls/sdtv.m3u8" target="_blank" >移动端</a>
              </div>
              <div class="2u">
                  <p>湖北卫视</p>
                  <a class="icon1" href="/player.html?channel=hbtv" target="_blank" >PC端</a>&nbsp;
                  <a class="icon1" href="/hls/hbtv.m3u8" target="_blank" >移动端</a>
              </div>
          </div>
          <div class="row" style="margin-top:50px">
              <div class="2u -2u">
                  <p>广西卫视</p>
                  <a class="icon1" href="/player.html?channel=gxtv" target="_blank">PC端</a>&nbsp;
                  <a class="icon1" href="/hls/gxtv.m3u8" target="_blank">移动端</a>
              </div>
              <div class="2u">
                  <p>河北卫视</p>
                  <a class="icon1" href="/player.html?channel=hebtv" target="_blank" >PC端</a>&nbsp;
                  <a class="icon1" href="/hls/hebtv.m3u8" target="_blank" >移动端</a>
              </div>
              <div class="2u">
                  <p>西藏卫视</p>
                  <a class="icon1" href="/player.html?channel=xztv" target="_blank" >PC端</a>&nbsp;
                  <a class="icon1" href="/hls/xztv.m3u8" target="_blank" >移动端</a>
              </div>
              <div class="2u">
                  <p>内蒙古卫视</p>
                  <a class="icon1" href="/player.html?channel=nmtv" target="_blank" >PC端</a>&nbsp;
                  <a class="icon1" href="/hls/nmtv.m3u8" target="_blank" >移动端</a>
              </div>
          </div>
          <div class="row" style="margin-top:50px">
              <div class="2u -2u">
                  <p>青海卫视</p>
                  <a class="icon1" href="/player.html?channel=qhtv" target="_blank">PC端</a>&nbsp;
                  <a class="icon1" href="/hls/qhtv.m3u8" target="_blank">移动端</a>
              </div>
              <div class="2u">
                  <p>四川卫视</p>
                  <a class="icon1" href="/player.html?channel=sctv" target="_blank" >PC端</a>&nbsp;
                  <a class="icon1" href="/hls/sctv.m3u8" target="_blank" >移动端</a>
              </div>
              <div class="2u">
                  <p>江苏卫视</p>
                  <a class="icon1" href="/player.html?channel=jstv" target="_blank" >PC端</a>&nbsp;
                  <a class="icon1" href="/hls/jstv.m3u8" target="_blank" >移动端</a>
              </div>
              <div class="2u">
                  <p>天津卫视</p>
                  <a class="icon1" href="/player.html?channel=tjtv" target="_blank" >PC端</a>&nbsp;
                  <a class="icon1" href="/hls/tjtv.m3u8" target="_blank" >移动端</a>
              </div>
          </div>
          <div class="row" style="margin-top:50px">
              <div class="2u -2u">
                  <p>山西卫视</p>
                  <a class="icon1" href="/player.html?channel=sxrtv" target="_blank">PC端</a>&nbsp;
                  <a class="icon1" href="/hls/sxrtv.m3u8" target="_blank">移动端</a>
              </div>
              <div class="2u">
                  <p>辽宁卫视</p>
                  <a class="icon1" href="/player.html?channel=lntv" target="_blank" >PC端</a>&nbsp;
                  <a class="icon1" href="/hls/lntv.m3u8" target="_blank" >移动端</a>
              </div>
              <div class="2u">
                  <p>厦门卫视</p>
                  <a class="icon1" href="/player.html?channel=xmtv" target="_blank" >PC端</a>&nbsp;
                  <a class="icon1" href="/hls/xmtv.m3u8" target="_blank" >移动端</a>
              </div>
              <div class="2u">
                  <p>新疆卫视</p>
                  <a class="icon1" href="/player.html?channel=xjtv" target="_blank" >PC端</a>&nbsp;
                  <a class="icon1" href="/hls/xjtv.m3u8" target="_blank" >移动端</a>
              </div>
          </div>
          <div class="row" style="margin-top:50px">
              <div class="2u -2u">
                  <p>东方卫视</p>
                  <a class="icon1" href="/player.html?channel=dftv" target="_blank">PC端</a>&nbsp;
                  <a class="icon1" href="/hls/dftv.m3u8" target="_blank">移动端</a>
              </div>
              <div class="2u">
                  <p>黑龙江卫视</p>
                  <a class="icon1" href="/player.html?channel=hljtv" target="_blank" >PC端</a>&nbsp;
                  <a class="icon1" href="/hls/hljtv.m3u8" target="_blank" >移动端</a>
              </div>
              <div class="2u">
                  <p>湖南卫视</p>
                  <a class="icon1" href="/player.html?channel=hunantv" target="_blank" >PC端</a>&nbsp;
                  <a class="icon1" href="/hls/hunantv.m3u8" target="_blank" >移动端</a>
              </div>
              <div class="2u">
                  <p>云南卫视</p>
                  <a class="icon1" href="/player.html?channel=yntv" target="_blank" >PC端</a>&nbsp;
                  <a class="icon1" href="/hls/yntv.m3u8" target="_blank" >移动端</a>
              </div>
          </div>
          <div class="row" style="margin-top:50px">
              <div class="2u -2u">
                  <p>江西卫视</p>
                  <a class="icon1" href="/player.html?channel=jxtv" target="_blank">PC端</a>&nbsp;
                  <a class="icon1" href="/hls/jxtv.m3u8" target="_blank">移动端</a>
              </div>
              <div class="2u">
                  <p>福建东南卫视</p>
                  <a class="icon1" href="/player.html?channel=dntv" target="_blank" >PC端</a>&nbsp;
                  <a class="icon1" href="/hls/dntv.m3u8" target="_blank" >移动端</a>
              </div>
              <div class="2u">
                  <p>浙江卫视</p>
                  <a class="icon1" href="/player.html?channel=zjtv" target="_blank" >PC端</a>&nbsp;
                  <a class="icon1" href="/hls/zjtv.m3u8" target="_blank" >移动端</a>
              </div>
              <div class="2u">
                  <p>贵州卫视</p>
                  <a class="icon1" href="/player.html?channel=gztv" target="_blank" >PC端</a>&nbsp;
                  <a class="icon1" href="/hls/gztv.m3u8" target="_blank" >移动端</a>
              </div>
          </div>
          <div class="row" style="margin-top:50px">
              <div class="2u -2u">
                  <p>宁夏卫视</p>
                  <a class="icon1" href="/player.html?channel=nxtv" target="_blank">PC端</a>&nbsp;
                  <a class="icon1" href="/hls/nxtv.m3u8" target="_blank">移动端</a>
              </div>
              <div class="2u">
                  <p>甘肃卫视</p>
                  <a class="icon1" href="/player.html?channel=gstv" target="_blank" >PC端</a>&nbsp;
                  <a class="icon1" href="/hls/gstv.m3u8" target="_blank" >移动端</a>
              </div>
              <div class="2u">
                  <p>重庆卫视</p>
                  <a class="icon1" href="/player.html?channel=cqtv" target="_blank" >PC端</a>&nbsp;
                  <a class="icon1" href="/hls/cqtv.m3u8" target="_blank" >移动端</a>
              </div>
              <div class="2u">
                  <p>兵团卫视</p>
                  <a class="icon1" href="/player.html?channel=bttv" target="_blank" >PC端</a>&nbsp;
                  <a class="icon1" href="/hls/bttv.m3u8" target="_blank" >移动端</a>
              </div>
          </div>
          <div class="row" style="margin-top:50px">
              <div class="2u -2u">
                  <p>旅游卫视</p>
                  <a class="icon1" href="/player.html?channel=lytv" target="_blank">PC端</a>&nbsp;
                  <a class="icon1" href="/hls/lytv.m3u8" target="_blank">移动端</a>
              </div>
          </div>

          <div class="row" style="margin-top:50px">
              <p class="8u -2u">
                1. PC端链接播放需要您的浏览器支持Flash控件；<br />
                2. ivi测试用，该页面播放会计算校外流量<br />
              </p>
              <p class="8u -2u">
              </p>
          </div>
      </div>
    </body>
</html>
*/