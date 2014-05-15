package ch.ethz.inf.vs.californium.examples;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import ch.ethz.inf.vs.californium.server.Server;

public class CaliforniumServer {

	public static void main(String[] args) {
		Server server = new Server();
		
		server.setExecutor(Executors.newScheduledThreadPool(2));
		
		server.add(new HelloResource("hello"));
		
		server.start();
	}

}
