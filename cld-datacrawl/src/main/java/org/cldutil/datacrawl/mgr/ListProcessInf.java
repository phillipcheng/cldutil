package org.cldutil.datacrawl.mgr;

import java.util.Date;
import java.util.List;

import org.cldutil.datacrawl.CrawlConf;
import org.cldutil.taskmgr.entity.Task;
import org.cldutil.taskmgr.entity.TaskStat;
import org.cldutil.util.entity.Category;
import org.hibernate.SessionFactory;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public interface ListProcessInf {
	public List<Task> process(HtmlPage listPage, Date readTime, 
			Category cat, CrawlConf cconf, Task task, int maxItems, WebClient wc) throws InterruptedException;
}
