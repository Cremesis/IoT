package ch.ethz.inf.vs.californium.examples;

import java.net.URI;
import java.net.URISyntaxException;

import ch.ethz.inf.vs.californium.Utils;
import ch.ethz.inf.vs.californium.coap.MediaTypeRegistry;
import ch.ethz.inf.vs.californium.coap.Request;
import ch.ethz.inf.vs.californium.coap.Response;

public class ClientObserver {

	public static void main(String[] args) {
		String host="aaaa::c30c:0:0:4";
		URI	uri	= null;
		try {
			uri	= new URI("coap://["+host+"]:5683/event");
		}
		catch (URISyntaxException e)
		{
			System.err.println("Failed to parse URI: " + e.getMessage());
			System.exit(-1);
		}

		Request	request = Request.newGet();
		request.setURI(uri);
		request.setPayload("");
		request.getOptions().setContentFormat(MediaTypeRegistry.TEXT_PLAIN);
		request.setObserve();

		try
		{
			request.send();
			Response response = null;
			
			do {
				// receive response
				try {
					response = request.waitForResponse();
				} catch (InterruptedException e) {
					System.err.println("Failed to receive response: "
							+ e.getMessage());
					System.exit(-1);
				}
				// output response
				if (response != null) {
					System.out.println(Utils.prettyPrint(response));
					System.out.println("Time elapsed (ms): "
							+ response.getRTT());
				} else {
					// no response received
					System.err.println("Request timed out");
				}
			} while(true);
		}
		catch(Exception e)
		{
			System.err.println("Failed to execute request: " + e.getMessage());
			System.exit(-1);
		}
	}

}
