package org.cldutil.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MoneyUtil {

	public static Logger logger = LogManager.getLogger(MoneyUtil.class);
	
	public static final char RMB_1='¥';
	public static final String RMB_2="￥"; //CH_MONEY_SIGN
	public static final String PRICE_SPE=",";
	
	public static final char DOLLAR_1='$';
	
	/*
	 * ¥74.50
	 * ￥ 157.80
	 * ￥ 1,157.80
	 * 
	 */
	public static double getRMBValue(String orgString){
		String stripSep = orgString.replaceAll(PRICE_SPE, "");
		String withNoSign = null;
		if (stripSep.indexOf(RMB_1) != -1){
			withNoSign = stripSep.substring(stripSep.indexOf(RMB_1) + 1);
			return Double.parseDouble(withNoSign);
		}else if (stripSep.indexOf(RMB_2) != -1){
			withNoSign = stripSep.substring(stripSep.indexOf(RMB_2) + 1);
			return Double.parseDouble(withNoSign);
		}else{
			logger.error("price symbal not found is:" + orgString);
			return -1;
		}
	}
	
	/*
	 * $12
	 * $1,000
	 */
	public static double getDollarValue(String orgString){
		String stripSep = orgString.replaceAll(PRICE_SPE, "");
		String withNoSign = stripSep;
		if (stripSep.indexOf(DOLLAR_1) != -1){
			withNoSign = stripSep.substring(stripSep.indexOf(RMB_1) + 1);	
		}
		return Double.parseDouble(withNoSign);
	}
	
	public static final String RANGE_SEP="-";
	public static double[] getPriceRange(String str){
		String[] nums = str.split(RANGE_SEP);
		double[] out = new double[nums.length];
		for (int i=0; i<nums.length; i++){
			out[i]=getDollarValue(nums[i]);
		}
		return out;
	}
}
