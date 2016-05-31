package utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lsy on 15-2-23.
 */
public class UtilsGet {

    /**
     * 使用正则表达式判断Ｅｍａｉｌ格式字符！
     *
     * @param email 需要判断的字符串
     * @return　true 是Ｅｍａｉｌ格式
     * 　　　　　false 不是Ｅｍａｉｌ格式
     */
    public static boolean isEmail(String email) {
        final String RE = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+.[a-zA-Z0-9_-]+$";
        Pattern pattern = Pattern.compile(RE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

}
