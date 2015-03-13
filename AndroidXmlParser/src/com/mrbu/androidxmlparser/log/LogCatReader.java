package com.mrbu.androidxmlparser.log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import com.mrbu.androidxmlparser.ui.DragAndShowFrame;

public class LogCatReader {
	private static BufferedWriter br;
	private boolean running = true;
	private DragAndShowFrame mShowFrame;

	public LogCatReader(DragAndShowFrame dragAndShowFrame) {
		mShowFrame = dragAndShowFrame;
	}

	public void startLog() throws IOException {
		File f = new File("log.txt");
		if (!f.exists()) {
			f.createNewFile();
		}
		br = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f),
				"GBK"));
		new Thread(new Runnable() {
			@Override
			public void run() {
				Process logcatProcess = null;
				BufferedReader bufferedReader = null;
				try {
					logcatProcess = Runtime.getRuntime().exec("adb logcat");

					bufferedReader = new BufferedReader(new InputStreamReader(
							logcatProcess.getInputStream(), "UTF-8"));
					String line;
					// 筛选需要的字串
					String strFilter1 = "Lk";
					String strFilter2 = "androidruntime";
					String strFilter3 = "AndroidRuntime";

					while (((line = bufferedReader.readLine()) != null)
							&& running) {
						/** 检测到strFilter的log日志语句，进行你需要的处理 */
						if (line.indexOf(strFilter1) >= 0) {
							printLog(line);
						}
						if (line.indexOf(strFilter2) >= 0) {
							printLog(line);
						}
						if (line.indexOf(strFilter3) >= 0) {
							printLog(line);
						}
					}
					br.close();
				} catch (Exception e) {
					try {
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
