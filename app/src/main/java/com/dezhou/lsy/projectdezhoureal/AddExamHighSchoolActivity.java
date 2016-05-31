package com.dezhou.lsy.projectdezhoureal;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
 * Created by zy on 15-4-25.
 */
public class AddExamHighSchoolActivity extends Activity{

    private static String urlHighExamSchool = URLSet.serviceUrl+"/kaoban/highExam/school/";

    // 获取考研学校的具体内容
    private static String urlHighExamContent = URLSet.serviceUrl+"/kaoban/highExam/content/";

    // 添加考试
    private static final String urlAddExam = URLSet.serviceUrl+"/kaoban/addExam/";

    private static String second = null;

    ListView schoolListView = null;

    private static List<NameValuePair> listExamAtten = new ArrayList<NameValuePair>();

    private static Map<String, String> mapTmp = new HashMap<String, String>();
//    final int[] examIcon = { R.drawable.addexam_headimg1,
//            R.drawable.addexam_headimg2, R.drawable.addexam_headimg3, R.drawable.addexam_headimg4};

    private List<String> myExamCopy = null;
    private List<String> examTagCopy = null;

    private String[] examTag = {"0/", "1/", "2/"}; //加标签是为了在查看我的考试的时候能确认标签是属于哪类考试

    private TextView sureSend;
    private static Context context = null;

