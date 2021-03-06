package com.app.model;

public class WomanItemModel {

	private String id; // 用户id
	private int defaultImg; // 默认头像
	private String img;// 头像
	private String name; // 名字
	private int vote = 0;// 投票
	private int rank = 0; // 排行

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getDefaultImg() {
		return defaultImg;
	}

	public void setDefaultImg(int defaultImg) {
		this.defaultImg = defaultImg;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
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
