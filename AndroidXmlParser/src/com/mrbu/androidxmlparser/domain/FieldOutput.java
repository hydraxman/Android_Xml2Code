package com.mrbu.androidxmlparser.domain;

import com.mrbu.androidxmlparser.parser.Json2BeanParser;

public class FieldOutput {
	
	private String fieldName;
	public String getFieldName() {
		return fieldName;
	}
	public int getFieldClassInt() {
		return fieldClassInt;
	}
	private int fieldClassInt;
	public FieldOutput(String fieldName, int fieldClassInt) {
		super();
		this.fieldName = fieldName;
		this.fieldClassInt=fieldClassInt;
	}
	private String fieldContent;
	private int classIntSub;
	public String toContentString() {
		fieldContent=Json2BeanParser.createFieldContent(this);
		return fieldContent;
	}
	public void setClassIntSub(int classIntSub) {
		this.classIntSub=classIntSub;
	}
	public int getClassIntSub() {
		return classIntSub;
	}
}
