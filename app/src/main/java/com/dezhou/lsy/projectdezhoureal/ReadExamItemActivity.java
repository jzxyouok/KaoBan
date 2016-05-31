package com.dezhou.lsy.projectdezhoureal;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import org.apache.http.impl.client.DefaultHttpClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaobanxml.XmlParser;
import network.MyHttpClient;
import tempvalue.HighExamItem;
import utils.URLSet;

/**
 * Created by zy on 15-8-20.
 */
public class ReadExamItemActivity extends Activity{

    private final String url = URLSet.serviceUrl+"/kaoban/highExam/content/";
    private String school;
    private String conNum;

    private ListView listView;

    private List<HighExamItem> list;
    private TextView back;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String res = (String)msg.obj;
            list = XmlParser.highExamParser(res);
            if (list != null){
                setListView(list);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_readexam_item);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }

        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.blue);
        init ();
    }

    private void init(){
        Intent intent = getIntent();
        school = intent.getStringExtra("school");
        conNum = intent.getStringExtra("conNum");
        back=(TextView)this.findViewById(R.id.backactivity);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        listView = (ListView) this.findViewById(R.id.listview);

        startInternet();
    }

    private void setListView(List<HighExamItem> itemList){
        List<Map<String,String>> data = new ArrayList<>();
        for (HighExamItem item : itemList){
            Map<String,String> map = new HashMap<>();
            map.put("name",item.getName());
            data.add(map);
        }

        SimpleAdapter simpleAdapter = new SimpleAdapter(this,data,R.layout.listview_item,
                new String[]{"name"},new int[] {R.id.tv_item});

        listView.setAdapter(simpleAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ReadExamItemActivity.this,ZyWebViewActivity.class);
                intent.putExtra("item",list.get(position).getId());
                intent.putExtra("exam",1);
                startActivity(intent);
            }
        });
    }

    private void startInternet(){
        final Map<String,String> map = new HashMap<>();
        map.put("school",school);
        map.put("conNum",conNum);
        new Thread(){
            @Override
            public void run() {
                DefaultHttpClient client = MyHttpClient.getHttpClient();
                String res = MyHttpClient.doGet(client,url,map,false);
                handler.sendMessage(handler.obtainMessage(0, res));
            }
        }.start();
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
