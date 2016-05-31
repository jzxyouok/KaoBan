package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.dezhou.lsy.projectdezhoureal.R;
import com.zhy.utils.ImageLoader;

import java.util.List;

/**
 * Created by Administrator on 2015/10/30.
 */
public class NewTopicImagesAdapter extends BaseAdapter {
    List<String> list;
    Context context;
    LayoutInflater inflater;
    private ImageLoader mImageLoader;
    public NewTopicImagesAdapter(Context context,List<String> list){
        this.list=list;
        this.context=context;
        inflater=LayoutInflater.from(context);
        mImageLoader = ImageLoader.getInstance(3, ImageLoader.Type.LIFO);
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView=inflater.inflate(R.layout.new_topic_image_item,null);
        ImageView imageView=(ImageView)convertView.findViewById(R.id.new_topic_image_item_image);
        mImageLoader.loadImage(list.get(position), imageView, true);

        return convertView;
    }
}
