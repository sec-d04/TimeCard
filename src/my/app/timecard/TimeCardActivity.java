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
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;
import android.widget.Toast;

public class TimeCardActivity extends Activity implements OnDateChangedListener,
		OnTimeChangedListener {

	static private final String TAG = "TimeCardActivity";

	static private final String LOG_DIR = "/mnt/sdcard/Android/data/my.app.timecard/";
	static private final String LOG_FILE = LOG_DIR + "log.dat";

	private static final int MENU_ID_ADDRESS = (Menu.FIRST + 1);

	private Button mUp = null;
	private Button mDown = null;
	private Button mMoveStart = null;
	private Button mMoveFinish = null;
	private Button mClear = null;
	private Button mMail = null;

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
		mMail = (Button) findViewById(R.id.mail);

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
		final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
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
		mMail.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				sendMail(loadAddress());
			}
		});

		readLog();
	}

	private class ProccessButtonClickListener implements OnClickListener {
		public void onClick(View view) {
			Button btn = (Button) view;
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// メニューアイテムを追加します
		menu.add(Menu.NONE, MENU_ID_ADDRESS, Menu.NONE, "set address");
		return super.onCreateOptionsMenu(menu);
	}

	// オプションメニューが表示される度に呼び出されます
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return super.onPrepareOptionsMenu(menu);
	}

	// オプションメニューアイテムが選択された時に呼び出されます
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean ret = true;
		switch (item.getItemId()) {
		default:
			ret = super.onOptionsItemSelected(item);
			break;
		case MENU_ID_ADDRESS:
			// テキスト入力を受け付けるビューを作成します。
			final EditText editView = new EditText(TimeCardActivity.this);
			editView.setText(loadAddress());
			new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_info)
					.setTitle("メールアドレスを入力してください")
					// setViewにてビューを設定します。
					.setView(editView)
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							// 入力した文字をトースト出力する
							Toast.makeText(TimeCardActivity.this, editView.getText().toString(),
									Toast.LENGTH_SHORT).show();
							String address = editView.getText().toString();
							saveAddress(address);
						}
					}).setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
						}
					}).show();
			
			ret = true;
			break;
		}
		return ret;
	}

	private void sendMail(String address) {
		readLog();
		String[] email = { address };
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_EMAIL, email);
		intent.putExtra(Intent.EXTRA_SUBJECT, "TimeCard");
		intent.putExtra(Intent.EXTRA_TEXT, mLog);
		startActivity(intent);
	}

	private void saveAddress(String address) {
		SharedPreferences sp = getSharedPreferences("address", MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString("address", address);
		editor.commit();
	}

	private String loadAddress() {
		SharedPreferences sp = getSharedPreferences("address", MODE_PRIVATE);
		String address = sp.getString("address", "");
		return address;
	}

}