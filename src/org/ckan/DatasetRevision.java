package org.ckan;

/**
 * 
 */
public class DatasetRevision {
	
	/**
	 * To suit gson
	 */
	public DatasetRevision(){}
	
	private String id;
	private String timestamp;
	private String message;
	private String author;
	//private String approved_timestamp;
	
	public String getID() {
		return id;
	}
	
	public String getTimestamp() {
		return timestamp;
	}
	
	public String getMessage() {
		return message;
	}
	
	public String getAuthor() {
		return author;
	}
	
	/**
	 * 
	 * @return the author of the edit as a URI.
	 */
	public String getAuthorURI() {
		return author;
	}
	
	public String toString() {
		return author + " at " + timestamp;
	}
}