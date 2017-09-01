package org.cldutil.datastore.hibernate;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cldutil.util.jdbc.PersistObject;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

/**
 * @author Cheng Yi
 */
public class HibernatePersistMgr {
	private static Logger logger =  LogManager.getLogger(HibernatePersistMgr.class);
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	private static String HIBERNATE_CFG="hibernate.cfg.xml";
	protected SessionFactory hibernateSF = null;
	
	public void init(){
		Configuration cfg = new Configuration();
		cfg.configure(HIBERNATE_CFG);
		hibernateSF = cfg.buildSessionFactory();
	}
	
	public void cleanup(){
		if (hibernateSF!=null && !hibernateSF.isClosed()){
			hibernateSF.close();
		}
	}

	public PersistObject get(Class clazz, Serializable id){
		Session ses = hibernateSF.openSession();
		try {
			PersistObject obj = ses.get(clazz, id);
			return obj;
		}finally{
			if (ses!=null){
				ses.close();
			}
		}
	}
	public void addOrUpdate(PersistObject obj){
		Session ses = hibernateSF.openSession();
		Transaction tx=null;
		try {
			PersistObject old = ses.get(obj.getClass(), obj.getId());
			tx = ses.beginTransaction();
			if (old!=null){
				old.copy(obj);
				ses.save(old);
			}else{
				ses.save(obj);
			}
			tx.commit();
		}catch(Exception e){
			try{
				if (tx!=null)
					tx.rollback();
			}catch(Throwable t1){
				logger.error("throwable caught while rollback transaction", t1);
			}
			logger.error("", e);
		}
		finally{
			if (ses!=null){
				ses.close();
			}
		}
	}
}
