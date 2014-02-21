package com.app.model;

public class WomanItemModel {

	private int img;//装女郎图片
	private String name; //装女郎名字
	private int vote = 0;//装女郎被投票数
	private int rank = 0; //排名
	
	public int getRank() {
		return rank;
	}
	public void setRank(int rank) {
		this.rank = rank;
	}
	public int getImg() {
		return img;
	}
	public void setImg(int img) {
		this.img = img;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getVote() {
		return vote;
	}
	public void setVote(int vote) {
		this.vote = vote;
	}
	
	
}
