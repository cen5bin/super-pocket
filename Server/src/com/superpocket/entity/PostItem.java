package com.superpocket.entity;

/**
 * 文章条目，在文章列表里显示文章简介时需要用到
 * @author wubincen
 *
 */
public class PostItem {
	private String title = "";
	private int pid = 0;
	private String tags = "";
	private String plainText ="";
	private String time = "";
	public PostItem(String title, int pid, String tags, String plainText,
			String time) {
		super();
		this.title = title;
		this.pid = pid;
		this.tags = tags;
		this.plainText = plainText;
		this.time = time;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getPid() {
		return pid;
	}
	public void setPid(int pid) {
		this.pid = pid;
	}
	public String getTags() {
		return tags;
	}
	public void setTags(String tags) {
		this.tags = tags;
	}
	public String getPlainText() {
		return plainText;
	}
	public void setPlainText(String plainText) {
		this.plainText = plainText;
	}
	
}
