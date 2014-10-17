package org.shapiro.doron.impswitch.broadcast_receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Pair;

import org.shapiro.doron.impswitch.SwitchAlarmManager;

/**
 * Created by doron on 10/11/14.
 */
public class BootBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {

            // Force refresh of AlarmManager
            boolean isAlarmSet = SwitchAlarmManager.getInstance(context).isAlarmActive();
            SwitchAlarmManager.getInstance(context).setAlarmEnabled(isAlarmSet);
        }
    }

}
