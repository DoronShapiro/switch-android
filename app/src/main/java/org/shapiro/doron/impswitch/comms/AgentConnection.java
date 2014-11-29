package org.shapiro.doron.impswitch.comms;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.shapiro.doron.impswitch.AgentManager;
import org.shapiro.doron.impswitch.enums.CloudCommand;

/**
 * Created by doron on 9/21/14.
 */
public class AgentConnection {
    private static final String BASE_AGENT_URL = "https://agent.electricimp.com/";

    private static AsyncHttpClient client = new AsyncHttpClient();

    // TODO: what if agent id not configured?
    private static String getAgentUrl(Context context) {
        String id = AgentManager.getInstance().getAgentID(context);
        if(id == null || id.isEmpty()){
            throw new IllegalStateException("No Agent ID found");
        }
        return BASE_AGENT_URL + id;
    }

    public static void sendFlip(Context context, AsyncHttpResponseHandler responseHandler){
        sendValue(context, CloudCommand.KEY_SWITCH, "2", responseHandler);
    }

    public static void sendGcmRegistration(Context context, String id, AsyncHttpResponseHandler responseHandler){
        sendValue(context, CloudCommand.KEY_REGISTER_GCM, id, responseHandler);
    }

    public static void queryStats(Context context, AsyncHttpResponseHandler responseHandler){
        String gcmId = GcmUtility.getId(context);
        if(gcmId != null) {
            sendValue(context, CloudCommand.KEY_REQUEST_STAT, gcmId, responseHandler);
        } else {
            throw new IllegalStateException("No GCM ID found");
        }
    }

    private static void sendValue(Context context, CloudCommand key, String value, AsyncHttpResponseHandler responseHandler){
        RequestParams params = new RequestParams(key.getKey(), value);
        client.get(getAgentUrl(context), params, responseHandler);
    }

}
