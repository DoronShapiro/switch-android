package org.shapiro.doron.impswitch.enums;

/**
 * Created by doron on 10/5/14.
 *
 * Represents a command to be sent to the Agent cloud.
 */
public enum CloudCommand {
    KEY_SWITCH("switch"), // Toggles plugtop state
    KEY_REGISTER_GCM("registerGCM"), // Registers GCM ID with agent
    KEY_REQUEST_STAT("requestStats"); // Requests a full state update from plugtop

    private String serverSideKey;

    CloudCommand(String serverSideKey){
        this.serverSideKey = serverSideKey;
    }

    public String getKey(){
        return serverSideKey;
    }
}
