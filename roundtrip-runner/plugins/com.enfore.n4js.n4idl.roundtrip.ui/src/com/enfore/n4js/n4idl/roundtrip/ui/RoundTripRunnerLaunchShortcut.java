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
package com.enfore.n4js.n4idl.roundtrip.ui;

import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.n4js.runner.ui.AbstractRunnerLaunchShortcut;

import com.enfore.n4js.n4idl.roundtrip.RoundTripRunner;

/**
 * Node runner {@link ILaunchShortcut}, used to create node runner launches from selections in the UI
 */
public class RoundTripRunnerLaunchShortcut extends AbstractRunnerLaunchShortcut {

	@Override
	protected String getRunnerId() {
		return RoundTripRunner.ID;
	}

	/**
	 * LaunchConfigurationTyp id/name for node.js, needs to match one used in plugin xml
	 */
	private final static String LAUNCHCONFIGURATIONTYPE_ROUNDTRIPRUNNER_ID = "com.enfore.n4js.n4idl.roundtrip.ui.launchConfigurationType";

	@Override
	protected String getLaunchConfigTypeID() {
		return LAUNCHCONFIGURATIONTYPE_ROUNDTRIPRUNNER_ID;
	}
}
