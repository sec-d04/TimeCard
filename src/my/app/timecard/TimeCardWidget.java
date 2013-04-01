package my.app.timecard;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class TimeCardWidget extends AppWidgetProvider {
	
	private static final String TAG = "TimeCardWidget";

	@Override
	public void onUpdate(Context c, AppWidgetManager awm, int[] awi) {
		Log.d(TAG, "onUpdate");

		Intent intent = new Intent(c, TimeCardWidgetService.class);
        c.startService(intent);
	}

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		Log.d(TAG, "onDeleted");
		super.onDeleted(context, appWidgetIds);
	}



	@Override
	public void onDisabled(Context context) {
		Log.d(TAG, "onDisabled");
		super.onDisabled(context);
	}



	@Override
	public void onEnabled(Context context) {
		Log.d(TAG, "onEnabled");
		super.onEnabled(context);
	}

}

