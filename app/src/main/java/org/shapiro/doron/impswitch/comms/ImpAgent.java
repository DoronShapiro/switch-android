package org.shapiro.doron.impswitch.comms;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.shapiro.doron.impswitch.enums.CloudCommand;

/**
 * Created by doron on 9/21/14.
 */
public class ImpAgent {
    private static final String BASE_AGENT_URL = "https://agent.electricimp.com/";
    private static final String AGENT = "PUT_AGENT_ID_HERE";

    private static AsyncHttpClient client = new AsyncHttpClient();

    private static String getAgentUrl() {
        return BASE_AGENT_URL + AGENT;
    }

    public static String getAgentID(){
        return AGENT;
    }

    public static void sendFlip(AsyncHttpResponseHandler responseHandler){
        sendValue(CloudCommand.KEY_SWITCH, "2", responseHandler);
    }

    public static void sendGcmRegistration(String id, AsyncHttpResponseHandler responseHandler){
        sendValue(CloudCommand.KEY_REGISTER_GCM, id, responseHandler);
    }

    public static void queryStats(Context context, AsyncHttpResponseHandler responseHandler){
        String gcmId = GcmUtility.getId(context);
        if(gcmId != null) {
            sendValue(CloudCommand.KEY_REQUEST_STAT, gcmId, responseHandler);
        } else {
            throw new IllegalStateException("No GCM id found");
        }
    }

    private static void sendValue(CloudCommand key, String value, AsyncHttpResponseHandler responseHandler){
        RequestParams params = new RequestParams(key.getKey(), value);
        client.get(getAgentUrl(), params, responseHandler);
    }

}
