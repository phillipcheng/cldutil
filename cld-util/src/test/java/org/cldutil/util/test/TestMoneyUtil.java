package org.cldutil.util.test;

import static org.junit.Assert.assertTrue;

import org.cldutil.util.MoneyUtil;
import org.junit.Test;

public class TestMoneyUtil {
	@Test
	/*
	 * ¥74.50
	 * ￥ 157.80
	 * ￥ 1,157.80
	 * 
	 */
	public void testGetRMBValue(){
		double d = MoneyUtil.getRMBValue("¥74.50");
		assertTrue(d==74.5);
		
		
		d = MoneyUtil.getRMBValue("￥ 157.80");
		assertTrue(d==157.8);
		
		d = MoneyUtil.getRMBValue("￥ 1,157.80");
		assertTrue(d==1157.8);
		
		
	}
}
