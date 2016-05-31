package adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dezhou.lsy.projectdezhoureal.R;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.List;

import bean.Topic;

/**
 * Created by Administrator on 2015/8/2.
 */
public class TopicsAdapter extends BaseAdapter {
    Context context;
    List<Topic> topics;
    LayoutInflater mInflater;
    private HashMap<String,SoftReference<Bitmap>> imageCache=null;
    BitmapFactory.Options option = new BitmapFactory.Options();

    public TopicsAdapter(Context context, List<Topic> topics) {
        this.context = context;
        this.topics = topics;
        this.mInflater=LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return topics.size();
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
        ViewHolder viewHolder=new ViewHolder();
        view=mInflater.inflate(R.layout.faxian_list_item_else,null);
        viewHolder.img=(ImageView)view.findViewById(R.id.discover_list_match_img);
        viewHolder.topic=(TextView)view.findViewById(R.id.topic);
        viewHolder.commandNum=(TextView)view.findViewById(R.id.command_num);
        viewHolder.topic.setText(topics.get(i).getTopicName());
        viewHolder.commandNum.setText("已产生"+topics.get(i).getContentNum()+"条内容");
 //       Bitmap bitmap=loadBitmap(topics.get(i).getTopicPic());
//        if(bitmap!=null){
//            viewHolder.img.setImageBitmap(bitmap);
//        }
//        else{
//            LoadBitmapAsyn loadBitmapAsyn = new LoadBitmapAsyn(viewHolder.img);
//            loadBitmapAsyn.execute(topics.get(i).getTopicPic());
//        }

        Glide.with(context).load(topics.get(i).getTopicPic()).into(viewHolder.img);
        return view;
    }
    public class ViewHolder{
        public TextView topic,commandNum;
        public ImageView img;
    }

}
