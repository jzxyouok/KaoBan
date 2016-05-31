package network;

/**
 * Created by lsy on 15-2-5.
 */
public class Friend {

    private String userNum;
    /**
     * 用户序号（系统分配）
     */
    private String userName;
    /**
     * 用户名
     */
    private String headImg;

    public Friend() {

    }

    public Friend(String userNum, String userName, String headImg) {
        this.userNum = userNum;
        this.userName = userName;
        this.headImg = headImg;
    }

    /**
     * 用户头像（Ｂａｓｅ６４编码）
     */


    public String getUserNum() {
        return userNum;
    }

    public void setUserNum(String userNum) {
        this.userNum = userNum;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg.split(",")[1];
    }
}
