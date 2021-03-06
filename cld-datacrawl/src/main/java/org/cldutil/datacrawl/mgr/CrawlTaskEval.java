package org.cldutil.datacrawl.mgr;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cldutil.datacrawl.CrawlConf;
import org.cldutil.datacrawl.CrawlUtil;
import org.cldutil.datacrawl.NextPage;
import org.cldutil.datacrawl.util.HtmlPageResult;
import org.cldutil.datacrawl.util.HtmlUnitUtil;
import org.cldutil.datacrawl.util.VerifyPageByBoolOp;
import org.cldutil.taskmgr.TaskUtil;
import org.cldutil.util.PatternIO;
import org.cldutil.util.PatternUtil;
import org.cldutil.util.SafeSimpleDateFormat;
import org.cldutil.util.ScriptEngineUtil;
import org.cldutil.util.StringUtil;
import org.cldutil.xml.mytaskdef.ConfKey;
import org.cldutil.xml.taskdef.AttributeType;
import org.cldutil.xml.taskdef.BinaryBoolOp;
import org.cldutil.xml.taskdef.ScopeType;
import org.cldutil.xml.taskdef.ValueType;
import org.cldutil.xml.taskdef.VarType;

import com.gargoylesoftware.htmlunit.html.DomNamespaceNode;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.DomText;
import com.gargoylesoftware.htmlunit.html.FrameWindow;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class CrawlTaskEval {

	private static Logger logger =  LogManager.getLogger(CrawlTaskEval.class);
	
	public static int getIntValue(Object value, String valueExp){
		if (value instanceof Integer){
			//user set type to int
			return ((Integer)value).intValue();
		}else if (value instanceof String){
			String strValue = ((String)value).trim();
			//user has not set the type default to String
			try{	
				strValue = strValue.replaceAll("\\D+","");
				return Integer.parseInt(strValue);
			}catch(NumberFormatException nfe){
				logger.error(String.format("defined:%s is a number", valueExp), nfe);
			}
		}else{
			logger.error(String.format("expection valueExp to be an integer, but got %s", valueExp));
		}
		return -1;
	}
	
	private static HtmlPage getPage(Object xpathResult, DomNode page, ValueType vt, List<BinaryBoolOp> pvtList, 
			CrawlConf cconf, Map<String, Object> params) 
			throws InterruptedException{
		HtmlPageResult hpResult=null;

		HtmlElement input = (HtmlElement)xpathResult;
		NextPage np = new NextPage(input, (HtmlPage) page, vt.getFrameId(), vt.isReturnParent());
		VerifyPageByBoolOp vp=null;
		if (pvtList!=null){
			vp = new VerifyPageByBoolOp(pvtList.toArray(new BinaryBoolOp[pvtList.size()]), cconf);
		}
		hpResult = HtmlUnitUtil.clickNextPageWithRetryValidate(null, np, vp, params, null, cconf);
		if (hpResult.getErrorCode()==HtmlPageResult.SUCCSS){
			HtmlPage hp = hpResult.getPage();
			return hp;
		}else{
			logger.error("get page error:" + hpResult);
			return null;
		}
	}
	
	

	public static Object eval(DomNode page, ValueType vt, CrawlConf cconf, Map<String, Object> params) throws InterruptedException {
		Map<String, List<? extends DomNode>> pageMap = new HashMap<String, List<? extends DomNode>>();
		List<DomNode> pages = new ArrayList<DomNode>();
		pages.add(page);
		pageMap.put(ConfKey.CURRENT_PAGE, pages);
		pageMap.put(ConfKey.START_PAGE, pages);
		return eval(pageMap, vt, cconf, params);
	}
	
	private static String anchorToFullUrl(HtmlAnchor ha, HtmlPage page){
		try {
			URL url = page.getFullyQualifiedUrl(ha.getHrefAttribute());
			return url.toExternalForm();
		} catch (MalformedURLException e) {
			logger.error("", e);
			return null;
		}
	}
	private static String divToString(HtmlDivision hd){
		DomText dt = hd.getFirstByXPath(".//text()");
		if (dt==null){
			return "";
		}else{
			String tc = dt.getTextContent();
			if ("".equals(tc.trim())){
				return hd.asText();
			}else{
				return tc;
			}
		}
	}
	
	/**
	 * @param entry
	 * @param page: used for htmlImage and htmlAnchor
	 * @return
	 */
	public static String getStringValue(Object entry){
		String finalString=null;
		if (entry instanceof DomText){
			finalString = ((DomText)entry).getTextContent();
		}else if (entry instanceof HtmlDivision){
			finalString = divToString((HtmlDivision)entry);
		}else if (entry instanceof HtmlInput){
			finalString = ((HtmlInput)entry).getValueAttribute();
		}else if (entry instanceof DomNamespaceNode){
			finalString = ((DomNamespaceNode)entry).getTextContent();
		}else if (entry instanceof DomText){
			finalString = ((DomText)entry).getTextContent();
		}else if (entry instanceof HtmlElement){
			finalString = ((HtmlElement)entry).asText();
		}else{
			finalString = entry.toString();
		}
		return finalString.trim();
	}
	
	public static String getURLStringValue(Object entry, DomNode page){
		String finalString=null;
		if (entry instanceof HtmlImage){
			try {
				URL url = ((HtmlPage)page).getFullyQualifiedUrl(((HtmlImage)entry).getSrcAttribute());
				finalString = url.toExternalForm();
			} catch (MalformedURLException e) {
				logger.error("", e);
			}
		}else if (entry instanceof HtmlAnchor){
			HtmlPage p = null;
			if (page instanceof HtmlPage){
				p = (HtmlPage)page;
			}else{
				p = (HtmlPage)page.getPage();
			}
			finalString = anchorToFullUrl((HtmlAnchor)entry, (HtmlPage)p);
		}
		return finalString;
	}
	
	private static String processFile(ValueType vt, HtmlElement clickable, Map<String, Object> params, CrawlConf cconf, DomNode page){
		String fileName = "";
		if (vt.getToDirectory()!=null){
			fileName = TaskUtil.evalStringValue(vt.getToEntryType(), vt.getToDirectory(), params);
			logger.info(String.format("fileName is: %s", fileName));
		}
		try{
			InputStream is = null;
			try {
				is = clickable.click().getWebResponse().getContentAsStream();
				if (clickable instanceof HtmlAnchor){
					String url = anchorToFullUrl((HtmlAnchor)clickable, (HtmlPage)page);
					if (FilenameUtils.indexOfExtension(fileName)==-1){//the to directory generated does not contains a file
						fileName = fileName + "/" + FilenameUtils.getName(url);
					}
				}
				CrawlUtil.downloadPage(cconf, is, fileName);
			}finally{
				if (is!=null){
					is.close();
				}
			}
		}catch(IOException ioe){
			logger.error("", ioe);
		}
		return fileName;
	}
	
	private static void processXpath(ValueType vt, String xpathValue, Map<String, List<? extends DomNode>> pageMap
			, CrawlConf cconf, List<List> rxpathListResultList, List<List<HtmlPage>> rpagelistList, List<HtmlPage> rpageList
			, List<String> valueExpList, Map<String, Object> params) throws InterruptedException{
		//process fromType="XPATH"
		List<? extends DomNode> pages = null;
		if (vt.getBasePage()==null){
			//if not specified, then evaluate on the current page of the map
			pages = pageMap.get(ConfKey.CURRENT_PAGE);
		}else{
			pages = pageMap.get(vt.getBasePage());
		}
		
		if (pages!=null){
			for (DomNode page : pages){
				//switch page for frame
				FrameWindow fw=null;
				DomNode orgPage = null;
				String frameId = vt.getFrameId();
				if (frameId!=null && page instanceof HtmlPage){
					orgPage = page;
					try {
						//by idx
						int idx = Integer.parseInt(frameId);
						fw = ((HtmlPage)page).getFrames().get(idx);
						page = (DomNode) fw.getEnclosedPage();
						logger.debug(String.format("get frame %s by name %s", page, frameId));
					}catch(NumberFormatException nfe){
						//by name
						fw = ((HtmlPage)page).getFrameByName(frameId);
						page = (DomNode) fw.getEnclosedPage();
						logger.debug(String.format("get frame %s by name %s", page, frameId));
					}
				}
				Object xpathResult = null;
				List xpathListResult = null;
				if (VarType.LIST == vt.getToType() || VarType.EXTERNAL_LIST == vt.getToType()){
					xpathListResult = page.getByXPath(xpathValue);
					if (xpathListResult.size()==0){
						logger.error(String.format("list from page %s at xpath %s is null.", page.getHtmlPageOrNull(), vt.getValue()));
					}else{
						List convertedList = new ArrayList();
						if (vt.getToEntryType()==VarType.STRING){
							for (Object entry:xpathListResult){
								String finalString= getURLStringValue(entry, page);//TODO for list result, prefer url
								if (finalString==null){
									finalString = getStringValue(entry);
								}
								if (finalString!=null){
									convertedList.add(finalString);
								}
							}
						}else if (vt.getToEntryType()==VarType.INT){
							for (Object entry:xpathListResult){
								String finalString = getStringValue(entry);
								if (finalString!=null){
									convertedList.add(Integer.parseInt(finalString));
								}
							}
						}else if (vt.getToEntryType()==VarType.FILE){
							for (Object entry:xpathListResult){
								convertedList.add(processFile(vt, (HtmlElement)entry, params, cconf, page));
							}
						}else{
							logger.warn(String.format(
									"unsupported toEntryType %s for fromType xpath and toType list", vt.getToEntryType()));
							convertedList = xpathListResult;
						}
						rxpathListResultList.add(convertedList); //return list of element, no further processing
					}
				}else if (VarType.PAGELIST == vt.getToType()){
					xpathListResult = page.getByXPath(xpathValue);
					if (xpathListResult.size()==0){
						logger.error(String.format("pagelist from page %s at xpath %s is null", page.getBaseURI(), vt.getValue()));
					}else{
						List<HtmlPage> pagelist = new ArrayList<HtmlPage>();
						for (int i=0; i<xpathListResult.size(); i++){
							Object xpathRes = xpathListResult.get(i);
							BinaryBoolOp pvt = null;
							if (vt.getPageVerify().size()>0){
								//TODO: For page list type
								//no multiple pageVerify supported for 1 page
								if (vt.getPageVerify().size()==xpathListResult.size()){
									//the pageVerify specified for this ValueType is 1 to 1 mapped to the returned page list
									pvt = vt.getPageVerify().get(i);
								}else if (vt.getPageVerify().size()==1){
									//one pageVerify for all pages
									pvt = vt.getPageVerify().get(0);
								}else{
									logger.error("page verify and page number not match for vt:" + vt.toString());
								}
							}
							List<BinaryBoolOp> pvtlist = new ArrayList<BinaryBoolOp>();
							if (pvt!=null)
								pvtlist.add(pvt);
							HtmlPage pageGet = getPage(xpathRes, page, vt, pvtlist, cconf, params);
							if (pageGet!=null){
								if (fw!=null){
									fw.setEnclosedPage(pageGet);
									pagelist.add((HtmlPage) orgPage.cloneNode(true));
								}else{
									pagelist.add(pageGet.cloneNode(true));
								}
							}else{
								logger.error(String.format("can't get page for xpath: %s", xpathRes));
								pagelist.add(null);
							}
						}
						logger.debug("pagelist got is:" + pagelist);
						rpagelistList.add(pagelist); //return list of pages, no further processing
					}
				}else{//none list type
					String valueExp = null;
					xpathResult = page.getFirstByXPath(xpathValue);
					if (xpathResult!=null){
						if (VarType.PAGE==vt.getToType()){
							HtmlPage pageGet = getPage(xpathResult, page, vt, vt.getPageVerify(), cconf, params);
							if (pageGet!=null){
								if (fw!=null){
									fw.setEnclosedPage(pageGet);
									logger.debug(String.format("enclosing page: %s", pageGet.asXml()));
									rpageList.add((HtmlPage) orgPage);
								}else{
									rpageList.add(pageGet);
								}
							}else{
								rpageList.add(null);
							}
						}else if (VarType.FILE == vt.getToType()){
							processFile(vt, (HtmlElement)xpathResult, params, cconf, page);	//no return value needed
						}else if (VarType.URL==vt.getToType()){
							valueExp = getURLStringValue(xpathResult, page);
							valueExpList.add(valueExp);
						}else if (xpathResult instanceof Double){
							valueExp = ((Double)xpathResult).intValue() + "";
							valueExpList.add(valueExp);
						}else {//get the string first
							valueExp = getStringValue(xpathResult);//TODO for single entry, prefer string
							valueExpList.add(valueExp);
						}
					}else{
						if (page instanceof HtmlPage){
							logger.error(String.format("node by xpath:%s not found on page:%s", 
									xpathValue, ((HtmlPage)page).getUrl().toExternalForm()));
							
						}else{
							logger.error(String.format("node by xpath:%s not found on page: %s", 
									xpathValue, page.asXml()));
						}
						valueExpList.add(null);
					}
				}
			}
		}else{
			logger.error(String.format("pageMap %s contains null pagelist for key:%s", pageMap, vt.getBasePage()));
		}
	}
	
	/**
	 * @pageMap contains key and its corresponding page list, can be a single page
	 * @vt valueType specifies from type, to type and value expression to evaluate
	 * @return either the list of evaluated values (for the base page refers to page list) 
	 * or a single value (base page refers to a single page)
	 */
	public static Object eval(Map<String, List<? extends DomNode>> pageMap, ValueType vt, CrawlConf cconf, 
			Map<String, Object> params)  throws InterruptedException{
		//the list result to return
		List<List> rxpathListResultList = new ArrayList<List>();
		List<List<HtmlPage>> rpagelistList = new ArrayList<List<HtmlPage>>();
		List<HtmlPage> rpageList = new ArrayList<HtmlPage>();
		List<String> valueExpList = new ArrayList<String>();
		
		List<Object> valueList = new ArrayList<Object>();
		
		//get from value
		VarType fromType = vt.getFromType();
		if (fromType==null){
			if (vt.getValue().contains("/"))
				fromType=VarType.XPATH;
			else
				fromType=VarType.STRING;
		}
		
		if (ScopeType.PARAM == vt.getFromScope() || ScopeType.ATTRIBUTE==vt.getFromScope()){
			if (params.containsKey(vt.getValue())){
				valueExpList.add(params.get(vt.getValue()).toString());
			}else{
				logger.error(String.format("params does not have: %s", vt.getValue()));
			}
		}else{//expression without variables [called const]
			if (fromType == VarType.STRING){
				//string const
				valueExpList.add(vt.getValue());
			}else if (VarType.XPATH == fromType){
				//xpath may contains parameters needs to be replaced
				String xpathValue =  vt.getValue();
				xpathValue = StringUtil.fillParams(xpathValue, params, ConfKey.PARAM_PRE, ConfKey.PARAM_POST);
				processXpath(vt, xpathValue, pageMap, cconf, rxpathListResultList, rpagelistList, rpageList, valueExpList, params);
			}else if (VarType.EXPRESSION == fromType){
				String ret = (String) ScriptEngineUtil.eval(vt.getValue(), VarType.STRING, params);
				if (ret!=null && ret.contains("/")){
					processXpath(vt, ret, pageMap, cconf, rxpathListResultList, rpagelistList, rpageList, valueExpList, params);
				}else{
					valueExpList.add(ret);
				}
			}else if (VarType.URL == fromType){
				List<? extends DomNode> currentPages = pageMap.get(ConfKey.CURRENT_PAGE);
				HtmlPage currentPage = (HtmlPage) currentPages.get(0);
				HtmlPageResult pageResult = HtmlUnitUtil.clickNextPageWithRetryValidate(currentPage.getWebClient(), 
							new NextPage(vt.getValue()), new VerifyPageByBoolOp(vt.getPageVerify(), cconf), params, null, cconf);
				if (pageResult.getErrorCode()==HtmlPageResult.SUCCSS){
					currentPage = pageResult.getPage();
					rpageList.add(currentPage);
				}else{
					logger.error(String.format("get page %s failed.", vt.getValue()));
					rpageList.add(null);
				}		
			}else{
				logger.error(String.format("unsupported fromType: %s", fromType));
			}
		}
		
		for (String valueExp1:valueExpList){
			Object value = null;
			if (valueExp1!=null){
				value = TaskUtil.getValue(vt, valueExp1);
			}else{
				value = null;
			}
			valueList.add(value);
		}
		
		List<Object> retList = new ArrayList<Object>();
		retList.addAll(rxpathListResultList);//list of HtmlElement
		retList.addAll(rpagelistList);//list of page list
		retList.addAll(rpageList);//list of page
		retList.addAll(valueList);//list of other object (int, string, date, double, etc)
		if (retList.size()==1){
			return retList.get(0);
		}else{
			return retList;
		}
	}
	
	public static void setInitAttributes(List<AttributeType> attrs, Map<String, Object> paramMap, 
			Map<String, Object> inParams) 
			throws InterruptedException {
		for (int i=0; i<attrs.size(); i++){
			AttributeType nvt = attrs.get(i);
			ValueType vt = nvt.getValue();
			if (vt.getFromScope()==ScopeType.PARAM){
				if (inParams.containsKey(vt.getValue())){
					paramMap.put(nvt.getName(), inParams.get(vt.getValue()));
				}else{
					logger.error(String.format("input params does not have: %s", nvt.getName()));
				}
			}else if (vt.getFromScope() == ScopeType.CONST){
				paramMap.put(nvt.getName(), TaskUtil.getValue(vt, vt.getValue()));
			}else{
				paramMap.put(nvt.getName(), null);
			}
		}
	}
	
	public static void setUserAttributes(DomNode page, List<AttributeType> attrs, 
			Map<String, Object> paramMap, CrawlConf cconf, Map<String, Object> inParams) throws InterruptedException {
		Map<String, List<? extends DomNode>> pageMap = new HashMap<String, List<? extends DomNode>>();
		List<DomNode> currentPages = new ArrayList<DomNode>();
		currentPages.add(page);
		pageMap.put(ConfKey.CURRENT_PAGE, currentPages);
		setUserAttributes(pageMap, attrs, paramMap, cconf, inParams, false);
	}
	
	public static boolean setUserAttributes(Map<String, List<? extends DomNode>> pages, List<AttributeType> attrs, 
			Map<String, Object> paramMap, CrawlConf cconf, Map<String, Object> inParams, boolean tryPattern) throws InterruptedException {
		return setUserAttributes(pages, attrs, paramMap, cconf, inParams, tryPattern, false);
	}
	//TODO currently only 1 external list type var with tryPattern supported
	/**
	 * @param pages
	 * @param attrs
	 * @param paramMap: inout, can not be null
	 * @param cconf
	 * @param inParams: in, can be null
	 * @param tryPattern
	 * @return
	 * @throws InterruptedException
	 */
	public static boolean setUserAttributes(Map<String, List<? extends DomNode>> pages, List<AttributeType> attrs, 
			Map<String, Object> paramMap, CrawlConf cconf, Map<String, Object> inParams, boolean tryPattern, boolean firstPage) throws InterruptedException {
		boolean externalistFinished=false;
		for (int i=0; i<attrs.size(); i++){
			AttributeType nvt = attrs.get(i);
			ValueType vt = nvt.getValue();
			if (i==0){
				if (inParams!=null)
					paramMap.putAll(inParams);
			}
			Object val = eval(pages, vt, cconf, paramMap);
			if (vt.getToType()==VarType.EXTERNAL_LIST){
				List listvar = (List)val;
				if (vt.isFirstPageOnly() && !firstPage){//for first page only external list variable only record the first page value
					listvar.clear();
				}
				Object newFirstEntry = null;
				if (listvar.size()>0){
					newFirstEntry = listvar.get(0);
				}
				boolean doTryPattern=false;
				String patternVarName=nvt.getName() + "_pattern";
				if (newFirstEntry!=null && newFirstEntry instanceof String && tryPattern){
					doTryPattern=true;
				}
				if (paramMap.containsKey(nvt.getName())){
					List elistvarorg = (List) paramMap.get(nvt.getName());
					if (elistvarorg!=null){
						Object last = elistvarorg.get(elistvarorg.size()-1);
						Object first = elistvarorg.get(0);
						if (vt.isCirclicEntry()){//circlic
							if (newFirstEntry!=null){
								if (newFirstEntry.equals(last)||newFirstEntry.equals(first)){
									logger.info(String.format("this entry %s equals last %s or first %s", newFirstEntry, last, first));
									externalistFinished=true;
								}
							}
						}
						if (doTryPattern){
							PatternIO pio = (PatternIO) paramMap.get(patternVarName);
							if (pio==null){
								pio = new PatternIO();
								paramMap.put(patternVarName, pio);
							}
							List elistvar = new ArrayList();
							for (int j=0; j<listvar.size(); j++){
								elistvar.clear();
								elistvar.addAll(elistvarorg);
								elistvar.addAll(listvar.subList(0, j+1));
								PatternUtil.usePattern(pio, elistvar);
								if (pio.isFinished()){
									externalistFinished=true;
									break;
								}
							}
							paramMap.put(nvt.getName(), elistvar);
						}else{
							elistvarorg.addAll(listvar);
							paramMap.put(nvt.getName(), elistvarorg);
						}
					}else{
						paramMap.put(nvt.getName(), listvar);
					}
				}else{
					if (doTryPattern){
						//create the pattern attribute for this external list as well
						PatternIO pio = new PatternIO();
						paramMap.put(patternVarName, pio);
						List elistvar = new ArrayList();
						for (int j=0; j<listvar.size(); j++){
							elistvar = listvar.subList(0, j+1);
							PatternUtil.usePattern(pio, elistvar);
							if (pio.isFinished()){
								externalistFinished=true;
								break;
							}
						}
						paramMap.put(nvt.getName(), elistvar);
					}else{
						paramMap.put(nvt.getName(), listvar);
					}
				}
			}else{
				if (val instanceof List){
					List vallist = (List)val;
					if (vallist.size()>0){
						Object firstObj = vallist.get(0);
						if (firstObj instanceof HtmlPage){
							//add the returned page list to pageMap
							pages.put(nvt.getName(), vallist);
						}
						paramMap.put(nvt.getName(), vallist);
					}else{
						paramMap.put(nvt.getName(), vallist);
					}
				}else if (val instanceof HtmlPage){
					//add the returned page to pageMap
					List<HtmlPage> list = new ArrayList<HtmlPage>();
					list.add((HtmlPage)val);
					pages.put(nvt.getName(), list);
				}else{
					//add the new tuple to paramMap
					paramMap.put(nvt.getName(), val);
				}
			}
		}
		return externalistFinished;
	}
}
