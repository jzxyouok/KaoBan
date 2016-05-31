package com.dezhou.lsy.projectdezhoureal;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.sso.UMSsoHandler;
import com.zhy.utils.ImageLoader;

import org.apache.http.NameValuePair;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.lang.ref.SoftReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bean.ArticalBean;
import bean.Topic;
import kaobanxml.XmlParser;
import kongjian.AlertDialogs;
import kongjian.SelfActivity;
import network.MyHttpClient;
import tempvalue.MyArtical;
import tempvalue.UserNum;
import utils.URLSet;
import view.view.XListView;


/*
发现三级界面----帖子列表
 */
public class ArticalListActivity extends FragmentActivity implements XListView.IXListViewListener {
    XListView list;
    ImageButton backButton;
    Topic topic;
    List<ArticalBean> articals=new ArrayList<ArticalBean>();
    int page = 1;
    Bitmap bitmap;
    ProgressDialog pro;
    private int width;
    private ImageLoader mImageLoader;
    List<Uri> bitmapURIs;
    List<String> imgeStrings;
    Handler mHandler = new Handler();
    public static boolean real=false;
    public static int flag=0;
    private static List<NameValuePair> listArtical;
    int ADDNEWREQUEST=4;
    int ADDNEWRESULT=5;
    private final static int SECONDS = 1;
    private final static int MINUTES = 60 * SECONDS;
    private final static int HOURS   = 60 * MINUTES;
    private final static int DAYS    = 24 * HOURS;
    private final static int WEEKS   =  7 * DAYS;
    private final static int MONTHS  =  4 * WEEKS;
    private final static int YEARS   = 12 * MONTHS;

