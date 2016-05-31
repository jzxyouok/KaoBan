package com.dezhou.lsy.projectdezhoureal;

/**
 * Created by zy on 15-3-18.
 */

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import org.apache.http.NameValuePair;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kaobanxml.XmlParser;
import network.MyHttpClient;
import tempvalue.Exam;
import tempvalue.IfTest;
import tempvalue.MyExam;
import utils.URLSet;

/**
 * listview初始化的时候第一项不会变色，
 * 可能需要重写adapter
 */

public class AddExamActivity extends Activity implements View.OnClickListener {

    // 添加考试标签的ｕｒｌ
    private static final String urlAddExam = URLSet.serviceUrl+"/kaoban/addExam/";

    private static String urlDiploma = URLSet.serviceUrl+"/kaoban/diploma/";
    private static String urlOfficial = URLSet.serviceUrl+"/kaoban/official/";
    private static String urlHighExam = URLSet.serviceUrl+"/kaoban/highExam/";

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

    private String[] examTag = {"0/", "1/", "2/"}; //加标签是为了在查看我的考试的时候能确认标签是属于哪类考试
//
//    final int[] examIcon = {R.drawable.addexam_headimg, R.drawable.addexam_headimg1,
//            R.drawable.addexam_headimg2, R.drawable.addexam_headimg3, R.drawable.addexam_headimg4};

    TextView diplomaButton;
    TextView examButton;
    TextView officeButton;

    Button actionSureButton;

    EditText searchEditText;

    ImageButton backButton;

    ProgressDialog dialog;

    List<LinearLayout> buttonList;

//    Set<String> addExams = null; //记录添加的考试，最后点确定的时候一起提交

    private static List<NameValuePair> listExamAtten = new ArrayList<NameValuePair>();
    private List<String> myExamCopy = null;
    private List<String> examTagCopy = null;

    PopupWindow popupWindow = null;
    View popWindowContentView = null;


    ListView firstListView = null;
    ListView secondListView = null;

    private static Map<String, String> mapTmp = new HashMap<String, String>();

