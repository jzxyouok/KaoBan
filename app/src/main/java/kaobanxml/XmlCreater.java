package kaobanxml;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lsy on 15-1-31.
 */
public class XmlCreater {
    private static final String SHAREDPREFERENCESTEST = "sharedPreferencesTest";

    /**
     * 保存登陆次数的操作
     * @param context 上下文
     * @param number 登陆次数
     */
    public static void writeEnterNumber(Context context,int number){
        SharedPreferences sp = context.getSharedPreferences("enterNumber",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("enter_number",number);
        editor.commit();
    }

    /**
     * 读取登陆次数的操作
     * @param context 上下文
     * @return 登陆次数
     */
    public static int readEnterNumber(Context context){
        SharedPreferences sp = context.getSharedPreferences("enterNumber",Context.MODE_PRIVATE);
        int enterNumber = sp.getInt("enter_number",-1);
        return enterNumber;
    }

    /**
     * 保存用户名和密码到本地的操作
     * @param context 上下文
     * @param email 注册邮箱
     * @param password 密码
     * @return true 保存成功
     * 　　　　 false 保存失败
     */
    public static void savePassword(Context context,String email,String password){
        // 创建userinfo的ｘｍｌ文件
        SharedPreferences sp = context.getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        // 获取编辑的Ｅｄｉｔｏｒ
        SharedPreferences.Editor editor = sp.edit();
        if(!"".equals(email)){
            editor.putString("email",email);
        }
        // 需要加密算法的编写
        if(!"".equals(password)) {
            editor.putString("password", password);
        }
        editor.commit();
        Log.d(SHAREDPREFERENCESTEST,"写入成功！");
    }

    /**
     * 获取用户信息并添加到Ｌｉｓｔ中
     * @param context 上下文
     * @return 载有用户信息的Ｌｉｓｔ
     */
    public static List<NameValuePair> getUserInfo(Context context) {
        SharedPreferences sp = context.getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        NameValuePair npEmail = new BasicNameValuePair("email", sp.getString("email", "READ ERROR"));
        NameValuePair npPw = new BasicNameValuePair("password", sp.getString("password", ""));
        list.add(npEmail);
        list.add(npPw);
        return list;
    }

}
