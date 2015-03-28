package com.mrbu.androidxmlparser.log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.mrbu.androidxmlparser.domain.LogItem;
import com.mrbu.androidxmlparser.ui.DragAndShowFrame;

public class LogCatReader {
	private static BufferedWriter br;
	private boolean running = true;
	private DragAndShowFrame mShowFrame;
	private boolean mMatchMark = false;

	public LogCatReader(DragAndShowFrame dragAndShowFrame) {
		mShowFrame = dragAndShowFrame;
	}

	public void startLog(final String strFilter1, final String strFilter2,
			final String strFilter3) throws IOException {
		File f = new File("logAt"
				+ new SimpleDateFormat("yyyy-MM-dd&HH-mm-ss").format(new Date(
						System.currentTimeMillis())) + ".txt");
		if (!f.exists()) {
			f.createNewFile();
		}
		br = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f),
				"GBK"));
		new Thread(new Runnable() {
			private LogItem log;
			private boolean mMatchMyTag;
			private String strFilter4="System.err";

			@Override
			public void run() {
				Process logcatProcess = null;
				BufferedReader bufferedReader = null;
				try {
					logcatProcess = Runtime.getRuntime().exec(
							"adb logcat -v time");

					bufferedReader = new BufferedReader(new InputStreamReader(
							logcatProcess.getInputStream(), "UTF-8"));
					String line = null;

					// 筛选需要的字串
					while (((line = bufferedReader.readLine()) != null)
							&& running) {
						/** 检测到strFilter的log日志语句，进行你需要的处理 */
						mMatchMark = false;
						mMatchMyTag = false;
						if (line.indexOf(strFilter1) >= 0) {
							mMatchMark = true;
						}
						if (line.indexOf(strFilter2) >= 0) {
							mMatchMark = true;
						}
						if (line.indexOf(strFilter3) >= 0) {
							mMatchMyTag = true;
							mMatchMark = true;
						}
						/*if (strFilter4 != null && line.indexOf(strFilter4) >= 0) {
							mMatchMark = true;
						}*/
						if (mMatchMark) {
							log = new LogItem(line);
							printLog(log.toString());
						}
					}
					if (br != null) {
						br.close();
						logcatProcess.destroy();
					}
				} catch (Exception e) {
					try {
						if (br != null)
							br.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					e.printStackTrace();
				}
			}

		}).start();
	}

	private void printLog(String line) throws IOException {
		if (br == null) {
			return;
		}
		br.write(line);
		br.newLine();
		mShowFrame.printLog(line);
		br.flush();
	}

	public void stopPrinting() {
		running = false;
		try {
			if (br != null) {
				br.close();
				br = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
