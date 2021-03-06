package com.edu.claf.Utils;

import android.content.Context;

import com.edu.claf.view.ProgressView;

import java.util.concurrent.TimeUnit;

import cn.bmob.v3.BmobQuery;

/**
 * 设置查询缓存
 */
public class BmobCacheUtils {
    public static BmobQuery setQueryCacheConditon(BmobQuery query,Context context,Class clazz){

        query.setMaxCacheAge(TimeUnit.DAYS.toMillis(2));
        final boolean isCache = query.hasCachedResult(context, clazz);
        if (isCache) {
            query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK); // 如果有缓存的话，则设置策略为CACHE_ELSE_NETWORK
        } else {
            ProgressView.getProgressDialog(context, "正在加载...");
            query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);// 如果没有缓存的话，则设置策略为NETWORK_ELSE_CACHE
        }
        return query;
    }
}
