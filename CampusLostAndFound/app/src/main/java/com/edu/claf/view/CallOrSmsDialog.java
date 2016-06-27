package com.edu.claf.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by Administrator on 2016/4/12.
 */
@SuppressWarnings("ALL")
public class CallOrSmsDialog {
    private static  AlertDialog.Builder builder;

    //获取大电话的对话框
    public static void getCallDialog(final Context context, final String phonenum){
        builder = new AlertDialog.Builder(context);
        builder.setTitle("提示!");
        builder.setMessage("确定要打电话给Ta:" + phonenum);
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+phonenum));
                context.startActivity(intent);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    //获取发短信对话框
    public static void getSendSmsDialog(final Context context, final String phonenum){
        builder = new AlertDialog.Builder(context);
        builder.setTitle("提示!");
        builder.setMessage("确定要发短信给Ta:" + phonenum);
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("smsto:"+phonenum));
                context.startActivity(intent);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });
        builder.create().show();

    }
}
