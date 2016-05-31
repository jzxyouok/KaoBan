package fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.Lc.PullToRefreshLayout;
import com.Lc.PullableListView;
import com.dezhou.lsy.projectdezhoureal.AddExamHighSchoolActivity;
import com.dezhou.lsy.projectdezhoureal.GradeActivity;
import com.dezhou.lsy.projectdezhoureal.MainActivity;
import com.dezhou.lsy.projectdezhoureal.R;
import com.dezhou.lsy.projectdezhoureal.ReadExamActivity;
import com.dezhou.lsy.projectdezhoureal.ReadExamItemActivity;
import com.dezhou.lsy.projectdezhoureal.ZyWebViewActivity;

import org.apache.http.impl.client.DefaultHttpClient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaobanxml.XmlParser;
import network.MyHttpClient;
import tempvalue.Exam;
import tempvalue.IfTest;
import tempvalue.MyExam;
import tempvalue.UserNum;
import utils.URLSet;
import view.ExamTextView;

/**
 *
 */
public class ExamFragment extends Fragment {
    //获取考试标签
    private static String urlGetExam = URLSet.serviceUrl+"/kaoban/getExam/";
    // 添加考试标签的ｕｒｌ
    private static final String urlAddExam = URLSet.serviceUrl+"/kaoban/addExam/";
    private static final String urlDeleteExam = URLSet.serviceUrl+"/kaoban/removeExam/";

    private static String urlGetFirst = URLSet.serviceUrl+"/kaoban/getFirst/";

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

    final int[] examIcon = {R.drawable.addexam_headimg, R.drawable.addexam_headimg,
            R.drawable.addexam_headimg, R.drawable.addexam_headimg, R.drawable.addexam_headimg};

    private ListView myexamListView = null;

    private static Map<String, String> mapTmp = new HashMap<String, String>();

    private List<Exam> newExams;

    private ProgressDialog dialog;

    private Button gradeButton;

    private PullableListView pullableListView;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            dialog.dismiss();
            if (msg.what == 0x241) {
                String tmp = msg.getData().getString("GetShuoshuo");
                Log.d("TestApp", "....获取的数据为....." + tmp);
                if (tmp != null && !"".equals(tmp)) {
                    List<Exam> exams = XmlParser.xmlMyExamsGet(tmp);
                    if (exams != null) {
                        UserNum.exams=exams;
                        setListView(exams);
                        if (IfTest.ifTest) {
                            Toast.makeText(getActivity(), "获取考试成功" + exams.toString(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (IfTest.ifTest) {
                            Toast.makeText(getActivity(), "信息提交失败！", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    if (IfTest.ifTest) {
                        Toast.makeText(getActivity(), "请求被重置！++" + tmp, Toast.LENGTH_SHORT).show();
                    }
                }
            }else if (msg.what == 0x237){
                initNetWork();
            }
        }
    };

    public ExamFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_exam, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        if(MainActivity.isNetworkConnected(getActivity())) {
            initNetWork();
        }else {
            Toast.makeText(getActivity(),"当前网络无连接",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void onResume() {
        super.onResume();
//        setListView(setString2ListExam(MyExam.myExams));
//        setListView(MyExam.exams);
        if (MyExam.fresh){
            initNetWork();
        }
        MyExam.fresh = false;
    }

    private void initView() {
        dialog = new ProgressDialog(this.getActivity());

        gradeButton = (Button) this.getView().findViewById(R.id.button_grade);
        gradeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), GradeActivity.class);
                startActivity(intent);
            }
        });

        ((PullToRefreshLayout)this.getView().findViewById(R.id.refresh_view))
                .setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {

                    @Override
                    public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {
                        // TODO Auto-generated method stub
                        // ����ˢ�²���
                        new Handler() {
                            @Override
                            public void handleMessage(Message msg) {
                                initNetWork();
                                pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                            }
                        }.sendEmptyMessageDelayed(0, 3000);
                    }
                });



        myexamListView = (PullableListView) this.getView().findViewById(R.id.exam_myexam_listview);

    }

