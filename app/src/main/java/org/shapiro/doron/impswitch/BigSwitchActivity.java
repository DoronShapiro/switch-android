package org.shapiro.doron.impswitch;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.doomonafireball.betterpickers.radialtimepicker.RadialPickerLayout;
import com.doomonafireball.betterpickers.radialtimepicker.RadialTimePickerDialog;

import org.shapiro.doron.impswitch.comms.AgentConnection;
import org.shapiro.doron.impswitch.comms.GcmUtility;
import org.shapiro.doron.impswitch.listeners.SwitchStatusListener;

import java.util.GregorianCalendar;


public class BigSwitchActivity extends FragmentActivity
        implements SwitchStatusListener, RadialTimePickerDialog.OnTimeSetListener {

    private final static String TAG = "BSA";
    private final static String TAG_TIMEPICKER = "BSA.TP";

    private boolean mIsOn;
    private MeteredStatsAdapter mMeteredStatsAdapter;
    private TextView mAlarmStatusView;
    private Switch mAlarmSwitchView;

    //Do not convert to local var: observed class keeps weak references
    private SharedPreferences.OnSharedPreferenceChangeListener mAlarmSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // put play services-dependent code here
        if(GcmUtility.checkPlayServices(this)){
            GcmUtility.registerGcmIfNecessary(this);
        }

        setContentView(R.layout.activity_big_switch);

        /****************************/
        /* Stat meter configuration */
        /****************************/

        if(mMeteredStatsAdapter == null){
            mMeteredStatsAdapter = new MeteredStatsAdapter(this, 5000);
        }
        ((ListView) findViewById(R.id.list_stats)).setAdapter(mMeteredStatsAdapter);
        mMeteredStatsAdapter.refresh(false);

        findViewById(R.id.button_refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMeteredStatsAdapter.refresh(true);
            }
        });

        /*******************************/
        /* Switch button configuration */
        /*******************************/

        findViewById(R.id.button_switch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AgentConnection.sendFlip(BigSwitchActivity.this, new AgentConnection.DefaultAgentResponseHandler() {
                    @Override
                    public Context getContext() {
                        return BigSwitchActivity.this;
                    }
                });
            }
        });
        onSwitch(PlugTopModel.getInstance().isOn());
        PlugTopModel.getInstance().registerSwitchListener(this);

        /***********************/
        /* Alarm configuration */
        /***********************/

        mAlarmStatusView = (TextView) findViewById(R.id.content_timer_display);
        mAlarmStatusView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pair<Integer, Integer> alarmTime = SwitchAlarmManager.getInstance(BigSwitchActivity.this).getAlarmTime();
                int hour = alarmTime.first;
                int minute = alarmTime.second;
                RadialTimePickerDialog timePickerDialog = RadialTimePickerDialog
                        .newInstance(BigSwitchActivity.this, hour, minute,
                                DateFormat.is24HourFormat(BigSwitchActivity.this));
                timePickerDialog.show(getSupportFragmentManager(), TAG_TIMEPICKER);
            }
        });

        mAlarmSwitchView = (Switch) findViewById(R.id.switch_timer);
        mAlarmSwitchView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SwitchAlarmManager.getInstance(BigSwitchActivity.this).setAlarmEnabled(isChecked);
            }
        });

        mAlarmSetListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                refreshAlarmDisplay();
            }
        };

        SwitchAlarmManager.getInstance(this).registerOnSharedPreferenceChangeListener(mAlarmSetListener);
        refreshAlarmDisplay();
    }

    @Override
    protected void onResume() {
        super.onResume();
        GcmUtility.checkPlayServices(this);
        if(mIsOn){
            mMeteredStatsAdapter.setAutoRefreshState(true);
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        mMeteredStatsAdapter.setAutoRefreshState(false);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        mMeteredStatsAdapter.close();
        mMeteredStatsAdapter = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.big_switch, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            final EditText editText = new EditText(this);
            editText.setHint(R.string.hint_agent_id);
            editText.setText(AgentManager.getInstance().getAgentID(this));
            builder.setView(editText);
            builder.setTitle(R.string.title_agent_url).
                    setPositiveButton(R.string.button_update, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            AgentManager.getInstance().setAgentID(BigSwitchActivity.this, editText.getText().toString());
                        }
                    }).setNegativeButton(R.string.button_cancel, null);
            builder.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Sets the window's state for the plugtop.  Sets the stat meter display to only auto-refresh
     * when the plugtop is on.
     * @param switchOn The new state of the plugtop
     */
    @Override
    public void onSwitch(boolean switchOn) {
        mIsOn = switchOn;
        updateSwitchButtonText(switchOn);
        if(mMeteredStatsAdapter != null) { // TODO: there was once a NPE below... fix it for real
            mMeteredStatsAdapter.setAutoRefreshState(switchOn);
        }
    }

    private void updateSwitchButtonText(boolean switchOn) {
        Button switchButton = (Button) findViewById(R.id.button_switch);
        if(switchButton != null){
            if(switchOn){
                switchButton.setText(R.string.text_button_switch_off);
            } else {
                switchButton.setText(R.string.text_button_switch_on);
            }
        }
    }

    @Override
    public void onTimeSet(RadialPickerLayout radialPickerLayout, int hourOfDay, int minute) {
        SwitchAlarmManager.getInstance(this).setAlarmTimeAndEnable(hourOfDay, minute);
    }

    //TODO: have this take parameters and update on the listener
    private void refreshAlarmDisplay(){
        boolean alarmActive = SwitchAlarmManager.getInstance(this).isAlarmActive();

        Pair<Integer, Integer> alarmTime = SwitchAlarmManager.getInstance(this).getAlarmTime();
        int hour = alarmTime.first;
        int minute = alarmTime.second;

        mAlarmStatusView.setText(DateFormat.format("h:mm a", new GregorianCalendar(0, 0, 0, hour, minute)));

        mAlarmSwitchView.setChecked(alarmActive);
        int textColor = alarmActive ?
                getResources().getColor(R.color.default_text_color_holo_light) :
                getResources().getColor(R.color.default_text_color_holo_light_disabled);
        mAlarmStatusView.setTextColor(textColor);
    }
}
