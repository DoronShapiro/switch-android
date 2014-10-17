package org.shapiro.doron.impswitch.listeners;

import org.shapiro.doron.impswitch.enums.DeviceStatType;

import java.util.Map;

/**
 * Created by doron on 10/10/14.
 */
public interface SwitchMeterListener {
    public void onGetDataUpdate(Map<DeviceStatType, String> update);
}
