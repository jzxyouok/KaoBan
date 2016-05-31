package com.Lc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dezhou.lsy.projectdezhoureal.R;
import com.dezhou.lsy.projectdezhoureal.RegisterActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import exitapp.ExitApplication;

public class PhoneRegister extends Activity implements View.OnClickListener{
    private static Context context;
    private EditText etEmailRegister,yanzhengEdit;
    private Button next_t,getYanzheng;
    private static String email = "";

    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x444) {
                int time = msg.getData().getInt("time");
                if (time == 0) {
                    getYanzheng.setText("获取验证码");
                    getYanzheng.setClickable(true);
                } else {
                    getYanzheng.setText(time + "s");
                }

            }
        }
    };
    EventHandler eh=new EventHandler(){

        @Override
        public void afterEvent(int event, int result, Object data) {

            if (result == SMSSDK.RESULT_COMPLETE) {
                //回调完成
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {

                    //提交验证码成功


//
                }else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){

//                    SMSSDK.unregisterEventHandler(eh);
//                    thread.start();
                }else if (event ==SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES){
                    //返回支持发送验证码的国家列表
                }else{
                    Toast.makeText(PhoneRegister.this, "验证码错误", Toast.LENGTH_SHORT).show();
                }
            }else{
                ((Throwable)data).printStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_register);
        context = getApplicationContext();

        initViews();

        // 添加到退出队列
        ExitApplication.getInstance().addActivity(this);
    }

    private void initViews() {
        etEmailRegister = (EditText) findViewById(R.id.et_regiser_email);
        getYanzheng=(Button)findViewById(R.id.send_yanzheng_button);
        yanzhengEdit=(EditText)findViewById(R.id.yanzheng_edit);
        next_t=(Button)findViewById(R.id.next_t);
        getYanzheng.setOnClickListener(this);
        next_t.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.next_t:
                String yanzheng=yanzhengEdit.getText().toString().trim();
                if (yanzheng.equals("")){
                    Toast.makeText(getApplicationContext(),"请输入验证码",Toast.LENGTH_SHORT).show();
                }
                else if(!isMobileNum(etEmailRegister.getText().toString().trim())==true){
                    Toast.makeText(getApplicationContext(),"手机号码格式不对",Toast.LENGTH_SHORT).show();
                }
                else{
                    Intent intent=new Intent(PhoneRegister.this, RegisterActivity.class);
                    intent.putExtra("yanzhengma",yanzheng);
                    startActivity(intent);
                    finish();
                }
            case R.id.send_yanzheng_button:
                SMSSDK.registerEventHandler(eh); //注册短信回调
                email = etEmailRegister.getText().toString().trim();
                SMSSDK.getVerificationCode("+86", email);
                YanzhengbuttonChange thread=new YanzhengbuttonChange();
                thread.start();
                getYanzheng.setClickable(false);
                break;

        }
    }
    public class YanzhengbuttonChange extends Thread{
        public void run(){
            int time=60;
            while(time!=0){
                time--;
                Message message=new Message();
                message.what=0x444;
                Bundle bundle=new Bundle();
                bundle.putInt("time",time);
                message.setData(bundle);
                myHandler.sendMessage(message);
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }
    public static boolean isMobileNum(String mobiles) {
        Pattern p = Pattern
                .compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        System.out.println(m.matches() + "---");
        return m.matches();

    }
}
