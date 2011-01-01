package com.flattr4android.rest.demo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.flattr4android.rest.Thing;


public class SampleThing extends Thing {

	public SampleThing() {
		title = "Rotate PDF documents online";
		description = "This service enables you to rotate PDF documents, "
				+ "free of charge. All you need to do is select the PDF document "
				+ "that you want to rotate on your computer, select the rotation angle "
				+ "and click a button.";
		clicks = 26;
		categoryId = "rest";
		categoryName = "The rest";
		// 12/10/2010
		created = new Date(1281617100L);
		id = "7e4c65bfab8ee31e7d79f4d3b7bcfe19";
		language = "en_US";
		status = "ok";
		tags = new ArrayList<String>();
		tags.add("pdf");
		tags.add("rotate");
		tags.add("saas");
		tags.add("online");
		tags.add("free");
		url = "http://www.rotatepdf.net/";
		userId = 29173;
		userName = "pbernard";
	}

	public SampleThing(Thing model) {
		title = model.getTitle();
		description = model.getDescription();
		clicks = model.getClicks();
		categoryId = model.getCategoryId();
		categoryName = model.getCategoryName();
		// 12/10/2010
		created = model.getCreationDate();
		id = model.getId();
		language = model.getLanguage();
		status = model.getStatus();
		tags = new ArrayList<String>();
		for (String tag : model.getTags()) {
			tags.add(tag);
		}
		url = model.getURL();
		userId = model.getUserId();
		userName = model.getUserName();
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public void setUserId(int userId) {
		this.userId = userId;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	public void setCreationDate(Date created) {
		this.created = created;
	}
	
	public void setClicks(int clicks) {
		this.clicks = clicks;
	}
	
	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}
	
	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public void click() {
		// Do nothing, just update the thing accordingly
		updateAfterClick();
	}

}
