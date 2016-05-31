package adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by guochunpeng on 15/9/5.
 */
public class DiscoverBannerPageAdapter extends PagerAdapter {
    List<View> viewsList;
public DiscoverBannerPageAdapter(List<View> viewsList){
    this.viewsList=viewsList;
}

    @Override
    public int getCount() {
        return viewsList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        container.addView(viewsList.get(position));
        return viewsList.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
