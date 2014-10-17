package org.shapiro.doron.impswitch;

import android.content.Context;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.shapiro.doron.impswitch.comms.ImpAgent;
import org.shapiro.doron.impswitch.enums.DeviceStatType;
import org.shapiro.doron.impswitch.listeners.SwitchMeterListener;
import org.shapiro.doron.impswitch.listeners.SwitchStatusListener;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by doron on 9/21/14.
 */
public class ImpModel {
    private static ImpModel impModel;

    private boolean mIsOn;
    private Map<DeviceStatType, String> mMeterData;
    private LinkedList<SwitchStatusListener> mSwitchStatusListeners;
    private LinkedList<SwitchMeterListener> mSwitchMeterListeners;

    private ImpModel(){
        mMeterData = new HashMap<DeviceStatType, String>(DeviceStatType.values().length);
        mSwitchStatusListeners = new LinkedList<SwitchStatusListener>();
        mSwitchMeterListeners = new LinkedList<SwitchMeterListener>();
    }

    public static ImpModel getInstance(){
        if(impModel == null){
            impModel = new ImpModel();
        }
        return impModel;
    }

    public void requestSync(Context context){
        ImpAgent.queryStats(context, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    public void setIsOn(boolean isOn){
        boolean statusChanged = mIsOn != isOn;
        mIsOn = isOn;
        if(statusChanged) {
            notifySwitchListeners();
        }
    }

    public void updateMeterData(Map<DeviceStatType, String> update){
        mMeterData.clear();
        mMeterData.putAll(update);
        notifyMeterListeners();
    }

    public boolean isOn(){
        return mIsOn;
    }

    public void registerSwitchListener(SwitchStatusListener listener){
        mSwitchStatusListeners.add(listener);
    }

    private void notifySwitchListeners(){
        for (SwitchStatusListener listener : mSwitchStatusListeners){
            listener.onSwitch(mIsOn);
        }
    }

    public void registerMeterListener(SwitchMeterListener listener){
        mSwitchMeterListeners.add(listener);
    }

    public void deregisterMeterListener(SwitchMeterListener listener){
        mSwitchMeterListeners.remove(listener);
    }

    private void notifyMeterListeners(){
        for (SwitchMeterListener listener : mSwitchMeterListeners){
            listener.onGetDataUpdate(new HashMap<DeviceStatType, String>(mMeterData));
        }
    }
}
