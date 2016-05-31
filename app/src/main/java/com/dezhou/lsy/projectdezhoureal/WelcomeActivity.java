package com.dezhou.lsy.projectdezhoureal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.igexin.sdk.PushManager;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import org.apache.http.NameValuePair;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;

import cn.smssdk.SMSSDK;
import exitapp.ExitApplication;
import kaobanxml.XmlParser;
import kaobanxml.XmlSharedPreferences;
import network.MyHttpClient;
import tempvalue.IfTest;
import tempvalue.UserNum;
import utils.URLSet;


public class WelcomeActivity extends ActionBarActivity {

    private Context context;

    private int enterNumber;
    private String username;
    private String pwd;
    private String xmlInfo = null;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor spEditor;

    private int TIME = 2000;
    private int loginTime = 0;
//    private static final int GOMAINACTIVITY = 1000;
    private static final int GOGUIDE = 1001;

    private ArrayList<NameValuePair> listInfo = new ArrayList<NameValuePair>();
    private DefaultHttpClient client = null;
    private static final int sleepTime = 2000;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch (msg.what) {
                case GOGUIDE:
                    Intent intent = new Intent(context,LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    finish();
                    break;
//
                case 0x200:

                    String tmp = msg.getData().getString("LoginInfo");
                    String email = msg.getData().getString("Email").trim();
                    String pwd = msg.getData().getString("Pwd").trim();
                    if(tmp != null) {
                        // 注册时ｘｍｌ数据的解析
                        String contentNext = XmlParser.xmlParserGet(tmp).trim();
                        Log.d("REGISTERTEST", "保存获取的＋" + contentNext);

                        if (!contentNext.isEmpty() && ! "fail".equals(contentNext)){
                            UserNum.userNum = contentNext;
                            Log.d("loginusernum","usernum" + UserNum.userNum);
                            if (IfTest.ifTest){
                                Toast.makeText(context, "userNum！" + UserNum.userNum, Toast.LENGTH_SHORT).show();
                            }
                            // 统一进入主界面随便看看！
                            Intent intent1 = new Intent(context, MainActivity.class);
                            // 非Ａｃｔｉｖｉｔｙ中跳转Ａｃｔｉｖｉｔｙ
                            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent1);
                            finish();
                        } else {
                            Toast.makeText(context, "登陆失败！", Toast.LENGTH_SHORT).show();

                            if (loginTime > 3){
                                finish();
                            }
                            login(email,pwd);
                            loginTime ++;
                        }

                    } else {
                        Toast.makeText(context, "网络连接故障", Toast.LENGTH_SHORT).show();
                        if (loginTime > 3){
                            finish();
                        }
                        login(email,pwd);
                        loginTime ++;
                    }

                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }

        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.blue);
        context = getApplicationContext();

        //loginHuanxin(huanxinName,passwordHuanXin);
        SMSSDK.initSDK(this, URLSet.APP_KEY_SMS, URLSet.APP_SECRETE_SMS);
        // 初始化跳转
        if(readXML()){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                        Intent intent=new Intent(WelcomeActivity.this,GuidePageActivity.class);
                        startActivity(intent);
                        finish();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        }else{
        init();
        }
        // 添加到退出队列
        ExitApplication.getInstance().addActivity(this);
    }

    public boolean readXML(){
        SharedPreferences sharedPreferences= getSharedPreferences("userInfo",
                Activity.MODE_PRIVATE);
// 使用getString方法获得value，注意第2个参数是value的默认值
        return sharedPreferences.getBoolean("ifFirst", true);//true代表首次进入

    }


    private void init(){
        initGeTui();

        sharedPreferences = XmlSharedPreferences.getInstance().getMySharedPreferences(context);
        spEditor = XmlSharedPreferences.getInstance().getMySharedPreferencesEditor(context);
        // 获取进入次数
        enterNumber = XmlSharedPreferences.getInstance().readEnterNum(sharedPreferences);
        if(enterNumber != 0){
            Log.d("TAG","登陆次数："+enterNumber);
            username = XmlSharedPreferences.getInstance().readUsername(sharedPreferences);
            pwd = XmlSharedPreferences.getInstance().readPassword(sharedPreferences);
            if(!"noNameGet".equals(username)){
                // 写入登陆次数
                XmlSharedPreferences.getInstance().writeEnterNum(spEditor,++enterNumber);
                /**
                 * 自动登陆操作，后续编写
                 * 目前直接跳转
                 */
                login(username,pwd);
                Log.d("TAG","写入登陆次数为"+enterNumber);
//                handler.sendEmptyMessageDelayed(GOMAINACTIVITY,TIME);
            } else {
                Log.d("TAG","执行到该处的登陆次数为"+enterNumber);
                // 写入登陆次数再跳转
                XmlSharedPreferences.getInstance().writeEnterNum(spEditor,++enterNumber);
                handler.sendEmptyMessageDelayed(GOGUIDE,TIME);
            }
        } else {
            XmlSharedPreferences.getInstance().writeEnterNum(spEditor,++enterNumber);
            Log.d("TAG","写入登陆次数为"+enterNumber);
            handler.sendEmptyMessageDelayed(GOGUIDE,TIME);
        }
    }

    /**
     * 初始化个推所需
     */
    private void initGeTui() {
//        PushManager.getInstance().initialize(this.getApplicationContext());
        PushManager.getInstance().initialize(this);

        String clientID = PushManager.getInstance().getClientid(this);

        if (IfTest.ifTest){
            Toast.makeText(this,"clientID : " + clientID , Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_welcome, menu);
        return true;
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



    /**
     * 用户登陆
     * @param emailGet 邮箱
     * @param passwordGet　密码
     */
    private void login(final String emailGet, final String passwordGet) {
        if ("".equals(emailGet) || "".equals(passwordGet)) {
            Toast.makeText(context, "登陆信息不完整！", Toast.LENGTH_SHORT).show();

            return;
        }

//        if(!UtilsGet.isEmail(emailGet)){
//            Toast.makeText(context,"邮箱格式不正确",Toast.LENGTH_SHORT).show();
//
//            return;
//        }

        // 执行登录的操作
        new Thread(new Runnable() {
            @Override
            public void run() {
                //不带clientid
//                xmlInfo = postLoginInfo(emailGet, passwordGet);
                xmlInfo = postLoginInfo(emailGet, passwordGet,true);
                Message msg = new Message();
                msg.what = 0x200;
                Bundle bundle = new Bundle();
                bundle.putString("LoginInfo",xmlInfo);
                bundle.putString("Email",emailGet);
                bundle.putString("Pwd",passwordGet);
                msg.setData(bundle);
                handler.sendMessage(msg);
            }
        }).start();
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
