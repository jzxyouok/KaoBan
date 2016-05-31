package kongjian.AddImage;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.dezhou.lsy.projectdezhoureal.AddNewArticalActivity;
import com.dezhou.lsy.projectdezhoureal.R;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.util.ArrayList;
import java.util.List;

import lc.com.nui.multiphotopicker.model.ImageItem;
import lc.com.nui.multiphotopicker.util.ImageDisplayer;
import lc.com.nui.multiphotopicker.util.IntentConstants;

public class AddImageZoom extends Activity
{

    private ViewPager pager;
    private MyPageAdapter adapter;
    private int currentPosition;
    private List<ImageItem> mDataList = new ArrayList<ImageItem>();
    private RelativeLayout photo_relativeLayout;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_zoom);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }

        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.blue);
        photo_relativeLayout = (RelativeLayout) findViewById(R.id.photo_relativeLayout);
        photo_relativeLayout.setBackgroundColor(Color.parseColor("#000000"));

        initData();
        Button photo_bt_exit = (Button) findViewById(R.id.photo_bt_exit);
        photo_bt_exit.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                finish();
            }
        });
        Button photo_bt_del = (Button) findViewById(R.id.photo_bt_del);
        photo_bt_del.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                if (mDataList.size() == 1)
                {
                    removeImgs();
                }
                else
                {
                    removeImg(currentPosition);
                    pager.removeAllViews();
                    adapter.removeView(currentPosition);
                    adapter.notifyDataSetChanged();
                }
            }
        });

        pager = (ViewPager) findViewById(R.id.viewpager);
        pager.setOnPageChangeListener(pageChangeListener);

        adapter = new MyPageAdapter(mDataList);
        pager.setAdapter(adapter);
        pager.setCurrentItem(currentPosition);
    }

    private void initData()
    {
        currentPosition = getIntent().getIntExtra(
                IntentConstants.EXTRA_CURRENT_IMG_POSITION, 0);
        mDataList = AddNewArticalActivity.mDataList;
    }

    private void removeImgs()
    {
        mDataList.clear();
    }

    private void removeImg(int location)
    {
        if (location + 1 <= mDataList.size())
        {
            mDataList.remove(location);
        }
    }

    private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener()
    {

        public void onPageSelected(int arg0)
        {
            currentPosition = arg0;
        }

        public void onPageScrolled(int arg0, float arg1, int arg2)
        {
        }

        public void onPageScrollStateChanged(int arg0)
        {

        }
    };

    class MyPageAdapter extends PagerAdapter
    {
        private List<ImageItem> dataList = new ArrayList<ImageItem>();
        private ArrayList<ImageView> mViews = new ArrayList<ImageView>();

        public MyPageAdapter(List<ImageItem> dataList)
        {
            this.dataList = dataList;
            int size = dataList.size();
            for (int i = 0; i != size; i++)
            {
                ImageView iv = new ImageView(AddImageZoom.this);
                ImageDisplayer.getInstance(AddImageZoom.this).displayBmp(
                        iv, null, dataList.get(i).sourcePath, false);
                iv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
                mViews.add(iv);
            }
        }

        public int getItemPosition(Object object)
        {
            return POSITION_NONE;
        }

        public Object instantiateItem(View arg0, int arg1)
        {
            ImageView iv = mViews.get(arg1);
            ((ViewPager) arg0).addView(iv);
            return iv;
        }

        public void destroyItem(View arg0, int arg1, Object arg2)
        {
            if (mViews.size() >= arg1 + 1)
            {
                ((ViewPager) arg0).removeView(mViews.get(arg1));
            }
        }

        public boolean isViewFromObject(View arg0, Object arg1)
        {
            return arg0 == arg1;
        }

        @Override
        public int getCount()
        {
            return dataList.size();
        }

        public void removeView(int position)
        {
            if (position + 1 <= mViews.size())
            {
                mViews.remove(position);
            }
        }

    }
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }
}