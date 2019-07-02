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
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

/**
 * A composed {@link Handler} that delegates to its sub-handlers
 * based on the route of the URI.
 */
public class RouteHandler extends AbstractHandler {

	Map<String, Handler> routeHandlers = new HashMap<>();
	Handler defaultHandler = null;

	/**
	 * Adds a route to this route handler.
	 *
	 * Note that the route must include the leading '/' character.
	 */
	public void addRoute(String route, Handler handler) {
		routeHandlers.put(route, handler);
	}

	/**
	 * Sets the default handler that is invoked for all otherwise unhandled requests.
	 */
	public void setDefaultHandler(Handler handler) {
		this.defaultHandler = handler;
	}

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		for (Entry<String, Handler> entry : routeHandlers.entrySet()) {
			final String route = entry.getKey();
			final Handler handler = entry.getValue();

			if (baseRequest.getRequestURI().equals(route)) {
				handler.handle(target, baseRequest, request, response);
				// if request was handled, do not trigger other handlers
				if (baseRequest.isHandled()) {
					break;
				}
			}
		}

		if (!baseRequest.isHandled() && null != defaultHandler) {
			defaultHandler.handle(target, baseRequest, request, response);
		}
	}

	@Override
	public final void doStart() throws Exception {
		for (Handler handler : routeHandlers.values()) {
			handler.start();
		}

		super.doStart();
	}

	@Override
	public final void doStop() throws Exception {
		for (Handler handler : routeHandlers.values()) {
			handler.stop();
		}

		super.doStop();
	}

}
