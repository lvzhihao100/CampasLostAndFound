package com.edu.claf.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created by Administrator on 2016/4/17.
 */
public class BitmapUtils {

    public static Bitmap getBitmapFromSdCard(String path,int inSampleSize) {
        FileInputStream fis = null;
        Bitmap bitmap = null;
        try {
            fis = new FileInputStream(path);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = inSampleSize;//图片的长宽都是原来的1/8
            BufferedInputStream bis = new BufferedInputStream(fis);
            bitmap = BitmapFactory.decodeStream(bis,null,options);
            return bitmap;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
