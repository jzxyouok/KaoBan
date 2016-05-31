package com.Lc;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.com.easemob.chatuidemo.DemoHXSDKHelper;
import com.dezhou.lsy.projectdezhoureal.MainActivity;
import com.dezhou.lsy.projectdezhoureal.R;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.applib.controller.HXSDKHelper;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactListener;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.exceptions.EaseMobException;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import org.apache.http.Header;
import org.xmlpull.v1.XmlPullParser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import bean.UserInfoBean;
import fragment.FriendListFragment;
import tempvalue.UnReadMessage;
import utils.URLSet;

public class FriendInvited extends Activity implements EMEventListener{
    private TextView message,name;
    private Button agree,refuse;
    private FriendListFragment f=new FriendListFragment();
    private RelativeLayout invited;
    private UserInfoBean userInf;
    private ImageView avatar;
    private ImageView back_invite;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_invited);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }

        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.blue);
        message = (TextView)findViewById(R.id.invite_message);
        agree = (Button)findViewById(R.id.user_state);
        name=(TextView)findViewById(R.id.invite_name);
        refuse=(Button)findViewById(R.id.refuse);
        invited=(RelativeLayout)findViewById(R.id.friend_invited);
        avatar=(ImageView)findViewById(R.id.avatar);
        back_invite=(ImageView)findViewById(R.id.back_invite);
        back_invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        final String username = getIntent().getStringExtra("username");
        String reason = getIntent().getStringExtra("reason");
        if (UnReadMessage.username==null){
            invited.setVisibility(View.GONE);

        }
        else{
            getUserInfoHttp(username,name,message,avatar);
        }

        agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                receiveInvite(username);
            }
        });
        refuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refuse(username);
            }
        });

        NewMessageBroadcastReceiver msgReceiver = new NewMessageBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(EMChatManager.getInstance().getNewMessageBroadcastAction());
        intentFilter.setPriority(3);
        registerReceiver(msgReceiver, intentFilter);

        //最后要通知sdk，UI 已经初始化完毕，注册了相应的receiver和listener, 可以接受broadcast了
        EMChat.getInstance().setAppInited();

        IntentFilter inviteIntentFilter = new IntentFilter(EMChatManager.getInstance().getContactInviteEventBroadcastAction());
        registerReceiver(contactInviteReceiver, inviteIntentFilter);


        EMContactManager.getInstance().setContactListener(new EMContactListener() {

            @Override
            public void onContactAgreed(String username) {
                //好友请求被同意

            }

            @Override
            public void onContactRefused(String username) {
                //好友请求被拒绝
            }

            @Override
            public void onContactInvited(final String username, String reason) {

            }

            @Override
            public void onContactDeleted(List<String> usernameList) {
                //被删除时回调此方法
            }


            @Override
            public void onContactAdded(List<String> usernameList) {
                //增加了联系人时回调此方法
            }
        });
    }



    private BroadcastReceiver contactInviteReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            //请求理由
            final String reason = intent.getStringExtra("reason");
            final boolean isResponse = intent.getBooleanExtra("isResponse", false);
            //消息发送方username
            final String from = intent.getStringExtra("username");
            //sdk暂时只提供同意好友请求方法，不同意选项可以参考微信增加一个忽略按钮。
            if (!isResponse) {
                name.setText(from);
                message.setText(from + "请求添加你为好友" + reason);

                agree.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        receiveInvite(from);
                    }
                });


            } else {
                Log.d("", from + "同意了你的好友请求");
            }
            //具体ui上的处理参考chatuidemo。
        }
    };


    private void receiveInvite(final String username) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    EMChatManager.getInstance().acceptInvitation(username);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            UnReadMessage.username=null;
                            UnReadMessage.reason=null;
                            Toast.makeText(getApplicationContext(), "添加好友成功", Toast.LENGTH_SHORT).show();
                            Intent i=new Intent(FriendInvited.this, MainActivity.class);
                            startActivity(i);
                            finish();
                        }
                    });
                } catch (EaseMobException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "添加好友失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void refuse(final String username) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    EMChatManager.getInstance().refuseInvitation(username);//需异步处理
                    runOnUiThread(new Runnable() {
                        public void run() {
                            UnReadMessage.username=null;
                            UnReadMessage.reason=null;
                            Toast.makeText(getApplicationContext(), "拒绝好友请求", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(FriendInvited.this, MainActivity.class);
                            startActivity(i);
                            finish();
                        }
                    });
                } catch (EaseMobException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "拒绝好友请求失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                    e.printStackTrace();
                }
            }
        }).start();

    }
    @Override
    public void onEvent(EMNotifierEvent event) {
        switch (event.getEvent()) {
            case EventNewMessage:
            {
                //获取到message
                EMMessage message = (EMMessage) event.getData();
                String username = null;
                //单聊消息
                username = message.getFrom();
                //声音和震动提示有新消息
                HXSDKHelper.getInstance().getNotifier().viberateAndPlayTone(message);
                break;
            }
            case EventDeliveryAck:
            {
                //获取到message
                EMMessage message = (EMMessage) event.getData();
                break;
            }
            case EventReadAck:
            {
                //获取到message
                EMMessage message = (EMMessage) event.getData();
                break;
            }
            case EventOfflineMessage:
            {
                //a list of offline messages
                //List<EMMessage> offlineMessages = (List<EMMessage>) event.getData();

                break;
            }
            default:
                break;
        }

    }



    @Override
    protected void onResume() {
        super.onResume();


        DemoHXSDKHelper sdkHelper = (DemoHXSDKHelper) DemoHXSDKHelper.getInstance();
        sdkHelper.pushActivity(this);
        // register the event listener when enter the foreground
        EMChatManager.getInstance().registerEventListener(
                this,
                new EMNotifierEvent.Event[] { EMNotifierEvent.Event.EventNewMessage,EMNotifierEvent.Event.EventOfflineMessage,
                        EMNotifierEvent.Event.EventDeliveryAck, EMNotifierEvent.Event.EventReadAck });
    }

    private class NewMessageBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 注销广播
            abortBroadcast();

            // 消息id（每条消息都会生成唯一的一个id，目前是SDK生成）
            String msgId = intent.getStringExtra("msgid");
            //发送方
            String username = intent.getStringExtra("from");
            // 收到这个广播的时候，message已经在db和内存里了，可以通过id获取mesage对象
            EMMessage message = EMChatManager.getInstance().getMessage(msgId);
            EMConversation conversation = EMChatManager.getInstance().getConversation(username);
            // 如果是群聊消息，获取到group id
            if (message.getChatType() == EMMessage.ChatType.GroupChat) {
                username = message.getTo();
            }
            if (!username.equals(username)) {
                // 消息不是发给当前会话，return
                return;
            }
        }
    }
    private void getUserInfoHttp(String username, final TextView name,final TextView message, final ImageView imagehead) {
        String url = URLSet.serviceUrl+"/kaoban/userInformation/?userNum="+username;
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                if (statusCode == 200) {
                    InputStream xml = new ByteArrayInputStream(responseBody);
                    try {
                        userInf = getUserInf(xml);
                        String ava_username=userInf.getName();
                        String imagepath=userInf.getHeadImg();
                        name.setText(ava_username);
                        message.setText("加个好友呗");
                        String url1 = URLSet.serviceUrl+ imagepath;
                        String str2="";
                        str2 = url1.replaceAll(" ", "");
                        URL url2= new URL(str2);
                        Glide.with(getApplicationContext()).load(url2).into(imagehead);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                System.out.println("---->error");
            }
        });
    }



    /**
     * 用户信息解析
     * @param xml  InputStream
     * @return UserInfoBean
     * @throws Exception
     */
    private UserInfoBean getUserInf(InputStream xml) throws Exception {

        UserInfoBean userInf = null;
        XmlPullParser parser = Xml.newPullParser(); // 利用Android的Xml工具类获取xmlPull解析器
        parser.setInput(xml, "UTF-8"); // 解析文件，设置字符集
        int event = parser.getEventType(); // 获取解析状态，返回的是int型数字状态
        while (event != XmlPullParser.END_DOCUMENT) {
            switch (event) {

                case XmlPullParser.START_TAG:
                    if ("Document".equals(parser.getName())) {
                        userInf = new UserInfoBean();
                    } else if ("name".equals(parser.getName())) {
                        userInf.setName(parser.nextText());
                    } else if ("number".equals(parser.getName())) {
                        userInf.setNumber(parser.nextText());
                    } else if ("sex".equals(parser.getName())) {
                        userInf.setSex(parser.nextText());
                    } else if ("birthday".equals(parser.getName())) {
                        userInf.setBirthday(parser.nextText());
                    } else if ("school".equals(parser.getName())) {
                        userInf.setSchool(parser.nextText());
                    } else if ("hometown".equals(parser.getName())) {
                        userInf.setHomeTown(parser.nextText());
                    } else if ("province".equals(parser.getName())) {
                        userInf.setProvince(parser.nextText());
                    } else if ("habit".equals(parser.getName())) {
                        userInf.setHabit(parser.nextText());
                    } else if ("job".equals(parser.getName())) {
                        userInf.setJob(parser.nextText());
                    } else if ("headImg".equals(parser.getName())) {
                        userInf.setHeadImg(parser.nextText());
                    }else if("photos".equals(parser.getName())){
                        userInf.setPhotos(parser.nextText());
                    }
                    break;
                case XmlPullParser.END_TAG:
                    break;
            }
            event = parser.next(); // 让指针指向下一个节点
        }
        return userInf;
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
