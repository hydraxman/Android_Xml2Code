package com.mrbu.androidxmlparser.parser;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.dom4j.tree.DefaultAttribute;

import com.mrbu.androidxmlparser.Const;
import com.mrbu.androidxmlparser.domain.Operation;
import com.mrbu.androidxmlparser.domain.ViewItem;

public class AndroidXmlParser {

	private static final String XML_FORACTIVITY_TAG = "forActivity";
	private static final String casesPat = "#%cases%#";
	private static final String inflateViewPat = "#%inflateView%#";
	private static final String holderFindViewByIdPat = "#%holderFindViewById%#";
	private static final String localDeclarationPat = "#%localDeclaration%#";
	private static final String idNamePat = "#%idName%#";
	private static final String fileNamePat = "#%fileName%#";
	private static final String viewDecPat = "#%showViewDec%#";
	private static final String parentPat = "#%parent%#";
	private static SAXReader reader = null;
	public static String parseExtractStyle(String xmlText){
		String notExtractedStyle="text";
		xmlText="<Root xmlns:android=\"http://schemas.android.com/apk/res/android\">"+xmlText+"</Root>";
		reader = new SAXReader();
		Document views = null;
		try {
			views = reader.read(new ByteArrayInputStream(xmlText.getBytes("UTF-8")));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			return e.getMessage();
		}
		List<Element> elements = views.getRootElement().selectNodes("//*");
		Document extractedStyle = DocumentFactory.getInstance().createDocument("UTF-8");
		Element root=extractedStyle.addElement("resources");
		Element style=null;
		List<DefaultAttribute> attrs;
		for(Element ele:elements){
			if(ele.getName().equals("Root")){
				continue;
			}
			style=root.addElement("style");
			boolean hasStyleName=false;
			attrs = ele.attributes();
			for(DefaultAttribute attr:attrs){
				//获取包括命名空间在内的全属性名
				String attrName = attr.getQualifiedName();
				String attrValue = attr.getStringValue();
				if(attrName.equals("android:id")){
					hasStyleName = true;
					style.addAttribute("name", attrValue.replace("@+id/", "")+"_style");
					continue;
				}
				style.addElement("item").addAttribute("name", attrName).setText(attrValue);
			}
			if(!hasStyleName){
				style.addAttribute("name", "undefined");
			}
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		XMLWriter writer=null;
		try {
			writer=new XMLWriter(baos, OutputFormat.createPrettyPrint());
			writer.write(extractedStyle);
			return new String(baos.toByteArray(),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return extractedStyle.asXML();
	}
	/*<style name="line_break">
	    <item name="android:background">#ff000000</item>
	    <item name="android:layout_width">fill_parent</item>
	    <item name="android:layout_height">0.20000005dip</item>
	    <item name="android:layout_marginLeft">5.0dip</item>
	    <item name="android:layout_marginRight">5.0dip</item>
    </style>*/
	/**
	 * 
	 * @param files
	 * @param op
	 * @return
	 */
	public static String parseFile(List<File> files, Operation op) {
		String code = null;
		try {
			reader = new SAXReader();
			FileInputStream in = new FileInputStream(files.get(0));
			String filename = files.get(0).getName().replace(".xml", "");
			Document document = reader.read(in);
			Element root = document.getRootElement();
			List<Element> elements = root.selectNodes("//*[@android:id]");
			ViewItem vi = null;
			StringBuffer sbAll = null;
			List<ViewItem> list = new ArrayList<ViewItem>();
			for (Element e : elements) {
				String idAttr = e.valueOf("@android:id").toString();
				vi = new ViewItem(
						idAttr.substring(idAttr.indexOf("@+id/") + 5),
						e.getName(), op.isGenerateListener());
				list.add(vi);
			}
			in.close();
			reader = null;
			switch (op.getOperationType()) {
			case Const.FOR_LAYOUT_VIEW:
				sbAll = generateLayoutCode(list, op);
				break;
			case Const.FOR_ITEM_VIEW:
				sbAll = generateItemCode(list, op, filename);
				break;
			case Const.FOR_LISTVIEW:
				sbAll = generateListViewCode(list, op, filename);
				break;
			}
			return sbAll.toString();
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return code;
	}
	/**
	 * 
	 * @param list
	 * @param op
	 * @param filename
	 * @return
	 * @throws DocumentException
	 */
	private static StringBuffer generateListViewCode(List<ViewItem> list,
			Operation op, String filename) throws DocumentException {
		// 获取模板文本
		Element root = getPatternRootElement();
		String listPat = root.element("forlist").getText();
		StringBuffer sbLocalDeclaration = new StringBuffer();
		StringBuffer sbHolderFindView = new StringBuffer();
		StringBuffer sbListener = new StringBuffer();
		StringBuffer sbAll = new StringBuffer();
		String switchPat = null;
		for (ViewItem vi : list) {
			sbLocalDeclaration.append(vi.toDeclaration(op.getAuthType()));
			sbHolderFindView.append(vi.toHolderFindView(op.getParentView()));
		}
		String inflateStr = root.element("inflate").getText()
				.replace(fileNamePat, filename).replace(parentPat, op.getParentView()).replace(viewDecPat, "");
		listPat = listPat.replace(localDeclarationPat, sbLocalDeclaration.toString())
				.replace(holderFindViewByIdPat, sbHolderFindView.toString())
				.replace(inflateViewPat, inflateStr);
		if(op.isGenerateListener()){
			switchPat=generateSwitch(root ,list,op);
		}else{
			switchPat="";
		}
		sbAll.append(listPat).append(switchPat).append(sbListener);
		return sbAll;
	}
	/**
	 * 
	 * @param root
	 * @param list
	 * @param op
	 * @return
	 */
	private static String generateSwitch(Element root ,List<ViewItem> list,
			Operation op){
		String switchPat = root.element("switch").getText();
		StringBuffer sbCases = new StringBuffer();
		StringBuffer sbSetListener = new StringBuffer();
		for(ViewItem vi:list){
			sbCases.append(root.element("case").getText()
					.replaceAll(idNamePat, vi.getIdName()));
			sbSetListener.append(vi.toSetOnClick(op.getClassname()));
		}
		switchPat=switchPat.replaceAll(casesPat, sbCases.toString());
		return switchPat+sbSetListener.toString();
	}
	/**
	 * 
	 * @param list
	 * @param op
	 * @param filename
	 * @return
	 * @throws DocumentException
	 */
	private static StringBuffer generateItemCode(List<ViewItem> list,
			Operation op, String filename) throws DocumentException {
		Element root = getPatternRootElement();
		String inflateStr =root.element("inflate").getText()
				.replaceAll(fileNamePat, filename).replace(viewDecPat, "").replace(parentPat, op.getParentView());
		StringBuffer sbDeclaration = new StringBuffer();
		StringBuffer sbFindView = new StringBuffer();
		StringBuffer sbSetOnClick = new StringBuffer();
		StringBuffer sbAll = new StringBuffer();
		
		for (ViewItem vi : list) {
			sbDeclaration.append(vi.toDeclaration(op.getAuthType()));
			sbFindView.append(vi.toFindView(op.getParentView()));
			if (op.isGenerateListener()) {
				sbSetOnClick.append(vi.toSetOnClick(op.getClassname()));
			}
		}
		sbAll.append(inflateStr).append(sbDeclaration).append(sbFindView).append(sbSetOnClick);
		return sbAll;
	}
	/**
	 * 
	 * @param list
	 * @param op
	 * @return
	 * @throws DocumentException
	 */
	private static StringBuffer generateLayoutCode(List<ViewItem> list,
			Operation op) throws DocumentException {
		StringBuffer sbDeclaration = new StringBuffer();
		StringBuffer sbFindView = new StringBuffer();
		StringBuffer sbAll = new StringBuffer();
		for (ViewItem vi : list) {
			sbDeclaration.append(vi.toDeclaration(op.getAuthType()));
			sbFindView.append(vi.toFindView(op.getParentView()));
		}
		String switchPat=null;
		Element root = getPatternRootElement();
		if(op.isGenerateListener()){
			switchPat=generateSwitch(root ,list,op);
		}else{
			switchPat="";
		}
		sbAll.append(sbDeclaration).append(sbFindView).append(switchPat);
		return sbAll;
	}
	public static Element getPatternRootElement() throws DocumentException{
		reader = new SAXReader();
		InputStream in = AndroidXmlParser.class.getClassLoader()
				.getResourceAsStream("pattern.xml");
		
		Element root = reader.read(in).getRootElement();
		return root;
	}
	public static String getComment() {
		reader = new SAXReader();
		String comment=null;
		try {
			InputStream in = AndroidXmlParser.class.getClassLoader()
					.getResourceAsStream("comment.xml");
			Element root = reader.read(in).getRootElement();
			comment = root.element(XML_FORACTIVITY_TAG).getText();
			in.close();
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return comment;
	}
}
