package com.shuiyes.video.ui.tvlive;

import android.os.Bundle;
import android.util.Log;

import com.shuiyes.video.bean.ListVideo;
import com.shuiyes.video.ui.base.BaseTVLiveActivity;
import com.shuiyes.video.util.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

public class SopPlusActivity extends BaseTVLiveActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("sopplus.tv");
    }

    @Override
    public String getApi() {
        return "http://chlist.sopplus.tv/v1/channels";
    }

    @Override
    public String getPlayUrl(int tv) {
        String ret = "";
        switch (tv){
            case 1:
                ret = "tvbus://1R8Rw3uqyfdkci7b9VWm7N17ynqv259h1Bo7i2uyi9bmq513uX";
            break;
            case 2:
                ret = "tvbus://12MGYHm2fp2wtTjiGpDsbD1odpShZDSn5ub2y74ok1HX5WB6wk";
            break;
            case 3:
                ret = "tvbus://1CA1abneBv2mhnLyEzjh42Fg3ehAuL1vQcbPUygwh7CXz5dejb";
            break;
            case 4:
                ret = "tvbus://12VFuSR8upLqpWEdDXipjrq1rci3sv8rzfDn8c1c7xymtEk8vwt";
            break;
        }
        return ret;
    }

    @Override
    public void refreshVideos(String result) throws Exception {
        Utils.setFile("chlist.sopplus.tv", result);

        JSONArray arr = new JSONArray(result);
        mVideos.clear();

        for (int i =0; i<arr.length(); i++){
            JSONObject obj = arr.getJSONObject(i);
            String type = obj.getString("type");
            if("public".equals(type)){
                String name = obj.getString("name");
                String address = obj.getString("address");
                mVideos.add(new ListVideo(name, name, address));
            }else{
                continue;
            }
        }
    }

}

