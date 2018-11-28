package com.shuiyes.video.util;

import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import java.io.File;

public class MemoryUtil {

    private static boolean externalMemoryAvailable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    //获取外部可用存储空间
    public static float getAvailableExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSizeLong();
            long availableBlocks = stat.getAvailableBlocksLong();
            return availableBlocks * blockSize;
        } else {
            return -1;
        }
    }

    //获取外部总共存储空间
    public static float getTotalExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSizeLong();
            long totalBlocks = stat.getBlockCountLong();
            return totalBlocks * blockSize;
        } else {
            return -1;
        }
    }

    // 内置存储(/sdcard)真实路径
    public static final String SDCARD_PATH = "/sdcard/";

    /**
     * 获取内部存储空间
     * @return
     */
    public static long[] getInternalMemorySize() {
        return getPathSize(SDCARD_PATH);
    }

    /**
     * 获取DATA分区空间
     * @return
     */
    public static long[] getDataMemorySize() {
        return getPathSize(Environment.getDataDirectory().getAbsolutePath());
    }

    private static long[] getPathSize(String path){
        StatFs stat = new StatFs(path);
        long blockSize = stat.getBlockSizeLong();
        long availableBlocks = stat.getAvailableBlocksLong();
        long totalBlocks = stat.getBlockCountLong();

        long available = blockSize*availableBlocks;
        long total = blockSize*totalBlocks;

        final long[] size = {available, total};
        return size;
    }

}
