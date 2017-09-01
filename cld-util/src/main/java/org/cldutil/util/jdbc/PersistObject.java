package org.cldutil.util.jdbc;

import java.io.Serializable;

public interface PersistObject {
	
	public Serializable getId();
	
	public void copy(PersistObject po);

}
