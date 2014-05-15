package ch.ethz.inf.vs.californium.examples;

import java.applet.Applet;
import java.applet.AudioClip;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.json.JSONException;
import org.json.JSONObject;

import ch.ethz.inf.vs.californium.CaliforniumLogger;
import ch.ethz.inf.vs.californium.Utils;
import ch.ethz.inf.vs.californium.coap.MediaTypeRegistry;
import ch.ethz.inf.vs.californium.coap.Request;
import ch.ethz.inf.vs.californium.coap.Response;
import ch.ethz.inf.vs.scandium.ScandiumLogger;

public class ClientObserver {
	
	private static String[] hostList = {"aaaa::c30c:0:0:4", "aaaa::c30c:0:0:8"};
	private static Thread[] threads = new Thread[hostList.length];
	
	static {
		CaliforniumLogger.initialize();
		CaliforniumLogger.setLevel(Level.WARNING);
		
		ScandiumLogger.initializeLogger();
		ScandiumLogger.setLoggerLevel(Level.INFO);
	}

	public static void main(String[] args) {
		
		for(int i = 0; i < hostList.length; i++) {
			URI uri = null;
			try {
				uri	= new URI("coap://["+hostList[i]+"]:5683/alarm");
			} catch(URISyntaxException e) {
				System.err.println("Failed to parse URI: " + e.getMessage());
				System.exit(-1);
			}
			Request request = Request.newGet();
			request.setURI(uri);
			request.setPayload("");
			request.getOptions().setContentFormat(MediaTypeRegistry.TEXT_PLAIN);
			request.setConfirmable(false);
			request.setObserve();
			
			try
			{
				request.send();
				System.out.println("SENT OBSERVING REQUEST");
				threads[i] = new AlarmObserver(request);
				threads[i].start();
				
			} catch(Exception e) {
				System.err.println("Failed to execute request: " + e.getMessage());
				System.exit(-1);
			}
		}

	}

}
