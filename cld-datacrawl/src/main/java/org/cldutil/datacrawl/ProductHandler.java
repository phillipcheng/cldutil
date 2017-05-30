package org.cldutil.datacrawl;

import org.cldutil.taskmgr.entity.Task;
import org.cldutil.util.entity.Category;
import org.cldutil.util.entity.Product;
import org.cldutil.xml.mytaskdef.ParsedBrowsePrd;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public interface ProductHandler {
	
	//must have a constructor with CrawlConf as parameter
	
	public void handleProduct(Product product, Task task, ParsedBrowsePrd taskDef);
	
	public void handleCategory(String requestUrl, HtmlPage catlist, Category cat, Task task) 
			throws InterruptedException;
	
}
