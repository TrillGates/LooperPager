package com.sunofbeaches.looperpager;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements MyViewPager.OnViewPagerTouchListener, ViewPager.OnPageChangeListener {

    private static final String TAG = "MainActivity";
    private MyViewPager mLoopPager;
    private LooperPagerAdapter mLooperPagerAdapter;

    private static List<Integer> sPics = new ArrayList<>();

    static {
        sPics.add(R.mipmap.pic1);
        sPics.add(R.mipmap.pic2);
        sPics.add(R.mipmap.pic3);
    }

    private Handler mHandler;
    private boolean mIsTouch = false;
    private LinearLayout mPointContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

//        Random random = new Random();
//        //准备数据
//        for (int i = 0; i < 5; i++) {
//            sColos.add(Color.argb(random.nextInt(255), random.nextInt(255), random.nextInt(255), random.nextInt(255)));
//        }

        //给适配器设置数据
        //mLooperPagerAdapter.setData(sColos);
        //
        //mLooperPagerAdapter.notifyDataSetChanged();
        mHandler = new Handler();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        //当我这个界面绑定到窗口的时候
        mHandler.post(mLooperTask);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.d(TAG, "onDetachedFromWindow");
        mHandler.removeCallbacks(mLooperTask);
    }

    private Runnable mLooperTask = new Runnable() {
        @Override
        public void run() {
            if (!mIsTouch) {
                //切换viewPager里的图片到下一个
                int currentItem = mLoopPager.getCurrentItem();
                mLoopPager.setCurrentItem(++currentItem, true);
            }
            mHandler.postDelayed(this, 3000);
        }
    };

    private void initView() {
        //就是找到这个viewPager控件
        mLoopPager = (MyViewPager) this.findViewById(R.id.looper_pager);
        //设置适配器
        mLooperPagerAdapter = new LooperPagerAdapter();
        mLooperPagerAdapter.setData(sPics);
        mLoopPager.setAdapter(mLooperPagerAdapter);
        mLoopPager.setOnViewPagerTouchListener(this);
        mLoopPager.addOnPageChangeListener(this);
        mPointContainer = (LinearLayout) this.findViewById(R.id.points_container);
        //根据图片的个数,去添加点的个数
        insertPoint();
        mLoopPager.setCurrentItem(mLooperPagerAdapter.getDataRealSize() * 100, false);
    }

    private void insertPoint() {
        for (int i = 0; i < sPics.size(); i++) {
            View point = new View(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(40, 40);
            point.setBackground(getResources().getDrawable(R.drawable.shape_point_normal));
            layoutParams.leftMargin = 20;
            point.setLayoutParams(layoutParams);
            mPointContainer.addView(point);
        }
    }

    @Override
    public void onPagerTouch(boolean isTouch) {
        mIsTouch = isTouch;
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        //这个方法的调用,其实是viewPager停下来以后选中的位置
        int realPosition;
        if (mLooperPagerAdapter.getDataRealSize() != 0) {
            realPosition = position % mLooperPagerAdapter.getDataRealSize();
        } else {
            realPosition = 0;
        }
        setSelectPoint(realPosition);
    }

    private void setSelectPoint(int realPosition) {
        for (int i = 0; i < mPointContainer.getChildCount(); i++) {
            View point = mPointContainer.getChildAt(i);
            if (i != realPosition) {
                //那就是白色
                point.setBackgroundResource(R.drawable.shape_point_normal);
            } else {
                //选中的颜色
                point.setBackgroundResource(R.drawable.shape_point_selected);
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
