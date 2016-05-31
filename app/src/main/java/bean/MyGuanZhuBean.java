package bean;

import android.graphics.Bitmap;

public class MyGuanZhuBean {

	private String userNum;
	private String username;
	private Bitmap headImg;
	private String beFollow;


	public String getUserNum() {

		return userNum;
	}



	public void setUserNum(String userNum) {

		this.userNum = userNum;
	}



	public String getUsername() {

		return username;
	}



	public void setUsername(String username) {

		this.username = username;
	}



	public Bitmap getHeadImg() {

		return headImg;
	}



	public void setHeadImg(Bitmap headImg) {

		this.headImg = headImg;
	}


	public String isBeFollow() {
		return beFollow;
	}

	public void setBeFollow(String beFollow) {
		this.beFollow = beFollow;
	}
}
