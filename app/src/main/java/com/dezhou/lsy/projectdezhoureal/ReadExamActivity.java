package com.dezhou.lsy.projectdezhoureal;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import view.MyScroll;

/**
 * Created by zy on 15-8-17.
 */
public class ReadExamActivity extends Activity {
    MyScroll scroll;
    Button button;

    private String second;
    private String urlExamContent;
    private int examType;
    private TextView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.readexam_roll);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }

        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.blue);
        init ();
    }

    private void init(){
        second = this.getIntent().getStringExtra("second");
        urlExamContent = this.getIntent().getStringExtra("url");
        examType = this.getIntent().getIntExtra("examType", 0);
        back=(TextView)this.findViewById(R.id.backactivity);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        scroll = (MyScroll) this.findViewById(R.id.scroll);
        scroll.init(second,urlExamContent,examType);
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
