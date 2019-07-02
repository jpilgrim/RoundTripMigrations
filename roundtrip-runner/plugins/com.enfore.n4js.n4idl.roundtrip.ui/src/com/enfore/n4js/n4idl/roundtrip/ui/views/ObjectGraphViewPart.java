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

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.internal.browser.BrowserViewer;
import org.eclipse.ui.part.ViewPart;

import com.enfore.n4js.n4idl.roundtrip.server.RoundTripResultListener;
import com.enfore.n4js.n4idl.roundtrip.server.RoundTripServer;
import com.enfore.n4js.n4idl.roundtrip.ui.viewer.ObjectGraphViewerTypeTemplate;

/**
 * Simple {@link IViewPart} to display object graphs as returned by an execution
 * of the RoundTrip Runner.
 */
@SuppressWarnings("restriction")
public abstract class ObjectGraphViewPart extends ViewPart implements IViewPart, RoundTripResultListener {

	private BrowserViewer browserViewer;

	@Override
	public void createPartControl(Composite parent) {
		GridLayoutFactory.fillDefaults().applyTo(parent);	
		browserViewer = new BrowserViewer(parent, SWT.NONE );
		
		GridDataFactory.fillDefaults().grab(true, true).applyTo(browserViewer);
		browserViewer.setURL(ObjectGraphViewerTypeTemplate.makeAbsoluteServerUrl(getQuery()));
		//viewer.setURL("https://google.com");
	}

	/**
	 * Returns the server query this view uses in order to display a object
	 * graph.
	 *
	 * The query should be given as a relative-uri with a leading '/' character.
	 * (e.g. "/graph/current/original")
	 */
	protected abstract String getQuery();

	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);
		// add this view as a listener for round-trip results
		RoundTripServer.getInstance().getResultRouteHandler().addListener(this);	
	}

	@Override
	public void dispose() {
		// remove this view as a listener for round-trip results
		RoundTripServer.getInstance().getResultRouteHandler().removeListener(this);
	}

	@Override
	public void setFocus() {
		this.getBrowserViewer().setFocus();
	}

	@Override
	public void resultReceived(String module, String result) {
		// refresh browser, if a new result has been received
		if (null != getBrowserViewer()) {
			getBrowserViewer().getDisplay().asyncExec(() -> {
				getBrowserViewer().setURL(ObjectGraphViewerTypeTemplate.makeAbsoluteServerUrl(getQuery()));
				//getBrowserViewer().setURL("https://google.com");
				getBrowserViewer().refresh();
			});
		}
	}

	/**
	 * @return The {@link BrowserViewer} instance of this view's control.
	 *
	 *         This may return {@code null} if the view has not been created yet
	 *         (cf. {@link #createPartControl(Composite)}).
	 */
	public BrowserViewer getBrowserViewer() {
		return browserViewer;
	}
}
