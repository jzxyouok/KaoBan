package adapter;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.dezhou.lsy.projectdezhoureal.AddNewArticalActivity;
import com.dezhou.lsy.projectdezhoureal.R;

import java.util.List;

import bean.Topic;

/**
 * Created by Administrator on 2015/8/8.
 */
public class PhotosGridViewAdapter extends BaseAdapter {
    private Context context;
    private List<Uri> bitmapURIs;
    LayoutInflater inflater;
    ContentResolver cr ;
    Topic topic;
    int FILE_SELECT_CODE=2;
    BitmapFactory.Options option = new BitmapFactory.Options();
    public PhotosGridViewAdapter(Context context, List<Uri> bitmapURIs, Topic topic) {
        this.context = context;
        this.bitmapURIs = bitmapURIs;
        this.topic=topic;
        inflater= LayoutInflater.from(context);
        cr = context.getContentResolver();
        option.inSampleSize=10;
    }

    @Override
    public int getCount() {
        return bitmapURIs.size()+1;
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
        view=inflater.inflate(R.layout.artical_list_photos_item,null);
        ImageView img=(ImageView)view.findViewById(R.id.artical_list_photos_item_photo);
        if(i<bitmapURIs.size()){
            try{
                Uri uri=bitmapURIs.get(i);
                Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri),null,option);
                bitmap = ThumbnailUtils.extractThumbnail(bitmap, bitmap.getWidth(), bitmap.getWidth());
                img.setImageBitmap(bitmap);
                bitmap=null;
            }catch (Exception e){

            }

        }else{
            img.setImageResource(R.drawable.icon_addpic_unfocused);
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

//                    if(topic.getTopicName().equals("ffff")){
//                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                        intent.setType("*/*");
//                        intent.addCategory(Intent.CATEGORY_OPENABLE);
//
//                        try {
//                            ((AddNewArticalActivity)context).startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), FILE_SELECT_CODE);
//                        } catch (android.content.ActivityNotFoundException ex) {
//
//                        }
//                    }
//
//                    else {
                    Intent intent = new Intent();

                    intent.setType("image/*");

                    intent.setAction(Intent.ACTION_GET_CONTENT);

                    ((AddNewArticalActivity)context).startActivityForResult(intent, 1);
//                    }
                }
            });
        }
        return view;
    }
}
