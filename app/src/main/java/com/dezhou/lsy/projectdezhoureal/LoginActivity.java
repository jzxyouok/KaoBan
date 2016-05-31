package com.dezhou.lsy.projectdezhoureal;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.easemob.chat.EMChatManager;
import com.easemob.exceptions.EaseMobException;
import com.igexin.sdk.PushManager;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;

import org.apache.http.NameValuePair;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import cn.smssdk.SMSSDK;
import exitapp.ExitApplication;
import kaobanxml.XmlParser;
import kaobanxml.XmlSharedPreferences;
import kongjian.AlertDialogs;
import network.MyHttpClient;
import tempvalue.IfTest;
import tempvalue.UserNum;
import utils.URLSet;

/**
 * 登陆操作页面
 * 主要使用Thread-Handler机制！
 */
/**
 * 登陆操作页面
 * 主要使用Thread-Handler机制！
 */
public class LoginActivity extends ActionBarActivity implements View.OnClickListener {

    private static Context context;

    //private Toolbar toolBarLogin;

    private EditText etEmail;
    private EditText etPassword;

    private ImageView loginForQQ,loginForSina;
    private Button btnLogin,register;

    private boolean isExit = false;
    private String emailGet = "";
    private String passwordGet = "";
    private String xmlInfo = null;
    private ArrayList<NameValuePair> listInfo = new ArrayList<NameValuePair>();

    private DefaultHttpClient client = null;

    private static SharedPreferences.Editor editor;
    private Button forgetpassword;
    private ProgressDialog dialog;
    UMQQSsoHandler qqSsoHandler;

