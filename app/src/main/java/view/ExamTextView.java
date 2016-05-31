package view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import org.apache.http.impl.client.DefaultHttpClient;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kaobanxml.XmlParser;
import network.MyHttpClient;
import utils.URLSet;

/**
 * Created by zy on 15-8-24.
 */
public class ExamTextView extends TextView{

    private static String url = URLSet.serviceUrl+"/kaoban/getFirst/";

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            String res = (String)msg.obj;
            if (res != null && !res.isEmpty()){
                String exam = XmlParser.firstExamParser(res);
                Log.d("kaoban","first  " + exam);
                String exam_exceptNum=removeNum(exam);
                setExam(exam_exceptNum);
            }
        }
    };

    public ExamTextView(Context context) {

        super(context,null,0);
    }

    public ExamTextView(Context context, AttributeSet attrs) {
        super(context, attrs,0);
    }

    public ExamTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public static String removeNum(String str) {
        String regEx = "[0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
//替换与模式匹配的所有字符（即数字的字符将被""替换）
        return m.replaceAll("").trim();
    }

    public  void getFirst(String second){
        network(second);
    }

    private void network(String second){
        final DefaultHttpClient client = MyHttpClient.getHttpClient();
        final Map<String,String> map = new HashMap<>();
        map.put("second", second);

        new Thread(){
            @Override
            public void run() {
                String res = MyHttpClient.doGet(client,url,map,false);
                handler.sendMessage(handler.obtainMessage(0,res));
            }
        }.start();
    }

    private void setExam(String exam){
        this.setText(exam);
    }

}
