package org.cldutil.pagea.general;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cldutil.datacrawl.CrawlConf;
import org.cldutil.datacrawl.ProductHandler;
import org.cldutil.taskmgr.entity.Task;
import org.cldutil.util.entity.Category;
import org.cldutil.util.entity.Product;
import org.cldutil.xml.mytaskdef.ParsedBrowsePrd;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class DefaultHandler implements ProductHandler{
	
	private static Logger logger =  LogManager.getLogger(DefaultHandler.class);
	
	private CrawlConf cconf;
	//must have a constructor with CrawlConf as parameter
	public DefaultHandler(CrawlConf cconf){
		this.cconf = cconf;
	}
	
	@Override
	public void handleProduct(Product product, Task task, ParsedBrowsePrd taskDef) {
	}

	@Override
	public void handleCategory(String requestUrl, HtmlPage catlist,
			Category cat, Task task) throws InterruptedException {
	}
}
