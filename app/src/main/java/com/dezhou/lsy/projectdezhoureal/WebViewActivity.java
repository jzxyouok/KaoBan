package com.dezhou.lsy.projectdezhoureal;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import network.SerializableMsg;


public class WebViewActivity extends ActionBarActivity {
    private WebView webView;
    private String url;

    SerializableMsg msg = null;
    String msgId = "";

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            String content = (String) msg.obj;
            Log.d("getpic",content);
            String CSS_STYLE ="<style>* {font-size:16px;line-height:20px;}p {color:#6d6d6d;}" +
                    "body {background-color:#f6f8f7;}</style>";
            webView.loadDataWithBaseURL(null,CSS_STYLE + content,"text/html","utf-8",null);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        // 设置webView
        webView = (WebView) findViewById(R.id.web_view);
        webViewSettings(webView);

        // 获取前边传来的ｕｒｌ　修改，传Ｍａｐ，解析获取Ｋｅｙ和Ｖａｌｕｅ，根据Ｋｅｙ来加载页面
        Bundle bundle = this.getIntent().getExtras();
        msg = (SerializableMsg) bundle.getSerializable("Message");
        Log.d("TestApp", "获取信息！：" + msg.getId() + "::" + msg.getMsg());
        msgId = msg.getId();
        // 开始请求
//        startLoadURL(msgId);
        new Thread(new Runnable() {
            @Override
            public void run() {
                getData(msg.getMsg());
            }
        }).start();

    }

    private void webViewSettings(WebView webView) {
        webView.getSettings().setJavaScriptEnabled(false);
        webView.getSettings().setSupportZoom(false);
        webView.getSettings().setBuiltInZoomControls(false);
//        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN); //支持重新布局
        webView.getSettings().setDefaultFixedFontSize(20);
//        webView.setBackgroundColor(getResources().getColor(R.color.transparent_grey));
    }

    /**
     * 开启WebView请求
     *
     * @param msgIdGet
     */
    private void startLoadURL(String msgIdGet) {
        switch (msgIdGet) {
            case "OfficialContent":
                int numTmp = msg.getIntNum();
                if (numTmp <= 10 && numTmp >= 1) {
                    // WebView显示
                    webView.loadUrl(msg.getMsg());
                } else {
                    // 下载文件的操作
                    // 返回前一个页面

                }
                break;
            case "DiplomaContent":
                int numTmpNext = msg.getIntNum();
                if (numTmpNext <= 7 && numTmpNext >= 1) {
                    // WebView显示
                    webView.loadUrl(msg.getMsg());
                } else {
                    // ｘｍｌ解析大纲和考前准备的内容
                    // 返回前一个页面
                }
                break;
            case "DiplomaGuide":
                webView.loadUrl(msg.getMsg());
                break;
            case "BeforeExamGuide":
                webView.loadUrl(msg.getMsg());
                break;
            case "HighExamContent":
                int numTmpThird = msg.getIntNum();
                if (numTmpThird <= 5 && numTmpThird >= 1) {
                    // WebView显示
                    webView.loadUrl(msg.getMsg());
                } else {
                    // ｘｍｌ解析大纲和考前准备的内容
                    // 返回前一个页面
                }
                break;
        }
    }

    private void getData(String url){
        try {
            URL newUrl = new URL(url);
            Log.d("getpic",url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) newUrl.openConnection();
            httpURLConnection.setRequestMethod("GET");

            InputStream inputStream = httpURLConnection.getInputStream();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            String content = "";
            while ((line = bufferedReader.readLine()) != null){
                content += line + "\n";
            }
            handler.sendMessage(handler.obtainMessage(1,content));

        } catch (Exception e) {
            e.printStackTrace();
            Log.d("getpic","exception");
        }

    }


}
