package com.dezhou.lsy.projectdezhoureal;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.zhy.utils.ImageLoader;

import org.apache.http.NameValuePair;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bean.ArticalBean;
import bean.Comment;
import kaobanxml.XmlParser;
import network.MyHttpClient;
import roundimgview.RoundImgView;
import tempvalue.IfTest;
import tempvalue.UserNum;
import utils.URLSet;
import view.view.NoScrollGridView;


public class ArticalInfoActivity extends Activity {
    private static Map<String, String> simpleFlag = new HashMap<String, String>();
    ArticalBean artical;
    ListView list;
    List<Comment> comments;
    public Bitmap bitmap;
    List<NameValuePair> valuePairs;
    ImageView shareImg, commentImg, storeImg, likeImg;
    BitmapFactory.Options option = new BitmapFactory.Options();
    LinearLayout sharelayout,pinglunlayout,storelayout,likelayout;
    private ImageLoader mImageLoader;
    final UMSocialService mController = (UMSocialService) UMServiceFactory.getUMSocialService("com.umeng.share");
    Handler handler = new Handler() {
        /**
         * what:
         * 1: ��������ˢ��
         * 2: ������������
         * @param msg
         */

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {

                case 0x121:
                    Bundle bundle = msg.getData();
                    String aticalCommentsXml = bundle.getString("articalComments");
                    comments = new ArrayList<Comment>();
                    comments = XmlParser.xmlAticalCommentsGet(aticalCommentsXml);
                    ArticalInfoAdapter adapter = new ArticalInfoAdapter(ArticalInfoActivity.this, artical, comments);
                    list.setAdapter(adapter);
                    break;


                case 0x124:
                    Bundle bunle2 = new Bundle();
                    bunle2 = msg.getData();
                    String resultXml = bunle2.getString("likeResult");
                    String result = XmlParser.xmlResultGet(resultXml);
                    if (result.equals("success")) {
                        Toast.makeText(ArticalInfoActivity.this, "已赞", Toast.LENGTH_SHORT).show();
                        artical.setIfLike("true");
                        likeImg.setImageResource(R.drawable.like);

                    } else {
                        Toast.makeText(ArticalInfoActivity.this, "赞失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 0x125:
                    Bundle bundle3 = new Bundle();
                    bundle3 = msg.getData();
                    String storeResultXml = bundle3.getString("storeResult");
                    String storeResult = XmlParser.xmlResultGet(storeResultXml);
                    if (storeResult.equals("success")) {

                        storeImg.setImageResource(R.drawable.store);

                    } else {
                        Toast.makeText(ArticalInfoActivity.this, "收藏失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 0x126:
                    Bundle bunle3 = new Bundle();
                    bunle3 = msg.getData();
                    String resultXml3 = bunle3.getString("likeResult");
                    String result3 = XmlParser.xmlResultGet(resultXml3);
                    if (result3.equals("success")) {
                        artical.setIfLike("false");
                        likeImg.setImageResource(R.drawable.not_like);

                    } else {
                        Toast.makeText(ArticalInfoActivity.this, "取消赞失败，请重试", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }

        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.blue);
        Intent intent = getIntent();
        artical = (ArticalBean) intent.getSerializableExtra("artical");
        initActionBar();
        initView();
        init();

    }

    private void initActionBar() {
        ActionBar actionBar = this.getActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.activity_info_actionbar);

        ImageView backImg = (ImageView) actionBar.getCustomView().findViewById(R.id.activity_info_actionbar_back);
        backImg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ArticalInfoActivity.this.finish();
            }
        });
    }


    public void initView() {
        list = (ListView) findViewById(R.id.artical_comment_list);
        shareImg = (ImageView) findViewById(R.id.artical_info_share);
        commentImg = (ImageView) findViewById(R.id.artical_info_comment);
        storeImg = (ImageView) findViewById(R.id.artical_info_store);
        likeImg = (ImageView) findViewById(R.id.artical_info_like);
        sharelayout=(LinearLayout)findViewById(R.id.artical_info_share_layout);
        pinglunlayout=(LinearLayout)findViewById(R.id.artical_info_pinglun_layout);
        storelayout=(LinearLayout)findViewById(R.id.artical_info_store_layout);
        likelayout=(LinearLayout)findViewById(R.id.artical_info_like_layout);
        if (artical.getIfLike().equals("true")) {

            likeImg.setImageResource(R.drawable.like);
        } else {
            likeImg.setImageResource(R.drawable.not_like);
        }
        if (artical.getIfStore().equals("true")) {
            storeImg.setImageResource(R.drawable.store);
        } else {
            storeImg.setImageResource(R.drawable.not_store);
        }
        sharelayout.setOnClickListener(new BottomClick());
        pinglunlayout.setOnClickListener(new BottomClick());
        storelayout.setOnClickListener(new BottomClick());
        likelayout.setOnClickListener(new BottomClick());
    }

    public class BottomClick implements OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.artical_info_share_layout:
                    share(artical.getArticalcontent(), artical.getImg());
                    break;
                case R.id.artical_info_pinglun_layout:
                    Intent intent = new Intent(ArticalInfoActivity.this, PinglunAddActivity.class);
                    intent.putExtra("artical", artical);
                    intent.putExtra("flag", "1");

                    startActivityForResult(intent, 1);
                    break;
                case R.id.artical_info_store_layout:

                    if (artical.getIfStore().equals("true")) {

                       /* new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Map<String, String> mapTmp = new HashMap<String, String>();
                                mapTmp.put("articalNum", artical.getArticalsNum());
                                DefaultHttpClient client = MyHttpClient.getHttpClient();
                                String simpleString = MyHttpClient.doGet(client, URLSet.removeLike, mapTmp, true);
                                Message message = new Message();
                                message.what = 0x126;
                                Bundle bundle = new Bundle();
                                bundle.putString("storeResult", simpleString);
                                message.setData(bundle);
                                handler.sendMessage(message);
                            }
                        }).start();*/
                    } else {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Map<String, String> mapTmp = new HashMap<String, String>();
                                mapTmp.put("articalNum", artical.getArticalsNum());
                                DefaultHttpClient client = MyHttpClient.getHttpClient();
                                String simpleString = MyHttpClient.doGet(client, URLSet.articalStoreURL, mapTmp, true);
                                Message message = new Message();
                                message.what = 0x125;
                                Bundle bundle = new Bundle();
                                bundle.putString("storeResult", simpleString);
                                message.setData(bundle);
                                handler.sendMessage(message);
                            }
                        }).start();
                    }


                    break;
                case R.id.artical_info_like_layout:

                    if (artical.getIfLike().equals("true")) {
//                        Toast.makeText(ArticalInfoActivity.this,"取消赞，待开发",Toast.LENGTH_SHORT).show();
                        //取消赞
                        NameValuePair np = new BasicNameValuePair("articalNum", artical.getArticalsNum());
                        valuePairs = new ArrayList<NameValuePair>();
                        valuePairs.add(np);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                DefaultHttpClient client = MyHttpClient.getHttpClient();

                                String tmpString = MyHttpClient.doPost(client, URLSet.removeLike, valuePairs, true);
                                Message msg = new Message();
                                msg.what = 0x126;
                                Bundle bundle = new Bundle();
                                bundle.putString("likeResult", tmpString);
                                msg.setData(bundle);
                                handler.sendMessage(msg);
                            }
                        }).start();

                    } else {
                        NameValuePair np = new BasicNameValuePair("articalNum", artical.getArticalsNum());
                        valuePairs = new ArrayList<NameValuePair>();
                        valuePairs.add(np);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                DefaultHttpClient client = MyHttpClient.getHttpClient();

                                String tmpString = MyHttpClient.doPost(client, URLSet.articalLikeURL, valuePairs, true);
                                Message msg = new Message();
                                msg.what = 0x124;
                                Bundle bundle = new Bundle();
                                bundle.putString("likeResult", tmpString);
                                msg.setData(bundle);
                                handler.sendMessage(msg);
                            }
                        }).start();
                    }
                    break;
            }
        }
    }


    public void init() {


        if (UserNum.userNum != null) {
            simpleFlag.put("articalNum", artical.getArticalsNum());

        } else {

        }
        netWorkThread(URLSet.getArticalInfo, simpleFlag, 0x121);

    }

    private void netWorkThread(final String url, final Map<String, String> mapTmp, final int msgWhat) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DefaultHttpClient client = MyHttpClient.getHttpClient();
                String simpleString = MyHttpClient.doGet(client, url, mapTmp, true);
                Message message = new Message();
                message.what = msgWhat;
                Bundle bundle = new Bundle();
                bundle.putString("articalComments", simpleString);
                message.setData(bundle);
                handler.sendMessage(message);
            }
        }).start();
    }

    public void refresh() {
        if (UserNum.userNum != null) {
            simpleFlag.put("articalNum", artical.getArticalsNum());

        } else {

        }
        netWorkThread(URLSet.getArticalInfo, simpleFlag, 0x121);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1 || resultCode == 2) {

            init();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * 分销给好友
     */
    private void share(String text, String img) {

        // 设置分享内容
        mController.setShareContent(text);
        // 设置分享图片, 参数2为图片的url地址
        mController.setShareMedia(new UMImage(ArticalInfoActivity.this, img));


        // 添加微信平台
        UMWXHandler wxHandler = new UMWXHandler(ArticalInfoActivity.this, URLSet.APP_ID_WX, URLSet.APP_KEY_WX);
        wxHandler.addToSocialSDK();
        // 添加微信朋友圈
        UMWXHandler wxCircleHandler = new UMWXHandler(ArticalInfoActivity.this, URLSet.APP_ID_WX, URLSet.APP_KEY_WX);
        wxCircleHandler.setToCircle(true);
        wxCircleHandler.addToSocialSDK();

        //参数1为当前Activity，参数2为开发者在QQ互联申请的APP ID，参数3为开发者在QQ互联申请的APP kEY.
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(ArticalInfoActivity.this, URLSet.APP_ID_QQ, URLSet.APP_KEY_QQ);
        qqSsoHandler.addToSocialSDK();
//参数1为当前Activity，参数2为开发者在QQ互联申请的APP ID，参数3为开发者在QQ互联申请的APP kEY.
        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(ArticalInfoActivity.this, URLSet.APP_ID_QQ, URLSet.APP_KEY_QQ);

        qZoneSsoHandler.addToSocialSDK();
        mController.getConfig().removePlatform(SHARE_MEDIA.RENREN, SHARE_MEDIA.DOUBAN);
        mController.openShare(ArticalInfoActivity.this, false);

        if (IfTest.ifTest) {
            Toast.makeText(ArticalInfoActivity.this, "分享给好友", Toast.LENGTH_SHORT).show();
        }
    }

    public class ArticalInfoAdapter extends BaseAdapter {
        public ArticalBean artical;
        public List<Comment> comments;
        List<NameValuePair> list;
        private Context context;
        public LayoutInflater mInflater;
        int width;
        ContentHolder contentHolder;
        private HashMap<String, SoftReference<Bitmap>> imageCache = null;


        public ArticalInfoAdapter(Context context, ArticalBean artical, List<Comment> comments) {
            this.context = context;
            this.artical = artical;
            this.comments = comments;
            mInflater = LayoutInflater.from(context);
            WindowManager wm = (WindowManager) context

                    .getSystemService(Context.WINDOW_SERVICE);
            option.inSampleSize = 8;


            width = wm.getDefaultDisplay().getWidth();

//        int height = wm.getDefaultDisplay().getHeight();
        }

        @Override
        public int getCount() {
            return comments.size() + 1;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {

            bitmap = null;
            if (i == 0) {
                contentHolder = new ContentHolder();
                view = mInflater.inflate(R.layout.artical_info_top, null);
                contentHolder.headImg = (RoundImgView) view.findViewById(R.id.artical_info_user_img);
                contentHolder.userName = (TextView) view.findViewById(R.id.artical_info_username);
                contentHolder.time = (TextView) view.findViewById(R.id.artical_info_time);
                contentHolder.userSchool = (TextView) view.findViewById(R.id.artical_info_userschool);
                contentHolder.content = (TextView) view.findViewById(R.id.artical_info_top_command);
                contentHolder.position = (TextView) view.findViewById(R.id.location_artical);
                contentHolder.imgsGrid = (GridView) view.findViewById(R.id.artical_info_top_imgs);
                contentHolder.location_linear = (LinearLayout) view.findViewById(R.id.location_linear);
                contentHolder.content.setText(artical.getArticalcontent());
                contentHolder.userSchool.setText(artical.getUserSchool());
                Glide.with(getApplicationContext()).load(artical.getImgHeader()).into(contentHolder.headImg);
                //contentHolder.headImg.setOnClickListener(new BtClickListener());
                final String sImagePath = artical.getSmimg();
                final String mImgPath = artical.getImg();
                final String[] imagePathSims = sImagePath.split(";");
                final String[] imgPaths = mImgPath.split(";");
                List<String> sImagePathlist = new ArrayList<String>();
                for (int m = 0; m < imagePathSims.length; m++) {
                    String imgpathSim = URLSet.serviceUrl + imagePathSims[m];
                    sImagePathlist.add(imgpathSim);
                }
                List<String> mImagePathList=new ArrayList<String>();
                for(int m=0;m<imgPaths.length;m++){
                    String mImagePath=URLSet.serviceUrl+imgPaths[m];
                    mImagePathList.add(mImagePath);
                }

                mImageLoader = ImageLoader.getInstance(3, ImageLoader.Type.LIFO);
                if (!(sImagePath== null||sImagePath.equals(""))){
                    contentHolder.imgsGrid.setAdapter(new ListImgItemAdaper(getApplicationContext(), 0,
                            sImagePathlist));
                } else {
                    contentHolder.imgsGrid.setAdapter(null);
                }

                contentHolder.imgsGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(ArticalInfoActivity.this, PhotoActivity.class);
                        intent.putExtra("imgPaths", imgPaths);
                        intent.putExtra("imgflag", position);
                        startActivity(intent);
                    }
                });
                try {
                    Date date = ArticalListActivity.ConverToDate(artical.getArticalTime());
                    String timeago = ArticalListActivity.getTimeAgo(getApplicationContext(),date);
                    contentHolder.time.setText(timeago);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (artical.getPostion().equals("")){
                    contentHolder.location_linear.setVisibility(View.GONE);
                }else {
                    contentHolder.position.setText(artical.getPostion());
                }

                contentHolder.userName.setText(artical.getUserName());
                //contentHolder.time.setText(artical.getArticalTime());
                view.setEnabled(false);
            } else {
                ViewHolder viewHolder = new ViewHolder();
                view = mInflater.inflate(R.layout.artical_info_comment__item_layout, null);
                viewHolder.commentUserName = (TextView) view.findViewById(R.id.artical_info_comment_username);
                viewHolder.commentTime = (TextView) view.findViewById(R.id.artical_info_comment_time);
                viewHolder.commentCotent = (TextView) view.findViewById(R.id.artical_info_comment_command);
//            viewHolder.secondCommentList=(ListView)view.findViewById(R.id.artical_info_comment_secondcomment_list);
                viewHolder.backlayout = (LinearLayout) view.findViewById(R.id.artical_info_comment_back_layout);
                viewHolder.commentImagGrid=(NoScrollGridView)view.findViewById(R.id.artical_info_comment_grid);
                viewHolder.commentUserImg = (RoundImgView) view.findViewById(R.id.artical_info_comment_userimg);
                viewHolder.secondCommentLayout = (LinearLayout) view.findViewById(R.id.artical_info_comment_item_secondcomment_layout);
                viewHolder.commentUserName.setText(comments.get(i - 1).getCommendUserName());
                //viewHolder.commentTime.setText(comments.get(i - 1).getCommendTime());
                try {
                    Date date = ArticalListActivity.ConverToDate(comments.get(i - 1).getCommendTime());
                    String timeago = ArticalListActivity.getTimeAgo(getApplicationContext(),date);
                    viewHolder.commentTime.setText(timeago);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                viewHolder.commentCotent.setText(comments.get(i - 1).getCommendContent());

                // viewHolder.commentUserImg.setOnClickListener(new BtClickListener(i));

                Glide.with(getApplicationContext()).load(comments.get(i - 1).getHeadImg()).into(viewHolder.commentUserImg);



                final String sImagePath = comments.get(i-1).getCommendSmimg();
                final String[] imagePathSims = sImagePath.split(";");
                List<String> sImagePathlist = new ArrayList<String>();
                for (int m = 0; m < imagePathSims.length; m++) {
                    String imgpathSim = URLSet.serviceUrl + imagePathSims[m];
                    sImagePathlist.add(imgpathSim);
                }

                final String mImagePath=comments.get(i-1).getCommentImg();
                final String[] mImagePaths=mImagePath.split(";");

                if(!(sImagePath==null||sImagePath.equals(""))) {
                    viewHolder.commentImagGrid.setAdapter(new ListImgItemAdaper(getApplicationContext(), 0,
                            sImagePathlist));
                }

                viewHolder.commentImagGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(ArticalInfoActivity.this, PhotoActivity.class);

                        intent.putExtra("imgPaths", mImagePaths);
                        intent.putExtra("imgflag", position);
                        startActivity(intent);
                    }
                });

                viewHolder.backlayout.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, PinglunAddActivity.class);
                        intent.putExtra("comment", comments.get(i - 1));
                        intent.putExtra("artical", artical);
                        intent.putExtra("flag", "2");//评论评论
                        ((ArticalInfoActivity) context).startActivityForResult(intent, 1);

                    }
                });
                if (comments.get(i - 1).getSecondComments().size() != 0) {
                    viewHolder.secondCommentLayout = (LinearLayout) view.findViewById(R.id.artical_info_comment_item_secondcomment_layout);
                    viewHolder.secondCommentLayout.setOrientation(LinearLayout.VERTICAL);
                    for (int n = 0; n < comments.get(i - 1).getSecondComments().size(); n++) {
                        View secondCommentView = mInflater.inflate(R.layout.artical_info_secondcomment_item, null);
                        TextView secondCommentUserName = (TextView) secondCommentView.findViewById(R.id.artical_info_second_username);
                        TextView secondCommentToUserName = (TextView) secondCommentView.findViewById(R.id.artical_info_second_acceptusername);
                        TextView secondCommentCotant = (TextView) secondCommentView.findViewById(R.id.artical_info_second_contant);
                        secondCommentUserName.setText(comments.get(i - 1).getSecondComments().get(n).getSecondCommendUserName());
                        secondCommentToUserName.setText("@" + comments.get(i - 1).getSecondComments().get(n).getSecondCommendToUserName());
                        secondCommentCotant.setText(comments.get(i - 1).getSecondComments().get(n).getSecondCommendContent());
                        viewHolder.secondCommentLayout.addView(secondCommentView);
                        secondCommentView.setOnClickListener(new ViewOnClickListener(n, i));
                    }

                }


            }
            return view;
        }

        public class ViewHolder {
            TextView commentUserName, commentTime, commentCotent;
            RoundImgView commentUserImg;
            LinearLayout backlayout, secondCommentLayout;
            NoScrollGridView commentImagGrid;
        }

        public class ContentHolder {
            TextView userName, time, content, userSchool,position;
            LinearLayout location_linear;
            RoundImgView headImg;
            GridView imgsGrid;
        }

        public class ViewOnClickListener implements OnClickListener {
            int n = 0, i = 0;

            public ViewOnClickListener(int n, int i) {
                this.n = n;
                this.i = i;
            }

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PinglunAddActivity.class);
                intent.putExtra("comment", comments.get(i - 1));
                intent.putExtra("artical", artical);
                intent.putExtra("flag", "2");//评论评论

                intent.putExtra("secondComment", comments.get(i - 1).getSecondComments().get(n));

           /* context.startActivity(intent);*/
                ((ArticalInfoActivity) context).startActivityForResult(intent, 1);
                ;
            }
        }

