/*
 * Copyright (c) 2018 NumberFour AG, Luca Beurer-Kellner
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Luca Beurer-Kellner - Initial API and implementation
 */
package com.enfore.n4js.n4idl.roundtrip.server;

import java.io.IOException;
import java.net.MalformedURLException;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.resource.URLResource;

import com.enfore.n4js.n4idl.roundtrip.RoundTripRunnerConstants;

/**
 * IDE server to exchange RoundTrip Runner results and display instance
 * diagrams.
 * 
 * Only servers content to requests from localhost (no network exposure).
 */
public class RoundTripServer {
	private static Logger LOGGER = Logger.getLogger(RoundTripServer.class);
	private static final int DEFAULT_PORT = RoundTripRunnerConstants.DEFAULT_PORT;

	private static RoundTripServer INSTANCE = null;

	private Server server;
	private int port;
	private HandlerList handlerList;
	private ResultRouteHandler resultRouteHandler;

	/**
	 * Returns the currently running instance of the {@link RoundTripServer}.
	 */
	public static RoundTripServer getInstance() {
		if (null == INSTANCE) {
			INSTANCE = create();
		}
		return INSTANCE;
	}

	/**
	 * Stops the currently running instance of the {@link RoundTripServer}.
	 */
	public static void stopInstance() {
		try {
			if (null != INSTANCE) {
				INSTANCE.stop();
			}
		} catch (Exception e) {
			if (INSTANCE != null && INSTANCE.server.isRunning()) {
				LOGGER.error("Failed to stop RoundTripServer instance on port ");
				return;
			}
		}
		RoundTripServer.INSTANCE = null;
	}

	/**
	 * Tries to launch an instance of the {@link RoundTripServer} and returns it.
	 *
	 * May return {@code null} if something goes wrong.
	 */
	private static RoundTripServer create() {
		int port = DEFAULT_PORT;
		RoundTripServer server = new RoundTripServer(DEFAULT_PORT);
		try {
			server.start();
		} catch (Exception e) {
			LOGGER.error("Failed to launch RoundTripServer on port " + port + ": " + e);
			return null;
		}

		return server;
	}

	/**
	 * Instantiates a new {@link RoundTripServer} listening on the given port.
	 */
	private RoundTripServer(int port) {
		server = new Server();

		// configure address binding
		ServerConnector connector = new ServerConnector(server);
		connector.setPort(port);
		connector.setHost("127.0.0.1");
		server.setConnectors(new Connector[] { connector });

		// configure handlers
		this.resultRouteHandler = new ResultRouteHandler();

		this.handlerList = new HandlerList();
		this.handlerList.addHandler(this.resultRouteHandler);
		this.handlerList.addHandler(createResourceHandler());
		this.handlerList.addHandler(new ObjectGraphHandler());

		// wrap handler list in {@link ContextHandler}
		ContextHandler contextHandler = new ContextHandler();
		contextHandler.setHandler(this.handlerList);
		
		server.setHandler(contextHandler);

		// configure result route handler listeners
		this.resultRouteHandler.addListener((module, result) -> {
			RoundTripResultStorage.addResult(module, result);
		});

		this.port = port;
	}

	/**
	 * Instantiates and configures the {@link ResourceHandler} for serving
	 * the mxgraph UML graphs.
	 */
	private static ResourceHandler createResourceHandler() {
		ResourceHandler resourceHandler = new ResourceHandler();

		final String externalURI = RoundTripServer.class.getClassLoader().getResource("res").toExternalForm();
		
		try {
			final Resource baseResource = URLResource.newResource(externalURI);
			resourceHandler.setBaseResource(baseResource);
		} catch (MalformedURLException e) {
			LOGGER.error(String.format("Failed to create a base resource from URI '%s'", externalURI));
			return null;
		} catch (IOException e) {
			LOGGER.error(String.format("Failed to create a base resource from URI '%s'", externalURI));
			return null;
		}
		
		return resourceHandler;
	}

	/**
	 * Starts this instance of the {@link RoundTripServer}.
	 * @throws Exception
	 */
	private void start() throws Exception {
		server.start();
		LOGGER.info("RoundTripServer running on port " + this.getPort());
	}

	/**
	 * Stops this instance of the {@link RoundTripServer}.
	 * @throws Exception
	 */
	private void stop() throws Exception {
		if (server.isRunning()) {
			server.stop();
		}
	}

	/**
	 * Joins the thread of this server instance.
	 */
	private void join() throws InterruptedException {
		server.join();
	}

	/**
	 * Returns the port this instance of the {@link RoundTripServer} is running on.
	 * @return
	 */
	public int getPort() {
		return this.port;
	}

	/**
	 * Returns the {@link ResultRouteHandler} that is used by the server.
	 */
	public ResultRouteHandler getResultRouteHandler() {
		return this.resultRouteHandler;
	}

	/**
	 * Main method for testing purposes.
	 */
	public static void main(String[] args) throws InterruptedException {
		RoundTripServer instance = RoundTripServer.getInstance();
		instance.join();
	}
}
