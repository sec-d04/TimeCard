package my.app.timecard;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class TimeCardActivity extends Activity {

	static private final String TAG = "TimeCardActivity";

	private static final int MENU_ID_ADDRESS = (Menu.FIRST + 1);

	private Button mDateChange = null;
	private Button mTimeChange = null;
	private Button mUp = null;
	private Button mDown = null;
	private Button mMoveStart = null;
	private Button mMoveFinish = null;
	private Button mClear = null;
	private Button mMail = null;

	DatePickerDialog mDatePickerDialog = null;
	TimePickerDialog mTimePickerDialog = null;

	TextView mTextDate = null;
	TextView mTextTime = null;
	TextView mLogView = null;
	

	AlertDialog mClearDialog = null;

	String mLog = new String();
	private Calendar mCurrentTime;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// ログファイル作成
		boolean ret = LogManager.create();
		if (!ret) {
			finish();
		}

		mLogView = (TextView) findViewById(R.id.text_log);
		mTextDate = (TextView) findViewById(R.id.text_date);
		mTextTime = (TextView) findViewById(R.id.text_time);
		mDateChange = (Button) findViewById(R.id.change_date);
		mTimeChange = (Button) findViewById(R.id.change_time);
		mUp = (Button) findViewById(R.id.up);
		mDown = (Button) findViewById(R.id.down);
		mMoveStart = (Button) findViewById(R.id.move_start);
		mMoveFinish = (Button) findViewById(R.id.move_finish);
		mClear = (Button) findViewById(R.id.clear);
		mMail = (Button) findViewById(R.id.mail);

		mDateChange.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mDatePickerDialog.show();
			}
		});
		mTimeChange.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mTimePickerDialog.show();
			}
		});
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

		mCurrentTime = Calendar.getInstance();
		setCurrentTime();
		createDialog();

		mLog = LogManager.load();
		mLogView.setText(mLog);
	}

	private void setCurrentTime() {
		CharSequence time = DateFormat.format("yyyy/MM/dd", mCurrentTime);
		mTextDate.setText(time);
		Log.d(TAG, "setCurrentTime : Date = " + time);
		time = DateFormat.format("kk:mm:ss", mCurrentTime);
		mTextTime.setText(time);
		Log.d(TAG, "setCurrentTime : time = " + time);
	}
	
	private void createDialog() {
 		OnDateSetListener dateListener = new OnDateSetListener(){
			
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				Log.d(TAG,"onDateSet : year = " + year + ", month = " + monthOfYear + ", day = " + dayOfMonth);
				mCurrentTime.set(year, monthOfYear, dayOfMonth);
				mCurrentTime.set(Calendar.YEAR, year);
				mCurrentTime.set(Calendar.MONTH, monthOfYear);
				mCurrentTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				setCurrentTime();
		}};

		OnTimeSetListener timeListener = new OnTimeSetListener() {

			@Override
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
				Log.d(TAG,"onDateSet : hour = " + hourOfDay + ", minute = " + minute);
				mCurrentTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
				mCurrentTime.set(Calendar.MINUTE, minute);
				setCurrentTime();
			}
		};
		
		mCurrentTime = Calendar.getInstance();
		mDatePickerDialog = new DatePickerDialog(this, dateListener,
				mCurrentTime.get(Calendar.YEAR),
				mCurrentTime.get(Calendar.MONTH),
				mCurrentTime.get(Calendar.DAY_OF_MONTH));
		mTimePickerDialog = new TimePickerDialog(this, timeListener,
				mCurrentTime.get(Calendar.HOUR_OF_DAY),
				mCurrentTime.get(Calendar.MINUTE), true);
		mDatePickerDialog.setTitle(R.string.change_date);
		mTimePickerDialog.setTitle(R.string.change_time);
		
		String ok = getString(R.string.ok);
		String cancel = getString(R.string.cancel);

		// クリア確認ダイアログ
		String confirm = getString(R.string.confirm);
		String msg = getString(R.string.clear_msg);
		final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
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

	}

	private class ProccessButtonClickListener implements OnClickListener {
		public void onClick(View view) {
			Button btn = (Button)view;
			
			String str = String.format("%s %s %s\n",
					mTextDate.getText(),
					mTextTime.getText(),
					btn.getText());
			mLog = str + mLog;
			mLogView.setText(mLog);
			LogManager.write(mLog);
		}
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
					.setTitle(getString(R.string.set_email_msg))
					// setViewにてビューを設定します。
					.setView(editView)
					.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							// 入力した文字をトースト出力する
							Toast.makeText(TimeCardActivity.this, editView.getText().toString(),
									Toast.LENGTH_SHORT).show();
							String address = editView.getText().toString();
							saveAddress(address);
						}
					}).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
						}
					}).show();
			
			ret = true;
			break;
		}
		return ret;
	}

	private void sendMail(String address) {
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