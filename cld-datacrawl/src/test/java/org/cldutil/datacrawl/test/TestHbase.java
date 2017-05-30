package org.cldutil.datacrawl.test;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cldutil.datacrawl.CrawlTaskGenerator;
import org.cldutil.datastore.impl.HbaseUtil;
import org.cldutil.taskmgr.entity.Task;
import org.junit.Test;

public class TestHbase {
	
	private static Logger logger =  LogManager.getLogger(TestHbase.class);
	
	@Test
	public void test0(){
		HbaseUtil.copyRow("nasdaq-ids", "nasdaq-ids|ALL", "nasdaq-ids|ALL_2015-10-17");
	}
	

}
