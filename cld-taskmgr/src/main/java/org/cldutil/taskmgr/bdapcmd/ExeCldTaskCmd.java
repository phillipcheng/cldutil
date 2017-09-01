package org.cldutil.taskmgr.bdapcmd;

import java.util.List;
//log4j2
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import etl.engine.ETLCmd;
import etl.engine.types.ProcessMode;
import scala.Tuple2;

public class ExeCldTaskCmd extends ETLCmd {
	private static final long serialVersionUID = 1L;

	public static final Logger logger = LogManager.getLogger(ExeCldTaskCmd.class);

	public ExeCldTaskCmd(){
		super();
	}
	
	public ExeCldTaskCmd(String wfName, String wfid, String staticCfg, String defaultFs, String[] otherArgs){
		init(wfName, wfid, staticCfg, null, defaultFs, otherArgs, ProcessMode.Single);
	}
	
	public ExeCldTaskCmd(String wfName, String wfid, String staticCfg, String defaultFs, String[] otherArgs, ProcessMode pm){
		init(wfName, wfid, staticCfg, null, defaultFs, otherArgs, pm);
	}
	
	@Override
	public void init(String wfName, String wfid, String staticCfg, String prefix, String defaultFs, String[] otherArgs, ProcessMode pm){
		super.init(wfName, wfid, staticCfg, prefix, defaultFs, otherArgs, pm);
		
	}
	
	@Override
	/**
	 * @param tableName: tableName is the input fileName, so can be ignored
	 */
	public List<Tuple2<String, String>> flatMapToPair(String tableName, String value, Mapper<LongWritable, Text, Text, Text>.Context context) throws Exception{
		return null;
	}

	@Override
	public boolean hasReduce(){
		return false;
	}
}
