package com.dezhou.lsy.projectdezhoureal;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class GuidePageActivity extends Activity {

    private ViewPager mPager;//页卡内容
    private List<View> listViews; // Tab页面列表

    private int offset = 0;// 动画图片偏移量
    private int currIndex = 0;// 当前页卡编号
    private int bmpW;// 动画图片宽度
    private ImageView cursor;// 动画图片
    TextView entrance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_page);

        initViewPager();
    }
    private void initViewPager() {
        entrance=(TextView)findViewById(R.id.entrance);
        entrance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            writeXML();
                Intent intent=new Intent(GuidePageActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
          mPager = (ViewPager) findViewById(R.id.guide_pager);
         listViews = new ArrayList<View>();
           LayoutInflater mInflater = LayoutInflater.from(GuidePageActivity.this);
           listViews.add(mInflater.inflate(R.layout.guide_page_1, null));
            listViews.add(mInflater.inflate(R.layout.guide_page_2, null));
             listViews.add(mInflater.inflate(R.layout.guide_page_3, null));
        listViews.add(mInflater.inflate(R.layout.guide_page_4,null));
             mPager.setAdapter(new MyPagerAdapter(listViews));
            mPager.setCurrentItem(0);
            mPager.setOnPageChangeListener(new MyOnPageChangeListener());
      }
    public void writeXML(){
        //实例化SharedPreferences对象（第一步）
        SharedPreferences mySharedPreferences= getSharedPreferences("userInfo",
                Activity.MODE_PRIVATE);
//实例化SharedPreferences.Editor对象（第二步）
        SharedPreferences.Editor editor = mySharedPreferences.edit();
//用putString的方法保存数据
        editor.putBoolean("ifFirst", false);//false代表不是第一次进入

//提交当前数据
        editor.commit();
    }
    /**
     2      * ViewPager适配器
     3 */
      public class MyPagerAdapter extends PagerAdapter {
           public List<View> mListViews;

                  public MyPagerAdapter(List<View> mListViews) {
                     this.mListViews = mListViews;
                      }

                          @Override
                  public void destroyItem(View arg0, int arg1, Object arg2) {
                          ((ViewPager) arg0).removeView(mListViews.get(arg1));
                      }

                          @Override
                  public void finishUpdate(View arg0) {
                      }

                          @Override
                  public int getCount() {
                          return mListViews.size();
                      }

                          @Override
                  public Object instantiateItem(View arg0, int arg1) {
                          ((ViewPager) arg0).addView(mListViews.get(arg1), 0);
                          return mListViews.get(arg1);
                      }

                          @Override
                  public boolean isViewFromObject(View arg0, Object arg1) {
                          return arg0 == (arg1);
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

    /**
     * 页卡切换监听
     */
    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        int one = offset * 2 + bmpW;// 页卡1 -> 页卡2 偏移量
        int two = one * 2;// 页卡1 -> 页卡3 偏移量

        @Override
        public void onPageSelected(int arg0) {
//            Animation animation = null;
            switch (arg0) {
                case 0:
                    entrance.setVisibility(View.GONE);
                    break;
                case 1:
                    entrance.setVisibility(View.GONE);
                    break;
                case 2:
                    entrance.setVisibility(View.GONE);
                    break;
                case 3:
                    entrance.setVisibility(View.VISIBLE);
                    break;
               /* case 0:
                    if (currIndex == 1) {
                        animation = new TranslateAnimation(one, 0, 0, 0);
                    } else if (currIndex == 2) {
                        animation = new TranslateAnimation(two, 0, 0, 0);
                    }
                    break;
                case 1:
                    if (currIndex == 0) {
                        animation = new TranslateAnimation(offset, one, 0, 0);
                    } else if (currIndex == 2) {
                        animation = new TranslateAnimation(two, one, 0, 0);
                    }
                    break;
                case 2:
                    if (currIndex == 0) {
                        animation = new TranslateAnimation(offset, two, 0, 0);
                    } else if (currIndex == 1) {
                        animation = new TranslateAnimation(one, two, 0, 0);
                    }
                    break;
                case 3:
                    if (currIndex == 0) {
                        animation = new TranslateAnimation(offset, two, 0, 0);
                    } else if (currIndex == 1) {
                        animation = new TranslateAnimation(one, two, 0, 0);
                    }
                    break;*/
            }
           /* currIndex = arg0;
            animation.setFillAfter(true);// True:图片停在动画结束位置
            animation.setDuration(300);
            cursor.startAnimation(animation);*/
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    }

}
