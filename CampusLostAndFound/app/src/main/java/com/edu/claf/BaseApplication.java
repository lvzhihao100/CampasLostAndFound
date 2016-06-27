package com.edu.claf;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Handler;

import org.xutils.x;

import java.util.LinkedList;
import java.util.List;

/**
 * 用来结束所有后台activity
 */
public class BaseApplication extends Application {

    //运用list来保存们每一个activity是关键
    private List<Activity> mList = new LinkedList<Activity>();
    //为了实现每次使用该类时不创建新的对象而创建的静态对象
    private static BaseApplication application;
    //应用程序启动就调用，代表整个应用
    public static Handler handler;

    public static Context context;

    public static int mainTid;

    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        // 设置是否输出debug
        x.Ext.setDebug(true);
        application = this;
        handler = new Handler();
        mainTid = android.os.Process.myTid();

    }

    public static Handler getHandler() {
        return handler;
    }

    public static Context getContext() {
        return context;
    }

    public synchronized static BaseApplication getApplication() {
        return application;
    }

    // add Activity
    public void addActivity(Activity activity) {
        mList.add(activity);
    }

    //关闭每一个list内的activity
    public void exit() {
        try {
            for (Activity activity : mList) {
                if (activity != null)
                    activity.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.exit(0);
        }
    }

    //杀进程
    public void onLowMemory() {
        super.onLowMemory();
        System.gc();
    }

    public static int getMainTid() {
        return mainTid;
    }
}
