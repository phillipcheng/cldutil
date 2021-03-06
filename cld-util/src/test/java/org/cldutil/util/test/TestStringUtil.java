package org.cldutil.util.test;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cldutil.util.JsonUtil;
import org.cldutil.util.StringUtil;
import org.json.JSONObject;
import org.junit.Test;


public class TestStringUtil {
	public static final Logger logger = LogManager.getLogger(TestStringUtil.class);
	
	@Test
	public void testGetStringFromNum(){
		String s = StringUtil.getStringFromNum(1, 3);
		logger.info(s);
		assertTrue("001".equals(s));
		
		s = StringUtil.getStringFromNum(12, 4);
		logger.info(s);
		assertTrue("0012".equals(s));
		
		s = StringUtil.getStringFromNum(23, 3);
		assertTrue(s.equals("023"));
	}
	
	@Test
	public void testGetFirstSentence(){
		String str = StringUtil.getFirstSentence("购买由亚马逊自营图书(电子书和Z实惠图书除外)超过1件，另外购买《古文观止(上下册)(插图珍藏版) 》，可享受￥ 5.0元优惠。" +
				" 在订单确认页面输入GA8HD5R4 点此查看如何获得促销优惠 (促销说明)");
		logger.info(str);
	}
	
	@Test
	public void testGetIntegerFrom(){
		int i = StringUtil.getIntegerFrom("显示 1 个结果");
		assertTrue(i==1);
		
		i = StringUtil.getIntegerFrom("显示  15 个结果");
		assertTrue(i==15);
	}
	
	@Test
	public void testFromStringList(){
		String in = "999999,999998";
		List<String> slist = StringUtil.fromStringList(in);
		assertTrue(slist.size()==2);
		assertTrue(slist.get(0).equals("999999"));
		assertTrue(slist.get(1).equals("999998"));
	}
	
	@Test
	public void testJsonUtil1(){
		JSONObject jobj = JsonUtil.getJsonDataFromSingleParameterFunctionCall("sinaSSOController.preloginCallBack({\"retcode\":0})");
		logger.info(jobj);
	}
	
	@Test
	public void testParseSteps(){
		String[] fl = StringUtil.parseFloatSteps("(1)-1-0.5");
		logger.info(StringUtils.join(fl,","));
	}
	
	@Test
	public void testSplit(){
		String param="|nasdaq-quote-fq-historical|nasdaq-quote-historical";
		String[] excludeCmds = StringUtils.split(param,'|');
		logger.info(String.format("excludeCmds: %s", Arrays.asList(excludeCmds)));
	}

}
