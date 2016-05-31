package bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2015/8/3.
 */
public class SecondComment implements Serializable {
    private String secondCommendUserNum;
    private String secondCommendUserName;
    private String secondCommendContent;
    private String secondCommendTime;
private String secondCommendHeadImg;
    private String secondCommendToUserName;
    private String secondCommendToUserNum;

    public String getSecondCommendToUserName() {
        return secondCommendToUserName;
    }

    public void setSecondCommendToUserName(String secondCommendToUserName) {
        this.secondCommendToUserName = secondCommendToUserName;
    }

    public String getSecondCommendToUserNum() {
        return secondCommendToUserNum;
    }

    public void setSecondCommendToUserNum(String secondCommendToUserNum) {
        this.secondCommendToUserNum = secondCommendToUserNum;
    }

    public String getSecondCommendHeadImg() {
        return secondCommendHeadImg;
    }

    public void setSecondCommendHeadImg(String secondCommendHeadImg) {
        this.secondCommendHeadImg = secondCommendHeadImg;
    }

    public String getSecondCommendUserName() {
        return secondCommendUserName;
    }

    public void setSecondCommendUserName(String secondCommendUserName) {
        this.secondCommendUserName = secondCommendUserName;
    }

    public String getSecondCommendContent() {
        return secondCommendContent;
    }

    public void setSecondCommendContent(String secondCommendContent) {
        this.secondCommendContent = secondCommendContent;
    }

    public String getSecondCommendTime() {
        return secondCommendTime;
    }

    public void setSecondCommendTime(String secondCommendTime) {
        this.secondCommendTime = secondCommendTime;
    }

    public String getSecondCommendUserNum() {
        return secondCommendUserNum;
    }

    public void setSecondCommendUserNum(String secondCommendUserNum) {
        this.secondCommendUserNum = secondCommendUserNum;
    }
}
