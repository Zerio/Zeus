package com.elfadili.wheresmyplaces.object;

/**
 * 
 * KeywordObject.java
 * 
 * @author :DOBAO
 * @Email :dotrungbao@gmail.com
 * @Skype :baopfiev_k50
 * @Phone :+84983028786
 * @Date :Feb 24, 2014
 * @project :WhereMyLocation
 * @Package :com.ypyproductions.wheremylocation.object
 */
public class KeywordObject {
	private String name;
	private String keyword;

	public KeywordObject(String name, String keyword) {
		super();
		this.name = name;
		this.keyword = keyword;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

}
