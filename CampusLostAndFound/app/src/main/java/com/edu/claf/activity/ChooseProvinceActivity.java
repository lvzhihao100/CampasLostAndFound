package com.edu.claf.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

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
 * 省份信息选择界面
 */
public class ChooseProvinceActivity extends AppCompatActivity {

    private static final int PROVINCE_REQUEST_SCHOOL = 1;
    private static final int CHOOSE_PROVINCE = 2;
    private static final int RESULT_MAIN_PROVINCE = 112;
    @InjectView(R.id.ib_chooseprovince_back)
    ImageButton chooseProvienceBack;
    @InjectView(R.id.lv_choose_province)
    ListView lvChooseProvience;
    @InjectView(R.id.sidebar_choose_province)
    SideBar sideBarChooseProvince;
    private SortAdapter adapter;

    private boolean clickFlag = false;
    /**
     * 汉字转换成拼音的类
     */
    private CharacterParserUtils characterParser;
    private List<SortBean> mProvinceData;
    /**
     * 根据拼音来排列ListView里面的数据类
     */
    private PinyinComparator pinyinComparator;
    private String mSourceProvince;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_choose_province);
        BaseApplication.getApplication().addActivity(this);
        ButterKnife.inject(this);
        initViews();
    }

    /**
     * 依据省份，跳到相应省份的学校界面
     * @param source
     */
    private void provinceToSchool(String source) {
        Intent toSchool = new Intent(ChooseProvinceActivity.this, ChooseSchoolActivity.class);
        toSchool.putExtra("type", "province_to_school");
        toSchool.putExtra("choose_province", mSourceProvince);
        startActivityForResult(toSchool, PROVINCE_REQUEST_SCHOOL);
    }

    /**
     * 初始化界面
     */
    private void initViews() {
        //实例化汉字转拼音类
        characterParser = CharacterParserUtils.getInstance();
        pinyinComparator = new PinyinComparator();
        sideBarChooseProvince.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                //该字母首次出现的位置
                int position = adapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    lvChooseProvience.setSelection(position);
                }
            }
        });

        lvChooseProvience.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String type = getIntent().getStringExtra("type");
                clickFlag = true;
                //这里要利用adapter.getItem(position)来获取当前position所对应的对象
                mSourceProvince = ((SortBean) adapter.getItem(position)).getName();
                if (type.equals("location_province")) { //主界面中，选择省份分类
                    SharedPrefUtils.writeString(ChooseProvinceActivity.this, "main_province", mSourceProvince);
                    SharedPrefUtils.writeString(ChooseProvinceActivity.this, "main_school", "全部学校");
                    Intent intent = new Intent();
                    intent.putExtra("main_province", mSourceProvince);
                    setResult(RESULT_MAIN_PROVINCE, intent);
                    finish();
                } else if (type.equals("location_school")) { //主界面中，选择学校分类
                    provinceToSchool(mSourceProvince);
                } else { //直接选择省份
                    Intent cityIntent = new Intent();
                    cityIntent.putExtra("choose_province", mSourceProvince);
                    setResult(RESULT_OK, cityIntent);
                    finish();
                }
            }
        });

        mProvinceData = filledData(getResources().getStringArray(R.array.province));
        //根据a-z对源数据进行排序
        Collections.sort(mProvinceData, pinyinComparator);
        adapter = new SortAdapter(this, mProvinceData);
        lvChooseProvience.setAdapter(adapter);

    }

    /**
     * 为listview填充数据
     * @param provinceData
     * @return
     */
    private List<SortBean> filledData(String[] provinceData) {
        List<SortBean> mSortList = new ArrayList<>();
        for (int i = 0; i < provinceData.length; i++) {
            SortBean sortBean = new SortBean();
            sortBean.setName(provinceData[i]);
            //汉字转换成拼音
            String pinyin = characterParser.getSelling(provinceData[i]);
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
     * 依据请求种类，处理请求返回的数据
     * @param requestCode 请求码
     * @param resultCode 结果码
     * @param data 返回的数据
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            //通过省份界面请求该省的学校信息
            case PROVINCE_REQUEST_SCHOOL:
                setResult(RESULT_OK, data);
                finish();
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 在省份选择界面未选择省份
     * @param view
     */
    @OnClick(R.id.ib_chooseprovince_back)
    public void chooseProvienceBack(View view) {
        if (!clickFlag) {  //未选择省份，设置选择省份数据为空，并将数据返回到上一级界面
            Intent intent = new Intent();
            intent.putExtra("main_province", "");
            setResult(RESULT_OK, intent);
            finish();
        }

    }

    /**
     * 在省份选择界面未选择省份
     */
    @Override
    public void onBackPressed() {
        if (!clickFlag) { //未选择省份，设置选择省份数据为空，并将数据返回到上一级界面
            Intent intent = new Intent();
            intent.putExtra("main_province", "");
            setResult(RESULT_MAIN_PROVINCE, intent);
            finish();
        }
        super.onBackPressed();
    }
}
