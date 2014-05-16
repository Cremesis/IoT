/*******************************************************************************
 * Copyright (c) 2012, Institute for Pervasive Computing, ETH Zurich.
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the Institute nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE INSTITUTE AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE INSTITUTE OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * 
 * This file is part of the Californium (Cf) CoAP framework.
 ******************************************************************************/
package it.master.iot;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;

import ch.ethz.inf.vs.californium.CaliforniumLogger;
import ch.ethz.inf.vs.californium.Utils;
import ch.ethz.inf.vs.californium.coap.MediaTypeRegistry;
import ch.ethz.inf.vs.californium.coap.Request;
import ch.ethz.inf.vs.californium.coap.Response;
import ch.ethz.inf.vs.californium.coap.CoAP.Type;
import ch.ethz.inf.vs.scandium.ScandiumLogger;


public class Client2 {

	static {
		CaliforniumLogger.initialize();
		CaliforniumLogger.setLevel(Level.WARNING);

		ScandiumLogger.initializeLogger();
		ScandiumLogger.setLoggerLevel(Level.INFO);
	}

	/*
	 * Main method of this client.
	 */
	public static void main(String[] args) {
		String host="aaaa::c30c:0:0:4";
		URI	uri	= null;
		try {
			uri	= new URI("coap://["+host+"]:5683/hello");
		}
		catch (URISyntaxException e)
		{
			System.err.println("Failed to parse URI: " + e.getMessage());
			System.exit(-1);
		}

		Request	request = Request.newPut();
		request.setURI(uri);
		request.setPayload("v=14");
		request.setType(Type.CON);
		request.getOptions().setContentFormat(MediaTypeRegistry.TEXT_PLAIN);
		System.out.println(Utils.prettyPrint(request));

		try
		{
			request.send();
			// receive response
			Response response = null;
			try {
				response = request.waitForResponse();
			} catch (InterruptedException e) {
				System.err.println("Failed to receive response: " +	e.getMessage());
				System.exit(-1);
			}
			// output response
			if
			(response != null)
			{
				System.out.println(Utils.prettyPrint(response));
				System.out.println("Time elapsed (ms): " + response.getRTT());
			}
			else
			{
				// no response received
				System.err.println("Request timed out");
			}
		}
		catch(Exception e)
		{
			System.err.println("Failed to execute request: " + e.getMessage());
			System.exit(-1);
		}
	}

}
