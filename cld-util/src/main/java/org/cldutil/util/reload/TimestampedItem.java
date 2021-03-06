package org.cldutil.util.reload;

import java.util.Date;

import org.cldutil.util.DateTimeUtil;

public class TimestampedItem<T> {
	
	boolean updated;
	long timestamp;
	T content;
	
	public TimestampedItem(long t, T c){
		this.timestamp = t;
		this.content = c;
	}

	public void update(long t, T c){
		this.timestamp = t;
		this.content = c;
	}
	
	public long getTimestamp(){
		return timestamp;
	}
	
	public T getContent(){
		return content;
	}
	
	public boolean isUpdated() {
		return updated;
	}

	public void setUpdated(boolean updated) {
		this.updated = updated;
	}

	public String toString(){
		return "timestamp:" + DateTimeUtil.sddf.format(new Date(timestamp)) + "\n" + 
				"isUpdated:" + updated + "\n" + 
				"content:" + content.toString() + "\n"; 
	}
	
	

}
