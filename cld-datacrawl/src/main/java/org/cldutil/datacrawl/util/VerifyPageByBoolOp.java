package org.cldutil.datacrawl.util;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cldutil.datacrawl.CrawlConf;
import org.cldutil.datacrawl.mgr.BinaryBoolOpEval;
import org.cldutil.util.PatternResult;
import org.cldutil.xml.taskdef.BinaryBoolOp;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class VerifyPageByBoolOp implements VerifyPage {
	public static Logger logger = LogManager.getLogger(VerifyPageByBoolOp.class);
	
	private BinaryBoolOp[] validationOps;
	private CrawlConf cconf;
	
	public VerifyPageByBoolOp(){
	}
	
	public VerifyPageByBoolOp(BinaryBoolOp[] ops, CrawlConf cconf){
		this.validationOps = ops;
		this.cconf = cconf;
	}
	
	public VerifyPageByBoolOp(List<BinaryBoolOp> ops, CrawlConf cconf){
		this.validationOps = ops.toArray(new BinaryBoolOp[ops.size()]);
		this.cconf = cconf;
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		for (BinaryBoolOp x: validationOps){
			sb.append(x);
		}
		return sb.toString();
	}
	
	@Override
	public boolean verifySuccess(HtmlPage page, Object param) {
		Map<String, Object> attributes = (Map<String, Object>)param;
		for (BinaryBoolOp op: validationOps){
			if (BinaryBoolOpEval.eval(op, attributes, page, cconf)==false){
				return false;
			}
		}
		return true;
	}
}
