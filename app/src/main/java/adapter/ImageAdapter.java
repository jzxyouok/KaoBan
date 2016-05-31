package adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import adapter.ImageDownLoader.onImageLoaderListener;
import com.dezhou.lsy.projectdezhoureal.R;

public class ImageAdapter extends BaseAdapter implements OnScrollListener{
	/**
	 */
	private Context context;
	
	/**
	 */
	private String [] imageThumbUrls;
	
	/**
	 */
	private GridView mGridView;
	
	/**
	 */
	private ImageDownLoader mImageDownLoader;
	
	/**
	 */
	private boolean isFirstEnter = true;
	
	/**
	 */
	private int mFirstVisibleItem;
	
	/**
	 */
	private int mVisibleItemCount;

	  private GridView.LayoutParams mImageViewLayoutParams;
	public ImageAdapter(Context context, GridView mGridView, String [] imageThumbUrls){
		this.context = context;
		this.mGridView = mGridView;
		this.imageThumbUrls = imageThumbUrls;
		mImageDownLoader = new ImageDownLoader(context);
		mGridView.setOnScrollListener(this);
		
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if(scrollState == OnScrollListener.SCROLL_STATE_IDLE){
			showImage(mFirstVisibleItem, mVisibleItemCount);
		}else{
			cancelTask();
		}
		
	}


	/**
	 */
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		mFirstVisibleItem = firstVisibleItem;
		mVisibleItemCount = visibleItemCount;
		if(isFirstEnter && visibleItemCount > 0){
			showImage(mFirstVisibleItem, mVisibleItemCount);
			isFirstEnter = false;
		}
	}


	@Override
	public int getCount() {
		return imageThumbUrls.length;
	}

	@Override
	public Object getItem(int position) {
		return imageThumbUrls[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView mImageView;
		final String mImageUrl = imageThumbUrls[position];
		if(convertView == null){
			mImageView = new ImageView(context);
		}else{
			mImageView = (ImageView) convertView;
		}
	    mImageViewLayoutParams = new GridView.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		
		mImageView.setLayoutParams(mImageViewLayoutParams);
		mImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
		
		mImageView.setTag(mImageUrl);
		
		
		Bitmap bitmap = mImageDownLoader.showCacheBitmap(mImageUrl.replaceAll("[^\\w]", ""));
		if(bitmap != null){
			mImageView.setImageBitmap(bitmap);
		}else{
			//mImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_empty));
		}
		/**********************************************************************************/
		
		
		return mImageView;
	}
	
	/**
	 * @param firstVisibleItem
	 * @param visibleItemCount
	 */
	private void showImage(int firstVisibleItem, int visibleItemCount){
		Bitmap bitmap = null;
		for(int i=firstVisibleItem; i<firstVisibleItem + visibleItemCount; i++){
			String mImageUrl = imageThumbUrls[i];
			final ImageView mImageView = (ImageView) mGridView.findViewWithTag(mImageUrl);
			bitmap = mImageDownLoader.downloadImage(mImageUrl, new onImageLoaderListener() {
				
				@Override
				public void onImageLoader(Bitmap bitmap, String url) {
					if(mImageView != null && bitmap != null){
						mImageView.setImageBitmap(bitmap);
					}
					
				}
			});
			
			if(bitmap != null){
				mImageView.setImageBitmap(bitmap);
			}else{
				//mImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_empty));
			}
		}
	}

	/**
	 */
	public void cancelTask(){
		mImageDownLoader.cancelTask();
	}


}
