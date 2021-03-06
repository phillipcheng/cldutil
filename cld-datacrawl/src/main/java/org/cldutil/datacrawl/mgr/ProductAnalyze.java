package org.cldutil.datacrawl.mgr;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.MapContext;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import org.cldutil.datacrawl.CrawlConf;
import org.cldutil.datacrawl.NextPage;
import org.cldutil.datacrawl.util.HtmlPageResult;
import org.cldutil.datacrawl.util.HtmlUnitUtil;
import org.cldutil.datacrawl.util.VerifyPage;
import org.cldutil.datacrawl.util.VerifyPageByBoolOp;
import org.cldutil.datacrawl.util.VerifyPageByBoolOpXPath;
import org.cldutil.datacrawl.util.VerifyPageByXPath;
import org.cldutil.datastore.api.DataStoreManager;
import org.cldutil.datastore.impl.HdfsDataStoreManagerImpl;
import org.cldutil.etl.fci.AbstractCrawlItemToCSV;
import org.cldutil.pagea.general.ProductAnalyzeUtil;
import org.cldutil.taskmgr.entity.Task;
import org.cldutil.taskmgr.hadoop.HadoopTaskLauncher;
import org.cldutil.util.ScriptEngineUtil;
import org.cldutil.util.entity.CrawledItem;
import org.cldutil.util.entity.Product;
import org.cldutil.xml.mytaskdef.ParsedBrowsePrd;
import org.cldutil.xml.mytaskdef.XPathType;
import org.cldutil.xml.taskdef.BinaryBoolOp;
import org.cldutil.xml.taskdef.BrowseDetailType;
import org.cldutil.xml.taskdef.BrowseTaskType;
import org.cldutil.xml.taskdef.CsvOutputType;
import org.cldutil.xml.taskdef.CsvTransformType;
import org.cldutil.xml.taskdef.FilterType;
import org.cldutil.xml.taskdef.TransformOp;
import org.cldutil.xml.taskdef.VarType;

public class ProductAnalyze{
	
	private static Logger logger =  LogManager.getLogger(ProductAnalyze.class);
	//used in the java script provided in csvtransform/ops
	public static final String VAR_CSV_NAME= "arr"; //name of the 1 dimension csv array passed to the filter
	public static final String VAR_RET_NAME= "ret"; //name of the array return object
	//param id to be used for csv output byId
	public static final String PARAM_ID="id";//name
	
	private VerifyPage VPXP; //
	
	public String toString(){
		return System.identityHashCode(this) + "\n";
	}
	
	public ProductAnalyze(){
	}
	
