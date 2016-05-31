package com.dezhou.lsy.projectdezhoureal;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.Lc.Readdoc;
import com.bumptech.glide.Glide;
import com.com.easemob.chatuidemo.DemoApplication;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import net.tsz.afinal.FinalBitmap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaobanxml.XmlParser;
import kongjian.AlertDialogs;
import network.FileDown;
import network.MyHttpClient;
import tempvalue.Cookie;
import tempvalue.UserNum;
import utils.URLSet;
import view.view.XListView;

public class DocActvity extends FragmentActivity implements XListView.IXListViewListener {
    private View v;
    private List<NameValuePair> listArtical;
    private int page =1;
    Handler mHandler=new Handler();
    private static String urlFileDown = URLSet.serviceUrl+"/kaoban/getDoc/";
    private List<FileDown> files=new ArrayList<FileDown>();
    FinalBitmap finalBitmap =null;
    private XListView listView;
    private FileAdapter fileAdapter;
    private HashMap<String,SoftReference<Bitmap>> imageCache=null;
    BitmapFactory.Options option = new BitmapFactory.Options();
    DemoApplication app;
    private String user_num;
    ImageView back,addNew;
    public String topic;
    public final int FILE_SELECTE_CODE=6;
    private Handler handler=new Handler(){
        public void handleMessage(Message msg){
            switch (msg.what){
                case 0x237:
                    Bundle bundle4=new Bundle();
                    bundle4=msg.getData();
                    String resultXML=bundle4.getString("result");
                    if(!resultXML.equals("")){
                        String result=XmlParser.xmlResultGet(resultXML);
                        if(result.equals("success")){
                            Toast.makeText(DocActvity.this,"上传成功",Toast.LENGTH_SHORT).show();
                            files=new ArrayList<FileDown>();
                            page=1;
                            initNetWork(page);
                        }
                        else{
                            Toast.makeText(DocActvity.this,"上传失败",Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
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
                    fileAdapter = new FileAdapter(DocActvity.this,files);
                    listView.setAdapter(fileAdapter);
                    listView.setSelection(files.size()-changeFiles.size());
                    listView.setXListViewListener(DocActvity.this);
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
                        Toast.makeText(getApplicationContext(), "删除资料失败", Toast.LENGTH_SHORT).show();
                    }
            }
        }
    };


    private void onLoad() {
        listView.stopRefresh();
        listView.stopLoadMore();
        listView.setRefreshTime("刚刚");
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc_actvity);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }

        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.blue);
        Intent intent=getIntent();
        topic=intent.getStringExtra("topic");
        initView();
    }
    public void initView(){
        back=(ImageView)findViewById(R.id.doc_list_back);
        addNew=(ImageView)findViewById(R.id.doc_list_add);
        TextView title=(TextView)findViewById(R.id.artical_title);
        title.setText(topic);
        listView=(XListView)findViewById(R.id.file_listview);
        listView.setPullLoadEnable(true);
        back.setOnClickListener(new BtClickListener());
        addNew.setOnClickListener(new BtClickListener());
        initNetWork(page);
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

    public class BtClickListener implements View.OnClickListener{

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.doc_list_back:
                finish();
                break;
            case R.id.doc_list_add:
                showFileChooser();
                break;
        }
    }
}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case FILE_SELECTE_CODE:
                if(resultCode==RESULT_OK){
                    Uri uri = data.getData();
                    final String path = FileUtils.getPath(this, uri);
                    final EditText inputServer = new EditText(this);
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("输入资料介绍").setIcon(android.R.drawable.ic_dialog_info).setView(inputServer)
                            .setNegativeButton("取消", null);
                    builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            String info=inputServer.getText().toString();
                            sendNewDoc(path,info);
                        }
                    });
                    builder.show();
                }
                break;
        }
    }
