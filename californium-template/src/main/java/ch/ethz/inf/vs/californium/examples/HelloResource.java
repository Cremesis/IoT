package ch.ethz.inf.vs.californium.examples;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

import ch.ethz.inf.vs.californium.coap.CoAP.ResponseCode;
import ch.ethz.inf.vs.californium.coap.OptionSet;
import ch.ethz.inf.vs.californium.coap.Request;
import ch.ethz.inf.vs.californium.coap.Response;
import ch.ethz.inf.vs.californium.network.Exchange;
import ch.ethz.inf.vs.californium.observe.ObserveManager;
import ch.ethz.inf.vs.californium.observe.ObserveRelation;
import ch.ethz.inf.vs.californium.observe.ObservingEndpoint;
import ch.ethz.inf.vs.californium.server.resources.ResourceBase;

public class HelloResource extends ResourceBase {
	
	private int n = 15;
	private Pattern pattern = Pattern.compile("numero=(\\d+)");

	public HelloResource(String name) {
		super(name);
		setObservable(true);
	}
	
	@Override
	public void handleGET(Exchange exchange) {
		System.out.println("GET");
		Response response = new Response(ResponseCode.CONTENT);
		response.setPayload("n ha valore " + n);
		respond(exchange, response);
	}
	
	@Override
	public void handlePUT(Exchange exchange) {
		// In order to remove an observer (I think)
//		ObserveManager observeManager = new ObserveManager();
//		InetSocketAddress initSocketAddress = exchange.getEndpoint().getAddress();
//		ObservingEndpoint observingEndpoint = observeManager.getObservingEndpoint(initSocketAddress);
//		this.removeObserveRelation(new ObserveRelation(observingEndpoint, this, exchange));
		Request request = exchange.getCurrentRequest();
		if(request.getOptions().getURIQueryCount() > 0) {
			List<String> params = request.getOptions().getURIQueries();
			for(String param : params) {
				Matcher m = pattern.matcher(param);
				if(m.matches()) {
					n = Integer.parseInt(m.group(1));
					/*
					System.out.println("Parametro v = " + n);
//					JSONObject jsonObject = new JSONObject();
//					jsonObject.put("Human readable", "Hai scritto " + n);
//					jsonObject.put("Result", Math.pow(n, 2));
//					OptionSet options = new OptionSet();
//					options.setContentFormat(50);
//					response.setOptions(options);
					response.setPayload(jsonObject.toString());
					 */
					Response response = new Response(ResponseCode.CONTENT);
					response.setPayload("Risposta PUT");
					respond(exchange, response);
					notifyObserverRelations();
					return;
				} else {
					exchange.respond(ResponseCode.BAD_REQUEST, "CIAO, mi hai detto " + param);
					System.out.println("Sent BAD response!");
					return;
				}
			}
		}
		exchange.respond(ResponseCode.BAD_REQUEST, "CIAO, mancano i parametri");
	}

}
