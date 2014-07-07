package org.ckan;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.log4j.Logger;

/**
 * Connection holds the connection details for this session
 *
 * @author      Ross Jones <ross.jones@okfn.org>
 * @version     1.7
 * @since       2012-05-01
 */
public final class Connection {

	private static final Logger log = Logger.getLogger(Connection.class);
	
    private String m_host;
    private int    m_port;
    private String _apikey = null;

    public Connection() {
        this("http://datahub.io", 80);
    }

    public Connection( String host ) {
        this( host, 80 );
    }

    public Connection( String host, int port ) {

       // Hack, since this.Post() does not follow redirects.
       // http://stackoverflow.com/questions/7546849/httpclient-redirect-for-newbies
       // http://stackoverflow.com/questions/5169468/handling-httpclient-redirects
       // http://hc.apache.org/httpcomponents-client-ga/httpclient/apidocs/org/apache/http/client/RedirectStrategy.html
       // http://hc.apache.org/httpcomponents-client-ga/httpclient/apidocs/index.html?overview-summary.html
       if( "http://thedatahub.org".equals(host) ) {
          host = "http://datahub.io";
       }

       this.m_host = host;
       this.m_port = port;

       try {
          URL u = new URL( this.m_host + ":" + this.m_port + "/api");
       } catch ( MalformedURLException mue ) {
          log.error("malformed URL");
          log.error(mue);
       }
    }

    public void setApiKey( String key ) {
        this._apikey = key;
    }


    /**
     * Makes a POST request
     *
     * Submits a POST HTTP request to the CKAN instance configured within
     * the constructor, returning the entire contents of the response.
     *
     * @param  path - The URL path to make the POST request to
     * @param  data - The data to be posted to the URL
     * @returns The String contents of the response.
     * 
     * @throws A CKANException if the request fails
     */
    protected String Post(String path, String data) throws CKANException {

    	URL url = null;

    	try {
    		url = new URL( this.m_host + ":" + this.m_port + path);
    		
        	log.warn("POSTing to " + url.toString());
        	log.warn("POSTing " + data);
    	}catch ( MalformedURLException mue ) {
    		log.error("malformed URL");
    		System.err.println(mue);
    		return null;
    	}

    	String body = "";

    	HttpClient httpclient = new DefaultHttpClient();
    	// http://hc.apache.org/httpcomponents-core-ga/httpcore/apidocs/org/apache/http/params/CoreConnectionPNames.html#SO_TIMEOUT
    	int MINUTE = 60000;
    	httpclient.getParams().setParameter(HttpConnectionParams.CONNECTION_TIMEOUT, 1 * MINUTE);
    	httpclient.getParams().setParameter(HttpConnectionParams.SO_TIMEOUT,         3 * MINUTE);
    	
    	/*
    	 * Set an HTTP proxy if it is specified in system properties.
    	 * 
    	 * http://docs.oracle.com/javase/6/docs/technotes/guides/net/proxies.html
    	 * http://hc.apache.org/httpcomponents-client-ga/httpclient/examples/org/apache/http/examples/client/ClientExecuteProxy.java
    	 */
    	if( isSet(System.getProperty("http.proxyHost")) ) {
	    	log.warn("http.proxyHost = " + System.getProperty("http.proxyHost") );
	    	log.warn("http.proxyPort = " + System.getProperty("http.proxyPort"));
    		int port = 80;
    		if( isSet(System.getProperty("http.proxyPort")) ) {
    			port = Integer.parseInt(System.getProperty("http.proxyPort"));
    		}
    		HttpHost proxy = new HttpHost(System.getProperty("http.proxyHost"), port, "http");
    		httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
    	}
    	
    	try {
    		HttpPost postRequest = new HttpPost(url.toString());
    		postRequest.setHeader( "X-CKAN-API-Key", this._apikey );

    		StringEntity input = new StringEntity(data);
    		input.setContentType("application/json");
    		postRequest.setEntity(input);

    		HttpResponse response = httpclient.execute(postRequest);
    		int statusCode = response.getStatusLine().getStatusCode();
    		log.warn("POST response code " + statusCode);

    		BufferedReader br = new BufferedReader(
    				new InputStreamReader((response.getEntity().getContent())));

    		String line = "";
    		while ((line = br.readLine()) != null) {
    			body += line;
    		}
    	} catch( IOException ioe ) {
    		System.out.println( ioe );
    	} finally {
    		httpclient.getConnectionManager().shutdown();
    	}

    	//System.out.println("body: " + body);
    	return body;
    }
    


    private static boolean isSet(String string) {
    	return string != null && string.length() > 0;
    }
}