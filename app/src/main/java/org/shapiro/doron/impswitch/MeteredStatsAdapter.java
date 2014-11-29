package org.shapiro.doron.impswitch;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.shapiro.doron.impswitch.comms.AgentConnection;
import org.shapiro.doron.impswitch.enums.DeviceStatType;
import org.shapiro.doron.impswitch.listeners.SwitchMeterListener;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by doron on 10/5/14.
 */
public class MeteredStatsAdapter extends BaseAdapter {

    private static final DeviceStatType[] TYPES_TO_DISPLAY =
            {DeviceStatType.TYPE_SSID, DeviceStatType.TYPE_CURRENT,
             DeviceStatType.TYPE_POWER, DeviceStatType.TYPE_VOLTAGE};

    private Map<DeviceStatType, String> mAvailablePowerData;
    private Context mContext;
    private SwitchMeterListener mSwitchMeterListener;
    private final Handler mRefreshHandler;
    private final Runnable mRefreshRunner;

    public MeteredStatsAdapter(Context context, final int refreshRate){
        mAvailablePowerData = new LinkedHashMap<DeviceStatType, String>(TYPES_TO_DISPLAY.length);
        mContext = context;
        mSwitchMeterListener = new SwitchMeterListener() {

            @Override
            public void onGetDataUpdate(Map<DeviceStatType, String> update) {
                mAvailablePowerData.clear();
                for(DeviceStatType allowableType : TYPES_TO_DISPLAY){
                    if(update.containsKey(allowableType)){
                        mAvailablePowerData.put(allowableType, update.get(allowableType));
                    }
                }
                notifyDataSetChanged();
            }
        };

        PlugTopModel.getInstance().registerMeterListener(mSwitchMeterListener);

        mRefreshHandler = new Handler();
        mRefreshRunner = new Runnable() {
            @Override
            public void run() {
                refresh();
                mRefreshHandler.postDelayed(mRefreshRunner, refreshRate);
            }
        };
    }

    public void close(){
        PlugTopModel.getInstance().deregisterMeterListener(mSwitchMeterListener);
        setAutoRefreshState(false);
    }

    public void setAutoRefreshState(boolean shouldRun){
        if(shouldRun){
            mRefreshHandler.post(mRefreshRunner);
        } else {
            mRefreshHandler.removeCallbacks(mRefreshRunner);
            refresh();
        }
    }

    public void refresh(){
        AgentConnection.queryStats(mContext,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                    }
                });
    }

    private DeviceStatType getTypeForPosition(int position){
        return (DeviceStatType) mAvailablePowerData.keySet().toArray()[position];
    }

    @Override
    public int getCount() {
        return mAvailablePowerData.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        DeviceStatType targetType = getTypeForPosition(position);

        View view = convertView;

        if(view == null){
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.listitem_meterstat, parent, false);
        }

        StringBuilder keyText = new StringBuilder(mContext.getString(targetType.getLabelId()));
        keyText.setCharAt(0, Character.toUpperCase(keyText.charAt(0)));
        keyText.append(":");
        ((TextView) view.findViewById(R.id.text_meterstat_key)).setText(keyText);

        StringBuilder valueText = new StringBuilder();
        valueText.append(mAvailablePowerData.get(targetType));
        valueText.append(" ");
        valueText.append(mContext.getString(targetType.getUnitId()));
        ((TextView) view.findViewById(R.id.text_meterstat_value)).setText(valueText);

        return view;
    }
}
