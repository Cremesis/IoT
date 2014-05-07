package ch.ethz.inf.vs.californium.examples;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

import ch.ethz.inf.vs.californium.coap.CoAP.ResponseCode;
import ch.ethz.inf.vs.californium.coap.Option;
import ch.ethz.inf.vs.californium.coap.OptionSet;
import ch.ethz.inf.vs.californium.coap.Request;
import ch.ethz.inf.vs.californium.coap.Response;
import ch.ethz.inf.vs.californium.network.Exchange;
import ch.ethz.inf.vs.californium.server.resources.ResourceBase;

public class HelloResource extends ResourceBase {
	
	private Pattern pattern = Pattern.compile("numero=(\\d+)");

	public HelloResource(String name) {
		super(name);
	}
	
	@Override
	public void handleGET(Exchange exchange) {
		// TODO Auto-generated method stub
		Request request = exchange.getCurrentRequest();
		if(request.getOptions().getURIQueryCount() > 0) {
			List<String> params = request.getOptions().getURIQueries();
			for(String param : params) {
				Matcher m = pattern.matcher(param);
				if(m.matches()) {
					int n = Integer.parseInt(m.group(1));
					System.out.println("Parametro v = " + n);
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("Human readable", "Hai scritto " + n);
					jsonObject.put("Result", Math.pow(n, 2));
					Response response = new Response(ResponseCode.CONTENT);
					OptionSet options = new OptionSet();
					options.setContentFormat(50);
					response.setOptions(options);
					response.setPayload(jsonObject.toString());
					respond(exchange, response);
					return;
				} else {
					exchange.respond(ResponseCode.BAD_REQUEST, "CIAO, mi hai detto " + param);
					return;
				}
			}
		}
		exchange.respond(ResponseCode.BAD_REQUEST, "CIAO, mancano i parametri");
	}

}
