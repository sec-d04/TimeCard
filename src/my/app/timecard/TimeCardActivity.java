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
		boolean ret = LogManager.create();
		if (!ret) {
			finish();
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
						LogManager.delete();
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

		mLog = LogManager.load();
		mLogView.setText(mLog);
	}
	
	private class ProccessButtonClickListener implements OnClickListener {
		public void onClick(View view) {
			Button btn = (Button)view;
			
			String str = String.format("%04d/%02d/%02d %02d:%02d %s\n", mYear, mMonth,
					mDay, mHour, mMin, btn.getText());
			mLog = str + mLog;
			mLogView.setText(mLog);
			LogManager.write(mLog);
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



}