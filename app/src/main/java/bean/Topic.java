package bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2015/7/29.
 */
public class Topic  implements Serializable {

    private String topicName;
    private int classification;//分类
    private String num;//标题ID
    private String topicNum;// 内容数量
    private String contentNum;
    private int serial;
    private String topicPic;

    public String getTopicPic() {
        return topicPic;
    }

    public void setTopicPic(String topicPic) {
        this.topicPic = topicPic;
    }

    public int getSerial() {
        return serial;
    }

    public void setSerial(int serial) {
        this.serial = serial;
    }

    public String getContentNum() {
        return contentNum;
    }

    public void setContentNum(String contentNum) {
        this.contentNum = contentNum;
    }

    public String getTopicNum() {
        return topicNum;
    }

    public void setTopicNum(String topicNum) {
        this.topicNum = topicNum;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public int getClassification() {
        return classification;
    }

    public void setClassification(int classification) {
        this.classification = classification;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }
}
