Electric Imp Plugtop Setup and Deployment
======================

Cloud and device configuration for an Electric Imp-powered plugtop device.

Building and Configuration
-----

### Google Cloud Configuration
[Google Cloud Messenger](https://developer.android.com/google/gcm/gcm.html) (GCM) must be configured for communication between the app and plugtop.  If you have not already, follow the "Getting Started" instructions [here](https://developer.android.com/google/gcm/gs.html) and record both the project number and API key.

### Device Configuration
1. Follow the BlinkUp instructions at https://electricimp.com/docs/gettingstarted/blinkup/ to set up an Electric Imp account and connect it to the plugtop device.
2. Follow the instructions at https://electricimp.com/docs/resources/ideuserguide/#3-5 to add a new device model and assign it to the plugtop.
3. Upload the provided Agent and Device code to the model.
4. In the Agent code, replace the "PUT\_GCM\_KEY_HERE" string with the API key you recorded in the Google Cloud Configuration step.
5. Click on "Agent Link" in the Imp IDE and record the portion of the url after 
"https://agent.electricimp.com/".
	* For example, if your Agent URL is  "https://agent.electricimp.com/xXxXxXxXxX-x", keep the identifier "xXxXxXxXxX-x" for later use.