    final UMSocialService mController = (UMSocialService) UMServiceFactory.getUMSocialService("com.umeng.share");
    private static Map<String, String> aticalSecondFlag = new HashMap<String, String>();
    public Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x121:
                    onLoad();
                    Bundle bundle = new Bundle();
                    bundle = msg.getData();
                    String topicsXml = bundle.getString("simpleString");
                    if (topicsXml.equals("")) {
                        break;
                    } else {
                        List<ArticalBean> changeArticals=XmlParser.xmlArticalsGet(topicsXml);
                        for(int i=0;i<changeArticals.size();i++){
                            articals.add(changeArticals.get(i));
                        }
                        ArticalsListAdapter adapter = new ArticalsListAdapter(ArticalListActivity.this, articals);
                        list.setAdapter(adapter);
                        if(real) {
//                            list.setSelection(articals.size() - changeArticals.size());
                            list.setSelection(flag);
                            real=false;
                        }
                        else{
                            list.setSelection(articals.size()-changeArticals.size());
                        }
                        list.setXListViewListener(ArticalListActivity.this);

                    }
                    break;
                case 0x254:
                    Bundle bundle1=new Bundle();
                    bundle1=msg.getData();
                    String deleteResultXML=bundle1.getString("deleteResult");
                    if(deleteResultXML==null){
                        break;
                    }
                    else{
                        String result=XmlParser.xmlResultGet(deleteResultXML);
                        if(result.equals("success")){
                            articals=new ArrayList<ArticalBean>();
                            initNetWork();
                        }else{
                            Toast.makeText(ArticalListActivity.this,"删除失败",Toast.LENGTH_SHORT).show();
                        }
                    }

            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artical_list);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }

        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.blue);
        Intent intent = getIntent();
        topic = (Topic) intent.getSerializableExtra("topic");
        bitmapURIs = new ArrayList<Uri>();
        imgeStrings = new ArrayList<String>();
        initView();
        onRefresh();
    }

    public void initView() {
        ImageView addNew = (ImageView) findViewById(R.id.artical_list_add);
        list = (XListView) findViewById(R.id.list_artical);
        list.setPullLoadEnable(true);
        ImageView back = (ImageView) findViewById(R.id.artical_list_back);
        TextView title = (TextView) findViewById(R.id.artical_title);
        back.setOnClickListener(new BtClickListener());

        addNew.setOnClickListener(new BtClickListener());

        title.setText(topic.getTopicName());
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(ArticalListActivity.this, ArticalInfoActivity.class);
                intent.putExtra("artical", articals.get(i - 1));
                startActivityForResult(intent, 1);
            }
        });
    }
    public void deleteThread(final String url,final Map<String,String> map,final int msgWhat){

        new Thread(new Runnable() {
            @Override
            public void run() {
                DefaultHttpClient client = MyHttpClient.getHttpClient();
                String result = MyHttpClient.doGet(client, url, map, true);
                Message message=new Message();
                message.what=msgWhat;
                Bundle bundle=new Bundle();
                if(result==null)
                    result="";
                bundle.putString("deleteResult",result);
                message.setData(bundle);
                handler.sendMessage(message);
            }
        }).start();

    }
    public class BtClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {

                case R.id.artical_list_back:
                    finish();
                    break;

                case R.id.artical_list_add:
                    if(topic.getTopicName().equals("机密资料")){
                       /* Intent intent2 = new Intent(ArticalListActivity.this, AddNewInfo.class);
                        intent2.putExtra("topic", topic);
                        startActivityForResult(intent2, 1);*/
                        Toast.makeText(ArticalListActivity.this,"上传资料待开发",Toast.LENGTH_SHORT).show();
                    }else {
                        Intent intent2 = new Intent(ArticalListActivity.this, AddNewArticalActivity.class);
                        intent2.putExtra("topic", topic);
                        startActivityForResult(intent2, ADDNEWREQUEST);
                    }
                    break;
            }
        }
    }


    public void initNetWork() {

        aticalSecondFlag.put("Name", UserNum.userNum);
        aticalSecondFlag.put("topic", topic.getTopicNum());
        aticalSecondFlag.put("page", page + "");
        netWorkThread(URLSet.getArticalListURL, aticalSecondFlag, 0x121);
    }

    private void netWorkThread(final String url, final Map<String, String> mapTmp, final int msgWhat) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DefaultHttpClient client = MyHttpClient.getHttpClient();
                String aticalSecond = MyHttpClient.doGet(client, url, mapTmp, true);
                Message message = new Message();
                message.what = msgWhat;
                Bundle bundle = new Bundle();
                if(aticalSecond==null)
                    aticalSecond="";
                bundle.putString("simpleString", aticalSecond);
                message.setData(bundle);
                handler.sendMessage(message);

            }
        }).start();
    }

    @Override
    public void onRefresh() {

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                page = 1;
                articals.clear();
                articals=new ArrayList<ArticalBean>();
                initNetWork();

            }
        }, 1000);


    }

    @Override
    public void onLoadMore() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                page++;
                initNetWork();

            }
        }, 1000);
    }

    private void onLoad() {
        list.stopRefresh();
        list.stopLoadMore();
        list.setRefreshTime("刚刚");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode==ADDNEWREQUEST&&resultCode==ADDNEWRESULT){
            initNetWork();
        }
        UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode);
        if (ssoHandler != null) {
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    public class ArticalsListAdapter extends BaseAdapter {

        private LayoutInflater inflater;
        private List<ArticalBean> list;
        private Context mContext;
        private HashMap<String, SoftReference<Bitmap>> imageCache = null;

        public final UMSocialService mController = (UMSocialService) UMServiceFactory.getUMSocialService("com.umeng.share");
        //    UMSocialService mController=UMServiceFactory.getUMSocialService("com.umeng.share");
        List<NameValuePair> tmpList;
        BitmapFactory.Options option = new BitmapFactory.Options();
        Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0x124:
                        Bundle bunle = new Bundle();
                        bunle = msg.getData();
                        String resultXml = bunle.getString("likeResult");
                        flag=bunle.getInt("flag");
                        real=true;
                        String result = XmlParser.xmlResultGet(resultXml);
                        if (result.equals("success")) {
                            Toast.makeText(mContext, "已赞", Toast.LENGTH_SHORT).show();
                            articals=new ArrayList<ArticalBean>();
                            initNetWork();
                        } else {
                            Toast.makeText(mContext, "赞失败", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 0x125:
                        Bundle bundle = new Bundle();
                        bundle = msg.getData();
                        String storeResultXml = bundle.getString("storeResult");
                        flag=bundle.getInt("flag");
                        real=true;
                        String storeResult = XmlParser.xmlResultGet(storeResultXml);
                        if (storeResult.equals("success")) {
                            Toast.makeText(mContext, "收藏成功", Toast.LENGTH_SHORT).show();
                            articals=new ArrayList<ArticalBean>();
                            initNetWork();
                        } else {
                            Toast.makeText(mContext, "收藏失败", Toast.LENGTH_SHORT).show();
                        }
                        break;


                    case 0x126:
                        Bundle bundle2 = new Bundle();
                        bundle2 = msg.getData();
                        String resultXml2 = bundle2.getString("likeResult");
                        flag=bundle2.getInt("flag");
                        real=true;
                        String result2 = XmlParser.xmlResultGet(resultXml2);
                        if (result2.equals("success")) {
                            Toast.makeText(mContext, "取消赞", Toast.LENGTH_SHORT).show();
                            articals=new ArrayList<ArticalBean>();
                            initNetWork();
                        } else {
                            Toast.makeText(mContext, "赞失赞失败，请重试", Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
            }
        };

        public ArticalsListAdapter(Context context, List<ArticalBean> list) {
            this.mContext = context;
            inflater = LayoutInflater.from(context);
            this.list = list;
            bitmap = null;
            option.inSampleSize = 4;
            WindowManager wm = (WindowManager) mContext

                    .getSystemService(Context.WINDOW_SERVICE);



            width = wm.getDefaultDisplay().getWidth();
        }

        public void updateView(List<ArticalBean> list) {

            this.list = list;
            notifyDataSetChanged();
        }


        @Override
        public int getCount() {

            return list.size();
        }


        @Override
        public Object getItem(int position) {

            return list.get(position);
        }


        @Override
        public long getItemId(int position) {

            return position;
        }


        @Override
        public View getView(final int i, View view, ViewGroup parent) {
            final ViewHolder viewHolder = new ViewHolder();
            view = inflater.inflate(R.layout.artical_list_item, null);
            viewHolder.deleteArtical=(TextView)view.findViewById(R.id.delete_artical);
            viewHolder.headImg = (ImageView) view.findViewById(R.id.artical_list_user_img);
            viewHolder.likelayout = (LinearLayout) view.findViewById(R.id.artical_list_like_layout);
            viewHolder.storelayout = (LinearLayout) view.findViewById(R.id.artical_list_store_layout);
            viewHolder.pinglunlayout = (LinearLayout) view.findViewById(R.id.artical_list_pinglun_layout);
            //  viewHolder.ifImg=(ImageView)view.findViewById(R.id.artical_list_item_if_img);
            viewHolder.like = (ImageView) view.findViewById(R.id.artical_list_like_img);
            viewHolder.pinglun = (ImageView) view.findViewById(R.id.artical_list_pinglun_img);
            viewHolder.imgsGrid = (GridView) view.findViewById(R.id.articalfragment_info_top_imgs);
            viewHolder.shoucangImg = (ImageView) view.findViewById(R.id.artical_list_shoucang_img);
            viewHolder.userName = (TextView) view.findViewById(R.id.user_name);
            viewHolder.time = (TextView) view.findViewById(R.id.artical_time);
            //viewHolder.sex=(ImageView)view.findViewById(R.id.artical_list_sex);
            viewHolder.text = (TextView) view.findViewById(R.id.text);
            viewHolder.likeNum = (TextView) view.findViewById(R.id.artical_list_like_num);
            viewHolder.pinglunNum = (TextView) view.findViewById(R.id.artical_list_pinglun_num);
            viewHolder.shoucangNum = (TextView) view.findViewById(R.id.artical_list_shoucang_num);
            viewHolder.school = (TextView) view.findViewById(R.id.artical_list_school);
            viewHolder.position=(TextView)view.findViewById(R.id.location_artical);
            viewHolder.location=(LinearLayout)view.findViewById(R.id.location_linear);
            viewHolder.likelayout.setOnClickListener(new btClickListener(i));
            viewHolder.storelayout.setOnClickListener(new btClickListener(i));

            viewHolder.pinglunlayout.setOnClickListener(new btClickListener(i));
//            bitmap = null;
//            bitmap = loadBitmap(list.get(i).getImgHeader());
//            if (bitmap != null) {
//                viewHolder.headImg.setImageBitmap(bitmap);
//            } else {
//                LoadBitmapAsyn loadBitmapAsyn = new LoadBitmapAsyn(viewHolder.headImg);
//                loadBitmapAsyn.execute(list.get(i).getImgHeader());
//            }
            Glide.with(getApplicationContext()).load(list.get(i).getImgHeader()).into(viewHolder.headImg);

            if (!list.get(i).getImg().equals("")) {
                final String imagePath = list.get(i).getSmimg();
                final String imgPath = list.get(i).getImg();
                final String[] imagePaths = imagePath.split(";");
                final String[] imgPaths = imgPath.split(";");
                List<String> imagePathlist = new ArrayList<String>();
                for (int m = 0; m < imagePaths.length; m++) {
//                    String imgpath = "http://115.28.35.2:8801" + imagePaths[m];
                    String imgpath = URLSet.serviceUrl + imagePaths[m];
                    imagePathlist.add(imgpath);
                }

                mImageLoader = ImageLoader.getInstance(3, ImageLoader.Type.LIFO);
                if (imagePath != null) {
                    viewHolder.imgsGrid.setAdapter(new ListImgItemAdaper(getApplicationContext(), 0,
                            imagePathlist));
                } else {
                    viewHolder.imgsGrid.setAdapter(null);
                }

                viewHolder.imgsGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(mContext, PhotoActivity.class);

                        intent.putExtra("imgPaths", imgPaths);
                        intent.putExtra("imgflag", position);
                        startActivity(intent);
                    }
                });
            }
            if (list.get(i).getPostion().equals("")){
                viewHolder.location.setVisibility(View.GONE);
            }else {
                viewHolder.position.setText(list.get(i).getPostion());
            }

            viewHolder.userName.setText(list.get(i).getUserName());

            try {
                Date date= ConverToDate(list.get(i).getArticalTime().toString().trim());
                String timeago=getTimeAgo(getApplicationContext(),date);
                viewHolder.time.setText(timeago);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (list.get(i).getUserNum().equals(UserNum.userNum)){
                viewHolder.deleteArtical.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new AlertDialogs(ArticalListActivity.this).builder().setTitle("确定删除吗?")
                                .setPositiveButton("删除", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Map<String, String> map = new HashMap<String, String>();
                                        map.put("articalNum", list.get(i).getArticalsNum());
                                        deleteThread(URLSet.deleteArtical, map, 0x254);

                                    }
                                }).setNegativeButton("取消", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        }).show();
                    }
                });
            }
            else {
                viewHolder.deleteArtical.setVisibility(View.GONE);
            }



            if (list.get(i).getArticalcontent().length() > 200) {
                viewHolder.text.setText(list.get(i).getArticalcontent().substring(0, 200) + "...");
            } else {
                viewHolder.text.setText(list.get(i).getArticalcontent());
            }
            viewHolder.headImg.setOnClickListener(new btClickListener(i));
