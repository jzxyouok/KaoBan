package adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.dezhou.lsy.projectdezhoureal.R;
import com.zhy.utils.ImageLoader;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.List;

import utils.URLSet;

/**
 * Created by guochunpeng on 15/8/28.
 */
public class ArticalInfoImagesAdapter extends BaseAdapter {

    BitmapFactory.Options option = new BitmapFactory.Options();
    private List<String> imagePaths;
    Context context;
    LayoutInflater inflater;
    int width;
    private ImageLoader mImageLoader;
    private HashMap<String, SoftReference<Bitmap>> imageCache = null;

    public ArticalInfoImagesAdapter(Context context, List<String> imagePaths) {
        this.context = context;
        this.imagePaths = imagePaths;
        inflater = LayoutInflater.from(context);
        WindowManager wm = (WindowManager) context

                .getSystemService(Context.WINDOW_SERVICE);
        option.inSampleSize = 1;
        mImageLoader = ImageLoader.getInstance(3, ImageLoader.Type.LIFO);

        width = wm.getDefaultDisplay().getWidth();
    }

    @Override
    public int getCount() {
        return imagePaths.size();
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
    public View getView(int position, View view, ViewGroup parent) {
        view = inflater.inflate(R.layout.artical_list_photos_item, null);
        ImageView image = (ImageView) view.findViewById(R.id.artical_list_photos_item_photo);
        Bitmap bitmap = null;
        Glide.with(context).load(imagePaths.get(position)).into(image);
        mImageLoader.loadImage(imagePaths.get(position),image,true);
//        Glide.with(context).load(imagePaths.get(position)).into(image);

//        bitmap = loadBitmap(URLSet.serviceUrl + imagePaths.get(position));
//        if (bitmap != null) {
//            bitmap = ThumbnailUtils.extractThumbnail(bitmap, width / 3, width / 3);
//            image.setImageBitmap(bitmap);
//        } else {
//            LoadBitmapAsyn loadBitmapAsyn = new LoadBitmapAsyn(image, width / 3);
//            loadBitmapAsyn.execute(URLSet.serviceUrl + imagePaths.get(position));
//        }
        return view;
    }



}
