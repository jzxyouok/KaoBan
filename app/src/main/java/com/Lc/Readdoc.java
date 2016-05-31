package com.Lc;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dezhou.lsy.projectdezhoureal.R;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import java.io.File;
import java.net.URL;
import java.util.List;

import utils.URLSet;

public class Readdoc extends Activity {
    private Button openfile,downloadfile;
    private Handler myHandler;
    private TextView textView;
    private ImageView doc,back_SignActivity;
    FinalHttp fh;
    private String filedowned;
    private File file1;
    private final String[][] MIME_MapTable={
            //{后缀名， MIME类型}
            {".3gp",    "video/3gpp"},
            {".apk",    "application/vnd.android.package-archive"},
            {".asf",    "video/x-ms-asf"},
            {".avi",    "video/x-msvideo"},
            {".bin",    "application/octet-stream"},
            {".bmp",    "image/bmp"},
            {".c",  "text/plain"},
            {".class",  "application/octet-stream"},
            {".conf",   "text/plain"},
            {".cpp",    "text/plain"},
            {".doc",    "application/msword"},
            {".docx",   "application/vnd.openxmlformats-officedocument.wordprocessingml.document"},
            {".xls",    "application/vnd.ms-excel"},
            {".xlsx",   "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"},
            {".exe",    "application/octet-stream"},
            {".gif",    "image/gif"},
            {".gtar",   "application/x-gtar"},
            {".gz", "application/x-gzip"},
            {".h",  "text/plain"},
            {".htm",    "text/html"},
            {".html",   "text/html"},
            {".jar",    "application/java-archive"},
            {".java",   "text/plain"},
            {".jpeg",   "image/jpeg"},
            {".jpg",    "image/jpeg"},
            {".js", "application/x-javascript"},
            {".log",    "text/plain"},
            {".m3u",    "audio/x-mpegurl"},
            {".m4a",    "audio/mp4a-latm"},
            {".m4b",    "audio/mp4a-latm"},
            {".m4p",    "audio/mp4a-latm"},
            {".m4u",    "video/vnd.mpegurl"},
            {".m4v",    "video/x-m4v"},
            {".mov",    "video/quicktime"},
            {".mp2",    "audio/x-mpeg"},
            {".mp3",    "audio/x-mpeg"},
            {".mp4",    "video/mp4"},
            {".mpc",    "application/vnd.mpohun.certificate"},
            {".mpe",    "video/mpeg"},
            {".mpeg",   "video/mpeg"},
            {".mpg",    "video/mpeg"},
            {".mpg4",   "video/mp4"},
            {".mpga",   "audio/mpeg"},
            {".msg",    "application/vnd.ms-outlook"},
            {".ogg",    "audio/ogg"},
            {".pdf",    "application/pdf"},
            {".png",    "image/png"},
            {".pps",    "application/vnd.ms-powerpoint"},
            {".ppt",    "application/vnd.ms-powerpoint"},
            {".pptx",   "application/vnd.openxmlformats-officedocument.presentationml.presentation"},
            {".prop",   "text/plain"},
            {".rc", "text/plain"},
            {".rmvb",   "audio/x-pn-realaudio"},
            {".rtf",    "application/rtf"},
            {".sh", "text/plain"},
            {".tar",    "application/x-tar"},
            {".tgz",    "application/x-compressed"},
            {".txt",    "text/plain"},
            {".wav",    "audio/x-wav"},
            {".wma",    "audio/x-ms-wma"},
            {".wmv",    "audio/x-ms-wmv"},
            {".wps",    "application/vnd.ms-works"},
            {".xml",    "text/plain"},
            {".z",  "application/x-compress"},
            {".zip",    "application/x-zip-compressed"},
            {"",        "*/*"}
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_readdoc);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }

        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.blue);
        String title = getIntent().getStringExtra("title");
        filedowned = getIntent().getStringExtra("filepath");
        openfile = (Button)findViewById(R.id.open);
        textView = (TextView)findViewById(R.id.textViewfile);
        doc=(ImageView)findViewById(R.id.doc);
        downloadfile=(Button)findViewById(R.id.download);
        back_SignActivity=(ImageView)findViewById(R.id.back_SignActivity);
        back_SignActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        downloadfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                URL url;
                String dir = Environment.getExternalStorageDirectory().toString() + "/kaoban/";
                String url1 = URLSet.serviceUrl+ filedowned;
                String str2="";
                str2 = url1.replaceAll(" ", "");
                String fileName = str2.substring(str2.lastIndexOf("/") + 1);
                file1 = new File(dir+fileName);
                if(file1.exists()){
                    file1.delete();
                }
                fh=new FinalHttp();
                fh.download(url1,dir+fileName,
                        new AjaxCallBack<File>() {
                            @Override
                            public void onStart() {
                                super.onStart();
                                Toast.makeText(getApplicationContext(), "开始下载资料", Toast.LENGTH_SHORT).show();
                            }
                            @SuppressLint("DefaultLocale")
                            @Override
                            public void onLoading(long count, long current) {
                                super.onLoading(count, current);
                                int progress=0;
                                if (current != count && current != 0) {
                                    progress = (int) (current / (float) count * 100);
                                } else {
                                    progress = 100;
                                }
                                textView.setText("进度："+progress+"%");
                            }
                            @Override
                            public void onSuccess(File t) {
                                super.onSuccess(t);
                                Toast.makeText(getApplicationContext(), "下载完成", Toast.LENGTH_SHORT).show();
                                textView.setText(t==null?"null":t.getAbsoluteFile().toString());
                            }
                            public void onFailure(Throwable t, int errorNo,String strMsg) {
                                super.onFailure(t,strMsg);
                                Toast.makeText(getApplicationContext(), "下载失败", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });


        openfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (file1==null){
                    Toast.makeText(getApplicationContext(),"您还没下载文档，请先下载",Toast.LENGTH_LONG).show();
                }
                else{
                    openFile(file1);
                }

            }
        });
    }

    private void openFile(File file) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //设置intent的Action属性
        intent.setAction(Intent.ACTION_VIEW);
        if (isIntentExisting(getApplicationContext(),Intent.ACTION_VIEW)){
            //获取文件file的MIME类型
            String type = getMIMEType(file);
            //设置intent的data和Type属性。
            intent.setDataAndType(/*uri*/Uri.fromFile(file), type);
            //跳转
            startActivity(intent);
        }
        else {
            Toast.makeText(getApplicationContext(),"您的手机还未安装任何应用",Toast.LENGTH_LONG).show();
        }

    }

    private String getMIMEType(File file) {
        String type="*/*";
        String fName = file.getName();
        //获取后缀名前的分隔符"."在fName中的位置。
        int dotIndex = fName.lastIndexOf(".");
        if(dotIndex < 0){
            return type;
        }
    /* 获取文件的后缀名 */
        String end=fName.substring(dotIndex,fName.length()).toLowerCase();
        if(end=="")return type;
        //在MIME和文件类型的匹配表中找到对应的MIME类型。
        for(int i=0;i<MIME_MapTable.length;i++){
            if(end.equals(MIME_MapTable[i][0]))
                type = MIME_MapTable[i][1];
        }
        return type;
    }
    public boolean isIntentExisting(Context context, String action) {
        final PackageManager packageManager = context.getPackageManager();
        final Intent intent = new Intent(action);
        List<ResolveInfo> resolveInfo =
                packageManager.queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);
        if (resolveInfo.size() > 0) {
            return true;
        }
        return false;
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
