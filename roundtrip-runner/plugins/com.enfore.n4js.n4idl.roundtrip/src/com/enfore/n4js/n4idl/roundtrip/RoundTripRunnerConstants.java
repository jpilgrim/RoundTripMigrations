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
package com.enfore.n4js.n4idl.roundtrip;

import org.eclipse.xtext.naming.QualifiedName;

/**
 * Constant global values that define the main-parameters of the round-trip runner plugins.
 */
public class RoundTripRunnerConstants {
	/**
	 * The root directory of the resource files.
	 */
	public static final String RESOURCE_ROOT = "res/";

	/**
	 * The default port to serve the round-trip viewer UI at.
	 */
	public static final int DEFAULT_PORT = 2121;

	/**
	 * The OsGi symbolic name of the round-trip runner main bundle.
	 */
	public static final String MAIN_PLUGIN_ID = "com.enfore.n4js.n4idl.roundtrip";

	/**
	 * The name of the contract interface between N4IDL code and the round-trip runner.
	 */
	public static final String RUNNER_CONTRACT_INTERFACE = "RoundTripMigration";

	/**
	 * FQN of the contract interface that a class must implement to be considered
	 * a valid input for the round-trip runner.
	 */
	public static final QualifiedName ROUND_TRIP_MIGRATION_FQN = QualifiedName.create("com",
			"enfore", "n4js", "n4idl", "roundtrip",
			RoundTripRunnerConstants.RUNNER_CONTRACT_INTERFACE,
			RoundTripRunnerConstants.RUNNER_CONTRACT_INTERFACE);
}
