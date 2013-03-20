package my.app.timecard;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.util.Log;
import android.widget.RemoteViews;

public class TimeCardWidget extends AppWidgetProvider {
	
	@Override
	public void onUpdate(Context c, AppWidgetManager awm, int[] awi) {
		Log.e("AAAA", "onUpdate");
		RemoteViews rv = new RemoteViews(c.getPackageName(), R.layout.widget);
			
		ComponentName cn = new ComponentName(c, TimeCardWidget.class);
		awm.updateAppWidget(cn, rv);
	}
}
