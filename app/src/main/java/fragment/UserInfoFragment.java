package fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.com.easemob.chatuidemo.DemoApplication;
import com.dezhou.lsy.projectdezhoureal.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.xmlpull.v1.XmlPullParser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import bean.UserInfoBean;
import kongjian.EduActivity;
import kongjian.SignActivity;
import tempvalue.MyKongjian;
import tempvalue.UserNum;
import utils.URLSet;


public class UserInfoFragment extends Fragment {

    private TextView mName;
    private TextView mBirthday;
    private TextView mdepartment;
    private TextView mschooltime;
    private TextView mSchool;
    private TextView msex;
    private TextView mhometown;

    private TextView mhabit;
    private TextView msign;
    private TextView mnumber;
    private View v;
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    private String signstring="";
    private String schoolstring="";
    private String numberstring="";
    private String department="";
    private String schooltime="";
    private String namestring="";
    private String sexstring="";
    private String habitstring="";
    private String birthdaystring="";
    private String hometownstring="";
    private LinearLayout edu_self,normal_self;
    private RelativeLayout resign;
    private Handler myHandler;
    private DemoApplication app;
    private String username="";
    private String localUserName="";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.personal, null);
        app=(DemoApplication)getActivity().getApplication();
        username=app.getName();
        localUserName= UserNum.userNum;

        initActive();
        initView();

        return v;
    }



    private void initActive() {

        mName = (TextView) v.findViewById(R.id.name);
        mBirthday = (TextView) v.findViewById(R.id.birthday);
        mdepartment = (TextView) v.findViewById(R.id.department);
        mschooltime = (TextView) v.findViewById(R.id.schooltime);

        mSchool = (TextView) v.findViewById(R.id.school);
        msex = (TextView) v.findViewById(R.id.sex);
        mhometown = (TextView) v.findViewById(R.id.hometown);
        mhabit = (TextView) v.findViewById(R.id.habit);
        msign = (TextView) v.findViewById(R.id.sign);
        mnumber = (TextView)v.findViewById(R.id.number);
        edu_self=(LinearLayout)v.findViewById(R.id.edu_self);
        normal_self = (LinearLayout)v.findViewById(R.id.normal_self);
        resign = (RelativeLayout)v.findViewById(R.id.resign);
        getUserInfoHttp(username);
    }



    private void initView() {

        if (!localUserName.equals(username)){
            resign.setEnabled(false);
            edu_self.setEnabled(false);
            normal_self.setEnabled(false);
        }
        resign.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), SignActivity.class);
                intent.putExtra("signpost", signstring);
                startActivityForResult(intent,1);

            }
        });
        edu_self.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(getActivity(),EduActivity.class);
                startActivityForResult(intent1, 1);
            }
        });
        normal_self.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(getActivity(),EduActivity.class);
                startActivityForResult(intent2, 1);
            }
        });
    }

//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//       // String sign = data.getExtras().getString("sign");
//        // 根据上面发送过去的请求吗来区别
//        switch (resultCode){
//            case -1:
//
//               myHandler.sendEmptyMessage(0x1122);
//
//                break;
//            default:
//                    break;
//
//
//        }
//    }
//

    /***
     * 得到用户信息
     */
    private void getUserInfoHttp(String userName) {

        String url = URLSet.serviceUrl+"/kaoban/userInformation/?userNum="+userName;
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new AsyncHttpResponseHandler() {

            UserInfoBean userInf;

            InputStream xml;
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                if (statusCode == 200) {
                    xml = new ByteArrayInputStream(responseBody);
                    try {
                        myHandler=
                                new Handler()
                                {
                                    public void handleMessage(android.os.Message msg)
                                    {
                                        if(msg.what==0x1122)
                                        {
                                            msign.setText("\t"+signstring);
                                            mSchool.setText("\t"+schoolstring);
                                            mnumber.setText("\t"+numberstring);
                                            mdepartment.setText("\t"+department);
                                            mschooltime.setText("\t"+schooltime);
                                            mName.setText("\t"+namestring);
                                            msex.setText("\t"+sexstring);
                                            mhabit.setText("\t"+habitstring);
                                            mBirthday.setText("\t"+birthdaystring);
                                            mhometown.setText("\t"+hometownstring);
                                        }
                                    };
                                };
                        new Thread()
                        {


                            public void run()
                            {
                                try {
                                    userInf = getUserInf(xml);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                signstring = userInf.getSign();
                                schoolstring = userInf.getSchool();
                                department = userInf.getDepartment();
                                schooltime = userInf.getSchooltime();
                                numberstring = userInf.getNumber();
                                namestring = userInf.getName();
                                sexstring = userInf.getSex();
                                habitstring =userInf.getHabit();
                                birthdaystring = userInf.getBirthday();
                                hometownstring = userInf.getHomeTown();

                                myHandler.sendEmptyMessage(0x1122);

                            };

                        }.start();
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

    /**
     * 用户信息解析
     * @param xml  InputStream
     * @return UserInfoBean
     * @throws Exception
     */
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

    public void onResume(){
        super.onResume();
        if (MyKongjian.fresh){
            getUserInfoHttp(username);
        }

    }
}
