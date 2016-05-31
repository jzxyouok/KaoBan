package view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
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
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;


import com.dezhou.lsy.projectdezhoureal.AddExamGuideAndBefore;
import com.dezhou.lsy.projectdezhoureal.R;
import com.dezhou.lsy.projectdezhoureal.ReadExamActivity;
import com.dezhou.lsy.projectdezhoureal.ReadExamItemActivity;
import com.dezhou.lsy.projectdezhoureal.ZyWebViewActivity;

import org.apache.http.impl.client.DefaultHttpClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import kaobanxml.XmlParser;
import network.MyHttpClient;
import tempvalue.IfTest;
import tempvalue.ItemContent;
import utils.URLSet;


/**
 * Created by zy on 15-8-6.
 */
public class MyScroll extends ScrollView {


    //  获取公务员考试的具体内容
    private static String urlOfficialContent = URLSet.serviceUrl+"/kaoban/official/content/";
    private static String mainUrl = URLSet.serviceUrl;

    //获取考证的具体内容
//    private static String urlOfficialContent = "http://115.28.35.2:8802/kaoban/diploma/content/";
    private static String urlExamContent = "";


    private String second;

    private ButtonState[] CUR_STATE;
    private ButtonState[] NEXT_STATE;

    private FrameLayout frameLayout;

    private LinearLayout headerLinearLayout;
    private ImageView examNameImageView; //显示是考研还是考公务员还是考证

    private TextView backTextView;
    private TextView centerUpTextView;
    private TextView centerDownTextView;
    private Context context;

    private Rect windowRect;

    private List<Integer> bigList;

    private boolean ifStart;

    private float moveY;
    private float offset = 0;
    private float allOffset = 0;

    private int lastY;
    private int screenHeight;
    private int screenWidth;

    private int downY;

    private int buttonNum = 10;
    private int firstLineButtonNum = 5;
    private int buttonMaxHeight = 100;
    private int buttonAddHeight = 20;
    private int textviewWidth = buttonMaxHeight * 2;
    private int textviewHeight = buttonMaxHeight / 2;

    private int leftEdge = 400;     //中心按钮距离最左边的距离，
    private int bottomLine = 900;   //按钮变成最大的最高处

    private int edge = 40;   //用于按钮距离中心的距离

    private int secondSubFirst = 200;

    private int examType;

