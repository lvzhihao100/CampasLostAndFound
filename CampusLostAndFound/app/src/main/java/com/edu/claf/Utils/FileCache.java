package com.edu.claf.Utils;

import android.content.Context;

import java.io.File;

/**
 * 文件缓存
 */
public class FileCache {

    private File cacheDir;

    public FileCache(Context context) {
        // 如果有SD卡则在SD卡中建一个LostAndFound的目录存放缓存的图片
        // 没有SD卡就放在系统的缓存目录中
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            cacheDir = new File(FileUtils.getImageDir().getAbsolutePath() + File.separator);
        }
        else
            cacheDir = context.getCacheDir();
        if (!cacheDir.exists())
            cacheDir.mkdirs();
    }

    public File getFile(String url,String phone) {
        // 将电话号码作为缓存的文件名
        String filename = phone+".jpg";
        File f = new File(cacheDir, filename);
        return f;

    }

    public void clear() {
        File[] files = cacheDir.listFiles();
        if (files == null)
            return;
        for (File f : files)
            f.delete();
    }

}
