package org.shapiro.doron.impswitch.enums;

/**
 * Created by doron on 10/5/14.
 */
public enum CloudCommand {
    KEY_SWITCH("switch"),
    KEY_REGISTER_GCM("registerGCM"),
    KEY_REQUEST_STAT("requestStats");

    private String serverSideKey;

    CloudCommand(String serverSideKey){
        this.serverSideKey = serverSideKey;
    }

    public String getKey(){
        return serverSideKey;
    }
}
