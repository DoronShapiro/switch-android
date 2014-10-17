package org.shapiro.doron.impswitch.enums;

import org.shapiro.doron.impswitch.R;

/**
 * Created by doron on 10/10/14.
 */
public enum DeviceStatType {
    TYPE_POWER("power", R.string.text_label_power, R.string.text_unit_power),
    TYPE_CURRENT("current", R.string.text_label_current, R.string.text_unit_current),
    TYPE_VOLTAGE("voltage", R.string.text_label_voltage, R.string.text_unit_voltage),
    TYPE_SSID("ssid", R.string.text_label_ssid, R.string.text_unit_empty),
    TYPE_STATUS("status", R.string.text_label_status, R.string.text_unit_empty),
    TYPE_MAC("mac", R.string.text_label_mac, R.string.text_unit_empty),
    TYPE_DEVICEID("deviceid", R.string.text_label_deviceid, R.string.text_unit_empty),
    TYPE_UPDATETIME("time", R.string.text_label_updatetime, R.string.text_unit_empty);

    private String cloudKey;
    private int uiLabelId;
    private int uiUnitId;

    DeviceStatType(String cloudKey, int uiLabelId, int uiUnitId){
        this.cloudKey = cloudKey;
        this.uiLabelId = uiLabelId;
        this.uiUnitId = uiUnitId;
    }

    public int getLabelId(){
        return uiLabelId;
    }

    public int getUnitId(){
        return uiUnitId;
    }

    public String getCloudKey(){
        return cloudKey;
    }
}
