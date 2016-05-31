package fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.Lc.Readdoc;
import com.bumptech.glide.Glide;
import com.com.easemob.chatuidemo.DemoApplication;
import com.dezhou.lsy.projectdezhoureal.ArticalListActivity;
import com.dezhou.lsy.projectdezhoureal.R;

import net.tsz.afinal.FinalBitmap;

import org.apache.http.NameValuePair;
import org.apache.http.impl.client.DefaultHttpClient;

import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaobanxml.XmlParser;
import kongjian.AlertDialogs;
import network.FileDown;
import network.MyHttpClient;
import utils.URLSet;
import view.view.XListView;

/**
 * Created by wenjun on 2015/7/25.
 */
public class PhotoFragment extends Fragment implements XListView.IXListViewListener{

    private View v;
    private List<NameValuePair> listArtical;
    private int page =1;
    Handler mHandler=new Handler();
    private static String urlFileDown = URLSet.serviceUrl+"/kaoban/getUserDoc/";
    private List<FileDown> files=new ArrayList<FileDown>();
    FinalBitmap finalBitmap =null;
    private XListView listView;
    private FileAdapter fileAdapter;
    private HashMap<String,SoftReference<Bitmap>> imageCache=null;
    BitmapFactory.Options option = new BitmapFactory.Options();
    DemoApplication app;
    private String user_num;
    private Handler handler=new Handler(){
        public void handleMessage(Message msg){
            switch (msg.what){

                case 0x238:
                    onLoad();
                    Bundle bundle2=new Bundle();
                    bundle2=msg.getData();
                    String sendArticalXml=bundle2.getString("key");
                    if(sendArticalXml.equals(""))
                        break;
                    List<FileDown> changeFiles=new ArrayList<FileDown>();
                    changeFiles=XmlParser.xmlFileDown(sendArticalXml);
//                    files= XmlParser.xmlFileDown(sendArticalXml);
                    for(int i=0;i<changeFiles.size();i++){
                        files.add(changeFiles.get(i));
                    }
                    ListFileView(files);
                    break;
                case 0x239:
                    Bundle bundle=new Bundle();
                    bundle=msg.getData();
                    String deleteDoc=bundle.getString("deletedoc");
                    String result=XmlParser.xmlResultGet(deleteDoc);
                    if(result.equals("success")){
                        files=new ArrayList<FileDown>();
                        page=1;
                        initNetWork(page);
                    }else {
                        Toast.makeText(getActivity(),"删除资料失败",Toast.LENGTH_SHORT).show();
                    }
            }
        }
    };
    private void ListFileView(List<FileDown> files) {
        fileAdapter = new FileAdapter(getActivity(),files);
        listView.setAdapter(fileAdapter);
        listView.setXListViewListener(this);
        setListViewHeight(listView);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_files, null);

