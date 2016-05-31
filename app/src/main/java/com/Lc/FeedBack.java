package com.Lc;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dezhou.lsy.projectdezhoureal.R;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import org.apache.http.NameValuePair;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import kaobanxml.XmlParser;
import network.MyHttpClient;
import utils.URLSet;

public class FeedBack extends ActionBarActivity {
    private EditText textsign;
    private TextView send;
    private ImageView back_Self;
    private String postSign="";
    private List<NameValuePair> listArtical;

    private Handler handler=new Handler(){
        public void handleMessage(Message msg){
            switch (msg.what){

                case 0x238:
                    Bundle bundle2=new Bundle();
                    bundle2=msg.getData();
                    String sendArticalXml=bundle2.getString("key");
                    String result= XmlParser.xmlResultGet(sendArticalXml);
                    if(result.equals("success")){
                        Toast.makeText(FeedBack.this, "谢谢你的反馈，我们一定会改进的", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(FeedBack.this,"反馈失败",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    break;

            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }

        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.blue);
        textsign=(EditText)findViewById(R.id.textsign);
        send=(TextView)findViewById(R.id.send);
        back_Self = (ImageView) findViewById(R.id.back_SignActivity);
        back_Self.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                postSign = textsign.getText().toString().trim();

                postToServer("content",postSign);

            }
        });
    }

    private void postToServer(String key,String values ){


        listArtical = new ArrayList<NameValuePair>();

        NameValuePair npTmp3 = new BasicNameValuePair(key, values);

        listArtical.add(npTmp3);
        new Thread(new Runnable() {
            @Override
            public void run() {
                DefaultHttpClient client = MyHttpClient.getHttpClient();
                String tmpString = MyHttpClient.doPost(client, URLSet.suggestion, listArtical, true);
                Message msg = new Message();
                msg.what = 0x238;
                Bundle bundle = new Bundle();
                bundle.putString("key", tmpString);
                msg.setData(bundle);
                handler.sendMessage(msg);
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
