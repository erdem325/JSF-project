package com.jforce.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.jforce.controller.KisiController;
@ManagedBean(name="userBilgi")
@ViewScoped
public class UserBilgi implements Serializable{
	private String name;
	private String surname;
	private Long identification;
	private String birthday;
	private String email;
	private String phonenumber;
	private String city;
	private String state;
	private UserBilgi kisiBilgi;
	
	

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	public String getSurname() {
		return surname;
	}
	public void setSurname(String surname) {
		this.surname = surname;
	}
	
	public Long getIdentification() {
		return identification;
	}
	public void setIdentification(Long identification) {
		this.identification = identification;
	}
	public String getBirthday() {
		return birthday;
	}
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhonenumber() {
		return phonenumber;
	}
	public void setPhonenumber(String phonenumber) {
		this.phonenumber = phonenumber;
	}
	
	public String getCity() {
		return city;
	}
	public void setState(String state) {
		this.state = state;
	}
	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}
	public UserBilgi getUserBilgi() {
		return kisiBilgi;
	}
	public void setUserBilgi(UserBilgi kisiBilgi) {
		this.kisiBilgi = kisiBilgi;
	}
	
	@Override
	public String toString() {
		
		return name + " " + surname +"  " +identification +"  " +birthday +"  " +email +" " +phonenumber +"  " +city +" " +state;
		
}
}
