package com.mrbu.androidxmlparser.parser;

import java.io.InputStream;
import java.util.Map.Entry;
import java.util.ArrayList;
import java.util.Set;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mrbu.androidxmlparser.domain.ClassOutput;
import com.mrbu.androidxmlparser.domain.FieldOutput;
import com.mrbu.androidxmlparser.utils.IOUtils;

/**
 * 需要修改为全部面向对象的编程
 * 
 * @author MrBu
 * 
 */
public class Json2BeanParser {
	public static final int STRING_ADAPT_TYPE = 1 << 1;
	public static final int BOOL_ADAPT_TYPE = 1 << 2;
	public static final int JOBJ_ADAPT_TYPE = 1 << 3;
	public static final int JARR_ADAPT_TYPE = 1 << 4;
	private static final String PATTERN_JSON_FILE_NAME = "patternJson.json";
	private static final String FIELD_NAME_REP = "#%fieldName%#";
	private static final String CLASS_NAME_REP = "#%className%#";
	private static final String FIELD_DECLARATIONS_REP = "#%fieldDeclarations%#";
	private static final String CLASS_KEY = "class";
	private static final String FIELD_STRING_KEY = "fieldString";
	private static final String FIELD_BOOl_KEY = "fieldBool";

	private static final String FIELD_STRING_LIST_KEY = "fieldStringList";
	private static final String FIELD_OBJ_LIST_KEY = "fieldObjList";
	private static final String FIELD_OBJ_KEY = "fieldObj";
	private static String classReplacer;
	private static String fieldStringReplacer;
	private static String fieldBoolReplacer;
	private static String fieldStringListReplacer;
	private static String fieldObjListReplacer;
	private static String fieldObjReplacer;

	public static String parseJson(String trim, String className) {
		initBase();
		JSONObject json = JSON.parseObject(trim);
		StringBuilder builder=new StringBuilder();
		ArrayList<ClassOutput> arrayList=new ArrayList<ClassOutput>();
		new ClassOutput(json, className, arrayList);
		ClassOutput co=null;
		for(int i=arrayList.size()-1;i>=0;i--){
			co=arrayList.get(i);
			co.createClassContent();
			builder.append(co.getClassContent());
		}
		return builder.toString();
	}


	public static int inferClass(Object value) {
		if (value instanceof String || value instanceof Integer
				|| value instanceof Long) {
			return STRING_ADAPT_TYPE;
		} else if (value instanceof Boolean) {
			return BOOL_ADAPT_TYPE;
		} else if (value instanceof JSONObject) {
			return JOBJ_ADAPT_TYPE;
		} else if (value instanceof JSONArray) {
			return JARR_ADAPT_TYPE;
		}
		return 0;
	}

	private static void initBase() {
		InputStream in = AndroidXmlParser.class.getClassLoader()
				.getResourceAsStream(PATTERN_JSON_FILE_NAME);
		String json = IOUtils.stream2String(in);
		JSONObject parseObject = JSON.parseObject(json);
		classReplacer = parseObject.getString(CLASS_KEY);
		fieldStringReplacer = parseObject.getString(FIELD_STRING_KEY);
		fieldObjReplacer = parseObject.getString(FIELD_OBJ_KEY);
		fieldBoolReplacer = parseObject.getString(FIELD_BOOl_KEY);
		fieldStringListReplacer = parseObject.getString(FIELD_STRING_LIST_KEY);
		fieldObjListReplacer = parseObject.getString(FIELD_OBJ_LIST_KEY);
	}

	public static String createClassContent(ClassOutput classOutput) {
		return classReplacer.replace(FIELD_DECLARATIONS_REP,
				classOutput.getFieldsContent()).replace(CLASS_NAME_REP,
				classOutput.getClassName());
	}

	public static String createFieldContent(FieldOutput fo) {
		switch (fo.getFieldClassInt()) {
		case STRING_ADAPT_TYPE:
			return fieldStringReplacer.replace(FIELD_NAME_REP,
					fo.getFieldName());
		case BOOL_ADAPT_TYPE:
			return fieldBoolReplacer.replace(FIELD_NAME_REP, fo.getFieldName());
		case JOBJ_ADAPT_TYPE:
			return fieldObjReplacer.replace(FIELD_NAME_REP, fo.getFieldName())
					.replace(CLASS_NAME_REP, fo.getFieldName());
		case JARR_ADAPT_TYPE:
			switch (fo.getClassIntSub()) {
			case STRING_ADAPT_TYPE:
				return fieldStringListReplacer.replace(FIELD_NAME_REP,
						 fo.getFieldName());
			case JOBJ_ADAPT_TYPE:
				return fieldObjListReplacer
						.replace(FIELD_NAME_REP,  fo.getFieldName()).replace(CLASS_NAME_REP,
								capitalLizeName(eraserEndS(fo.getFieldName())));
			}
			break;
		}
		return "";
	}
	public static String capitalLizeName(String name){
		String target="_";
		if(name.contains(target)){
			int pos=0;
			int nPos=0;
			int len=name.length();
			while(pos>=0&&pos<len-1){
				pos=name.indexOf(target, pos+1);
				nPos=pos+1;
				name = capitalizePos(name, nPos);
			}
			return capitalizePos(name.replaceAll(target, ""),0);
		}else{
			return capitalizePos(name, 0);
		}
	}


	private static String capitalizePos(String name, int nPos) {
		String charStr="";
		String newCharStr="";
		charStr=String.valueOf(name.charAt(nPos));
		newCharStr=charStr.toUpperCase();
		name=name.subSequence(0, nPos)+newCharStr+name.substring(nPos+1);
		return name;
	}
	public static String eraserEndS(String name) {
		if(name.endsWith("s")){
			return name.substring(0, name.length()-1);
		}
		return name;
	}

}
