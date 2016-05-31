package com.dezhou.lsy.projectdezhoureal;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import org.apache.http.NameValuePair;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;

import kaobanxml.XmlParser;
import network.MyHttpClient;
import utils.URLSet;

public class InputNewPassword extends ActionBarActivity {

    Button confirmChange;

    EditText firstPassWord,secondPassWord;
    String firstPassWordStr,secondPassWordStr,phoneNum;
    private ArrayList<NameValuePair> listInfo ;
    private DefaultHttpClient client = null;
    private TextView back;
    Handler handler=new Handler(){
        public void handleMessage(Message msg){
            switch (msg.what){
                case 0x243:
                    Bundle bundle=msg.getData();
                    String forgetResult=bundle.getString("forgetResult");
                    if(forgetResult.equals("")){
                        Toast.makeText(InputNewPassword.this,"找回失败，请重试",Toast.LENGTH_SHORT).show();
                    }else {
                        String result = XmlParser.xmlResultGet(forgetResult);

                        if (result.equals("success")) {
                            Toast.makeText(InputNewPassword.this, "找回密码成功", Toast.LENGTH_SHORT).show();
                            ForgetPasswordActivity.instance.finish();
                            InputYanzhengActivity.instance.finish();
                            InputNewPassword.this.finish();

                        } else {
                            Toast.makeText(InputNewPassword.this, "找回失败，请重试", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_new_password);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }

        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.blue);
        Intent intent=getIntent();
        phoneNum=intent.getStringExtra("phoneNum");
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
        confirmChange=(Button)findViewById(R.id.confirm_change_button);
        firstPassWord=(EditText)findViewById(R.id.input_new_password);
        secondPassWord=(EditText)findViewById(R.id.input_second_password);
        confirmChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firstPassWordStr = firstPassWord.getText().toString().trim();
                secondPassWordStr = secondPassWord.getText().toString().trim();
                if (firstPassWordStr.equals(secondPassWordStr)) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            forgetPassWord(URLSet.forgetPassWord, phoneNum, firstPassWordStr);
                        }
                    }).start();

                } else {
                    Toast.makeText(InputNewPassword.this, "密码输入不一致，请重新输入", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    /**
     * 提交登陆信息

     * @return 服务器返回的ｘｍｌ信息
     */
    private void forgetPassWord(String url,String mobile,String password){
        // 将请求内容添加到请求头
        listInfo = new ArrayList<NameValuePair>();
        NameValuePair pairEmail = new BasicNameValuePair("mobile",mobile);
        NameValuePair pairPassword = new BasicNameValuePair("pwd",password);
        listInfo.add(pairEmail);
        listInfo.add(pairPassword);
        // 执行post操作

        client = MyHttpClient.getInstance().getHttpClient();
        String result = MyHttpClient.getInstance().doPost(client,
                url,listInfo,true);
        Message message=new Message();
        message.what=0x243;
        Bundle bundle=new Bundle();
        if(result==null)
            result="";
        bundle.putString("forgetResult", result);
        message.setData(bundle);
        handler.sendMessage(message);



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
