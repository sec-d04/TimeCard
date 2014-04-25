package my.app.timecard;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Button;

public class TimeCardCalendar {
	private static final String TAG = "[" + TimeCardCalendar.class.getSimpleName() + "] ";

	Activity mContext;
	int mCalendarId;
	String mCalendarName;
	Button mRead;
	Button mWrite;

	private static final String WHERE = CalendarContract.Events.TITLE + " LIKE '" + TAG + "%'";

	public TimeCardCalendar(Activity context) {
		mContext = context;
		getCalendarId();
		Log.d("Calendar Data", "mCalendarId=" + mCalendarId);

		// mRead = (Button) findViewById(R.id.read);
		// mWrite = (Button) findViewById(R.id.write);
		// mRead.setOnClickListener(new OnClickListener() {
		// public void onClick(View arg0) {
		// Log.d(TAG, "mRead onClick() [IN]");
		// setList(mCalendar.read());
		// Log.d(TAG, "mRead onClick() [OUT]");
		// }
		// });
		// mWrite.setOnClickListener(new OnClickListener() {
		// public void onClick(View arg0) {
		// Log.d(TAG, "mWrite onClick() [IN]");
		// Calendar date = Calendar.getInstance();
		// date.set(mYear, mMonth, mDay);
		// mCalendar.write(date, mTextView.getText().toString());
		// Log.d(TAG, "mWrite onClick() [OUT]");
		// }
		// });
	}

	public void write(Calendar date, String msg) {
		// イベント開始・終了時間を設定
		long startMillis = 0;
		long endMillis = 0;
		Calendar beginTime = (Calendar) date.clone();
		startMillis = beginTime.getTimeInMillis();
		Calendar endTime = (Calendar) date.clone();
		endTime.add(Calendar.HOUR, 1);
		endMillis = endTime.getTimeInMillis();

		// イベントデータを登録
		ContentResolver cr = mContext.getContentResolver();
		ContentValues values = new ContentValues();
		values.put(CalendarContract.Events.DTSTART, startMillis);
		values.put(CalendarContract.Events.DTEND, endMillis);
		values.put(CalendarContract.Events.EVENT_TIMEZONE,
				TimeZone.getDefault().getDisplayName(Locale.ENGLISH));
		values.put(CalendarContract.Events.TITLE, TAG + DateFormat.format(" yyyy/MM/dd", beginTime));
		values.put(CalendarContract.Events.DESCRIPTION, msg);
		values.put(CalendarContract.Events.CALENDAR_ID, mCalendarId);
		Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
	}

	public String read() {
		// ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext,
		// android.R.layout.simple_list_item_2);
		String adapter = "";

		ContentResolver cr = mContext.getContentResolver();
		// カラム
		String[] projection = new String[] { CalendarContract.Events.TITLE,
				CalendarContract.Events.DESCRIPTION, CalendarContract.Events.DTSTART, };

		// クエリ実行
		Cursor c = cr.query(CalendarContract.Events.CONTENT_URI, projection, WHERE, null, null);
		// mContext.managedQuery(calendars, projection, null, null, null);
		// 値を取得
		if (c.moveToFirst()) {
			String title;
			String description;
			int startTimeMills;
			int tColumn = c.getColumnIndex(CalendarContract.Events.TITLE);
			int dColumn = c.getColumnIndex(CalendarContract.Events.DESCRIPTION);
			int sColumn = c.getColumnIndex(CalendarContract.Events.DTSTART);

			do {
				title = c.getString(tColumn);
				description = c.getString(dColumn);
				startTimeMills = c.getInt(sColumn);
				Calendar beginTime = Calendar.getInstance();
				beginTime.setTimeInMillis(startTimeMills);
				Log.d("Calendar Data", "title=" + title + ",description=" + description);
				adapter += (title + ": " + description + ": "
						+ DateFormat.format(" hh:mm:ss", beginTime) + "\n");
			} while (c.moveToNext());
		}
		c.close();
		return adapter;
	}

	private void getCalendarId() {
		// カラム
		String[] projection = new String[] { "_id", "name" };
		// Uriの作成
		Uri calendars = CalendarContract.Calendars.CONTENT_URI;
		// クエリ実行
		Cursor c = mContext.managedQuery(calendars, projection, null, null, null);
		// 値を取得
		if (c.moveToFirst()) {
			int idColumn = c.getColumnIndex("_id");
			int nameColumn = c.getColumnIndex("name");
			do {
				mCalendarId = c.getInt(idColumn);
				mCalendarName = c.getString(nameColumn);
				Log.d("getCalendarId", "id=" + mCalendarId + ",name=" + mCalendarName);
			} while (c.moveToNext());
		}
	}

	/**
	 * Delete all Calendar data created by me. TODO Show delete item list and
	 * confirm it.
	 * 
	 * @param isForceClear
	 *            true: not show dialog.
	 * @return deleted item number
	 */
	public int clearAll(boolean isForceClear) {
		ContentResolver cr = mContext.getContentResolver();
		return cr.delete(CalendarContract.Events.CONTENT_URI, WHERE, null);
	}

}
