package com.mrbu.androidxmlparser.ui;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Color;
import java.awt.Font;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.CharBuffer;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;

import com.mrbu.androidxmlparser.AndroidXmlParser;
import com.mrbu.androidxmlparser.Const;
import com.mrbu.androidxmlparser.domain.Operation;

public class DragAndShowFrame extends JFrame {
	private static final String REMINDER = "此处可设置父控件名字";
	private static final String REMINDER_CLASSNAME = "此处可设置监听器类的名字";
	JPanel panel;
	JPanel panelWest;
	JPanel panelBottom;
	JPanel panelBottom2;
	CheckboxGroup cbg;
	TextArea ta;
	Button btn;
	JTabbedPane tab;
	Button btnForStyle;
	TextArea taWest;
	TextField tfClassname;
	Checkbox cbForLayout;
	Checkbox cbForListView;
	Checkbox cbForItemView;
	Checkbox cbForListener;
	JComboBox<String> jcb;
	public DragAndShowFrame() {
		tfClassname=new TextField(REMINDER_CLASSNAME ,48);
		ta=new TextArea("drag file here and then codes will be shown",21,75);
		ta.setEditable(false);
		Font font=new Font(null, Font. BOLD|Font.ITALIC, 15);
		ta.setFont(font);
		tab=new JTabbedPane(JTabbedPane.TOP);
		panel = new JPanel();
		panelBottom = new JPanel();
		panelBottom2 = new JPanel();
		panel.add(ta);
		panel.setBackground(Color.YELLOW);
		
    	cbg = new CheckboxGroup();
    	
    	cbForLayout=new Checkbox("for Layout View",cbg,true);
		cbForListView=new Checkbox("for ListView",cbg,false);
		cbForItemView=new Checkbox("for Item View",cbg,false);
		
		cbForListener=new Checkbox("Generate Listener");
		panelBottom.setLayout(new BorderLayout());
		
		jcb=new JComboBox<String>();
		jcb.setEditable(true);
		jcb.addItem(REMINDER);
		jcb.addItem("rootView");
		jcb.addItem("view");
		
		
		panelBottom.add(jcb,BorderLayout.NORTH);
		panelBottom.add(tfClassname,BorderLayout.CENTER);
		panelBottom2.add(cbForListener);
		panelBottom2.add(cbForListView);
		panelBottom2.add(cbForItemView);
		panelBottom2.add(cbForLayout);
		btn=new Button("get Comment");
		panelBottom2.add(btn);
		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				ta.setText(AndroidXmlParser.getComment());
			}
		});
		panelBottom.add(panelBottom2,BorderLayout.SOUTH);
		
		getContentPane().add(panelBottom, BorderLayout.SOUTH);
		setSize(700, 500);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocation(400, 200);
		setTitle("代码生成器");
		initWest();
		initTabs();
		drag();
		setAlwaysOnTop(true);
	}
	private void initTabs() {
		tab.add("代码生成",panel);
		tab.add("样式抽取",panelWest);
		getContentPane().add(tab, BorderLayout.CENTER);
	}
	private void initWest() {
		Font font=new Font(null, Font. BOLD|Font.ITALIC, 15);
		taWest=new TextArea("paste xml style here",18,35);
		taWest.setFont(font);
		panelWest = new JPanel();
		panelWest.setLayout(new BorderLayout());
		panelWest.add(taWest,BorderLayout.CENTER);
		btnForStyle=new Button("get Style");
		btnForStyle.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				ta.setText(AndroidXmlParser.parseExtractStyle(taWest.getText()));
			}
		});
		panelWest.add(btnForStyle,BorderLayout.SOUTH);
	}
	public static void main(String[] args) throws Exception
    {
        UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");//设置皮肤
        new DragAndShowFrame().setVisible(true);;
    }
	private void drag() {
		new DropTarget(panel, DnDConstants.ACTION_COPY_OR_MOVE,
				new DropTargetAdapter() {
					@Override
					public void drop(DropTargetDropEvent dtde) {
						if(dtde.isDataFlavorSupported(DataFlavor.plainTextFlavor)){
							dtde.acceptDrop(DnDConstants.ACTION_COPY);
							StringReader text = null;
							try {
								text = (StringReader) dtde
										.getTransferable()
										.getTransferData(DataFlavor.plainTextFlavor);
							} catch (UnsupportedFlavorException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							}
							dtde.dropComplete(true);
							StringBuffer sb=new StringBuffer();
							int len=0;
							char[] cbuf=new char[1024];
							
							try {
								while((len=text.read(cbuf))!=-1){
									sb.append(new String(cbuf,0,len));
									
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
							showCodeInFrame(sb.toString());
						}
						else if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
							dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
							try {
								List<File> files = (List<File>) dtde
										.getTransferable().getTransferData(
												DataFlavor.javaFileListFlavor);
								String parentView=tfClassname.getText().replace(REMINDER, "");
								Operation op = genOperation();
								String code = AndroidXmlParser.parseFile(files,op);
								showCodeInFrame(code);
								dtde.dropComplete(true);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}else{
							dtde.rejectDrop();
						}
					}
				});
	}

	protected Operation genOperation() {
		boolean generateListener = cbForListener.getState();
		int operationType=0;
		if(cbg.getSelectedCheckbox()==cbForItemView){
			operationType = Const.FOR_ITEM_VIEW;
		}
		if(cbg.getSelectedCheckbox()==cbForLayout){
			operationType = Const.FOR_LAYOUT_VIEW;
		}
		if(cbg.getSelectedCheckbox()==cbForListView){
			operationType = Const.FOR_LISTVIEW;
		}
		String classname = tfClassname.getText().replace(REMINDER_CLASSNAME, "");
		String parentView = jcb.getEditor().getItem().toString().replace(REMINDER, "");
		Operation op=new Operation(parentView, classname, operationType, generateListener);
		return op;
	}
	protected void showCodeInFrame(String code) {
		ta.setText(code);
	}
}
