package fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.Lc.FeedBack;
import com.bumptech.glide.Glide;
import com.com.easemob.chatuidemo.DemoApplication;
import com.dezhou.lsy.projectdezhoureal.AboutActivity;
import com.dezhou.lsy.projectdezhoureal.R;
import com.dezhou.lsy.projectdezhoureal.StoreActivity;
import com.dezhou.lsy.projectdezhoureal.WelcomeActivity;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;

import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlPullParser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bean.UserInfoBean;
import kaobanxml.XmlCreater;
import kaobanxml.XmlParser;
import kaobanxml.XmlSharedPreferences;
import kongjian.AlertDialogs;
import kongjian.PullToRefreshView;
import kongjian.SelfActivity;
import network.MyHttpClient;
import tempvalue.IfTest;
import tempvalue.UserNum;
import tools.DataCleanManager;
import utils.URLSet;

/**
 *
 */
public class MeFragment extends Fragment implements View.OnClickListener, PullToRefreshView.OnHeaderRefreshListener, PullToRefreshView.OnFooterRefreshListener {

    final UMSocialService mController = (UMSocialService) UMServiceFactory.getUMSocialService("com.umeng.share");
    private static String shareText = "考伴助我考试成功，你也来试试";

    // 修改密码的ＵＲＬ
    private static final String urlChangePassword = URLSet.serviceUrl+"/kaoban/changePwd/";
    // 查看用户信息
    private static final String urlGetUserInfo = URLSet.serviceUrl+"/kaoban/userInformation/";

    private RelativeLayout selfRelative;

    private Button yesButton;
    private Button noButton;

    private EditText pwdEdit;

    private Dialog dialog;

    private LinearLayout dialogLinearLayout;

    private ImageView headImageView;
    private TextView nameTextView,signTextView;

    private NameValuePair npOldPwd;
    private static NameValuePair npNewPwd;

    private static List<NameValuePair> list = new ArrayList<NameValuePair>();

