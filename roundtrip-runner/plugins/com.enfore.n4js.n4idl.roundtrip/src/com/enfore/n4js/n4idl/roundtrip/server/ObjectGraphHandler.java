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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import com.enfore.n4js.n4idl.roundtrip.server.RoundTripResultStorage.RoundTripResult;
import com.enfore.n4js.n4idl.roundtrip.ui.viewer.ObjectGraphViewerTemplate;
import com.enfore.n4js.n4idl.roundtrip.ui.viewer.ObjectGraphViewerTypeTemplate;
import com.google.common.base.Optional;

/**
 * A handler to return HTML to display the specified object graph.
 *
 * This handler provides the following routes:
 *
 * /graph/current/original: Display original object graph
 * /graph/current/migrated: Displays migrated object graph
 * /graph/current/migratedModified: Display modified migrated object graph
 * /graph/current/roundTrip: Display round-trip object graph
 * /graph/current: Display all available object graphs in a connected diagram (migration links)
 */
public class ObjectGraphHandler extends AbstractHandler {
	private static final String ROUTE_NAME = "graph";

	private class QueryData {
		String id;
		Optional<ObjectGraphType> type;

		/**
		 * Initializes a new instance of {@link QueryData} with the
		 * given data.
		 *
		 * @param type May be null if the full object graph is requested
		 */
		QueryData(String id, Optional<ObjectGraphType> type) {
			this.id = id;
			this.type = type;
		}
	}

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		Optional<QueryData> queryDataOptional = isHandledQuery(baseRequest);

		// ignore queries that are not handled
		if (!queryDataOptional.isPresent()) {
			return;
		}

		// TODO actually handle id here
		QueryData queryData = queryDataOptional.get();
		if (!queryData.type.isPresent()) {
			handleAllObjectGraph(response, queryData);
		} else {
			handleSpecificObjectGraph(response, queryData);
		}

        baseRequest.setHandled(true);
	}

	private void handleAllObjectGraph(HttpServletResponse response, QueryData queryData) throws IOException {
		final RoundTripResult result = getResultForId(queryData.id);

		final ObjectGraphViewerTemplate template = new ObjectGraphViewerTemplate();

		response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().print(template.createForData(result));
	}

	/**
	 * Handles requests for a specific part of the full round-trip object graph.
	 *
	 * This method assumes that {@link QueryData#type} is present.
	 *
	 * @param response The response to answer in
	 * @param queryData The extracted {@link QueryData}.
	 * @throws IOException
	 */
	private void handleSpecificObjectGraph(HttpServletResponse response, QueryData queryData) throws IOException {
		final RoundTripResult result = getResultForId(queryData.id);

		final ObjectGraphViewerTypeTemplate template = new ObjectGraphViewerTypeTemplate(queryData.type.get());

		response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().print(template.createForData(result));
	}


	private RoundTripResult getResultForId(String id) {
		RoundTripResult result = RoundTripResultStorage.getLatest();

		if (null == result) {
			return new RoundTripResult("", "{}");
		}

		return result;
	}

	/**
	 * Returns the extracted {@link QueryData} from the given {@link Request}, or
	 * an absent {@link Optional} to indicate that the given {@link Request} is not handled.
	 */
	public Optional<QueryData> isHandledQuery(Request baseRequest) {
		final String uri = baseRequest.getRequestURI();
		final String[] segments = uri.split("/");

		// do not handle if the number of segments doesn't match
		if (segments.length < 3) {
			return Optional.absent();
		}

		// first (==[0]) segment is empty due to leading '/' character
		final String path = segments[1];
		final String id = segments[2];

		// do not handle if the main-route doesn't match
		if (!path.equals(ROUTE_NAME)) {
			return Optional.absent();
		}

		// if query is for all object graphs
		if (segments.length == 3) {
			return Optional.of(new QueryData(id, Optional.absent()));
		} // otherwise segments.length must be >= 4

		// otherwise, determine graph type
		final String type = segments[3];
		ObjectGraphType graphType;
		try {
			graphType = ObjectGraphType.valueOf(type);
		} catch (IllegalArgumentException e) {
			return Optional.absent();
		}

		return Optional.of(new QueryData(id, Optional.of(graphType)));
	}

}
