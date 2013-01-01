package com.android.xbmccontentcompare;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

import org.json.JSONObject;

public class FileRequest {

	public static void writeToSDFile(final JSONObject jsnoToBeWritten,
			final String fileName) {
		File root = android.os.Environment.getExternalStorageDirectory();

		File dir = new File(root.getAbsolutePath() + "/XBMCContentCompare");
		dir.mkdirs();
		File file = new File(dir, fileName);
		try {
			FileOutputStream f = new FileOutputStream(file);
			PrintWriter pw = new PrintWriter(f);
			pw.print(jsnoToBeWritten.toString());
			pw.flush();
			pw.close();
			f.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static JSONObject readJsonFromFile(String fileTobeRead) {
		JSONObject returnVal = null;
		try {
			File root = android.os.Environment.getExternalStorageDirectory();
			File dir = new File(root.getAbsolutePath() + "/XBMCContentCompare");

			File yourFile = new File(dir.getAbsolutePath() + "/home.json");
			FileInputStream stream = new FileInputStream(yourFile);
			String jString = null;
			try {
				FileChannel fc = stream.getChannel();
				MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0,
						fc.size());
				/* Instead of using default, pass in a decoder. */
				jString = Charset.defaultCharset().decode(bb).toString();
			} finally {
				stream.close();
			}

			returnVal = new JSONObject(jString);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnVal;
	}
	
}
