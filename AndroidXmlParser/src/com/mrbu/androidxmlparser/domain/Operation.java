package com.mrbu.androidxmlparser.domain;

import com.mrbu.androidxmlparser.Const;

public class Operation {
	private String parentView;
	private String classname;
	private int operationType;
	private int authType;
	private boolean generateListener;
	
	public Operation() {
	}
	
	/**
	 * @param parentView
	 * @param classname
	 * @param operationType
	 * @param authType
	 * @param generateListener
	 */
	public Operation(String parentView, String classname, int operationType,boolean generateListener) {
		super();
		this.parentView = parentView;
		this.classname = classname;
		this.operationType = operationType;
		switch (operationType) {
		case Const.FOR_ITEM_VIEW:
			this.authType = Const.defaultAuth;
			break;
		case Const.FOR_LISTVIEW:
			this.authType = Const.defaultAuth;
			break;
		case Const.FOR_LAYOUT_VIEW:
			this.authType = Const.privateAuth;
			break;
		default:
			break;
		}
		this.generateListener = generateListener;
	}

	public String getClassname() {
		return classname;
	}
	public void setClassname(String classname) {
		this.classname = classname;
	}
	public String getParentView() {
		return parentView;
	}
	public void setParentView(String parentView) {
		this.parentView = parentView;
	}
	public int getOperationType() {
		return operationType;
	}
	public void setOperationType(int operationType) {
		this.operationType = operationType;
	}
	public int getAuthType() {
		return authType;
	}
	public void setAuthType(int authType) {
		this.authType = authType;
	}
	public boolean isGenerateListener() {
		return generateListener;
	}
	public void setGenerateListener(boolean generateListener) {
		this.generateListener = generateListener;
	}
	
}
