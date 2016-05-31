package com.dezhou.lsy.projectdezhoureal;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.Lc.BitmapUtils;
import com.alibaba.fastjson.JSON;
import com.com.easemob.chatuidemo.DemoApplication;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import adapter.ImageLcAdapter;
import adapter.PhotosGridViewAdapter;
import bean.Topic;
import ggl.zf.iosdialog.widget.ActionSheetDialog;
import kaobanxml.XmlParser;
import kongjian.AddImage.AddImageBucketChoose;
import kongjian.AddImage.AddImageZoom;
import lc.com.nui.multiphotopicker.model.ImageItem;
import lc.com.nui.multiphotopicker.util.CustomConstants;
import lc.com.nui.multiphotopicker.util.IntentConstants;
import tempvalue.AddNewArtical;
import tempvalue.Cookie;
import tempvalue.MyArtical;
import tempvalue.UserNum;
import utils.URLSet;

public class AddNewArticalActivity extends ActionBarActivity {
    Topic topic;
    PhotosGridViewAdapter adapter;
    List<Uri> photosUri;
    private GridView mGridView;
    private ImageLcAdapter mAdapter;
    public static List<ImageItem> mDataList = new ArrayList<ImageItem>();
    private EditText content;
    ProgressDialog pro;
    List<Map<String, Object>> listArtical;
    List<String> photosPath;
    int FILE_SELECT_CODE = 2;
    private static final String TAG = "UploadImage";
    private DemoApplication app;
    private RelativeLayout location;
    private static final int REQUEST_CODE_MAP = 4;
    private static final int REQUEST_photo = 3;
    private TextView now_location;
    int ADDNEWRESULT=5;
    String commentStr="";
    private String locationAddress="";
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x237:
                    pro.dismiss();
                    Bundle bundle = new Bundle();
                    bundle = msg.getData();
                    String resultXML = bundle.getString("result");
                    if(!resultXML.equals("")) {
                        String result = XmlParser.xmlResultGet(resultXML);
                        if ("success".equals(result)) {
                            Toast.makeText(AddNewArticalActivity.this, "发表成功!", Toast.LENGTH_SHORT).show();
                            mDataList.clear();
                            AddNewArtical.edit=null;
                            AddNewArtical.location="";
                            setResult(ADDNEWRESULT);
                            MyArtical.fresh=true;
                            finish();

                        } else {
                            Toast.makeText(AddNewArticalActivity.this, "发表失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_artical);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }

        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.blue);
        Intent intent = getIntent();
        topic = (Topic) intent.getSerializableExtra("topic");
        app=(DemoApplication)getApplication();
        commentStr=intent.getStringExtra("commentStr");
        app.setTopic(topic);
        initView();
        initData();

    }

    protected void onPause()
    {
        super.onPause();
        saveTempToPref();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        saveTempToPref();
    }

    private void saveTempToPref()
    {
        SharedPreferences sp = getSharedPreferences(
                CustomConstants.APPLICATION_NAME, MODE_PRIVATE);
        String prefStr = JSON.toJSONString(mDataList);
        sp.edit().putString(CustomConstants.PREF_TEMP_IMAGES, prefStr).commit();

    }

    private void getTempFromPref()
    {
        SharedPreferences sp = getSharedPreferences(
                CustomConstants.APPLICATION_NAME, MODE_PRIVATE);
        String prefStr = sp.getString(CustomConstants.PREF_TEMP_IMAGES, null);
        if (!TextUtils.isEmpty(prefStr))
        {
            List<ImageItem> tempImages = JSON.parseArray(prefStr,
                    ImageItem.class);
            mDataList = tempImages;
        }
    }

    private void removeTempFromPref()
    {
        SharedPreferences sp = getSharedPreferences(
                CustomConstants.APPLICATION_NAME, MODE_PRIVATE);
        sp.edit().remove(CustomConstants.PREF_TEMP_IMAGES).commit();
    }

    @SuppressWarnings("unchecked")
    private void initData()
    {
        getTempFromPref();
        List<ImageItem> incomingDataList = (List<ImageItem>) getIntent()
                .getSerializableExtra(IntentConstants.EXTRA_IMAGE_LIST);
        if (incomingDataList != null)
        {
            mDataList.addAll(incomingDataList);

        }

        mAdapter = new ImageLcAdapter(AddNewArticalActivity.this, mDataList);
        mGridView.setAdapter(mAdapter);


        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (position == getDataSize()) {
                    new PopupWindows(AddNewArticalActivity.this, mGridView);
                } else {
                    Intent intent = new Intent(AddNewArticalActivity.this,
                            AddImageZoom.class);
                    intent.putExtra(IntentConstants.EXTRA_IMAGE_LIST,
                            (Serializable) mDataList);
                    intent.putExtra(IntentConstants.EXTRA_CURRENT_IMG_POSITION, position);
                    startActivityForResult(intent, 1);

                }
            }
        });

    }

    @Override
    protected void onResume()
    {
        super.onResume();
        content.setText(AddNewArtical.edit);
        now_location.setText(AddNewArtical.location);
        notifyDataChanged(); //当在ImageZoomActivity中删除图片时，返回这里需要刷新

    }
    private int getDataSize()
    {
        return mDataList == null ? 0 : mDataList.size();
    }

    private int getAvailableSize()
    {
        int availSize = CustomConstants.MAX_IMAGE_SIZE - mDataList.size();
        if (availSize >= 0)
        {
            return availSize;
        }
        return 0;
    }

    public String getString(String s)
    {
        String path = null;
        if (s == null) return "";
        for (int i = s.length() - 1; i > 0; i++)
        {
            s.charAt(i);
        }
        return path;
    }

    public class PopupWindows extends PopupWindow
    {

        public PopupWindows(Context mContext, View parent)
        {

            new ActionSheetDialog(AddNewArticalActivity.this)
                    .builder()
                    .setCancelable(true)
                    .setCanceledOnTouchOutside(true)
                    .addSheetItem("相机", ActionSheetDialog.SheetItemColor.Blue,
                            new ActionSheetDialog.OnSheetItemClickListener() {
                                @Override
                                public void onClick(int which) {
                                    takePhoto();
                                    dismiss();
                                }
                            })
                    .addSheetItem("相册", ActionSheetDialog.SheetItemColor.Blue,
                            new ActionSheetDialog.OnSheetItemClickListener() {
                                @Override
                                public void onClick(int which) {
                                    AddNewArtical.edit=content.getText().toString().trim();
                                    Intent intent = new Intent(AddNewArticalActivity.this,
                                            AddImageBucketChoose.class);
                                    intent.putExtra(IntentConstants.EXTRA_CAN_ADD_IMAGE_SIZE,
                                            getAvailableSize());
                                    startActivityForResult(intent,REQUEST_photo);
                                    finish();
                                    dismiss();
                                }
                            }).show();
        }
    }

    private static final int TAKE_PICTURE = 0x000000;
    private String Caremapath="";
    private String caremapathsend="";
    public void takePhoto()
    {
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File vFile = new File(Environment.getExternalStorageDirectory()
                + "/myimage/", String.valueOf(System.currentTimeMillis())
                + ".jpg");
        if (!vFile.exists())
        {
            File vDirPath = vFile.getParentFile();
            vDirPath.mkdirs();
        }
        else
        {
            if (vFile.exists())
            {
                vFile.delete();
            }
        }
        Caremapath = vFile.getPath();
        Uri cameraUri = Uri.fromFile(vFile);
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
        startActivityForResult(openCameraIntent, TAKE_PICTURE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode)
        {
            case TAKE_PICTURE:
                if (mDataList.size() < CustomConstants.MAX_IMAGE_SIZE
                        && resultCode == -1 && !TextUtils.isEmpty(Caremapath))
                {
                    ImageItem item = new ImageItem();
                    Bitmap photobitmap= BitmapUtils.decodeSampledBitmapFromFile(Caremapath, mGridView.getWidth(), mGridView.getHeight());
                    saveMyBitmap(photobitmap);
                    item.sourcePath = caremapathsend;
                    mDataList.add(item);

                }
                break;

            case REQUEST_CODE_MAP:
                content.setText(AddNewArtical.edit);
                double latitude = data.getDoubleExtra("latitude", 0);
                double longitude = data.getDoubleExtra("longitude", 0);
                locationAddress = data.getStringExtra("address");
                if (locationAddress != null && !locationAddress.equals("")) {
                    now_location.setText(locationAddress);
                    AddNewArtical.location=locationAddress;
                } else {
                    String st = getResources().getString(R.string.unable_to_get_loaction);
                    Toast.makeText(this, st, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void notifyDataChanged()
    {
        mAdapter.notifyDataSetChanged();
    }


    public void initView() {
        mGridView = (GridView) findViewById(R.id.artical_new_photos);
        //  mGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        photosUri = new ArrayList<Uri>();
        photosPath = new ArrayList<String>();
//        adapter = new PhotosGridViewAdapter(this, photosUri, topic);
//        photos.setAdapter(adapter);
        TextView title = (TextView) findViewById(R.id.activity_add_artical_title);
        title.setText(topic.getTopicName());
        TextView cancel = (TextView) findViewById(R.id.activity_new_artical_cancel);
        TextView success = (TextView) findViewById(R.id.activity_add_artical_do);
        content = (EditText) findViewById(R.id.activity_add_artical_edit);
        if(commentStr==null)
            commentStr="";
        content.setText(commentStr);
        location=(RelativeLayout) findViewById(R.id.location_find);
        now_location=(TextView) findViewById(R.id.location_now);

        location.setOnClickListener(new BtClickListener());
        cancel.setOnClickListener(new BtClickListener());
        success.setOnClickListener(new BtClickListener());

    }


    public class BtClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.activity_new_artical_cancel:
                    finish();
                    break;
                case R.id.location_find:
                    AddNewArtical.edit=content.getText().toString().trim();
                    startActivityForResult(new Intent(AddNewArticalActivity.this, BaiduMapArtical.class), REQUEST_CODE_MAP);
                    break;
                case R.id.activity_add_artical_do:
                    String artical_content=content.getText().toString().trim();
                    if (artical_content.equals("")) {
                        Toast.makeText(AddNewArticalActivity.this, "内容不能为空", Toast.LENGTH_SHORT).show();
                    } else {

                        listArtical = new ArrayList<Map<String, Object>>();
                        pro = new ProgressDialog(AddNewArticalActivity.this);
                        pro.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        pro.setTitle("请稍候");
                        pro.setMessage("正在上传");
                        pro.setCancelable(true);
                        pro.setCanceledOnTouchOutside(false);
                        pro.show();
                        sendNewArtical(URLSet.getSendNewArtical(), mDataList, topic.getTopicName(),artical_content,AddNewArtical.location,AddNewArticalActivity.this);
                    }
                    break;
            }
        }
    }



   /*
    发送新帖子
     */

    public void sendNewArtical(String urlPath, final List<ImageItem> photosPath, final String tag, final String content, final String locationAddress,Context context) {


        final String url = urlPath;

        new Thread() {
            @Override
            public void run() {

                try {
                    ;
                    HttpContext context = new BasicHttpContext();

                    MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

                    for (int x = 0; x < photosPath.size(); x++) {
                        entity.addPart("img", new FileBody(new File((photosPath.get(x).sourcePath))));
                    }

                    entity.addPart("content", new StringBody(content, Charset.forName("UTF-8")));
                    entity.addPart("tag", new StringBody(tag, Charset.forName("UTF-8")));
                    entity.addPart("pos",new StringBody(locationAddress,Charset.forName("UTF-8")));
                    DefaultHttpClient client = new DefaultHttpClient();
                    client.setCookieStore(Cookie.cookieStore);
                    HttpPost post = new HttpPost(url + "?userNum=" + UserNum.userNum);

                    post.setEntity(entity);

                    HttpResponse response = client.execute(post, context);


                    /**请求发送成功，并得到响应**/
                    if (response.getStatusLine().getStatusCode() == 200) {
                        String str = "";
                        String result="";
                        try {
                            str = response.getStatusLine().getReasonPhrase();


                            if(str.equals("OK")){
                                HttpEntity httpEntity = response.getEntity();
                                result= EntityUtils.toString(httpEntity, "utf-8");
                                Message message = new Message();
                                message.what = 0x237;
                                Bundle bundle = new Bundle();
                                if(result==null)
                                    result="";
                                bundle.putString("result", result);
                                message.setData(bundle);
                                handler.sendMessage(message);
                            }
                        } catch (Exception e) {
                            Toast.makeText(AddNewArticalActivity.this, "未知错误,发送失败", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(AddNewArticalActivity.this, "未知错误,发送失败", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Toast.makeText(AddNewArticalActivity.this, "未知错误,发送失败", Toast.LENGTH_SHORT).show();
                }
            }

        }.start();

    }

    public void saveMyBitmap(Bitmap mBitmap){
        File f = new File(Environment.getExternalStorageDirectory()
                + "/kaoban/", String.valueOf(System.currentTimeMillis())
                + ".jpg");
        if (!f.exists())
        {
            File vDirPath = f.getParentFile();
            vDirPath.mkdirs();
        }
        else
        {
            if (f.exists())
            {
                f.delete();
            }
        }
        caremapathsend = f.getPath();
        try {
            f.createNewFile();
        } catch (IOException e) {
            // TODO Auto-generated catch block
//            DebugMessage.put("在保存图片时出错："+e.toString());
        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }
}
