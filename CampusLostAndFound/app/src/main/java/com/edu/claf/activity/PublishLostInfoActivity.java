package com.edu.claf.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.edu.claf.BaseApplication;
import com.edu.claf.R;
import com.edu.claf.Utils.RegexUtils;
import com.edu.claf.bean.LostInfo;
import com.edu.claf.bean.User;
import com.edu.claf.view.ProgressView;
import com.edu.claf.watcher.MyTextWatcher;
import com.edu.claf.wheel.StrericWheelAdapter;
import com.edu.claf.wheel.WheelView;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.listener.SaveListener;


/**
 * 发布失物信息界面
 */
public class PublishLostInfoActivity extends AppCompatActivity {

    private int minYear = 1970;  //最小年份
    private int fontSize = 13;     //字体大小
    private WheelView yearWheel, monthWheel, dayWheel, hourWheel, minuteWheel, secondWheel;
    public static String[] yearContent = null;
    public static String[] monthContent = null;
    public static String[] dayContent = null;
    public static String[] hourContent = null;
    public static String[] minuteContent = null;
    public static String[] secondContent = null;

    private static final int PUBLISH_LOST_INFO = 301;
    @InjectView(R.id.iv_post_lost_message_back)
    ImageButton postLostMessageBack;

    /**
     * 失物标题
     */
    @InjectView(R.id.et_post_lost_message_title)
    EditText etPostLostMessageTitle;

    /**
     * 丢失时间
     */
    @InjectView(R.id.et_post_lost_message_time)
    EditText etPostLostMessageTime;

    /**
     * 丢失地点
     */
    @InjectView(R.id.et_post_lost_message_place)
    EditText etPostLostMessagePlace;

    /**
     * 联系电话
     */
    @InjectView(R.id.et_post_lost_message_telephone)
    EditText etPostLostMessageTelephone;

    /**
     * 失物简介（描述）
     */
    @InjectView(R.id.et_post_lost_message_desc)
    EditText etPostLostMessageDesc;

    @InjectView(R.id.btn_post_lost_message_time)
    Button btnPublishLostTime;

    @InjectView(R.id.gv_post_lost_type_message)
    GridView gvPostLostMessageGridview;

    @InjectView(R.id.post_lost_message_Num)
    TextView tvPostLostMessageNum;

    private User mUser;

    private SimpleAdapter simpleAdapter;

    private StringBuffer mLostTime;

    private String lostType;

    private static List<Map<String, Object>> lstImageItem;

    private int[] icon = {R.drawable.purse_ch, R.drawable.card_ch, R.drawable.keys_ch
            , R.drawable.digital_ch, R.drawable.ring_ch, R.drawable.cloth_ch
            , R.drawable.file_ch, R.drawable.book_ch, R.drawable.pat_ch, R.drawable.car_ch
            , R.drawable.people_ch, R.drawable.others_ch};


