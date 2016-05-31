package com.dezhou.lsy.projectdezhoureal;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import utils.URLSet;

public class ForgetPasswordActivity extends ActionBarActivity {

    Button step;
    EditText phoneNum;
    String phone;
    private TextView back;
    int flag=0;//忘记密码与注册公用的界面，flag＝1代表忘记密码 flag＝2代表注册
    public static ForgetPasswordActivity instance;

    Handler handler=new Handler(){
        public void handleMessage(Message message){
        switch (message.what){
            case 0x242:
                step.setClickable(true);
                step.setBackgroundResource(R.drawable.login_btn_selector);

                break;
        }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }

        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.blue);
        flag=getIntent().getIntExtra("flag",0);
        instance=this;

        SMSSDK.registerEventHandler(eh); //注册短信回调
        initView();
    }

    public void initView(){
        step=(Button)findViewById(R.id.forgetpassword_step);
        back=(TextView)findViewById(R.id.backactivity);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        phoneNum=(EditText)findViewById(R.id.forgetpassword_phone);
        phoneNum.addTextChangedListener(new EditChangedListener(phoneNum));
        step.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phone=phoneNum.getText().toString().trim();
                SMSSDK.getVerificationCode("+86", phone);
            }
        });
    }



    EventHandler eh=new EventHandler(){

        @Override
        public void afterEvent(int event, int result, Object data) {

            if (result == SMSSDK.RESULT_COMPLETE) {
                //回调完成
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    //提交验证码成功
                }else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
                    //获取验证码成功

                        Intent intent = new Intent(ForgetPasswordActivity.this, InputYanzhengActivity.class);
                        intent.putExtra("phoneNum", phone);
                        intent.putExtra("flag", flag);
                        startActivity(intent);
                        SMSSDK.unregisterEventHandler(eh);

                }else if (event ==SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES){
                    //返回支持发送验证码的国家列表
                }
            }else{
                ((Throwable)data).printStackTrace();
            }
        }
    };



    class EditChangedListener implements TextWatcher {
        private CharSequence temp;//监听前的文本
        private int editStart;//光标开始位置
        private int editEnd;//光标结束位置
        private final int charMaxNum = 10;
        EditText mEditTextMsg;
        public EditChangedListener(EditText mEditTextMsg){
            this.mEditTextMsg=mEditTextMsg;
        }
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            temp = s;
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
//            if (DEBUG)
//                Log.i(TAG, "输入文字中的状态，count是一次性输入字符数");
//            mTvAvailableCharNum.setText("还能输入" + (charMaxNum - s.length()) + "字符");

        }

        @Override
        public void afterTextChanged(Editable s) {

            /** 得到光标开始和结束位置 ,超过最大数后记录刚超出的数字索引进行控制 */
            editStart = mEditTextMsg.getSelectionStart();
            editEnd = mEditTextMsg.getSelectionEnd();
            if (temp.length() ==11) {
                Message message=new Message();
                message.what=0x242;
                handler.sendMessage(message);
            }

        }
    };
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
