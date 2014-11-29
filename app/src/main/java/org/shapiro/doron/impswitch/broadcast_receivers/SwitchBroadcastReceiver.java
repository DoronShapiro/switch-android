package org.shapiro.doron.impswitch.broadcast_receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.shapiro.doron.impswitch.R;
import org.shapiro.doron.impswitch.SwitchAlarmManager;
import org.shapiro.doron.impswitch.comms.AgentConnection;

/**
 * Created by doron on 10/5/14.
 */
public class SwitchBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "br/SBR";
    public static final String ACTION_SWITCH = "org.shapiro.doron.impswitch.ACTION_SWITCH";
    public static final String EXTRA_ISALARM = "isAlarm";

    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.v(TAG, "got switch broadcast");

        String action = intent.getAction();
        Bundle extras = intent.getExtras();

        if(extras != null && !extras.isEmpty()){
            boolean isAlarm = intent.getBooleanExtra(EXTRA_ISALARM, false);

            if(isAlarm){
                Log.v(TAG, "switch broadcast was an alarm");
                SwitchAlarmManager.getInstance(context).setAlarmEnabled(false);
            }
        }

        if (ACTION_SWITCH.equals(action)) {
            AgentConnection.sendFlip(context, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Log.v(TAG, "Switch server command failed: " + statusCode);
                    switch (statusCode) {
                        case 0:
                            Toast.makeText(context, R.string.text_error_connection, Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(context, R.string.text_error_communication, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
