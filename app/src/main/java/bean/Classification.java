package bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2015/8/1.
 */
//话题分类
public class Classification  implements Serializable {
    List<Topic> topics;
    String classificationName;
    int serial;

    public int getSerial() {
        return serial;
    }

    public void setSerial(int serial) {
        this.serial = serial;
    }

    public List<Topic> getTopics() {
        return topics;
    }

    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }

    public String getClassificationName() {
        return classificationName;
    }

    public void setClassificationName(String classificationName) {
        this.classificationName = classificationName;
    }
}
