package it.master.iot;

import java.applet.Applet;
import java.applet.AudioClip;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import ch.ethz.inf.vs.californium.coap.Request;
import ch.ethz.inf.vs.californium.coap.Response;

public class AlarmObserver extends Thread {
	
	private Request request = null;
	private String SOUND_FILE = "/home/user/Downloads/californium-template/californium-template/media/beep.wav";
	private AudioClip clip = null;
	
	public AlarmObserver(Request request) {
		// Load sound file
		try {
			clip = Applet.newAudioClip(new URL("file://" + SOUND_FILE));
		} catch(Exception e) {
			e.printStackTrace();
		}
		this.request = request;
	}
	
	@Override
	public void run() {
		Response response = null;
		do {
			// receive response
			try {
				response = request.waitForResponse(0);
			} catch (InterruptedException e) {
				System.err.println("Failed to receive response: "
						+ e.getMessage());
				System.exit(-1);
			}
			// output response
			if (response != null) {
				try {
					JSONObject jsonObject = new JSONObject(response.getPayloadString());
					boolean isAlarmed = (Boolean) jsonObject.get("alarm");
					if(isAlarmed) {
						clip.play();
						System.out.println("ALARM FROM: " + response.getSource().getHostAddress());
					}
//					System.out.println(Utils.prettyPrint(response));
				} catch(JSONException e) {
					System.out.println("NO JSON RESPONSE");
				}
			} else {
				// no response received
				System.err.println("Request timed out");
			}
		} while(true);
	}
}
