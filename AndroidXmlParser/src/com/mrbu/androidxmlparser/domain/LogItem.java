package com.mrbu.androidxmlparser.domain;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogItem {

	public String date;
	public String time;
	public String tagClass;
	public String tag;
	public String pid;
	public String content;
	public int pidInt;

	public LogItem(String line) {
		// 03-19 13:13:40.147 I/Lk ( 3335): MsgListFragment:onAppear
		Pattern p = Pattern.compile("(\\(\\s*\\d{3,6}\\))");
		Matcher m = p.matcher(line);
		if (m.find()) {
			pid = m.group(1);
		}
		String[] contents = line.split(pid);
		pidInt = Integer.parseInt(pid.replace("(", "").replace(")", "").trim());
		content = contents[1].trim().substring(2).trim();
		String[] infos = contents[0].trim().split("\\s+");
		date = infos[0];
		time = infos[1];
		String[] logClassTag = infos[2].split("/");
		tagClass = logClassTag[0];
		tag = logClassTag[1];
	}

	@Override
	public String toString() {
		return date + "::" + time + "," + tagClass + "," + tag + ", pid:" + pid
				+ "," + content;
	}
}
