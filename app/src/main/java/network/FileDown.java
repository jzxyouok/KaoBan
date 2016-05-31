package network;

/**
 * Created by wenjun on 2015/8/21.
 */
public class FileDown {
    private String userName;
    private String headImg;
    private String userNum;
    private String title;
    private String file;
    private String docNum;
    private String school;
    private String date;

    public String getDocNum() {
        return docNum;
    }

    public void setDocNum(String docNum) {
        this.docNum = docNum;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public FileDown(){

    }
    public FileDown(String userName, String headImg, String userNum, String title, String file){
        this.userName=userName;
        this.headImg=headImg;
        this.userNum=userNum;
        this.title=title;
        this.file=file;
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
        this.headImg = headImg;
    }

    public String getUserNum() {
        return userNum;
    }

    public void setUserNum(String userNum) {
        this.userNum = userNum;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }
}
