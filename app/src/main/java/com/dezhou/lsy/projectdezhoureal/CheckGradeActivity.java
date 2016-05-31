package com.dezhou.lsy.projectdezhoureal;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import org.apache.http.impl.client.DefaultHttpClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaobanxml.XmlParser;
import network.MyHttpClient;
import utils.URLSet;

/**
 * Created by zy on 15-9-2.
 */
public class CheckGradeActivity extends Activity{

    private String url = URLSet.serviceUrl+"/kaoban/score/account/";

    private Button submitButton;

    private EditText zkzhEdit;
    private EditText sfzhEdit;
    private TextView back;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String res = (String) msg.obj;
            if (res != null && !res.isEmpty()){
                List<Map<String,String>> list = XmlParser.gradeParser(res);
                StringBuilder content = new StringBuilder();
                for (Map<String,String> map : list){
                    content.append(map.get("name"));
                    content.append("  :  ");
                    content.append(map.get("value"));
                    content.append("\n");
                }
                setGrade(content.toString());
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkgrade);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }

        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.blue);
        init();
    }

    private void init(){
        back=(TextView)this.findViewById(R.id.backactivity);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        zkzhEdit = (EditText) this.findViewById(R.id.edit_zkzh);
        sfzhEdit = (EditText) this.findViewById(R.id.edit_sfzh);

        submitButton = (Button) this.findViewById(R.id.button_submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNetWork(zkzhEdit.getText().toString().trim(),sfzhEdit.getText().toString().trim());
            }
        });
    }

    private void startNetWork(String zkzh,String sfzh){
        final Map<String,String> map = new HashMap<>();
        map.put("zkzh", zkzh);
        map.put("sfzh",sfzh);

        new Thread(){
            @Override
            public void run() {
                DefaultHttpClient client = MyHttpClient.getHttpClient();
                String res = MyHttpClient.doGet(client, url, map, false);
                handler.sendMessage(handler.obtainMessage(0,res));
            }
        }.start();
    }

    private void setGrade(String content){
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage(content)
                .show();
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
