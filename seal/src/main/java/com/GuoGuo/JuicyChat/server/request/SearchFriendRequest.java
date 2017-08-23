package com.GuoGuo.JuicyChat.server.request;

/**
 * Created by cs on 2017/4/25.
 */

public class SearchFriendRequest {
	private String search;
	
	public SearchFriendRequest(String search) {
		this.search = search;
	}
	
	public String getSearch() {
		return search;
	}
	
	public void setSearch(String search) {
		this.search = search;
	}
}
