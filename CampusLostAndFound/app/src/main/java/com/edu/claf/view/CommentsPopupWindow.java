package com.edu.claf.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.edu.claf.R;

import cn.bmob.v3.BmobUser;

/**
 * 评论的弹出窗口
 */
public class CommentsPopupWindow extends PopupWindow {


    private static final int CHANGE_PHOTO = 4;

    //评论
    Button btnCommetns;
    LinearLayout popLayout;

    private View mMenuView;
    private BmobUser mUser;
    private String mPhoneNumber;
    EditText etComments;
    Context context;

    public CommentsPopupWindow() {

    }

    public CommentsPopupWindow(Activity context, View.OnClickListener itemsOnClick) {
        super(context);
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.activity_comments_popup_window, null);
        btnCommetns = (Button) mMenuView.findViewById(R.id.btn_comments);
        etComments = (EditText) mMenuView.findViewById(R.id.et_comments);
        btnCommetns.setOnClickListener(itemsOnClick);
        //设置SelectPicPopupWindow的View
        this.setContentView(mMenuView);
        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        //输入法退出回到底部
        this.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        //设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.AnimBottom);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0x00000000);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
        //mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        mMenuView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int height = mMenuView.findViewById(R.id.pop_comments_layout).getTop();
                int y = (int) motionEvent.getY();
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });

    }


    public void setBtnCommetnText(String text){
        btnCommetns.setText(text);
    }

    public String getComments() {
        return etComments.getText().toString();
    }

    /**
     * 显示或隐藏输入法
     */
    private void onFocusChange(boolean hasFocus) {
        final boolean isFocus = hasFocus;
        (new Handler()).postDelayed(new Runnable() {
            public void run() {
                InputMethodManager imm = (InputMethodManager)
                        etComments.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (isFocus) {
                    //显示输入法
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                } else {
                    //隐藏输入法
                    imm.hideSoftInputFromWindow(etComments.getWindowToken(), 0);
                }
            }
        }, 100);
    }

    /**
     * 判断对话框中是否输入内容
     */
    private boolean isEditEmply() {
        String comment = etComments.getText().toString().trim();
        if (comment.equals("")) {
            Toast.makeText(context, "评论不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        etComments.setText("");
        return true;
    }

}
