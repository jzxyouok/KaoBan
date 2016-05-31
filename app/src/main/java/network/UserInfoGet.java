package network;

/**
 * Created by lsy on 15-2-4.
 */
public class UserInfoGet {
    /**
     * <?xml version="1.0" encoding="utf-8"?>
     <Document>
     <name>user1</name>

     <sex>sex</sex>

     <brithday>brithday</brithday>

     <school>school</school>

     <hometown>hometown</hometown>

     <province>province</province>

     <habit>habit</habit>

     <job>job</job>

     <headImg src="data:image/jpg;base64,base64码" />
     </Document>
     * */
    private String name; /** 用户名称 */
    private String number; /** 编号 */
    private String sex; /** 性别 */
    private String birthday; /** 生日 */
    private String school; /** 所属学校 */
    private String hometown; /** 所在地 */
    private String province; /** 省份 */
    private String habit;/** 兴趣爱好 */
    private String job; /** 工作 */
    private String headImg; /** 头像 */

    public UserInfoGet(String name, String number, String sex, String birthday, String school,
                       String hometown, String province, String job, String habit, String headImg) {
        this.name = name;
        this.number = number;
        this.sex = sex;
        this.birthday = birthday;
        this.school = school;
        this.hometown = hometown;
        this.province = province;
        this.job = job;
        this.habit = habit;
        this.headImg = headImg;
    }

    public UserInfoGet(){

    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getHabit() {
        return habit;
    }

    public void setHabit(String habit) {
        this.habit = habit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getHometown() {
        return hometown;
    }

    public void setHometown(String hometown) {
        this.hometown = hometown;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }
}
