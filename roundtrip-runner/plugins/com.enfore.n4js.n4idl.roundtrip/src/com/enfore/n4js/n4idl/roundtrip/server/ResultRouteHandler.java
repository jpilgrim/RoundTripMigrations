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

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

/**
 * Handles POST requests to the "/result" route.
 *
 * The body of requests to this route is handled as a JSON-encoded string
 * of a valid ObjectGraph data object (in the context of the ObjectGraph UI).
 *
 */
public class ResultRouteHandler extends AbstractHandler {
	public static final String ROUTE = "/result";
	public static final String MODULE_QUERY_PARAMETER = "module";

	private Set<RoundTripResultListener> listeners = new HashSet<>();

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		// ignore non-"/result" requests
		if (!baseRequest.getRequestURI().equals(ROUTE)) {
			return;
		}

		// ignore non-POST requests
		if (!baseRequest.getMethod().equals("POST")) {
			return;
		}

		// determine module for which the result is posted
		String module = request.getParameter(MODULE_QUERY_PARAMETER);

		// do not handle, if module is not specified
		if (null == module) {
			return;
		}

		// invoke registered listeners
		final String body = getRequestBodyAsString(request);
		this.listeners.forEach(l -> l.resultReceived(module, body));

		// Declare response encoding and types
		response.setContentType("text/html; charset=utf-8");

		// Declare response status code
		response.setStatus(HttpServletResponse.SC_OK);

		// Inform jetty that this request has now been handled
		baseRequest.setHandled(true);
	}

	/**
	 * Registers a new {@link RoundTripResultListener}.
	 */
	public void addListener(RoundTripResultListener listener) {
		this.listeners.add(listener);
	}

	/**
	 * Removes a registered {@link RoundTripResultListener}.
	 */
	public void removeListener(RoundTripResultListener listener) {
		this.listeners.remove(listener);
	}

	ResultRouteHandler() {
		// package constructor to avoid instantiation by clients
	}

	/**
	 * Tries to read the body from the given request as a string.
	 *
	 * Return {@code null} if it fails.
	 */
	private static String getRequestBodyAsString(HttpServletRequest request) {
		StringBuffer jb = new StringBuffer();
		String line = null;
		try {
			BufferedReader reader = request.getReader();
			while ((line = reader.readLine()) != null)
				jb.append(line);
		} catch (Exception e) {
			return null;
		}
		return jb.toString();
	}
}