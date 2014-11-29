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

    public static AgentManager getInstance(){
        if(instance == null){
            instance = new AgentManager();
        }
        return instance;
    }

    public String getAgentID(Context context){
        SharedPreferences agentPrefs = context.getSharedPreferences(AGENT_PREFS_FILE, 0);
        return agentPrefs.getString(AGENT_PREFS_KEY_ID, "");
    }

    public void setAgentID(Context context, String id){
        SharedPreferences.Editor editor = context.getSharedPreferences(AGENT_PREFS_FILE, 0).edit();
        editor.putString(AGENT_PREFS_KEY_ID, id);
        editor.apply();
    }
}
