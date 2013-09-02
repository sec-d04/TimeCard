package my.app.timecard;

import java.util.Calendar;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

public class TimeCardWidgetService extends Service {

	private static final String TAG = "TimeCardWidgetService";
	private static final String ACTION_UP = "my.app.timecard.UP";
	private static final String ACTION_DOWN = "my.app.timecard.DOWN";
	private static final String ACTION_MOVE_START = "my.app.timecard.MOVE_START";
	private static final String ACTION_MOVE_FINISH = "my.app.timecard.MOVE_FINISH";
	
	private RemoteViews mRemoteViews;
	
	@Override
	public IBinder onBind(Intent arg0) {

		return null;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		if (intent == null) {
			Log.d(TAG, "onStart : intent = null");
		} else {
			Log.d(TAG, "onStart : action = " + intent.getAction());
		}
		super.onStart(intent, startId);
		
		if (intent == null) {
			return;
		}
		
		setupRemoteViews();
		
		String action = intent.getAction();
		String term = "";
		if (action == null) {
		} else if (action.equals(ACTION_UP)) {
			term = getString(R.string.up);
		} else if (action.equals(ACTION_DOWN)) {
			term = getString(R.string.down);
		} else if (action.equals(ACTION_MOVE_START)) {
			term = getString(R.string.move_start);
		} else if (action.equals(ACTION_MOVE_FINISH)) {
			term = getString(R.string.move_finish);
		}
		
		String log = LogManager.load();
		if (!term.equals("")) {
			Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);
			int hour = c.get(Calendar.HOUR_OF_DAY);
			int min = c.get(Calendar.MINUTE);
	
			String str = String.format("%04d/%02d/%02d %02d:%02d %s\n", year, month,
					day, hour, min, term);
			log = str + log;
			LogManager.write(log);
		}
		
		Context context = this.getApplicationContext();
		mRemoteViews.setTextViewText(R.id.text_log, log);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
		ComponentName cn = new ComponentName(context, TimeCardWidget.class);
		appWidgetManager.updateAppWidget(cn, mRemoteViews);
	}
	
	private void setupRemoteViews() {
		Context context = this.getApplicationContext();
		mRemoteViews = new RemoteViews(getPackageName(), R.layout.widget);

		Intent clickIntent = new Intent(context, TimeCardWidgetService.class);
        clickIntent.setAction(ACTION_UP);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, clickIntent, 0);
        mRemoteViews.setOnClickPendingIntent(R.id.up, pendingIntent);
        
        clickIntent = new Intent(context, TimeCardWidgetService.class);
        clickIntent.setAction(ACTION_DOWN);
        pendingIntent = PendingIntent.getService(context, 0, clickIntent, 0);
        mRemoteViews.setOnClickPendingIntent(R.id.down, pendingIntent);
        
        clickIntent = new Intent(context, TimeCardWidgetService.class);
        clickIntent.setAction(ACTION_MOVE_START);
        pendingIntent = PendingIntent.getService(context, 0, clickIntent, 0);
        mRemoteViews.setOnClickPendingIntent(R.id.move_start, pendingIntent);
        
        clickIntent = new Intent(context, TimeCardWidgetService.class);
        clickIntent.setAction(ACTION_MOVE_FINISH);
        pendingIntent = PendingIntent.getService(context, 0, clickIntent, 0);
        mRemoteViews.setOnClickPendingIntent(R.id.move_finish, pendingIntent);

	}
	
}
