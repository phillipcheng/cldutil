package org.cldutil.util;

import java.util.Date;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cldutil.xml.taskdef.VarType;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class ScriptEngineUtil {
	private static ScriptEngineManager manager = new ScriptEngineManager();
	private static Logger logger =  LogManager.getLogger(ScriptEngineUtil.class);
	
	public static Object eval(String exp, VarType toType, Map<String,Object> variables){
		return eval(exp, toType, variables, true);
	}
	public static Object eval(String exp, VarType toType, Map<String,Object> variables, boolean logError){
		ScriptEngine jsEngine = manager.getEngineByName("JavaScript");
		if (variables!=null){
			for (String key: variables.keySet()){
				Object v = variables.get(key);
				jsEngine.put(key, v);
			}
		}
		try {
			Object ret = jsEngine.eval(exp);
			logger.debug(String.format("eval %s get result %s", exp, ret));
			if (toType == VarType.OBJECT){
				return ret;
			}
			if (ret!=null){
				if (ret instanceof String){
					if (toType == VarType.STRING){
						;
					}else if (toType == VarType.INT){
						ret = Integer.parseInt((String)ret);
					}else{
						logger.error(String.format("unsupported to type for string result: %s", toType));
					}
				}else if (ret instanceof Double){
					if (toType ==VarType.INT){
						ret = ((Double)ret).intValue();
					}else if (toType==VarType.FLOAT){
						ret = ((Double)ret).floatValue();
					}else{
						logger.error(String.format("unsupported to type for double result: %s", toType));
					}
				}else if (ret instanceof Boolean){
					if (toType==VarType.BOOLEAN){
						return ret;
					}else{
						logger.error(String.format("expect a boolean result from exp:%s", exp));
						return null;
					}
				}else{
					logger.error(String.format("unsupported type of eval ret: %s", ret.getClass()));
				}
			}
			return ret;
		} catch (Exception e) {
			if (logError){
				logger.error(String.format("error msg: %s while eval %s, var map is %s", e.getMessage(), exp, variables));
			}
			return null;
		}
	}
}
