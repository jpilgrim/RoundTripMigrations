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
package com.enfore.n4js.n4idl.roundtrip.ui.viewer

import com.enfore.n4js.n4idl.roundtrip.RoundTripRunnerConstants
import com.enfore.n4js.n4idl.roundtrip.server.ObjectGraphType
import com.enfore.n4js.n4idl.roundtrip.server.RoundTripResultStorage.RoundTripResult

class ObjectGraphViewerTypeTemplate extends ObjectGraphViewerTemplate{
	final ObjectGraphType graphType;

	/**
	 * Initializes a new ObjectGraph HTML template for the
	 * given ObjectGraphType.
	 *
	 * @param graphType The object graph type to display.
	 * 					This directly translates to a property access on the given data.
	 */
	new(ObjectGraphType graphType) {
		this.graphType = graphType;
	}

	override createScript(RoundTripResult result) {
		return '''const moduleName = "«result.module»"; const data = JSON.parse(`«result.data»`).«graphType.name»;'''
	}

	/**
	 * Returns an absolute URL to the local RoundTripServer given
	 * a relative URI.
	 */
	static def String makeAbsoluteServerUrl(String uri) {
		return '''http://localhost:«RoundTripRunnerConstants.DEFAULT_PORT»«uri»''';
	}
}