    private String[] iconName = {"钱包", "证件", "钥匙", "数码", "饰品", "衣服"
            , "文件", "书籍", "宠物", "车辆", "找人", "其他"};
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 222:
                    tvPostLostMessageNum.setText((int)msg.obj+"");
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_publish_lost_info);
        BaseApplication.getApplication().addActivity(this);
        ButterKnife.inject(this);
        initUI();
        initData();
        initContent();

    }

    /**
     * 初始化界面
     */
    private void initUI() {
        lstImageItem = new ArrayList<>();
        lstImageItem = initNightGrid();
        mUser = new BmobUser().getCurrentUser(this, User.class);
        if (mUser != null) {
            etPostLostMessageTelephone.setText(mUser.getMobilePhoneNumber());
        }
        simpleAdapter = new SimpleAdapter(this, lstImageItem, R.layout.night_item,
                new String[]{"ItemImage", "ItemText"},
                new int[]{R.id.ItemImage, R.id.ItemText});
        gvPostLostMessageGridview.setAdapter(simpleAdapter);
        gvPostLostMessageGridview.setOnItemClickListener(new ItemOnClickListener());
        btnPublishLostTime = (Button) findViewById(R.id.btn_post_lost_message_time);
        etPostLostMessageDesc.addTextChangedListener(new MyTextWatcher(etPostLostMessageDesc,222,handler));
    }

    /**
     * 获取失物时间
     * @param view
     */
    @OnClick(R.id.btn_post_lost_message_time)
    public void getLostTime(View view) {
        View viewTime = ((LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(R.layout.time_picker, null);

        Calendar calendar = Calendar.getInstance();
        int curYear = calendar.get(Calendar.YEAR);
        int curMonth = calendar.get(Calendar.MONTH) + 1;
        int curDay = calendar.get(Calendar.DAY_OF_MONTH);
        int curHour = calendar.get(Calendar.HOUR_OF_DAY);
        int curMinute = calendar.get(Calendar.MINUTE);

        yearWheel = (WheelView) viewTime.findViewById(R.id.yearwheel);
        monthWheel = (WheelView) viewTime.findViewById(R.id.monthwheel);
        dayWheel = (WheelView) viewTime.findViewById(R.id.daywheel);
        hourWheel = (WheelView) viewTime.findViewById(R.id.hourwheel);
        minuteWheel = (WheelView) viewTime.findViewById(R.id.minutewheel);


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(viewTime);

        yearWheel.setAdapter(new StrericWheelAdapter(yearContent));
        yearWheel.setCurrentItem(curYear - 2013);
        yearWheel.setCyclic(true);
        yearWheel.setInterpolator(new AnticipateOvershootInterpolator());


        monthWheel.setAdapter(new StrericWheelAdapter(monthContent));

        monthWheel.setCurrentItem(curMonth - 1);

        monthWheel.setCyclic(true);
        monthWheel.setInterpolator(new AnticipateOvershootInterpolator());

        dayWheel.setAdapter(new StrericWheelAdapter(dayContent));
        dayWheel.setCurrentItem(curDay - 1);
        dayWheel.setCyclic(true);
        dayWheel.setInterpolator(new AnticipateOvershootInterpolator());

        hourWheel.setAdapter(new StrericWheelAdapter(hourContent));
        hourWheel.setCurrentItem(curHour);
        hourWheel.setCyclic(true);
        hourWheel.setInterpolator(new AnticipateOvershootInterpolator());

        minuteWheel.setAdapter(new StrericWheelAdapter(minuteContent));
        minuteWheel.setCurrentItem(curMinute);
        minuteWheel.setCyclic(true);
        minuteWheel.setInterpolator(new AnticipateOvershootInterpolator());


        builder.setTitle("选取失物时间");
        builder.setPositiveButton("确  定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                mLostTime = new StringBuffer();
                mLostTime.append(yearWheel.getCurrentItemValue()).append("-")
                        .append(monthWheel.getCurrentItemValue()).append("-")
                        .append(dayWheel.getCurrentItemValue());

                mLostTime.append(" ");
                mLostTime.append(hourWheel.getCurrentItemValue())
                        .append(":").append(minuteWheel.getCurrentItemValue());
                etPostLostMessageTime.setText(mLostTime);
                dialog.cancel();
            }
        });

        builder.show();
    }

    /**
     * 选择失物类型
     */
    private class ItemOnClickListener implements AdapterView.OnItemClickListener {
        int flag = 0;
        private int itemIndex = 0;
        private int itemRecord;
        private List<Map<String, Object>> list = new ArrayList<>();

        Message msg = new Message();
        HashMap<String, Object> temp = null;

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            HashMap<String, Object> item = (HashMap<String, Object>) parent.getItemAtPosition(position);
            lostType = iconName[position];
            if (flag == 1 || itemIndex == position) {
                if (itemIndex != position) {
                    lstImageItem.set(itemIndex, temp);
                    temp = (HashMap<String, Object>) lstImageItem.get(position);
                    //生成新的节点并设置进去
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("ItemImage", R.drawable.chosen_state);
                    map.put("ItemText", iconName[position]);
                    //更改选中的图标
                    lstImageItem.set(position, map);
//                    }
                    itemIndex = position;

                } else {

                }

            } else {
                temp = (HashMap<String, Object>) lstImageItem.get(position);
                //生成新的节点并设置进去
                HashMap<String, Object> map = new HashMap<>();
                map.put("ItemImage", R.drawable.chosen_state);
                map.put("ItemText", iconName[position]);
                //更改选中的图标
                lstImageItem.set(position, map);
                flag = 1;
                itemIndex = position;
            }
            simpleAdapter.notifyDataSetChanged();
            msg.obj = view;
            msg.what = position;
        }

    }


    /**
     * 初始化数据
     */
    private void initData() {
        postLostMessageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }

    /**
     * 初始化失物类型gridView
     * @return
     */
    public List<Map<String, Object>> initNightGrid() {
        for (int i = 0; i < icon.length; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("ItemImage", icon[i]);
            map.put("ItemText", iconName[i]);
            lstImageItem.add(map);
        }
        return lstImageItem;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }


    /**
     * 提交失物信息
     * @param view
     */
    public void submitLostInfo(View view) {
        //格式化用户选择的失物日期
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        LostInfo lostInfo = new LostInfo();
        //获取用户填写的失物信息
        String lTitle = etPostLostMessageTitle.getText().toString();
        String lTime = etPostLostMessageTime.getText().toString();
        String lLocation = etPostLostMessagePlace.getText().toString();
        String lDesc = etPostLostMessageDesc.getText().toString();
        String lPhone = etPostLostMessageTelephone.getText().toString();
        //验证用户填写的失物信息
        if (RegexUtils.vertifyPublishInfo(lTitle,lTime,lLocation,lDesc,lPhone,lostType)) {
            //如果验证失物信息全部填写，则封装失物信息
            Date currentDate = null;
            lostInfo.setLostTitle(lTitle);
            lostInfo.setPublishUser(mUser);
            lostInfo.setLostLocation(lLocation);
            lostInfo.setLostType(lostType);
            lostInfo.setLostWhat("失物");
            lostInfo.setPublishUserSchool(mUser.getSchoolName());
            try {
                //将获取到的用户时间格式化为Date对象
                Date date = dateFormat.parse(lTime);
                lostInfo.setLostDate(new BmobDate(date));
                currentDate = dateFormat.parse(System.currentTimeMillis() + "");
                lostInfo.setPublishTime(new BmobDate(currentDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            lostInfo.setContactNumber(lPhone);
            lostInfo.setLostDesc(lDesc);
            final ProgressView progressView = new ProgressView(this);
            ProgressView.getProgressDialog(this, "正在发布中...");
            //保存用户的失物信息到后端云服务器上
            lostInfo.save(this, new SaveListener() {
                //保存用户发布的失物信息成功
                @Override
                public void onSuccess() {
                    Toast.makeText(PublishLostInfoActivity.this, "发布成功", Toast.LENGTH_SHORT).show();
                    ProgressView.progressDialogDismiss();
                    PublishLostInfoActivity.this.finish();
                    Intent intent = new Intent(PublishLostInfoActivity.this, MainActivity.class);
                    intent.putExtra("type", "publishLostInfo");
                    startActivity(intent);
                }
                //保存用户发表的失物信息失败
                @Override
                public void onFailure(int i, String s) {
                    ProgressView.progressDialogDismiss();
                    if (i == 9010) {
                        Toast.makeText(PublishLostInfoActivity.this, "发布失败,网络超时" , Toast.LENGTH_SHORT).show();
                    }else if(i == 9016){
                        Toast.makeText(PublishLostInfoActivity.this, "发布失败，网络连接错误，请检查手机网络", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else{ //验证失败，要求用户填写所有的失物信息
            Toast.makeText(PublishLostInfoActivity.this, "请填写失物信息的所有项", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 初始化时间选择器内容
     */
    public void initContent() {
        yearContent = new String[10];
        for (int i = 0; i < 10; i++)
            yearContent[i] = String.valueOf(i + 2013);

        monthContent = new String[12];
        for (int i = 0; i < 12; i++) {
            monthContent[i] = String.valueOf(i + 1);
            if (monthContent[i].length() < 2) {
                monthContent[i] = "0" + monthContent[i];
            }
        }

        dayContent = new String[31];
        for (int i = 0; i < 31; i++) {
            dayContent[i] = String.valueOf(i + 1);
            if (dayContent[i].length() < 2) {
                dayContent[i] = "0" + dayContent[i];
            }
        }
        hourContent = new String[24];
        for (int i = 0; i < 24; i++) {
            hourContent[i] = String.valueOf(i);
            if (hourContent[i].length() < 2) {
                hourContent[i] = "0" + hourContent[i];
            }
        }

        minuteContent = new String[60];
        for (int i = 0; i < 60; i++) {
            minuteContent[i] = String.valueOf(i);
            if (minuteContent[i].length() < 2) {
                minuteContent[i] = "0" + minuteContent[i];
            }
        }
        secondContent = new String[60];
        for (int i = 0; i < 60; i++) {
            secondContent[i] = String.valueOf(i);
            if (secondContent[i].length() < 2) {
                secondContent[i] = "0" + secondContent[i];
            }
        }
    }

}
