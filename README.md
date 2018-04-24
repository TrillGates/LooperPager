#Android开发中无限轮播图的实现
前面在我们的论坛里头看到有同学们提问，怎么样去实现无限轮播。所以晚上回来就录制了视频了！

![这里写图片描述](https://img-blog.csdn.net/20180424232158536?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L1RyaWxsR2F0ZXM=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

#实现方式
最简单的方式，就是使用viewpager来实现咯！
我们一开始只是实现图片在viewPager上面可以滑动起来

但是我现在已经把所有的代码写完了，哈哈！
所以大家就看代码好了！

这是主界面的布局，也就是MainActivity的布局：

```
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="200dp"
    tools:context=".MainActivity">


    <com.sunofbeaches.looperpager.MyViewPager
        android:id="@+id/looper_pager"
        android:layout_width="match_parent"
        android:layout_height="200dp"/>

    <LinearLayout
        android:id="@+id/points_container"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_alignParentBottom="true"
        android:layout_marginBottom="40px"
        android:gravity="center"
        android:orientation="horizontal">

        <!--<View-->
            <!--android:layout_width="40px"-->
            <!--android:layout_height="40px"-->
            <!--android:background="@drawable/shape_point_selected"/>-->

        <!--<View-->
            <!--android:layout_width="40px"-->
            <!--android:layout_height="40px"-->
            <!--android:layout_marginLeft="20px"-->
            <!--android:background="@drawable/shape_point_normal"/>-->

    </LinearLayout>

</RelativeLayout>

```
然后呢，也不用给大家看预览图了，很简单，一个viewpager，一个指示器点的容器。

接下来是MainActivity的代码：

```
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

```
对了，为什么前面的布局一个是自定义的ViewPager呢？因为我们要处理一下事件，当我们触摸的页面的时候，就停止自动切换到下一张图片，所以我们就写了一个自己的viewPager去修改一下：

```
package com.sunofbeaches.looperpager;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by TrillGates on 18/4/23.
 * God bless my code!
 */
public class MyViewPager extends ViewPager {

    private OnViewPagerTouchListener mTouchListener = null;

    public MyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyViewPager(Context context) {
        super(context);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mTouchListener != null) {
                    mTouchListener.onPagerTouch(true);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (mTouchListener != null) {
                    mTouchListener.onPagerTouch(false);
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    public void setOnViewPagerTouchListener(OnViewPagerTouchListener listener) {
        this.mTouchListener = listener;
    }

    public interface OnViewPagerTouchListener {
        void onPagerTouch(boolean isTouch);
    }

}

```
还有一个东西，当然是适配器啦！

```
package com.sunofbeaches.looperpager;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by TrillGates on 18/4/23.
 * God bless my code!
 */
public class LooperPagerAdapter extends PagerAdapter {

    private List<Integer> mPics = null;

    @Override
    public int getCount() {
        if (mPics != null) {
            return Integer.MAX_VALUE;
        }
        return 0;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        int realPosition = position % mPics.size();
        ImageView imageView = new ImageView(container.getContext());
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        //imageView.setBackgroundColor(mPics.get(position));
        imageView.setImageResource(mPics.get(realPosition));
        //设置完数据以后,就添加到容器里
        container.addView(imageView);
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public void setData(List<Integer> colos) {
        this.mPics = colos;
    }

    public int getDataRealSize() {
        if (mPics != null) {
            return mPics.size();
        }
        return 0;
    }
}


```
实现的效果：
![这里写图片描述](https://img-blog.csdn.net/20180424233248272?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L1RyaWxsR2F0ZXM=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

好啦今天文章就到这里了，代码可以到githut上面去下载，感谢您的支持！

代码下载地址：https://github.com/TrillGates/LooperPager
