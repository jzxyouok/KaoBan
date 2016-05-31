package lc.com.nui.multiphotopicker.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dezhou.lsy.projectdezhoureal.R;

import lc.com.nui.multiphotopicker.model.ImageBucket;
import lc.com.nui.multiphotopicker.util.ImageDisplayer;


public class ImageBucketAdapter extends BaseAdapter
{
	private List<ImageBucket> mDataList;
	private Context mContext;

	public ImageBucketAdapter(Context context, List<ImageBucket> dataList)
	{
		this.mContext = context;
		this.mDataList = dataList;
	}

	
	public int getCount()
	{
		return mDataList.size();
	}

	
	public Object getItem(int position)
	{
		return mDataList.get(position);
	}

	
	public long getItemId(int position)
	{
		return position;
	}

	
	public View getView(int position, View convertView, ViewGroup parent)
	{
		final ViewHolder mHolder;
		if (convertView == null)
		{
			convertView = View.inflate(mContext, R.layout.item_bucket_list,
					null);
			mHolder = new ViewHolder();
			mHolder.coverIv = (ImageView) convertView.findViewById(R.id.cover);
			mHolder.titleTv = (TextView) convertView.findViewById(R.id.title);
			mHolder.countTv = (TextView) convertView.findViewById(R.id.count);
			convertView.setTag(mHolder);
		}
		else
		{
			mHolder = (ViewHolder) convertView.getTag();
		}

		final ImageBucket item = mDataList.get(position);

		if (item.imageList != null && item.imageList.size() > 0)
		{
			String thumbPath = item.imageList.get(0).thumbnailPath;
			String sourcePath = item.imageList.get(0).sourcePath;
			ImageDisplayer.getInstance(mContext).displayBmp(mHolder.coverIv, thumbPath,
					sourcePath);
		}
		else
		{
			mHolder.coverIv.setImageBitmap(null);
		}

		mHolder.titleTv.setText(item.bucketName);
		mHolder.countTv.setText(item.count + "张");

		return convertView;
	}

	static class ViewHolder
	{
		private ImageView coverIv;
		private TextView titleTv;
		private TextView countTv;
	}

}
