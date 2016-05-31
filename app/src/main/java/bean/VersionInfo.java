package bean;

/**
 * Created by guochunpeng on 15/9/23.
 */
public class VersionInfo {
    private String versionName;
    private int versionCode;
    private String newVersionPath;

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getNewVersionPath() {
        return newVersionPath;
    }

    public void setNewVersionPath(String newVersionPath) {
        this.newVersionPath = newVersionPath;
    }
}
