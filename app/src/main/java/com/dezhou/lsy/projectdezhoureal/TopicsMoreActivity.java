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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import org.apache.http.impl.client.DefaultHttpClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adapter.TopicsAdapter;
import bean.Classification;
import bean.Topic;
import kaobanxml.XmlParser;
import network.MyHttpClient;
import utils.URLSet;

/*
发现二级界面——（一级界面点击更多）
 */

public class TopicsMoreActivity extends Activity {
    ListView list;
    ImageButton backButton;
    Classification classification;
    List<Topic> topics;
    ImageView addTopic;
    private static Map<String,String> aticalSecondFlag=new HashMap<String,String>();
    int ADDTOPICREQUEST=1;
    int ADDTOPICRESULT=2;
    public Handler handler=new Handler(){
        public void handleMessage(Message msg){
            switch (msg.what){
                case 0x121:
                    Bundle bundle=new Bundle();
                    bundle=msg.getData();
                    String topicsXml=bundle.getString("simpleString");
                    topics= XmlParser.xmlTopicsGet(topicsXml);
                    TopicsAdapter adapter=new TopicsAdapter(TopicsMoreActivity.this,topics);
                    list.setAdapter(adapter);
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atical_second);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }

        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.blue);


        Intent intent=getIntent();
        classification=(Classification)intent.getSerializableExtra("title");
        initView();

        initNetWork();
    }
    public void initView(){
        addTopic=(ImageView)findViewById(R.id.add_topic);
        list=(ListView)findViewById(R.id.list);
        ImageView back=(ImageView)findViewById(R.id.back);
        TextView title=(TextView)findViewById(R.id.title);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        addTopic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent intent=new Intent(TopicsMoreActivity.this,AddTopicsActivity.class);
                intent.putExtra("classification",classification);
                startActivityForResult(intent,ADDTOPICREQUEST);
            }
        });
        title.setText(classification.getClassificationName());
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (topics.get(i).getTopicName().equals("机密资料")) {
                    Intent intent = new Intent(TopicsMoreActivity.this, DocActvity.class);
                    intent.putExtra("topic", topics.get(i).getTopicName());
                    startActivityForResult(intent, 2);
                } else {
                    Intent intent = new Intent(TopicsMoreActivity.this, ArticalListActivity.class);
                    intent.putExtra("topic", topics.get(i));
                    startActivityForResult(intent, 2);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==ADDTOPICREQUEST&&resultCode==ADDTOPICRESULT){

            initNetWork();
        }
    }

    public void initNetWork() {

        aticalSecondFlag.put("first", classification.getClassificationName());
        netWorkThread(URLSet.getClassificationURL, aticalSecondFlag, 0x121);
    }
    private void netWorkThread(final String url, final Map<String, String> mapTmp,final int msgWhat) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DefaultHttpClient client = MyHttpClient.getHttpClient();
                String aticalSecond = MyHttpClient.doGet(client, url, mapTmp, true);
                Message message=new Message();
                message.what=msgWhat;
                Bundle bundle=new Bundle();
                bundle.putString("simpleString",aticalSecond);
                message.setData(bundle);
                handler.sendMessage(message);

            }
        }).start();
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
