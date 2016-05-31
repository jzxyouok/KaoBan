package com.dezhou.lsy.projectdezhoureal;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import org.apache.http.impl.client.DefaultHttpClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaobanxml.XmlParser;
import network.MyHttpClient;
import tempvalue.Exam;
import tempvalue.IfTest;
import utils.URLSet;

/**
 * Created by zy on 15-4-29.
 */
public class AddExamSearchActivity extends Activity implements View.OnClickListener {

    private static String urlDiploma = URLSet.serviceUrl+"/kaoban/diploma/search/";
    private static String urlOfficial = URLSet.serviceUrl+"/kaoban/official/search/";
    private static String urlHighExam = URLSet.serviceUrl+"/kaoban/highExam/search/";

    //  获取公务员考试的具体内容
    private static String urlOfficialContent = URLSet.serviceUrl+"/kaoban/official/content/";
    // 获取证书考试的具体内容  115.28.35.2:8801/kaoban/diploma/guide/
    private static String urlDiplomaContent = URLSet.serviceUrl+"/kaoban/diploma/content/";
    // 获取证书考试大纲的具体内容
    private static String urlDiplomaGuide = URLSet.serviceUrl+"/kaoban/diploma/guide/";
    // 获取证书考前准备的具体内容
    private static String urlBeforeExamGuide = URLSet.serviceUrl+"/kaoban/diploma/beforeExam/";
    // 获取考研学校的具体内容
    private static String urlHighExamContent = URLSet.serviceUrl+"/kaoban/highExam/content/";


    private static String searchText = "搜索:";
    private String sendSearch;
    private String curUrl;

    private EditText searchEditText = null;
    private TextWatcher textWatcher = null;

    private Button diplomaButton = null;
    private Button highExamButton = null;
    private Button officialButton = null;

    private TextView noresultTextView = null;

    private ListView listView = null;
    private SimpleAdapter simpleAdapter = null;

    private static Map<String, String> mapTmp = new HashMap<String, String>();

    private int examType = 0;

    private ImageView back;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.what == 0x240) {
                String tmp = msg.getData().getString("GetShuoshuo");
                Log.d("TestApp", "....获取的数据为....." + tmp);
                if (tmp != null && !"".equals(tmp)) {
                    List<Exam> exams = XmlParser.xmlExamsGet(tmp);
                    if (exams != null) {
                        if (IfTest.ifTest){
                            Toast.makeText(AddExamSearchActivity.this, "获取搜索成功" + exams.toString(), Toast.LENGTH_SHORT).show();
                        }
                        setListView(exams);
                        if (exams.isEmpty()){
                            setNoResultTextViewGone(false);
                        }else{
                            setNoResultTextViewGone(true);
                        }
                    } else {
                        if (IfTest.ifTest){
                            Toast.makeText(AddExamSearchActivity.this, "信息提交失败！", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    if (IfTest.ifTest){
                        Toast.makeText(AddExamSearchActivity.this, "请求被重置！++" + tmp, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addexam_search);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }

        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.blue);
        initView();

    }

    private void initView() {
        back=(ImageView)this.findViewById(R.id.back_sousuo);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        diplomaButton = (Button) this.findViewById(R.id.addexam_search_diplomabutton);
        diplomaButton.setOnClickListener(this);

        highExamButton = (Button) this.findViewById(R.id.addexam_search_highexambutton);
        highExamButton.setOnClickListener(this);

        officialButton = (Button) this.findViewById(R.id.addexam_search_officialbutton);
        officialButton.setOnClickListener(this);

        noresultTextView = (TextView) this.findViewById(R.id.addexam_search_noresult);

        textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sendSearch = s.toString();
                if (s.length() <= 0) {
                    setButtonVisibility(true);
                } else {
                    setButtonText(s.toString());
                    setButtonVisibility(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        searchEditText = (EditText) this.findViewById(R.id.addexam_search);
        searchEditText.addTextChangedListener(textWatcher);


        listView = (ListView) this.findViewById(R.id.addexam_search_listview);
    }

    private void setListView(List<Exam> exams) {
        List<Map<String, Object>> examData = setList(exams);

        simpleAdapter = new SimpleAdapter(this, examData, R.layout.addexam_search_list_item,
                new String[]{"name"}, new int[]{R.id.addexam_search_item_textview});

        setListViewGone(false);
        listView.setAdapter(simpleAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView) view.findViewById(R.id.addexam_search_item_textview);
                startReadExam(curUrl, textView.getText().toString(), examType);
            }
        });
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

    private void setButtonText(String text) {
        if (diplomaButton == null) {
            diplomaButton = (Button) this.findViewById(R.id.addexam_search_diplomabutton);
            diplomaButton.setOnClickListener(this);
        }
        if (highExamButton == null) {
            highExamButton = (Button) this.findViewById(R.id.addexam_search_highexambutton);
            highExamButton.setOnClickListener(this);
        }
        if (officialButton == null) {
            officialButton = (Button) this.findViewById(R.id.addexam_search_officialbutton);
            officialButton.setOnClickListener(this);
        }

        diplomaButton.setText(searchText + "考证   " + text);
        highExamButton.setText(searchText + "考研   " + text);
        officialButton.setText(searchText + "考公务员 " + text);

    }

    private void setButtonVisibility(boolean gone) {
        if (gone) {
            diplomaButton.setVisibility(View.GONE);
            highExamButton.setVisibility(View.GONE);
            officialButton.setVisibility(View.GONE);
        } else {
            diplomaButton.setVisibility(View.VISIBLE);
            highExamButton.setVisibility(View.VISIBLE);
            officialButton.setVisibility(View.VISIBLE);

            setListViewGone(true);

            setNoResultTextViewGone(true);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addexam_search_diplomabutton:
                buttonClick(urlDiploma, 0);
                setButtonVisibility(true);
                break;
            case R.id.addexam_search_highexambutton:
                buttonClick(urlHighExam, 1);
                setButtonVisibility(true);
                break;
            case R.id.addexam_search_officialbutton:
                buttonClick(urlOfficial, 2);
                setButtonVisibility(true);
                break;
        }

    }

    private void buttonClick(String url, int examType) {
        mapTmp.clear();
        mapTmp.put("search", sendSearch);
        this.examType = examType;

        switch (examType) {
            case 0: //考证
                curUrl = urlDiplomaContent;
                break;
            case 1: //考研
                curUrl = urlHighExamContent;
                break;
            case 2: //考公务员
                curUrl = urlOfficialContent;
                break;
        }

        netWorkThread(url, mapTmp, 0x240);
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


    private void startReadExam(String url, String second, int examType) {
        Intent intent = new Intent(this, ReadExamActivity.class);
        intent.putExtra("second", second);
        intent.putExtra("url", url);
        intent.putExtra("examType", examType);
        startActivity(intent);
    }

    private void setNoResultTextViewGone(boolean gone){
        if (gone){
            noresultTextView.setVisibility(View.GONE);
        }else{
            noresultTextView.setVisibility(View.VISIBLE);
        }

    }

    private void setListViewGone(boolean gone){
        if (gone){
            listView.setVisibility(View.GONE);
        }else{
            listView.setVisibility(View.VISIBLE);
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
