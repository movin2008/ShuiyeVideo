package com.shuiyes.video.os;

import android.os.IBinder;

import java.lang.reflect.Method;

public class ServiceManager {

    public static IBinder getService(String name) {
        try {
            Class clz = Class.forName("android.os.ServiceManager");
            Method getService = clz.getMethod("getService", String.class);
            IBinder binder = (IBinder) getService.invoke(null, name);
            return binder;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void addService(String name, IBinder service) {
        try {
            Class clz = Class.forName("android.os.ServiceManager");
            Method addService = clz.getMethod("addService", String.class, IBinder.class);
            addService.invoke(null, name, service);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
