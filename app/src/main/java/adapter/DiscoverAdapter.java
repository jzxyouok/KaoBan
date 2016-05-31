package adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dezhou.lsy.projectdezhoureal.R;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bean.Classification;
import bean.Topic;

/**
 * Created by Administrator on 2015/7/29.
 */
public class DiscoverAdapter extends BaseAdapter {
    private List<Classification> classifications;
    private Classification classification;
    private ArrayList<Topic> topics;
    private Topic topic;
    private Context context;
    private LayoutInflater mInflater;
    private ArrayList<View> pageview;
    int x,m;
    public Bitmap bitmap;
    List<Fragment> viewList;
    private HashMap<String,SoftReference<Bitmap>> imageCache=null;
    BitmapFactory.Options option = new BitmapFactory.Options();

    String style;


    public DiscoverAdapter(Context context, List<Classification> classifications) {
        this.context = context;
        this.classifications = classifications;
        this.mInflater = LayoutInflater.from(context);

    }

    @Override
    public int getCount() {
        int num = classifications.size();
        for (int i = 0; i < classifications.size(); i++) {
            num += classifications.get(i).getTopics().size();
        }
        return num;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = new ViewHolder();

       /* if(i==0){

            view=mInflater.inflate(R.layout.discover_photo_layout,null);
            ViewPager viewPager=(ViewPager)view.findViewById(R.id.discover_photo_banner_viewpager);
            View v1=mInflater.inflate(R.layout.photo_item, null);
            ImageView photo = (ImageView) v1.findViewById(R.id.photo_img);
            photo.setImageResource(R.drawable.headimg);
            pageview =new ArrayList<View>();
            pageview.add(v1);
            viewPager.setAdapter(mPagerAdapter);
           *//* DiscoverBannerPageAdapter adapter=new DiscoverBannerPageAdapter(viewList);
            viewPager.setAdapter(adapter);*//*
        }

        else{*/
        for(x=0;x<classifications.size();x++){
            if(classifications.get(x).getSerial()==i){
                view = mInflater.inflate(R.layout.faxian_list_item_title, null);
                viewHolder.title=(TextView)view.findViewById(R.id.class_title);
                viewHolder.more=(TextView)view.findViewById(R.id.more);
                viewHolder.title.setText(classifications.get(x).getClassificationName());

                return view;
            }else{
                for(m=0;m<classifications.get(x).getTopics().size();m++){
                    if(classifications.get(x).getTopics().get(m).getSerial()==i) {
                        view = mInflater.inflate(R.layout.faxian_list_item_else, null);
                        viewHolder.img = (ImageView) view.findViewById(R.id.discover_list_match_img);
                        viewHolder.topic = (TextView) view.findViewById(R.id.topic);
                        viewHolder.commandNum = (TextView) view.findViewById(R.id.command_num);
                        viewHolder.topic.setText(classifications.get(x).getTopics().get(m).getTopicName());
                        viewHolder.commandNum.setText("已产生" + classifications.get(x).getTopics().get(m).getContentNum() + "条内容");
                        Glide.with(context).load(classifications.get(x).getTopics().get(m).getTopicPic()).into(viewHolder.img);
//                    }
                        return view;
                    }
            }
          }
        }

    return null;
}

public class ViewHolder {
    public TextView title,more, topic, commandNum;
    ImageView img;

}
}