    private String contentNext="";
    UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.login");
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            dialog.dismiss();
            switch (msg.what){
                case 0x200:
                    String tmp = msg.getData().getString("LoginInfo").trim();
                    String email = msg.getData().getString("Email").trim();
                    String pwd = msg.getData().getString("Pwd").trim();
                    String result=XmlParser.xmlResultGet(tmp);

                    if(tmp != null) {
                        // 注册时ｘｍｌ数据的解析,返回success，fail
                        contentNext = XmlParser.xmlParserGet(tmp).trim();

                        //登录后返回的是userNum 相应的服务器login函数应该修改返回值

                        // 判断是否为真  此处应该加上判断不是fail
                        if (!contentNext.equals("fail")){
                            UserNum.userNum = contentNext;
                            if (IfTest.ifTest){
                                Toast.makeText(context, "userNum！" + UserNum.userNum, Toast.LENGTH_SHORT).show();
                            }
                            XmlSharedPreferences.getInstance().writeUsername(editor, email);
                            XmlSharedPreferences.getInstance().writePassword(editor, pwd);

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        EMChatManager.getInstance().createAccountOnServer(contentNext, "admin");
                                    } catch (EaseMobException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }).start();



                            // 统一进入主界面随便看看！
                            Intent intent = new Intent(context, MainActivity.class);
                            // 非Ａｃｔｉｖｉｔｙ中跳转Ａｃｔｉｖｉｔｙ
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                            finish();
                        } else {

                            if (IfTest.ifTest){
                                Toast.makeText(context, "登陆失败！", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(context, "用户名或密码错误！", Toast.LENGTH_SHORT).show();
                            }
                        }


                    } else {
                        Toast.makeText(context, "网络连接故障", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 0x201:
                    String resultXML = msg.getData().getString("LoginInfo").trim();
//                    String resultSecondLogin=XmlParser.xmlResultGet(resultXML);
                 if(resultXML.equals(""))
                     break;
                        // 注册时ｘｍｌ数据的解析,返回success，fail

                        String contentNext = XmlParser.xmlResultGet(resultXML).trim();

                        //登录后返回的是userNum 相应的服务器login函数应该修改返回值

                        // 判断是否为真  此处应该加上判断不是fail
                        if (!contentNext.equals("fail")){
                            UserNum.userNum = contentNext;
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            LoginActivity.this.startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this,"登录失败",Toast.LENGTH_SHORT).show();
                        }
                    break;
            }


        }
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }
        SMSSDK.initSDK(this, URLSet.APP_KEY_SMS, URLSet.APP_SECRETE_SMS);
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.blue);
        initData();

        // 初始化各项组件register
        initViews();

        // 加入到退出队列
        ExitApplication.getInstance().addActivity(this);
    }

    private void initData() {
        context = getApplicationContext();
        editor = XmlSharedPreferences.getInstance().getMySharedPreferencesEditor(context);
    }

    private void initViews() {
        forgetpassword=(Button)findViewById(R.id.tv_forget_password);
        etEmail = (EditText) findViewById(R.id.et_email);
        etPassword = (EditText) findViewById(R.id.et_password);
        btnLogin = (Button) findViewById(R.id.btn_confirm_login);
        register=(Button)findViewById(R.id.register);
        loginForSina=(ImageView)findViewById(R.id.loginforsina);
        btnLogin.setOnClickListener(this);
        loginForQQ = (ImageView) findViewById(R.id.loginforqq);
        loginForQQ.setOnClickListener(this);
        loginForSina.setOnClickListener(this);
        register.setOnClickListener(this);
        forgetpassword.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_confirm_login:
                // 登陆操作
                emailGet = etEmail.getText().toString().trim();
                passwordGet = etPassword.getText().toString().trim();

//                login(emailGet,passwordGet);
                login(emailGet,passwordGet);
                //
                break;
            case R.id.loginforqq:
                // 退出操作
                // 统一进入主界面随便看看
              /*  Intent intent = new Intent(context,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                finish();*/
                loginForQQ();

                break;
            case R.id.loginforsina:
                loginForSina();

                break;

            case R.id.register:
                Intent intent = new Intent(LoginActivity.this,ForgetPasswordActivity.class);
                intent.putExtra("flag",2);
                startActivity(intent);

                break;
            case R.id.tv_forget_password:
                Intent intent2=new Intent(LoginActivity.this,ForgetPasswordActivity.class);
                intent2.putExtra("flag",1);
                startActivity(intent2);

        }
    }

    /**
     * 用户登陆
     * @param emailGet 邮箱
     * @param passwordGet　密码
     */
    private void login(final String emailGet, final String passwordGet) {
        if ("".equals(emailGet) || "".equals(passwordGet)) {
            Toast.makeText(context,"登陆信息不完整！",Toast.LENGTH_SHORT).show();
            // 清空所有输入数据
//            etEmail.setText("");
//            etPassword.setText("");
            return;
        }

       /* if(!UtilsGet.isEmail(emailGet)){
            Toast.makeText(context,"邮箱格式不正确",Toast.LENGTH_SHORT).show();
            etEmail.setText("");
            etPassword.setText("");
            //etPasswordConfirm.setText("");
            return;
        }*/

        /*if(!UtilsGet.isMobileNO(emailGet)){
            Toast.makeText(LoginActivity.this,"手机号格式不正确",Toast.LENGTH_SHORT).show();
            return;
        }*/
        else {
            dialog = new ProgressDialog(this);
            dialog.setMessage("正在登录...");
            dialog.show();
            // 执行登录的操作
            new Thread(new Runnable() {
                @Override
                public void run() {

                    //不带clientid
//                xmlInfo = postLoginInfo(emailGet, passwordGet);
                    xmlInfo = postLoginInfo(emailGet, passwordGet, true);
                    Message msg = new Message();
                    msg.what = 0x200;
                    Bundle bundle = new Bundle();
                    bundle.putString("LoginInfo", xmlInfo);
                    bundle.putString("Email", emailGet);
                    bundle.putString("Pwd", passwordGet);
                    msg.setData(bundle);
                    mHandler.sendMessage(msg);
                }
            }).start();
        }
    }



    /**
     * 提交登陆信息
     * @param mEmail 注册邮箱
     * @param mPassword 注册密码
     * @return 服务器返回的ｘｍｌ信息
     */
    private String postLoginInfo(String mEmail,String mPassword){
        // 将请求内容添加到请求头
        NameValuePair pairEmail = new BasicNameValuePair("email",mEmail);
        NameValuePair pairPassword = new BasicNameValuePair("password",mPassword);
        listInfo.add(pairEmail);
        listInfo.add(pairPassword);
        // 执行post操作
        client = MyHttpClient.getInstance().getHttpClient();
        String result = MyHttpClient.getInstance().doPost(client,
                URLSet.getInstance().getUrlLogin(),listInfo,true);
        return result;
    }
    /**
     * 提交登陆信息
     * @param mEmail 注册邮箱
     * @param mPassword 注册密码
     * @return 服务器返回的ｘｍｌ信息
     */
    private String postLoginInfo(String mEmail,String mPassword,boolean withClientID){
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
                URLSet.getInstance().getUrlLogin(),listInfo,true);
        return result;
    }

    /**
     * 退出操作
     * @param keyCode
     * @param event
     * @return
     */

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            exitByKeyDown2Click();
        }
        return false;
    }

    // 双击退出的操作
    public void exitByKeyDown2Click() {

        new AlertDialogs(LoginActivity.this).builder().setTitle("提示")
                .setMsg("确定退出？")
                .setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ExitApplication.getInstance().exit(); // 执行退出操作
                        System.exit(0);

                    }
                }).setNegativeButton("取消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        }).show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**使用SSO授权必须添加如下代码 */
        UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode);
        if(ssoHandler != null){
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void loginForSina(){
        mController.doOauthVerify(LoginActivity.this, SHARE_MEDIA.SINA, new SocializeListeners.UMAuthListener() {
            @Override
            public void onError(SocializeException e, SHARE_MEDIA platform) {
            }

            @Override
            public void onComplete(Bundle value, SHARE_MEDIA platform) {
                if (value != null && !TextUtils.isEmpty(value.getString("uid"))) {


                    Toast.makeText(LoginActivity.this, "授权成功.", Toast.LENGTH_SHORT).show();


                    mController.getPlatformInfo(LoginActivity.this, SHARE_MEDIA.SINA, new SocializeListeners.UMDataListener() {
                        @Override
                        public void onStart() {
                            Toast.makeText(LoginActivity.this, "获取平台数据开始...", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onComplete(int status, Map<String, Object> info) {
                            if (status == 200 && info != null) {
                                StringBuilder sb = new StringBuilder();
                                Set<String> keys = info.keySet();
                                for (String key : keys) {
                                    sb.append(key + "=" + info.get(key).toString() + "\r\n");
                                }

                            } else {

                            }
                        }
                    });


                } else {
                    Toast.makeText(LoginActivity.this, "授权失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancel(SHARE_MEDIA platform) {
            }

            @Override
            public void onStart(SHARE_MEDIA platform) {
            }
        });

    }
    public void loginForQQ(){
        qqSsoHandler = new UMQQSsoHandler(LoginActivity.this, URLSet.APP_ID_QQ,
                URLSet.APP_KEY_QQ);
        qqSsoHandler.addToSocialSDK();
        mController.doOauthVerify(LoginActivity.this, SHARE_MEDIA.QQ, new SocializeListeners.UMAuthListener() {
            @Override
            public void onStart(SHARE_MEDIA platform) {
                Toast.makeText(LoginActivity.this, "授权开始", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onError(SocializeException e, SHARE_MEDIA platform) {
                Toast.makeText(LoginActivity.this, "授权错误", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onComplete(Bundle value, SHARE_MEDIA platform) {
                Toast.makeText(LoginActivity.this, "授权完成", Toast.LENGTH_SHORT).show();
                //获取相关授权信息
                mController.getPlatformInfo(LoginActivity.this, SHARE_MEDIA.QQ, new SocializeListeners.UMDataListener() {
                    @Override
                    public void onStart() {
                        Toast.makeText(LoginActivity.this, "获取平台数据开始...", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onComplete(int status, Map<String, Object> info) {
                        if(status == 200 && info != null){
                            String id_third="";
                            String name="";
                            StringBuilder sb = new StringBuilder();
                            Set<String> keys = info.keySet();
                            for(String key : keys){
                                sb.append(key+"="+info.get(key).toString()+"\r\n");
                                if(key.equals("screen_name")){
                                    name=info.get(key).toString();
                                }
                                if(key.equals("profile_image_url")){
                                    id_third=info.get(key).toString();
                                }
                            }
                            thirdLogin(id_third,name);

                        }else{
                            Toast.makeText(LoginActivity.this,"获取平台信息失败",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            @Override
            public void onCancel(SHARE_MEDIA platform) {
                Toast.makeText(LoginActivity.this, "授权取消", Toast.LENGTH_SHORT).show();
            }
        } );
    }
public void thirdLogin(String id_third,String name){
    listInfo=new ArrayList<NameValuePair>();
    NameValuePair pairUID = new BasicNameValuePair("uid",id_third);
    NameValuePair pairName=new BasicNameValuePair("name",name);
    listInfo.add(pairUID);
    listInfo.add(pairName);
    dialog = new ProgressDialog(LoginActivity.this);
    dialog.setMessage("正在登录...");
    dialog.show();
    new Thread(new Runnable() {
        @Override
        public void run() {
            // 执行post操作
            client = MyHttpClient.getInstance().getHttpClient();
            String result = MyHttpClient.getInstance().doPost(client,
                    URLSet.login_third, listInfo, true);

            Message msg = new Message();
            msg.what = 0x201;
            Bundle bundle = new Bundle();
            if(result==null){
                result="";
            }
            bundle.putString("LoginInfo", result);
            msg.setData(bundle);
            mHandler.sendMessage(msg);
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
