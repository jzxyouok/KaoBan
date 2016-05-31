package adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by lsy on 15-2-14.
 */
public class IndicatorPagerAdapter extends PagerAdapter {
    private ArrayList<View> views;
    private Context context;

    public IndicatorPagerAdapter(ArrayList<View> views, Context context) {
        this.views = views;
        this.context = context;
    }

    /**
     * 添加View到List中
     *
     * @param container
     * @param position
     * @return
     */
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ((ViewPager) container).addView(views.get(position));
        return views.get(position);
    }

    /**
     * 移除Ｖｉｅｗ
     *
     * @param container
     * @param position
     * @param object
     */
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView(views.get(position));
    }

    @Override
    public int getCount() {
        return views.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }
}
