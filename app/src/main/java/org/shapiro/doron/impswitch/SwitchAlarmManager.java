package org.shapiro.doron.impswitch;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.util.Log;
import android.util.Pair;

import org.shapiro.doron.impswitch.broadcast_receivers.BootBroadcastReceiver;
import org.shapiro.doron.impswitch.broadcast_receivers.SwitchBroadcastReceiver;

import java.util.Calendar;

/**
 * Created by doron on 10/12/14.
 */
public class SwitchAlarmManager {

    private static class PendingAlarmIntentFactory {
        private static PendingIntent intent;

        public static PendingIntent getAlarmIntent(Context context){
            if(intent == null) {
                Intent switchIntent = new Intent(context, SwitchBroadcastReceiver.class);
                switchIntent.setAction(SwitchBroadcastReceiver.ACTION_SWITCH);
                switchIntent.putExtra(SwitchBroadcastReceiver.EXTRA_ISALARM, true);
                intent = PendingIntent.getBroadcast(context, 0, switchIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            }
            return intent;
        }

    }

    private static final String TAG = "SAM";
    private static final String TIMER_PREFS_FILE = "TimerPrefsFile";
    private static final String TIMER_PREFS_KEY_TIMERACTIVE = "isActive";
    private static final String TIMER_PREFS_KEY_HOUR = "hour";
    private static final String TIMER_PREFS_KEY_MINUTE = "minute";

    private static SwitchAlarmManager instance;
    SharedPreferences mTimerPrefs;
    Context mApplicationContext;

    public static SwitchAlarmManager getInstance(Context context){
        if(instance == null){
            instance = new SwitchAlarmManager(context);
        }
        return instance;
    }

    private SwitchAlarmManager(Context context){
        mApplicationContext = context.getApplicationContext();
        mTimerPrefs = mApplicationContext.getSharedPreferences(TIMER_PREFS_FILE, 0);
    }

    public void setAlarmEnabled(boolean enabled){
        SharedPreferences.Editor timerPrefsEditor = mTimerPrefs.edit();
        timerPrefsEditor.putBoolean(TIMER_PREFS_KEY_TIMERACTIVE, enabled);
        timerPrefsEditor.apply();
        registerAlarmWithSystem();
    }

    /**
     * Sets the alarm time and enables the alarm.  Note that setAlarmEnabled does not need to be
     * called after this.
     */
    public void setAlarmTimeAndEnable(int hourOfDay, int minute){
        SharedPreferences.Editor timerPrefsEditor = mTimerPrefs.edit();
        timerPrefsEditor.putInt(TIMER_PREFS_KEY_HOUR, hourOfDay);
        timerPrefsEditor.putInt(TIMER_PREFS_KEY_MINUTE, minute);
        timerPrefsEditor.putBoolean(TIMER_PREFS_KEY_TIMERACTIVE, true);
        timerPrefsEditor.apply();
        registerAlarmWithSystem();
    }

    public Pair<Integer, Integer> getAlarmTime(){
        int hourOfDay = mTimerPrefs.getInt(TIMER_PREFS_KEY_HOUR, 0);
        int minute = mTimerPrefs.getInt(TIMER_PREFS_KEY_MINUTE, 0);
        return new Pair<Integer, Integer>(hourOfDay, minute);
    }

    public boolean isAlarmActive(){
        return mTimerPrefs.getBoolean(TIMER_PREFS_KEY_TIMERACTIVE, false);
    }

    public void registerOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener){
        mTimerPrefs.registerOnSharedPreferenceChangeListener(listener);
    }

    private void registerAlarmWithSystem(){
        AlarmManager alarmManager = (AlarmManager) mApplicationContext.getSystemService(Context.ALARM_SERVICE);
        ComponentName receiver = new ComponentName(mApplicationContext, BootBroadcastReceiver.class);
        PendingIntent alarmIntent = PendingAlarmIntentFactory.getAlarmIntent(mApplicationContext);
        alarmManager.cancel(alarmIntent);
        if(isAlarmActive()) {
            int hourOfDay = mTimerPrefs.getInt(TIMER_PREFS_KEY_HOUR, 0);
            int minute = mTimerPrefs.getInt(TIMER_PREFS_KEY_MINUTE, 0);
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);
            if(calendar.before(Calendar.getInstance())){
                calendar.add(Calendar.DAY_OF_YEAR, 1);
            }

            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);

            mApplicationContext.getPackageManager().setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);
            Log.v(TAG, "set alarm");
        } else {
            Log.v(TAG, "canceled alarm");

            mApplicationContext.getPackageManager().setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP);
        }
    }
}
