package com.gallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.dezhou.lsy.projectdezhoureal.R;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;

public class ImageAdapter extends BaseAdapter {

	private ArrayList<String> data;
	private LayoutInflater inflater;
	private int width;
	private Handler myHandler;
    private Context context;
	private Bitmap bitmap;
	private AsyncBitmapLoader asyncBitmapLoader;
	private ImageView iv;
	BitmapFactory.Options option = new BitmapFactory.Options();
	private HashMap<String,SoftReference<Bitmap>> imageCache=null;
	public ImageAdapter(Context context, ArrayList<String> data, int width) {
		this.data = data;
        this.context=context;
		this.inflater = LayoutInflater.from(context);
		this.width = width;
		this.asyncBitmapLoader = new AsyncBitmapLoader();
	}

	@Override
	public int getCount() {
		int count = 0;
		if (data != null) {
			count = data.size();
		}
		return count;
	}

	@Override
	public Object getItem(int position) {
		String item = null;
		if (data != null) {
			item = data.get(position);
		}
		return item;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.cell, null);
		}
		iv = (ImageView) convertView.findViewById(R.id.iv);
		int height = width * 300 / 500;
		convertView.setLayoutParams(new Gallery.LayoutParams(width, height));
		//iv.setScaleType(ImageView.ScaleType.FIT_XY);
//		BitmapDrawable bitmapDrawable = asyncBitmapLoader.loadBitmap(
//				data.get(position), iv, new ImageCallBack() {
//
//					@Override
//					public void imageLoad(ImageView imageView,
//							BitmapDrawable bitmap) {
//						imageView.setImageBitmap(bitmap.getBitmap());
//					}
//				});

//		myHandler=new Handler()
//		{
//			public void handleMessage(android.os.Message msg)
//			{
//				if(msg.what==0x1122)
//				{
//					if (bitmap != null) {
//						iv.setImageBitmap(bitmap);
//					}
//					else {
//						iv.setImageResource(R.drawable.empty);
//					}
//				}
//			};
//		};
//		new Thread()
//		{
//			public void run()
//			{
//				URL url;
//				try {
//					url = new URL(data.get(position));
//					InputStream is=url.openStream();
//					bitmap= BitmapFactory.decodeStream(is);
//					is.close();
//				} catch (Exception e) {
//					// TODO 自动生成的 catch 块
//					e.printStackTrace();
//				}
//				myHandler.sendEmptyMessage(0x1122);
//
//			};
//
//		}.start();
//
//		Bitmap bitmap=loadBitmap(data.get(position));
//		if(bitmap!=null){
//			iv.setImageBitmap(bitmap);
//		}
//		else{
//			LoadBitmapAsyn loadBitmapAsyn = new LoadBitmapAsyn(iv);
//			loadBitmapAsyn.execute(data.get(position));
//		}

        Glide.with(context).load(data.get(position)).into(iv);
		return convertView;
	}


}
