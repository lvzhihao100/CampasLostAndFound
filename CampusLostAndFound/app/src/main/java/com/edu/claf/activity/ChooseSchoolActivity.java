package com.edu.claf.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.edu.claf.BaseApplication;
import com.edu.claf.R;
import com.edu.claf.Utils.CharacterParserUtils;
import com.edu.claf.Utils.PinyinComparator;
import com.edu.claf.Utils.SharedPrefUtils;
import com.edu.claf.adapter.SortAdapter;
import com.edu.claf.bean.SortBean;
import com.edu.claf.view.SideBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 选择学校信息界面
 */
public class ChooseSchoolActivity extends AppCompatActivity {

    @InjectView(R.id.ib_chooseschool_back)
    ImageButton chooseSchoolBack;

    @InjectView(R.id.lv_choose_school)
    ListView lvChooseSchool;
    @InjectView(R.id.sidebar_choose_school)
    SideBar sideBarChooseSchool;

    private SortAdapter adapter;

    /**
     * 汉字转换为拼音的类
     */
    private CharacterParserUtils characterParser;

    /**
     * 要转换的数据
     */
    private List<SortBean> mSchoolData;

    /**
     * 根据拼音来排列listview里面的数据的类
     */
    private PinyinComparator pinyinComparator;
    private String school;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_choose_school);
        BaseApplication.getApplication().addActivity(this);
        ButterKnife.inject(this);
        initViews();
    }

    /**
     * 初始化界面数据
     */
    private void initViews() {
        //实例化汉字转拼音类
        characterParser = CharacterParserUtils.getInstance();
        pinyinComparator = new PinyinComparator();
        Intent intent = getIntent();
        String type = intent.getStringExtra("type");
        String schoolName = null;
        final String sourceProvince = intent.getStringExtra("choose_province");
        SharedPrefUtils.writeString(this,"main_province",sourceProvince);
        String selling = characterParser.getSelling(sourceProvince);
        sideBarChooseSchool.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                int position = adapter.getPositionForSection(s.charAt(0));
                if (position != 1) {
                    lvChooseSchool.setSelection(position);
                }
            }
        });
        //选择了某条数据
        lvChooseSchool.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                school = ((SortBean) adapter.getItem(position)).getName();
                Intent intent = new Intent();
                intent.putExtra("choose_province", sourceProvince);
                intent.putExtra("choose_school", school);
                SharedPrefUtils.writeString(ChooseSchoolActivity.this, "main_province", sourceProvince);
                SharedPrefUtils.writeString(ChooseSchoolActivity.this, "main_school", school);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        schoolName = selling + "school";
        if (type.equals("register_school")) { //注册界面的选择学校
        } else if (type.equals("province_to_school")) { //先选择省份，再选择学校
            Intent mainIntent = new Intent(this, LostInfoActivity.class);
            mainIntent.putExtra("change_school", school);
        }
        int resId = getSourceProvinceResID(schoolName);
        mSchoolData = filledData(getResources().getStringArray(resId));
        //根据a-z对源数据进行排序
        Collections.sort(mSchoolData, pinyinComparator);
        adapter = new SortAdapter(this, mSchoolData);
        lvChooseSchool.setAdapter(adapter);

    }

    /**
     * 对要填充的数据进行排序处理
     * @param schoolData
     * @return
     */
    private List<SortBean> filledData(String[] schoolData) {
        List<SortBean> mSortList = new ArrayList<>();
        for (int i = 0; i < schoolData.length; i++) {
            SortBean sortBean = new SortBean();
            sortBean.setName(schoolData[i]);
            //汉字转换成拼音
            String pinyin = characterParser.getSelling(schoolData[i]);
            String sortString = pinyin.substring(0, 1).toUpperCase();
            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                sortBean.setSortLetters(sortString.toUpperCase());
            } else {
                sortBean.setSortLetters("#");
            }
            mSortList.add(sortBean);
        }
        return mSortList;
    }

    /**
     * 依据选择的省份，获取其学校的资源id
     * @param sourceProvince
     * @return
     */
    public int getSourceProvinceResID(String sourceProvince) {
        Context ctx = getBaseContext();
        int resId = getResources().getIdentifier(sourceProvince, "array", ctx.getPackageName());
        return resId;
    }

    /**
     * 返回到上一级
     * @param view
     */
    @OnClick(R.id.ib_chooseschool_back)
    public void chooseSchoolBack(View view) {
        onBackPressed();
    }
}
