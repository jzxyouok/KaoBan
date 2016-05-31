package com.dezhou.lsy.projectdezhoureal;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.zhy.utils.ImageLoader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.nio.charset.Charset;

import bean.Classification;
import kaobanxml.XmlParser;
import tempvalue.Cookie;
import tempvalue.UserNum;
import utils.URLSet;

public class AddTopicsActivity extends Activity {

    Classification topic;
    EditText topicName, topicDesc;
    ImageView chooseImage;
    Button upload;
    private int RESULT_LOAD_IMAGE = 1;
    String picturePath;
    int ADDTOPICRESULT = 2;
    int CHOOSEIMGREQUEST = 5;
    int CHOOSEIMGRESULT = 6;
    private ImageLoader mImageLoader;
    public Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x237:
                    Bundle bundle = msg.getData();
                    String addTopicResultXml = bundle.getString("addTopicResultXml");
                    if (!addTopicResultXml.equals("")) {
                        String result = XmlParser.xmlResultGet(addTopicResultXml);
                        if (result.equals("success")) {
                            setResult(ADDTOPICRESULT);
                            finish();
                        }
                    } else {
                        Toast.makeText(AddTopicsActivity.this, "新建失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_topics);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }

        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(false);
//        tintManager.setStatusBarTintResource(R.color.blue);

        WindowManager.LayoutParams windowLP = getWindow().getAttributes();
        windowLP.alpha = 0.99f;
        getWindow().setAttributes(windowLP);
        Intent intent = getIntent();
        topic = (Classification) intent.getSerializableExtra("classification");
        initView();
    }

    public void initView() {
        topicName = (EditText) findViewById(R.id.activity_add_topic_picname);
        topicDesc = (EditText) findViewById(R.id.activity_add_topic_des);
        chooseImage = (ImageView) findViewById(R.id.choose_imag);
        upload = (Button) findViewById(R.id.upload);
        chooseImage.setOnClickListener(new BtClickListener());
        upload.setOnClickListener(new BtClickListener());
    }

    public class BtClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.choose_imag:

                    Intent intent = new Intent(AddTopicsActivity.this, ChooseIMGActivity.class);

                    startActivityForResult(intent, CHOOSEIMGREQUEST);
                    break;
                case R.id.upload:
                    if (picturePath == null) {
                        Toast.makeText(AddTopicsActivity.this, "请选择图片", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    String topicNameStr = topicName.getText().toString().trim();
                    String topicDesStr = topicDesc.getText().toString().trim();
                    if (topicNameStr == null || topicDesStr == null) {
                        Toast.makeText(AddTopicsActivity.this, "请填写完整", Toast.LENGTH_SHORT).show();
                        break;
                    }else {
                        if(picturePath!=null)
                        sendNewTopic(URLSet.addNewTopic, picturePath, topicNameStr, topicDesStr);
                    }
                    break;
            }


        }
    }

    public void sendNewTopic(String urlPath, final String photosPath, final String topicName, final String topicDes) {
        final String url = urlPath;

        new Thread() {
            @Override
            public void run() {

                try {
                    ;
                    HttpContext context = new BasicHttpContext();

                    MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
                    entity.addPart("image", new StringBody(picturePath.substring(23,picturePath.length()), Charset.forName("UTF-8")));
                    entity.addPart("firstTopic", new StringBody(topic.getClassificationName(), Charset.forName("UTF-8")));
                    entity.addPart("name", new StringBody(topicName, Charset.forName("UTF-8")));
                    entity.addPart("content", new StringBody(topicDes, Charset.forName("UTF-8")));
                    DefaultHttpClient client = new DefaultHttpClient();
                    client.setCookieStore(Cookie.cookieStore);
                    HttpPost post = new HttpPost(url + "?userNum=" + UserNum.userNum);

                    post.setEntity(entity);

                    HttpResponse response = client.execute(post, context);


                    /**请求发送成功，并得到响应**/
                    if (response.getStatusLine().getStatusCode() == 200) {
                        String str = "";
                        String result = "";
                        try {
                            str = response.getStatusLine().getReasonPhrase();


                            if (str.equals("OK")) {
                                HttpEntity httpEntity = response.getEntity();
                                result = EntityUtils.toString(httpEntity, "utf-8");
                                Message message = new Message();
                                message.what = 0x237;
                                Bundle bundle = new Bundle();
                                if (result == null)
                                    result = "";
                                bundle.putString("addTopicResultXml", result);
                                message.setData(bundle);
                                handler.sendMessage(message);
                            }
                        } catch (Exception e) {
                            Toast.makeText(AddTopicsActivity.this, "未知错误,发送失败", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(AddTopicsActivity.this, "未知错误,发送失败", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Toast.makeText(AddTopicsActivity.this, "未知错误,发送失败", Toast.LENGTH_SHORT).show();
                }
            }

        }.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            cursor.close();
            chooseImage.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            chooseImage.setClickable(false);

        }
        if (requestCode == CHOOSEIMGREQUEST&&resultCode==CHOOSEIMGRESULT) {
            mImageLoader = ImageLoader.getInstance(3, ImageLoader.Type.LIFO);
            picturePath = data.getStringExtra("image");
            mImageLoader.loadImage(picturePath, chooseImage, true);
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
