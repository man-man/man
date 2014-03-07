package com.app.model;

import java.util.Date;
import java.util.List;

/**
 * 男人帮item model
 * 
 * @author XH
 * 
 */
public class ManItemModel {

	// 文章id
	private int articleId;

	// 作者id
	private int authorId;

	// 作者名
	private String authorName;

	// 作者头像
	private String authorImg;

	// 正文内容
	private String summary;

	// 发表日期
	private String date;

	// 图片集合
	private List<ManItemImgsModel> images;

	public int getArticleId() {
		return articleId;
	}

	public void setArticleId(int articleId) {
		this.articleId = articleId;
	}

	public int getAuthorId() {
		return authorId;
	}

	public void setAuthorId(int authorId) {
		this.authorId = authorId;
	}

	public String getAuthorName() {
		return authorName;
	}

	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}

	public String getAuthorImg() {
		return authorImg;
	}

	public void setAuthorImg(String authorImg) {
		this.authorImg = authorImg;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public List<ManItemImgsModel> getImages() {
		return images;
	}

	public void setImages(List<ManItemImgsModel> images) {
		this.images = images;
	}
	
}
