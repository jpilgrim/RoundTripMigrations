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
 * Enum to name the different object graphs.
 */
public enum ObjectGraphType {
	/**
	 * The originally created object graph.
	 */
	original,
	/**
	 * The migrated object-graph.
	 */
	migrated,
	/**
	 * The object-graph after a modification of the migrated instance.
	 */
	migratedModified,
	/**
	 * The object-graph after migrating it back to the original version.
	 */
	roundTrip
}
