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

import org.eclipse.n4js.utils.N4ExecutableExtensionFactory;

import com.enfore.n4js.n4idl.roundtrip.RoundTripRunnerModule;
import com.google.inject.Injector;

/**
 * Executable extension factory for the runner Node.js UI module.
 */
public class RunnerRoundTripUiExecutableExtensionFactory extends N4ExecutableExtensionFactory {

	@Override
	protected ClassLoader getClassLoader() {
		return this.getClass().getClassLoader();
	}

	@Override
	protected Injector getInjector() {
		return RoundTripRunnerActivator.getInstance().getInjector(RoundTripRunnerModule.RUNNER_UI_MODULE_ID);
	}

	@Override
	protected String getBundleId() {
		return RoundTripRunnerActivator.PLUGIN_ID;
	}

}