/*
[
{
chid: 83399,
name: "c5",
address: "tvbus://1CdRb1Kmdj5Cq11mm6Kfpiu9ELCKSJSPFE9ztqYQPwJ5LQbwki",
startTime: 1578360336,
qs: 2,
qc: 39,
users: 2,
trending: 0.123,
from: "US",
type: "private"
},
{
chid: 322210,
name: "hycj",
address: "tvbus://16JPMPX8ZiYr1uYD9k7b5xud1VWzEQaVpE9iJDp1CA6WcghrLv",
startTime: 1578360337,
qs: 100,
qc: 100,
users: 2,
trending: 0.123,
from: "US",
type: "private"
},
{
chid: 361109,
name: "dsxw",
address: "tvbus://12SLTE5DJmmA6rfiZ2YTJoWsfa65JtZi8LpT8vRnCR2DNDiC6uj",
startTime: 1578360339,
qs: 96,
qc: 100,
users: 166,
trending: 0.123,
from: "US",
type: "private"
},
{
chid: 372495,
name: "eranews__",
address: "tvbus://128DtHrkkDFdLr36AxmTcLnKsykrK5UzMdrSeetPmTkLsL1tog6",
startTime: 1578337218,
qs: 100,
qc: 100,
users: 6,
trending: 0.123,
from: "US",
type: "private"
},
{
chid: 515864,
name: "pearl_",
address: "tvbus://1xZN4sZLpXmNmoXhvUMKCCAyaoXWsNzPVVvAt1GoYnW1DmFWVV",
startTime: 1578236734,
qs: 100,
qc: 100,
users: 2,
trending: 0.123,
from: "US",
type: "private"
},
{
chid: 816601,
name: "ztxw",
address: "tvbus://11HpcrMAWgFfFiKTXo2PW93FhQjrPzCYcYJFY1222pvcyzE5k2",
startTime: 1578360335,
qs: 88,
qc: 100,
users: 631,
trending: 0.123,
from: "US",
type: "private"
},
{
chid: 1065779,
name: "C1HK",
address: "tvbus://1AYhBSVJbQys66vHWjk8so2aSmHT4vZ7KsKMHxq6MBqJWMVAkS",
startTime: 1578360339,
qs: 100,
qc: 94,
users: 5,
trending: 0.123,
from: "US",
type: "private"
},
{
chid: 1325717,
name: "ndxw",
address: "tvbus://19ozTxdjWyrsp1x9RoUu5HtC9cSyeKgUTFHTg1ghELkDKVDW92",
startTime: 1578360340,
qs: 96,
qc: 100,
users: 94,
trending: 0.123,
from: "US",
type: "private"
},
{
chid: 1337823,
name: "huanyu2_",
address: "tvbus://15reMUdd54RmzaBgdhaRpS4dyGiZiVsy8BxkKoGea74aPdzT3D",
startTime: 1578013209,
qs: 100,
qc: 100,
users: 3,
trending: 0.123,
from: "US",
type: "private"
},
{
chid: 1438787,
name: "ftvHD_",
address: "tvbus://12JU7hZPmFe1etRh4G5QamebS6gUw2go1Qq4TSevxVGoA2fPopR",
startTime: 1578315991,
qs: 100,
qc: 100,
users: 2,
trending: 0.123,
from: "US",
type: "private"
},
{
chid: 1484261,
name: "鳳凰微信:iptv8000",
address: "tvbus://1iA7eK9Le3rdVhztgv3BbdF7CK4q1kEoVq3y9UNHSBRUkCjq2W",
startTime: 1578376716,
qs: 100,
qc: 85,
users: 2,
trending: 0.123,
from: "US",
type: "public"
},
{
chid: 1560642,
name: "ty008",
address: "tvbus://12WRDYLGSC8RzULdMyP7Rm3Haxks2N2cMtZsXziZGQieTPQNztx",
startTime: 1578360338,
qs: 88,
qc: 100,
users: 27,
trending: 0.123,
from: "US",
type: "private"
},
{
chid: 1677615,
name: "bloomberg_",
address: "tvbus://12LkYGgkAx7XCgPUYbuabj9McY6tLVGTRERFrSCEtX5GxajvBTB",
startTime: 1577931764,
qs: 100,
qc: 100,
users: 7,
trending: 0.123,
from: "TW",
type: "private"
},
{
chid: 1741256,
name: "ftvn",
address: "tvbus://1oEot9DVcHMBvSVN8h8E3DtF79ffC6n5gkSkVBDwu9BD7F13Ub",
startTime: 1578375790,
qs: 100,
qc: 100,
users: 28,
trending: 0.123,
from: "US",
type: "private"
},
{
chid: 1823190,
name: "cts_",
address: "tvbus://1KSQSa2sMsAmbLRv4T5mPo5XgzPVXheCNkqS3fC9bhdPAqYbe5",
startTime: 1578222485,
qs: 93,
qc: 100,
users: 2,
trending: 0.123,
from: "US",
type: "private"
},
{
chid: 2055447,
name: "RHK31",
address: "tvbus://12Jb9Laa77uM4KTTKY8zAkTASuN1S7VhhcNFKm8uPihjRBMoijp",
startTime: 1578373177,
qs: 100,
qc: 100,
users: 2,
trending: 0.123,
from: "US",
type: "private"
},
{
chid: 2617156,
name: "jade",
address: "tvbus://1xYhmD5kE6bK2j4FyNUhjVjrwmwj6igphfdpmyH9fvfRAjvmWf",
startTime: 1578360337,
qs: 100,
qc: 100,
users: 445,
trending: 0.123,
from: "US",
type: "private"
},
{
chid: 2714197,
name: "gdty",
address: "tvbus://129ZzjJfg8egWUvwYAXfNKHVD5JNdfnkEGxkNHZ2o8cwdwjw5WM",
startTime: 1578360337,
qs: 58,
qc: 96,
users: 14,
trending: 0.123,
from: "US",
type: "private"
},
{
chid: 2714437,
name: "hyxw",
address: "tvbus://12rS5gTka6mjJQJG8pzXnvEj25Sd2z72Jn3L97P1d6Kgvnc1Uzq",
startTime: 1578360335,
qs: 100,
qc: 100,
users: 94,
trending: 0.123,
from: "US",
type: "private"
},
{
chid: 3166136,
name: "scmhd_",
address: "tvbus://12uvKC2WZZqUkq1XNMRfyRX2Z1u4B4UkVHGcAPjS92L11LSvkM8",
startTime: 1578337236,
qs: 100,
qc: 100,
users: 6,
trending: 0.123,
from: "US",
type: "private"
},
{
chid: 3203126,
name: "HBO__",
address: "tvbus://19ShSpBQfZbsxzDTJ7pVLnfRqQw2rWtFiVsziFCvAzPVfUGp5U",
startTime: 1578372020,
qs: 100,
qc: 100,
users: 6,
trending: 0.123,
from: "US",
type: "private"
},
{
chid: 3204523,
name: "hyxw2",
address: "tvbus://1EYGMY3LDdtfPTPeABNn63qhmEXoQjk1JvPMPMcxCoQwKta2qm",
startTime: 1578360335,
qs: 100,
qc: 95,
users: 26,
trending: 0.123,
from: "US",
type: "private"
},
{
chid: 3346326,
name: "vlsports_",
address: "tvbus://12hHrAV5wNnDniHYsCe8LpN6JguqZ4wrb5m432QiGmmrswDAwsn",
startTime: 1578315984,
qs: 98,
qc: 100,
users: 2,
trending: 0.123,
from: "TW",
type: "private"
},
{
chid: 3421062,
name: "tvbNews_",
address: "tvbus://12pRH9Xw5UJf3oSZgZHdrpkzVK1rkzazguWGBrrTiMfkPd1aSid",
startTime: 1578337180,
qs: 90,
qc: 94,
users: 10,
trending: 0.123,
from: "US",
type: "private"
},
{
chid: 3476231,
name: "cnn_",
address: "tvbus://12nzYhyvD2vXYYxMwpnXbT43Rdeox43ceDvceK5r8e3rEysTkEE",
startTime: 1578226737,
qs: 100,
qc: 100,
users: 4,
trending: 0.123,
from: "TW",
type: "private"
},
{
chid: 3552951,
name: "ctv__",
address: "tvbus://1J2iEJienWkJWsykfP6UAbTJdrqbZVQhvUBGsBVJDtZzTLGY2P",
startTime: 1578222477,
qs: 100,
qc: 100,
users: 2,
trending: 0.123,
from: "US",
type: "private"
},
{
chid: 3563534,
name: "tvbsxw",
address: "tvbus://12YpKWfwGi18E6i93nPKDkW6meezZkhJUxJr7xMnKzDQr99AZZx",
startTime: 1578360335,
qs: 100,
qc: 100,
users: 168,
trending: 0.123,
from: "US",
type: "private"
},
{
chid: 3622332,
name: "ctinews_",
address: "tvbus://193RJkdvWo4jgR3niGUExuhV1zcTxqaAnWZ6k5c2wAxpJ1LZF8",
startTime: 1578041573,
qs: 100,
qc: 100,
users: 50,
trending: 0.123,
from: "TW",
type: "private"
},
{
chid: 3776515,
name: "bbc4_",
address: "tvbus://1wbhkcXtXG9qwBHU3QSu6yaqz68CLZjEN5DscJ6p6TitTGDqUu",
startTime: 1578264527,
qs: 100,
qc: 100,
users: 2,
trending: 0.123,
from: "US",
type: "private"
},
{
chid: 3817612,
name: "开电视",
address: "tvbus://1ZeBAKgnTZ5kiGHbnNeSPJ63Tm4zKDRJhX6dzytTYNPraQzUMX",
startTime: 1578360338,
qs: 100,
qc: 100,
users: 18,
trending: 0.123,
from: "US",
type: "private"
},
{
chid: 4008697,
name: "Ts",
address: "tvbus://1ywrZ8uAbGembKDiNVjRvk3iekw6wr8tLrmdGT9n3SWUbEBxVw",
startTime: 1578360344,
qs: 100,
qc: 100,
users: 8,
trending: 0.123,
from: "US",
type: "private"
},
{
chid: 4327229,
name: "tvbs55_",
address: "tvbus://12RtkYF5h3XwykxGy2UwSjRX3vEkuhRBXetn1Me1thDifbSgj7C",
startTime: 1577931749,
qs: 100,
qc: 100,
users: 13,
trending: 0.123,
from: "TW",
type: "private"
},
{
chid: 4420335,
name: "ftvnews_",
address: "tvbus://12pxceg3xyUvUF6CEA4cBmtFB9u5oahaeJwTV7wwTyrHhZJeg9A",
startTime: 1578290329,
qs: 100,
qc: 100,
users: 9,
trending: 0.123,
from: "US",
type: "private"
},
{
chid: 4439516,
name: "yxxw",
address: "tvbus://12gYb2oZ4dcAzTMS2EMoASk44xPqrQib2QWbuYDszBWTDuzfSZg",
startTime: 1578360341,
qs: 100,
qc: 100,
users: 895,
trending: 0.123,
from: "US",
type: "private"
},
{
chid: 4472236,
name: "VLsport",
address: "tvbus://1eBabRKDqBPU9zW1jYEq1mr6g4eokRs3UAtPBcbQD573aM5Y9h",
startTime: 1578360343,
qs: 98,
qc: 100,
users: 4,
trending: 0.123,
from: "US",
type: "private"
},
{
chid: 4483413,
name: "pearl",
address: "tvbus://1PJ3cgTdBx4o4QHH9vgLTLH54zdQYPAdEwjL4Bj8GaQv1LEBff",
startTime: 1578360337,
qs: 100,
qc: 100,
users: 14,
trending: 0.123,
from: "US",
type: "private"
},
{
chid: 4760090,
name: "CH80",
address: "tvbus://12RnHPgXZCsDfE8j2E2uJbagww4e3P8CZZ5WGySe8rRBz7bGoc5",
startTime: 1578348232,
qs: 96,
qc: 78,
users: 4,
trending: 0.123,
from: "",
type: "public"
},
{
chid: 5152396,
name: "CH90",
address: "tvbus://1YS9FSxS6A4XcMbsX7tmqJihUY3Mk6UKsCXux6qpL5BERg8Qjy",
startTime: 1578315476,
qs: 100,
qc: 98,
users: 15,
trending: 0.123,
from: "",
type: "public"
},
{
chid: 5289039,
name: "Tvbs",
address: "tvbus://13eaV2c1fpk73s4cqRFiS8JUbp8GDCHm79JYHJUNtwHS1qKiXA",
startTime: 1578360340,
qs: 96,
qc: 100,
users: 139,
trending: 0.123,
from: "US",
type: "private"
},
{
chid: 5370708,
name: "EttvAsia",
address: "tvbus://12tyrWKj1e3wV6ugYg7LjAyq71b1xTxe18WpkwoLq1KeEoTjCzC",
startTime: 1578360336,
qs: 100,
qc: 100,
users: 3,
trending: 0.123,
from: "US",
type: "private"
},
{
chid: 5435523,
name: "j2",
address: "tvbus://121goH2BZ9phPaCdP3wobzDzU9W8o9AHCYT5jaTMfshMER9ScHH",
startTime: 1578360337,
qs: 100,
qc: 100,
users: 48,
trending: 0.123,
from: "US",
type: "private"
},
{
chid: 5451034,
name: "ttv_",
address: "tvbus://12Ej7jswBNYZW67Li187EfEYc4pjHkvgq3pHzEkkXnGF9xNFY1u",
startTime: 1578222487,
qs: 98,
qc: 100,
users: 2,
trending: 0.123,
from: "US",
type: "private"
},
{
chid: 5613171,
name: "HKC新聞台HD",
address: "tvbus://1i8bTugoKo19Dwr6pbPydCp2DeyMq1f8Zu2ooNEdbksdSiJKC8",
startTime: 1578378070,
qs: 35,
qc: 52,
users: 1,
trending: 0.123,
from: "HK",
type: "private"
},
{
chid: 5913876,
name: "jade_",
address: "tvbus://12CyPgoRZ7dP2wbvY4JAPNb88zBPupvcbGWE7hp6uvHvUb3MV3M",
startTime: 1578361728,
qs: 100,
qc: 100,
users: 6,
trending: 0.123,
from: "IT",
type: "private"
},
{
chid: 5914464,
name: "tvbgongfu_",
address: "tvbus://1nmoR9Nty9nmCVbcfvTC2JqNLY91i3jr1vEb74bcZ8FETKpXiD",
startTime: 1578231838,
qs: 88,
qc: 99,
users: 3,
trending: 0.123,
from: "US",
type: "private"
},
{
chid: 6638946,
name: "fashiontv_",
address: "tvbus://12dtaek6WxZebkdw944gXce1voEbsLvCh1qUE82nLUsqxitKcR6",
startTime: 1578299352,
qs: 99,
qc: 100,
users: 6,
trending: 0.123,
from: "US",
type: "private"
},
{
chid: 6786114,
name: "yxdy",
address: "tvbus://12qmmPJvZyYchhyCxYBEVsuPZQkDRjBchnwwqMsP7dCPuVZHza4",
startTime: 1578360339,
qs: 100,
qc: 100,
users: 128,
trending: 0.123,
from: "US",
type: "private"
},
{
chid: 7231927,
name: "slxw",
address: "tvbus://12Z525TkyTpgJLxUx1F4xzQbUiLZT8WaydbPisMv12jscc7u8GH",
startTime: 1578365065,
qs: 100,
qc: 100,
users: 128,
trending: 0.123,
from: "US",
type: "private"
},
{
chid: 7451463,
name: "ettvcj_",
address: "tvbus://12mJKGon8EYRykfxLaD4fDM1L54yNXwmUNY1KeCpCmJa7F2SUHj",
startTime: 1578315980,
qs: 98,
qc: 100,
users: 4,
trending: 0.123,
from: "US",
type: "private"
},
{
chid: 7912253,
name: "鳳凰中文台HD",
address: "tvbus://12PHCXtRoWSs6ayxoEGSaPtQyk2L9nYCZUrbUYyJ5GVCnYqMaAM",
startTime: 1578324904,
qs: 90,
qc: 96,
users: 2,
trending: 0.123,
from: "HK",
type: "private"
},
{
chid: 8540870,
name: "CH70",
address: "tvbus://1oYf8WfnBZ6m1Aj8fomLxGHYKNRRYF4HKa2i8ZCMyfg5UAAzf7",
startTime: 1578271103,
qs: 92,
qc: 91,
users: 94,
trending: 0.123,
from: "",
type: "public"
},
{
chid: 8703575,
name: "HKC娱乐",
address: "tvbus://1WSdmYrTiYcYuK19Jh2hxjGTSZVja1oGTHUV1wsc4xPxACzYmT",
startTime: 1578360336,
qs: 100,
qc: 100,
users: 22,
trending: 0.123,
from: "US",
type: "private"
},
{
chid: 8719095,
name: "hs",
address: "tvbus://1SxBhRunWBsbsrDbzUXNPVMz1H66dpttprZ9xuKnW23pENVHb5",
startTime: 1578360382,
qs: 98,
qc: 100,
users: 5,
trending: 0.123,
from: "US",
type: "private"
},
{
chid: 8744246,
name: "Viutv",
address: "tvbus://12cUx99dsgAyDi2UhqNEQARnQg1igWP33e7UMnE8WSUtuZjNY2r",
startTime: 1578360337,
qs: 100,
qc: 100,
users: 17,
trending: 0.123,
from: "US",
type: "private"
},
{
chid: 8806157,
name: "CH800",
address: "tvbus://1rnhk2AEsGNXLsv7avw1SDYQPu9KtLNx8azfAHzLnb6S6FcwP7",
startTime: 1578344355,
qs: 89,
qc: 95,
users: 40,
trending: 0.123,
from: "US",
type: "public"
},
{
chid: 8854276,
name: "zs",
address: "tvbus://1TSXwyH8xBjnbLzyVDvMjZACv6ohHMzivbPJJU2Af83wC9AwGG",
startTime: 1578360386,
qs: 73,
qc: 99,
users: 5,
trending: 0.123,
from: "US",
type: "private"
},
{
chid: 8911466,
name: "setnews__",
address: "tvbus://1fCwLprSPuRR6Mxo1JRdgUqhqg5fVmbzS6zsxmqJSS3rVwCUtq",
startTime: 1577931757,
qs: 100,
qc: 100,
users: 7,
trending: 0.123,
from: "TW",
type: "private"
},
{
chid: 9108698,
name: "Asia",
address: "tvbus://1zChJfnxwMoKyrvY75pR4WrJHUivYnU9UCcvCNqVAfwgjNVGx6",
startTime: 1578360336,
qs: 100,
qc: 100,
users: 9,
trending: 0.123,
from: "US",
type: "private"
},
{
chid: 9181694,
name: "tvbwxxw",
address: "tvbus://12KoRs7a6cMoRg692TQnkvD82RV2QCGMXxkNhSKx8sZpob23rZG",
startTime: 1578360337,
qs: 100,
qc: 96,
users: 1529,
trending: 0.123,
from: "US",
type: "private"
},
{
chid: 9288022,
name: "HKC新聞台HD(Core)",
address: "tvbus://12JdHdkfpvKBG4Vz3my4SCwY1RhMXr5hCKfkg6agi6MN9aJvqc2",
startTime: 1578376718,
qs: 100,
qc: 100,
users: 1,
trending: 0.123,
from: "US",
type: "public"
},
{
chid: 9342822,
name: "RHK32",
address: "tvbus://12FKumCV7CWCnpKMf6wbkp5F7MWGimsyDeDHtbUQAgPkppRTgW9",
startTime: 1578360337,
qs: 100,
qc: 100,
users: 81,
trending: 0.123,
from: "US",
type: "private"
},
{
chid: 9578364,
name: "飛貓群:681107883",
address: "tvbus://12PNdc5ovdf1r4XG9anK5ChwYW7fLViGURvfj1vVKAYAZ8dBUU8",
startTime: 1578347362,
qs: 2,
qc: 5,
users: 1,
trending: 0.123,
from: "HK",
type: "private"
},
{
chid: 9808778,
name: "hk18_",
address: "tvbus://12WZHJcoG8jVPdi3mpLxXZjbLRANQ1wdsewY6Z5RhTjRbBhYdxo",
startTime: 1578316004,
qs: 100,
qc: 100,
users: 3,
trending: 0.123,
from: "IT",
type: "private"
},
{
chid: 9924753,
name: "wxcj001",
address: "tvbus://1gPq5BouPZ8qB5gX2nxCxtZUm93QfRQs4yeyh2k17ZoZK2tyiw",
startTime: 1578360340,
qs: 100,
qc: 99,
users: 36,
trending: 0.123,
from: "US",
type: "private"
},
{
chid: 9948282,
name: "ctinews_35",
address: "tvbus://1ydJGY12pzpsd3KwbqMPvj4VA7GSTgFVH43iRJH3W2ebXosS5Q",
startTime: 1578064225,
qs: 100,
qc: 100,
users: 14,
trending: 0.123,
from: "IT",
type: "private"
},
{
chid: 10324694,
name: "cctv5_",
address: "tvbus://1CfTZtziiysRW9ez8h45PZD9FDp5js3EMVkr36hu9SRtnKoT7K",
startTime: 1578260437,
qs: 98,
qc: 100,
users: 3,
trending: 0.123,
from: "US",
type: "private"
},
{
chid: 11214555,
name: "bbc_",
address: "tvbus://12CvfdqYzmxw147ewNwJUizabVfNeDzpmT5GKvbnEJhPbAYhxAn",
startTime: 1578250867,
qs: 100,
qc: 100,
users: 3,
trending: 0.123,
from: "US",
type: "private"
},
{
chid: 11360955,
name: "ctvnews__",
address: "tvbus://12AP2a3Er6pxZNyGEfppjoSLGafzUZzRn7dzYtuCQFg8ez94Qnx",
startTime: 1578362215,
qs: 100,
qc: 100,
users: 2,
trending: 0.123,
from: "US",
type: "private"
},
{
chid: 11661842,
name: "V2",
address: "tvbus://1L99Sn7x2r67RtPZQKF5S1AVVvfPpUnhTaAZ82y4JrMFxXC8CH",
startTime: 1578360335,
qs: 100,
qc: 94,
users: 14,
trending: 0.123,
from: "US",
type: "private"
},
{
chid: 11910639,
name: "CH50",
address: "tvbus://15kt11HTmNiHYbDF3VeSxDgtn4BCgLVeFBCPhzazURaMES8YWX",
startTime: 1578271842,
qs: 100,
qc: 94,
users: 9,
trending: 0.123,
from: "",
type: "public"
},
{
chid: 11962010,
name: "asiazh_",
address: "tvbus://1asqpFZNBpmMwmFfj5vmL1TiteuDYUkWvXrSi7tfuaU2EX5EFa",
startTime: 1578068736,
qs: 93,
qc: 100,
users: 6,
trending: 0.123,
from: "US",
type: "private"
},
{
chid: 12121785,
name: "cs",
address: "tvbus://12nwzSrGzypQSzAyUCeWQDNWNNuCjR8qWr2jHoxEm9RprKFP7sc",
startTime: 1578360337,
qs: 100,
qc: 100,
users: 2,
trending: 0.123,
from: "US",
type: "private"
},
{
chid: 12539132,
name: "cnbc_",
address: "tvbus://1ztzd13SbSRrq3zVQYS3Tjz8sQnxdxHeyuMULSz17T7SnUAbAp",
startTime: 1578250838,
qs: 100,
qc: 100,
users: 4,
trending: 0.123,
from: "US",
type: "private"
},
{
chid: 12635182,
name: "tv002",
address: "tvbus://1EanUuDTqdpWXYLti2RF6u5yuLo9WTg9KQC4A5zMb2j657VJbW",
startTime: 1578360345,
qs: 100,
qc: 99,
users: 25,
trending: 0.123,
from: "US",
type: "private"
},
{
chid: 12656731,
name: "yxw",
address: "tvbus://12rnLW2diJBhCrmKe5gVTdmfu68upRBn7v4XTQH9xpJgciTHBju",
startTime: 1578373240,
qs: 100,
qc: 100,
users: 130,
trending: 0.123,
from: "US",
type: "private"
},
{
chid: 13045090,
name: "ffnewS_",
address: "tvbus://12FVj95udgLkTRUPoRBSfBQcBfMjnmfAbjga8piBFAfXCjefBgN",
startTime: 1577931776,
qs: 100,
qc: 100,
users: 4,
trending: 0.123,
from: "TW",
type: "private"
},
{
chid: 13066535,
name: "三立微信:likeniptv",
address: "tvbus://12G1urXSEgZ929EmDi7KCZjfHyfofWdzaEDLzuDpw8ZFwkvBNnV",
startTime: 1578324880,
qs: 100,
qc: 100,
users: 2,
trending: 0.123,
from: "HK",
type: "private"
},
{
chid: 13417241,
name: "hlwmovies_",
address: "tvbus://12Mj5c2ixzbie4BpnfVMhrk2CsHTkKENSCjTB1dc88JDqTjT1YB",
startTime: 1578147125,
qs: 64,
qc: 85,
users: 4,
trending: 0.123,
from: "US",
type: "private"
},
{
chid: 13492247,
name: "nat_",
address: "tvbus://1f1k4g36apL89B4pTazuiJBTajLqqaGw8g6p4zUULevWaTdXAh",
startTime: 1578337220,
qs: 86,
qc: 100,
users: 4,
trending: 0.123,
from: "US",
type: "private"
},
{
chid: 13798932,
name: "icabnewS_",
address: "tvbus://1bhsnTvfSS5w4qrPGQbwYazrBKDZ56G2m2MMHXHbFET8vxyGS",
startTime: 1578260296,
qs: 100,
qc: 100,
users: 21,
trending: 0.123,
from: "US",
type: "private"
},
{
chid: 13990481,
name: "nhk_",
address: "tvbus://12ESXVa4WYLn53QbzfLLcnfkc4Z6p36bwMnDzo9XU62cNyiQYGz",
startTime: 1578368496,
qs: 100,
qc: 100,
users: 4,
trending: 0.123,
from: "US",
type: "private"
},
{
chid: 14514933,
name: "HKC綜合娛樂台HD",
address: "tvbus://12RxiCjyJ9Y1rojVD2FEARVNtt1SFKADznsrUxpstwnA8GCnsg6",
startTime: 1578324871,
qs: 48,
qc: 100,
users: 2,
trending: 0.123,
from: "HK",
type: "private"
},
{
chid: 14666600,
name: "CtiAsia",
address: "tvbus://128wau4HzNn3YVeFjmqBv3zdm4F5Xywt4EDo9XTCmxvHaJhdn1E",
startTime: 1578360336,
qs: 100,
qc: 100,
users: 9,
trending: 0.123,
from: "US",
type: "private"
},
{
chid: 14711479,
name: "ifun_",
address: "tvbus://1WbnWJ8eP5VKz5AJJTPUaeaFAVwC9UXjG4z56HA7kqnScidPkD",
startTime: 1578355846,
qs: 100,
qc: 100,
users: 2,
trending: 0.123,
from: "US",
type: "private"
},
{
chid: 14716538,
name: "鳳凰微信:iptv8000",
address: "tvbus://12kaNRZuchg5tKPjrvUeN2W1dwngr845jYrPZM59iuEB1odqp8Z",
startTime: 1578373928,
qs: 100,
qc: 100,
users: 1,
trending: 0.123,
from: "HK",
type: "private"
},
{
chid: 14744038,
name: "jadeHD_",
address: "tvbus://118YwyiUsiUXPPh9QPBx45H1vaNyfCcYqRKgicHtRUMaHoESGR",
startTime: 1578337181,
qs: 93,
qc: 96,
users: 11,
trending: 0.123,
from: "US",
type: "private"
},
{
chid: 15036603,
name: "ebcnews_",
address: "tvbus://1L6sUXNxm3SuR4fCtoRj5YL54vhY3x6UUVzwMoFqfbcv46iwqe",
startTime: 1578289725,
qs: 100,
qc: 100,
users: 14,
trending: 0.123,
from: "TW",
type: "private"
},
{
chid: 15101722,
name: "Atv",
address: "tvbus://12uhaiHRuEwUUkZ9XZjnvdUgvzL3DAywRd6ME5vVLuVXmXXjzad",
startTime: 1578360336,
qs: 100,
qc: 100,
users: 25,
trending: 0.123,
from: "US",
type: "private"
},
{
chid: 15308036,
name: "tvbs56__",
address: "tvbus://1615S6L1Y7m7MJFfX7c1ssS8eBwL5V9uPyx3PYRWpQGFxZ7MKE",
startTime: 1578375038,
qs: 52,
qc: 85,
users: 3,
trending: 0.123,
from: "US",
type: "private"
},
{
chid: 15312651,
name: "CBNSTV2",
address: "tvbus://12f5ccBEyvik54ZhdX92yoTsdHHc5p3Trp44rKZaNx2RtLRVfLu",
startTime: 1578192552,
qs: 100,
qc: 100,
users: 2,
trending: 0.123,
from: "CA",
type: "public"
},
{
chid: 15346941,
name: "ms",
address: "tvbus://12fpBgTFcgPj8xUd2kgBeXgGaV3RoKVi19Uq18Qa7bU8d9N6ubk",
startTime: 1578360352,
qs: 84,
qc: 100,
users: 33,
trending: 0.123,
from: "US",
type: "private"
},
{
chid: 15589986,
name: "CBNSTV",
address: "tvbus://12Yj9SU1yvj5FxFWMh3nuxaJgbxjru8jkuv49LAoQFxvTVKewE3",
startTime: 1577966436,
qs: 100,
qc: 100,
users: 1,
trending: 0.123,
from: "CA",
type: "public"
},
{
chid: 15773705,
name: "XH",
address: "tvbus://1kB8xDrz8Qdp1N3ELP86WqqejUBvo2SS8n7BszvjWkeCd6HMyz",
startTime: 1578376133,
qs: 2,
qc: 27,
users: 7,
trending: 0.123,
from: "US",
type: "private"
},
{
chid: 15884279,
name: "xiumi-001",
address: "tvbus://12sXP3aCVpBmarN79JgVj9irR1bqwsvwazZH63icum2HYJ5odFk",
startTime: 1578312894,
qs: 100,
qc: 100,
users: 1,
trending: 0.123,
from: "TW",
type: "private"
},
{
chid: 16403435,
name: "三立微信:likeniptv",
address: "tvbus://12awwG9hXCKwPiAx8oEp7rBkmow1XcuST741Fr1YMkE8Rte3uc9",
startTime: 1578376704,
qs: 100,
qc: 100,
users: 1,
trending: 0.123,
from: "US",
type: "public"
},
{
chid: 16512954,
name: "huanyu1",
address: "tvbus://1dQpvnxdDjBCLiTFoL6LWqzYhpHgmvyCJ4KAGZZU5rhbhwr1Wi",
startTime: 1578226593,
qs: 100,
qc: 95,
users: 12,
trending: 0.123,
from: "US",
type: "private"
},
{
chid: 1121,
name: "鳳凰資訊",
address: "tvbus://12sRsVsHunbSaGra5WoiBZF7d4zhDKovZhVMDxUe2AvDbACg7hF",
startTime: 1493128629,
qs: 100,
qc: 95,
users: 10,
trending: 0.33333334,
from: "HK",
type: "public"
}
]
*/