	public static void writeCsvOut(CsvTransformType csvtrans, Map<String, BufferedWriter> hdfsByIdOutputMap, 
			MapContext<Object, Text, Text, Text> context, MultipleOutputs<Text, Text> mos, 
			CrawledItem ci, CrawlConf cconf, Task task){
		try{
			CsvOutputType cot = csvtrans.getOutputType();
			FileSystem fs = FileSystem.get(context.getConfiguration());
			String outputDirPrefix=null;
			
			if (cot == CsvOutputType.BY_ID){//all ci has the same id for multiple ci output
				Map<String, Object> paramMap = new HashMap<String, Object>();
				paramMap.putAll(task.getParamMap());
				paramMap.putAll(ci.getParamMap());
				String id = (String) ci.getParam(PARAM_ID);
				if (id==null) id=ci.getId().getId();
				outputDirPrefix = cconf.getHadoopCrawledItemFolder() + "/" +
						task.getOutputDir(paramMap, null, cconf) + "/" + id;
			}
			String[][] csv = ci.getCsvValue();
			if (csv!=null){
				int total=csv.length;
				logger.info(String.format("going to write out %d csvs", total));
				for (int i=0; i<total; i++){
					String[] v;
					if (!csvtrans.isReverse()){
						v = csv[i];
					}else{
						v = csv[total-1-i];
					}
					if (v.length==2){//key, value
						if (v[1]!=null && !"".equals(v[1])){
							String csvkey = v[0];
							String csvvalue = v[1];
							if (cot != CsvOutputType.BY_ID){
								if (AbstractCrawlItemToCSV.KEY_VALUE_UNDEFINED.equals(csvkey)){
									context.write(new Text(csvvalue), new Text(""));
								}else{
									context.write(new Text(csvkey), new Text(csvvalue));
								}
							}else{
								//
								String outputFile = outputDirPrefix;
								BufferedWriter br = null;
								if (hdfsByIdOutputMap.containsKey(outputFile)){
									br = hdfsByIdOutputMap.get(outputFile);
								}else{
									br = new BufferedWriter(new OutputStreamWriter(fs.create(new Path(outputFile),true)));
									hdfsByIdOutputMap.put(outputFile, br);
								}
								if (AbstractCrawlItemToCSV.KEY_VALUE_UNDEFINED.equals(csvkey)){
									br.write(csvvalue + "\n");
								}else{
									br.write(csvkey + "," + csvvalue + "\n");
								}
							}
						}
					}else if (v.length==3){//key, value, output_file_prefix
						String outkey=v[0];
						String outvalue=v[1];
						String outfilePrefix=v[2];
						if (cot == CsvOutputType.BY_JOB_MULTI){
							mos.write(HadoopTaskLauncher.NAMED_OUTPUT_TXT, 
									new Text(outkey), new Text(outvalue), outfilePrefix);
						}else if (cot == CsvOutputType.BY_JOB_SINGLE){
							context.write(new Text(outkey), new Text(outvalue));
						}else if (cot == CsvOutputType.BY_ID){
							String outputFile = outputDirPrefix + "_" + outfilePrefix;
							BufferedWriter br = null;
							if (hdfsByIdOutputMap.containsKey(outputFile)){
								br = hdfsByIdOutputMap.get(outputFile);
							}else{
								br = new BufferedWriter(new OutputStreamWriter(fs.create(new Path(outputFile),true)));
								hdfsByIdOutputMap.put(outputFile, br);
							}
							if (AbstractCrawlItemToCSV.KEY_VALUE_UNDEFINED.equals(outkey)){
								br.write(outvalue + "\n");
							}else{
								br.write(outkey + "," + outvalue + "\n");
							}
						}else{
							logger.error(String.format("for key,value,output_file_prefix typed output, CsvOutpuType must be specified."));
						}
					}else{
						logger.error("wrong number of csv length: not 2 and 3 but:" + v.length);
					}
				}
			}else{
				//called from mapred, but no output specified.
			}
		}catch(Exception e){
			logger.error("", e);
		}
	}

	//return true for there are records filtered by date, so I do not need to crawl anymore, since the data is ordered by date
	public static boolean postCrawlProcess(Task taskInstance, BrowseTaskType btt, CrawledItem product){
		CsvTransformType csvTransform = btt.getCsvtransform();
		if (csvTransform!=null){
			//transform from data to array
			AbstractCrawlItemToCSV cicsv = null;
			try{
				cicsv = (AbstractCrawlItemToCSV) 
						Class.forName(csvTransform.getTransformClass()).newInstance();
			}catch(Exception e){
				logger.error(e);
			}
			String[][] csv = cicsv.getCSV(product, null);
			logger.info(String.format("csv get from crawledItem: %d.", csv.length));
			//transform operations
			if (csvTransform.getOps()!=null){
				for (TransformOp top: csvTransform.getOps()){
					for (int i=0; i<csv.length; i++){
						Map<String, Object> attributes = new HashMap<String, Object>();
						attributes.put(VAR_CSV_NAME, csv[i][1]);//pass the unsplit string
						attributes.putAll(product.getParamMap());
						attributes.putAll(taskInstance.getParamMap());
						String svarr = (String) ScriptEngineUtil.eval(top.getExpression(), VarType.STRING, attributes);//return a joined string
						csv[i][1] = svarr;
					}
				}
			}
			product.setCsvValue(csv);
			//filter the csv
			FilterType ft= btt.getFilter();
			Map<String, Object> attributes = new HashMap<String, Object>();
			attributes.putAll(product.getParamMap());
			boolean hasFiltered=false;
			if (ft!=null){
				String filterExp = ft.getFunction();
				List<String[]> passedCsv = new ArrayList<String[]>();
				for (String[] arr:csv){
					//anyway the csv[1] is the value list, i need to make it into an array
					String[] varr = arr[1].split(",");
					attributes.put(VAR_CSV_NAME, varr);
					Boolean ret = (Boolean)ScriptEngineUtil.eval(filterExp, VarType.BOOLEAN, attributes);
					if (ret!=null){
						if (ret.booleanValue()){
							passedCsv.add(arr);
						}else{
							hasFiltered=true;
						}
					}else{
						logger.error(String.format("eval exp:%s to null.", filterExp));
					}
				}
				String[][] passedCsvArray = new String[passedCsv.size()][];
				passedCsvArray = passedCsv.toArray(passedCsvArray);
				product.setCsvValue(passedCsvArray);
			}
			return (cicsv.isFiltered()||(hasFiltered&&ft.isByDate()));//filter by dateIdx or filter date exp
		}else{
			return false;
		}
	}
	
