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
   * 
   

crawl task instance:  
org.cldutil.datacrawl.task.BrowseProductTaskConf extends Task  

