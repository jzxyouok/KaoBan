package kaobanxml;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 读写ＸＭＬ文件的综合类,单例模式
 * Created by lsy on 15-2-15.
 */
public class XmlSharedPreferences {

    private static XmlSharedPreferences xmlSharedPreferences = null;
    private static SharedPreferences sharedPreferences = null;
    private static SharedPreferences.Editor spEditor = null;

    private XmlSharedPreferences(){

    }

    public static XmlSharedPreferences getInstance() {
        if(xmlSharedPreferences == null){
            xmlSharedPreferences = new XmlSharedPreferences();
        }
        return xmlSharedPreferences;
    }
    /**
     * 初始化读写的组件
     * @param context
     * @return
     */
    public SharedPreferences getMySharedPreferences(Context context){
        if(sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences("Kaoban.xml", Context.MODE_PRIVATE);
        }
        return sharedPreferences;
    }

    /**
     * 初始化读写的组件
     * @param context
     * @return
     */
    public SharedPreferences.Editor getMySharedPreferencesEditor(Context context){
        if(sharedPreferences == null || spEditor == null) {
            sharedPreferences = context.getSharedPreferences("Kaoban.xml", Context.MODE_PRIVATE);
            spEditor = sharedPreferences.edit();
        }
        return spEditor;
    }

    /**
     * 写入进入的次数
     * @param num 进入次数
     */
    public void writeEnterNum(SharedPreferences.Editor editor, int num){
        editor.putInt("EnterNumber", num);
        editor.commit();
    }

    /**
     * 读取进入的次数
     * @return 进入的次数
     */
    public int readEnterNum(SharedPreferences sharedPreferencesGet){
        int num = sharedPreferencesGet.getInt("EnterNumber",0);
        return num;
    }

    /**
     * 用户名写入,邮箱格式
     * @param username 用户名
     */
    public void writeUsername(SharedPreferences.Editor editorGet, String username) {
        editorGet.putString("UsernameEmail",username);
        editorGet.commit();
    }

    /**
     * 读取用户名
     * @return 邮箱
     */
    public String readUsername(SharedPreferences sharedPreferencesGet){
        String username = sharedPreferencesGet.getString("UsernameEmail","noNameGet");
        return username;
    }

    /**
     * 密码写入,需要加密
     * @param password 密码
     */
    public void writePassword(SharedPreferences.Editor editorGet, String password) {
        editorGet.putString("password", password);
        editorGet.commit();
    }

    /**
     * 读取密码
     * @return 密码
     */
    public String readPassword(SharedPreferences sharedPreferencesGet){
        String username = sharedPreferencesGet.getString("password","noPasswordGet");
        return username;
    }


}
