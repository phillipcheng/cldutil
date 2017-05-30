package org.cldutil.datacrawl.util;

import org.cldutil.datacrawl.NextPage;

public class SomePageErrorException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private NextPage errorPage;

	public NextPage getErrorPage() {
		return errorPage;
	}

	public void setErrorPage(NextPage errorPage) {
		this.errorPage = errorPage;
	}
}
