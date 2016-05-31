package network;

import java.io.Serializable;

/**
 * Created by lsy on 15-2-7.
 */
public class SerializableMsg implements Serializable {
    private String id;
    private String msg;
    private int intNum;

    public int getIntNum() {
        return intNum;
    }

    public void setIntNum(int intNum) {
        this.intNum = intNum;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
