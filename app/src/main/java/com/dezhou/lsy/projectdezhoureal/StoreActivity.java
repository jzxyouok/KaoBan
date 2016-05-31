package com.dezhou.lsy.projectdezhoureal;

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
import kaobanxml.XmlParser;
import kongjian.SelfActivity;
import network.MyHttpClient;
import tempvalue.UserNum;
import utils.URLSet;

public class StoreActivity extends Activity{

    Map<String,String> map;
    int page=1;
    ListView articalsList;
    List<ArticalBean> articalBeanList;
    private ImageLoader mImageLoader;
    private ImageView back;
    Handler handler=new Handler() {
        public void handleMessage(Message msg){
            switch (msg.what){
                case 0x222:
                    Bundle bundle=new Bundle();
                    bundle=msg.getData();
                    String storeStringXML=bundle.getString("store");
                    articalBeanList=new ArrayList<ArticalBean>();
                    if(storeStringXML.equals("")){
                        //���ղ�
//                        Toast.makeText(StoreActivity.this,"�������",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        articalBeanList = XmlParser.xmlArticalsGet(storeStringXML);
                        ArticalListAdapter adapter=new ArticalListAdapter(StoreActivity.this,articalBeanList);
                        articalsList.setAdapter(adapter);
                    }
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_store);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }

        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.blue);
        initView();
        initInfo();
    }
    public void initView(){
        back=(ImageView)findViewById(R.id.back_store);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        articalsList=(ListView)findViewById(R.id.activity_store_storelist);
        articalsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(StoreActivity.this, ArticalInfoActivity.class);
                intent.putExtra("artical", articalBeanList.get(i));
                startActivityForResult(intent, 1);
            }
        });
    }
    public void initInfo(){
        map=new HashMap<String,String>();
        map.put("userNum", UserNum.userNum);
        map.put("page",page+"");
        netWorkThread(URLSet.getStore, map,0x222);
    }

    private void netWorkThread(final String url, final Map<String, String> mapTmp,final int msgWhat) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DefaultHttpClient client = MyHttpClient.getHttpClient();
                String storeString = MyHttpClient.doGet(client, url, mapTmp, true);
                Message message=new Message();
                message.what=msgWhat;
                Bundle bundle=new Bundle();
                if(storeString==null){
                    storeString="";
                }
                bundle.putString("store",storeString);
                message.setData(bundle);
                handler.sendMessage(message);
            }
        }).start();
    }


    public class ArticalListAdapter extends BaseAdapter {

        private LayoutInflater inflater;
        private List<ArticalBean> articals;
        private Context mContext;
        private HashMap<String,SoftReference<Bitmap>> imageCache=null;



        List<NameValuePair> tmpList;
        BitmapFactory.Options option = new BitmapFactory.Options();
        Handler handler=new Handler(){
            public void handleMessage(Message msg){
                switch (msg.what){
                    case 0x124:
                        Bundle bunle=new Bundle();
                        bunle=msg.getData();
                        String resultXml=bunle.getString("likeResult");
                        String result= XmlParser.xmlResultGet(resultXml);
                        if(result.equals("success")){
                            Toast.makeText(mContext, "赞", Toast.LENGTH_SHORT).show();
                            initInfo();
                        }
                        else{
                            Toast.makeText(mContext,"赞失败",Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 0x125:
                        Bundle bundle=new Bundle();
                        bundle=msg.getData();
                        String storeResultXml=bundle.getString("storeResult");
                        String storeResult=XmlParser.xmlResultGet(storeResultXml);
                        if(storeResult.equals("success")){
                            Toast.makeText(mContext,"收藏成功",Toast.LENGTH_SHORT).show();
                            initInfo();
                        }
                        else{
                            Toast.makeText(mContext,"收藏失败",Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
            }
        };
        public ArticalListAdapter(Context context, List<ArticalBean> list) {
            this.mContext = context;
            inflater = LayoutInflater.from(context);
            this.articals = list;

            option.inSampleSize=8;
        }

        public void updateView(List<ArticalBean> list) {

            this.articals = list;
            notifyDataSetChanged();
        }


        @Override
        public int getCount() {

            return articals.size();
        }


        @Override
        public Object getItem(int position) {

            return articals.get(position);
        }


        @Override
        public long getItemId(int position) {

            return position;
        }


        @Override
        public View getView(int i, View view, ViewGroup parent) {
            ViewHolder viewHolder = new ViewHolder();
            view = inflater.inflate(R.layout.artical_list_item, null);
            viewHolder.headImg = (ImageView) view.findViewById(R.id.artical_list_user_img);
            viewHolder.gridView=(GridView)view.findViewById(R.id.articalfragment_info_top_imgs);
            //  viewHolder.ifImg=(ImageView)view.findViewById(R.id.artical_list_item_if_img);
            viewHolder.like = (ImageView) view.findViewById(R.id.artical_list_like_img);
            viewHolder.pinglun = (ImageView) view.findViewById(R.id.artical_list_pinglun_img);
            viewHolder.location_artical=(TextView)view.findViewById(R.id.location_artical);
            viewHolder.shoucangImg=(ImageView)view.findViewById(R.id.artical_list_shoucang_img);
            viewHolder.userName = (TextView) view.findViewById(R.id.user_name);
            viewHolder.time = (TextView) view.findViewById(R.id.artical_time);
            viewHolder.text = (TextView) view.findViewById(R.id.text);
            viewHolder.deleteArtical=(TextView)view.findViewById(R.id.delete_artical);
            viewHolder.likeNum = (TextView) view.findViewById(R.id.artical_list_like_num);
            viewHolder.pinglunNum = (TextView) view.findViewById(R.id.artical_list_pinglun_num);
            viewHolder.shoucangNum=(TextView)view.findViewById(R.id.artical_list_shoucang_num);
            viewHolder.school=(TextView)view.findViewById(R.id.artical_list_school);
            viewHolder.location=(LinearLayout)view.findViewById(R.id.location_linear);
            viewHolder.like.setOnClickListener(new btClickListener(i));
            viewHolder.shoucangImg.setOnClickListener(new btClickListener(i));
            viewHolder.deleteArtical.setVisibility(View.GONE);
            viewHolder.pinglun.setOnClickListener(new btClickListener(i));
            Glide.with(getApplicationContext()).load(articals.get(i).getImgHeader()).into(viewHolder.headImg);
            if(!articals.get(i).getImg().equals("")) {
                final String imagePath = articals.get(i).getSmimg();
                final String[] imagePaths = imagePath.split(";");
                List<String> imagePathlist = new ArrayList<String>();
                for (int m = 0; m < imagePaths.length; m++) {
                    String imgpath = URLSet.serviceUrl + imagePaths[m];
                    imagePathlist.add(imgpath);
                }

                mImageLoader = ImageLoader.getInstance(3, ImageLoader.Type.LIFO);
                if (imagePath != null) {
                    viewHolder.gridView.setAdapter(new ListImgItemAdaper(getApplicationContext(), 0,
                            imagePathlist));
                } else {
                    viewHolder.gridView.setAdapter(null);
                }


                viewHolder.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(mContext, PhotoActivity.class);

                        intent.putExtra("imgPaths", imagePaths);
                        intent.putExtra("imgflag", position);
                        startActivity(intent);
                    }
                });
            }

            if (articals.get(i).getPostion().equals("")){
                viewHolder.location.setVisibility(View.GONE);
            }else {
                viewHolder.location_artical.setText(articals.get(i).getPostion());
            }
            viewHolder.userName.setText(articals.get(i).getUserName());

            try {
                Date date = ArticalListActivity.ConverToDate(articals.get(i).getArticalTime());
                String timeago = ArticalListActivity.getTimeAgo(getApplicationContext(),date);
                viewHolder.time.setText(timeago);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (articals.get(i).getArticalcontent().length() > 200) {
                viewHolder.text.setText(articals.get(i).getArticalcontent().substring(0, 200) + "...");
            } else {
                viewHolder.text.setText(articals.get(i).getArticalcontent());
            }
            viewHolder.headImg.setOnClickListener(new btClickListener(i));
//        viewHolder.userImg.setImageResource(R.drawable.headimg);


            if (articals.get(i).getLikeNum().equals("0")){
                viewHolder.likeNum.setText("点赞");
            }
            else {
                viewHolder.likeNum.setText(articals.get(i).getLikeNum());
            }
            if (articals.get(i).getStoreNum().equals("0")){
                viewHolder.shoucangNum.setText("收藏");
            }
            else {
                viewHolder.shoucangNum.setText(articals.get(i).getStoreNum());
            }

            if (articals.get(i).getCommendNum().equals("0")){
                viewHolder.pinglunNum.setText("评论");
            }
            else {
                viewHolder.pinglunNum.setText(articals.get(i).getCommendNum());
            }


            viewHolder.school.setText(articals.get(i).getUserSchool());
            if (articals.get(i).getIfLike().equals("true"))
                viewHolder.like.setImageResource(R.drawable.like);
            else{
                viewHolder.like.setImageResource(R.drawable.not_like);
            }
            /*
            �Ƿ��ղ�
             */
            if(articals.get(i).getIfStore().equals("true")){
                viewHolder.shoucangImg.setImageResource(R.drawable.store);
            }else{
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
                    case R.id.artical_list_like_img:
                        if(articals.get(i).getIfLike().equals("true")){
                            Toast.makeText(mContext,"已赞",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            NameValuePair np = new BasicNameValuePair("articalNum", articals.get(i).getArticalsNum());
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
                                    msg.setData(bundle);
                                    handler.sendMessage(msg);
                                }
                            }).start();
                        }
                        break;
                    case R.id.artical_list_shoucang_img:
                        if(articals.get(i).getIfStore().equals("true")){
                            Toast.makeText(mContext,"已收藏",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Map<String, String> mapTmp = new HashMap<String, String>();
                                    mapTmp.put("articalNum", articals.get(i).getArticalsNum());
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

                    case R.id.artical_list_pinglun_img:
                        Intent intent=new Intent(mContext, PinglunAddActivity.class);
                        intent.putExtra("artical",articals.get(i));
                        intent.putExtra("flag", "1");

                        startActivityForResult(intent,1);

                        break;
                    case R.id.artical_list_user_img:
                        Intent intent2=new Intent(mContext, SelfActivity.class);
                        intent2.putExtra("userNum",articals.get(i).getUserNum());
                        startActivityForResult(intent2,1);
                }
            }
        }



        public class ViewHolder{
            public LinearLayout location;
            public TextView deleteArtical;
            public ImageView like,pinglun,shoucangImg;
            public TextView pinglunNum,likeNum,userName,time,text,shoucangNum,school,location_artical;
            public ImageView headImg;
            public GridView gridView;
        }




    }
    private class ListImgItemAdaper extends ArrayAdapter<String>
    {

        public ListImgItemAdaper(Context context, int resource, List<String> datas)
        {
            super(getApplicationContext(), 0, datas);

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            if (convertView == null)
            {
                convertView = getLayoutInflater().inflate(
                        R.layout.item_fragment_list_imgs, parent, false);
            }
            ImageView imageview = (ImageView) convertView
                    .findViewById(R.id.id_img);
            imageview.setImageResource(R.drawable.pictures_no);
            Glide.with(getApplicationContext()).load(getItem(position)).into(imageview);
            //mImageLoader.loadImage(getItem(position), imageview, true);
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
