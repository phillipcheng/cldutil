package org.cldutil.datastore.impl;

import java.util.Date;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cldutil.datastore.api.DataStoreManager;
import org.cldutil.util.entity.CrawledItem;
import org.cldutil.util.entity.CrawledItemId;

//for hbase, the createTime of CrawledItemId is not used.
public class HbaseDataStoreManagerImpl implements DataStoreManager {

	public static final String rowkey_sep = "|";
	public static final String CRAWLEDITEM_TABLE_NAME="crawledItem";
	public static final String CRAWLEDITEM_CF="cf";
	public static final byte[] CRAWLEDITEM_CF_BYTES=CRAWLEDITEM_CF.getBytes();
	public static final String CRAWLEDITEM_DATA="data";
	public static final byte[] CRAWLEDITEM_DATA_BYTES=CRAWLEDITEM_DATA.getBytes();
	
	
	private static Logger logger = LogManager.getLogger(HbaseDataStoreManagerImpl.class);

	private Configuration hbaseConf;
	
	public HbaseDataStoreManagerImpl(Configuration hadoopConf) {
		hbaseConf = HBaseConfiguration.addHbaseResources(hadoopConf);
	}

	public static String getRowKey(CrawledItemId cid){
		return cid.getStoreId() + rowkey_sep + cid.getId();
	}
	
	//storeId|id
	public static String[] fromRowKey(String rowKey){
		int idx = rowKey.indexOf(rowkey_sep);
		if (idx>=0){
			String[] res = new String[2];
			res[0]=rowKey.substring(0,idx);
			res[1]=rowKey.substring(idx+1, rowKey.length());
			return res;
		}else{
			logger.error(String.format("rowkey_sep not found in the rowkey: %s", rowKey));
			return null;
		}
	}
	
	public static String getRowKey(String id, String storeId){
		return storeId + rowkey_sep + id;
	}
	
	private static CrawledItem getCrawledItemFromResult(String id, String storeId, Result rs){
		if (!rs.isEmpty()){
        	Cell cell = rs.getColumnLatestCell(CRAWLEDITEM_CF_BYTES, CRAWLEDITEM_DATA_BYTES);
        	String json = new String(CellUtil.cloneValue(cell));
        	CrawledItem ci = CrawledItem.fromJson(json);
        	CrawledItemId ciid = new CrawledItemId(id, storeId, new Date(cell.getTimestamp()));
        	ci.setId(ciid);
            return ci;
        }else{
        	return null;
        }
	}
	
	public static CrawledItem getCrawledItemFromResult(String rowKey, Result rs){
		String[] keys = fromRowKey(rowKey);
		if (keys!=null){
			return getCrawledItemFromResult(keys[1], keys[0], rs);
		}else{
			return null;
		}
	}
	
	@Override
	public CrawledItem getCrawledItem(String id, String storeId,
			Class<? extends CrawledItem> crawledItemClazz) {
		HTable table = null;
		try{
			table = new HTable(hbaseConf, storeId);
			String rowKey = getRowKey(id, storeId);
			logger.debug("getCrawledItem rowkey:" + rowKey);
	        Get get = new Get(rowKey.getBytes());
	        Result rs = table.get(get);
	        return getCrawledItemFromResult(id, storeId, rs);
		}catch(Exception e){
			logger.error("", e);
			return null;
		}finally{
			if (table!=null){
				try{
					table.close();
				}catch(Exception e){
					logger.error("error close table.", e);
				}
			}
		}
	}

	@Override
	public boolean addUpdateCrawledItem(CrawledItem ci, CrawledItem oldCi) {
		if (!ci.contentEquals(oldCi)){
			HTable table = null;
			try {
				table = new HTable(hbaseConf, ci.getId().getStoreId());
				String rowKey = getRowKey(ci.getId());
				logger.debug("addUpdateCrawledItem rowkey:" + rowKey);
	            Put put = new Put(Bytes.toBytes(rowKey));
	            put.addColumn(Bytes.toBytes(CRAWLEDITEM_CF), Bytes.toBytes(CRAWLEDITEM_DATA), ci.getId().getCreateTime().getTime(),
	            		Bytes.toBytes(ci.toJson()));
	            table.put(put);
	            return true;
	        }catch (Exception e){
	        	logger.error("", e);
	        	return false;
	        }finally{
	        	if (table!=null){
	        		try{
	        			table.close();
	        		}catch(Exception e){
	        			logger.error("error at close table.", e);
	        		}
	        	}
	        }
		}else{
			return false;
		}
	}
	
}
