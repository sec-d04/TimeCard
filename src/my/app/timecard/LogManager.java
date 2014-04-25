package my.app.timecard;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.util.Log;

public class LogManager {
	
	static private final String TAG = "LogManager";

	static private final String LOG_DIR = "/mnt/sdcard/Android/data/my.app.timecard/";
	static private final String LOG_FILE = LOG_DIR + "log.dat";

	static public boolean create() {
		File dir = new File(LOG_DIR);
		if (!dir.exists()) {
			boolean ret = dir.mkdir();
			 if (!ret) {
				 Log.e(TAG, "directory not created. : " + LOG_DIR);	
				 return false;
			 }
		}
		return true;
	}
	
	static public String load() {
		String log = "";
		FileInputStream input = null;
		InputStreamReader sReader = null;
		BufferedReader bReader = null;
		try {
			input = new FileInputStream(LOG_FILE);
			sReader = new InputStreamReader(input, "UTF-8");
			bReader = new BufferedReader(sReader);
			String str;
			log = "";
			while ((str = bReader.readLine()) != null) {
				log += str + "\n";
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bReader != null)
					bReader.close();
				if (sReader != null)
					sReader.close();
				if (input != null)
					input.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return log;
	}
	
	static public List<String> loadList() {
		String all_log = "";
		all_log = load();
		ArrayList<String> list = new ArrayList<String>();
		
		String[] log_list = all_log.split("\n");
		for ( int cnt = 0 ; cnt < log_list.length ; cnt++ ) {
			list.add(log_list[cnt]);
		}
		return list;
	}
	
	static public void write(String log) {

		FileOutputStream output = null;
		OutputStreamWriter writer = null;
		try {
			output = new FileOutputStream(LOG_FILE);
			writer = new OutputStreamWriter(output);
			writer.write(log);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (writer != null)
					writer.close();
				if (output != null)
					output.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	static public void write(List<String> list) {
		String all_log = "";
		Iterator<String> it = list.iterator();
		while(it.hasNext()) {
			all_log += it.next() + "\n";
		}
		write(all_log);
	}
	
	static public void delete() {
		File log = new File(LOG_FILE);
		boolean b = log.delete();
		Log.d(TAG, "log  file delete : " + b);
	}

	public static void delete(int position) {
		// TODO Auto-generated method stub
		List<String> list = loadList();
		list.remove(position);
		write(list);
	}
}