    private ProgressDialog dialog;
    private ImageView back;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            dialog.dismiss();
            //获取考证第一大分类
            if (msg.what == 0x247) {
                String tmp = msg.getData().getString("GetShuoshuo");
                Log.d("TestApp", "....获取的数据为....." + tmp);
                if (tmp != null && !"".equals(tmp)) {
                    //String content = XmlParser.xmlParserGet(tmp).toString().trim();
                    List<Exam> exams = XmlParser.xmlExamsGet(tmp);
                    if (exams != null) {
                        if (IfTest.ifTest){
                            Toast.makeText(AddExamHighSchoolActivity.this, "获取考证第一大类成功" + exams.toString(), Toast.LENGTH_SHORT).show();
                        }
                        setSecondListView(urlHighExamSchool, exams, 1);
                    } else {
                        if (IfTest.ifTest){
                            Toast.makeText(AddExamHighSchoolActivity.this, "说说获取失败！", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    if (IfTest.ifTest){
                        Toast.makeText(AddExamHighSchoolActivity.this, "请求被重置！++" + tmp, Toast.LENGTH_SHORT).show();
                    }
                }
            }
            if (msg.what == 0x237) {
                String tmp = msg.getData().getString("examTag");
                Log.d("TestApp", "....获取的数据为....." + tmp);
                if (tmp != null && !"".equals(tmp)) {
                    String content = XmlParser.xmlParserGet(tmp).toString().trim();
                    if ("success".equals(content)) {
                        Toast.makeText(context, "课程关注成功！\n您关注的课程是＋" + listExamAtten.toString(), Toast.LENGTH_SHORT).show();
                        finish();
                        if (IfTest.ifTest){
                            Toast.makeText(context, "课程关注成功！\n您关注的课程是＋" + listExamAtten.toString(), Toast.LENGTH_SHORT).show();
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
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addexam_highschool);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }

        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.blue);
        context = this;
        second = this.getIntent().getStringExtra("second");

        initNetWork();
        initView();
    }

    private void initNetWork() {

        mapTmp.clear();
        mapTmp.put("second", second);

        int msgWhat = 0x247;

        netWorkThread(urlHighExamSchool, mapTmp, msgWhat);

    }

    private void initView() {
        dialog = new ProgressDialog(this);
        schoolListView = (ListView) this.findViewById(R.id.readexam_listview);
        sureSend = (TextView)findViewById(R.id.schoolSure);
        back=(ImageView)findViewById(R.id.back_SignActivity);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        sureSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.setMessage("正在提交");
                dialog.show();
                handUpExams();
            }


        });
    }
    private void handUpExams(){
        MyExam.myExams = myExamCopy;
        MyExam.examTag = examTagCopy;

        if (MyExam.myExams == null || MyExam.myExams.isEmpty()) {
            return;
        }

        MyExam.fresh = true;

        String tags = "";

        String[] exams = new String[MyExam.myExams.size()];
        MyExam.myExams.toArray(exams);
        for (int i = 0; i < MyExam.myExams.size(); i++) {
            tags += exams[i];
            if (i < MyExam.myExams.size() - 1) {
                tags += ',';
            }
        }

        if (IfTest.ifTest){
            Toast.makeText(this, tags, Toast.LENGTH_SHORT).show();
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
                String tmpString = MyHttpClient.doPost(client, urlAddExam, listExamAtten, true);
                Message msg = new Message();
                msg.what = 0x237;
                Bundle bundle = new Bundle();
                bundle.putString("examTag", tmpString);
                msg.setData(bundle);
                handler.sendMessage(msg);
            }
        }).start();
    }

    private void setSecondListView(final String url, final List<Exam> exams, final int examType) {

        ExamAdapter examAdapter = new ExamAdapter(this, exams, examType);
        Log.d("cout", "setSecondListView");
        schoolListView.setAdapter(examAdapter);
        schoolListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String exam = exams.get(position).getExam();
                startReadExam(urlHighExamContent,exams.get(position).getExam(),1);
            }
        });

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

    private void startReadExam(String url, String second,int examType) {
        Intent intent = new Intent(this, ReadExamActivity.class);
        intent.putExtra("second", second);
        intent.putExtra("url", url);
        intent.putExtra("examType",examType);
        startActivity(intent);
    }
    /**
     * 自定义adapter，实现添加考试按钮的监听事件
     */
    private class ExamAdapter extends BaseAdapter {

        private List<Exam> exams = null;
        private LayoutInflater inflater;
        private int examType = 0;

        public ExamAdapter(Context context, List<Exam> exams, int examType) {
            this.exams = exams;
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
              //  layoutParams.height = 100;
                convertView.setLayoutParams(layoutParams);
            }

            final ViewHolder viewHolder = new ViewHolder();

//            viewHolder.examIcon = (ImageView) convertView.findViewById(R.id.img_second_headimg);
//             Random random = new Random();
//            int icon = random.nextInt(examIcon.length);
//            viewHolder.examIcon.setImageDrawable(getResources().getDrawable(examIcon[icon]));

            viewHolder.examName = (TextView) convertView.findViewById(R.id.textview_second_name);

            viewHolder.examName.setText(exams.get(position).getExam());

            viewHolder.addExam = (ImageView) convertView.findViewById(R.id.img_second_add);

            viewHolder.highschool=(TextView)convertView.findViewById(R.id.img_second_headimg);
            String highschool=removeNum(second);
            viewHolder.highschool.setText(highschool);

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

            final String exam = examTag[examType] + second + ";" + exams.get(position).getExam();
            final String tag = examTag[examType];

            if (myExamCopy.contains(exam)) {
                viewHolder.addExam.setImageDrawable(getResources().getDrawable(R.drawable.addexam_done));
            } else {
                viewHolder.addExam.setImageDrawable(getResources().getDrawable(R.drawable.type_select_btn_nor));
            }

            viewHolder.addExam.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!myExamCopy.contains(exam)) {
                        viewHolder.addExam.setImageDrawable(getResources().getDrawable(R.drawable.addexam_done));
                        myExamCopy.add(exam);
                        examTagCopy.add(tag);
                    } else {
                        viewHolder.addExam.setImageDrawable(getResources().getDrawable(R.drawable.type_select_btn_nor));
                        myExamCopy.remove(exam);
                        examTagCopy.remove(tag);
                    }

                }
            });

            return convertView;
        }
    }
    private class ViewHolder {
        ImageView examIcon;
        TextView examName,highschool;
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
