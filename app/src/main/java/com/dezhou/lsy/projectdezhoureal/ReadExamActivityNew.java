package com.dezhou.lsy.projectdezhoureal;

/**
 * Created by zy on 15-3-18.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.impl.client.DefaultHttpClient;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import network.MyHttpClient;
import network.SerializableMsg;
import tempvalue.IfTest;
import utils.URLSet;


public class ReadExamActivityNew extends Activity implements View.OnTouchListener {

    //  获取公务员考试的具体内容
    private static String urlOfficialContent = URLSet.serviceUrl+"/kaoban/official/content/";

    //获取考证的具体内容
//    private static String urlOfficialContent = "http://115.28.35.2:8802/kaoban/diploma/content/";
    private static String urlExamContent = "";


    String SECOND;

    private ButtonState[] CUR_STATE;
    private ButtonState[] NEXT_STATE;

    private LinearLayout mainLinearLayout;
    private LinearLayout linearLayout1;
    private LinearLayout linearLayout2;

    private float moveY;

    private int downY;
    private int curButtonNum = 0;

    private int buttonNum = 10;
    private int firstLineButtonNum = 5;
    private int buttonMaxHeight = 100;
    private int buttonAddHeight = 10;
    private int textviewWidth = buttonMaxHeight * 2;
    private int textviewHeight = buttonMaxHeight / 2;

    private int leftEdge = 400;     //中心按钮距离最左边的距离，
    private int bottomLine = 900;   //按钮变成最大的最高处

    private int screenHeight = 0;
    private int screenWidth = 0;

    private int edge = 80;   //用于按钮距离中心的距离

    private int secondSubFirst = 200;

    private int examType;

    private int[] relativeIdList = {R.id.relativeLayout1,R.id.relativeLayout2,R.id.relativeLayout3,R.id.relativeLayout4,R.id.relativeLayout5,
            R.id.relativeLayout2_1,R.id.relativeLayout2_2,R.id.relativeLayout2_3,R.id.relativeLayout2_4,R.id.relativeLayout2_5,};
    private int[] buttonIdList = {R.id.button1,R.id.button2,R.id.button3,R.id.button4,R.id.button5,
            R.id.button2_1,R.id.button2_2,R.id.button2_3,R.id.button2_4,R.id.button2_5,};
    private int[] textviewIdList = {R.id.textview1,R.id.textview2,R.id.textview3,R.id.textview4,R.id.textview5,
            R.id.textview2_1,R.id.textview2_2,R.id.textview2_3,R.id.textview2_4,R.id.textview2_5,};

    private int[] iconList = {R.drawable.readexam_intrucduction, R.drawable.readexam_student,
            R.drawable.readexam_money, R.drawable.readexam_conn, R.drawable.readexam_process,
            R.drawable.readexam_condition, R.drawable.readexam_time, R.drawable.readexam_print,
            R.drawable.readexam_firstexam, R.drawable.readexam_secondexam};

    private int[] nameImgList = {R.drawable.readexam_diploma, R.drawable.readexam_highexam,
            R.drawable.readexam_official};

    /**
     * 各个考试对应的图标
     */
    private int[] diplomaImgList = {R.drawable.readexam_diploma1, R.drawable.readexam_diploma2,
            R.drawable.readexam_diploma3, R.drawable.readexam_diploma4, R.drawable.readexam_diploma5,
            R.drawable.readexam_diploma6, R.drawable.readexam_diploma7, R.drawable.readexam_diploma8,
            R.drawable.readexam_diploma9};
    private int[] highExamImgList = {R.drawable.readexam_highexam1, R.drawable.readexam_highexam2,
            R.drawable.readexam_highexam3, R.drawable.readexam_highexam4, R.drawable.readexam_highexam5,
            R.drawable.readexam_highexam6, R.drawable.readexam_highexam7, R.drawable.readexam_highexam8,
            R.drawable.readexam_highexam9, R.drawable.readexam_highexam10,};
    private int[] officialImgList = {R.drawable.readexam_official1, R.drawable.readexam_official2,
            R.drawable.readexam_official3, R.drawable.readexam_official4, R.drawable.readexam_official5,
            R.drawable.readexam_official6, R.drawable.readexam_official7, R.drawable.readexam_official8,
            R.drawable.readexam_official9};


    private String[] textList = {"考研介绍", "学校招生", "查看学费", "联系方式", "考试流程",
            "报名条件", "报名时间", "打印证件", "学校初试", "学校复试"};


    /**
     * 各个考试对应的名称
     */
    private String[] diplomaTextList = {"考试介绍", "报名条件", "考试大纲", "常见问题",
            "官方机构", "考前准备", "考试报名", "成绩查询", "领取证书"};
    private String[] highExamTextList = {"考研介绍", "考试流程", "打印准考证", "初试", "复试",
            "招生简章", "专业目录", "历年分数", "参考书目", "招生统计"};
    private String[] officialTextList = {"认识考试", "部门介绍", "考试大纲", "报考指南",
            "职位表", "笔试", "面试", "体检", "考察试用"};

    private List<RelativeLayout> relativeLayoutList;
    private List<Button> buttonList;
    private List<TextView> textViewList;


    private Map<String, String> mapTmp = null;

    private Handler downloadHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.readexam_new);

        getDataFromIntent();

        setScreenHeightAndWidth();

        initData();

        initView();

        initHandler();

    }

    private void getDataFromIntent() {
        SECOND = this.getIntent().getStringExtra("second");
        urlExamContent = this.getIntent().getStringExtra("url");
        examType = this.getIntent().getIntExtra("examType", 0);

        setExamType();

    }

    private void setExamType() {
        switch (examType) {
            case 0:   //考证
                iconList = diplomaImgList;
                textList = diplomaTextList;
                buttonNum = diplomaTextList.length;
                firstLineButtonNum = 4;
                break;
            case 1:   //考研
                iconList = highExamImgList;
                textList = highExamTextList;
                buttonNum = highExamTextList.length;
                firstLineButtonNum = 5;
                break;
            case 2:   //考公务员
                iconList = officialImgList;
                textList = officialTextList;
                buttonNum = officialTextList.length;
                firstLineButtonNum = 4;
                break;
        }
    }

    private void initHandler() {


        downloadHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String text = (String) msg.obj;
                if (IfTest.ifTest){
                    Toast.makeText(ReadExamActivityNew.this, text, Toast.LENGTH_SHORT).show();
                }
            }
        };
    }


    private void setScreenHeightAndWidth() {

        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenHeight = dm.heightPixels;
        screenWidth = dm.widthPixels;

    }

    private void initData() {

        bottomLine = screenHeight - edge;   //-edge是为了能看清楚按钮变大的趋势
        leftEdge = screenWidth / 2;
        buttonMaxHeight = screenWidth / 6;
        textviewWidth = buttonMaxHeight * 2;
        textviewHeight = buttonMaxHeight / 2;

    }

    private void initView() {

        View.OnTouchListener listener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        downY = (int) event.getRawY();

                        break;

                    case MotionEvent.ACTION_UP:
                        Log.d("getpic","up");
                        actionUp();
                        break;

                    case MotionEvent.ACTION_MOVE:

                        break;
                }

                return true;
            }
        };

        mainLinearLayout = (LinearLayout) this.findViewById(R.id.linear_main);
        linearLayout1 = (LinearLayout) this.findViewById(R.id.linear1);
        linearLayout2 = (LinearLayout) this.findViewById(R.id.linear2);

        mainLinearLayout.setOnTouchListener(listener);
        linearLayout1.setOnTouchListener(listener);
        linearLayout2.setOnTouchListener(listener);

        initRelative();

        initButton();

        initTextView();

    }

    private void initRelative() {
        relativeLayoutList = new ArrayList<>();
        for (int id : relativeIdList){
            RelativeLayout relativeLayout = (RelativeLayout) this.findViewById(id);
            relativeLayoutList.add(relativeLayout);
        }
    }

    private void initButton(){
        buttonList = new ArrayList<>();
        for (int i = 0 ; i < buttonIdList.length ; i ++){
            if (i >= iconList.length){
                break;
            }
            Button button = (Button) this.findViewById(buttonIdList[i]);
            button.setBackgroundResource(iconList[i]);
            buttonList.add(button);
        }
    }

    private void initTextView(){
        textViewList = new ArrayList<>();
        for (int i = 0 ; i < textviewIdList.length ; i ++){
            if (i >= textList.length){
                break;
            }
            TextView textView = (TextView) this.findViewById(textviewIdList[i]);
            textView.setText(textList[i]);
            textViewList.add(textView);
        }

    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                downY = (int) event.getRawY();

                break;

            case MotionEvent.ACTION_UP:
                actionUp();
                break;

            case MotionEvent.ACTION_MOVE:

                moveY = event.getRawY();

                actionMove();

                break;
        }

        return true;
    }


    private void actionUp(){
        curButtonNum ++;
        curButtonNum %= 10;

        if (curButtonNum > 4){
            linearLayout1.setVisibility(View.GONE);
            linearLayout2.setVisibility(View.VISIBLE);
        }else{
            linearLayout2.setVisibility(View.GONE);
            linearLayout1.setVisibility(View.VISIBLE);
        }
        LinearLayout.LayoutParams layoutParams;
        int i = 0 ;
        for (RelativeLayout relativeLayout : relativeLayoutList){
            layoutParams = (LinearLayout.LayoutParams) relativeLayout.getLayoutParams();
            layoutParams.weight = 1;
            relativeLayout.setLayoutParams(layoutParams);
            if (i >= textList.length){
                continue;
            }
            Button button = (Button) relativeLayout.getChildAt(0);
            button.setHeight(10);
            button.setWidth(10);
            TextView textView = (TextView) relativeLayout.getChildAt(1);
            textView.setText(textList[i ++]);
        }

        layoutParams = (LinearLayout.LayoutParams) relativeLayoutList.get(curButtonNum).getLayoutParams();
        layoutParams.weight = 2;
        relativeLayoutList.get(curButtonNum).setLayoutParams(layoutParams);
        Button button = (Button) relativeLayoutList.get(curButtonNum).getChildAt(0);
        button.setHeight(50);
        button.setWidth(50);
        TextView textView = (TextView) relativeLayoutList.get(curButtonNum).getChildAt(1);
        textView.append("\nfuckfuck.................");
    }

    /**
     * 触摸移动函数
     */
    private void actionMove() {
//
    }

    /**
     * 状态枚举
     */
    enum ButtonState {
        ON_NURMAL,
        ON_BIG,
        ON_SMALL,
    }


    /**
     * 李松阳写的函数
     */

    void readExam(int conNum) {
        //  获取公务员考试的具体内容
        mapTmp = getNewMap(mapTmp);
//        mapTmp.put("second", "计算机二级");
//        mapTmp.put("conNum", "1");
        mapTmp.put("second", SECOND);
        mapTmp.put("conNum", String.valueOf(conNum));

        String urlTmp = getNewUrl(urlExamContent, mapTmp);
        Log.d("cout", "readexam conNum = " + conNum + " url " + urlTmp);
        // 跳转到WebView页面
        Intent intent = new Intent(this, WebViewActivity.class);
        SerializableMsg msg = new SerializableMsg();
        msg.setId("OfficialContent");
        msg.setMsg(urlTmp);
        msg.setIntNum(Integer.parseInt(mapTmp.get("conNum")));
        intent.putExtra("Message", msg);
        startActivity(intent);
    }


    /**
     * 转换为Ｇｅｔ请求需要的ｕｒｌ
     *
     * @param url    未转换前的ｕｒｌ
     * @param mapTmp Ｇｅｔ请求的参数
     * @return mapTmp为空 未转换的ＵＲＬ
     * mapTmp不为空 转换后的URL
     */
    private String getNewUrl(String url, Map<String, String> mapTmp) {
        StringBuilder sb = null;
        if (mapTmp != null) {
            // url重新构造，UTF-8编码
            sb = new StringBuilder(url);
            sb.append('?');
            // ?page=1&tags="计算机"(全部则为all)
            for (Map.Entry<String, String> entry : mapTmp.entrySet()) {
                try {
                    sb.append(entry.getKey()).append('=').append(URLEncoder.encode(entry.getValue(), "UTF-8"))
                            .append('&');
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            sb.deleteCharAt(sb.length() - 1);
            Log.d("TestApp", "构造的ＵＲＬ为：" + sb.toString());
            return sb.toString();
        } else {
            sb = new StringBuilder(url);
            return sb.toString();
        }

    }

    /**
     * Ｍａｐ的初始化
     *
     * @param mapTmp 需要初始化的Ｍａｐ
     * @return　初始化后的Ｍａｐ
     */
    private Map<String, String> getNewMap(Map<String, String> mapTmp) {
        if (mapTmp == null) {
            mapTmp = new HashMap<String, String>();
        } else {
            mapTmp.clear();
        }
        return mapTmp;
    }


    private void downloadThread(final String url) {
        Toast.makeText(this, "开始下载", Toast.LENGTH_SHORT).show();
        // 触发下载任务！
        new Thread(new Runnable() {
            @Override
            public void run() {
//                if (mapTmp == null) {
//                    mapTmp = new HashMap<>();
//                } else {
//                    mapTmp.clear();
//                }
//                mapTmp.put("docNum", "1");
//                String newURL = urlBuild(url, mapTmp);
                DefaultHttpClient client = MyHttpClient.getHttpClient();
                String filePath = MyHttpClient.doGetFileDownload(client, url);
                if (filePath != null) {
                    Log.d("TestApp", "下载完成！");
                    Message msg = downloadHandler.obtainMessage(0, "下载完成");
                    downloadHandler.sendMessage(msg);
                } else {
                    Log.d("TestApp", "下载失败！");
                    Message msg = downloadHandler.obtainMessage(0, "下载失败");
                    downloadHandler.sendMessage(msg);
                }
            }
        }).start();
    }

    /**
     * 对url重新构造，满足Get请求的需求
     *
     * @param url 请求地址
     * @param map 请求参数
     * @return 构造的新的url
     */
    private String urlBuild(String url, Map<String, String> map) {
        // url重新构造，UTF-8编码
        StringBuilder sb = new StringBuilder(url);
        sb.append('?');
        // ?page=1&tags="计算机"(全部则为all)
        for (Map.Entry<String, String> entry : map.entrySet()) {
            try {
                sb.append(entry.getKey()).append('=').append(URLEncoder.encode(entry.getValue(), "UTF-8"))
                        .append('&');
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    private void upDialog(final String url) {
        new AlertDialog.Builder(this)
                .setTitle("下载职位表").setMessage("是否下载职位表？")
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        downloadThread(url);
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();

    }

}