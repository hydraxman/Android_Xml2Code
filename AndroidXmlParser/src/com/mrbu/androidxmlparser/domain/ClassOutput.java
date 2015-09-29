package com.mrbu.androidxmlparser.domain;

import java.util.ArrayList;
import java.util.Set;
import java.util.Map.Entry;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mrbu.androidxmlparser.parser.Json2BeanParser;

public class ClassOutput {
	private String className;
	public String getClassName() {
		return className;
	}

	private String classContent;
	private String fieldsContent;
	private ArrayList<FieldOutput> fields=new ArrayList<FieldOutput>();

	public ClassOutput(JSONObject json, String className,
			ArrayList<ClassOutput> list) {
		this.className = Json2BeanParser.capitalLizeName(className);
		Set<Entry<String, Object>> entrySet = json.entrySet();
		for (Entry<String, Object> entry : entrySet) {
			String key = entry.getKey();
			Object value = entry.getValue();
			int classInt = Json2BeanParser.inferClass(value);
			FieldOutput fieldOutput = new FieldOutput(key, classInt);
			if (classInt == Json2BeanParser.JOBJ_ADAPT_TYPE) {
				addNewClass(list, key, value);
			} else if(classInt == Json2BeanParser.JARR_ADAPT_TYPE){
				JSONArray jsonArray = (JSONArray) value;
				if(jsonArray!=null&&jsonArray.size()>0){
					Object obj = jsonArray.get(0);
					int classIntSub = Json2BeanParser.inferClass(obj);
					fieldOutput.setClassIntSub(classIntSub);
					if(classIntSub == Json2BeanParser.JOBJ_ADAPT_TYPE){
						addNewClass(list, Json2BeanParser.eraserEndS(key), obj);
					}
				}else{
					fieldOutput.setClassIntSub(Json2BeanParser.JOBJ_ADAPT_TYPE);
				}
			}
			fields.add(fieldOutput);
		}
		list.add(this);
	}
	public void createClassContent() {
		StringBuilder sb=new StringBuilder();
		for(FieldOutput fo:fields){
			sb.append(fo.toContentString());
		}
		fieldsContent=sb.toString();
		classContent=Json2BeanParser.createClassContent(this);
	}
	private void addNewClass(ArrayList<ClassOutput> list, String key,
			Object value) {
		JSONObject jsonSub = null;
		jsonSub = (JSONObject) value;
		new ClassOutput(jsonSub, key, list);
	}

	public String getClassContent() {
		return classContent;
	}
	public String getFieldsContent() {
		return fieldsContent;
	}
}
