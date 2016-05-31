package kongjian;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.util.Xml;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.Lc.Department;
import com.com.easemob.chatuidemo.DemoApplication;
import com.dezhou.lsy.projectdezhoureal.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlPullParser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import bean.UserInfoBean;
import exitapp.ExitApplication;
import gghl.view.wheelcity.AddressData;
import gghl.view.wheelcity.OnWheelChangedListener;
import gghl.view.wheelcity.WheelView;
import gghl.view.wheelcity.adapters.AbstractWheelTextAdapter;
import gghl.view.wheelcity.adapters.ArrayWheelAdapter;
import gghl.view.wheelview.JudgeDate;
import gghl.view.wheelview.ScreenInfo;
import gghl.view.wheelview.WheelMain;
import ggl.zf.iosdialog.widget.MyAlertDialog;
import kaobanxml.XmlParser;
import kongjian.com.widget.ActionSheet;
import kongjian.demo.ProvanceActivity;
import network.MyHttpClient;
import tempvalue.MyKongjian;
import utils.URLSet;

/**
 * Created by wenjun on 2015/7/26.
 */
public class EduActivity extends Activity {
    private String schoolstring="";
    private String numberstring="";
    private String department="";
    private String schooltime="";
    private String namestring="";
    private String sexstring="";
    private String habitstring="";
    private String birthdaystring="";
    private String hometownstring="";



