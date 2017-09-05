# web crawl engine

## crawl task definition: 
xsd complex type: BrowseDetailType  
java definition: org.cldutil.xml.taskdef.BrowseDetailType  

name:type:defaultValue:meaning  
1. tryPattern: boolean:   if a totalPage is specified, this is set to true by default, otherwise false  
&nbsp;&nbsp;system will try to guess the pattern of the url for each page, when the url is an expression based on current page number.  
&nbsp;&nbsp;if succeed, it will be much faster then clicking the next button till the end of the page

2. baseBrowseTask:(BrowseTaskType)  
   * taskName: 1. API explicit invoke, 2. xml definition of the nextTask, 3. logging
   * isStart: if marked as true, when invoke the browse tasks by xml definition file, only those will be invoked, 
   those not marked true can only be invoked via API or via nextTask definition
   * enableJS: boolean: whether to enable js engine when browsing it, it slows down a lot when js is enabled, 
   but lots of pages are using it.
   * dsm:hbase,hdfs(using csvtransform configured to generate csv),hibernate:this will override the one configured in the properties for this task. 
   will use this datastore to store/update/retrieve the crawled item.
   * sampleUrl: list of sample start urls (string) helping the studio to visualize configurating this crawl task
   * param: list of parameters for this task, when invoking this task passed in via API or configuration. the type of param can be expression, which uses previous parameters to calculate.
     * direction: in/out, if out, when do hdfs/csv output, this will be part of the output to csv, together with attributes.
   * startUrl: the starting page for crawl, this can be an expression using the param above
     * can be a string typed url
     * can be Page typed Object passed in via param from previous task invocation
```xml
	<PrdTask>
		<baseBrowseTask taskName="lvl1" isStart="true" enableJS="false">
			<startUrl value="xxxxxx"/>
			<userAttribute name="lvl2urls">
				<value value="//xxx/a" fromType="xpath" toType="list"/>
			</userAttribute>
			<nextTask>
				<invokeTask toCallTaskName="lvl2">
					<param paramName="lvl2urls"/>
				</invokeTask>
			</nextTask>
		</baseBrowseTask>
	</PrdTask>
	<PrdTask>
		<baseBrowseTask taskName="lvl2" isStart="true" enableJS="false">
			<param name="lvl2urls" type="list"/>
			<startUrl value="lvl2urls" fromType="expression"/>
		</baseBrowseTask>
	</PrdTask>
```
   * cachePage: define the hdfs file name to save the starting page
   * userAttribute: 
     * get type attribute: fetch the values from the page and set to attributes stored in CrawledItem (will be returned)
       * name: the attribute on the CrawledItem to store the fetched value
       * value: defines how to get the value for this attribute from the page
         * 
     * set type attribute: for input / select typed element on the page, we need to set values
       * name: by default the nameType is XPATH, and name indicates the xpath of the input element of the page 
       * value: the value to set in the input element
   * pageVerify: defined the boolean verification expression for the page, if not met, page will be refetched until met or timed out.
   * csvtransform: handler defined to tranform the crawledItem to list of csv lines and output to hdfs
   * filter: boolean typed js functionused together with csvtransform, to filter csv line by line
   * nextTask
     * condition: boolean typed conditional expression, if evaluated true, next task will be invoked
     * invokeTask: the task name to be called, the params passed to the next task
   


## crawl task execution:  
### org.cldutil.datacrawl.task.BrowseProductTaskConf extends Task

browseProduct:  
1. evaluate all the param (since some are expressions) defined on this taskDef  
2. evaluate the starturl using the param (input param), if starturl is defined using a list-typed param, the result is a list of starturl, a separate task instance will be generated for each.  
3. if cachePage is defined, save the whole page as files defined.
4. determined the product id before opening the page, if the starturl is a string, then the starturl is the product id, if the starturl is a Page Object passed via param, set the product id to the "id" user attributes defined.
5. check the crawled item database (if there is one defined [via dsm on the task]) to see whether there is an old product of the same id already completly crawled, if not we will open the page and start the crawling. Then we will go into the addProduct method within ProductAnalyze, to do the crawling, attribute setting, csv transformation, and output file generation.
6. 

### org.cldutil.datacrawl.mgr.ProductAnalyze
addProduct:


