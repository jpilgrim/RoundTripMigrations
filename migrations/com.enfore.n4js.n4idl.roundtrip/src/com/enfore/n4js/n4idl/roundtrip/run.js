/*
 * Copyright (c) 2018 Luca Beurer-Kellner
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Luca Beurer-Kellner - Initial API and implementation
 */

/**
 * Main entry-point for running RoundTrip executions.
 * 
 * Reads the roundTripModule from the environment variables that are set in the
 * RoundTrip Runner logic and imports the RoundTripRunner class.
 * 
 * Delegates to RoundTripRunner.main(constructor{AbstractRoundTripTest}) for further
 * round trip test execution.
 */
 
const options = {
	"module": process.env.ROUND_TRIP_MODULE,
	"className": process.env.ROUND_TRIP_CLASS_NAME
}

const targetModule = require(options.module)
const runner = require("./RoundTripRunner")

runner.default.main(targetModule[options.className], options)