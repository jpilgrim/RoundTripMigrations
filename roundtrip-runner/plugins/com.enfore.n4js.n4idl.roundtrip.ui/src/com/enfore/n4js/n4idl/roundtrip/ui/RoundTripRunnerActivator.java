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

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.n4js.ui.internal.N4JSActivator;
import org.eclipse.xtext.util.Modules2;
import org.osgi.framework.BundleContext;

import com.enfore.n4js.n4idl.roundtrip.RoundTripRunnerModule;
import com.enfore.n4js.n4idl.roundtrip.server.RoundTripServer;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * The activator class controls the plug-in life cycle
 */
public class RoundTripRunnerActivator extends N4JSActivator {
	// The plug-in ID
	public static final String PLUGIN_ID = "com.enfore.n4js.n4idl.roundtrip.ui"; //$NON-NLS-1$

	// The shared instance
	private static RoundTripRunnerActivator INSTANCE;

	private static final Logger logger = Logger.getLogger(RoundTripRunnerActivator.class);

	/**
	 * The constructor
	 */
	public RoundTripRunnerActivator() {
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext )
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		INSTANCE = this;

		// make sure the RoundTripServer is running
		RoundTripServer.getInstance();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext )
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		INSTANCE = null;
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static RoundTripRunnerActivator getInstance() {
		return INSTANCE;
	}

	/**
	 * Prints given message to eclipse error log
	 *
	 * @param msg
	 *            the message
	 * @param ex
	 *            the exception, may be null
	 */
	public static void logError(String msg, Throwable ex) {
		getInstance().getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, IStatus.OK, msg, ex));
	}

	@Override
	protected Injector createInjector(String language) {
		// only handle RUNNER_UI_MODULE_ID in the sub-implementation
		if (!language.equals(RoundTripRunnerModule.RUNNER_UI_MODULE_ID)) {
			return super.createInjector(language);
		}

		try {
			Module runtimeModule = getRuntimeModule(N4JSActivator.ORG_ECLIPSE_N4JS_N4JS);
			Module sharedStateModule = getSharedStateModule();
			Module uiModule = getUiModule(N4JSActivator.ORG_ECLIPSE_N4JS_N4JS);
			Module roundTripRunnerModule = getRoundTripRunnerUIModule();
			Module mergedModule = Modules2.mixin(runtimeModule, sharedStateModule, uiModule, roundTripRunnerModule);
			return Guice.createInjector(mergedModule);
		} catch (Exception e) {
			logger.error("Failed to create injector for " + language);
			logger.error(e.getMessage(), e);
			throw new RuntimeException("Failed to create injector for " + language, e);
		}
	}

	/**
	 * Returns the RoundTrip Runner module.
	 */
	private Module getRoundTripRunnerUIModule() {
		return new RoundTripRunnerModule();
	}

}
