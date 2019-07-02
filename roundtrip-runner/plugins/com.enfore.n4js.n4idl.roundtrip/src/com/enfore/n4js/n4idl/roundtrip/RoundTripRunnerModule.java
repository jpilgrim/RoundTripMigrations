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

import org.eclipse.xtext.service.AbstractGenericModule;

public class RoundTripRunnerModule extends AbstractGenericModule {
	/**
	 * The ID of the runner UI module.
	 */
	public static final String RUNNER_UI_MODULE_ID = RoundTripRunnerModule.class.getName();
	
//	/**
//	 * Custom {@link NodeRunOptions} implementation.
//	 */
//	public Class<? extends NodeRunOptions> bindNodeRunOptions() {
//		return RoundTripRunConfiguration.class;
//	}
//
//	/**
//	 * Custom command builder to inject roundTrip data to ELF code.
//	 */
//	public Class<? extends NodeEngineCommandBuilder> bindNodeEngineCommandBuilder() {
//		return RoundTripEngineCommandBuilder.class;
//	}
}
