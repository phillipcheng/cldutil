package org.cldutil.datastore.impl;

import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cldutil.datastore.api.DataStoreManager;
import org.cldutil.util.entity.CrawledItem;

public class HibernateDataStoreManagerImpl implements DataStoreManager {

	private static Logger logger = LogManager.getLogger(DataStoreManager.class);

	private SessionFactory hsf;

	public SessionFactory getHibernateSF(){
		return hsf;
	}
	public void setHibernateSF(SessionFactory sf){
		hsf = sf;
	}
	
	public HibernateDataStoreManagerImpl() {
	}
	
	/******************************
	 * CrawledItem Operations
	 */
	private CrawledItem getCrawledItem(String id, String storeId, Class<? extends CrawledItem> crawledItemClazz, Session session) {
		try {
			List<CrawledItem> results = session.createCriteria(crawledItemClazz)
				.add(Restrictions.eq("id.id", id))
				.add(Restrictions.eq("id.storeId", storeId))
				.addOrder(Property.forName("id.createTime").desc())
				.setMaxResults(1)
				.list();
			CrawledItem p = null;
			if (results != null && !results.isEmpty()) {
				p = (CrawledItem) results.get(0);
				p.fromParamData();
			}
			return p;
		} catch (Exception e) {
			logger.error("", e);
			return null;
		}
	}
	public CrawledItem getCrawledItem(String id, String storeId, Class<? extends CrawledItem> crawledItemClazz) {
		Session session = hsf.openSession();
		try {
			return getCrawledItem(id, storeId, crawledItemClazz, session);
		}finally{
			session.close();
		}
	}

	@Override
	public boolean addUpdateCrawledItem(CrawledItem ci, CrawledItem oldCi) {
		Session session = hsf.openSession();
		Transaction tx = null;
		try {
			if (oldCi==null){
				oldCi = getCrawledItem(ci.getId().getId(), ci.getId().getStoreId(), CrawledItem.class, session);
				if (ci.contentEquals(oldCi))
					return false;
			}
			if (oldCi!=null){//copy ci to oldCi, if update
				oldCi.copy(ci);
			}
			tx = session.beginTransaction();
			if (oldCi!=null){
				oldCi.toParamData();
				session.update(oldCi);
			}else{
				ci.toParamData();
				session.save(ci);
			}
			tx.commit();
			return true;
		} catch (Exception e) {
			try{
				if (tx!=null)
					tx.rollback();
			}catch(Throwable t1){
				logger.error("throwable caught while rollback transaction for:" + ci, t1);
			}
			logger.error("", e);
			return false;
		} finally {
			session.close();
		}
	}
}
