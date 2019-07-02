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
import com.enfore.n4js.n4idl.roundtrip.server.RoundTripResultStorage.RoundTripResult

 class ObjectGraphViewerTemplate {
	/**
	 * Initializes a new ObjectGraph HTML template for the
	 * given ObjectGraphType.
	 */
	new() {}

	/**
	 * Returns the content of a valid HTML file that displays the
	 * object graph described by the given JSON-encoded data.
	 *
	 * @param result The RoundTrip Runner result to display on the page.
	 */
	def String createForData(RoundTripResult result) {
		val html = '''
			<html>
				<head>
					<title>RoundTrip Viewer</title>
					<link rel="stylesheet" href="«makeAbsoluteServerUrl("/js/joint.min.css")»"/>
					<script>
					«createScript(result)»
					</script>
					<style>
						body, #content {
							width: 100%;
							display: block;
							height: 100%;
							margin: 0pt;
							background: transparent;
						}
						@media print
						{    
						    .no-print, .no-print *
						    {
						        display: none !important;
						    }
						}
					</style>
				</head>
				<body>
					<div id="content"></div>
					<script src="«makeAbsoluteServerUrl("/js/bundle.js")»"></script>
					<a style="display: none;position:absolute;top:0;lef:0" id="link" download="graph.svg" href="#download">Save SVG</a>
				</body>
			</html>
		''';
		return html;
	}

	/**
	 * Returns the JavaScript code that is used to initialize the
	 * viewer UI with the given JSON-encoded data.
	 *
	 * This script is executed after all dependencies have been included,
	 * but before any of the viewer specific code has been executed.
	 */
	protected def createScript(RoundTripResult result) {
		return '''let moduleName = "«result.module»"; let data = JSON.parse(`«result.data»`); console.log("Hello");'''
	}

	/**
	 * Returns an absolute URL to the local RoundTripServer given
	 * a relative URI.
	 */
	static def String makeAbsoluteServerUrl(String uri) {
		return '''http://localhost:«RoundTripRunnerConstants.DEFAULT_PORT»«uri»''';
	}
}
