package org.shapiro.doron.impswitch.broadcast_receivers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.shapiro.doron.impswitch.BigSwitchActivity;
import org.shapiro.doron.impswitch.PlugTopModel;
import org.shapiro.doron.impswitch.R;
import org.shapiro.doron.impswitch.SwitchAppWidgetProvider;
import org.shapiro.doron.impswitch.enums.DeviceStatType;

import java.util.HashMap;

/**
 * Created by doron on 10/5/14.
 */
public class GcmBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "BR/GBR";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);

        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // To post notification of received message:
                //sendNotification(context, "Received: " + extras.toString());
                Log.i(TAG, "Received: " + extras.toString());

                String internalMessageType = extras.getString("type");
                if("event.switch".equals(internalMessageType)){

                    boolean isOn = extras.getString("status").equals("true");
                    dispatchSwitchToWidget(context, isOn);

                } else if("request.stats".equals(internalMessageType)){
                    // The first three values aren't used yet
                    String mac = extras.getString(DeviceStatType.TYPE_MAC.getCloudKey());
                    String deviceId = extras.getString(DeviceStatType.TYPE_DEVICEID.getCloudKey());
                    String time = extras.getString(DeviceStatType.TYPE_UPDATETIME.getCloudKey());
                    String isOnString = extras.getString(DeviceStatType.TYPE_STATUS.getCloudKey());
                    String ssid = extras.getString(DeviceStatType.TYPE_SSID.getCloudKey());
                    String voltage = extras.getString(DeviceStatType.TYPE_VOLTAGE.getCloudKey());
                    String current = extras.getString(DeviceStatType.TYPE_CURRENT.getCloudKey());
                    String power = extras.getString(DeviceStatType.TYPE_POWER.getCloudKey());
                    HashMap<DeviceStatType, String> dataUpdate = new HashMap<DeviceStatType, String>(DeviceStatType.values().length);
                    dataUpdate.put(DeviceStatType.TYPE_SSID, ssid);
                    dataUpdate.put(DeviceStatType.TYPE_VOLTAGE, voltage);
                    dataUpdate.put(DeviceStatType.TYPE_CURRENT, current);
                    dataUpdate.put(DeviceStatType.TYPE_POWER, power);

                    PlugTopModel imp = PlugTopModel.getInstance();
                    boolean isOn = Integer.parseInt(isOnString) == 1;
                    if(imp.isOn() != isOn){
                        imp.setIsOn(isOn);
                        dispatchSwitchToWidget(context, isOn);
                    }
                    imp.updateMeterData(dataUpdate);
                }

            }
        }
    }

    /**
     * Notifies any home screen widgets to update their visual state
     */
    private void dispatchSwitchToWidget(Context context, boolean isOn){
        PlugTopModel.getInstance().setIsOn(isOn);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName targetComponent = new ComponentName(context, SwitchAppWidgetProvider.class);
        RemoteViews newView = SwitchAppWidgetProvider.getRemoteViewsWithState(context, isOn);
        appWidgetManager.updateAppWidget(targetComponent, newView);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(Context context, String msg) {
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, BigSwitchActivity.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("GCM Notification")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        notificationManager.notify((int) (Math.random() * 1000), mBuilder.build());
    }

}
