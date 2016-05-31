package com.Lc;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.dezhou.lsy.projectdezhoureal.R;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import org.apache.http.NameValuePair;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bean.Item;
import kaobanxml.XmlParser;
import kongjian.EduActivity;
import network.MyHttpClient;
import utils.URLSet;

public class Department extends ActionBarActivity {

    private List<Item> items = new ArrayList<Item>();
    private ListView department_self;
    private List<NameValuePair> listArtical;
    private ImageView back;
    private String school;
    private Handler handler=new Handler(){
        public void handleMessage(Message msg){
            switch (msg.what){
                case 0x238:
                    Bundle bundle2=new Bundle();
                    bundle2=msg.getData();
                    String sendArticalXml=bundle2.getString("key");
                    String result= XmlParser.xmlResultGet(sendArticalXml);
                    if(result.equals("success")){

                        Intent intent_department = new Intent(Department.this, EduActivity.class);
                        startActivity(intent_department);
                        finish();
                    }
                    else{


                    }
                    break;
                case 0x239:
                    Bundle bundle=new Bundle();
                    bundle=msg.getData();
                    String sendSchoolXml=bundle.getString("schoolbundle");
                    items = XmlParser.xmlSchoolsGet(sendSchoolXml);
                    setTheme(R.style.ActionSheetStyleIOS7);
                    showDepartment(items);
                    break;

            }
        }
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_department);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }

        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.blue);
        department_self=(ListView)findViewById(R.id.department_self);
        back=(ImageView)findViewById(R.id.back_SignActivity);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        school = getIntent().getStringExtra("school");
        postSchool("school",school);
    }

    private void postSchool(String schoolkey,String schoolvalue){

        final Map<String,String> schoolMap = new HashMap<String,String>();
        schoolMap.put(schoolkey, schoolvalue);

        new Thread(new Runnable() {
            @Override
            public void run() {
                DefaultHttpClient client = MyHttpClient.getHttpClient();

                String tmpString = MyHttpClient.doGet(client, URLSet.schoolInf, schoolMap, true);
                Message msg = new Message();
                msg.what = 0x239;
                Bundle bundle = new Bundle();
                bundle.putString("schoolbundle", tmpString);
                msg.setData(bundle);
                handler.sendMessage(msg);
            }
        }).start();
    }
    private void showDepartment(final List<Item> items) {
        DepartAdapter adapter = new DepartAdapter(this,items);
        department_self.setAdapter(adapter);
        department_self.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                postToServer("department", items.get(position).getItem());
            }
        });
    }

    private void postToServer(String key,String values ){


        listArtical = new ArrayList<NameValuePair>();

        NameValuePair npTmp3 = new BasicNameValuePair(key, values);

        listArtical.add(npTmp3);

        new Thread(new Runnable() {
            @Override
            public void run() {
                DefaultHttpClient client = MyHttpClient.getHttpClient();

                String tmpString = MyHttpClient.doPost(client, URLSet.changeInf, listArtical, true);
                Message msg = new Message();
                msg.what = 0x238;
                Bundle bundle = new Bundle();
                bundle.putString("key", tmpString);
                msg.setData(bundle);
                handler.sendMessage(msg);
            }
        }).start();

    }
    private class DepartAdapter extends BaseAdapter {

        private List<Item> depart = null;
        private LayoutInflater inflater;

        public DepartAdapter(Context context, List<Item> depart) {
            this.depart = depart;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return depart.size();
        }

        @Override
        public Object getItem(int position) {
            return depart.get(position).getItem();
        }

        @Override
        public long getItemId(int position) {
            Log.d("cout", "getItemId  " + position);
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = this.inflater.inflate(R.layout.department_item, null);
                AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
         //      layoutParams.height = 100;
                convertView.setLayoutParams(layoutParams);
            }

            final ViewHolder viewHolder = new ViewHolder();

//            viewHolder.examIcon = (ImageView) convertView.findViewById(R.id.department_headimg);
//            Random random = new Random();
//            int icon = random.nextInt(examIcon.length);
//            viewHolder.examIcon.setImageDrawable(getResources().getDrawable(examIcon[icon]));
            viewHolder.department=(TextView)convertView.findViewById(R.id.img_second_headimg);
            viewHolder.department.setText(school);

            viewHolder.examName = (TextView) convertView.findViewById(R.id.department_name);

            viewHolder.examName.setText(depart.get(position).getItem());

            return convertView;
        }
    }
    private class ViewHolder {
        ImageView examIcon;
        TextView examName,department;
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
