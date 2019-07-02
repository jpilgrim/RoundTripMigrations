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
package com.enfore.n4js.n4idl.roundtrip.ui.views;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;

/**
 * Displays the all intermediate object-graphs of an execution of a round-trip migration.
 */
public class FullObjectGraphViewPart extends ObjectGraphViewPart {

	@SuppressWarnings("restriction")
	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		addAction("Save PDF", () -> getBrowserViewer().getBrowser().evaluate("savePDF();"));
		addAction("Save SVG", () -> getBrowserViewer().getBrowser().evaluate("saveSVG();"));
		addAction("Toggle Trace Links", () -> getBrowserViewer().getBrowser().evaluate("toggleTraceLinks();"));
	}

	@Override
	protected String getQuery() {
		return "/graph/current";
	}

	private void addAction(String title, Runnable runnable) {
		Action action = new Action(title) {
			@Override
			public void run() {
				runnable.run();
			}
		};

		IActionBars actionBars = getViewSite().getActionBars();
		IMenuManager dropDownMenu = actionBars.getMenuManager();
		IToolBarManager toolBar = actionBars.getToolBarManager();
		dropDownMenu.add(action);
		toolBar.add(action);
	}

}
