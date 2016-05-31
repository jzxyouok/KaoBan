package com.dezhou.lsy.projectdezhoureal;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Xml;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.com.easemob.chatuidemo.Constant;
import com.com.easemob.chatuidemo.DemoHXSDKHelper;
import com.easemob.EMCallBack;
import com.easemob.EMConnectionListener;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.applib.controller.HXSDKHelper;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactListener;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chatuidemo.db.InviteMessgeDao;
import com.easemob.chatuidemo.domain.InviteMessage;
import com.easemob.chatuidemo.domain.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import org.apache.http.Header;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xmlpull.v1.XmlPullParser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adapter.MyFragmentPagerAdapter;
import bean.UserInfoBean;
import bean.VersionInfo;
import exitapp.ExitApplication;
import fragment.DiscoverFragment;
import fragment.ExamFragment;
import fragment.FriendListFragment;
import fragment.MeFragment;
import fragment.MessageFragment;
import fragment.SimilarPeopleFragment;
import kaobanxml.XmlParser;
import kongjian.AlertDialogs;
import kongjian.demo.ProvanceActivity;
import network.MyHttpClient;
import tempvalue.UnReadMessage;
import tempvalue.UserNum;
import utils.URLSet;


public class MainActivity extends FragmentActivity implements View.OnClickListener,EMEventListener{
    // 添加关注
    private static final String urlAddFocus = URLSet.serviceUrl+"/kaoban/follow/";

    private Context context;

    public static Toolbar toolbar;

    private ViewPager viewPager;

    private ImageView imgDynamic;
    private ImageView imgMsg;
    private ImageView imgExam;
    private ImageView imgMe;
    private static ArrayList<Fragment> fragments;
    private ExamFragment examFragment;
    private MeFragment meFragment;
    private MessageFragment messageFragment;
    private FriendListFragment friendListFragment;
    private MyFragmentPagerAdapter myFragmentPagerAdapter;
    private SimilarPeopleFragment similarFragment;
    private DiscoverFragment faxianFragment;

    //张翼的actionba按钮
    private Button newButton;
    private Button bestButton;
    private Button mesButton;
    private Button friendButton;
    private Button examExamButton;
    private Button examSimilarButton;
    private Button addFriendDialogYesButton;
    private Button addFriendDialogNoButton;
    public ImageButton addExamButton;
    private ImageButton examAddButton;
    private LinearLayout dialogLinearLayout;
    private Dialog dialog;


    LinearLayout examLayout,discoverLayout,messageLayout,meLayout;
    TextView examText,discoverText,messageText,meText;

    // 当前Fragment所在页面
    private int currentIndex = 0;


    // 是否退出的操作
    private static boolean isExit = false;


