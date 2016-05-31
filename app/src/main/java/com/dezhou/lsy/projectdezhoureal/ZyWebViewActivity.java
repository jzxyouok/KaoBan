package com.dezhou.lsy.projectdezhoureal;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import org.apache.http.impl.client.DefaultHttpClient;

import java.util.HashMap;
import java.util.Map;

import kaobanxml.XmlParser;
import network.MyHttpClient;
import tempvalue.ItemContent;
import utils.URLSet;


public class ZyWebViewActivity extends Activity{

    private final String highExamUrl = URLSet.serviceUrl+"/kaoban/highExam/scontent/";
    private final String officialUrl = URLSet.serviceUrl+"/kaoban/official/content/";
    private final String diplomaUrl = URLSet.serviceUrl+"/kaoban/diploma/content/";
    private final String guideUrl = URLSet.serviceUrl+"/kaoban/diploma/guide/";
    private static String mainUrl = URLSet.serviceUrl;

    private WebView webView;
    private Button fileButton;

    private int exam;
    private TextView back;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            String res = (String) msg.obj;
            ItemContent content = XmlParser.examItemParser(res);
            setData(content);
        }
    };

    Handler downloadHandler = new Handler() {
        @Override
        public void handleMessage (Message msg){
            String text = (String) msg.obj;
            Toast.makeText(ZyWebViewActivity.this, text, Toast.LENGTH_SHORT).show();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zywebview);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }

        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.blue);
        init();
    }

    private void init(){
        back=(TextView)this.findViewById(R.id.backactivity);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        fileButton = (Button) this.findViewById(R.id.button_file);

        // 设置webView
        webView = (WebView) findViewById(R.id.webview_zy);
        webViewSettings(webView);

        exam = getIntent().getIntExtra("exam",-1);

        String second;
        String conNum;
        String guide;
        int tag;


        switch (exam){
            case 0:
                tag = getIntent().getIntExtra("tag",-1);
                if (tag == 2){
                    guide = getIntent().getStringExtra("guide");
                    getGuide(guide,guideUrl);
                }else{
                    second = getIntent().getStringExtra("second");
                    conNum = getIntent().getStringExtra("conNum");
                    getOfficialOrDiplomaData(second, conNum,diplomaUrl);
                }
                break;
            case 1:
                String item = getIntent().getStringExtra("item");
                getHighExamData(item);
                break;
            case 2:
                second = getIntent().getStringExtra("second");
                conNum = getIntent().getStringExtra("conNum");
                getOfficialOrDiplomaData(second, conNum, officialUrl);
                break;
        }
    }

    private void webViewSettings(WebView webView) {
        webView.getSettings().setJavaScriptEnabled(false);
        webView.getSettings().setSupportZoom(false);
        webView.getSettings().setBuiltInZoomControls(false);
//        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN); //支持重新布局
//        webView.getSettings().setDefaultFixedFontSize(20);
//        webView.setBackgroundColor(getResources().getColor(R.color.transparent_grey));
    }


    private void getHighExamData(final String item){
        new Thread(){
            @Override
            public void run() {
                startHighExam(item);
            }
        }.start();
    }

    private void getOfficialOrDiplomaData(final String second, final String conNum, final String url){
        new Thread(){
            @Override
            public void run() {
                startOfficialOrDiploma(second, conNum, url);
            }
        }.start();
    }

    private void getGuide(final String guide, final String url){
        new Thread(){
            @Override
            public void run() {
                startGuide(guide, url);
            }
        }.start();
    }

    private void startHighExam(String item){
        Map<String,String> map = new HashMap<>();
        map.put("item",item);
        DefaultHttpClient client = MyHttpClient.getHttpClient();
        String res = MyHttpClient.doGet(client,highExamUrl,map,false);

        handler.sendMessage(handler.obtainMessage(1, res));
    }

    private void startOfficialOrDiploma(String second,String conNum,String url){
        Map<String,String> map = new HashMap<>();
        map.put("second",second);
        map.put("conNum",conNum);
        DefaultHttpClient client = MyHttpClient.getHttpClient();
        String res = MyHttpClient.doGet(client,url,map,false);

        handler.sendMessage(handler.obtainMessage(2, res));

    }

    private void startGuide(String guide,String url){
        Map<String,String> map = new HashMap<>();
        map.put("guide",guide);
        DefaultHttpClient client = MyHttpClient.getHttpClient();
        String res = MyHttpClient.doGet(client,url,map,false);

        handler.sendMessage(handler.obtainMessage(2, res));
    }

    private void setData(final ItemContent content){
        String CSS_STYLE = "<style type=\"text/css\">* {background-color:#f6f8f7; " +
                "font-size:16px;color:#8e8e8e}</style>";
        if (content == null){
            return ;
        }
        webView.loadDataWithBaseURL(null, CSS_STYLE + content.getContent(), "text/html", "utf-8", null);

        if (content.getFileName() != null && ! content.getFileName().isEmpty()){
            fileButton.setText(content.getFileName() + "点击下载");
            fileButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    upDialog(mainUrl + content.getFilePath());
                }
            });
        }
    }

    private void downloadThread(final String url) {
        Toast.makeText(this, "开始下载", Toast.LENGTH_SHORT).show();
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

    private void upDialog(final String url) {
        new AlertDialog.Builder(this)
                .setTitle("下载文件").setMessage("是否下载文件？")
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