    private static Map<String, String> mapTmp = new HashMap<String, String>();
    private HashMap<String,SoftReference<Bitmap>> imageCache=null;
    BitmapFactory.Options option = new BitmapFactory.Options();
    private UserInfoBean userInf;
    private DemoApplication app;
    private RelativeLayout save,self_changepwd,self_share,self_check,self_secret,self_logout,self_about,self_clean;
    PullToRefreshView mPullToRefreshView;
    private TextView cleancash;
    /**
     * handler原来李松阳写的是static的，不知道为什么，写static的话不能使用getActivity
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x234){
                String tmp = msg.getData().getString("changePwd");
                if (tmp != null && !"".equals(tmp)) {
                    String content = XmlParser.xmlParserGet(tmp).toString().trim();
                    if ("success".equals(content)) {
                        // 修改本地存储的密码
                        XmlCreater.savePassword(getActivity(), "", npNewPwd.getValue());
                        Toast.makeText(getActivity(), "修改密码成功！", Toast.LENGTH_SHORT).show();
                        logout();
                    } else {
                        Toast.makeText(getActivity(), "修改密码失败，请检查网络连接！", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "请求被重置！++" + tmp, Toast.LENGTH_SHORT).show();
                }
            }


        }
    };
    private String clancashsize;

    public MeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();
    }

    private void init() {
        getUserInfoHttp(UserNum.userNum);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        return inflater.inflate(R.layout.fragment_me, container, false);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initView();
        init();
    }

    private void initView() {
//        RelativeLayout self = (RelativeLayout) this.findViewById(R.id.relative_self);
//        self.setOnClickListener(this);kandao
        mPullToRefreshView = (PullToRefreshView)this.getView().findViewById(R.id.main_pull_refresh_view);
        mPullToRefreshView.setOnHeaderRefreshListener(this);
        mPullToRefreshView.setOnFooterRefreshListener(this);

        headImageView = (ImageView) this.getView().findViewById(R.id.img_self_headimg);
        nameTextView = (TextView) this.getView().findViewById(R.id.text_mes_name);
        signTextView=(TextView)this.getView().findViewById(R.id.person_sign);

        selfRelative = (RelativeLayout) this.getView().findViewById(R.id.relative_self);
        selfRelative.setOnClickListener(this);
        cleancash=(TextView)this.getView().findViewById(R.id.datacash);
        save = (RelativeLayout) this.getView().findViewById(R.id.save);
        self_clean=(RelativeLayout)this.getView().findViewById(R.id.self_clean);
        self_about = (RelativeLayout) this.getView().findViewById(R.id.self_about);
        self_changepwd = (RelativeLayout) this.getView().findViewById(R.id.self_changepwd);
        self_check = (RelativeLayout) this.getView().findViewById(R.id.self_check);
        self_logout = (RelativeLayout) this.getView().findViewById(R.id.self_logout);
        self_secret = (RelativeLayout) this.getView().findViewById(R.id.self_secret);
        self_share = (RelativeLayout) this.getView().findViewById(R.id.self_share);
        try {
            clancashsize= DataCleanManager.getTotalCacheSize(getActivity());
            cleancash.setText(clancashsize);

        } catch (Exception e) {
            e.printStackTrace();
        }
        self_clean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialogs(getActivity()).builder().setTitle("提示")
                        .setMsg("缓存大小为"+clancashsize+",确定清理？")
                        .setPositiveButton("确认", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DataCleanManager.clearAllCache(getActivity());
                                try {
                                    clancashsize= DataCleanManager.getTotalCacheSize(getActivity());
                                    cleancash.setText(clancashsize);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        }).setNegativeButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }).show();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), StoreActivity.class);
                startActivity(i);
            }
        });
        self_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share();
            }
        });
        self_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                about();
            }
        });
        self_secret.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                secret();
            }
        });
        self_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
        self_changepwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chengePwd();
            }
        });
        self_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check();
            }
        });
    }
//        ListView listView = (ListView) getView().findViewById(R.id.list_selfmes);
//
//        List<Map<String, Object>> menuList = setList();
//
//        SimpleAdapter simpleAdapter = new SimpleAdapter(getActivity(), menuList, R.layout.selfmes_item,
//                new String[]{"selfText", "selfIcon"}, new int[]{R.id.text_item, R.id.img_item});
//
//        listView.setAdapter(simpleAdapter);

//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                switch (position) {
//                    case 0:  //修改密码
//                        chengePwd();
//                        break;
//
//                    case 1:  //分享给好友
//                        share();
//                        break;
//
//                    case 2:  //检查版本更新
//                        check();
//                        break;
//
//                    case 3:  //服务隐私
//                        secret();
//                        break;
//
//                    case 4: //注销
//                        logout();
//                        break;
//
//                    case 5:  //关于
//                        about();
//                        break;
//
//                }
//            }
//        });
//    }


//    private List<Map<String, Object>> setList() {
//        String[] selfText = {"修改密码", "推荐给好友", "检查更新", "服务隐私", "注销", "关于"};
//        int[] selfIcon = {R.drawable.self_changepwd, R.drawable.self_share, R.drawable.self_check,
//                R.drawable.self_secret, R.drawable.self_logout, R.drawable.self_about};
//
//        List<Map<String, Object>> menuList = new ArrayList<Map<String, Object>>();
//
//        for (int i = 0; i < selfText.length; i++) {
//            Map<String, Object> map = new HashMap<String, Object>();
//            map.put("selfText", selfText[i]);
//            map.put("selfIcon", selfIcon[i]);
//
//            menuList.add(map);
//        }
//
//        return menuList;
//    }

    /**
     * 修改密码
     */
    private void chengePwd() {
        dialogLinearLayout = (LinearLayout) LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_changepwd, null);
        dialog = new AlertDialog.Builder(getActivity()).create();
        dialog.show();
        dialog.getWindow().setContentView(dialogLinearLayout);
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        setDialogButton();
    }

    private void setDialogButton() {
        yesButton = (Button) dialogLinearLayout.findViewById(R.id.button_dialog_yes);
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.button_dialog_yes) {
                    changePwdYes();
                }
            }
        });

        noButton = (Button) dialogLinearLayout.findViewById(R.id.button_dialog_no);
        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.button_dialog_no) {
                    changePwdNo();
                }
            }
        });
    }

    /**
     * 修改密码框确定
     */
    private void changePwdYes() {
        pwdEdit = (EditText) dialogLinearLayout.findViewById(R.id.edit_dialog_oldpwd);
        String oldPwd = pwdEdit.getText().toString().trim();

        pwdEdit = (EditText) dialogLinearLayout.findViewById(R.id.edit_dialog_newpwd);
        String newPwd = pwdEdit.getText().toString().trim();

        dialog.cancel();

        // 添加数据
        npOldPwd = new BasicNameValuePair("oldPwd", oldPwd);
        npNewPwd = new BasicNameValuePair("newPwd", newPwd);
        list.add(npOldPwd);
        list.add(npNewPwd);
        Log.d("TestApp", "新旧密码为：" + list.toString());
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 开始请求服务器更改密码
                DefaultHttpClient httpClientTmp = MyHttpClient.getHttpClient();
                String contentBack = MyHttpClient.doPost(httpClientTmp, urlChangePassword, list, false);
                Message msg = new Message();
                msg.what = 0x234;
                Bundle bundle = new Bundle();
                bundle.putString("changePwd", contentBack);
                msg.setData(bundle);
                handler.sendMessage(msg);
            }
        }).start();

    }

    /**
     * 修改密码取消
     */
    private void changePwdNo() {
        dialog.cancel();
    }

    /**
     * 分销给好友
     */
    private void share() {

        // 设置分享内容
        mController.setShareContent(shareText);
        // 设置分享图片, 参数2为图片的url地址
        mController.setShareMedia(new UMImage(getActivity(), ""));

        String appID = "wx967daebe835fbeac";
        String appSecret = "5fa9e68ca3970e87a1f83e563c8dcbce";
        // 添加微信平台
        UMWXHandler wxHandler = new UMWXHandler(getActivity(), appID, appSecret);
        wxHandler.addToSocialSDK();
        // 添加微信朋友圈
        UMWXHandler wxCircleHandler = new UMWXHandler(getActivity(), appID, appSecret);
        wxCircleHandler.setToCircle(true);
        wxCircleHandler.addToSocialSDK();

        //参数1为当前Activity，参数2为开发者在QQ互联申请的APP ID，参数3为开发者在QQ互联申请的APP kEY.
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(getActivity(), "100424468", "c7394704798a158208a74ab60104f0ba");
        qqSsoHandler.addToSocialSDK();

        mController.getConfig().removePlatform(SHARE_MEDIA.RENREN, SHARE_MEDIA.DOUBAN);
        mController.openShare(getActivity(), false);

        if (IfTest.ifTest) {
            Toast.makeText(getActivity(), "分享给好友", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 检查版本更新
     */
    private void check() {
        Toast.makeText(getActivity(), "已经是最新版本", Toast.LENGTH_SHORT).show();
    }

    /**
     * 隐私服务
     */
    private void secret() {
        Intent feedback=new Intent(getActivity(), FeedBack.class);
        startActivity(feedback);
    }

    /**
     * 注销
     */
    private void logout() {

        EMChatManager.getInstance().logout();//此方法为同步方法
        new Thread(new Runnable() {
            @Override
            public void run() {
                //此方法为异步方法
                EMChatManager.getInstance().logout(new EMCallBack() {

                    @Override
                    public void onSuccess() {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onProgress(int progress, String status) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onError(int code, String message) {
                        // TODO Auto-generated method stub

                    }
                });
            }
        }).start();
        Toast.makeText(getActivity(), "注销", Toast.LENGTH_SHORT).show();

        //写入默认name
        SharedPreferences.Editor editor = XmlSharedPreferences.getInstance().
                getMySharedPreferencesEditor(getActivity());
        String logoutName = "noNameGet";

        XmlSharedPreferences.getInstance().writeUsername(editor, logoutName);

        getActivity().finish();

        //打开欢迎界面
        Intent intent = new Intent(getActivity(), WelcomeActivity.class);
        startActivity(intent);
    }

    /**
     * 关于
     */
    private void about() {
        Intent intent = new Intent(getActivity(), AboutActivity.class);
        startActivity(intent);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.relative_self:
                Intent intent = new Intent(this.getActivity(), SelfActivity.class);
                intent.putExtra("userNum",UserNum.userNum);
                startActivity(intent);
                break;
        }

    }

    private void selfMesTHread(final Map<String, String> map) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DefaultHttpClient client = MyHttpClient.getHttpClient();
                String tmpString = MyHttpClient.doGet(client, urlGetUserInfo, map, false);
                Message msg = new Message();
                msg.what = 0x242;
                Bundle bundle = new Bundle();
                bundle.putString("GetUserInfo", tmpString);
                msg.setData(bundle);
                handler.sendMessage(msg);
            }
        }).start();
    }




    private void getUserInfoHttp(String userName) {

        String url = URLSet.serviceUrl+"/kaoban/userInformation/?userNum="+userName;
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new AsyncHttpResponseHandler() {


            private Handler myHandler;

            private Bitmap bitmap;

            String str2 = "";

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                if (statusCode == 200) {
                    InputStream xml = new ByteArrayInputStream(responseBody);
                    try {
                        userInf = getUserInf(xml);
                        nameTextView.setText(userInf.getName());
                        signTextView.setText(userInf.getSign());
                        String url1 = URLSet.serviceUrl+ userInf.getHeadImg();
                        str2 = url1.replaceAll(" ", "");
                        URL url2=new URL(str2);
                        Glide.with(getActivity()).load(url2).into(headImageView);
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
//						String str=parser.getAttributeValue(0);
//						int index=str.indexOf(",");
//						str=str.substring(index+1);
//						byte[] decode = Base64.decode(str.toString(), Base64.DEFAULT);
//						Bitmap bitmap = BitmapFactory.decodeByteArray(decode, 0, decode.length);
//						userInf.setHeadImg(bitmap);

                    }else if ("photoWall".equals(parser.getName())) {

                    }
                    break;
                case XmlPullParser.END_TAG:
                    break;
            }
            event = parser.next(); // 让指针指向下一个节点
        }
        //xml.close();
        return userInf;
    }



    @Override
    public void onFooterRefresh(PullToRefreshView view) {
        mPullToRefreshView.postDelayed(new Runnable() {

            @Override
            public void run() {
                mPullToRefreshView.onFooterRefreshComplete();
                getUserInfoHttp(UserNum.userNum);
            }
        }, 3000);
    }

    @Override
    public void onHeaderRefresh(PullToRefreshView view) {
        mPullToRefreshView.postDelayed(new Runnable() {

            @Override
            public void run() {

                mPullToRefreshView.onHeaderRefreshComplete();
                getUserInfoHttp(UserNum.userNum);
            }
        }, 3000);

    }
}
