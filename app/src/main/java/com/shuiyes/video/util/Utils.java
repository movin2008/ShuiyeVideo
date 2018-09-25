package com.shuiyes.video.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

public class Utils {

    public static void setFile(String path, String info){
        try {
            File file = new File(path);
            if (file.exists()) {
                file.delete();
            }

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            bw.write(info);
            bw.close();
        }catch (Exception e){
        }
    }

}
