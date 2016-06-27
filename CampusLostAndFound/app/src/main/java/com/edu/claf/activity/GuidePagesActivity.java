package com.edu.claf.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IntegerRes;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.edu.claf.BaseApplication;
import com.edu.claf.R;
import com.edu.claf.Utils.SharedPrefUtils;

import java.util.ArrayList;

/**
 * 导航界面
 * @author Administrator
 *
 */
public class GuidePagesActivity extends Activity {

	/** ViewPager对象实现导航*/
	private ViewPager vp;
	/**装跳转页面的list*/
	private ArrayList<ImageView> views;
	private final int[] splashImages = new int[]{R.drawable.guide1,R.drawable.guide2,
			R.drawable.guide3};
	/**点的布局*/
	private LinearLayout ll_point;
	/**两个圆点之间的距离*/
	private int mPointWidth;
	/**小红点实例对象*/
	private View redPoint;
	/**开始进入程序按钮*/
	private Button btnStart;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//设置显示样式无标题栏,一定要在setConteneView之前
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.guidepagers);
		BaseApplication.getApplication().addActivity(this);
		vp = (ViewPager)findViewById(R.id.vp_guide);
		ll_point =(LinearLayout)findViewById(R.id.ll_point_group);
		redPoint = findViewById(R.id.view_red_point);
		btnStart = (Button) findViewById(R.id.btn_start);
		btnStart.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//更新SharedPreference,表示已经展示了新手引导
				SharedPrefUtils.setBoolean(GuidePagesActivity.this,"is_user_guide_showed",true);
				//跳转至主页面
				Intent intent = new Intent(GuidePagesActivity.this,MainActivity.class);
				intent.putExtra("type","guidePage");
				startActivity(intent);
				finish();
			}
		});
		//初始化页面
		initViews();
		vp.setAdapter(new GuideAdapter());
        vp.addOnPageChangeListener(new GuidePageChangeListener());
	}


	/**
	 * 初始化界面
	 */
	private void initViews(){
		views = new ArrayList<ImageView>();
		//初始化引导页的图片
		for (int i = 0; i < splashImages.length; i++) {
			ImageView myImageView = new ImageView(this);
			myImageView.setBackgroundResource(splashImages[i]);//设置引导页背景
			views.add(myImageView);
		}

		//初始化引导页的小圆点
		for (int j = 0; j < splashImages.length;j++){
            View dot = new View(this);
            //设置按钮宽高
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(15,15);
			//设置引导点
			dot.setBackgroundResource(R.drawable.shap_point_gray);
			if (j > 0){
				params.leftMargin = 10;
			}
			//设置圆点的大小
			dot.setLayoutParams(params);
			//将圆点添加给线性布局
			ll_point.addView(dot);
			//measure -->  layout --> onDraw
			//获取视图树,对layout的结束事件进行监听
			ll_point.getViewTreeObserver().addOnGlobalLayoutListener(
					new ViewTreeObserver.OnGlobalLayoutListener() {
						//当layout执行结束后回调此方法
					 @Override
					 public void onGlobalLayout() {
						 //运行一次后，删除监听
						 ll_point.getViewTreeObserver().removeGlobalOnLayoutListener(this);
						 mPointWidth = ll_point.getChildAt(1).getLeft()
								 - ll_point.getChildAt(0).getLeft();
					 }
				 }
			);
		}
	}


	/**
	 * 引导页适配器
	 */
	class GuideAdapter extends PagerAdapter{

		@Override
		public int getCount() {
			return splashImages.length;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View)object);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(views.get(position));
			return views.get(position);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return (arg0 == arg1);
		}

	}


    /**ViewPager的页面监听事件*/
    class GuidePageChangeListener implements ViewPager.OnPageChangeListener{

        //滑动事件
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			//移动距离
			int len =(int) (mPointWidth * positionOffset) + (position * mPointWidth);
			//获取当前红点的布局参数
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)redPoint.getLayoutParams();
			params.leftMargin = len;//设置左边界
			//重新给小红点设置布局参数
			redPoint.setLayoutParams(params);
        }
        //某个页面被选中
        @Override
        public void onPageSelected(int position) {
			//显示开始体验按钮
			if (position == splashImages.length - 1){
				btnStart.setVisibility(View.VISIBLE);
			}else{
				btnStart.setVisibility(View.INVISIBLE);
			}
        }

        //滑动状态发生改变
        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }



}
