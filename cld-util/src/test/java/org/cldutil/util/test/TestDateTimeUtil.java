package org.cldutil.util.test;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cldutil.util.DateTimeRange;
import org.cldutil.util.DateTimeUtil;
import org.junit.Test;

public class TestDateTimeUtil {
	

	public static final Logger logger = LogManager.getLogger(TestDateTimeUtil.class);
	public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	@Test
	public void testEpoch(){
		long epoch = DateTimeUtil.getEpochDefaultFormat("2017-09-01");
		logger.info(epoch);
	}
	/*
	 * TODO adding MdHms_DF support
	 * 2013年1月14日10:00到2013年1月21日09:59:59
	 * 2013年01月12日00:00到01月18日00:00
	 * 2013年1月21日上午09:30
	 */
	@Test
	public void testGetTimeRange(){
		DateTimeRange dr;
		Date fromDT, toDT;
		
		dr = DateTimeUtil.getDTRange("2013年1月14日10:00到2013年1月21日09:59:59");
		fromDT = dr.getFromDT();
		toDT = dr.getToDT();
		logger.info("fromDT:" + fromDT);
		logger.info("toDT:" + toDT);
		
		dr = DateTimeUtil.getDTRange("2013年01月12日00:00到01月18日00:00");
		fromDT = dr.getFromDT();
		toDT = dr.getToDT();
		logger.info("fromDT:" + fromDT);
		logger.info("toDT:" + toDT);
		
		dr = DateTimeUtil.getDTRange("2013年1月21日上午09:30");
		fromDT = dr.getFromDT();
		toDT = dr.getToDT();
		logger.info("fromDT:" + fromDT);
		logger.info("toDT:" + toDT);
	}
	
	
	@Test
	public void testLocale(){
		String str = "2013年1月21日上午09:30";
		try {
			Date d = DateTimeUtil.yMdahm_DF_China.parse(str);
			logger.info(d);
			String str2= DateTimeUtil.yMdahm_DF_China.format(d);
			logger.info(str2);
			assertTrue(str.equals(str2));
		} catch (ParseException e) {
			logger.error("",e);
			assertTrue(false);
		}
		
		
	}

}
