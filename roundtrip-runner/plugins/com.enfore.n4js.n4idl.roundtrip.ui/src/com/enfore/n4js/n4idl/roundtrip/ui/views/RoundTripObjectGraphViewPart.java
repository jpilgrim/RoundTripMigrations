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
package com.enfore.n4js.n4idl.roundtrip.ui.views;

import com.enfore.n4js.n4idl.roundtrip.server.ObjectGraphType;

/**
 * Displays the roundTrip object graph of an execution of a migration round trip.
 */
public class RoundTripObjectGraphViewPart extends ObjectGraphViewPart {

	@Override
	protected String getQuery() {
		return "/graph/current/" + ObjectGraphType.roundTrip.name();
	}

}
