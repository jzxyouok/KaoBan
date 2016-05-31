package tempvalue;

/**
 * Created by zy on 15-8-20.
 */
public class HighExamItem {
    private String name;
    private String id;

    public HighExamItem(String name,String id){
        this.id = id;
        this.name = name;
    }

    public HighExamItem(){

    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }
}
