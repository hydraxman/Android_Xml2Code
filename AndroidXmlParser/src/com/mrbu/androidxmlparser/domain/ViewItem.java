package com.mrbu.androidxmlparser.domain;

import com.mrbu.androidxmlparser.Const;

public class ViewItem {
	private String idName;
	private String type;
	private boolean hasListener;
	
	public static final String findViewById="findViewById(";
	public static final String privateStr="private ";
	public static final String publicStr="public ";
	public static final String protectedStr="protected ";
	public static final String defaultStr="";
	public static final String sp=" ";
	public static final String eq="=";
	public static final String le="(";
	public static final String re=")";
	public static final String rn="\r\n";
	public static final String holder="holder.";
	public static final String setOnClickListener=".setOnClickListener(";
	public static final String thisStr="this)";
	public static final String rid="R.id.";
	public ViewItem() {
	}
	public ViewItem(String idName, String type, boolean hasListener) {
		super();
		this.idName = idName;
		if(type.contains(".")){
			type=type.substring(type.lastIndexOf(".")+1);
		}
		this.type = type;
		this.hasListener = hasListener;
	}
	public String getIdName() {
		return idName;
	}
	public void setIdName(String idName) {
		this.idName = idName;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		if(type.contains(".")){
			type=type.substring(type.lastIndexOf(".")+1);
		}
		this.type = type;
	}
	public boolean isHasListener() {
		return hasListener;
	}
	public void setHasListener(boolean hasListener) {
		this.hasListener = hasListener;
	}
	
	@Override
	public String toString() {
		return "ViewItem [idName=" + idName + ", type=" + type
				+ ", hasListener=" + hasListener + "]";
	}
	public String toDeclaration(int authType) {
		StringBuilder sb=new StringBuilder();
		switch (authType) {
		case Const.defaultAuth:
			sb.append(defaultStr);
			break;
		case Const.privateAuth:
			sb.append(privateStr);
			break;
		case Const.publicAuth:
			sb.append(publicStr);
			break;
		case Const.protectedAuth:
			sb.append(protectedStr);
			break;
		}
		sb.append(type).append(sp).append(idName).append(";").append(rn);
		return sb.toString();
	}
	public String toFindView(String parentView) {
		StringBuilder sb=new StringBuilder();
		sb.append(idName).append(eq).append(le).append(type).append(re);
		if(parentView!=null&&!parentView.trim().equals("")){
			sb.append(parentView).append(".");
		}
		sb.append(findViewById).append(rid).append(idName).append(re).append(";").append("\r\n");
		return sb.toString();
	}
	public String toSetOnClick(String className) {
		StringBuilder sb=new StringBuilder();
		sb.append(idName).append(setOnClickListener).append(className);
		if(className!=null&&!className.trim().equals("")){
			sb.append(className).append(".");
		}
		sb.append(thisStr).append(";").append("\r\n");
		return sb.toString();
	}
	public String toHolderFindView(String parentView) {
		StringBuilder sb=new StringBuilder();
		sb.append(holder).append(idName).append(eq).append(le).append(type).append(re);
		if(parentView!=null&&!parentView.trim().equals("")){
			sb.append(parentView).append(".");
		}
		sb.append(findViewById).append(rid).append(idName).append(re).append(";").append("\r\n");
		return sb.toString();
	}
	
}
