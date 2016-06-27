package com.edu.claf.Utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import com.edu.claf.BaseApplication;


/**
 * Created by Administrator on 2016/4/8.
 */
public class UIUtils {
    /**
     * 获取字符数组
     * @param ResId
     */
    public static String[] getStringArray(int ResId){
        return  getResources().getStringArray(ResId);
    }

    public static Resources getResources() {
        return BaseApplication.getApplication().getResources();
    }

    /**
     * dip转换px
     * @param dip
     * @return
     */
    public static  int dip2px(int dip){
        final  float scale = getResources().getDisplayMetrics().density;
        return (int)(dip * scale + 0.5f);
    }

    /**
     * px转换dip
     * @param px
     * @return
     */
    public static  int px2dip(int px){
        final  float scale = getResources().getDisplayMetrics().density;
        return (int)(px / scale + 0.5f);
    }

    public static  void runOnUiThread(Runnable runnable){
        if (android.os.Process.myTid() == BaseApplication.mainTid){
            //主线程
            runnable.run();
        }else{
            //子线程
            BaseApplication.getHandler().post(runnable);
        }
    }

    public static Context getContext(){
        return BaseApplication.getApplication();
    }

    public static Drawable getDrawalbe(int ResId) {
        return getResources().getDrawable(ResId);
    }
}
