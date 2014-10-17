package org.shapiro.doron.impswitch;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import org.shapiro.doron.impswitch.broadcast_receivers.SwitchBroadcastReceiver;

/**
 * Created by doron on 10/2/14.
 */
public class SwitchAppWidgetProvider extends AppWidgetProvider {

    private static RemoteViews getRemoteViewsWithState(Context context, boolean toggled){
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_switch);

        int targetBgResourceId = toggled ? R.drawable.widget_bg_on : R.drawable.widget_bg_off;
        views.setImageViewResource(R.id.button_switch_widget, targetBgResourceId);

        views.setImageViewResource(R.id.icon_switch_widget, R.drawable.ic_outlet_bw);
        if(toggled) {
            views.setInt(R.id.icon_switch_widget, "setColorFilter", 0x20FFFFFF);
        } else {
            views.setInt(R.id.icon_switch_widget, "setColorFilter", 0x80323232);
        }

        Intent switchIntent = new Intent(context, SwitchBroadcastReceiver.class);
        switchIntent.setAction(SwitchBroadcastReceiver.ACTION_SWITCH);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, switchIntent, 0);

        views.setOnClickPendingIntent(R.id.button_switch_widget, pendingIntent);
        return views;
    }

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds){
            ImpModel impModel = ImpModel.getInstance();
            impModel.requestSync(context); // Note that the widget may initially be in an incorrect
                                           // state, but the sync will quickly fix it
            RemoteViews views = getRemoteViewsWithState(context, impModel.isOn());
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

}
