package kongjian;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import tempvalue.MyKongjian;
import utils.URLSet;


/**
 * Created by wenjun on 2015/7/26.
 */
public class SignActivity extends Activity {


    private String signget, postSign;
    private EditText textSign;

    private TextView send;
    private ImageView back_Self;
    private List<NameValuePair> listArtical;
    private int resultCode = 0;
    private Handler handler=new Handler(){
        public void handleMessage(Message msg){
            switch (msg.what){

                case 0x238:
                    Bundle bundle2=new Bundle();
                    bundle2=msg.getData();
                    String sendArticalXml=bundle2.getString("key");
                    String result=XmlParser.xmlResultGet(sendArticalXml);
                    if(result.equals("success")){
                        Toast.makeText(SignActivity.this,"发布说说成功",Toast.LENGTH_SHORT).show();
                        MyKongjian.fresh=true;
                        finish();
                    }
                    else{
                        Toast.makeText(SignActivity.this,"发布说说失败",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    break;

            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signactivity);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }

        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.blue);
        textSign = (EditText) findViewById(R.id.textsign);
        send = (TextView) findViewById(R.id.send);
        back_Self = (ImageView) findViewById(R.id.back_SignActivity);
        Intent intent = getIntent();
        signget = intent.getStringExtra("signpost");
        textSign.setText(signget);




        back_Self.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                postSign = textSign.getText().toString().trim();

                postToServer("sign",postSign);

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

                String tmpString = MyHttpClient.doPost(client, URLSet.changeInf, listArtical, false);
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


