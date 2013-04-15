package org.ckan;

import java.net.URL;

import java.net.MalformedURLException;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.entity.StringEntity;
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
    private int m_port;
    private String _apikey = null;

    public Connection() {
        this("http://datahub.io", 80);
    }

    public Connection( String host ) {
        this( host, 80 );
    }

    public Connection( String host, int port ) {
    	
    	// Hack, since this.Post() does not follow redirects.
    	if( "http://thedatahub.org".equals(host) ) {
    		host = "http://datahub.io";
    	}
    	
        this.m_host = host;
        this.m_port = port;

        try {
            URL u = new URL( this.m_host + ":" + this.m_port + "/api");
        } catch ( MalformedURLException mue ) {
            System.out.println(mue);
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
    	}catch ( MalformedURLException mue ) {
    		log.error("malformed URL");
    		System.err.println(mue);
    		return null;
    	}

    	log.warn("POSTing to " + url.toString());
    	log.warn("POSTing " + data);

    	String body = "";

    	HttpClient httpclient = new DefaultHttpClient();
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

    	return body;
    }
}