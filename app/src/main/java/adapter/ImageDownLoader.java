package adapter;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.LruCache;

public class ImageDownLoader {
	/**
	 */
	private LruCache<String, Bitmap> mMemoryCache;
	/**
	 */
	private FileUtils fileUtils;
	/**
	 */
	private ExecutorService mImageThreadPool = null;
	
	
	public ImageDownLoader(Context context){
		int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int mCacheSize = maxMemory / 8;
		mMemoryCache = new LruCache<String, Bitmap>(mCacheSize){

			@Override
			protected int sizeOf(String key, Bitmap value) {
				return value.getRowBytes() * value.getHeight();
			}
			
		};
		
		fileUtils = new FileUtils(context);
	}
	
	
	/**
	 * @return
	 */
	public ExecutorService getThreadPool(){
		if(mImageThreadPool == null){
			synchronized(ExecutorService.class){
				if(mImageThreadPool == null){
					mImageThreadPool = Executors.newFixedThreadPool(2);
				}
			}
		}
		
		return mImageThreadPool;
		
	}
	
	/**
	 * @param key
	 * @param bitmap
	 */
	public void addBitmapToMemoryCache(String key, Bitmap bitmap) {  
	    if (getBitmapFromMemCache(key) == null && bitmap != null) {  
	        mMemoryCache.put(key, bitmap);  
	    }  
	}  
	 
	/**
	 * @param key
	 * @return
	 */
	public Bitmap getBitmapFromMemCache(String key) {  
	    return mMemoryCache.get(key);  
	} 
	
	/**
	 * @param url
	 * @param listener
	 * @return
	 */
	public Bitmap downloadImage(final String url, final onImageLoaderListener listener){
		final String subUrl = url.replaceAll("[^\\w]", "");
		Bitmap bitmap = showCacheBitmap(subUrl);
		if(bitmap != null){
			return bitmap;
		}else{
			
			final Handler handler = new Handler(){
				@Override
				public void handleMessage(Message msg) {
					super.handleMessage(msg);
					listener.onImageLoader((Bitmap)msg.obj, url);
				}
			};
			
			getThreadPool().execute(new Runnable() {
				
				@Override
				public void run() {
					Bitmap bitmap = getBitmapFormUrl(url);
					Message msg = handler.obtainMessage();
					msg.obj = bitmap;
					handler.sendMessage(msg);
					
					try {
						fileUtils.savaBitmap(subUrl, bitmap);
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					addBitmapToMemoryCache(subUrl, bitmap);
				}
			});
		}
		
		return null;
	}
	
	/**
	 * @param url
	 * @return
	 */
	public Bitmap showCacheBitmap(String url){
		if(getBitmapFromMemCache(url) != null){
			return getBitmapFromMemCache(url);
		}else if(fileUtils.isFileExists(url) && fileUtils.getFileSize(url) != 0){
			Bitmap bitmap = fileUtils.getBitmap(url);
			
			addBitmapToMemoryCache(url, bitmap);
			return bitmap;
		}
		
		return null;
	}
	
	
	/**
	 * @param url
	 * @return
	 */
	private Bitmap getBitmapFormUrl(String url) {
		Bitmap bitmap = null;
		HttpURLConnection con = null;
		try {
			URL mImageUrl = new URL(url);
			con = (HttpURLConnection) mImageUrl.openConnection();
			con.setConnectTimeout(10 * 1000);
			con.setReadTimeout(10 * 1000);
			con.setDoInput(true);
			con.setDoOutput(true);
			bitmap = BitmapFactory.decodeStream(con.getInputStream());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (con != null) {
				con.disconnect();
			}
		}
		return bitmap;
	}
	
	public synchronized void cancelTask() {
		if(mImageThreadPool != null){
			mImageThreadPool.shutdownNow();
			mImageThreadPool = null;
		}
	}
	
	
	/**
	 * @author len
	 *
	 */
	public interface onImageLoaderListener{
		void onImageLoader(Bitmap bitmap, String url);
	}
	
}
