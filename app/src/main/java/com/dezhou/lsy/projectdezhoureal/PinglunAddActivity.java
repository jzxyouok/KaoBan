package com.dezhou.lsy.projectdezhoureal;

import android.app.ActionBar;
import android.app.Activity;
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
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.Lc.BitmapUtils;
import com.alibaba.fastjson.JSON;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
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

import adapter.ImageLcAdapter;
import bean.ArticalBean;
import bean.Comment;
import bean.SecondComment;
import ggl.zf.iosdialog.widget.ActionSheetDialog;
import kaobanxml.XmlParser;
import kongjian.AddImage.AddImageBucketChoose;
import kongjian.AddImage.AddImageZoom;
import lc.com.nui.multiphotopicker.model.ImageItem;
import lc.com.nui.multiphotopicker.util.CustomConstants;
import lc.com.nui.multiphotopicker.util.IntentConstants;
import tempvalue.AddNewArtical;
import tempvalue.Cookie;
import tempvalue.UserNum;
import utils.URLSet;

public class PinglunAddActivity extends Activity {

    Button queren;
    EditText commPinglun;
    Comment comment;
    SecondComment secondComment;
    ProgressDialog pro;
    String flag;
    ArticalBean artical;
    NameValuePair npTmp1,npTmp2,npTmp3,npTmp4;
    GridView imageGrid;
    private String Caremapath="";
    private static final int REQUEST_photo = 3;
    private static final int TAKE_PICTURE = 0x000000;
    public static List<ImageItem> mDataList = new ArrayList<ImageItem>();
    private String caremapathsend="";
    String commentStr="";
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x222:
                    pro.dismiss();
                    String tmp = msg.getData().getString("pinglunresult").trim();
                    String contentNext = XmlParser.xmlParserGet(tmp).trim();
                    if ("success".equals(contentNext)) {
                        Toast.makeText(PinglunAddActivity.this, "评论成功",
                                Toast.LENGTH_SHORT).show();
                        mDataList.clear();
                        AddNewArtical.edit=null;
                        setResult(2);
                        finish();
                    } else {
                        if ("fail".equals(contentNext)) {
                            Toast.makeText(PinglunAddActivity.this, "评论失败",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    break;
                case 0x444:
                    pro.dismiss();
                    Toast.makeText(PinglunAddActivity.this, "未知错误,发送失败", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

    };
    private String userNum;
    private int articalID;
    private ImageLcAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pinglun_add);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }

        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.blue);
        Intent intent = getIntent();
        comment= (Comment) intent.getSerializableExtra("comment");
        artical=(ArticalBean)intent.getSerializableExtra("artical");
        secondComment=(SecondComment)intent.getSerializableExtra("secondComment");
        flag=intent.getStringExtra("flag");
        commentStr=intent.getStringExtra("commentStr");

