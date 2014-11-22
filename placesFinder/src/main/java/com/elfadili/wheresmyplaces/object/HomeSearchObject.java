package com.elfadili.wheresmyplaces.object;


public class HomeSearchObject {
	
	public static final int STANDARD_SEARCH=1;
	public static final int CUSTOM_SEARCH=1;
	
	private int typeHomeObject=STANDARD_SEARCH;
	
	private int type;
	private String name;
	private String keyword;
	private String img;
	private boolean isSelected;
	private ResponsePlaceResult responcePlaceResult;
	private String realName="";
	
	public HomeSearchObject(int type, String name, String keyword, String img) {
		super();
		this.type = type;
		this.name = name;
		this.keyword = keyword;
		this.img = img;
	}
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
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
	public String getImg() {
		return img;
	}
	public void setImg(String img) {
		this.img = img;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public ResponsePlaceResult getResponcePlaceResult() {
		return responcePlaceResult;
	}

	public void setResponcePlaceResult(ResponsePlaceResult responcePlaceResult) {
		if(this.responcePlaceResult!=null){
			this.responcePlaceResult.onDestroy();
			this.responcePlaceResult=null;
		}
		this.responcePlaceResult = responcePlaceResult;
	}

	public int getTypeHomeObject() {
		return typeHomeObject;
	}

	public void setTypeHomeObject(int typeHomeObject) {
		this.typeHomeObject = typeHomeObject;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}
	
}
