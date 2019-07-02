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

/**
 * Listener interface to listen for new {@link RoundTripServer} results.
 */
public interface RoundTripResultListener {
	/**
	 * This method is invoked whenever the {@link RoundTripServer} receives a
	 * new result via its result route.
	 *
	 * @param module
	 *            The module for which a new result has been created.
	 * @param result
	 *            The result data (JSON-encoded string).
	 */
	void resultReceived(String module, String result);
}