//        public class BtClickListener implements OnClickListener {
//
//            int i;
//            String flag;
//
//            public BtClickListener() {
//
//            }
//
//            public BtClickListener(int i) {
//                this.i = i;
//            }
//
//            public BtClickListener(int i, String flag) {
//                this.i = i;
//                this.flag = flag;
//            }
//
//            @Override
//            public void onClick(View view) {
//                switch (view.getId()) {
//
//
//                    case R.id.artical_info_user_img:
//                        Intent intent2 = new Intent(context, SelfActivity.class);
//                        intent2.putExtra("userNum", artical.getUserNum());
//                        startActivityForResult(intent2, 2);
//                        break;
//
//                    case R.id.artical_info_comment_userimg:
//                        Intent intent3 = new Intent(context, SelfActivity.class);
//                        intent3.putExtra("userNum", comments.get(i - 1).getCommentUserNum());
//                        startActivityForResult(intent3, 2);
//                        break;
//                }
//
//            }
//        }

    }
    private class ListImgItemAdaper extends ArrayAdapter<String> {
        List<String> sImagePaths=new ArrayList<String>();
        ImageView imageView;

        public ListImgItemAdaper(Context context, int resource, List<String> datas) {
            super(getApplicationContext(), 0, datas);
            this.sImagePaths=datas;
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


//            Glide.with(getApplicationContext()).load(sImagePaths.get(position)).into(imageview);
            mImageLoader.loadImage(getItem(position), imageview, true);
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
}

