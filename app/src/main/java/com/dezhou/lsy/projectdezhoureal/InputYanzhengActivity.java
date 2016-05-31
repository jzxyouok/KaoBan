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


public class InputYanzhengActivity extends ActionBarActivity {

    Button step,nextGet;
    EditText yanzhengNum;
    String phoneNum;
    TextView titleText;
    int flag=0;
    private TextView back;
    public static InputYanzhengActivity instance;
    public final int GETYANZHENGREQUEST=2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_yanzheng);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }

        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.blue);
        Intent intent=getIntent();
        instance=this;
        flag=intent.getIntExtra("flag", flag);
        phoneNum=intent.getStringExtra("phoneNum");
        SMSSDK.registerEventHandler(eh);
        initView();

    }

    Handler handler=new Handler(){
        public void handleMessage(Message msg){

            switch (msg.what){
                case 0x242:
                    step.setClickable(true);
                    step.setBackgroundResource(R.drawable.login_btn_selector);
                    break;
                case 0x243:
                    Bundle bundle=msg.getData();
                    int time=bundle.getInt("time");
                    if(time==0){
                        nextGet.setText("重新获取");
                        nextGet.setClickable(true);
                    }
                    else{
                        nextGet.setText(time+"s");
                    }
                    break;
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
                    if(flag==1) {
                        Intent intent = new Intent(InputYanzhengActivity.this, InputNewPassword.class);
                        intent.putExtra("phoneNum", phoneNum);
                        startActivityForResult(intent, GETYANZHENGREQUEST);
                        SMSSDK.unregisterEventHandler(eh);
                    }
                    if(flag==2) {
                        Intent intent = new Intent(InputYanzhengActivity.this, RegisterActivity.class);
                        intent.putExtra("phoneNum", phoneNum);
                        startActivityForResult(intent, GETYANZHENGREQUEST);
                        SMSSDK.unregisterEventHandler(eh);
                    }
                }else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
                    //获取验证码成功
                    nextGet.setClickable(false);
                    Timer timeThread=new Timer();
                    timeThread.start();
                }else if (event ==SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES){
                    //返回支持发送验证码的国家列表
                }
            }else{
                ((Throwable)data).printStackTrace();
            }
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case GETYANZHENGREQUEST:
                SMSSDK.registerEventHandler(eh);
                break;
        }
    }

    public void initView(){
        back=(TextView)findViewById(R.id.backactivity);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        step=(Button)findViewById(R.id.activity_input_yanzheng_step);
        yanzhengNum=(EditText)findViewById(R.id.activity_input_yanzheng_num);
        yanzhengNum.addTextChangedListener(new EditChangedListener(yanzhengNum));
        nextGet=(Button)findViewById(R.id.forgetpassword_get_yanzheng);
        titleText=(TextView)findViewById(R.id.input_activity_title);
        titleText.setText("我们已经向您的手机 "+ phoneNum +" 发送了一条验证码");
        nextGet.setClickable(false);
        Timer timerThread=new Timer();
        timerThread.start();
        step.setOnClickListener(new ButtonClick());
        nextGet.setOnClickListener(new ButtonClick());
    }
    public class ButtonClick implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            switch (view.getId()){

                case R.id.activity_input_yanzheng_step:

                    String code=yanzhengNum.getText().toString().trim();
                    SMSSDK.submitVerificationCode("+86", phoneNum, code);
                    break;
                case R.id.forgetpassword_get_yanzheng:
                    SMSSDK.getVerificationCode("+86", phoneNum);
                    break;
            }

        }
    }
    public class Timer extends Thread{
        public void run(){
            int time=60;
            while(time!=0){
                try {
                    Thread.sleep(1000);
                    time--;
                    Message msg=new Message();
                    msg.what=0x243;
                    Bundle bundle=new Bundle();
                    bundle.putInt("time", time);
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            }
        }
    }
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
            if (temp.length() ==4) {
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
