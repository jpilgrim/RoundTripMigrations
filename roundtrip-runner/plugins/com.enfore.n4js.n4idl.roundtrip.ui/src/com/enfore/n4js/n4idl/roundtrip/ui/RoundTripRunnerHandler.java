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

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;

import com.google.inject.Inject;

/**
 * A handler which triggers a RoundTrip Runner execution for the currently active editor.
 */
public class RoundTripRunnerHandler extends AbstractHandler {
	@Inject
	RoundTripRunnerLaunchShortcut launchShortcut;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IEditorPart editorPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();

		if (editorPart == null) {
			String message = "The active editor could not be determined.";
			ErrorDialog.openError(Display.getCurrent().getActiveShell(), "Failed to run the RoundTrip Runner", message,
					new Status(IStatus.ERROR, RoundTripRunnerActivator.PLUGIN_ID, message), IStatus.ERROR);
			return null;
		}
		launchShortcut.launch(editorPart, "run");

		return null;
	}
}
