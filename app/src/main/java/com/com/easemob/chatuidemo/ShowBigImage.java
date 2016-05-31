package com.com.easemob.chatuidemo;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ProgressBar;

import com.com.easemob.chatuidemo.utils.ImageCache;
import com.dezhou.lsy.projectdezhoureal.BaseActivity;
import com.dezhou.lsy.projectdezhoureal.R;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chatuidemo.task.LoadLocalBigImgTask;
import com.easemob.chatuidemo.widget.photoview.PhotoView;
import com.easemob.util.EMLog;
import com.easemob.util.ImageUtils;
import com.easemob.util.PathUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ShowBigImage extends BaseActivity {
    private static final String TAG = "ShowBigImage";
    private ProgressDialog pd;
    private PhotoView image;
    private int default_res = R.drawable.default_image;
    private String localFilePath;
    private Bitmap bitmap;
    private boolean isDownloaded;
    private ProgressBar loadLocalPb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_big_image);
        image = (PhotoView) findViewById(R.id.image);
        loadLocalPb = (ProgressBar) findViewById(R.id.pb_load_local);
        default_res = getIntent().getIntExtra("default_image", R.drawable.default_avatar);
        Uri uri = getIntent().getParcelableExtra("uri");
        String remotepath = getIntent().getExtras().getString("remotepath");
        String secret = getIntent().getExtras().getString("secret");
        EMLog.d(TAG, "show big image uri:" + uri + " remotepath:" + remotepath);

        if (uri != null && new File(uri.getPath()).exists()) {
            EMLog.d(TAG, "showbigimage file exists. directly show it");
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            // int screenWidth = metrics.widthPixels;
            // int screenHeight =metrics.heightPixels;
            bitmap = ImageCache.getInstance().get(uri.getPath());
            if (bitmap == null) {
                LoadLocalBigImgTask task = new LoadLocalBigImgTask(this, uri.getPath(), image, loadLocalPb, ImageUtils.SCALE_IMAGE_WIDTH,
                        ImageUtils.SCALE_IMAGE_HEIGHT);
                if (android.os.Build.VERSION.SDK_INT > 10) {
                    task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    task.execute();
                }
            } else {
                image.setImageBitmap(bitmap);
            }
        } else if (remotepath != null) {
            EMLog.d(TAG, "download remote image");
            Map<String, String> maps = new HashMap<String, String>();
            if (!TextUtils.isEmpty(secret)) {
                maps.put("share-secret", secret);
            }
            downloadImage(remotepath, maps);
        } else {
            image.setImageResource(default_res);
        }

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * @param remoteUrl
     * @return
     */
    public String getLocalFilePath(String remoteUrl){
        String localPath;
        if (remoteUrl.contains("/")){
            localPath = PathUtil.getInstance().getImagePath().getAbsolutePath() + "/"
                    + remoteUrl.substring(remoteUrl.lastIndexOf("/") + 1);
        }else{
            localPath = PathUtil.getInstance().getImagePath().getAbsolutePath() + "/" + remoteUrl;
        }
        return localPath;
    }

    /**
     *
     * @param remoteFilePath
     */
    private void downloadImage(final String remoteFilePath, final Map<String, String> headers) {
        String str1 = getResources().getString(R.string.Download_the_pictures);
        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage(str1);
        pd.show();
        localFilePath = getLocalFilePath(remoteFilePath);
        final EMCallBack callback = new EMCallBack() {
            public void onSuccess() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DisplayMetrics metrics = new DisplayMetrics();
                        getWindowManager().getDefaultDisplay().getMetrics(metrics);
                        int screenWidth = metrics.widthPixels;
                        int screenHeight = metrics.heightPixels;

                        bitmap = ImageUtils.decodeScaleImage(localFilePath, screenWidth, screenHeight);
                        if (bitmap == null) {
                            image.setImageResource(default_res);
                        } else {
                            image.setImageBitmap(bitmap);
                            ImageCache.getInstance().put(localFilePath, bitmap);
                            isDownloaded = true;
                        }
                        if (pd != null) {
                            pd.dismiss();
                        }
                    }
                });
            }

            public void onError(int error, String msg) {
                EMLog.e(TAG, "offline file transfer error:" + msg);
                File file = new File(localFilePath);
                if (file.exists()&&file.isFile()) {
                    file.delete();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pd.dismiss();
                        image.setImageResource(default_res);
                    }
                });
            }

            public void onProgress(final int progress, String status) {
                EMLog.d(TAG, "Progress: " + progress);
                final String str2 = getResources().getString(R.string.Download_the_pictures_new);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        pd.setMessage(str2 + progress + "%");
                    }
                });
            }
        };

        EMChatManager.getInstance().downloadFile(remoteFilePath, localFilePath, headers, callback);

    }

    @Override
    public void onBackPressed() {
        if (isDownloaded)
            setResult(RESULT_OK);
        finish();
    }
}
