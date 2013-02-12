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
import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

public class TimeCardActivity extends Activity implements
		OnDateChangedListener, OnTimeChangedListener {

	static private final String TAG = "TimeCardActivity";

	static private final String LOG_DIR = "/mnt/sdcard/Android/data/my.app.timecard/";
	static private final String LOG_FILE = LOG_DIR + "log.dat";

	Button mUp = null;
	Button mDown = null;
	Button mClear = null;

	DatePicker mDatePicker = null;
	TimePicker mTimePicker = null;
	TextView mLogView = null;

	AlertDialog mClearDialog = null;

	String mLog = new String();
	int mYear, mMonth, mDay, mHour, mMin;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// フォルダ作成
		File dir = new File(LOG_DIR);
		// String[] list = dir.list();
		// for (int i = 0 ; i < list.length ; i++) {
		// Log.e(TAG, list[i]);
		// }

		boolean ret = dir.mkdir();
		// if (!ret) {
		// Log.e(TAG, "directory not created. : " + LOG_DIR);
		// finish();
		// return;
		// }
		File log = new File("LOG_FILE");
		if (!log.exists()) {
			try {
				log.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		mDatePicker = (DatePicker) findViewById(R.id.DatePicker);
		mTimePicker = (TimePicker) findViewById(R.id.TimePicker);
		mLogView = (TextView) findViewById(R.id.Text);
		mUp = (Button) findViewById(R.id.up);
		mDown = (Button) findViewById(R.id.down);
		mClear = (Button) findViewById(R.id.clear);

		Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);
		mHour = c.get(Calendar.HOUR_OF_DAY);
		mMin = c.get(Calendar.MINUTE);
		mDatePicker.init(mYear, mMonth, mDay, this);
		mTimePicker.setOnTimeChangedListener(this);

		final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				this);
		alertDialogBuilder.setTitle("確認");
		alertDialogBuilder.setMessage("クリア？");
		alertDialogBuilder.setPositiveButton("OK",
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						File log = new File("LOG_FILE");
						log.delete();
					}
				});
		alertDialogBuilder.setNegativeButton("Cancel",
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
					}
				});

		// ダイアログを表示
		mClearDialog = alertDialogBuilder.create();

		mUp.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				writeLog("出社");
			}
		});

		mDown.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				writeLog("退社");
			}
		});

		mClear.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				mClearDialog.show();
			}
		});

		readLog();
	}

	public void onDateChanged(DatePicker view, int year, int month, int day) {
		mYear = year;
		mMonth = month;
		mDay = day;
	}

	public void onTimeChanged(TimePicker arg0, int hour, int min) {
		mHour = hour;
		mMin = min;
	}

	private void readLog() {
		FileInputStream input = null;
		InputStreamReader sReader = null;
		BufferedReader bReader = null;
		try {
			input = new FileInputStream(LOG_FILE);
			sReader = new InputStreamReader(input, "UTF-8");
			bReader = new BufferedReader(sReader);
			String str;
			mLog = "";
			while ((str = bReader.readLine()) != null) {
				mLog += str + "\n";
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
		mLogView.setText(mLog);
	}

	private void writeLog(String str) {
		str = String.format("%04d/%02d/%02d %02d:%02d %s\n", mYear, mMonth,
				mDay, mHour, mMin, str);
		String temp = str + mLog;
		mLog = temp;

		FileOutputStream output = null;
		OutputStreamWriter writer = null;
		try {
			output = new FileOutputStream(LOG_FILE);
			writer = new OutputStreamWriter(output);
			writer.write(mLog);

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
		mLogView.setText(mLog);

	}

}