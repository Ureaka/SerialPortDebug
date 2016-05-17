package com.urika.serialportdebug;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class PagesActivity extends AppCompatActivity {
    private static final String TAG = "xxx";
    private ViewPager viewPager;

    private Context cxt;
    private viewPagerAdapter vpAdapter;

    private LayoutInflater mInflater;
    private List<View> mListViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pages_layout);

        mListViews = new ArrayList<View>();
        mInflater = getLayoutInflater();
        mListViews.add(mInflater.inflate(R.layout.activity_main, null));
        mListViews.add(mInflater.inflate(R.layout.activity_serial, null));
        Log.d(TAG, "mListViews.size " + mListViews.size());

        vpAdapter = new viewPagerAdapter();
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(vpAdapter);

    }

    private class viewPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return mListViews.size();
        }

        /**
         * 从指定的position创建page
         *
         * @param container ViewPager容器
         * @param position  The page position to be instantiated.
         * @return 返回指定position的page，这里不需要是一个view，也可以是其他的视图容器.
         */
        @Override
        public Object instantiateItem(View collection, int position) {
            ((ViewPager) collection).addView(mListViews.get(position), 0);
            return mListViews.get(position);
        }

        /**
         * <span style="font-family:'Droid Sans';">从指定的position销毁page</span>
         * <p/>
         * <p/>
         * <span style="font-family:'Droid Sans';">参数同上</span>
         */
        @Override
        public void destroyItem(View collection, int position, Object view) {
            ((ViewPager) collection).removeView(mListViews.get(position));
        }


        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == (object);
        }

        @Override
        public void finishUpdate(View arg0) {
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(View arg0) {
        }

    }

}