        listView=(XListView)v.findViewById(R.id.file_listview);
        listView.setPullLoadEnable(true);
        app=(DemoApplication)getActivity().getApplicationContext();
        user_num=app.getName();
        initNetWork(page);
        return v;
    }
    public void setListViewHeight(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    private void initNetWork(int pages) {

        netWorkThread("page", pages+ "");
    }



    private void netWorkThread(String key, String values) {
        final Map<String,String>map=new HashMap<String,String>();
        map.put(key, values);
        map.put("userNum", user_num);
        new Thread(new Runnable() {
            @Override
            public void run() {
                DefaultHttpClient client = MyHttpClient.getHttpClient();
                String tmpString = MyHttpClient.doGet(client, urlFileDown, map, false);
                Message msg = new Message();
                msg.what = 0x238;
                Bundle bundle = new Bundle();
                bundle.putString("key", tmpString);
                msg.setData(bundle);
                handler.sendMessage(msg);
            }
        }).start();

    }

    private void deleteDoc(String key, String value) {
        final Map<String,String>map=new HashMap<String,String>();
        map.put(key, value);

        new Thread(new Runnable() {
            @Override
            public void run() {
                DefaultHttpClient client = MyHttpClient.getHttpClient();

                String tmpString = MyHttpClient.doGet(client, URLSet.deleteDoc, map, true);
                Message msg = new Message();
                msg.what = 0x239;
                Bundle bundle = new Bundle();
                bundle.putString("deletedoc", tmpString);
                msg.setData(bundle);
                handler.sendMessage(msg);
            }
        }).start();
    }

    private void onLoad() {
        listView.stopRefresh();
        listView.stopLoadMore();
        listView.setRefreshTime("刚刚");
    }

    @Override
    public void onRefresh() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                page = 1;
                files.clear();
                files=new ArrayList<FileDown>();
                initNetWork(page);

            }
        }, 3000);
    }

    @Override
    public void onLoadMore() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                page++;
                initNetWork(page);

            }
        }, 3000);
    }


    private class FileAdapter extends BaseAdapter {

        private List<FileDown> files = null;
        private LayoutInflater inflater;
        private int examType = 0;
        private Handler myHandler;
        private Bitmap bitmap;
        private String imgPath="";
        public FileAdapter(Context context, List<FileDown> files) {
            this.files = files;
            inflater = LayoutInflater.from(context);
            Log.d("cout", "ExamAdapter");
        }

        @Override
        public int getCount() {
            return files.size();
        }

        @Override
        public Object getItem(int position) {
            return files.get(position).getFile();
        }

        @Override
        public long getItemId(int position) {
            Log.d("cout", "getItemId  " + position);
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = this.inflater.inflate(R.layout.file, null);
                AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                convertView.setLayoutParams(layoutParams);

            }

            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.linear_doc=(LinearLayout)convertView.findViewById(R.id.linear_doc);
            viewHolder.headImg=(ImageView)convertView.findViewById(R.id.file_headImg);
            imgPath=files.get(position).getHeadImg();
            viewHolder.userName=(TextView)convertView.findViewById(R.id.file_userName);
            viewHolder.userName.setText(files.get(position).getUserName());
            viewHolder.title=(TextView)convertView.findViewById(R.id.file_title);
            viewHolder.title.setText(files.get(position).getTitle());
            viewHolder.filedowned =(TextView)convertView.findViewById(R.id.file);
            viewHolder.school=(TextView)convertView.findViewById(R.id.doc_school);
            viewHolder.date=(TextView)convertView.findViewById(R.id.file_time);
            viewHolder.deletefile=(TextView)convertView.findViewById(R.id.delete_file);
            final String title = files.get(position).getTitle();
            final String filepath = files.get(position).getFile();
            String strFile =filepath.substring(filepath.lastIndexOf("/")+1);
            viewHolder.filedowned.setText(strFile);
            viewHolder.school.setText(files.get(position).getSchool());
            viewHolder.linear_doc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), Readdoc.class);
                    intent.putExtra("title", title);
                    intent.putExtra("filepath", filepath);
                    startActivity(intent);
                }
            });


            try {
                Date date = ArticalListActivity.ConverToDate(files.get(position).getDate());
                String timeago = ArticalListActivity.getTimeAgo(getActivity(),date);
                viewHolder.date.setText(timeago);
            } catch (Exception e) {
                e.printStackTrace();
            }

            viewHolder.deletefile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialogs(getActivity()).builder().setTitle("确定删除吗?")
                            .setPositiveButton("删除", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String docNum=files.get(position).getDocNum();
                                    deleteDoc("docNum",docNum);

                                }
                            }).setNegativeButton("取消", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    }).show();
                }
            });

            String url1 = URLSet.serviceUrl+ imgPath;
            String str2="";
            str2 = url1.replaceAll(" ", "");
            try {
                URL url2=new URL(str2);
                Glide.with(getActivity()).load(url2).into(viewHolder.headImg);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
//            Bitmap bitmap=loadBitmap(str2);
//            if(bitmap!=null){
//                viewHolder.headImg.setImageBitmap(bitmap);
//            }
//            else{
//                LoadBitmapAsyn loadBitmapAsyn = new LoadBitmapAsyn(viewHolder.headImg);
//                loadBitmapAsyn.execute(str2);
//            }
//            finalBitmap = FinalBitmap.create(getActivity());
//            finalBitmap.display(viewHolder.headImg,str2);
            return convertView;
        }
    }



    private class ViewHolder {
        ImageView headImg;
        TextView userName,date,deletefile;
        TextView title;
        TextView filedowned;
        TextView school;
        LinearLayout linear_doc;
    }


}
