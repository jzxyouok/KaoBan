package utils;

/**
 * Created by lsy on 15-2-23.
 */
public class URLSet {


    private static URLSet urlSet = null;
    public static String serviceUrl="http://115.28.35.2:8802";
    public static String APP_KEY_SMS="a22a74a08cb2";
    public static String APP_SECRETE_SMS="13c2c82f1167599f3d9de965acc9f31d";
    public static final String articalLikeURL="http://115.28.35.2:8802/kaoban/likeArtical/";
    public static final String articalStoreURL="http://115.28.35.2:8802/kaoban/store/";
    public static final String uploadFile="http://115.28.35.2:8802/kaoban/uploadDoc/";
    public static final String getStore="http://115.28.35.2:8802/kaoban/getStore/";
    public static final String APP_ID_QQ="1104737725";
    public static final String APP_KEY_QQ="OowF5SKmfEXCiWni";
    public static final String APP_ID_WX="wx90ab084664d4bd5a";
    public static final String APP_KEY_WX="5ae73c1360f720416d8aa767e840c17b";
    public static final String login_third="http://115.28.35.2:8802/kaoban/thirdLogin/";
    public static final String deleteArtical="http://115.28.35.2:8802/kaoban/deleteArtical/";
    public static final String about=serviceUrl+"/kaoban/about/";
    /**
     * 注册地址
     */

    private static final String urlRegister = "http://115.28.35.2:8802/kaoban/register/";
    /**
     * 登陆地址
     */
    private static final String urlLogin = "http://115.28.35.2:8802/kaoban/login/";

    /*
    发送帖子
     */
    private static final String sendNewArtical="http://115.28.35.2:8802/kaoban/outputArtical/";

    public static final String checkVersion="http://115.28.35.2:8802/kaoban/version/";

    /**
     * 评论地址
     */
    private static final String urlSendPinglun = "http://115.28.35.2:8802/kaoban/comment/";

    private static final String getShuoshuo = "http://115.28.35.2:8802/kaoban/readArtical/";

    public static final String getSimplePeople="http://115.28.35.2:8802/kaoban/samePeople/";


    public static final String getClassificationURL="http://115.28.35.2:8802/kaoban/getTopic/";
    public static final String getArticalListURL="http://115.28.35.2:8802/kaoban/showArtical/";

    public static final String getArticalInfo="http://115.28.35.2:8802/kaoban/readArtical/";
    public static  final String forgetPassWord="http://115.28.35.2:8802/kaoban/forgetPwd/";
    public static final String getPersonZoomURL="http://115.28.35.2:8802/kaoban/getUserArtical/?userNum=00000031";
    public static final String removeLike="http://115.28.35.2:8802/kaoban/removeLike/";
    public static final String changeInf ="http://115.28.35.2:8802/kaoban/changeInf/";
    public static final String suggestion="http://115.28.35.2:8802/kaoban/sugg/";
    public static final String schoolInf = "http://115.28.35.2:8802/kaoban/school/getCollege/";
    public static  final String getMyArtical="http://115.28.35.2:8802/kaoban/getUserArtical";
    public static final String getGallery="http://115.28.35.2:8802/kaoban/getTopTopic";
    public static final String addNewTopic=serviceUrl+"/kaoban/addTopic/";
    // 删除资料
    public static final String deleteDoc="http://115.28.35.2:8802/kaoban/deleteDoc/";
    public static final String getNewTopicImg=serviceUrl+"/kaoban/toppic/";
    private URLSet() {

    }

    public static String getSendNewArtical() {
        return sendNewArtical;
    }

    public static URLSet getInstance() {
        if (urlSet == null) {
            urlSet = new URLSet();
        }
        return urlSet;
    }

    public String getUrlRegister() {
        return urlRegister;
    }

    public String getUrlLogin() {
        return urlLogin;
    }

    public static String getUrlsendpinglun() {
        return urlSendPinglun;
    }

    public static String getGetshuoshuo() {
        return getShuoshuo;
    }

}
