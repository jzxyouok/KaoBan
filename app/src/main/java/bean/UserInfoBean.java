package bean;

import android.graphics.Bitmap;

import java.io.Serializable;

public class UserInfoBean implements Serializable{

	private String name;
	private String schooltime;
	private String sex;
	private String birthday;
	private String School;
	private String homeTown;
	private String province;
	private String habit;
	private String department;
	private String sign;
	private String number;
	private String job;
	private String headImg;
	private String photos;

	public String getPhotos(){
		return  photos;
	}

	public  void setPhotos(String photos){
		this.photos = photos;
	}
	public String getHeadImg() {
		return headImg;
	}



	public void setHeadImg(String headImg) {
		this.headImg = headImg;
	}



	public String getJob() {
		return job;
	}



	public void setJob(String job) {
		this.job = job;
	}



	public String getNumber() {
		return number;
	}



	public void setNumber(String number) {
		this.number = number;
	}



	public String getSign() {
		return sign;
	}



	public void setSign(String sign) {
		this.sign = sign;
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

		return School;
	}



	public void setSchool(String school) {

		School = school;
	}



	public String getHomeTown() {

		return homeTown;
	}



	public void setHomeTown(String homeTown) {

		this.homeTown = homeTown;
	}



	public String getProvince() {

		return province;
	}



	public void setProvince(String province) {

		this.province = province;
	}



	public String getHabit() {

		return habit;
	}



	public void setHabit(String habit) {

		this.habit = habit;
	}



	public String getSchooltime() {
		return schooltime;
	}



	public void setSchooltime(String schooltime) {
		this.schooltime = schooltime;
	}



	public String getDepartment() {
		return department;
	}



	public void setDepartment(String department) {
		this.department = department;
	}





}