        initView();
        initActionBar();
    }

    private void initActionBar() {
        ActionBar actionBar = this.getActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.activity_pinglun_add_actionbar);

        queren = (Button) actionBar.getCustomView().findViewById(R.id.queren);

        queren.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                final String commStr = commPinglun.getText().toString().trim();
                String applyToNum = "";
                String commentNum = "";
                if (secondComment != null) {
                    applyToNum = secondComment.getSecondCommendUserNum();
                    commentNum = comment.getCommentID();
//					npTmp1 = new BasicNameValuePair("applyToNum", secondComment.getSecondCommendUserNum());
//					npTmp2 = new BasicNameValuePair("commentNum", comment.getCommentID());
                } else {
                    if (comment != null) {
                        applyToNum = comment.getCommentUserNum();
                        commentNum = comment.getCommentID();
//						npTmp1 = new BasicNameValuePair("applyToNum", comment.getCommentUserNum());
//						npTmp2 = new BasicNameValuePair("commentNum", comment.getCommentID());
                    }
                }
                npTmp3 = new BasicNameValuePair("content", commStr);
                npTmp4 = new BasicNameValuePair("articalNum", artical.getArticalsNum());

                sendComment(URLSet.getUrlsendpinglun(), mDataList, applyToNum, commentNum, commStr, artical.getArticalsNum(), flag);


            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            case TAKE_PICTURE:
                if (mDataList.size() < CustomConstants.MAX_IMAGE_SIZE
                        && resultCode == -1 && !TextUtils.isEmpty(Caremapath))
                {
                    ImageItem item = new ImageItem();
                    Bitmap photobitmap= BitmapUtils.decodeSampledBitmapFromFile(Caremapath, imageGrid.getWidth(), imageGrid.getHeight());
                    saveMyBitmap(photobitmap);
                    item.sourcePath = caremapathsend;
                    mDataList.add(item);
                }
                break;

        }
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
    public void initView() {
        //换成actionbar了，所以不要这个了
//		queren = (Button) findViewById(R.id.queren);
        commPinglun = (EditText) findViewById(R.id.comm_pinglun);
        imageGrid=(GridView)findViewById(R.id.artical_new_photos);
        if(commentStr==null)
            commentStr="";
        commPinglun.setText(commentStr);
        if(secondComment!=null) {
            commPinglun.setHint("回复" + secondComment.getSecondCommendUserName() + ";");
        }else {
            if (comment != null) {
                commPinglun.setHint("回复" + comment.getCommendUserName() + ";");
            } else {
                commPinglun.setHint("回复"+artical.getUserName()+";");
            }
        }
        if(!flag.equals("1")){
            imageGrid.setVisibility(View.GONE);
        }
        initData();
    }

    private void initData()
    {
        getTempFromPref();
        List<ImageItem> incomingDataList = (List<ImageItem>) getIntent()
                .getSerializableExtra(IntentConstants.EXTRA_IMAGE_LIST);
        if (incomingDataList != null)
        {
            mDataList.addAll(incomingDataList);

        }



        mAdapter = new ImageLcAdapter(PinglunAddActivity.this, mDataList);
        imageGrid.setAdapter(mAdapter);


        imageGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (position == getDataSize()) {
                    new PopupWindows(PinglunAddActivity.this, imageGrid);
                } else {
                    Intent intent = new Intent(PinglunAddActivity.this,
                            AddImageZoom.class);
                    intent.putExtra(IntentConstants.EXTRA_IMAGE_LIST,
                            (Serializable) mDataList);
                    intent.putExtra("artical", (Serializable) artical);
                    intent.putExtra("comment", (Serializable) comment);
                    intent.putExtra("secondComment", (Serializable) secondComment);

                    intent.putExtra(IntentConstants.EXTRA_CURRENT_IMG_POSITION, position);
                    startActivityForResult(intent, 1);

                }
            }
        });

    }
    private int getDataSize()
    {
        return mDataList == null ? 0 : mDataList.size();
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

    public void sendComment(String urlPath, final List<ImageItem> photosPath,final String applyToNum, final String commentNum, final String content, final String articalNum, final String type) {

        final String url = urlPath;
        pro=new ProgressDialog(PinglunAddActivity.this);
        pro.setTitle("评论");
        pro.setMessage("正在评论...");
        pro.show();
        new Thread() {
            @Override
            public void run() {
                try {
                    HttpContext context = new BasicHttpContext();

                    MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

                    for (int x = 0; x < photosPath.size(); x++) {
                        entity.addPart("img", new FileBody(new File((photosPath.get(x).sourcePath))));
                    }

                    entity.addPart("content", new StringBody(content, Charset.forName("UTF-8")));
                    if(type.equals("1"))
                        entity.addPart("articalNum", new StringBody(articalNum, Charset.forName("UTF-8")));
                    if(type.equals("2")){
                        entity.addPart("applyToNum",new StringBody(applyToNum, Charset.forName("UTF-8")));
                        entity.addPart("commentNum",new StringBody(commentNum, Charset.forName("UTF-8")));
                    }
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
                                message.what = 0x222;
                                Bundle bundle = new Bundle();
                                if(result==null)
                                    result="";
                                bundle.putString("pinglunresult", result);
                                message.setData(bundle);
                                handler.sendMessage(message);



                            }
                        } catch (Exception e) {
                            Toast.makeText(PinglunAddActivity.this, "未知错误,发送失败", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Message message=new Message();
                        message.what=0x444;
                        handler.sendMessage(message);

                    }

                } catch (Exception e) {
                    Toast.makeText(PinglunAddActivity.this, "未知错误,发送失败", Toast.LENGTH_SHORT).show();
                }
            }

        }.start();

    }
    public class PopupWindows extends PopupWindow
    {

        public PopupWindows(Context mContext, View parent)
        {

            new ActionSheetDialog(PinglunAddActivity.this)
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
                                    AddNewArtical.edit=commPinglun.getText().toString().trim();
                                    Intent intent = new Intent(PinglunAddActivity.this,
                                            AddImageBucketChoose.class);
                                    intent.putExtra(IntentConstants.EXTRA_CAN_ADD_IMAGE_SIZE,
                                            getAvailableSize());
                                    intent.putExtra("flag",1);
                                    intent.putExtra("artical",(Serializable)artical);
                                    intent.putExtra("comment",(Serializable)comment);
                                    intent.putExtra("secondComment",(Serializable)secondComment);
                                    intent.putExtra("commentStr",commPinglun.getText().toString().trim());
                                    intent.putExtra("type",flag);
                                    startActivityForResult(intent, REQUEST_photo);
                                    finish();
                                    dismiss();
                                }
                            }).show();
        }
    }
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
    private int getAvailableSize()
    {
        int availSize = CustomConstants.MAX_IMAGE_SIZE - mDataList.size();
        if (availSize >= 0)
        {
            return availSize;
        }
        return 0;
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
