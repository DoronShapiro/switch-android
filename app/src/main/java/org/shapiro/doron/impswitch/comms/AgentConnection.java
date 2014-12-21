package org.shapiro.doron.impswitch.comms;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.shapiro.doron.impswitch.AgentManager;
import org.shapiro.doron.impswitch.R;
import org.shapiro.doron.impswitch.enums.CloudCommand;

/**
 * Created by doron on 9/21/14.
 */
public class AgentConnection {

    /**
     * A default AsyncHttpResponseHandler for use in any of this class's message-sending methods.
     * Does nothing if the message is successfully sent but logs the error and displays a relevant
     * error message in a Toast on failure.
     */
    public abstract static class DefaultAgentResponseHandler extends AsyncHttpResponseHandler{
        private static final String TAG = "c.AC.DARH";

        /**
         * Injects a Context used to display a Toast on error.
         * @return The provided Context.
         */
        public abstract Context getContext();

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            Log.v(TAG, "Server command failed: error " + statusCode);
            Context context = getContext();
            switch (statusCode) {
                case 0:
                    Toast.makeText(context, R.string.text_error_connection, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(context, R.string.text_error_communication, Toast.LENGTH_SHORT).show();
            }
        }
    }

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

    /**
     * Sends a message to the cloud to flip the state of the plugtop.
     * @param responseHandler A callback for the resulting HTTP request.
     */
    public static void sendFlip(Context context, AsyncHttpResponseHandler responseHandler){
        sendValue(context, CloudCommand.KEY_SWITCH, "2", responseHandler);
    }

    /**
     * Sends a message to the cloud to register this device's Google Cloud Messenger ID.
     * @param id The GCM ID to register.
     * @param responseHandler A callback for the resulting HTTP request.
     */
    public static void sendGcmRegistration(Context context, String id, AsyncHttpResponseHandler responseHandler){
        sendValue(context, CloudCommand.KEY_REGISTER_GCM, id, responseHandler);
    }

    /**
     * Sends a message to the cloud requesting that the plugtop send meter information (voltage, etc)
     * @param responseHandler A callback for the resulting HTTP request.
     */
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
