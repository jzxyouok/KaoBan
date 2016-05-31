package tempvalue;

/**
 * Created by zy on 15-8-20.
 */
public class ItemContent {
    private String content;
    private String fileName;
    private String filePath;

    public ItemContent(){

    }

    public ItemContent(String content,String fileName,String filePath){
        this.content = content;
        this.fileName = fileName;
        this.filePath = filePath;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getContent() {
        return content;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFilePath() {
        return filePath;
    }
}