    private static Context context = null;
    private ImageView kaozheng,kaoyan,kaogongwuyuan;
    private LinearLayout lin_kaozheng,lin_kaoyan,lin_kaogongwuyuan;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            dialog.dismiss();
            if (msg.what == 0x237) {
                String tmp = msg.getData().getString("examTag");
                Log.d("TestApp", "....获取的数据为....." + tmp);
                if (tmp != null && !"".equals(tmp)) {
                    String content = XmlParser.xmlParserGet(tmp).toString().trim();
                    if ("success".equals(content)) {
                        Toast.makeText(context, "课程关注成功！\n您关注的课程是＋" + listExamAtten.toString(), Toast.LENGTH_SHORT).show();
                        finish();
                        if (IfTest.ifTest){
                            // Toast.makeText(context, "课程关注成功！\n您关注的课程是＋" + listExamAtten.toString(), Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    } else {
                        if (IfTest.ifTest){
                            Toast.makeText(context, "信息提交失败！", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    if (IfTest.ifTest){
                        Toast.makeText(context, "请求被重置！++" + tmp, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            //获取考证第一大分类
            if (msg.what == 0x240) {
                String tmp = msg.getData().getString("GetShuoshuo");
                Log.d("TestApp", "....获取的数据为....." + tmp);
                if (tmp != null && !"".equals(tmp)) {
                    //String content = XmlParser.xmlParserGet(tmp).toString().trim();
                    List<Exam> exams = XmlParser.xmlExamsGet(tmp);
                    if (exams != null) {
                        if (IfTest.ifTest){
                            Toast.makeText(context, "获取考证第一大类成功" + exams.toString(), Toast.LENGTH_SHORT).show();
                        }
                        setFirstListView(exams, 0);
                    } else {
                        if (IfTest.ifTest){
                            Toast.makeText(context, "说说获取失败！", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(context, "请求被重置！++" + tmp, Toast.LENGTH_SHORT).show();
                }
            }

            //获取考证第二类
            if (msg.what == 0x241) {
                String tmp = msg.getData().getString("GetShuoshuo");
                String secondExam=msg.getData().getString("secondExam");
                Log.d("TestApp", "....获取的数据为....." + tmp);
                if (tmp != null && !"".equals(tmp)) {
                    //String content = XmlParser.xmlParserGet(tmp).toString().trim();
                    List<Exam> exams = XmlParser.xmlExamsGet(tmp);
                    for (Exam e : exams) {
                        Log.d("cout", "exam : " + e.getExam());
                    }
                    if (exams != null) {
                        if (IfTest.ifTest){
                            Toast.makeText(context, "获取考证第二大类成功" + exams.toString(), Toast.LENGTH_SHORT).show();
                        }
                        setSecondListView(urlDiplomaContent, exams,secondExam, 0);
                    } else {
                        if (IfTest.ifTest){
                            Toast.makeText(context, "说说获取失败！", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    if (IfTest.ifTest){
                        Toast.makeText(context, "请求被重置！++" + tmp, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            //获取考研大类
            if (msg.what == 0x242) {
                String tmp = msg.getData().getString("GetShuoshuo");
                Log.d("TestApp", "....获取的数据为....." + tmp);
                if (tmp != null && !"".equals(tmp)) {
                    //String content = XmlParser.xmlParserGet(tmp).toString().trim();
                    List<Exam> exams = XmlParser.xmlExamsGet(tmp);
                    if (exams != null) {
                        if (IfTest.ifTest){
                            Toast.makeText(context, "获取考证第二大类成功" + exams.toString(), Toast.LENGTH_SHORT).show();
                        }
                        initPopUpWindow(true, exams);
                        showPopWindow();
                    } else {
                        Toast.makeText(context, "说说获取失败！", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "请求被重置！++" + tmp, Toast.LENGTH_SHORT).show();
                }
            }

            //获取考研第一大分类
            if (msg.what == 0x243) {
                String tmp = msg.getData().getString("GetShuoshuo");
                Log.d("TestApp", "....获取的数据为....." + tmp);
                if (tmp != null && !"".equals(tmp)) {
                    //String content = XmlParser.xmlParserGet(tmp).toString().trim();
                    List<Exam> exams = XmlParser.xmlExamsGet(tmp);
                    if (exams != null) {
                        if (IfTest.ifTest){
                            Toast.makeText(context, "获取考证第一大类成功" + exams.toString(), Toast.LENGTH_SHORT).show();
                        }
                        setFirstListView(exams, 1);
                    } else {
                        if (IfTest.ifTest){
                            Toast.makeText(context, "说说获取失败！", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    if (IfTest.ifTest){
                        Toast.makeText(context, "请求被重置！++" + tmp, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            //获取考研第二大分类
            if (msg.what == 0x244) {
                String tmp = msg.getData().getString("GetShuoshuo");
                String secondExam=msg.getData().getString("secondExam");
                Log.d("TestApp", "....获取的数据为....." + tmp);
                if (tmp != null && !"".equals(tmp)) {
                    //String content = XmlParser.xmlParserGet(tmp).toString().trim();
                    List<Exam> exams = XmlParser.xmlExamsGet(tmp);
                    if (exams != null) {
                        if (IfTest.ifTest){
                            Toast.makeText(context, "获取考证第二大类成功" + exams.toString(), Toast.LENGTH_SHORT).show();
                        }
                        setSecondListView(urlHighExamContent, exams,secondExam, 1);
                    } else {
                        if (IfTest.ifTest){
                            Toast.makeText(context, "说说获取失败！", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    if (IfTest.ifTest){
                        Toast.makeText(context, "请求被重置！++" + tmp, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            //获取考公务员第一大分类
            if (msg.what == 0x245) {
                String tmp = msg.getData().getString("GetShuoshuo");
                Log.d("TestApp", "....获取的数据为....." + tmp);
                if (tmp != null && !"".equals(tmp)) {
                    //String content = XmlParser.xmlParserGet(tmp).toString().trim();
                    List<Exam> exams = XmlParser.xmlExamsGet(tmp);
                    if (exams != null) {
                        if (IfTest.ifTest){
                            Toast.makeText(context, "获取考证第二大类成功" + exams.toString(), Toast.LENGTH_SHORT).show();
                        }
                        setFirstListView(exams, 2);
                    } else {
                        if (IfTest.ifTest){
                            Toast.makeText(context, "说说获取失败！", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    if (IfTest.ifTest){
                        Toast.makeText(context, "请求被重置！++" + tmp, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            //获取考公务员第二大类
            if (msg.what == 0x246) {
                String tmp = msg.getData().getString("GetShuoshuo");
                String secondExam=msg.getData().getString("secondExam");
                Log.d("TestApp", "....获取的数据为....." + tmp);
                if (tmp != null && !"".equals(tmp)) {
                    //String content = XmlParser.xmlParserGet(tmp).toString().trim();
                    List<Exam> exams = XmlParser.xmlExamsGet(tmp);
                    if (exams != null) {
                        if (IfTest.ifTest){
                            Toast.makeText(context, "获取考公务员第二大类成功" + exams.toString(), Toast.LENGTH_SHORT).show();
                        }
                        setSecondListView(urlOfficialContent, exams,secondExam, 2);
                    } else {
                        if (IfTest.ifTest){
                            Toast.makeText(context, "说说获取失败！", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    if (IfTest.ifTest){
                        Toast.makeText(context, "请求被重置！++" + tmp, Toast.LENGTH_SHORT).show();
                    }
                }
            }

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addexam);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }

        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.blue);
        context = this;

        initView();

        initActionBar();
    }


    private void initActionBar() {
        this.getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        this.getActionBar().setCustomView(R.layout.addexam_actionbar);

        backButton = (ImageButton) this.getActionBar().getCustomView().findViewById(R.id.img_button_addexam_back);
        backButton.setOnClickListener(this);

        actionSureButton = (Button) this.getActionBar().getCustomView().findViewById(R.id.addexam_actionbar_sure);
        actionSureButton.setOnClickListener(this);
    }

    private void initView() {
        kaozheng=(ImageView)this.findViewById(R.id.kaozheng);
        kaoyan=(ImageView)this.findViewById(R.id.kaoyan);
        kaogongwuyuan=(ImageView)this.findViewById(R.id.gongwuyuan);

        lin_kaozheng=(LinearLayout)this.findViewById(R.id.lin_kaozheng);
        lin_kaozheng.setOnClickListener(this);
        lin_kaoyan=(LinearLayout)this.findViewById(R.id.lin_kaoyan);
        lin_kaoyan.setOnClickListener(this);
        lin_kaogongwuyuan=(LinearLayout)this.findViewById(R.id.lin_gongwuyuan);
        lin_kaogongwuyuan.setOnClickListener(this);
        dialog = new ProgressDialog(this);

        diplomaButton = (TextView) this.findViewById(R.id.button_diploma);
        //iplomaButton.setOnClickListener(this);

        examButton = (TextView) this.findViewById(R.id.button_exam);
        //examButton.setOnClickListener(this);

        officeButton = (TextView) this.findViewById(R.id.button_office);
        //officeButton.setOnClickListener(this);

        searchEditText = (EditText) this.findViewById(R.id.addexam_search);
        searchEditText.setOnClickListener(this);

        buttonList = new ArrayList<LinearLayout>();
        buttonList.add(lin_kaozheng);
        buttonList.add(lin_kaoyan);
        buttonList.add(lin_kaogongwuyuan);

        firstListView = (ListView) this.findViewById(R.id.listview_first);
        secondListView = (ListView) this.findViewById(R.id.listview_second);

        initListView();

        initPopUpWindow(false, null);
    }


    private void initPopUpWindow(boolean haveMes, List<Exam> exams) {

        if (popWindowContentView == null) {
            popWindowContentView = LayoutInflater.from(this).inflate(R.layout.popupwindow, null);
        }


        //有信息的时候更新popupwindow
        if (haveMes) {
            LinearLayout linearLayout = (LinearLayout) popWindowContentView.findViewById(R.id.popwindow_linear);
            Log.d("cout", "popupwindow child num  " + linearLayout.getChildCount());

            if (exams != null && linearLayout.getChildCount() <= 1) {

                //移除显示没有更多信息的textview
                TextView nomoreTextView = (TextView) popWindowContentView.findViewById(R.id.popwindow_nomoremes);
                if (nomoreTextView != null) {
                    linearLayout.removeView(nomoreTextView);
                }

                //添加显示exam的textview
                for (final Exam exam : exams) {
                    TextView textView = new TextView(context);
                    textView.setText(exam.getExam());
                    textView.setTextSize(15);
                    textView.setGravity(Gravity.CENTER);
                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d("cout", "highexam click.......................");
                            mapTmp.clear();
                            mapTmp.put("big", exam.getExam());
                            int msgWhat = 0x243;
                            netWorkThread(urlHighExam, mapTmp, msgWhat);
                        }
                    });
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParams.topMargin = 10;
                    linearLayout.addView(textView, layoutParams);
                }

                popupWindow.dismiss();
                popupWindow.setContentView(popWindowContentView);
            }
        }

        if (popupWindow == null) {
            popupWindow = new PopupWindow(popWindowContentView);
            popupWindow.setBackgroundDrawable(new BitmapDrawable());
            popupWindow.setFocusable(true);
            popupWindow.setOutsideTouchable(true);
        }

    }


    private void showPopWindow() {
        popupWindow.setWidth(lin_kaoyan.getWidth());
        popupWindow.setHeight(ActionBar.LayoutParams.WRAP_CONTENT);

        popupWindow.showAsDropDown(lin_kaoyan);

        popupWindow.update();
    }

    private void initListView() {
//        List<Map<String, Object>> list = setList(firstList);
//        SimpleAdapter simpleAdapter = new SimpleAdapter(this, list, R.layout.addexam_first_item,
//                new String[]{"name"}, new int[]{R.id.textview_first_name});
//        firstListView.setAdapter(simpleAdapter);
//
////        setSecondListView(0);
//        setListViewClick();

        firstListViewNetWork(0);

    }

    private void setSecondListView(final String url, final List<Exam> exams, final String secondExam,final int examType) {

        ExamAdapter examAdapter = new ExamAdapter(this, exams,secondExam, examType);
        Log.d("cout", "setSecondListView");
        secondListView.setAdapter(examAdapter);
        secondListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String exam = exams.get(position).getExam();
                if (examType == 1) {
                    startSchoolList(exam);
                    return;
                }
                startReadExam(url, exam, examType);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lin_kaozheng:
                firstListViewNetWork(0);
                setButtonBackground(0);
                officeButton.setTextColor(getResources().getColor(R.color.dark));
                examButton.setTextColor(getResources().getColor(R.color.dark));
                diplomaButton.setTextColor(getResources().getColor(R.color.whites));
                kaoyan.setBackgroundResource(R.drawable.kaoyan1);
                kaozheng.setBackgroundResource(R.drawable.kaozheng);
                kaogongwuyuan.setBackgroundResource(R.drawable.gongwuyuan1);
                break;

            case R.id.lin_kaoyan:
                firstListViewNetWork(1);
                showPopWindow();
                setButtonBackground(1);
                officeButton.setTextColor(getResources().getColor(R.color.dark));
                examButton.setTextColor(getResources().getColor(R.color.whites));
                diplomaButton.setTextColor(getResources().getColor(R.color.dark));
                kaogongwuyuan.setBackgroundResource(R.drawable.gongwuyuan1);
                kaoyan.setBackgroundResource(R.drawable.kaoyan);
                kaozheng.setBackgroundResource(R.drawable.kaozheng1);
                break;

            case R.id.lin_gongwuyuan:
                firstListViewNetWork(2);
                setButtonBackground(2);
                officeButton.setTextColor(getResources().getColor(R.color.whites));
                examButton.setTextColor(getResources().getColor(R.color.dark));
                diplomaButton.setTextColor(getResources().getColor(R.color.dark));
                kaogongwuyuan.setBackgroundResource(R.drawable.gongwuyuan);
                kaoyan.setBackgroundResource(R.drawable.kaoyan1);
                kaozheng.setBackgroundResource(R.drawable.kaozheng1);
                break;

            case R.id.addexam_search:
                search();
                break;

            case R.id.img_button_addexam_back:
                this.finish();
                break;

            case R.id.addexam_actionbar_sure:
                dialog.setMessage("正在提交");
                dialog.show();
                handUpExams();
                break;
        }
    }

    void search() {
        Intent intent = new Intent(this, AddExamSearchActivity.class);
        startActivity(intent);

    }

    /**
     * 0------------考证
     * 1------------考研
     * 2------------考公务员
     * <p/>
     * 0x240----------获取考证第一大分类
     * 0x241----------获取考证第二大分类
     * 0x242----------获取考研大类
     * 0x243----------获取考研第一分类
     * 0x244----------获取考研第二分类
     * 0x245----------获取考公务员第一大分类
     * 0x246----------获取考公务员第二大分类
     *
     * @param examType
     */
    private void firstListViewNetWork(int examType) {
        switch (examType) {
            case 0:   //获取考证
                mapTmp.clear();
                mapTmp.put("all", "1"); //获取大类，提交的时候是不带参数的，这里提交了all，服务器是不接受的
                netWorkThread(urlDiploma, mapTmp, 0x240);
                break;
            case 1:  //获取考研
                mapTmp.clear();
                mapTmp.put("all", "1");  //获取大类
                netWorkThread(urlHighExam, mapTmp, 0x242);
                break;
            case 2:  //获取考公务员
                mapTmp.clear();
                mapTmp.put("all", "1"); //获取大类
                netWorkThread(urlOfficial, mapTmp, 0x245);
                break;
        }

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
                bundle.putString("secondExam",mapTmp.values().toString());
                bundle.putString("GetShuoshuo", tmpString);
                msg.setData(bundle);
                handler.sendMessage(msg);
            }
        }).start();
    }


    private void setFirstListView(final List<Exam> exams, final int examType) {
        List<Map<String, Object>> list = setList(exams);
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, list, R.layout.addexam_first_item,
                new String[]{"name"}, new int[]{R.id.textview_first_name});

        firstListView.setAdapter(simpleAdapter);

        firstListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                for (int i = 0; i < parent.getChildCount(); i++) {
                    Log.d("cout", "child num" + i);
                    Log.d("cout", "allNum" + parent.getChildCount());
                    parent.getChildAt(i).
                            setBackgroundColor(Color.TRANSPARENT);
                }

                view.setBackgroundColor(getResources().getColor(R.color.blue));

                String exam = exams.get(position).getExam();

                String url = "";
                int msgWhat = 0;

                if (examType == 0) {
                    url = urlDiploma;
                    msgWhat = 0x241;
                } else if (examType == 1) {
                    url = urlHighExam;
                    msgWhat = 0x244;
                } else if (examType == 2) {
                    url = urlOfficial;
                    msgWhat = 0x246;
                }


                mapTmp.clear();
                mapTmp.put("first", exam);

                netWorkThread(url, mapTmp, msgWhat);

                if (IfTest.ifTest){
                    Toast.makeText(AddExamActivity.this, exam, Toast.LENGTH_SHORT).show();
                }

            }
        });

    }


    private void setButtonBackground(int pos) {

        for (int i = 0; i < buttonList.size(); i++) {
            if (i == pos) {
                buttonList.get(i).setBackgroundColor(getResources().getColor(R.color.blue));
                continue;
            }

            buttonList.get(i).setBackgroundColor(getResources().getColor(R.color.eback_white));

        }
    }

    private void startReadExam(String url, String second, int examType) {
        Intent intent = new Intent(this, ReadExamActivity.class);
        intent.putExtra("second", second);
        intent.putExtra("url", url);
        intent.putExtra("examType", examType);
        startActivity(intent);
    }

    private void startSchoolList(String second) {
        Intent intent = new Intent(this, AddExamHighSchoolActivity.class);
        intent.putExtra("second", second);
        startActivity(intent);
    }


    private void handUpExams() {

        MyExam.myExams = myExamCopy;
        MyExam.examTag = examTagCopy;

        if (MyExam.myExams == null || MyExam.myExams.isEmpty()) {
            return;
        }

        MyExam.fresh = true;

        String tags = "";

        String[] exams = new String[MyExam.myExams.size()];
        MyExam.myExams.toArray(exams);

        if (MyExam.exams == null){
            MyExam.exams = new ArrayList<>();
        }else {
            MyExam.exams.clear();
        }

        for (int i = 0; i < MyExam.myExams.size(); i++) {
            tags += exams[i];
            if (i < MyExam.myExams.size() - 1) {
                tags += ',';
            }
            MyExam.exams.add(new Exam(exams[i]));
        }

        if (IfTest.ifTest){
           // Toast.makeText(this, tags, Toast.LENGTH_SHORT).show();
        }

        examThread(tags);
    }


    private void examThread(String tags) {
        NameValuePair npTmp1 = new BasicNameValuePair("tag", tags);
        listExamAtten.add(npTmp1);
        // 测试添加考试标签的功能
        new Thread(new Runnable() {
            @Override
            public void run() {
                DefaultHttpClient client = MyHttpClient.getHttpClient();
                String tmpString = MyHttpClient.doPost(client, urlAddExam, listExamAtten, false);
                Message msg = new Message();
                msg.what = 0x237;
                Bundle bundle = new Bundle();
                bundle.putString("examTag", tmpString);
                msg.setData(bundle);
                handler.sendMessage(msg);
            }
        }).start();
    }


    /**
     * 自定义adapter，实现添加考试按钮的监听事件
     */
    private class ExamAdapter extends BaseAdapter {

        private List<Exam> exams = null;
        private LayoutInflater inflater;
        private int examType = 0;
        private String secondExam;
        public ExamAdapter(Context context, List<Exam> exams, String secondExam,int examType) {
            this.exams = exams;
            this.secondExam=secondExam;
            this.examType = examType;
            inflater = LayoutInflater.from(context);
            Log.d("cout", "ExamAdapter");
        }

        @Override
        public int getCount() {
            return exams.size();
        }

        @Override
        public Object getItem(int position) {
            return exams.get(position).getExam();
        }

        @Override
        public long getItemId(int position) {
            Log.d("cout", "getItemId  " + position);
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = this.inflater.inflate(R.layout.addexam_second_item, null);
                AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
//                layoutParams.height = 100;
                convertView.setLayoutParams(layoutParams);

            }

            final ViewHolder viewHolder = new ViewHolder();

//            viewHolder.examIcon = (ImageView) convertView.findViewById(R.id.img_second_headimg);
//
//            Random random = new Random();
//            int icon = random.nextInt(examIcon.length);
//            viewHolder.examIcon.setImageDrawable(getResources().getDrawable(examIcon[icon]));

            viewHolder.largeEXam=(TextView)convertView.findViewById(R.id.img_second_headimg);
            String second_exam=removeNum(secondExam);
            Matcher m = Pattern.compile("\\[([^\\[\\]]+)\\]").matcher(second_exam);
            while (m.find()){
                viewHolder.largeEXam.setText(m.group(1));
            }


            viewHolder.examName = (TextView) convertView.findViewById(R.id.textview_second_name);

            viewHolder.examName.setText(exams.get(position).getExam());

            viewHolder.addExam = (ImageView) convertView.findViewById(R.id.img_second_add);

            if (myExamCopy == null) {
                myExamCopy = new ArrayList<>();
                for (String s : MyExam.myExams) {
                    myExamCopy.add(s);
                }
            }
            if (examTagCopy == null) {
                examTagCopy = new ArrayList<>();
                for (String s : MyExam.examTag) {
                    examTagCopy.add(s);
                }
            }


            final String exam = examTag[examType] + exams.get(position).getExam();
            final String tag = examTag[examType];

            if (myExamCopy.contains(exam)) {
                viewHolder.addExam.setImageDrawable(getResources().getDrawable(R.drawable.addexam_done));
            } else {
                viewHolder.addExam.setImageDrawable(getResources().getDrawable(R.drawable.type_select_btn_nor));
            }

            if (examType == 1){
                viewHolder.addExam.setImageBitmap(null);
                return convertView;
            }

            viewHolder.addExam.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!myExamCopy.contains(exam)) {
                        viewHolder.addExam.setImageDrawable(getResources().getDrawable(R.drawable.addexam_done));
//                        addExams.add(exams.get(position).getExam());
                        myExamCopy.add(exam);
                        examTagCopy.add(tag);
                        Log.d("cout", "myexamcopy" + myExamCopy.toString());
                        Log.d("cout", "examcopy" + MyExam.myExams.toString());
                    } else {
                        viewHolder.addExam.setImageDrawable(getResources().getDrawable(R.drawable.type_select_btn_nor));
//                        ((ImageView) v).setImageDrawable(getResources().getDrawable(R.drawable.addexam_add));
//                        addExams.remove(exams.get(position).getExam());
                        myExamCopy.remove(exam);
                        examTagCopy.remove(tag);
                    }

                }
            });

            return convertView;
        }
    }

    private class ViewHolder {

        TextView examName,largeEXam;
        ImageView addExam;
    }

    public static String removeNum(String str) {
        String regEx = "[0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
//替换与模式匹配的所有字符（即数字的字符将被""替换）
        return m.replaceAll("").trim();
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