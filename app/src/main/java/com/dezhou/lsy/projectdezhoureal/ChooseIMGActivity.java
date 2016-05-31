package com.dezhou.lsy.projectdezhoureal;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import org.apache.http.impl.client.DefaultHttpClient;

import java.util.List;

import adapter.NewTopicImagesAdapter;
import kaobanxml.XmlParser;
import network.MyHttpClient;
import utils.URLSet;

public class ChooseIMGActivity extends Activity {

    GridView images;
    List<String> imagesList;
    int CHOOSEIMGREQUEST=5;
    int CHOOSEIMGRESULT=6;
    private TextView back;
    Handler handler=new Handler(){
        public void handleMessage(Message msg){
            switch (msg.what){
                case 0x164:
                    Bundle bundle=msg.getData();
                    String imagesResultXML=bundle.getString("imagesResult");
                    if(!imagesResultXML.equals("")){
                        imagesList= XmlParser.xmlImagesString(imagesResultXML);
                        NewTopicImagesAdapter adapter=new NewTopicImagesAdapter(ChooseIMGActivity.this,imagesList);
                        images.setAdapter(adapter);
                    }
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_img);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }

        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.blue);
        initView();
    }

    public void initView(){
        back=(TextView)findViewById(R.id.backactivity);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        images=(GridView)findViewById(R.id.topic_images);
        initData(URLSet.getNewTopicImg,0x164);
        images.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra("image",imagesList.get(position));
                setResult(CHOOSEIMGRESULT,intent);
                finish();
            }
        });
    }
    public void initData(final String url,final int msgWhat){
        new Thread(new Runnable() {
            @Override
            public void run() {
                DefaultHttpClient client = MyHttpClient.getHttpClient();
                String imagesResult = MyHttpClient.doGet(client, url, null, false);
                Message message=new Message();
                message.what=msgWhat;
                Bundle bundle=new Bundle();
                if(imagesResult==null)
                    imagesResult="";
                bundle.putString("imagesResult",imagesResult);
                message.setData(bundle);
                handler.sendMessage(message);
            }
        }).start();
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
