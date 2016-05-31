package tempvalue;

/**
 * 考试专用类
 * Created by lsy on 15-2-6.
 */
public class Exam {
    private String name;
    private String rtime;
    private String etime;
    private String exam;
    private String examType;
    private String school;
    public Exam() {

    }

    public Exam(String exam) {
        this.exam=exam;
        this.name = exam;
    }
    public String getExam(){
        return exam;
    }

    public void setExam(String exam){
        this.exam=exam;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name=name;
    }

    public String getRtime(){
        return rtime;
    }

    public void setRtime(String rtime){
        this.rtime=rtime;
    }

    public String getEtime(){
        return etime;
    }

    public void setEtime(String etime){
        this.etime=etime;
    }

    public void setExamType(String examType) {
        this.examType = examType;
    }

    public String getExamType() {
        return examType;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getSchool() {
        return school;
    }
}