    private int allHeight = 0;

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
            R.drawable.readexam_highexam9, R.drawable.readexam_highexam10,R.drawable.readexam_highexam11,
            R.drawable.readexam_highexam12,R.drawable.readexam_highexam13,R.drawable.readexam_highexam14,
            R.drawable.readexam_highexam15,R.drawable.readexam_highexam16,R.drawable.readexam_highexam17,
            R.drawable.readexam_highexam18,R.drawable.readexam_highexam19,R.drawable.readexam_highexam20,
            R.drawable.readexam_highexam21};
    private int[] officialImgList = {R.drawable.readexam_official1, R.drawable.readexam_official2,
            R.drawable.readexam_official3, R.drawable.readexam_official4, R.drawable.readexam_official5,
            R.drawable.readexam_official6, R.drawable.readexam_official7, R.drawable.readexam_official8,
            R.drawable.readexam_official9};


    private String[] textList = {"考研介绍", "学校招生", "查看学费", "联系方式", "考试流程",
            "报名条件", "报名时间", "打印证件", "学校初试", "学校复试"};


    /**
     * 各个考试对应的名称
     */
    private String[] diplomaTextList = {"考试介绍", "考试大纲", "官方机构", "常见问题",
            "报名条件", "报名时间", "报名流程", "报名费用", "打印时间及入口","打印注意事项",
            "考试时间","考试科目","考试方法","考试题量","查分入口","合格标准","领取地点","领取方式"};
    private String[] highExamTextList = {"考研介绍", "常用网址", "考试大纲", "考研报名", "现场确认",
            "打印准考证", "初试", "成绩查询", "调剂复试录取", "学院介绍","招生简章","专业目录","参考书目",
            "分数报录比","导师信息","联系方式","培养政策","历年真题","调剂信息","复试信息","其他"};
    private String[] officialTextList = {"认识考试", "部门介绍", "报名考试时间", "考试大纲",
            "报考指南", "笔试", "面试", "查分入口", "体检","考察","试用","待遇","职位表"};

    private List<Button> buttonList;
    private List<Button> posList;    //存储中心的圆点按钮
    private List<TextView> textViewList;

    private boolean[] buttonBigFlags; //保存button是否可以变大，当大于bottomLine时为false
    private boolean[] buttonSmallFlags;

    private Scroller mScroller;

    private Map<String, String> mapTmp = null;

    private Handler bigHandler;
    private Handler smallHandler;
    private Handler downloadHandler;

    private Handler moveHandler;


    private Runnable scrollCheck = new Runnable() {
        @Override
        public void run() {
            if (lastY == MyScroll.this.getScrollY()) {
                checkButtonOnScreen();
//
//                bigList.clear();
//                for (int i = 0; i < posList.size(); i++) {
//                    if (onScreen(posList.get(i))) {
//                        if (buttonList.get(i).getHeight() < buttonMaxHeight){
//                            bigList.add(i);
//                        }
//                    } else {
//                        startMySmallThread(i);
//                    }
//                }
//                startBigThread();

            } else {
                lastY = MyScroll.this.getScrollY();
                postDelayed(scrollCheck, 100);
            }
        }
    };

    public MyScroll(Context context) {
        super(context, null, 0);
        this.context = context;
    }

    public MyScroll(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        this.context = context;
    }

    public MyScroll(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    public void init(String second,String url,int examType) {
        this.second = second;
        this.urlExamContent = url;
        this.examType = examType;

        examNameImageView = (ImageView) this.findViewById(R.id.readexam_img_examname);
        examNameImageView.setImageDrawable(getResources().getDrawable(nameImgList[examType]));

        ifStart = true;

        mScroller = new Scroller(getContext());

        setExamType();

        bigList = new ArrayList<>();
        lastY = -1;

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        screenHeight = displayMetrics.heightPixels;
        screenWidth = displayMetrics.widthPixels;

        windowRect = new Rect(0, 0, screenWidth, screenHeight);

        frameLayout = (FrameLayout) this.findViewById(R.id.layout);
        frameLayout.setLongClickable(true);

        this.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ifStart = false;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_UP:
                        lastY = MyScroll.this.getScrollY();
                        postDelayed(scrollCheck, 300);
                        break;
                }
                return false;
            }
        });

        initData();
        initHandler();
        initView();
        initDataAfterView();

        startMove();
    }

    private void startBigThread() {
        for (int i = 0; i < bigList.size(); i++) {
            startMyBigThread(bigList.get(i), i * 80);
        }
    }


    private boolean onScreen(View view) {
        return view.getLocalVisibleRect(windowRect);
    }


    /**
     * 状态枚举
     */
    enum ButtonState {
        ON_NURMAL,
        ON_BIG,
        ON_SMALL,
    }


    private void initHandler() {
        /**
         * 一开始自动向上handler
         */
        moveHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                //layoutMove(1);

            }
        };

        /**
         * 处理按钮变大
         */
        bigHandler = new Handler() {
            public void handleMessage(Message mes) {

                int curButton = mes.what;
                int curHeight = (int) mes.obj;

                setState(ButtonState.ON_BIG, curButton);

                LayoutParams layoutParams = (LayoutParams)
                        buttonList.get(curButton).getLayoutParams();

                if (curHeight >= buttonMaxHeight) {
                    curHeight = buttonMaxHeight;
                    if (curButton % 2 == 0) {
                        setTextViewAbsloutPos(curButton, (int) (posList.get(curButton).getX() + edge));
                    } else {
                        setTextViewAbsloutPos(curButton, (int) (posList.get(curButton).getX() - edge - textviewHeight));
                    }

                    if (NEXT_STATE[curButton] == ButtonState.ON_SMALL) {
                        startMySmallThread(curButton);
                    } else {
                        CUR_STATE[curButton] = ButtonState.ON_NURMAL;
                    }
                }

                layoutParams.height = curHeight;
                layoutParams.width = curHeight;
                buttonList.get(curButton).setLayoutParams(layoutParams);

                setButtonPos(curButton,layoutParams.height);
                buttonBigFlags[curButton] = false;
                buttonSmallFlags[curButton] = true;

                setTextViewPos(curButton,
                        (int) (screenWidth - edge - posList.get(curButton).getX()) / (buttonMaxHeight / buttonAddHeight));

            }
        };


        /**
         * 处理按钮变小
         */
        smallHandler = new Handler() {
            public void handleMessage(Message mes) {

                int curButton = mes.what;
                int curHeight = (int) mes.obj;

                setState(ButtonState.ON_SMALL, curButton);

                LayoutParams layoutParams = (LayoutParams)
                        buttonList.get(curButton).getLayoutParams();

                if (curHeight <= 0) {
                    curHeight = 0;
                    if (curButton % 2 == 0) {
                        setTextViewAbsloutPos(curButton, screenWidth);
                    } else {
                        setTextViewAbsloutPos(curButton, -textviewWidth);
                    }

                    if (NEXT_STATE[curButton] == ButtonState.ON_BIG) {
                        startMyBigThread(curButton, 0);
                    } else {
                        CUR_STATE[curButton] = ButtonState.ON_NURMAL;
                    }
                }

                layoutParams.height = curHeight;
                layoutParams.width = curHeight;
                buttonList.get(curButton).setLayoutParams(layoutParams);

                setButtonPos(curButton, layoutParams.height);
                buttonBigFlags[curButton] = true;
                buttonSmallFlags[curButton] = false;


                setTextViewPos(curButton,
                        -(int) (screenWidth - edge - posList.get(curButton).getX()) / (buttonMaxHeight / buttonAddHeight));

            }

        };

        downloadHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1){
                    String res = (String) msg.obj;
                    ItemContent content = XmlParser.examItemParser(res);
                    downloadThread(mainUrl + content.getContent());
                }else{
                    String text = (String) msg.obj;
                    Toast.makeText(MyScroll.this.getContext(), text, Toast.LENGTH_SHORT).show();
                }
            }
        };
    }


    private void startMyBigThread(int curButton, int sleepTime) {
        MyBigThread thread = new MyBigThread(curButton, buttonMaxHeight, buttonAddHeight, 0, sleepTime);
        new Thread(thread).start();
//        offset += 1;
//        Log.d("kaoban","+1  " + offset);
    }

    private void startMySmallThread(int curButton) {
        MySmallThread thread = new MySmallThread(curButton, buttonMaxHeight, buttonAddHeight, buttonMaxHeight);
        new Thread(thread).start();
//        offset -= 1;
//        Log.d("kaoban","-1  " + offset);
    }


    private void initData() {

        bottomLine = screenHeight - edge;   //-edge是为了能看清楚按钮变大的趋势
        leftEdge = screenWidth / 2;
        buttonMaxHeight = screenWidth / 5;
        textviewWidth = buttonMaxHeight * 2;
        textviewHeight = (int) (buttonMaxHeight / 1.5f);

    }

    private void initDataAfterView() {
        allOffset = screenHeight / 2 + buttonNum * buttonMaxHeight * 3 / 2;
        offset = 0;
    }

    private void initView() {

        initBack();

        initButton();

        initPos();

        initTextView();

    }

    private void initBack(){
        allHeight = screenHeight + (buttonNum + 2) * buttonMaxHeight * 3 / 2;

        backTextView = (TextView) this.findViewById(R.id.text_background);
        backTextView.setHeight(allHeight);
    }


    /**
     * 初始化中心按钮，位于中心，高度相差3/2的按钮高度
     */
    private void initPos() {
        posList = new ArrayList<>();

        centerUpTextView = (TextView) this.findViewById(R.id.text_center_up);
        centerUpTextView.setY(screenHeight + edge);
        centerUpTextView.setHeight((firstLineButtonNum - 1) * buttonMaxHeight * 3 / 2);

        centerDownTextView = (TextView) this.findViewById(R.id.text_center_down);
        centerDownTextView.setY(secondSubFirst + screenHeight + 2 * edge +
                firstLineButtonNum * buttonMaxHeight * 3 / 2);
        centerDownTextView.setHeight((buttonNum - firstLineButtonNum - 1) * buttonMaxHeight * 3 / 2);

        for (int i = 0; i < buttonNum; i++) {
            Button button = new Button(context);

            LayoutParams layoutParams = new LayoutParams(10, 10);
            layoutParams.leftMargin = leftEdge - 5;
            button.setLayoutParams(layoutParams);
//            button.setBackgroundResource(R.drawable.centerball);
            button.setBackgroundColor(getResources().getColor(R.color.white));

            if (i < firstLineButtonNum) {
                button.setY(screenHeight + edge + i * buttonMaxHeight * 3 / 2);
            } else {
                button.setY(secondSubFirst + screenHeight + 2 * edge + i * buttonMaxHeight * 3 / 2);

                if (i == firstLineButtonNum) {
                    TextView textView1 = new TextView(context);
                    LayoutParams layoutParams1 = new LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, secondSubFirst);

                    layoutParams1.leftMargin = 0;
                    textView1.setLayoutParams(layoutParams1);
                    textView1.setBackgroundColor(this.getResources().getColor(R.color.blue));
                    textView1.setY(secondSubFirst + screenHeight + (i - 1) * buttonMaxHeight * 3 / 2);
                    frameLayout.addView(textView1, layoutParams1);
                }
            }

            posList.add(button);

            frameLayout.addView(button, layoutParams);
        }


    }

    private void initButton() {

        buttonList = new ArrayList<>();

        buttonBigFlags = new boolean[buttonNum];
        buttonSmallFlags = new boolean[buttonNum];

        CUR_STATE = new ButtonState[buttonNum];
        NEXT_STATE = new ButtonState[buttonNum];

        for (int i = 0; i < buttonNum; i++) {
            final Button button = new Button(context);

            LayoutParams layoutParams = new LayoutParams(0, 0);
            layoutParams.topMargin = buttonMaxHeight * 1 / 2;
            layoutParams.leftMargin = leftEdge;
            button.setLayoutParams(layoutParams);
            button.setBackgroundResource(iconList[i]);
            final int finalI = i + 1;
            button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickItem(finalI);
                }
            });

            buttonList.add(button);

            frameLayout.addView(button, layoutParams);

            buttonBigFlags[i] = true;
            buttonSmallFlags[i] = false;

            CUR_STATE[i] = ButtonState.ON_NURMAL;
            NEXT_STATE[i] = ButtonState.ON_NURMAL;
        }

    }

    private void initTextView() {

        textViewList = new ArrayList<>();

        for (int i = 0; i < buttonNum; i++) {
            TextView textView = new TextView(context);

            LayoutParams layoutParams = new LayoutParams(textviewWidth, textviewHeight);
            if (i % 2 == 0) {
                layoutParams.leftMargin = screenWidth;
                textView.setBackgroundResource(R.drawable.readexam_textview_right);
            } else {
                layoutParams.leftMargin = -buttonMaxHeight;
                textView.setBackgroundResource(R.drawable.readexam_textview_left);
            }
            textView.setLayoutParams(layoutParams);
            textView.setY(-screenHeight);
            textView.setText(textList[i]);
            textView.setTextSize(textviewHeight / 8);
            textView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            textView.setTextColor(getResources().getColor(android.R.color.white));
            textView.setClickable(true);
            textView.setLines(1);
            textView.setEllipsize(TextUtils.TruncateAt.MIDDLE);
            textView.setGravity(Gravity.CENTER);
            final int finalI = i + 1;
            textView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            textViewList.add(textView);
            frameLayout.addView(textView, layoutParams);
        }
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()){
            scrollTo(mScroller.getCurrX(),mScroller.getCurrY());
            postInvalidate();
        }
        super.computeScroll();
    }

    private void startMove() {

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                post(new Runnable() {
                    @Override
                    public void run() {
//                        smoothScrollTo(0, buttonMaxHeight * 3 / 2);
                        mScroller.startScroll(getScrollX(),getScrollY(),getScrollX(),buttonMaxHeight *3 / 2,1600);
                        invalidate();
                        lastY = MyScroll.this.getScrollY();
                        postDelayed(scrollCheck, 300);
//                        startMyBigThread(0,0);
                    }
                });
            }
        }, 1000);

    }

    /**
     * 设置按钮，开启哪个线程
     *
     * @param curButton
     */
    private void setButton(int curButton) {
        int[] pos = new int[2];
        posList.get(curButton).getLocationOnScreen(pos);

        if (pos[1] <= bottomLine && buttonBigFlags[curButton]) {
            /**
             * 如果当前button状态是变大的状态或者正常状态
             * 状态在handler中改变
             */
            if (CUR_STATE[curButton] == ButtonState.ON_BIG || CUR_STATE[curButton] == ButtonState.ON_NURMAL) {
                startMyBigThread(curButton, 0);
            }
        }
        if (pos[1] > bottomLine && buttonSmallFlags[curButton]) {
            if (CUR_STATE[curButton] == ButtonState.ON_SMALL || CUR_STATE[curButton] == ButtonState.ON_NURMAL) {
                startMySmallThread(curButton);
            }
        }
    }


    /**
     * 设置button位置
     *
     * @param curButton
     * @param height
     */
    private void setButtonPos(int curButton, int height) {
        /**
         * 让按钮左右分布
         * 偶数在左边，奇数在右边,与textview相反
         */
        if (curButton % 2 == 0) {
            buttonList.get(curButton).setX(posList.get(curButton).getX() - height - edge);
        } else {
            buttonList.get(curButton).setX(posList.get(curButton).getX() + edge);
        }

        buttonList.get(curButton).setY(posList.get(curButton).getY() - height / 2);
    }


    /**
     * 设置textview位置
     *
     * @param curButton
     * @param curt
     */
    private void setTextViewPos(int curButton, int curt) {
        if (curButton % 2 == 0) {
            textViewList.get(curButton).setX(textViewList.get(curButton).getX() - curt);
        } else {
            textViewList.get(curButton).setX(textViewList.get(curButton).getX() + curt);
        }
        textViewList.get(curButton).setY(posList.get(curButton).getY()
                - textViewList.get(curButton).getHeight() / 3);

        float minX = (posList.get(curButton).getX() + edge);
        float maxX = (posList.get(curButton).getX() - edge - textviewWidth);

        if (textViewList.get(curButton).getX() < minX && curButton % 2 == 0) {
            textViewList.get(curButton).setX(minX);
        } else if (textViewList.get(curButton).getX() > maxX && curButton % 2 == 1) {
            textViewList.get(curButton).setX(maxX);
        }
    }


    /**
     * 设置textview的绝对位置
     *
     * @param curButton
     * @param posX
     */
    private void setTextViewAbsloutPos(int curButton, int posX) {
        textViewList.get(curButton).setX(posX);
        textViewList.get(curButton).setY(posList.get(curButton).getY());
    }


    /**
     * 设置状态，button是变大，缩小，还是正常
     *
     * @param state
     * @param curButton
     */
    private void setState(ButtonState state, int curButton) {
        if (CUR_STATE[curButton] == ButtonState.ON_NURMAL) {
            CUR_STATE[curButton] = state;
        } else {
            NEXT_STATE[curButton] = state;
        }
    }


    /**
     * button变大线程
     */
    class MyBigThread extends Thread {

        private int curButton = 0;
        private int curHeight = 0;
        private int buttonMaxHeight = 0;
        private int buttonAddHeight = 0;
        private int sleepTime = 0;

        private boolean flag = true;

        public MyBigThread(int curButton, int buttonMaxHeight, int buttonAddHeight, int curHeight, int sleepTime) {

            this.curButton = curButton;
            this.buttonMaxHeight = buttonMaxHeight;
            this.buttonAddHeight = buttonAddHeight;
            this.curHeight = curHeight;
            this.sleepTime = sleepTime;
            Log.d("buttoncout", " " + buttonMaxHeight);

        }

        public void run() {

            try {
                sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            while (flag) {
                if (curHeight >= buttonMaxHeight) {
                    flag = false;

                    break;
                }
                curHeight += buttonAddHeight;

                bigHandler.sendMessage(bigHandler.obtainMessage(curButton, curHeight));

                try {
                    sleep(30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }


    /**
     * button变小线程
     */
    class MySmallThread extends Thread {

        private int curButton = 0;
        private int curHeight = 0;
        private int buttonMaxHeight = 0;
        private int buttonAddHeight = 0;

        private boolean flag = true;

        public MySmallThread(int curButton, int buttonMaxHeight, int buttonAddHeight, int curHeight) {

            this.curButton = curButton;
            this.buttonMaxHeight = buttonMaxHeight;
            this.buttonAddHeight = buttonAddHeight;
            this.curHeight = curHeight;

        }

        public void run() {

            while (flag) {
                if (curHeight <= 0) {
                    flag = false;

                    break;
                }
                curHeight -= buttonAddHeight;

                smallHandler.sendMessage(smallHandler.obtainMessage(curButton, curHeight));

                try {
                    sleep(30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private void setExamType() {
        switch (examType) {
            case 0:   //考证
                iconList = highExamImgList;
                textList = diplomaTextList;
                buttonNum = diplomaTextList.length;
                firstLineButtonNum = 8;
                break;
            case 1:   //考研
                iconList = highExamImgList;
                textList = highExamTextList;
                buttonNum = highExamTextList.length;
                firstLineButtonNum = 10;
                break;
            case 2:   //考公务员
                iconList = highExamImgList;
                textList = officialTextList;
                buttonNum = officialTextList.length;
                firstLineButtonNum = 6;
                break;
        }
    }

    private void clickItem(int i){

        switch (examType){
            case 0:
                if (i == 2){
                    Intent intent = new Intent(this.getContext(), AddExamGuideAndBefore.class);
                    intent.putExtra("guideOrBefore",i);
                    intent.putExtra("second", second);
                    this.getContext().startActivity(intent);
                }else{
                    Intent intent = new Intent(this.getContext(),ZyWebViewActivity.class);
                    intent.putExtra("second",second);
                    intent.putExtra("conNum","" + i);
                    intent.putExtra("exam",0);

                    this.getContext().startActivity(intent);
                }

                break;
            case 1:
                Intent intent1 = new Intent(this.getContext(),ReadExamItemActivity.class);
                intent1.putExtra("school",second);
                intent1.putExtra("conNum","" + i);

                this.getContext().startActivity(intent1);
                break;
            case 2:
                if (i == 13){
                    upDialog(second,i);
                }else {
                    Intent intent2 = new Intent(this.getContext(), ZyWebViewActivity.class);
                    intent2.putExtra("second", second);
                    intent2.putExtra("conNum", "" + i);
                    intent2.putExtra("exam", 2);

                    this.getContext().startActivity(intent2);
                }
                break;
        }

    }

    private void downloadThread(final String url) {
        Toast.makeText(this.getContext(), "开始下载", Toast.LENGTH_SHORT).show();
        // 触发下载任务！
        new Thread(new Runnable() {
            @Override
            public void run() {

                DefaultHttpClient client = MyHttpClient.getHttpClient();
                String filePath = MyHttpClient.doGetFileDownload(client, url);
                if (filePath != null) {
                    Log.d("TestApp", "下载完成！");
                    Message msg = downloadHandler.obtainMessage(0, "下载完成 " + filePath);
                    downloadHandler.sendMessage(msg);
                } else {
                    Log.d("TestApp", "下载失败！");
                    Message msg = downloadHandler.obtainMessage(0, "下载失败");
                    downloadHandler.sendMessage(msg);
                }
            }
        }).start();
    }


    private void upDialog(final String second,final int conNum) {
        new AlertDialog.Builder(this.getContext())
                .setTitle("下载职位表").setMessage("是否下载职位表？")
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startDownload(second, conNum);
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    private void startDownload(String second,int conNum){
        final Map<String,String> map = new HashMap<>();
        map.put("second",second);
        map.put("conNum","" + conNum);

        new Thread(){
            @Override
            public void run() {
                DefaultHttpClient client = MyHttpClient.getHttpClient();
                String res = MyHttpClient.doGet(client,urlOfficialContent,map,false);

                downloadHandler.sendMessage(downloadHandler.obtainMessage(1, res));
            }
        }.start();
    }

    private void checkButtonOnScreen(){
        bigList.clear();
        for (int i = 0; i < posList.size(); i++) {
            if (onScreen(posList.get(i))) {
                if (buttonList.get(i).getHeight() < buttonMaxHeight){
                    bigList.add(i);
                }
            } else {
                startMySmallThread(i);
            }
        }
        startBigThread();
    }

}
