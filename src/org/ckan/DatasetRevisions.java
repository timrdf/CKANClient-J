package org.ckan;

import java.util.List;

/**
 * Thanks to http://www.javacreed.com/simple-gson-example/
 */
public class DatasetRevisions {
	
	/**
	 * To suit gson
	 */
    public class Response {
        public boolean success;
        public List<DatasetRevision> result;
    }
    
    /**
     * To suit gson
     */
	public DatasetRevisions(){}
}