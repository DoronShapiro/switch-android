package org.shapiro.doron.impswitch;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by doron on 11/29/14.
 */
public class AgentManager {
    private static AgentManager instance;

    private static final String TAG = "AM";
    private static final String AGENT_PREFS_FILE = "AgentPrefsFile";
    private static final String AGENT_PREFS_KEY_ID = "id";

    private AgentManager(){

    }

    /**
     * Getter for singleton representing an Agent's identity.  Note this only currently supports one
     * Agent at a time.
     */
    public static AgentManager getInstance(){
        if(instance == null){
            instance = new AgentManager();
        }
        return instance;
    }

    /**
     * Get the Agent ID used for constructing an Agent cloud endpoint.
     */
    public String getAgentID(Context context){
        SharedPreferences agentPrefs = context.getSharedPreferences(AGENT_PREFS_FILE, 0);
        return agentPrefs.getString(AGENT_PREFS_KEY_ID, "");
    }

    /**
     * Set the Agent ID used for constructing an Agent cloud endpoint.  This value persists across
     * app lifecycles.
     */
    public void setAgentID(Context context, String id){
        SharedPreferences.Editor editor = context.getSharedPreferences(AGENT_PREFS_FILE, 0).edit();
        editor.putString(AGENT_PREFS_KEY_ID, id);
        editor.apply();
    }
}