public void sendNewDoc(final String path, final String info){

    new Thread() {
        @Override
        public void run() {

            try {

                HttpContext context = new BasicHttpContext();

                MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
                entity.addPart("doc",new FileBody(new File(path)));

                entity.addPart("title", new StringBody(info, Charset.forName("UTF-8")));

                DefaultHttpClient client = new DefaultHttpClient();
                client.setCookieStore(Cookie.cookieStore);
                HttpPost post = new HttpPost(URLSet.uploadFile + "?userNum=" + UserNum.userNum);

                post.setEntity(entity);

                HttpResponse response = client.execute(post, context);


                /**请求发送成功，并得到响应**/
                if (response.getStatusLine().getStatusCode() == 200) {
                    String str = "";
                    String result="";
                    try {
                        str = response.getStatusLine().getReasonPhrase();
                        if(str.equals("OK")){
                            HttpEntity httpEntity = response.getEntity();
                            result= EntityUtils.toString(httpEntity, "utf-8");
                            Message message = new Message();
                            message.what = 0x237;
                            Bundle bundle = new Bundle();
                            if(result==null)
                                result="";
                            bundle.putString("result", result);
                            message.setData(bundle);
                            handler.sendMessage(message);
                        }
                        else{
                            Toast.makeText(DocActvity.this,"上传中断",Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(DocActvity.this, "未知错误,发送失败", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(DocActvity.this, "未知错误,发送失败", Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                Toast.makeText(DocActvity.this, "未知错误,发送失败", Toast.LENGTH_SHORT).show();
            }

        }

    }.start();

}
    public static class FileUtils {
        public static String getPath(Context context, Uri uri) {

            if ("content".equalsIgnoreCase(uri.getScheme())) {
                String[] projection = { "_data" };
                Cursor cursor = null;

                try {
                    cursor = context.getContentResolver().query(uri, projection,null, null, null);
                    int column_index = cursor.getColumnIndexOrThrow("_data");
                    if (cursor.moveToFirst()) {
                        return cursor.getString(column_index);
                    }
                } catch (Exception e) {
                    // Eat it
                }
            }

            else if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            }

            return null;
        }
    }
    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("text/doc");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult( Intent.createChooser(intent, "选择一个文档"),FILE_SELECTE_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Please install a File Manager.",  Toast.LENGTH_SHORT).show();
        }
    }
    private void initNetWork(int pages) {

        netWorkThread("page", pages+ "");
    }



    private void netWorkThread(String key, String values) {
        final Map<String,String> map=new HashMap<String,String>();
        map.put(key, values);
        new Thread(new Runnable() {
            @Override
            public void run() {
                DefaultHttpClient client = MyHttpClient.getHttpClient();
                String tmpString = MyHttpClient.doGet(client, urlFileDown, map, true);
                Message msg = new Message();
                msg.what = 0x238;
                Bundle bundle = new Bundle();
                if(tmpString==null)
                    tmpString="";
                bundle.putString("key", tmpString);
                msg.setData(bundle);
                handler.sendMessage(msg);
            }
        }).start();

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
                    Intent intent = new Intent(getApplicationContext(), Readdoc.class);
                    intent.putExtra("title", title);
                    intent.putExtra("filepath", filepath);
                    startActivity(intent);
                }
            });


            try {
                Date date = ArticalListActivity.ConverToDate(files.get(position).getDate());
                String timeago = ArticalListActivity.getTimeAgo(getApplicationContext(),date);
                viewHolder.date.setText(timeago);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (files.get(position).getUserNum().equals(UserNum.userNum)){
                viewHolder.deletefile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new AlertDialogs(DocActvity.this).builder().setTitle("确定删除吗?")
                                .setPositiveButton("删除", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String docNum = files.get(position).getDocNum();
                                        deleteDoc("docNum", docNum);

                                    }
                                }).setNegativeButton("取消", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        }).show();
                    }
                });
            }
            else{
                viewHolder.deletefile.setVisibility(View.GONE);
            }

            String url1 = URLSet.serviceUrl+ imgPath;
            String str2="";
            str2 = url1.replaceAll(" ", "");
            try {
                URL url= new URL(str2);
                Glide.with(getApplicationContext()).load(url).into(viewHolder.headImg);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

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
