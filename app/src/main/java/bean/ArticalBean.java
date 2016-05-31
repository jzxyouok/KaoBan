package bean;

import android.graphics.Bitmap;

import java.io.Serializable;

public class ArticalBean implements Serializable {

	private String imgHeader;
	private String userName;
	private String userNum;
	private String articalsNum;
	private String articalcontent;//帖子内容
	private String articalTime;//发帖时间
    private String postion;
	private String ifStore;
	private String img;//图片地址
	private String ifLike;
	private String storeNum;
	private String likeNum;
	private String commendNum;
	private Bitmap image;
	private String userSchool;
	private String userSex="man";

	public String getUserSex() {
		return userSex;
	}

	public void setUserSex(String userSex) {
		this.userSex = userSex;
	}

	private String smimg;

	public String getSmimg() {
		return smimg;
	}

	public void setSmimg(String smimg) {
		this.smimg = smimg;
	}

	public String getUserSchool() {
		return userSchool;
	}

	public void setUserSchool(String userSchool) {
		this.userSchool = userSchool;
	}

	public Bitmap getImage() {
		return image;
	}

	public void setImage(Bitmap image) {
		this.image = image;
	}

	public String getIfStore() {
		return ifStore;
	}

	public void setIfStore(String ifStore) {
		this.ifStore = ifStore;
	}

	public String getStoreNum() {
		return storeNum;
	}

	public void setStoreNum(String storeNum) {
		this.storeNum = storeNum;
	}

	public String getLikeNum() {
		return likeNum;
	}

	public void setLikeNum(String likeNum) {
		this.likeNum = likeNum;
	}

	public String getCommendNum() {
		return commendNum;
	}

	public void setCommendNum(String commendNum) {
		this.commendNum = commendNum;
	}

	public String getIfLike() {
		return ifLike;
	}

	public void setIfLike(String ifLike) {
		this.ifLike = ifLike;
	}

	public String getImgHeader() {
		return imgHeader;
	}

	public void setImgHeader(String imgHeader) {
		this.imgHeader = imgHeader;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserNum() {
		return userNum;
	}

	public void setUserNum(String userNum) {
		this.userNum = userNum;
	}

	public String getArticalsNum() {
		return articalsNum;
	}

	public void setArticalsNum(String articalsNum) {
		this.articalsNum = articalsNum;
	}

	public String getArticalcontent() {
		return articalcontent;
	}

	public void setArticalcontent(String articalcontent) {
		this.articalcontent = articalcontent;
	}

	public String getArticalTime() {
		return articalTime;
	}

	public void setArticalTime(String articalTime) {
		this.articalTime = articalTime;
	}



	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

    public String getPostion() {
        return postion;
    }

    public void setPostion(String postion) {
        this.postion = postion;
    }
}
