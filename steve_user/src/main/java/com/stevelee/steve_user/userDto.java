package com.stevelee.steve_user;

public class userDto {
	private String mail_id;
	private String pw;
	
	
	public String getMail_id() {
		return mail_id;
	}
	public void setMail_id(String mail_id) {
		this.mail_id = mail_id;
	}
	public String getPw() {
		return pw;
	}
	public void setPw(String pw) {
		this.pw = pw;
	}
	
	@Override
	public String toString() {
		return "userDto [mail_id=" + mail_id + ", pw=" + pw + "]";
	}
	
}