//        viewHolder.userImg.setImageResource(R.drawable.headimg);

            if (list.get(i).getLikeNum().equals("0")){
                viewHolder.likeNum.setText("点赞");
            }
            else {
                viewHolder.likeNum.setText(list.get(i).getLikeNum());
            }
            if (list.get(i).getStoreNum().equals("0")){
                viewHolder.shoucangNum.setText("收藏");
            }
            else {
                viewHolder.shoucangNum.setText(list.get(i).getStoreNum());
            }

            if (list.get(i).getCommendNum().equals("0")){
                viewHolder.pinglunNum.setText("评论");
            }
            else {
                viewHolder.pinglunNum.setText(list.get(i).getCommendNum());
            }

            viewHolder.school.setText(list.get(i).getUserSchool());
            if (list.get(i).getIfLike().equals("true"))
                viewHolder.like.setImageResource(R.drawable.like);
            else {
                viewHolder.like.setImageResource(R.drawable.not_like);
            }

//            if(list.get(i).getUserSex().equals("woman")){
//                viewHolder.sex.setImageResource(R.drawable.woman);
//
//            }
//            else{
//                viewHolder.sex.setImageResource(R.drawable.man);
//            }
            /*
            是否收藏
             */
            if (list.get(i).getIfStore().equals("true")) {
                viewHolder.shoucangImg.setImageResource(R.drawable.store);
            } else {
                viewHolder.shoucangImg.setImageResource(R.drawable.not_store);
            }
            return view;
        }

        public class btClickListener implements View.OnClickListener {
            int i = 0;

            public btClickListener(int i) {
                this.i = i;
            }

            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.artical_list_like_layout:
                        if (articals.get(i).getIfLike().equals("true")) {
                            //取消赞
                            NameValuePair np = new BasicNameValuePair("articalNum", list.get(i).getArticalsNum());
                            tmpList = new ArrayList<NameValuePair>();
                            tmpList.add(np);
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    DefaultHttpClient client = MyHttpClient.getHttpClient();

                                    String tmpString = MyHttpClient.doPost(client, URLSet.removeLike, tmpList, true);
                                    Message msg = new Message();
                                    msg.what = 0x126;
                                    Bundle bundle = new Bundle();
                                    bundle.putString("likeResult", tmpString);
                                    bundle.putInt("flag",i);
                                    msg.setData(bundle);
                                    handler.sendMessage(msg);
                                }
                            }).start();


                        } else {
                            NameValuePair np = new BasicNameValuePair("articalNum", list.get(i).getArticalsNum());
                            tmpList = new ArrayList<NameValuePair>();
                            tmpList.add(np);
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    DefaultHttpClient client = MyHttpClient.getHttpClient();

                                    String tmpString = MyHttpClient.doPost(client, URLSet.articalLikeURL, tmpList, true);
                                    Message msg = new Message();
                                    msg.what = 0x124;
                                    Bundle bundle = new Bundle();
                                    bundle.putString("likeResult", tmpString);
                                    bundle.putInt("flag",i);
                                    msg.setData(bundle);
                                    handler.sendMessage(msg);
                                }
                            }).start();

                        }
                        break;
                    case R.id.artical_list_store_layout:
                        if (articals.get(i).getIfStore().equals("true")) {
                            Toast.makeText(ArticalListActivity.this, "取消收藏，待开发", Toast.LENGTH_SHORT).show();
                        } else {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Map<String, String> mapTmp = new HashMap<String, String>();
                                    mapTmp.put("articalNum", list.get(i).getArticalsNum());
                                    DefaultHttpClient client = MyHttpClient.getHttpClient();
                                    String simpleString = MyHttpClient.doGet(client, URLSet.articalStoreURL, mapTmp, true);
                                    Message message = new Message();
                                    message.what = 0x125;
                                    Bundle bundle = new Bundle();
                                    bundle.putString("storeResult", simpleString);
                                    bundle.putInt("flag",i);
                                    message.setData(bundle);
                                    handler.sendMessage(message);
                                }
                            }).start();
                        }
                        break;

                    case R.id.artical_list_pinglun_layout:
                        Intent intent = new Intent(mContext, PinglunAddActivity.class);
                        intent.putExtra("artical", list.get(i));
                        intent.putExtra("flag", "1");//评论评论
                        startActivityForResult(intent, 1);

                        break;
                    case R.id.artical_list_user_img:
                        Intent intent2 = new Intent(mContext, SelfActivity.class);
                        intent2.putExtra("userNum", articals.get(i).getUserNum());
                        startActivityForResult(intent2, 1);
                }
            }
        }


        public class ViewHolder {
            public LinearLayout location;
            public GridView imgsGrid;
            public ImageView like, pinglun, shoucangImg, ifImg,sex;
            public TextView pinglunNum, likeNum, userName, time, text, shoucangNum, school,position,deleteArtical;
            public ImageView headImg;
            public LinearLayout likelayout, storelayout, pinglunlayout;
        }
    }

    private class ListImgItemAdaper extends ArrayAdapter<String> {

        public ListImgItemAdaper(Context context, int resource, List<String> datas) {
            super(getApplicationContext(), 0, datas);

        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(
                        R.layout.item_fragment_list_imgs, parent, false);
            }
            ImageView imageview = (ImageView) convertView
                    .findViewById(R.id.id_img);
            imageview.setImageResource(R.drawable.pictures_no);
            //mImageLoader.loadImage(getItem(position), imageview, true);
            Glide.with(getApplicationContext()).load(getItem(position)).into(imageview);
            return convertView;
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

    @Override
    protected void onResume(){
        super.onResume();
        if (MyArtical.fresh){
            onRefresh();
        }
    }

    public static String getTimeAgo(Context context, Date date) {
        int beforeSeconds = (int) (date.getTime() / 1000);
        int nowSeconds = (int) (Calendar.getInstance().getTimeInMillis() / 1000);
        int timeDifference = nowSeconds - beforeSeconds;

        Resources res = context.getResources();

        if (timeDifference < MINUTES) {
            return res.getQuantityString(R.plurals.fuzzydatetime__seconds_ago, timeDifference, timeDifference);
        } else if (timeDifference < HOURS) {
            return res.getQuantityString(R.plurals.fuzzydatetime__minutes_ago, timeDifference / MINUTES, timeDifference / MINUTES);
        } else if (timeDifference < DAYS) {
            return res.getQuantityString(R.plurals.fuzzydatetime__hours_ago, timeDifference / HOURS, timeDifference / HOURS);
        } else if (timeDifference < WEEKS) {
            return res.getQuantityString(R.plurals.fuzzydatetime__days_ago, timeDifference / DAYS, timeDifference / DAYS);
        } else if (timeDifference < MONTHS) {
            return res.getQuantityString(R.plurals.fuzzydatetime__weeks_ago, timeDifference / WEEKS, timeDifference / WEEKS);
        } else if (timeDifference < YEARS) {
            return res.getQuantityString(R.plurals.fuzzydatetime__months_ago, timeDifference / MONTHS, timeDifference / MONTHS);
        } else {
            return res.getQuantityString(R.plurals.fuzzydatetime__years_ago, timeDifference / YEARS, timeDifference / YEARS);
        }

    }


    public static Date ConverToDate(String strDate) throws Exception
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd/ HH:mm");
        return sdf.parse(strDate);
    }


}
