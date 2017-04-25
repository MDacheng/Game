package com.beans;
public class User{
	private String userName;
	private String address;
	private String dataAddress;

	private int rid = -1;
	private int bid = -1;

	public User(String userName, String dataAddress){
		this.userName = userName;
		this.dataAddress = dataAddress;
	}
	public User(String userName, String address, String dataAddress, int rid, int bid){
		this.userName = userName;
		this.address = address;
		this.dataAddress = dataAddress;
		this.rid = rid;
		this.bid = bid;
	}
	public void setUserName(String userName){
		this.userName = userName;
	}
	public String getUserName(){
		return this.userName;
	}
	public void setRid(int rid){
		this.rid = rid;
	}
	public int getRid(){
		return this.rid;
	}
	public void setBid(int bid){
		this.bid = bid;
	}
	public int getBid(){
		return this.bid;
	}
	public String getAddress(){
		return this.address;
	}
	public String setDataAddress(){
		return this.dataAddress;
	}
	public String getDataAddress(){
		return this.dataAddress;
	}
}