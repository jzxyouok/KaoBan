package exitapp;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 退出程序的封装操作！
 * Created by lsy on 15-2-21.
 */
public class ExitApplication extends Application{
    // 是否退出的判断，需要传入前面传来的参数
    private boolean isExitNext = false;

    // 保存需要Kill的程序中的Activity
    private List<Activity> activityList = new LinkedList<Activity>();

    private static ExitApplication exitInstance;
    // 单例模式的方法
    public static ExitApplication getInstance(){
        if(exitInstance == null){
            exitInstance = new ExitApplication();
        }
        return exitInstance;
    }
    // 将Activity添加到List中
    public void addActivity(Activity activity){
        activityList.add(activity);
    }
    // 退出
    public void exit(){
        for(Activity activity:activityList){
            activity.finish();
        }
        System.exit(0);
    }
}
