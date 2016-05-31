package kongjian.demo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.com.easemob.chatuidemo.DemoApplication;
import com.dezhou.lsy.projectdezhoureal.MainActivity;
import com.dezhou.lsy.projectdezhoureal.R;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import org.apache.http.NameValuePair;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaobanxml.XmlParser;
import kongjian.EduActivity;
import kongjian.pojo.School;
import network.MyHttpClient;

public class SchoolActivity extends Activity {
    private ProgressDialog pdialog;
    private static final String url="http://115.28.35.2:8802/kaoban/changeInf/";
    private List<NameValuePair> listArtical;
    private ProgressDialog pro;
    private String sh_school="";
    private String sch="";

    private TextView title;
    private DemoApplication app;
    private String provice;
    public Handler handler=new Handler(){
        public void handleMessage(Message msg){
            switch (msg.what){

                case 0x237:
                    Bundle bundle2=new Bundle();
                    bundle2=msg.getData();
                    String sendArticalXml=bundle2.getString("schoolResult");
                    String result= XmlParser.xmlResultGet(sendArticalXml);
                    pro.dismiss();
                    if(result.equals("success")){
                        if (sch.toString().trim().equals("edu")) {

                            Intent schoolintent = new Intent(SchoolActivity.this, EduActivity.class);
                            schoolintent.putExtra("school", sh_school);
                            startActivity(schoolintent);
                            finish();
                        }
                        else{
                            Intent schoolintent = new Intent(SchoolActivity.this, MainActivity.class);
                            startActivity(schoolintent);
                            finish();
                        }
                    }
                    else{
                        Toast.makeText(SchoolActivity.this,"更新失败",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mian);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }

        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.blue);
        sch=getIntent().getStringExtra("city");
        app=(DemoApplication)getApplicationContext();
        provice=app.getProvice();
        title=(TextView)findViewById(R.id.title_school);
        title.setText("学校");


        ArrayList<School> list_school = (ArrayList<School>) getIntent()
                .getExtras().getSerializable("list_school");
        ListView lv_provance = (ListView) findViewById(R.id.listview);
        List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
        String[] from = { "sh_school" };
        int[] to = { R.id.tv_item };
        for (School school : list_school) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("sh_id", school.getSh_id());
            map.put("sh_city", school.getSh_city());
            map.put("sh_school", school.getSh_shool());
            data.add(map);
        }
        SimpleAdapter adapter = new SimpleAdapter(SchoolActivity.this, data,
                R.layout.listview_item, from, to);
        lv_provance.setAdapter(adapter);
        lv_provance.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                ListView lv = (ListView) parent;
                HashMap<String, Object> itemAtPosition = (HashMap<String, Object>) lv
                        .getItemAtPosition(position);

                sh_school = (String) itemAtPosition.get("sh_school");
                Toast.makeText(getApplicationContext(), sh_school, Toast.LENGTH_LONG).show();
                String school_provice=provice+" "+sh_school;
                app.setProvice(school_provice);
                listArtical = new ArrayList<NameValuePair>();

                NameValuePair npTmp3 = new BasicNameValuePair("school", school_provice);

                listArtical.add(npTmp3);
                pro=new ProgressDialog(SchoolActivity.this);
                pro.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                pro.setTitle("请稍后");
                pro.setMessage("正在更新...");
                pro.setCancelable(true);
                pro.setCanceledOnTouchOutside(false);
                pro.show();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        DefaultHttpClient client = MyHttpClient.getHttpClient();

                        String tmpString = MyHttpClient.doPost(client, url, listArtical, true);
                        Message msg = new Message();
                        msg.what = 0x237;
                        Bundle bundle = new Bundle();
                        bundle.putString("schoolResult", tmpString);
                        msg.setData(bundle);
                        handler.sendMessage(msg);
                    }
                }).start();


            }
        });
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