    private void initNetWork() {
        mapTmp.clear();
        Log.d("usernum", "usernum" + UserNum.userNum);
        if (UserNum.userNum != null) {
            mapTmp.put("userNum", UserNum.userNum);
        } else {
            mapTmp.put("userNum", "");
        }
        netWorkThread(urlGetExam, mapTmp, 0x241);
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

    private void startReadSchool(String second){
        Intent intent = new Intent(this.getActivity(), AddExamHighSchoolActivity.class);
        intent.putExtra("second",second);
        startActivity(intent);
    }

    private void startReadExam(String url, String second, int examType) {
        Intent intent = new Intent(this.getActivity(), ReadExamActivity.class);
        intent.putExtra("second", second);
        intent.putExtra("url", url);
        intent.putExtra("examType", examType);
        startActivity(intent);
    }

    private List<Exam> setString2ListExam(List<String> strings) {
        List<Exam> exams = new ArrayList<>();

        if (strings != null) {
            for (String s : strings) {
                Exam e = new Exam(s);

                exams.add(e);
            }
        }
        return exams;

    }

    private void setListView(final List<Exam> exams) {

        final List<Exam> newExams = examsFilter(exams);

        ExamAdapter examAdapter = new ExamAdapter(getActivity(), newExams);
        if (myexamListView == null) {
            myexamListView = (ListView) this.getView().findViewById(R.id.exam_myexam_listview);
        }

        myexamListView.setAdapter(examAdapter);

        myexamListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String url = urlDiplomaContent;
                int examType = 0;
                switch (MyExam.examTag.get(position)) {
                    case "0": //考证
                        url = urlDiplomaContent;
                        examType = 0;
                        startReadExam(url, newExams.get(position).getExam(), examType);
                        break;
                    case "1": //考研
                        url = urlHighExamContent;
                        examType = 1;
//                        startSchoolList(exams.get(position).getExam());
                        if (newExams.get(position).getSchool().equals("")){
                            startReadSchool(newExams.get(position).getExam());
                        }else{
                            startReadExam(url, newExams.get(position).getSchool(), examType);
                        }
                        break;
                    case "2": //考公务员
                        url = urlOfficialContent;
                        examType = 2;
                        startReadExam(url, newExams.get(position).getExam(), examType);
                        break;
                }

            }
        });


        myexamListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("kaoban", "long click postion  :  " + position);
                showDialog(position);
                return true;
            }
        });
    }

    private void showDialog(final int position) {
        new AlertDialog.Builder(this.getActivity())
                .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteExam(position);
                    }
                })
                .show();
    }

    private void deleteExam(int position) {
        dialog.setMessage("正在删除");
        dialog.show();
        Log.d("exam","  myexam  " + MyExam.examTag.size() + " pos   " + position);
        String exam = "";
        switch (MyExam.examTag.get(position)) {
            case "0":
                exam += "0/" + newExams.get(position).getExam();
                break;
            case "1":
                if (newExams.get(position).getSchool().equals("")){
                    exam += "1/" + newExams.get(position).getExam();
                }else{
                    exam += "1/" + newExams.get(position).getExam() + ";"
                            + newExams.get(position).getSchool();
                }
                break;
            case "2":
                exam += "2/" + newExams.get(position).getExam();
                break;
        }

        for (int i = 0 ; i < MyExam.myExams.size() ; i ++){
            Log.d("exam","eee  " + MyExam.myExams.get(i));
            Log.d("exam","aaa  " + MyExam.examTag.get(i));
        }
        Log.d("exam","exam   " + exam);
        MyExam.examTag.remove(MyExam.myExams.indexOf(exam));
        MyExam.myExams.remove(exam);
        setListView(setString2ListExam(MyExam.myExams));

        handUpExams(exam);
    }

    private int findItemInMyExam(String exam) {
        String[] setExam = new String[MyExam.myExams.size()];
        MyExam.myExams.toArray(setExam);
        int i = 0;

        for (int j = 0; j < setExam.length; j++) {
            if (exam.compareTo(setExam[j]) == 0) {
                i = j;
                break;
            }
        }
        return i;
    }

    private List<Exam> examsFilter(List<Exam> exams) {

        if (MyExam.myExams == null) {
            MyExam.myExams = new ArrayList<>();
        } else {
            MyExam.myExams.clear();
        }

        newExams = new ArrayList<>();

        if (MyExam.examTag == null) {
            MyExam.examTag = new ArrayList<>();
        } else {
            MyExam.examTag.clear();
        }

        if (MyExam.exams == null){
            MyExam.exams = new ArrayList<>();
        }else {
            MyExam.exams.clear();
        }

        for (Exam e : exams) {
            MyExam.myExams.add(e.getExam());
            MyExam.exams.add(e);

            String[] eTag = e.getExam().split("/");

            if (eTag.length <= 1){
                continue;
            }

            String exam = eTag[1];

            if ("1".equals(eTag[0])){

                String[] sch = eTag[1].split(";");

                if (sch.length == 1){
                    Exam newE = new Exam(sch[0]);
                    newE.setEtime(e.getEtime());
                    newE.setRtime(e.getRtime());
                    newE.setExamType(eTag[0]);
                    newE.setSchool("");
                    newExams.add(newE);
                }else{
                    for (int i = 1 ; i < sch.length ; i ++){

                        Exam newE = new Exam(sch[0]);
                        newE.setEtime(e.getEtime());
                        newE.setRtime(e.getRtime());
                        newE.setExamType(eTag[0]);
                        newE.setSchool(sch[i]);
                        newExams.add(newE);
                    }
                }

            }else{

                Exam newE = new Exam(exam);
                newE.setEtime(e.getEtime());
                newE.setRtime(e.getRtime());
                newE.setExamType(eTag[0]);

                newExams.add(newE);
            }


            MyExam.examTag.add(eTag[0]);
        }

        return newExams;
    }


    private void handUpExams(String exam){

//        if (MyExam.myExams == null) {
//            return;
//        }
//
//        if (MyExam.myExams.isEmpty()) {
//            String tags = "empty";
//            examThread(tags);
//            return;
//        }
//
//        String tags = "";
//
//        String[] exams = new String[MyExam.myExams.size()];
//        MyExam.myExams.toArray(exams);
//        for (int i = 0; i < MyExam.myExams.size(); i++) {
//            tags += exams[i];
//            if (i < MyExam.myExams.size() - 1) {
//                tags += ',';
//            }
//        }

        examThread(exam);
    }

    //获取倒计时
    private long getCountDown(String date){
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd/");

        try {
            Date eDate = format.parse(date);
            Date nowDate = new Date();

            long diff =eDate.getTime() - nowDate.getTime();
            long days = diff / (1000 * 60 * 60 * 24);

            return days;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    private void examThread(String tags) {
        final Map<String,String> map = new HashMap<>();
        map.put("exam",tags);
        // 测试添加考试标签的功能
        new Thread(new Runnable() {
            @Override
            public void run() {
                DefaultHttpClient client = MyHttpClient.getHttpClient();
                String tmpString = MyHttpClient.doGet(client, urlDeleteExam,map, false);
                Message msg = new Message();
                msg.what = 0x237;
                Bundle bundle = new Bundle();
                bundle.putString("examTag", tmpString);
                msg.setData(bundle);
                handler.sendMessage(msg);
            }
        }).start();
    }

    private void clickItem(int examType,String second,int i){

        switch (examType){
            case 0:
                Intent intent = new Intent(ExamFragment.this.getActivity(),ZyWebViewActivity.class);
                intent.putExtra("second",second);
                intent.putExtra("conNum","" + i);
                intent.putExtra("exam",0);

                startActivity(intent);
                break;
            case 1:
                Intent intent1 = new Intent(ExamFragment.this.getActivity(),ReadExamItemActivity.class);
                intent1.putExtra("school",second);
                intent1.putExtra("conNum","" + i);

                startActivity(intent1);
                break;
            case 2:
                Intent intent2 = new Intent(ExamFragment.this.getActivity(),ZyWebViewActivity.class);
                intent2.putExtra("second",second);
                intent2.putExtra("conNum","" + i);
                intent2.putExtra("exam", 2);

                startActivity(intent2);
                break;
            case 3:
                startReadSchool(second);
                break;
        }

    }

    /**
     * 自定义adapter，实现添加考试按钮的监听事件
     */
    private class ExamAdapter extends BaseAdapter {

        private String [] highExam = {"常用网址","招生简章","专业目录","分数报录比","联系方式"};
        private String [] official = {"考试大纲","报考指南","笔试","面试","查分入口"};
        private String [] diploma = {"考试介绍","打印时间及入口","报名流程","查分入口","合格标准"};

        private int [] highImg = {R.drawable.exam_recommen_url,R.drawable.exam_recommen_criteria,
                R.drawable.exam_recommen_directory,R.drawable.exam_recommen_record,R.drawable.exam_recommen_contact};
        private int [] offImg = {R.drawable.exam_recommen_outline,R.drawable.exam_recommen_apply_guide,
                R.drawable.exam_recommen_test,R.drawable.exam_recommen_interview_test,R.drawable.exam_recommen_entrance};
        private int [] dipImg = {R.drawable.exam_recomend_introduce,R.drawable.exam_recommen_printing,
                R.drawable.exam_recommen_process,R.drawable.exam_recommen_entrance,
                R.drawable.exam_recommen_criteria};

        private int [] highExamId = {2,11,12,14,16};
        private int [] officialId = {4,5,6,7,8};
        private int [] diplomaId = {1,9,7,0,16};

        private List<Exam> exams = null;
        private LayoutInflater inflater;

        public ExamAdapter(Context context, List<Exam> exams) {
            this.exams = exams;
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
                convertView = this.inflater.inflate(R.layout.examfragment, null);
                AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                convertView.setLayoutParams(layoutParams);

            }

            final ViewHolder viewHolder = new ViewHolder();

//            viewHolder.icon = (ImageView)convertView.findViewById(R.id.icon_exam);
//            Random random = new Random();
//            int icon = random.nextInt(examIcon.length);
//            viewHolder.icon.setImageDrawable(getResources().getDrawable(examIcon[icon]));

            viewHolder.examIcon = (ExamTextView) convertView.findViewById(R.id.text_second_headimg);
            viewHolder.examName = (TextView) convertView.findViewById(R.id.textview_second_name);

            if ("1".equals(exams.get(position).getExamType())){
                viewHolder.examName.setText(exams.get(position).getExam() + "\n" + exams.get(position).getSchool());
                viewHolder.examIcon.getFirst(exams.get(position).getExam());
            }else{
                viewHolder.examName.setText(exams.get(position).getExam());
                viewHolder.examIcon.getFirst(exams.get(position).getExam());
            }

            viewHolder.rTime = (TextView) convertView.findViewById(R.id.textview_rtime);
            viewHolder.rTime.setText(exams.get(position).getRtime());
//            viewHolder.rTime.setText("" + getCountDown(exams.get(position).getRtime()));
            viewHolder.eTime = (TextView) convertView.findViewById(R.id.textview_etime);
            viewHolder.eTime.setText(exams.get(position).getEtime());
//            viewHolder.eTime.setText("" + getCountDown(exams.get(position).getEtime()));

            viewHolder.downLinear1 = (LinearLayout) convertView.findViewById(R.id.linear_down1);
            viewHolder.downLinear2 = (LinearLayout) convertView.findViewById(R.id.linear_down2);
            viewHolder.downLinear3 = (LinearLayout) convertView.findViewById(R.id.linear_down3);
            viewHolder.downLinear4 = (LinearLayout) convertView.findViewById(R.id.linear_down4);
            viewHolder.downLinear5 = (LinearLayout) convertView.findViewById(R.id.linear_down5);

            viewHolder.downText1 = (TextView) convertView.findViewById(R.id.text_down1);
            viewHolder.downText2 = (TextView) convertView.findViewById(R.id.text_down2);
            viewHolder.downText3 = (TextView) convertView.findViewById(R.id.text_down3);
            viewHolder.downText4 = (TextView) convertView.findViewById(R.id.text_down4);
            viewHolder.downText5 = (TextView) convertView.findViewById(R.id.text_down5);

            viewHolder.downImg1 = (ImageView) convertView.findViewById(R.id.img_down1);
            viewHolder.downImg2 = (ImageView) convertView.findViewById(R.id.img_down2);
            viewHolder.downImg3 = (ImageView) convertView.findViewById(R.id.img_down3);
            viewHolder.downImg4 = (ImageView) convertView.findViewById(R.id.img_down4);
            viewHolder.downImg5 = (ImageView) convertView.findViewById(R.id.img_down5);

            switch (exams.get(position).getExamType()){
                case "0":
                    setDown(viewHolder,diploma,exams.get(position).getExam(),0);
                    setImg(viewHolder,dipImg);
                    break;
                case "1":
                    if (exams.get(position).getSchool().equals("")){
                        setDown(viewHolder,highExam,exams.get(position).getExam(),3);
                    }else{
                        setDown(viewHolder,highExam,exams.get(position).getSchool(),1);
                    }
                    setImg(viewHolder,highImg);
                    break;
                case "2":
                    setDown(viewHolder,official,exams.get(position).getExam(),2);
                    setImg(viewHolder,offImg);
                    break;
            }

            return convertView;
        }

        private void setImg(ViewHolder viewHolder,int [] img){
            viewHolder.downImg1.setImageDrawable(getResources().getDrawable(img[0]));
            viewHolder.downImg2.setImageDrawable(getResources().getDrawable(img[1]));
            viewHolder.downImg3.setImageDrawable(getResources().getDrawable(img[2]));
            viewHolder.downImg4.setImageDrawable(getResources().getDrawable(img[3]));
            viewHolder.downImg5.setImageDrawable(getResources().getDrawable(img[4]));
        }

        private void setDown(ViewHolder viewHolder,String [] name,String second,int examType){
            viewHolder.downText1.setText(name[0]);
            viewHolder.downText2.setText(name[1]);
            viewHolder.downText3.setText(name[2]);
            viewHolder.downText4.setText(name[3]);
            viewHolder.downText5.setText(name[4]);

            switch (examType){
                case 0:
                    setLinear(viewHolder,0,second,diplomaId);
                    break;
                case 1:
                    setLinear(viewHolder,1,second,highExamId);
                    break;
                case 2:
                    setLinear(viewHolder,2,second,officialId);
                    break;
                case 3: //处理关注考研同时没有关注学校的特殊情况
                    setLinear(viewHolder,3,second,highExamId);
                    break;
            }
        }

        private void setLinear(ViewHolder viewHolder,int examType,String second,int [] id){
            viewHolder.downLinear1.setOnClickListener(new MyClickListener(examType,second,id[0]));
            viewHolder.downLinear2.setOnClickListener(new MyClickListener(examType,second,id[1]));
            viewHolder.downLinear3.setOnClickListener(new MyClickListener(examType,second,id[2]));
            viewHolder.downLinear4.setOnClickListener(new MyClickListener(examType,second,id[3]));
            viewHolder.downLinear5.setOnClickListener(new MyClickListener(examType,second,id[4]));
        }

    }

    private class ViewHolder {
        ImageView icon;
        ExamTextView examIcon;
        TextView examName;
        TextView rTime;
        TextView eTime;

        TextView downText1;
        TextView downText2;
        TextView downText3;
        TextView downText4;
        TextView downText5;

        LinearLayout downLinear1;
        LinearLayout downLinear2;
        LinearLayout downLinear3;
        LinearLayout downLinear4;
        LinearLayout downLinear5;

        ImageView downImg1;
        ImageView downImg2;
        ImageView downImg3;
        ImageView downImg4;
        ImageView downImg5;
    }

    private class MyClickListener implements View.OnClickListener {

        private String second;
        private int examType;
        private int conNum;

        public MyClickListener(int examType,String second,int conNum){
            this.second = second;
            this.examType = examType;
            this.conNum = conNum;
        }

        @Override
        public void onClick(View v) {
            Log.d("kaoban", "click : " + examType + "  " + second + "  " + conNum);
            clickItem(examType, second, conNum);
        }
    }

    public void getFirst(String second,int pos){
        network(second,pos);
    }

    private void network(String second,int pos){
        final DefaultHttpClient client = MyHttpClient.getHttpClient();
        final Map<String,String> map = new HashMap<>();
        map.put("second", second);

        new Thread(){
            @Override
            public void run() {
                String res = MyHttpClient.doGet(client,urlGetFirst,map,false);
                handler.sendMessage(handler.obtainMessage(0,res));
            }
        }.start();
    }

}
