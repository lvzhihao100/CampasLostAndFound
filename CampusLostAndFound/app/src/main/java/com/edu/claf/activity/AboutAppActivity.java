package com.edu.claf.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.edu.claf.BaseApplication;
import com.edu.claf.R;

/**
 * 关于应用界面
 */
public class AboutAppActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //隐藏标题栏
        getSupportActionBar().hide();
        //设置应用界面
        setContentView(R.layout.activity_about_app);
        BaseApplication.getApplication().addActivity(this);
    }

    /**
     * 返回到个人中心界面
     * @param view
     */
    public void backPersonCentral(View view) {
        //相应物理返回按键的处理
        onBackPressed();
    }
}