    private TextView mBirthday;
    private TextView mdepartment;
    private TextView mschooltime;
    private TextView mSchool;
    private TextView msex;
    private TextView mhometown;
    private TextView mnumber;
    private EditText mName,mhabit;
    private ImageView back;
    private List<NameValuePair> listArtical;
    WheelMain wheelMain;
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private String cityTxt;
    private String username="";
    private DemoApplication app;
    private RelativeLayout reSchool,reSchooltime,reSex,reHomeTown,reBirthday,postdepart;
    private Handler handler=new Handler(){
        public void handleMessage(Message msg){
            switch (msg.what){

                case 0x238:
                    Bundle bundle2=new Bundle();
                    bundle2=msg.getData();
                    String sendArticalXml=bundle2.getString("key");
                    String result= XmlParser.xmlResultGet(sendArticalXml);
                    if(result.equals("success")){
                        Toast.makeText(EduActivity.this,"发布成功",Toast.LENGTH_SHORT).show();
                        getUserInfoHttp(username);
                    }
                    else{
                        Toast.makeText(EduActivity.this,"发布失败",Toast.LENGTH_SHORT).show();

                    }
                    break;


            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eduactivity);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }

        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.blue);
        ExitApplication.getInstance().addActivity(this);
        app = (DemoApplication)getApplication();
        username = app.getName();
        init();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyKongjian.fresh=true;
                finish();
            }
        });
        postdepart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_school = new Intent(EduActivity.this, Department.class);
                String []str=schoolstring.split(" ");
                String school="";
                for (int i=0;i<str.length;i++){
                    school=str[1];
                }
                intent_school.putExtra("school",school);
                startActivity(intent_school);
                finish();
            }
        });
        reSchool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(EduActivity.this, ProvanceActivity.class);
                String eduinfomation="edu";
                intent1.putExtra("school_info",eduinfomation);
                startActivity(intent1);
                finish();
            }
        });
        reHomeTown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View v = dialogm();
                final MyAlertDialog dialog1 = new MyAlertDialog(EduActivity.this)
                        .builder()
                        .setTitle("家庭住址")

                        .setView(v)
                        .setNegativeButton("取消", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        });
                dialog1.setPositiveButton("保存", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mhometown.setText(cityTxt);
                        postToServer("hometown", cityTxt);
                    }
                });
                dialog1.show();
            }
        });


        reSex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setTheme(R.style.ActionSheetStyleIOS7);
                showActionSheet();
            }
        });
        reSchooltime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTheme(R.style.ActionSheetStyleIOS7);
                showSchoolTimeSheet();
            }
        });


        mName.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                mName.setSelection(mName.length());
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    postToServer("name", mName.getText().toString().trim());
                }
                return false;
            }
        });

        mhabit.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                mhabit.setSelection(mhabit.length());
                if (keyCode == KeyEvent.KEYCODE_ENTER) {

                    postToServer("habit", mhabit.getText().toString().trim());
                }
                return false;
            }
        });

        reBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater1 = LayoutInflater.from(EduActivity.this);
                final View timepickerview1 = inflater1.inflate(R.layout.timepicker1,
                        null);
                ScreenInfo screenInfo1 = new ScreenInfo(EduActivity.this);
                wheelMain = new WheelMain(timepickerview1);
                wheelMain.screenheight = screenInfo1.getHeight();
                String time1 = mBirthday.getText().toString();
                Calendar calendar1 = Calendar.getInstance();
                if (JudgeDate.isDate(time1, "yyyy-MM-dd")) {
                    try {
                        calendar1.setTime(dateFormat.parse(time1));
                    } catch (ParseException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                int year1 = calendar1.get(Calendar.YEAR);
                int month1 = calendar1.get(Calendar.MONTH);
                int day1 = calendar1.get(Calendar.DAY_OF_MONTH);
                wheelMain.initDateTimePicker(year1, month1, day1);
                final MyAlertDialog dialog = new MyAlertDialog(EduActivity.this)
                        .builder()
                        .setTitle("设置时间")

                        .setView(timepickerview1)
                        .setNegativeButton("取消", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        });
                dialog.setPositiveButton("保存", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBirthday.setText(wheelMain.getTime());
                        postToServer("brithday", wheelMain.getTime());
                    }
                });
                dialog.show();
            }
        });


    }

    private View dialogm() {

        View contentView = LayoutInflater.from(this).inflate(
                R.layout.wheelcity_cities_layout, null);
        final WheelView country = (WheelView) contentView
                .findViewById(R.id.wheelcity_country);
        country.setVisibleItems(3);
        country.setViewAdapter(new CountryAdapter(this));

        final String cities[][] = AddressData.CITIES;
        final String ccities[][][] = AddressData.COUNTIES;
        final WheelView city = (WheelView) contentView
                .findViewById(R.id.wheelcity_city);
        city.setVisibleItems(0);

        // 地区选择
        final WheelView ccity = (WheelView) contentView
                .findViewById(R.id.wheelcity_ccity);
        ccity.setVisibleItems(0);// 不限城市

        country.addChangingListener(new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                updateCities(city, cities, newValue);
                cityTxt = AddressData.PROVINCES[country.getCurrentItem()]

                        + AddressData.CITIES[country.getCurrentItem()][city
                        .getCurrentItem()]

                        + AddressData.COUNTIES[country.getCurrentItem()][city
                        .getCurrentItem()][ccity.getCurrentItem()];
            }
        });

        city.addChangingListener(new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                updatecCities(ccity, ccities, country.getCurrentItem(),
                        newValue);
                cityTxt = AddressData.PROVINCES[country.getCurrentItem()]

                        + AddressData.CITIES[country.getCurrentItem()][city
                        .getCurrentItem()]

                        + AddressData.COUNTIES[country.getCurrentItem()][city
                        .getCurrentItem()][ccity.getCurrentItem()];
            }
        });

        ccity.addChangingListener(new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                cityTxt = AddressData.PROVINCES[country.getCurrentItem()]

                        + AddressData.CITIES[country.getCurrentItem()][city
                        .getCurrentItem()]

                        + AddressData.COUNTIES[country.getCurrentItem()][city
                        .getCurrentItem()][ccity.getCurrentItem()];
            }
        });

        country.setCurrentItem(1);// 设置北京
        city.setCurrentItem(1);
        ccity.setCurrentItem(1);
        return contentView;

    }
    /**
     * Updates the city wheel
     */
    private void updateCities(WheelView city, String cities[][], int index) {
        ArrayWheelAdapter<String> adapter = new ArrayWheelAdapter<String>(this,
                cities[index]);
        adapter.setTextSize(18);
        city.setViewAdapter(adapter);
        city.setCurrentItem(0);
    }

    /**
     * Updates the ccity wheel
     */
    private void updatecCities(WheelView city, String ccities[][][], int index,
                               int index2) {
        ArrayWheelAdapter<String> adapter = new ArrayWheelAdapter<String>(this,
                ccities[index][index2]);
        adapter.setTextSize(18);
        city.setViewAdapter(adapter);
        city.setCurrentItem(0);
    }

    /**
     * Adapter for countries
     */
    private class CountryAdapter extends AbstractWheelTextAdapter {
        // Countries names
        private String countries[] = AddressData.PROVINCES;

        /**
         * Constructor
         */
        protected CountryAdapter(Context context) {
            super(context, R.layout.wheelcity_country_layout, NO_RESOURCE);
            setItemTextResource(R.id.wheelcity_country_name);
        }

        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            View view = super.getItem(index, cachedView, parent);
            return view;
        }

        @Override
        public int getItemsCount() {
            return countries.length;
        }

        @Override
        protected CharSequence getItemText(int index) {
            return countries[index];
        }
    }



    public void showSchoolTimeSheet(){
        final ActionSheet menuView = new ActionSheet(this);

        menuView.setCancelButtonTitle("取消");// before add items
        Time time = new Time("GMT+8");
        time.setToNow();
        int year = time.year;
        String currentyear = year +"年";
        String currentyear1 = year-1+"年";
        String currentyear2 = year-2+"年";
        String currentyear3 = year-3+"年";
        String currentyear4 = year-4+"年";
        String currentyear5 = year-5+"年";
        String currentyear6 = year-6+"年";
        String currentyear7 = year-7+"年";
        String currentyear8 = year-8+"年";
        final List<String> yearlist = new ArrayList<>();
        yearlist.add(currentyear);yearlist.add(currentyear1);yearlist.add(currentyear2);
        yearlist.add(currentyear3);yearlist.add(currentyear4);yearlist.add(currentyear5);
        yearlist.add(currentyear6);yearlist.add(currentyear7);yearlist.add(currentyear8);
        menuView.addItems(currentyear, currentyear1, currentyear2, currentyear3, currentyear4, currentyear5, currentyear6, currentyear7, currentyear8);
        menuView.setItemClickListener(new ActionSheet.MenuItemClickListener() {
            @Override
            public void onItemClick(int itemPosition) {
                mschooltime.setText(yearlist.get(itemPosition));
                postToServer("schoolTime",yearlist.get(itemPosition));
            }
        });
        menuView.setCancelableOnTouchMenuOutside(true);
        menuView.showMenu();
    }

    public void showActionSheet()
    {
        final ActionSheet menuView = new ActionSheet(this);

        menuView.setCancelButtonTitle("cancel");// before add items
        menuView.addItems("男", "女");
        menuView.setItemClickListener(new ActionSheet.MenuItemClickListener() {
            @Override
            public void onItemClick(int itemPosition) {
                String sex = "";
                if (itemPosition == 0) {
                    msex.setText("男");
                    sex = "男";
                } else {
                    msex.setText("女");
                    sex = "女";
                }
                postToServer("sex",sex);

            }
        });
        menuView.setCancelableOnTouchMenuOutside(true);
        menuView.showMenu();
    }

    private void init(){
        mName = (EditText) findViewById(R.id.name);
        mBirthday = (TextView) findViewById(R.id.birthday);
        mdepartment = (TextView) findViewById(R.id.department);
        mschooltime = (TextView) findViewById(R.id.schooltime);
        reBirthday = (RelativeLayout)findViewById(R.id.reBirthday);
        reHomeTown = (RelativeLayout)findViewById(R.id.reHometown);
        reSchool = (RelativeLayout) findViewById(R.id.reSchool);
        reSchooltime = (RelativeLayout)findViewById(R.id.reSchooltime);
        postdepart=(RelativeLayout)findViewById(R.id.postdepart);
        reSex = (RelativeLayout) findViewById(R.id.reSex);
        mSchool = (TextView) findViewById(R.id.school);
        msex = (TextView) findViewById(R.id.sex);
        mhometown = (TextView) findViewById(R.id.hometown);
        mhabit = (EditText) findViewById(R.id.habit);
        mnumber = (TextView)findViewById(R.id.number);
        back = (ImageView)findViewById(R.id.back_SignActivity);

        getUserInfoHttp(username);
    }


    private void getUserInfoHttp(String userName) {

        String url = "http://115.28.35.2:8802/kaoban/userInformation/?userNum="+userName;
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new AsyncHttpResponseHandler() {

            UserInfoBean userInf;

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                if (statusCode == 200) {
                    InputStream xml = new ByteArrayInputStream(responseBody);
                    try {
                        userInf = getUserInf(xml);
                        schoolstring = userInf.getSchool();
                        department = userInf.getDepartment();
                        schooltime = userInf.getSchooltime();
                        numberstring = userInf.getNumber();
                        namestring = userInf.getName();
                        sexstring = userInf.getSex();
                        habitstring = userInf.getHabit();
                        birthdaystring = userInf.getBirthday();
                        hometownstring = userInf.getHomeTown();



                        mSchool.setText("\t" + schoolstring);
                        mnumber.setText("\t" + numberstring);
                        mdepartment.setText("\t" + department);
                        mschooltime.setText("\t" + schooltime);
                        mName.setText("\t" + namestring);
                        msex.setText("\t" + sexstring);
                        mhabit.setText("\t" + habitstring);
                        mBirthday.setText("\t" + birthdaystring);
                        mhometown.setText("\t" + hometownstring);


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                System.out.println("---->error");
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

    private UserInfoBean getUserInf(InputStream xml) throws Exception {

        UserInfoBean userInf = null;
        XmlPullParser parser = Xml.newPullParser(); // 利用Android的Xml工具类获取xmlPull解析器
        parser.setInput(xml, "UTF-8"); // 解析文件，设置字符集
        int event = parser.getEventType(); // 获取解析状态，返回的是int型数字状态
        while (event != XmlPullParser.END_DOCUMENT) {
            switch (event) {

                case XmlPullParser.START_TAG:
                    if ("Document".equals(parser.getName())) {
                        userInf = new UserInfoBean();
                    } else if ("name".equals(parser.getName())) {
                        userInf.setName(parser.nextText());
                    } else if ("number".equals(parser.getName())) {
                        userInf.setNumber(parser.nextText());
                    } else if ("sex".equals(parser.getName())) {
                        userInf.setSex(parser.nextText());
                    } else if ("birthday".equals(parser.getName())) {
                        userInf.setBirthday(parser.nextText());
                    } else if ("school".equals(parser.getName())) {
                        userInf.setSchool(parser.nextText());
                    } else if ("department".equals(parser.getName())) {
                        userInf.setDepartment(parser.nextText());
                    } else if ("schoolTime".equals(parser.getName())) {
                        userInf.setSchooltime(parser.nextText());
                    }else if ("hometown".equals(parser.getName())) {
                        userInf.setHomeTown(parser.nextText());
                    } else if ("province".equals(parser.getName())) {
                        userInf.setProvince(parser.nextText());
                    } else if ("habit".equals(parser.getName())) {
                        userInf.setHabit(parser.nextText());
                    }
                    else if ("job".equals(parser.getName())) {
                        userInf.setJob(parser.nextText());
                    }
                    else if ("sign".equals(parser.getName())) {
                        userInf.setSign(parser.nextText());
                    } else if ("headImg".equals(parser.getName())) {
                        userInf.setHeadImg(parser.nextText());
//						String str=parser.getAttributeValue(0);
//						int index=str.indexOf(",");
//						str=str.substring(index+1);
//						byte[] decode = Base64.decode(str.toString(), Base64.DEFAULT);
//						Bitmap bitmap = BitmapFactory.decodeByteArray(decode, 0, decode.length);
//						userInf.setHeadImg(bitmap);

                    }else if ("photoWall".equals(parser.getName())) {

                    }
                    break;
                case XmlPullParser.END_TAG:
                    break;
            }
            event = parser.next(); // 让指针指向下一个节点
        }
        //xml.close();
        return userInf;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            MyKongjian.fresh=true;
            finish();
            return true;
        } else
            return super.onKeyDown(keyCode, event);
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
