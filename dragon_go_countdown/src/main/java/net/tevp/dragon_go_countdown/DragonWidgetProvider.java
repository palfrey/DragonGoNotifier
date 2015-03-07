package net.tevp.dragon_go_countdown;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;

public class DragonWidgetProvider extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        // initializing widget layout
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
        views.setInt(R.id.widgetBackground, "setBackgroundResource", R.drawable.widget_back_green);
        appWidgetManager.updateAppWidget(appWidgetIds[0], views);
    }
}
