package com.dezhou.lsy.projectdezhoureal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.impl.client.DefaultHttpClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaobanxml.XmlParser;
import network.MyHttpClient;
import network.SerializableMsg;
import tempvalue.Exam;
import tempvalue.IfTest;
import utils.URLSet;

/**
 * Created by zy on 15-4-30.
 */
public class AddExamGuideAndBefore extends Activity{

    private static String urlContent = URLSet.serviceUrl+"/kaoban/diploma/content/";
    private static String urlGuide = URLSet.serviceUrl+"/kaoban/diploma/guide/";

    TextView titleTextView = null;
    ListView listView = null;

    String second;
    String guideOrBeforeContent;
    int guideOrBefore = 0;


    private static Map<String, String> mapTmp = new HashMap<String, String>();

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            //获取考证第一大分类
            if (msg.what == 0x240) {
                String tmp = msg.getData().getString("GetShuoshuo");
                Log.d("TestApp", "....获取的数据为....." + tmp);
                if (tmp != null && !"".equals(tmp)) {
                    //String content = XmlParser.xmlParserGet(tmp).toString().trim();
                    List<Exam> exams = XmlParser.xmlExamsGet(tmp);
                    if (exams != null) {
                        if (IfTest.ifTest){
                            Toast.makeText(AddExamGuideAndBefore.this, "获取考证第一大类成功" + exams.toString(), Toast.LENGTH_SHORT).show();
                        }
                        setListView(exams);
                    } else {
                        if (IfTest.ifTest){
                            Toast.makeText(AddExamGuideAndBefore.this, "获取考试大纲失败！", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    if (IfTest.ifTest){
                        Toast.makeText(AddExamGuideAndBefore.this, "请求被重置！++" + tmp, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //此处直接借用了高校列表的layout，因为内容都一样，不必要重写一个
        setContentView(R.layout.activity_diploma_guide);

        getDataFromIntent();

        initNetWork();

        initView();
    }

    private void initNetWork() {
        Log.d("cout","network " + guideOrBefore + guideOrBeforeContent + second);
        mapTmp.clear();
        mapTmp.put("second", second);
        mapTmp.put("conNum","" + guideOrBefore);
        netWorkThread(urlContent, mapTmp, 0x240);
    }

    private void getDataFromIntent() {
        guideOrBefore = getIntent().getIntExtra("guideOrBefore",0);
        guideOrBeforeContent = getIntent().getStringExtra("content");
        second = getIntent().getStringExtra("second");
    }

    private void initView() {
        titleTextView = (TextView) this.findViewById(R.id.readexam_title);
        titleTextView.setText("考试大纲");

        listView = (ListView) this.findViewById(R.id.readexam_listview);
    }


    private void netWorkThread(final String url, final Map<String, String> mapTmp, final int msgWhat) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DefaultHttpClient client = MyHttpClient.getHttpClient();
                String tmpString = MyHttpClient.doGet(client, url, mapTmp, true);
                Message msg = new Message();
                msg.what = msgWhat;
                Bundle bundle = new Bundle();
                bundle.putString("GetShuoshuo", tmpString);
                msg.setData(bundle);
                handler.sendMessage(msg);
            }
        }).start();
    }

    private List<Map<String, Object>> setList(List<Exam> exams) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        for (int i = 0; i < exams.size(); i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("name", exams.get(i).getExam());
            list.add(map);
        }

        return list;
    }

    private void setListView(final List<Exam> exams){
        List<Map<String, Object>> map = setList(exams);
        SimpleAdapter simpleAdapter = new SimpleAdapter(this,map,R.layout.addexam_highschool_item,
                new String [] {"name"},new int[] {R.id.readexam_item_schoolname});

        listView.setAdapter(simpleAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                readExam(exams.get(position).getExam());
            }
        });
    }
    void readExam(String content) {
        // 跳转到WebView页面
        Intent intent = new Intent(this, ZyWebViewActivity.class);
        intent.putExtra("guide", content);
        intent.putExtra("tag", 2);
        intent.putExtra("exam",0);
        startActivity(intent);
    }

}
