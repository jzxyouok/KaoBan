package com.dezhou.lsy.projectdezhoureal;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.igexin.sdk.PushManager;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import org.apache.http.NameValuePair;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;

import exitapp.ExitApplication;
import kaobanxml.XmlParser;
import network.MyHttpClient;
import utils.URLSet;


public class RegisterActivity extends ActionBarActivity implements View.OnClickListener{

    private static Context context;

    //private Toolbar toolbarRegister;


    private EditText etRegisterPassword;
    private EditText etPasswordConfirm;

    private Button btnConfirmRegister;
    private Button btnCancel;

    private boolean isExit = false;

    private static String email = "";
    private static String password = "";
    private String passwordConfirm = "";
    private String xmlInfo = null;

    private static ArrayList<NameValuePair> listInfo = new ArrayList<NameValuePair>();

    private ProgressDialog dialog;
    private String yanzhengma="";

    // 网络连接相关
    private static DefaultHttpClient client;


    private Handler myHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            if(msg.what==0x445){
                btnConfirmRegister.setText("注册中...");
            }
            if (msg.what == 0x100) {

                String tmp = msg.getData().getString("RegisterInfo").trim();
                // 解析数据
                if (tmp != null) {
                    // 注册时ｘｍｌ数据的解析
                    String contentNext = XmlParser.xmlParserGet(tmp).trim();

                    // 判断是否为真

                    if ("success".equals(contentNext)) {
                    Toast.makeText(RegisterActivity.this,"注册成功",Toast.LENGTH_SHORT).show();
                    ForgetPasswordActivity.instance.finish();
                        InputYanzhengActivity.instance.finish();
                        RegisterActivity.this.finish();



                        // 保存用户名和密码，并进行加密
//                        Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
//                        RegisterActivity.this.finish();
//                        startActivity(intent);

//                       SharedPreferences.Editor editor = XmlSharedPreferences.getInstance()
//                                .getMySharedPreferencesEditor(context);
//                        XmlSharedPreferences.getInstance().writeUsername(editor, email);
//                        XmlSharedPreferences.getInstance().writePassword(editor, password);
//
//                        if (listInfo != null&&!listInfo.equals("")) {
//                            // 开启登陆
//
//                                new Thread(new Runnable() {
//                                    @Override
//                                    public void run() {
//
//                                        DefaultHttpClient tmpClient = MyHttpClient
//                                                .getInstance().getHttpClient();
//                                        // 需要获取登陆时的ｃｏｏｋｉｅ
//                                        String tmpString = MyHttpClient.getInstance()
//                                                .doPost(tmpClient, URLSet.getInstance().getUrlLogin(),
//                                                        listInfo, true);
//                                        Log.d("TestApp", "Thread中请求获取的内容为：" + tmpString);
//                                        Message msg = new Message();
//                                        msg.what = 0x101;
//                                        Bundle bundle = new Bundle();
//                                        bundle.putString("LoginInfo", tmpString);
//                                        msg.setData(bundle);
//                                        myHandler.sendMessage(msg);
//                                    }
//                                }).start();
//
//                        } else {
//                            Toast.makeText(context, "读取用户信息错误", Toast.LENGTH_SHORT).show();
//                        }
//                    }
                } else {
                    // Log.d("REGISTERTEST", "未收到数据！");
                    Toast.makeText(context, "网络连接故障", Toast.LENGTH_SHORT).show();
                }
            }
//
//            if(msg.what == 0x101){
//                String tmp = msg.getData().getString("LoginInfo").trim();
//                Log.d("TestApp","tmp :  " + tmp);
//                if (tmp != null && ! "".equals(tmp)) {
//                    // 登陆时ｘｍｌ数据的解析
//                    final String content = XmlParser.xmlParserGet(tmp).trim();
//
//                    Log.d("TestApp","content  :  " + content);
//                    if (!content.isEmpty()){
//                        UserNum.userNum = content;
//                        if (IfTest.ifTest){
//                            Toast.makeText(context, "userNum！" + UserNum.userNum, Toast.LENGTH_SHORT).show();
//                        }
//                        new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//                                try {
//                                    EMChatManager.getInstance().createAccountOnServer(content, "admin");
//                                } catch (EaseMobException e) {
//                                    e.printStackTrace();
//                                }
//
//                            }
//                        }).start();
//
//                        // 统一进入主界面随便看看！
//                        SMSSDK.unregisterEventHandler(eh);
//                        Intent i = new Intent(RegisterActivity.this,LoginActivity.class);
//                        startActivity(i);
//                        finish();
//                    } else {
//                        Toast.makeText(context, "登陆失败！", Toast.LENGTH_SHORT).show();
//                    }
////
//                } else {
//                    Log.d("REGISTERTEST", "登陆失败");
//                    Toast.makeText(context, "网络连接故障", Toast.LENGTH_SHORT).show();
//                }
            }


        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }

        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.blue);
        context = getApplicationContext();
        Intent intent=getIntent();
        yanzhengma=intent.getStringExtra("yanzhengma");
        email=intent.getStringExtra("phoneNum");
        initViews();

        // 添加到退出队列
        ExitApplication.getInstance().addActivity(this);
    }

    private void initViews(){

        etRegisterPassword = (EditText) findViewById(R.id.et_register_password);
        etPasswordConfirm = (EditText) findViewById(R.id.password_confirm_register);
        btnConfirmRegister = (Button) findViewById(R.id.btn_confirm_register);
        btnConfirmRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_confirm_register:
                // 注册操作

                password = etRegisterPassword.getText().toString().trim();
                passwordConfirm = etPasswordConfirm.getText().toString().trim();

                register(email, password, passwordConfirm);


                break;


        }
    }


    /**
     * 用户注册
     * @param emailGet 邮箱
     * @param passwordGet　密码
     * @param passwordConfirmGet　确认密码
     */
    private void register(final String emailGet, final String passwordGet,String passwordConfirmGet) {

        if ("".equals(emailGet) || "".equals(passwordGet) || "".equals(passwordConfirmGet)) {

            Toast.makeText(context,"注册信息不完整！",Toast.LENGTH_SHORT).show();
            // 清空所有输入数据

            etRegisterPassword.setText("");
            etPasswordConfirm.setText("");
            return;
        }
//
//        if(!UtilsGet.isEmail(emailGet)){
//            Toast.makeText(context,"邮箱格式不正确",Toast.LENGTH_SHORT).show();
//            etEmailRegister.setText("");
//            etRegisterPassword.setText("");
//            etPasswordConfirm.setText("");
//            return;
//        }

        if(!passwordConfirmGet.equals(passwordGet)){
            Toast.makeText(context,"密码前后不匹配！",Toast.LENGTH_SHORT).show();
            // 只清空密码
            etRegisterPassword.setText("");
            etPasswordConfirm.setText("");
            return;
        }

       myHandler.sendEmptyMessage(0x445);

        // 执行注册的操作
        new Thread(new Runnable() {
            @Override
            public void run() {
                //不带clientid
                xmlInfo = postRegisterInfo(emailGet,passwordGet);

//                xmlInfo = postRegisterInfo(emailGet,passwordGet,true);
                Message msg = new Message();
                msg.what = 0x100;
                Bundle bundle = new Bundle();
                if(xmlInfo==null)
                    xmlInfo="";
                bundle.putString("RegisterInfo",xmlInfo);
                msg.setData(bundle);

                Log.d("cout",msg.toString());
                myHandler.sendMessage(msg);
            }
        }).start();
    }

    /**
     * 提交注册信息
     * @param mEmail 注册邮箱
     * @param mPassword 注册密码
     * @return 服务器返回的ｘｍｌ信息
     */
    private String postRegisterInfo(String mEmail,String mPassword){
        // 将请求内容添加到请求头
        NameValuePair pairEmail = new BasicNameValuePair("email",mEmail);
        NameValuePair pairPassword = new BasicNameValuePair("password",mPassword);
        listInfo.add(pairEmail);
        listInfo.add(pairPassword);
        // 执行post操作
        client = MyHttpClient.getInstance().getHttpClient();
        String result = MyHttpClient.getInstance().doPost(client,
                URLSet.getInstance().getUrlRegister(),listInfo);
        return result;
    }

    /**
     * 提交注册信息
     * @param mEmail 注册邮箱
     * @param mPassword 注册密码
     * @return 服务器返回的ｘｍｌ信息
     */
    private String postRegisterInfo(String mEmail,String mPassword,boolean withClientID){
        // 将请求内容添加到请求头
        NameValuePair pairEmail = new BasicNameValuePair("email",mEmail);
        NameValuePair pairPassword = new BasicNameValuePair("password",mPassword);

        listInfo.add(pairEmail);
        listInfo.add(pairPassword);

        if (withClientID)
        {
            String clientID = PushManager.getInstance().getClientid(this);
            NameValuePair pairClientID = new BasicNameValuePair("clientid",clientID);
            listInfo.add(pairClientID);
        }
        // 执行post操作
        client = MyHttpClient.getInstance().getHttpClient();
        String result = MyHttpClient.getInstance().doPost(client,
                URLSet.getInstance().getUrlRegister(),listInfo);
        return result;
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