	/**
	 * 
	 * @param wc
	 * @param url
	 * @param product
	 * @param lastProduct
	 * @param task
	 * @param taskDef
	 * @param cconf
	 * @param retCsv: true, return the csv content for map to process, false save it to hdfs if dsm is hdfs
	 * @return the csv[] to return
	 * @throws InterruptedException
	 */
	public CrawledItem addProduct(WebClient wc, Object url, Product product, Product lastProduct, Task task, 
			ParsedBrowsePrd taskDef, CrawlConf cconf, boolean addToDB, 
			CsvTransformType csvtrans, Map<String, BufferedWriter> hdfsByIdOutputMap, 
			MapContext<Object, Text, Text, Text> context, MultipleOutputs<Text, Text> mos) 
			throws InterruptedException{
		logger.info(String.format("add product with start url:%s", url));
		BrowseDetailType bdt = taskDef.getBrowsePrdTaskType();
		XPathType[] xpaths = ProductAnalyzeUtil.getPageVerifyXPaths(task, taskDef);
		BinaryBoolOp[] bbops = ProductAnalyzeUtil.getPageVerifyBoolOp(task, taskDef);
		VerifyPageByBoolOp vpbbo = xpaths!=null? new VerifyPageByBoolOp(bbops, cconf):null;
		VerifyPageByXPath vpbxp = bbops!=null? new VerifyPageByXPath(xpaths):null;
		VPXP = new VerifyPageByBoolOpXPath(vpbbo, vpbxp);
		DataStoreManager dsManager = null;
		if (bdt.getBaseBrowseTask().getDsm()!=null){
			dsManager = cconf.getDsm(bdt.getBaseBrowseTask().getDsm());
		}else{
			dsManager = cconf.getDefaultDsm();
		}
		product.setRootTaskId(task.getRootTaskId());
		HtmlPageResult detailsResult = new HtmlPageResult();
		HtmlPage details = null;
		if (url instanceof String){
			product.setFullUrl((String)url);
			detailsResult = HtmlUnitUtil.clickNextPageWithRetryValidate(wc, new NextPage((String)url), VPXP, null, task.getParsedTaskDef(), cconf);	
			details  = detailsResult.getPage();
		}else{
			details = (HtmlPage) url;
			detailsResult.setErrorCode(HtmlPageResult.SUCCSS);
		}		
		
		if (detailsResult.getErrorCode() == HtmlPageResult.SUCCSS){
			//product name
			if (product.getName()==null){
				String title = ProductAnalyzeUtil.getTitle(details, task, taskDef, cconf);
				product.setName(title);
			}
			
			//call back
			ProductAnalyzeUtil.readDetails(wc, details, product, task, taskDef, cconf);
			product.getId().setCreateTime(new Date());
			//logger.debug("product got:" + product);
			boolean goNext=false;
			if (bdt.getBaseBrowseTask().getNextTask()!=null){
				BinaryBoolOp bbo = bdt.getBaseBrowseTask().getNextTask().getCondition();
				if (bbo!=null){
					goNext=BinaryBoolOpEval.eval(bbo, product.getParamMap(), details, cconf);
				}else{
					goNext=true;
				}
			}
			product.setGoNext(goNext);
			CsvTransformType csvTransform = bdt.getBaseBrowseTask().getCsvtransform();
			if (csvTransform!=null && csvTransform.getTransformClass()!=null){
				//do the transform and write out the csv and write to db if db is set
				try {
					postCrawlProcess(task, bdt.getBaseBrowseTask(), product);
					//write the output
					if (csvtrans!=null && context!=null){
						writeCsvOut(csvtrans, hdfsByIdOutputMap, context, mos, product, cconf, task);
					}
				} catch (Exception e) {
					logger.error("", e);
				}
			}else{
				if (CrawlConf.crawlDsManager_Value_Hdfs.equals(bdt.getBaseBrowseTask().getDsm())){
					product.setCsvValue(HdfsDataStoreManagerImpl.getCSV(product, bdt.getBaseBrowseTask()));
				}
			}
			logger.info(String.format("in add product, dsm is %s, add to DB is %b", bdt.getBaseBrowseTask().getDsm(), addToDB));
			if (dsManager!=null && addToDB && !CrawlConf.crawlDsManager_Value_Nothing.equals(bdt.getBaseBrowseTask().getDsm())){
				dsManager.addUpdateCrawledItem(product, lastProduct);	
			}
		}else{
			product.setCompleted(false);
		}
		
		return product;
	}
}
