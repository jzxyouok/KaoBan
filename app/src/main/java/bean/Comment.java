package bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2015/8/3.
 */
/*
评论
 */
public class Comment implements Serializable {

    private String commentID;
    private String commendUserName;
    private String commendContent;
    private String commendTime;
    private List<SecondComment> secondComments;

    private String headImg;
    private String commentUserNum;
    private String commentImg;
    private String commendImgSize;
    private String commendSmimg="";
    private String commendSmimgSize;

    public String getCommendImgSize() {
        return commendImgSize;
    }

    public void setCommendImgSize(String commendImgSize) {
        this.commendImgSize = commendImgSize;
    }

    public String getCommendSmimg() {
        return commendSmimg;
    }

    public void setCommendSmimg(String commendSmimg) {
        this.commendSmimg = commendSmimg;
    }

    public String getCommendSmimgSize() {
        return commendSmimgSize;
    }

    public void setCommendSmimgSize(String commendSmimgSize) {
        this.commendSmimgSize = commendSmimgSize;
    }

    public String getCommentImg() {
        return commentImg;
    }

    public void setCommentImg(String commentImg) {
        this.commentImg = commentImg;
    }

    public String getCommentUserNum() {
        return commentUserNum;
    }

    public void setCommentUserNum(String commentUserNum) {
        this.commentUserNum = commentUserNum;
    }

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }



    public String getCommentID() {
        return commentID;
    }

    public void setCommentID(String commentID) {
        this.commentID = commentID;
    }

    public String getCommendUserName() {
        return commendUserName;
    }

    public void setCommendUserName(String commendUserName) {
        this.commendUserName = commendUserName;
    }

    public String getCommendContent() {
        return commendContent;
    }

    public void setCommendContent(String commendContent) {
        this.commendContent = commendContent;
    }

    public String getCommendTime() {
        return commendTime;
    }

    public void setCommendTime(String commendTime) {
        this.commendTime = commendTime;
    }

    public List<SecondComment> getSecondComments() {
        return secondComments;
    }

    public void setSecondComments(List<SecondComment> secondComments) {
        this.secondComments = secondComments;
    }
}
