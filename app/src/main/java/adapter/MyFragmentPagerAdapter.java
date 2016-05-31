package adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

/**
 * 设置Fragment显示的Adapter
 * Created by lsy on 15-2-13.
 */
public class MyFragmentPagerAdapter extends FragmentStatePagerAdapter {

    private ArrayList<Fragment> list;
   /* private Context context;*/

    public MyFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public MyFragmentPagerAdapter(FragmentManager fm, ArrayList<Fragment> list) {
        super(fm);
        this.list = list;
    }

    /* public MyFragmentPagerAdapter(ArrayList<Fragment> list,Context context){
         this.list = list;
         this.context = context;
     }*/
    @Override
    public Fragment getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }
}
