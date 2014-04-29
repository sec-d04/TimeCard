package my.app.timecard;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * ログDatabaseアクセスHelper
 *
 */
public class LogDatabaseHelper extends SQLiteOpenHelper {

	private static final String TAG = "LogDatabaseHelper";
	private static final String DB_FILE_NAME = "Log.db";
	private static final String LOG_TABLE_NAME = "log";
	final static private int DB_VERSION = 1;
	
	private static final String COL_ID = "_id";
	private static final String COL_TIME = "time";
	private static final String COL_ACTION = "action";
	private static final String COL_MEMO_PATH = "memo_path";
	
	private static LogDatabaseHelper mInstance = null;
	static private Context mContext = null;
	static private SQLiteDatabase mDb = null;
	private List<Integer> mIdList = null;
	
	static public LogDatabaseHelper getDb(Context context) {
		
		if (mContext != context || mDb == null) {
			mContext = context;
			mInstance = new LogDatabaseHelper(context); 
			if (mDb != null) {
				mDb.close();
				mDb = null;
			}
			mDb = mInstance.getWritableDatabase();
		}

		return mInstance;
	}
	
	private LogDatabaseHelper(Context context) {
		super(context, DB_FILE_NAME, null, DB_VERSION);
	}


	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.i(TAG,"database table created");
		db.execSQL(
				"CREATE TABLE " + LOG_TABLE_NAME + " ("
					+ COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
	    	        + COL_TIME + " TEXT NOT NULL,"
	    	        + COL_ACTION + " TEXT NOT NULL,"
	    	        + COL_MEMO_PATH + " TEXT NOT NULL);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
	}
	
	public SQLiteDatabase open(Context context) {
		close();
		mDb = this.getWritableDatabase();
		return mDb;
	}
	
	public void close() {
		if (mDb != null) {
			mDb.close();
			mDb = null;
		}
	}

	/**
	 * ログのロード
	 *  - 全てのログを改行で区切って連結したStringを取得する。
	 * @return 全ログ
	 */
	public String load() {
		if (mDb == null) {
			Log.w(TAG, "load(): mDb = null");
			return null;
		}
		
		Cursor cursor = mDb.query(LOG_TABLE_NAME, null, null, null, null, null, null);
		int colId = cursor.getColumnIndex(COL_ID);
		int colTime = cursor.getColumnIndex(COL_TIME);
		int colAction = cursor.getColumnIndex(COL_ACTION);
		mIdList = new ArrayList<Integer>();
		
		String logAll = "";
		cursor.moveToFirst();
		for (int index = 0 ; index < cursor.getCount() ; index++) {
			logAll += cursor.getString(colTime) + " " + cursor.getString(colAction) + "\n";
			mIdList.add(Integer.valueOf(cursor.getInt(colId)));
			cursor.moveToNext();
		}
		
		return logAll;
	}
	
	public List<String> loadList() {
		if (mDb == null) {
			Log.w(TAG, "load(): mDb = null");
			return null;
		}
		
		Cursor cursor = mDb.query(LOG_TABLE_NAME, null, null, null, null, null, null);
		int colId = cursor.getColumnIndex(COL_ID);
		int colTime = cursor.getColumnIndex(COL_TIME);
		int colAction = cursor.getColumnIndex(COL_ACTION);
		mIdList = new ArrayList<Integer>();
		
		ArrayList<String> list = new ArrayList<String>();
		cursor.moveToFirst();
		for (int index = 0 ; index < cursor.getCount() ; index++) {
			String log = cursor.getString(colTime) + " " + cursor.getString(colAction);
			list.add(log);
			mIdList.add(Integer.valueOf(cursor.getInt(colId)));
			cursor.moveToNext();
		}
		
		return list;
	}
	
	public void write(String time, String action) {
		if (mDb == null) {
			Log.w(TAG, "load(): mDb = null");
			return;
		}
		ContentValues values = new ContentValues();
		values.put(COL_TIME, time);
		values.put(COL_ACTION, action);
		values.put(COL_MEMO_PATH, "");
		mDb.insert(LOG_TABLE_NAME, null, values);
	}
	
	public void delete() {
		if (mDb == null) {
			Log.w(TAG, "load(): mDb = null");
			return;
		}
		mDb.delete(LOG_TABLE_NAME, null, null);
	}
	
	public void delete(int position) {
		if (mDb == null) {
			Log.w(TAG, "load(): mDb = null");
			return;
		}
		
		Integer id = mIdList.remove(position);
		mDb.delete(LOG_TABLE_NAME, COL_ID + "=" + id, null);
	}

}
