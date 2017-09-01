package org.cldutil.datastore.api;

import org.cldutil.util.entity.CrawledItem;

public interface DataStoreManager {
	
	/****************
	 * CrawledItem operations
	 */
	/**
	 * @return the latest version
	 */
	public CrawledItem getCrawledItem(String id, String storeId, Class<? extends CrawledItem> crawledItemClazz);
	
	/**
	 * if content different, will add a new version
	 * @param ci
	 * @param oldCi
	 * @return true, new version added
	 */
	public boolean addUpdateCrawledItem(CrawledItem ci, CrawledItem oldCi);	
}
