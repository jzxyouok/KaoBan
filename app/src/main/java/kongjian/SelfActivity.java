package kongjian;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Xml;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.Lc.BitmapUtils;
import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.com.easemob.chatuidemo.DemoApplication;
import com.dezhou.lsy.projectdezhoureal.ChatMessage;
import com.dezhou.lsy.projectdezhoureal.R;
import com.easemob.chat.EMContactManager;
import com.easemob.exceptions.EaseMobException;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xmlpull.v1.XmlPullParser;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adapter.ImageAdapter;
import adapter.ImageLcAdapter;
import bean.FenShiBean;
import bean.MyGuanZhuBean;
import bean.UserInfoBean;
import exitapp.ExitApplication;
import fragment.ArticalFragment;
import fragment.PhotoFragment;
import fragment.UserInfoFragment;
import ggl.zf.iosdialog.widget.ActionSheetDialog;
import kaobanxml.XmlParser;
import lc.com.nui.multiphotopicker.model.ImageItem;
import lc.com.nui.multiphotopicker.util.CustomConstants;
import lc.com.nui.multiphotopicker.util.IntentConstants;
import network.MyHttpClient;
import tempvalue.Cookie;
import tempvalue.MyKongjian;
import tempvalue.UserNum;


public class SelfActivity extends FragmentActivity{
    private TextView JianLi;
    private TextView dongTai,photo_wall;
    private ImageView mGuanZhu;
    private TextView mGuanZhuNum;
    private TextView mFenShiNum;
    private TextView mUserName;

    private UserInfoBean userInf;
    private boolean isGuanZhu = false;
    private UserInfoFragment mUserInfoFragment=new UserInfoFragment();
    private ArticalFragment mArticalFragment=new ArticalFragment();
    private PhotoFragment photoFragment = new PhotoFragment();
    private GridView mGridView;
    private ImageLcAdapter mAdapter;
    public static List<ImageItem> mDataList = new ArrayList<ImageItem>();
    private ImageView headImg,back,userSex;
    private LinearLayout backgroundLayout;
    private String str2 = "";
    private ImageAdapter mImageAdapter;
    public String getUserNum="";
    private Button approve,sixin;

    private static CookieStore cookieStore = null;
    private static final int TIME_OUT = 10*10000000;   //超时时间
    private static final String CHARSET = "utf-8"; //设置编码

    private List<NameValuePair> listArtical;
    boolean flag= true;//默认为开始
    private static final String guanzhuurl = "http://115.28.35.2:8802/kaoban/follow/";
    private static final String quxiaourl = "http://115.28.35.2:8802/kaoban/removeFollow/";
    private LinearLayout buttonLinear,setBackground;
    private Drawable headdrawable=null;
    private DemoApplication app;
    private String localUserName="";
    private Button addFriend;
    private ImageView addNewFriends;
    private  List<MyGuanZhuBean> guanZhuFriends;
    private HashMap<String,SoftReference<Bitmap>> imageCache=null;
    BitmapFactory.Options option = new BitmapFactory.Options();
    private TextView text;
    PullToRefreshView mPullToRefreshView;
    private Bitmap mBitmap= null;

    private Activity mcontext;

