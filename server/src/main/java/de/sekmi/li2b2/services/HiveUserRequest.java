package de.sekmi.li2b2.services;

import org.w3c.dom.Document;

import de.sekmi.li2b2.hive.HiveRequest;

public class HiveUserRequest extends HiveRequest {
	private String userId;

	public HiveUserRequest(Document dom, String userId) {
		super(dom);
		this.userId = userId;
	}

	public String getUserId() {
		return userId;
	}

}