    private InviteMessgeDao inviteMessgeDao;
    Map<String,String> map;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }

        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.blue);


        loginHuanxin(UserNum.userNum, "admin");
        getServerVersion();
        // 获取上下文的操作
        context = getApplicationContext();
        getUserInfoHttp(UserNum.userNum);
        initViews();
        initAllFragments();
        // 将Activity添加到退出队列
        ExitApplication.getInstance().addActivity(this);

    }
    Handler handler=new Handler(){
        public void handleMessage(Message msg){
            switch (msg.what){
                case 0x453:
                    Bundle bundle=msg.getData();
                    String versionInfoStr=bundle.getString("versionInfo");
                    if(!versionInfoStr.equals("")){
                        VersionInfo versionInfo= XmlParser.xmlVersionInfo(versionInfoStr);
                        String versionName=versionInfo.getVersionName();
                        int versionCode=versionInfo.getVersionCode();
                        String versionPath=versionInfo.getNewVersionPath();
                        checkNew(versionCode,versionName,versionPath);
                    }
                    break;
            }
        }
    };
    public void getServerVersion(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                map = new HashMap<String, String>();
                map.put("userNum", UserNum.userNum);
                DefaultHttpClient client = MyHttpClient.getHttpClient();
                String simpleString = MyHttpClient.doGet(client, URLSet.checkVersion, map, true);
                Message message=new Message();
                message.what=0x453;
                Bundle bundle=new Bundle();
                if(simpleString==null)
                    simpleString="";
                bundle.putString("versionInfo",simpleString);
                message.setData(bundle);
                handler.sendMessage(message);
            }
        }).start();
    }

    public boolean checkNew(int newCode,String newName, final String versionPath){

        String versionName="";
        int versionCode;
        try{
            PackageManager manager=this.getPackageManager();
            PackageInfo info=manager.getPackageInfo(this.getPackageName(),0);
            versionName=info.versionName;
            versionCode=info.versionCode;
            if(versionCode<newCode){
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("发现新版本，是否下载");
                builder.setTitle("提示");
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent = new Intent();
                        intent.setAction("android.intent.action.VIEW");
                        Uri url = Uri.parse(versionPath);
                        intent.setData(url);
                        startActivity(intent);
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }
        }
        catch (Exception e){

        }

        return false;
    }
    public void loginHuanxin(final String userName, final String password) {
        EMChatManager.getInstance().login(userName, password, new EMCallBack() {
            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    public void run() {

                        EMGroupManager.getInstance().loadAllGroups();
                        EMChatManager.getInstance().loadAllConversations();
                    }

                });
            }


            public void onProgress(int progress, String status) {

            }


            public void onError(int code, String message) {
                Log.d("main", "");
            }
        });

        // setContactListener监听联系人的变化等
        EMContactManager.getInstance().setContactListener(new MyContactListener());
        EMContactManager.getInstance().setContactListener(new EMContactListener() {

            @Override
            public void onContactAgreed(String username) {
                //好友请求被同意
                UnReadMessage.hadInvite=true;
            }

            @Override
            public void onContactRefused(String username) {
                //好友请求被拒绝
                Toast.makeText(getApplicationContext(),username+"拒绝你的好友请求",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onContactInvited(String username, String reason) {
                Log.d("kaokao", " username  " + username + "   reason  " + reason);
                UnReadMessage.flag=true;
                UnReadMessage.username=username;
                UnReadMessage.reason=reason;
                // receiveInvite(username, reason);

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

        MyConnectionListener connectionListener = new MyConnectionListener();
        EMChatManager.getInstance().addConnectionListener(connectionListener);
    }

//    private void receiveInvite(final String username,String reason) {
//
//        Intent in = new Intent(this,FriendInvited.class);
//        in.putExtra("username",username);
//        in.putExtra("reason",reason);
//        startActivity(in);
//
//    }

    private void initViews() {
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        imgDynamic = (ImageView) findViewById(R.id.tab_dynamic_get);
        imgMsg = (ImageView) findViewById(R.id.tab_msg_get);
        imgExam = (ImageView) findViewById(R.id.tab_exam_get);
        imgMe = (ImageView) findViewById(R.id.tab_me);
        examLayout=(LinearLayout)findViewById(R.id.main_activity_exam_layout);
        discoverLayout=(LinearLayout)findViewById(R.id.main_activity_discover_layout);
        messageLayout=(LinearLayout)findViewById(R.id.main_activity_message_layout);
        meLayout=(LinearLayout)findViewById(R.id.main_activity_me_layout);
        examText=(TextView)findViewById(R.id.main_activity_exam_text);
        discoverText=(TextView)findViewById(R.id.main_activity_discover_text);
        messageText=(TextView)findViewById(R.id.main_activity_message_text);
        meText=(TextView)findViewById(R.id.main_activity_me_text);
        viewPager.setOffscreenPageLimit(6);
        examLayout.setOnClickListener(new ImgOnClickListener(0));
        discoverLayout.setOnClickListener(new ImgOnClickListener(1));
        messageLayout.setOnClickListener(new ImgOnClickListener(2));
        meLayout.setOnClickListener(new ImgOnClickListener(3));
        setExamActionBar();
    }
    private void initAllFragments() {
        // 将Fragment添加到List中
        fragments = new ArrayList<Fragment>();
        examFragment = new ExamFragment();

        fragments.add(examFragment);
        similarFragment=new SimilarPeopleFragment();
        fragments.add(similarFragment);
      /*  dynamicFragment = new DynamicFragment();
        fragments.add(dynamicFragment);*/
        faxianFragment=new DiscoverFragment();
        fragments.add(faxianFragment);
        messageFragment = new MessageFragment();
        fragments.add(messageFragment);
        friendListFragment = new FriendListFragment();
        fragments.add(friendListFragment);
        meFragment = new MeFragment();
        fragments.add(meFragment);


        // 设置Adapter
        myFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(),
                fragments);
        viewPager.setAdapter(myFragmentPagerAdapter);

        // 设置初始页面,并设置tab效果
        viewPager.setCurrentItem(0);
        imgExam.setImageResource(R.drawable.tab_exam_press);
        examText.setTextColor(getResources().getColor(R.color.blue));
        // 设置切换的监听器
        viewPager.setOnPageChangeListener(new MyPagerChangeListener());
    }

    /**
     * 监听事件
     */
    @Override
    public void onEvent(EMNotifierEvent event) {
        switch (event.getEvent()) {
            case EventNewMessage: // 普通消息
            {
                EMMessage message = (EMMessage) event.getData();

                // 提示新消息
                HXSDKHelper.getInstance().getNotifier().onNewMsg(message);

                refreshUI();
                break;
            }

            case EventOfflineMessage: {
                refreshUI();
                break;
            }

            case EventConversationListChanged: {
                refreshUI();
                break;
            }

            default:
                break;
        }
    }
    private void refreshUI() {
        runOnUiThread(new Runnable() {
            public void run() {
                messageFragment.refresh();
            }
        });
    }


    /**
     * 点击底部tab时的切换监听器
     */
    private class ImgOnClickListener implements View.OnClickListener {
        private int index = -1;
        // private ViewPager viewPagerGet;
        public ImgOnClickListener(int index) {
            this.index = index;
            // this.viewPagerGet = viewPagerGet;
        }

        @Override
        public void onClick(View v) {
            if (index==0) {
                viewPager.setCurrentItem(index, false);
            } if(index==1||index==2){
                viewPager.setCurrentItem(index + 1, false);
            }

            if(index==3){
                viewPager.setCurrentItem(index+2,false);
            }

        }
    }

    /**
     * tab切换时的监听器
     */
    private class MyPagerChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {


        }

        @Override
        public void onPageSelected(int position) {
            switch (position) {


                case 0:
                    imgDynamic.setImageResource(R.drawable.tab_dynamic);
                    imgMsg.setImageResource(R.drawable.tab_message);
                    imgMe.setImageResource(R.drawable.tab_me);
                    imgExam.setImageResource(R.drawable.tab_exam_press);
                    examText.setTextColor(getResources().getColor(R.color.blue));
                    discoverText.setTextColor(getResources().getColor(R.color.black));
                    messageText.setTextColor(getResources().getColor(R.color.black));
                    meText.setTextColor(getResources().getColor(R.color.black));
                    setExamActionBar();
                    setExamExam();
                    break;

                case 1:
                    imgDynamic.setImageResource(R.drawable.tab_dynamic);
                    imgMsg.setImageResource(R.drawable.tab_message);
                    imgMe.setImageResource(R.drawable.tab_me);
                    imgExam.setImageResource(R.drawable.tab_exam_press);
                    examText.setTextColor(getResources().getColor(R.color.blue));
                    discoverText.setTextColor(getResources().getColor(R.color.black));
                    messageText.setTextColor(getResources().getColor(R.color.black));
                    meText.setTextColor(getResources().getColor(R.color.black));
                    setExamActionBar();
                    setExamSimilar();
                    break;
                case 2:

                    imgMsg.setImageResource(R.drawable.tab_message);
                    imgExam.setImageResource(R.drawable.tab_exam);
                    imgMe.setImageResource(R.drawable.tab_me);
                    imgDynamic.setImageResource(R.drawable.tab_dynamic_press);
                    examText.setTextColor(getResources().getColor(R.color.black));
                    discoverText.setTextColor(getResources().getColor(R.color.blue));
                    messageText.setTextColor(getResources().getColor(R.color.black));
                    meText.setTextColor(getResources().getColor(R.color.black));
                    setDynamicActionBar();
                    faxianFragment.measure();
                    break;
                case 3:
                    imgDynamic.setImageResource(R.drawable.tab_dynamic);
                    imgExam.setImageResource(R.drawable.tab_exam);
                    imgMe.setImageResource(R.drawable.tab_me);
                    imgMsg.setImageResource(R.drawable.tab_message_press);
                    examText.setTextColor(getResources().getColor(R.color.black));
                    discoverText.setTextColor(getResources().getColor(R.color.black));
                    messageText.setTextColor(getResources().getColor(R.color.blue));
                    meText.setTextColor(getResources().getColor(R.color.black));
                    setMesActionBar();
                    setMes();

                    break;
                case 4:
                    imgDynamic.setImageResource(R.drawable.tab_dynamic);
                    imgExam.setImageResource(R.drawable.tab_exam);
                    imgMe.setImageResource(R.drawable.tab_me);
                    imgMsg.setImageResource(R.drawable.tab_message_press);
                    examText.setTextColor(getResources().getColor(R.color.black));
                    discoverText.setTextColor(getResources().getColor(R.color.black));
                    messageText.setTextColor(getResources().getColor(R.color.blue));
                    meText.setTextColor(getResources().getColor(R.color.black));
                    if (UnReadMessage.flag){
                        friendListFragment.UnReadMessage();
                    }
                    if (UnReadMessage.hadInvite){
                        friendListFragment.getHuanxin();
                    }
                    setMesActionBar();
                    setFriend();
                    break;
                case 5:
                    imgDynamic.setImageResource(R.drawable.tab_dynamic);
                    imgMsg.setImageResource(R.drawable.tab_message);
                    imgExam.setImageResource(R.drawable.tab_exam);
                    imgMe.setImageResource(R.drawable.tab_me_press);

                    examText.setTextColor(getResources().getColor(R.color.black));
                    discoverText.setTextColor(getResources().getColor(R.color.black));
                    messageText.setTextColor(getResources().getColor(R.color.black));
                    meText.setTextColor(getResources().getColor(R.color.blue));
                    setSelfMesActionBar();
                    break;

            }
            currentIndex = position;
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitByKeyDown2Click();
        }
        return false;
    }

    // 双击退出的操作
    public void exitByKeyDown2Click() {
        new AlertDialogs(MainActivity.this).builder().setTitle("提示")
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


//
//        Timer tExit = null;
//        if (isExit == false) {
//            isExit = true;
//            Toast.makeText(context, "再按一次退出...", Toast.LENGTH_SHORT).show();
//            tExit = new Timer();
//            tExit.schedule(new TimerTask() {
//                @Override
//                public void run() {
//                    isExit = false; // 取消退出的操作
//                }
//            }, 2000); // 两秒的时限，如果两秒内没有按下，则取消执行
//        } else {
//            ExitApplication.getInstance().exit(); // 执行退出操作
//            System.exit(0);
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private Toolbar.OnMenuItemClickListener onMenuItemClickListener = new Toolbar.OnMenuItemClickListener() {

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            String msg = "";
            switch (menuItem.getItemId()) {
                case R.id.action_plus:
                    msg += "Click plus";
                    break;

            }

            if (!msg.equals("")) {
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
            return true;
        }
    };


    /**
     * 消息和朋友列表的setactionbar
     */

    private void setMesActionBar() {
        ActionBar actionBar = this.getActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.message_actionbar);

        mesButton = (Button) actionBar.getCustomView().findViewById(R.id.button_mes_mes);
        mesButton.setOnClickListener(this);

        friendButton = (Button) actionBar.getCustomView().findViewById(R.id.button_mes_friend);
        friendButton.setOnClickListener(this);

    }
    /*
    * 相似的人的setActionbar
    * */
    private void setSimilarActionBar(){
        setActionBarCustionView(R.layout.similar_actionbar);


    }
    /**
     * 个人中心的actionbar
     */
    private void setSelfMesActionBar() {

        setActionBarCustionView(R.layout.selfmes_actionbar);

    }

    /**
     * 考试的actionbar
     */
    private void setExamActionBar() {

        ActionBar actionBar = this.getActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.exam_actionbar);

        examExamButton = (Button) actionBar.getCustomView().findViewById(R.id.button_exam_exam);
        examExamButton.setOnClickListener(this);

        examSimilarButton = (Button) actionBar.getCustomView().findViewById(R.id.button_exam_similar);
        examSimilarButton.setOnClickListener(this);

        addExamButton = (ImageButton) actionBar.getCustomView().findViewById(R.id.button_exam_add);
        addExamButton.setOnClickListener(this);

    }

    /**
     * 动态的acitonbar
     */
    private void setDynamicActionBar() {

        setActionBarCustionView(R.layout.dynamic_actionbar);

    }


    private void setActionBarCustionView(int layout) {
        ActionBar actionBar = this.getActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(layout);

    }



    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.button_mes_mes:
                setMes();
                break;

            case R.id.button_mes_friend:
                setFriend();
                break;

            case R.id.button_exam_add:
                setExam();
                break;
            case R.id.button_exam_exam:
                setExamExam();
                break;
            case R.id.button_exam_similar:
                setExamSimilar();
                break;

        }
    }
    /**
     * 点击消息后的设置
     */
    private void setMes() {

        mesButton.setBackgroundResource(R.drawable.top_button_background_press_left);

        mesButton.setTextColor(getResources().getColor(R.color.blue));
        friendButton.setTextColor(getResources().getColor(R.color.whites));
        friendButton.setBackgroundResource(R.drawable.top_button_background_right);
        viewPager.setCurrentItem(3);
    }

    /**
     * 点击联系人后的设置
     */
    private void setFriend() {

        mesButton.setBackgroundResource(R.drawable.top_button_background_left);
        friendButton.setBackgroundResource(R.drawable.top_button_background_press_right);
        mesButton.setTextColor(getResources().getColor(R.color.whites));
        friendButton.setTextColor(getResources().getColor(R.color.blue));
        viewPager.setCurrentItem(4);
    }

    /**
     * 点击考试后的设置
     */
    private void setExamExam() {
        addExamButton.setVisibility(View.VISIBLE);
        examExamButton.setBackgroundResource(R.drawable.top_button_background_press_left);

        examExamButton.setTextColor(getResources().getColor(R.color.blue));
        examSimilarButton.setTextColor(getResources().getColor(R.color.whites));
        examSimilarButton.setBackgroundResource(R.drawable.top_button_background_right);
        viewPager.setCurrentItem(0);
    }

    /**
     * 点击相似的人后的设置
     */
    private void setExamSimilar() {
        addExamButton.setVisibility(View.GONE);
        examExamButton.setBackgroundResource(R.drawable.top_button_background_left);
        examSimilarButton.setBackgroundResource(R.drawable.top_button_background_press_right);
        examExamButton.setTextColor(getResources().getColor(R.color.whites));
        examSimilarButton.setTextColor(getResources().getColor(R.color.blue));
        viewPager.setCurrentItem(1);
    }

    /**
     * 点击考试添加按钮
     * 进入添加考试界面
     */
    private void setExam() {
        Intent intent = new Intent(this, AddExamActivity.class);
        startActivity(intent);
    }


    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
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

    /**
     * 保存提示新消息
     *
     * @param msg
     */
    private void notifyNewIviteMessage(InviteMessage msg) {
        saveInviteMsg(msg);
        // 提示有新消息
        HXSDKHelper.getInstance().getNotifier().viberateAndPlayTone(null);
        messageFragment.refresh();
    }

    /**
     * 保存邀请等msg
     *
     * @param msg
     */
    private void saveInviteMsg(InviteMessage msg) {
        // 保存msg
        inviteMessgeDao.saveMessage(msg);
        // 未读数加1
        User user = ((DemoHXSDKHelper)HXSDKHelper.getInstance()).getContactList().get(Constant.NEW_FRIENDS_USERNAME);
        if (user.getUnreadMsgCount() == 0)
            user.setUnreadMsgCount(user.getUnreadMsgCount() + 1);
    }
    /***
     * 好友变化listener
     *
     */
    public class MyContactListener implements EMContactListener {

        @Override
        public void onContactAdded(List<String> usernameList) {

        }

        @Override
        public void onContactDeleted(final List<String> usernameList) {
            // 被删除

        }

        @Override
        public void onContactInvited(String username, String reason) {

            // 接到邀请的消息，如果不处理(同意或拒绝)，掉线后，服务器会自动再发过来，所以客户端不需要重复提醒
            List<InviteMessage> msgs = inviteMessgeDao.getMessagesList();

            for (InviteMessage inviteMessage : msgs) {
                if (inviteMessage.getGroupId() == null && inviteMessage.getFrom().equals(username)) {
                    inviteMessgeDao.deleteMessage(username);
                }
            }


        }

        @Override
        public void onContactAgreed(String username) {
            List<InviteMessage> msgs = inviteMessgeDao.getMessagesList();
            for (InviteMessage inviteMessage : msgs) {
                if (inviteMessage.getFrom().equals(username)) {
                    return;
                }
            }


        }

        @Override
        public void onContactRefused(String username) {

            // 参考同意，被邀请实现此功能,demo未实现
            Log.d(username, username + "拒绝了你的好友请求");
        }

    }

    public class MyConnectionListener implements EMConnectionListener {

        @Override
        public void onConnected() {
            boolean groupSynced = HXSDKHelper.getInstance().isGroupsSyncedWithServer();
            boolean contactSynced = HXSDKHelper.getInstance().isContactsSyncedWithServer();

            // in case group and contact were already synced, we supposed to notify sdk we are ready to receive the events
            if(groupSynced && contactSynced){
                new Thread(){
                    @Override
                    public void run(){
                        HXSDKHelper.getInstance().notifyForRecevingEvents();
                    }
                }.start();
            }else{
                if(!groupSynced){
//                    asyncFetchGroupsFromServer();
                }

                if(!contactSynced){
//                    asyncFetchContactsFromServer();
                }

                if(!HXSDKHelper.getInstance().isBlackListSyncedWithServer()){
//                    asyncFetchBlackListFromServer();
                }
            }

        }

        @Override
        public void onDisconnected(int i) {

        }

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

    private Handler myHandler;
    private String schoolstring="";
    private void getUserInfoHttp(String userName) {





        String url=URLSet.serviceUrl+"/kaoban/userInformation/?userNum="+userName;
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new AsyncHttpResponseHandler() {

            UserInfoBean userInf;

            InputStream xml;
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                if (statusCode == 200) {
                    xml = new ByteArrayInputStream(responseBody);
                    try {
                        myHandler=
                                new Handler()
                                {
                                    public void handleMessage(android.os.Message msg)
                                    {
                                        if(msg.what==0x1122)
                                        {

                                            if (schoolstring.toString().trim().equals("")){
                                                Intent i=new Intent(MainActivity.this, ProvanceActivity.class);

                                                i.putExtra("school_info","sjdfjds");
                                                startActivity(i);
                                                finish();
                                            }

                                        }
                                    };
                                };
                        new Thread()
                        {


                            public void run()
                            {
                                try {
                                    userInf = getUserInf(xml);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                schoolstring = userInf.getSchool();


                                myHandler.sendEmptyMessage(0x1122);

                            };

                        }.start();
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
                    } else if ("department".equals(parser.getName())) {
                        userInf.setDepartment(parser.nextText());
                    } else if ("schoolTime".equals(parser.getName())) {
                        userInf.setSchooltime(parser.nextText());
                    }else if ("hometown".equals(parser.getName())) {
                        userInf.setHomeTown(parser.nextText());
                    } else if ("province".equals(parser.getName())) {
                        userInf.setProvince(parser.nextText());
                    } else if ("habit".equals(parser.getName())) {
                        userInf.setHabit(parser.nextText());
                    }
                    else if ("job".equals(parser.getName())) {
                        userInf.setJob(parser.nextText());
                    }
                    else if ("sign".equals(parser.getName())) {
                        userInf.setSign(parser.nextText());
                    } else if ("headImg".equals(parser.getName())) {
                        userInf.setHeadImg(parser.nextText());
                    }else if ("photoWall".equals(parser.getName())) {

                    }
                    break;
                case XmlPullParser.END_TAG:
                    break;
            }
            event = parser.next(); // 让指针指向下一个节点
        }
        return userInf;
    }
}