    private Handler handler=new Handler(){
        public void handleMessage(Message msg){
            switch (msg.what){

                case 0x238:
                    Bundle bundle2=new Bundle();
                    bundle2=msg.getData();
                    String sendArticalXml=bundle2.getString("guanzhu");
                    String result= XmlParser.xmlResultGet(sendArticalXml);
                    if(result.equals("success")){
                        approve.setText("已关注");
                        MyFenShi(getUserNum);
                    }
                    else{
                        Toast.makeText(SelfActivity.this,"关注失败",Toast.LENGTH_SHORT).show();

                    }
                    break;
                case 0x239:
                    Bundle bundle=new Bundle();
                    bundle=msg.getData();
                    String sendguanzhuXml=bundle.getString("quxiaoguanzhu");
                    String result1= XmlParser.xmlResultGet(sendguanzhuXml);
                    if(result1.equals("success")){
                        approve.setText("关注");
                        MyFenShi(getUserNum);
                    }
                    else{
                        Toast.makeText(SelfActivity.this,"关注失败",Toast.LENGTH_SHORT).show();

                    }
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.personzoom);
        localUserName= UserNum.userNum;
        app=(DemoApplication)getApplication();
        getUserNum=getIntent().getStringExtra("userNum");
        app.setName(getUserNum);
        ExitApplication.getInstance().addActivity(this);
        initActive();
        initData();
        initView();
    }
    protected void onPause()
    {
        super.onPause();
        saveTempToPref();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        saveTempToPref();
    }

    private void saveTempToPref()
    {
        SharedPreferences sp = getSharedPreferences(
                CustomConstants.APPLICATION_NAME, MODE_PRIVATE);
        String prefStr = JSON.toJSONString(mDataList);
        sp.edit().putString(CustomConstants.PREF_TEMP_IMAGES, prefStr).commit();

    }

    private void getTempFromPref()
    {
        SharedPreferences sp = getSharedPreferences(
                CustomConstants.APPLICATION_NAME, MODE_PRIVATE);
        String prefStr = sp.getString(CustomConstants.PREF_TEMP_IMAGES, null);
        if (!TextUtils.isEmpty(prefStr))
        {
            List<ImageItem> tempImages = JSON.parseArray(prefStr,
                    ImageItem.class);
            mDataList = tempImages;
        }
    }

    private void removeTempFromPref()
    {
        SharedPreferences sp = getSharedPreferences(
                CustomConstants.APPLICATION_NAME, MODE_PRIVATE);
        sp.edit().remove(CustomConstants.PREF_TEMP_IMAGES).commit();
    }

    @SuppressWarnings("unchecked")
    private void initData()
    {
        getTempFromPref();
        List<ImageItem> incomingDataList = (List<ImageItem>) getIntent()
                .getSerializableExtra(IntentConstants.EXTRA_IMAGE_LIST);
        if (incomingDataList != null)
        {
            mDataList.addAll(incomingDataList);
        }
        if (mDataList.size()>0 && incomingDataList!=null) {

            int listSize = mDataList.size();
            String path = mDataList.get(listSize-1).sourcePath;
//            Bitmap bitmap = getLoacalBitmap(path);
            // Context context = getApplicationContext();
            //headdrawable = BlurImages(bitmap,context);
//            headImg.setImageBitmap(bitmap);
            // backgroundLayout.setBackgroundDrawable(headdrawable);
            getArticalContent(path,getUserNum);

        }

    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if (MyKongjian.fresh){
            getUserInfoHttp(getUserNum);
        }

        notifyDataChanged(); //当在ImageZoomActivity中删除图片时，返回这里需要刷新

    }
    private int getDataSize()
    {
        return mDataList == null ? 0 : mDataList.size();
    }

    private int getAvailableSize()
    {
        int availSize = CustomConstants.MAX_IMAGE_SIZE - mDataList.size();
        if (availSize >= 0)
        {
            return availSize;
        }
        return 0;
    }

    public String getString(String s)
    {
        String path = null;
        if (s == null) return "";
        for (int i = s.length() - 1; i > 0; i++)
        {
            s.charAt(i);
        }
        return path;
    }

    public class PopupWindows extends PopupWindow
    {

        public PopupWindows(Context mContext, View parent)
        {

            new ActionSheetDialog(SelfActivity.this)
                    .builder()
                    .setCancelable(true)
                    .setCanceledOnTouchOutside(true)
                    .addSheetItem("用相机更换头像", ActionSheetDialog.SheetItemColor.Blue,
                            new ActionSheetDialog.OnSheetItemClickListener() {
                                @Override
                                public void onClick(int which) {
                                    takePhoto();
                                    dismiss();
                                }
                            })
                    .addSheetItem("去相册选择头像", ActionSheetDialog.SheetItemColor.Blue,
                            new ActionSheetDialog.OnSheetItemClickListener() {
                                @Override
                                public void onClick(int which) {
                                    Intent intent = new Intent(SelfActivity.this,
                                            ImageBucketChoose.class);
                                    intent.putExtra(IntentConstants.EXTRA_CAN_ADD_IMAGE_SIZE,
                                            getAvailableSize());
                                    startActivityForResult(intent,1);
                                    finish();
                                    dismiss();
                                }
                            }).show();

        }
    }

    public class AddPopupWindows extends PopupWindow
    {

        public AddPopupWindows(Context mContext, final String userName, final String reason)
        {

            new ActionSheetDialog(SelfActivity.this)
                    .builder()
                    .setCancelable(true)
                    .setCanceledOnTouchOutside(true)

                    .addSheetItem("加为好友", ActionSheetDialog.SheetItemColor.Blue,
                            new ActionSheetDialog.OnSheetItemClickListener() {
                                @Override
                                public void onClick(int which) {

                                    if (!localUserName.equals(getUserNum)) {
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    EMContactManager.getInstance().addContact(userName, reason);
                                                    runOnUiThread(new Runnable() {
                                                        public void run() {
                                                            Toast.makeText(getApplicationContext(), "发送请求成功,等待对方验证", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                } catch (EaseMobException e) {
                                                    runOnUiThread(new Runnable() {
                                                        public void run() {
                                                            Toast.makeText(getApplicationContext(), "发送请求失败", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                    e.printStackTrace();
                                                }
                                            }
                                        }).start();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "不能添加自己为好友", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }).show();

        }
    }

    private static final int TAKE_PICTURE = 0x000000;
    private String Caremapath="";
    private String caremapathsend="";
    public void takePhoto()
    {
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File vFile = new File(Environment.getExternalStorageDirectory()
                + "/myimage/", String.valueOf(System.currentTimeMillis())
                + ".jpg");
        if (!vFile.exists())
        {
            File vDirPath = vFile.getParentFile();
            vDirPath.mkdirs();
        }
        else
        {
            if (vFile.exists())
            {
                vFile.delete();
            }
        }
        Caremapath = vFile.getPath();
        Uri cameraUri = Uri.fromFile(vFile);
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
        startActivityForResult(openCameraIntent, TAKE_PICTURE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode)
        {
            case TAKE_PICTURE:
                if (mDataList.size() < CustomConstants.MAX_IMAGE_SIZE
                        && resultCode == -1 && !TextUtils.isEmpty(Caremapath))
                {
                    ImageItem item = new ImageItem();
                    Bitmap photobitmap= BitmapUtils.decodeSampledBitmapFromFile(Caremapath,headImg.getWidth(),headImg.getHeight());
                    saveMyBitmap(photobitmap);
                    item.sourcePath = caremapathsend;
                    mDataList.add(item);
                    if (mDataList.size()>0) {

                        int listSize = mDataList.size();
                        String path = mDataList.get(listSize-1).sourcePath;
                        // Bitmap bitmap = getLoacalBitmap(path);
                        // Context context = getApplicationContext();
                        //headdrawable = BlurImages(bitmap,context);
                        //headImg.setImageBitmap(bitmap);
                        // backgroundLayout.setBackgroundDrawable(headdrawable);
                        getArticalContent(path,getUserNum);

                    }

                }
                break;
        }
    }

    private void notifyDataChanged()
    {
        mAdapter.notifyDataSetChanged();
    }

    public void initView()
    {


        setBackground=(LinearLayout)findViewById(R.id.setBackground);
        mGridView = (GridView) findViewById(R.id.self_gridview);
        mGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        addFriend=(Button)findViewById(R.id.addFriend);
        addNewFriends=(ImageView)findViewById(R.id.addNewFriends);
        headImg = (ImageView)findViewById(R.id.headImg);
        back = (ImageView)findViewById(R.id.back);
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });
        backgroundLayout = (LinearLayout)findViewById(R.id.background);
        buttonLinear = (LinearLayout)findViewById(R.id.buttonLayout);
        if (!localUserName.equals(getUserNum)){
            buttonLinear.setVisibility(View.VISIBLE);
            setBackground.setVisibility(View.GONE);
        }

        addFriend.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!localUserName.equals(getUserNum)){
                    Intent intent = new Intent(SelfActivity.this, ChatMessage.class);
                    intent.putExtra("userId", getUserNum);
                    startActivity(intent);
                }
            }
        });
        approve=(Button)findViewById(R.id.approve);

        approve.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag) {
                    flag = false;
                    final Map<String,String> map = new HashMap<String, String>();
                    map.put("userNum", getUserNum);


                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            DefaultHttpClient client = MyHttpClient.getHttpClient();

                            String tmpString = MyHttpClient.doGet(client, guanzhuurl, map, true);
                            Message msg = new Message();
                            msg.what = 0x238;
                            Bundle bundle = new Bundle();
                            bundle.putString("guanzhu", tmpString);
                            msg.setData(bundle);
                            handler.sendMessage(msg);
                        }
                    }).start();

                } else {
                    flag = true;
                    final Map<String,String> map1 = new HashMap<String, String>();
                    map1.put("follow", getUserNum);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            DefaultHttpClient client = MyHttpClient.getHttpClient();

                            String tmpString = MyHttpClient.doGet(client, quxiaourl, map1, true);
                            Message msg = new Message();
                            msg.what = 0x239;
                            Bundle bundle = new Bundle();
                            bundle.putString("quxiaoguanzhu", tmpString);
                            msg.setData(bundle);
                            handler.sendMessage(msg);
                        }
                    }).start();
                }
            }
        });

        mAdapter = new ImageLcAdapter(this, mDataList);
        mGridView.setAdapter(mAdapter);


        mGridView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (position == getDataSize()) {
                    new PopupWindows(SelfActivity.this, mGridView);
                } else {
                    Intent intent = new Intent(SelfActivity.this,
                            ImageZoom.class);
                    intent.putExtra(IntentConstants.EXTRA_IMAGE_LIST,
                            (Serializable) mDataList);
                    intent.putExtra(IntentConstants.EXTRA_CURRENT_IMG_POSITION, position);
                    startActivityForResult(intent, 1);

                }
            }
        });
    }




    /***
     * 初始化控件，得到布局中的控件
     */
    void initActive() {
        mUserName=(TextView) findViewById(R.id.userName);
        JianLi = (TextView) findViewById(R.id.jianli);
        dongTai = (TextView) findViewById(R.id.dongtai);
        photo_wall = (TextView) findViewById(R.id.photo_walls);
        userSex = (ImageView)findViewById(R.id.userSex);
//		mGuanZhu = (ImageView) findViewById(R.id.guanZhu);
//		mGuanZhu.setOnClickListener(this);
        mGuanZhuNum = (TextView) findViewById(R.id.GuanZhuNum);
        mFenShiNum = (TextView) findViewById(R.id.FenShiNum);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
//        JianLi.setTextColor(Color.parseColor("#808080"));
//        dongTai.setTextColor(Color.parseColor("#FF222222"));
//        photo_wall.setTextColor(Color.parseColor("#808080"));
        transaction.replace(R.id.layout, mArticalFragment);
        transaction.commit();

        getMyGuanZhu(getUserNum);
        MyFenShi(getUserNum);
        getUserInfoHttp(getUserNum);
    }


    /***
     * 个人简介 和 他的动态 按钮监听
     *
     * @param v
     */
    public void TextClick(View v) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        switch (v.getId()) {
            case R.id.jianli:
                JianLi.setTextColor(getResources().getColor(R.color.whites));
                dongTai.setTextColor(getResources().getColor(R.color.blue));
                photo_wall.setTextColor(getResources().getColor(R.color.blue));

                JianLi.setBackgroundResource(R.drawable.midder3);
                dongTai.setBackgroundResource(R.drawable.midder4);
                photo_wall.setBackgroundResource(R.drawable.file_conner);

                buttonLinear.setVisibility(View.GONE);
                transaction.replace(R.id.layout, mUserInfoFragment);
                break;
            case R.id.dongtai:
                JianLi.setTextColor(getResources().getColor(R.color.blue));
                dongTai.setTextColor(getResources().getColor(R.color.whites));
                photo_wall.setTextColor(getResources().getColor(R.color.blue));


                JianLi.setBackgroundResource(R.drawable.midder1);
                dongTai.setBackgroundResource(R.drawable.midder);
                photo_wall.setBackgroundResource(R.drawable.midder2);

                if (!localUserName.equals(getUserNum)) {
                    buttonLinear.setVisibility(View.VISIBLE);
                }
                transaction.replace(R.id.layout, mArticalFragment);
                break;

            case R.id.photo_walls:
                JianLi.setTextColor(getResources().getColor(R.color.blue));
                dongTai.setTextColor(getResources().getColor(R.color.blue));
                photo_wall.setTextColor(getResources().getColor(R.color.whites));

                photo_wall.setBackgroundResource(R.drawable.midder5);
                JianLi.setBackgroundResource(R.drawable.midder6);
                dongTai.setBackgroundResource(R.drawable.midder4);

                buttonLinear.setVisibility(View.GONE);
                transaction.replace(R.id.layout, photoFragment);

                break;
        }
        transaction.commit();
    }




    private void getArticalContent(String pathImg,String userNum) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.setCookieStore(Cookie.cookieStore);
        String url = "http://115.28.35.2:8802/kaoban/changeInf/?userNum=" + userNum;
        RequestParams params = new RequestParams();
        File file = new File(pathImg);

        try {
            params.put("headImg", file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        params.put("tag", "ccc");
        client.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] reponse) {

                if (arg0 == 200) {
                    InputStream xml = new ByteArrayInputStream(reponse);
                    try {
                        String result = SendArtical(xml);
                        if (result.toString().trim().equals("success")) {
                            getUserInfoHttp(getUserNum);

                            Toast.makeText(getApplicationContext(), "上传成功", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "上传失败", Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }


            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
            }
        });


    }
    private String SendArtical(InputStream xml) throws Exception {

        String result = null;
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(xml, "UTF-8");
        int event = parser.getEventType();
        while (event != XmlPullParser.END_DOCUMENT) {
            switch (event) {
                case XmlPullParser.START_DOCUMENT:
                    break;
                case XmlPullParser.START_TAG:
                    if ("result".equals(parser.getName())) {
                        result = parser.nextText();
                    }
                    break;
                case XmlPullParser.END_TAG:
                    break;
            }
            event = parser.next();
        }
        return result;
    }
    private void showDialog(String mess)
    {
        new AlertDialog.Builder(SelfActivity.this).setTitle("Message")
                .setMessage(mess)
                .setNegativeButton("确定",new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                    }
                })
                .show();
    }



    /**
     * 关注结果解析 取消关注解析结果
     *
     * @param xml
     * @return
     * @throws Exception
     */
    private String GuanZhuResult(InputStream xml) throws Exception {

        String result = null;
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(xml, "UTF-8");
        int event = parser.getEventType();
        while (event != XmlPullParser.END_DOCUMENT) {
            switch (event) {
                case XmlPullParser.START_DOCUMENT:
                    break;
                case XmlPullParser.START_TAG:
                    if ("result".equals(parser.getName())) {
                        result = parser.nextText();
                    }
                    break;
                case XmlPullParser.END_TAG:
                    break;
            }
            event = parser.next();
        }
        return result;
    }



    /**
     * 我的关注好友列表
     *
     * @param userNum
     *            用户号码
     */
    private void getMyGuanZhu(String userNum) {

        String url = "http://115.28.35.2:8802/kaoban/lookFollow/?userNum=" + userNum;
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                if (statusCode == 200) {
                    InputStream xml = new ByteArrayInputStream(responseBody);
                    try {
                        guanZhuFriends = getGuanZhuFriends(xml);
                        mGuanZhuNum.setText("" + guanZhuFriends.size());
                        addNewFriends.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String name = getUserNum;
                                String reason = "加个好友呗";
                                new AddPopupWindows(SelfActivity.this,name,reason);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }



    /**
     * 我的关注好友列表解析结果
     *
     * @param xml
     * @return
     * @throws Exception
     */
    private List<MyGuanZhuBean> getGuanZhuFriends(InputStream xml) throws Exception {

        List<MyGuanZhuBean> friends = null;
        MyGuanZhuBean friend = null;
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(xml, "UTF-8");
        int event = parser.getEventType();
        while (event != XmlPullParser.END_DOCUMENT) {
            switch (event) {
                case XmlPullParser.START_DOCUMENT:
                    friends = new ArrayList<MyGuanZhuBean>();
                    break;

                case XmlPullParser.START_TAG:
                    if ("friend".equals(parser.getName())) {
                        friend = new MyGuanZhuBean();
                    } else if ("userNum".equals(parser.getName())) {
                        friend.setUserNum(parser.nextText());
                    } else if ("userName".equals(parser.getName())) {
                        friend.setUsername(parser.nextText());
                    } else if ("headImg ".equals(parser.getName())) {
//					String str = parser.getAttributeValue(0);
//					int index = str.indexOf(",");
//					str = str.substring(index + 1);
//					byte[] decode = Base64.decode(str.toString(), Base64.DEFAULT);
//					Bitmap bitmap = BitmapFactory.decodeByteArray(decode, 0, decode.length);
//					headImg.setImageBitmap(bitmap);
                    }
                    else if ("beFollow".equals(parser.getName())) {
                        friend.setBeFollow(parser.nextText());
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if ("friend".equals(parser.getName())) {
                        friends.add(friend);
                        friend = null;
                    }
                    break;
            }
            event = parser.next();
        }
        return friends;
    }



    /**
     * 粉丝列表
     *
     * @param userNum
     */
    private void MyFenShi(String userNum) {

        String url = "http://115.28.35.2:8802/kaoban/lookBeFollow/?userNum=" + userNum;
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                if (statusCode == 200) {
                    InputStream xml = new ByteArrayInputStream(responseBody);
                    try {
                        List<FenShiBean> feiShiFriends = getFeiShiFriends(xml);
                        mFenShiNum.setText("" + feiShiFriends.size());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }
    /**
     * 得到粉丝列表
     *
     * @param xml
     * @return List<FenShiBean>
     * @throws Exception
     */
    private List<FenShiBean> getFeiShiFriends(InputStream xml) throws Exception {

        List<FenShiBean> friends = null;
        FenShiBean friend = null;
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(xml, "UTF-8");
        int event = parser.getEventType();
        while (event != XmlPullParser.END_DOCUMENT) {
            switch (event) {
                case XmlPullParser.START_DOCUMENT:
                    friends = new ArrayList<FenShiBean>();
                    break;

                case XmlPullParser.START_TAG:
                    if ("friend".equals(parser.getName())) {
                        friend = new FenShiBean();
                    } else if ("userNum".equals(parser.getName())) {
                        friend.setUserNum(parser.nextText());
                    } else if ("userName".equals(parser.getName())) {
                        friend.setUsername(parser.nextText());
                    } else if ("headImg ".equals(parser.getName())) {
//					String str = parser.getAttributeValue(0);
//					int index = str.indexOf(",");
//					str = str.substring(index + 1);
//					byte[] decode = Base64.decode(str.toString(), Base64.DEFAULT);
//					Bitmap bitmap = BitmapFactory.decodeByteArray(decode, 0, decode.length);
//					friend.setHeadImg(bitmap);
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if ("friend".equals(parser.getName())) {
                        friends.add(friend);
                        friend = null;
                    }
                    break;
            }
            event = parser.next();
        }
        return friends;
    }

        /***
     * 得到用户信息
     */
    public void getUserInfoHttp(String userName) {

        String url = "http://115.28.35.2:8802/kaoban/userInformation/?userNum="+userName;
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new AsyncHttpResponseHandler() {

            private Handler myHandler;

            private Bitmap bitmap;

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                if (statusCode == 200) {
                    InputStream xml = new ByteArrayInputStream(responseBody);
                    try {
                        userInf = getUserInf(xml);
                        mUserName.setText(userInf.getName());
                        String sex = userInf.getSex();
                        if(sex.equals("男")){
                            userSex.setBackgroundResource(R.drawable.man);
                        }else{
                            userSex.setBackgroundResource(R.drawable.woman);
                        }

                        String url1 = "http://115.28.35.2:8802"+userInf.getHeadImg();

                        str2 = url1.replaceAll(" ", "");
                        URL url = new URL(str2);
                         Glide.with(getApplicationContext()).load(url).into(headImg);

                        myHandler=new Handler()
                        {
                            public void handleMessage(Message msg)
                            {
                                if(msg.what==0x1122)
                                {
//                                    headImg.setImageBitmap(bitmap);
                                    Drawable drawable = BoxBlurFilter(bitmap);
                                    backgroundLayout.setBackgroundDrawable(drawable);
                                }
                            };
                        };
                        new Thread()
                        {
                            public void run()
                            {

                                try {
                                    String url1 = "http://115.28.35.2:8802"+userInf.getHeadImg();

                                    str2 = url1.replaceAll(" ", "");
                                    URL url = new URL(str2);

                                    InputStream is=url.openStream();
                                    bitmap=BitmapFactory.decodeStream(is);
                                    is.close();
                                } catch (Exception e) {
                                    // TODO 自动生成的 catch 块
                                    e.printStackTrace();
                                }
                                myHandler.sendEmptyMessage(0x1122);

                            };

                        }.start();

                        Bundle bundle=new Bundle();
                        String[] strArray={userInf.getName(),userInf.getBirthday(),
                                userInf.getNumber(),userInf.getNumber(),
                                userInf.getJob(),userInf.getProvince(),
                                userInf.getProvince(),userInf.getSchool()};
                        bundle.putStringArray("USER_INFO", strArray);
                        mUserInfoFragment.setArguments(bundle);
//							mName.setText("姓名\t"+userInf.getName());
//							mBirthday.setText("生日\t"+userInf.getBirthday());
//							mNumber.setText("考号\t"+userInf.getNumber());
//							mJob.setText("职业\t"+userInf.getJob());
//							mProvince.setText("省份\t"+userInf.getProvince());
//							mSchool.setText("学校\t"+userInf.getSchool());

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
//						String str=parser.getAttributeValue(0);
//						int index=str.indexOf(",");
//						str=str.substring(index+1);
//						byte[] decode = Base64.decode(str.toString(), Base64.DEFAULT);
//						Bitmap bitmap = BitmapFactory.decodeByteArray(decode, 0, decode.length);
//						headImg.setImageBitmap(bitmap);
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
    public void saveMyBitmap(Bitmap mBitmap){
        File f = new File(Environment.getExternalStorageDirectory()
                + "/caremasend/", String.valueOf(System.currentTimeMillis())
                + ".jpg");
        if (!f.exists())
        {
            File vDirPath = f.getParentFile();
            vDirPath.mkdirs();
        }
        else
        {
            if (f.exists())
            {
                f.delete();
            }
        }
        caremapathsend = f.getPath();
        try {
            f.createNewFile();
        } catch (IOException e) {
            // TODO Auto-generated catch block
//            DebugMessage.put("在保存图片时出错："+e.toString());
        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 1) {

            finish();
            return true;
        } else
            return super.onKeyDown(keyCode, event);
    }


    /** 水平方向模糊度 */
    private static float hRadius = 10;
    /** 竖直方向模糊度 */
    private static float vRadius = 10;
    /** 模糊迭代度 */
    private static int iterations = 7;


    public static Drawable BoxBlurFilter(Bitmap bmp) {
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        int[] inPixels = new int[width * height];
        int[] outPixels = new int[width * height];
        Bitmap bitmap = Bitmap.createBitmap(width, height,Bitmap.Config.ARGB_8888);
        bmp.getPixels(inPixels, 0, width, 0, 0, width, height);
        for (int i = 0; i < iterations; i++) {
            blur(inPixels, outPixels, width, height, hRadius);
            blur(outPixels, inPixels, height, width, vRadius);
        }
        blurFractional(inPixels, outPixels, width, height, hRadius);
        blurFractional(outPixels, inPixels, height, width, vRadius);
        bitmap.setPixels(inPixels, 0, width, 0, 0, width, height);
        Drawable drawable = new BitmapDrawable(bitmap);
        return drawable;
    }

    public static void blur(int[] in, int[] out, int width, int height,
                            float radius) {
        int widthMinus1 = width - 1;
        int r = (int) radius;
        int tableSize = 2 * r + 1;
        int divide[] = new int[256 * tableSize];

        for (int i = 0; i < 256 * tableSize; i++)
            divide[i] = i / tableSize;

        int inIndex = 0;

        for (int y = 0; y < height; y++) {
            int outIndex = y;
            int ta = 0, tr = 0, tg = 0, tb = 0;

            for (int i = -r; i <= r; i++) {
                int rgb = in[inIndex + clamp(i, 0, width - 1)];
                ta += (rgb >> 24) & 0xff;
                tr += (rgb >> 16) & 0xff;
                tg += (rgb >> 8) & 0xff;
                tb += rgb & 0xff;
            }

            for (int x = 0; x < width; x++) {
                out[outIndex] = (divide[ta] << 24) | (divide[tr] << 16)
                        | (divide[tg] << 8) | divide[tb];

                int i1 = x + r + 1;
                if (i1 > widthMinus1)
                    i1 = widthMinus1;
                int i2 = x - r;
                if (i2 < 0)
                    i2 = 0;
                int rgb1 = in[inIndex + i1];
                int rgb2 = in[inIndex + i2];

                ta += ((rgb1 >> 24) & 0xff) - ((rgb2 >> 24) & 0xff);
                tr += ((rgb1 & 0xff0000) - (rgb2 & 0xff0000)) >> 16;
                tg += ((rgb1 & 0xff00) - (rgb2 & 0xff00)) >> 8;
                tb += (rgb1 & 0xff) - (rgb2 & 0xff);
                outIndex += height;
            }
            inIndex += width;
        }
    }

    public static void blurFractional(int[] in, int[] out, int width,
                                      int height, float radius) {
        radius -= (int) radius;
        float f = 1.0f / (1 + 2 * radius);
        int inIndex = 0;

        for (int y = 0; y < height; y++) {
            int outIndex = y;

            out[outIndex] = in[0];
            outIndex += height;
            for (int x = 1; x < width - 1; x++) {
                int i = inIndex + x;
                int rgb1 = in[i - 1];
                int rgb2 = in[i];
                int rgb3 = in[i + 1];

                int a1 = (rgb1 >> 24) & 0xff;
                int r1 = (rgb1 >> 16) & 0xff;
                int g1 = (rgb1 >> 8) & 0xff;
                int b1 = rgb1 & 0xff;
                int a2 = (rgb2 >> 24) & 0xff;
                int r2 = (rgb2 >> 16) & 0xff;
                int g2 = (rgb2 >> 8) & 0xff;
                int b2 = rgb2 & 0xff;
                int a3 = (rgb3 >> 24) & 0xff;
                int r3 = (rgb3 >> 16) & 0xff;
                int g3 = (rgb3 >> 8) & 0xff;
                int b3 = rgb3 & 0xff;
                a1 = a2 + (int) ((a1 + a3) * radius);
                r1 = r2 + (int) ((r1 + r3) * radius);
                g1 = g2 + (int) ((g1 + g3) * radius);
                b1 = b2 + (int) ((b1 + b3) * radius);
                a1 *= f;
                r1 *= f;
                g1 *= f;
                b1 *= f;
                out[outIndex] = (a1 << 24) | (r1 << 16) | (g1 << 8) | b1;
                outIndex += height;
            }
            out[outIndex] = in[width - 1];
            inIndex += width;
        }
    }

    public static int clamp(int x, int a, int b) {
        return (x < a) ? a : (x > b) ? b : x;
    }
}
