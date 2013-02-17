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

	private Button mUp = null;
	private Button mDown = null;
	private Button mMoveStart = null;
	private Button mMoveFinish = null;
	private Button mClear = null;

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

		// ログファイル作成
		File dir = new File(LOG_DIR);
		if (!dir.exists()) {
			boolean ret = dir.mkdir();
			 if (!ret) {
				 Log.e(TAG, "directory not created. : " + LOG_DIR);	
				 finish();
				 return;
			 }
		}
		
		mDatePicker = (DatePicker) findViewById(R.id.DatePicker);
		mTimePicker = (TimePicker) findViewById(R.id.TimePicker);
		mLogView = (TextView) findViewById(R.id.Text);
		mUp = (Button) findViewById(R.id.up);
		mDown = (Button) findViewById(R.id.down);
		mMoveStart = (Button) findViewById(R.id.move_start);
		mMoveFinish = (Button) findViewById(R.id.move_finish);
		mClear = (Button) findViewById(R.id.clear);

		Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);
		mHour = c.get(Calendar.HOUR_OF_DAY);
		mMin = c.get(Calendar.MINUTE);
		mDatePicker.init(mYear, mMonth, mDay, this);
		mTimePicker.setOnTimeChangedListener(this);

		String ok = getString(R.string.ok);
		String cancel = getString(R.string.cancel);
		String confirm = getString(R.string.confirm);
		String msg = getString(R.string.clear_msg);
		final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				this);
		alertDialogBuilder.setTitle(confirm);
		alertDialogBuilder.setMessage(msg);
		alertDialogBuilder.setPositiveButton(ok,
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						File log = new File(LOG_FILE);
						boolean b = log.delete();
						Log.d(TAG, "log  file delete : " + b);
						mLog = "";
						mLogView.setText("");
					}
				});
		alertDialogBuilder.setNegativeButton(cancel, null);
		mClearDialog = alertDialogBuilder.create();

		mUp.setOnClickListener(new ProccessButtonClickListener());
		mDown.setOnClickListener(new ProccessButtonClickListener());
		mMoveStart.setOnClickListener(new ProccessButtonClickListener());
		mMoveFinish.setOnClickListener(new ProccessButtonClickListener());
		mClear.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				mClearDialog.show();
			}
		});

		readLog();
	}
	
	private class ProccessButtonClickListener implements OnClickListener {
		public void onClick(View view) {
			Button btn = (Button)view;
			writeLog(btn.getText());
		}
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

	private void writeLog(CharSequence str) {
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