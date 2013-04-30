package org.ckan;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.gson.Gson;

/**
 * The primary interface to this package the Client class is responsible
 * for managing all interactions with a given connection.
 *
 * @author      Ross Jones <ross.jones@okfn.org>
 * @version     1.7
 * @since       2012-05-01
 */
public final class Client {

	private static final Logger log = Logger.getLogger(Client.class);
	
    private Connection _connection = null;

    /**
    * Constructs a new Client for making requests to a remote CKAN instance.
    *
    * @param  c A Connection object containing info on the location of the
    *         CKAN Instance.
    * @param  apikey A user's API Key sent with every request.
    */
    public Client( Connection c, String apikey ) {
        this._connection = c;
        this._connection.setApiKey(apikey);
    }

    /**
    * Loads a JSON string into a class of the specified type.
    */
    protected <T> T LoadClass( Class<T> cls, String data ) {
        Gson gson = new Gson();
        return gson.fromJson(data, cls);
    }

    /**
    * Handles error responses from CKAN
    *
    * When given a JSON string it will generate a valid CKANException
    * containing all of the error messages from the JSON.
    *
    * @param  json The JSON response
    * @param  action The name of the action calling this for the primary
    *         error message.
    * @throws A CKANException containing the error messages contained in the
    *         provided JSON.
    */
    private void HandleError( String json, String action ) throws CKANException {

        CKANException exception = new CKANException("Errors occurred performing: " + action);

        HashMap hm  = LoadClass( HashMap.class, json);
        Map<String,Object> m = (Map<String,Object>)hm.get("error");
        for (Map.Entry<String,Object> entry : m.entrySet()) {
            if ( entry.getKey().startsWith("_") )
                continue;
            exception.addError( entry.getValue() + " - " + entry.getKey() );
        }
        throw exception;
    }


    /**
    * Retrieves a dataset
    *
    * Retrieves the dataset with the given name, or ID, from the CKAN
    * connection specified in the Client constructor.
    *
    * @param  name The name or ID of the dataset to fetch
    * @returns The Dataset for the provided name.
    * @throws A CKANException if the request fails
    */
    public Dataset getDataset(String name) throws CKANException {
    	
        String returned_json = this._connection.Post("/api/action/package_show",
                                                     "{\"id\":\"" + name + "\"}" );
        
        if( returned_json != null ) {
        	log.warn(name + " returned json length "+returned_json.length());
        }else {
        	log.warn(name + " returned null json");
        }
        // http://docs.ckan.org/en/latest/ckan.logic.action.get.html#ckan.logic.action.get.package_show
        // + 
        // http://docs.ckan.org/en/latest/api.html#get-able-api-functions
        // ==>
        // http://datahub.io/api/3/action/package_show?id=farmers-markets-geographic-data-united-states
        
        /*
         * {"help": "Return the metadata of a dataset (package) and its resources.\n\n    
         *           :param id: the id or name of the dataset\n    
         *           :type id: string\n\n    
         *           :rtype: dictionary\n\n    ", 
         *  "success": true, 
         *  "result": {"license_title": "Other (Not Open)", 
         *             "maintainer": "Sebastian Hellmann", 
         *             "relationships_as_object": [], 
         *             "maintainer_email": "", 
         *             "revision_timestamp": "2013-04-13T16:35:53.934228", 
         *             "id": "e7dba441-4786-40a3-b607-9090ad5aac97", 
         *             "metadata_created": "2011-09-09T07:53:14.447832", 
         *             "metadata_modified": "2013-04-13T16:35:53.934228", 
         *             "author": "Sebastian Hellmann", 
         *             "author_email": "", 
         *             "state": "active", 
         *             "version": "", 
         *             "license_id": 
         *             "other-closed", 
         *             "type": null, 
         *             "resources": [{"resource_group_id": 
         *                            "96b50db3-7c54-412f-b0ba-681d3f6aac46",
         *                            "cache_last_updated": null, 
         *                            "revision_timestamp": "2013-04-13T16:35:53.934228", 
         *                            "webstore_last_updated": null, 
         *                            "id": "130a9cf7-c680-44e9-b499-598bc25cf2a7", 
         *                            "size": null, 
         *                            "state": "active", 
         *                            "last_modified": null, 
         *                            "hash": "", 
         *                            "description": "Download", 
         *                            "format": "text/turtle", 
         *                            "tracking_summary": {"total": 3, "recent": 3}, 
         *                            "mimetype_inner": "", 
         *                            "mimetype": "", 
         *                            "cache_url": "", 
         *                            "name": "", 
         *                            "created": null, 
         *                            "url": "http://klappstuhlclub.de/wp/wp-content/plugins/RDF2WP/Output/EntireBlogExporter.php?format=text/plain", 
         *                            "webstore_url": "", 
         *                            "position": 0, 
         *                            "revision_id": "692caeb1-704e-4a4a-9f91-773fbf250cf5", 
         *                            "resource_type": ""}, {}, {}, {}], 
         *             "tags": [{"vocabulary_id": null, 
         *                       "display_name": "klappstuhlclub", 
         *                       "name": "klappstuhlclub", 
         *                       "revision_timestamp": "2011-09-09T07:53:14.447832", 
         *                       "state": "active", 
         *                       "id": "02ddf21f-5c70-432e-9a55-05342b9fc22d"}, 
         *                      {"vocabulary_id": null, 
         *                       "display_name": "ksc", 
         *                       "name": "ksc", 
         *                       "revision_timestamp": "2011-09-09T07:53:14.447832", 
         *                       "state": "active", 
         *                       "id": "28e3103f-8c5c-4585-9be6-f314fab005cb"}, 
         *                      {"vocabulary_id": null, 
         *                      "display_name": "lod", 
         *                      "name": "lod", 
         *                      "revision_timestamp": "2011-09-09T07:56:01.188565", 
         *                      "state": "active", 
         *                      "id": "54a512d9-f003-4b79-badf-c85bfa977a7e"}, 
         *             "tracking_summary": {"total": 171, "recent": 5}, 
         *             "groups": [{"capacity": "public", 
         *                         "description": "This group catalogs data sets that are available on the Web as [Linked Data](http://en.wikipedia.org/wiki/Linked_Data)  and contain data links pointing at other Linked Data sets.\r\n\r\nThe descriptions of the data sets in this group are used to generate the [Linking Open Data Cloud diagram](http://lod-cloud.net) at regular intervals. The descriptions are also used generate the statistics provided in the [State of the LOD Cloud](http://lod-cloud.net/state/) document.\r\n\r\nIf you publish a linked data set yourself, please add it to CKAN so that it appears in the next version of the LOD cloud diagram. Please describe your data set according to [Guidelines for Collecting Metadata on Linked Datasets in CKAN](http://www.w3.org/wiki/TaskForces/CommunityProjects/LinkingOpenData/DataSets/CKANmetainformation). Please tag newly added data sets with lod. The editors of this group will review your description afterwards and move your dataset into the lodcloud group.\r\n \r\nPlease also use the [CKAN LOD Validator](http://www4.wiwiss.fu-berlin.de/lodcloud/ckan/validator/validate.php) to check that the description of your data set is complete.", 
         *                         "title": "Linking Open Data Cloud", 
         *                         "created": "2010-04-26T21:06:05.978206", 
         *                         "approval_status": "approved", 
         *                         "state": "active", 
         *                         "image_url": "http://lod-cloud.net/diagram/resources/lod-cloud-icon.png", 
         *                         "revision_id": "c5462628-d015-41d3-b737-b0e439b98e72", 
         *                         "type": "group", 
         *                         "id": "ce086ef6-9e56-4af1-a63c-fd768fa2dfff", "name": "lodcloud"}], 
         *             "relationships_as_subject": [], 
         *             "name": "klappstuhlclub", 
         *             "isopen": false, 
         *             "url": "http://klappstuhlclub.de", 
         *             "notes": "##Klappstuhlclub\r\nhttp://klappstuhlclub.de \r\nThe Klappstuhlclub (Folding chair club) is meeting every week (mostly Wednesday evening) in cities around the globe. The focus is on Germany currently (Leipzig over 300 meetings, Berlin over 200 meetings), but also Lisbon, France and USA.\r\n\r\n### Get involved\r\nPlease email if:  \r\n1. You want to join the Klappstuhlclub or you want to create a Klappstuhlclub in your city . (kurzum@googlemail.com)\r\n2. You want to know how we made a simple Wordpress blog Linked Data ready and all that. (hellmann@informatik.uni-leipzig.de )\r\n\r\n### License \r\nData is protected for privacy issues.\r\n\r\n### Semantic Wiki Wordpress\r\nThe data was created with a Wordpress plugin see\r\nhttp://bitbucket.org/aksw/rdf2wp/overview\r\n\r\nEach Blogpost is available as Linked Data. User enter data the same way as in Wikipedia infoboxes (with a Wiki template)\r\n\r\nSPARQL endpoint (Virtuoso) is here:\r\nhttp://klappstuhlclub.de/sparql\r\n\r\n", 
         *             "title": "Klappstuhlclub",
         *             "extras": [{"state": "active", 
         *                         "value": "\"50\"", 
         *                         "revision_timestamp": "2011-09-09T08:23:54.817967", 
         *                         "package_id": "e7dba441-4786-40a3-b607-9090ad5aac97", 
         *                         "key": "links:dbpedia", 
         *                         "revision_id": "82b23f1a-5fdb-40f3-88a9-b4bbb1b46e76", 
         *                         "id": "d79b71b6-6e1e-431e-923f-24febeb9df1d"}}
         */
        Dataset.Response r = LoadClass( Dataset.Response.class, returned_json);
        if ( ! r.success ) {
            HandleError( returned_json, "getDataset");
        }
        return r.result;
    }
    
    /**
     * 
     * @param datasetName - the name or ID of the dataset to find revisions for.
     * 
     * @return
     */
    public List<DatasetRevision> getDatasetRevisions(String datasetName) throws CKANException {
    	
    	// e.g. http://datahub.io/api/3/action/package_revision_list?id=klappstuhlclub
        String returned_json = this._connection.Post("/api/action/package_revision_list",
        											 "{\"id\":\"" + datasetName + "\"}" );
        //log.warn("revisions: " + returned_json);
        
        /*
         * {"help": "Return a dataset (package)'s revisions as a list of dictionaries.\n\n    
         *          :param id: the id or name of the dataset\n    
         *          :type id: string\n\n    ", 
         *  "success": true, 
         *  "result": [{"id": "692caeb1-704e-4a4a-9f91-773fbf250cf5", 
         *              "timestamp": "2013-04-13T16:35:53.934228", 
         *              "message": "Edited settings.", 
         *              "author": "timrdf", 
         *              "approved_timestamp": null}, 
         *              
         *             {"id": "28abe894-2cf1-4eb6-bd93-b14e0292042b", 
         *              "timestamp": "2011-09-09T07:53:14.447832", 
         *              "message": "creation", 
         *              "author": "http://kurzum.myopenid.com/", 
         *              "approved_timestamp": null}
         *            ]}
         */
        
        // Build a POJO from the JSON
        DatasetRevisions.Response r = LoadClass( DatasetRevisions.Response.class, returned_json);
        
        if ( ! r.success ) {
        	log.error("response was not successful");
            //HandleError( returned_json, "getDataset");
        }
        
        //log.warn(" gsoned " + r.result.size());

        return r.result;
    }

    /**
    * Deletes a dataset
    *
    * Deletes the dataset specified with the provided name/id
    *
    * @param  name The name or ID of the dataset to delete
    * @throws A CKANException if the request fails
    */
    public void deleteDataset(String name) throws CKANException {
        
    	String returned_json = this._connection.Post("/api/action/package_delete",
                                                     "{\"id\":\"" + name + "\"}" );
        
        Dataset.Response r = LoadClass( Dataset.Response.class, returned_json);
        if ( ! r.success ) {
            HandleError( returned_json, "deleteDataset");
        }
    }

    /**
    * Creates a dataset on the server
    *
    * Takes the provided dataset and sends it to the server to
    * perform an create, and then returns the newly created dataset.
    *
    * @param  dataset A dataset instance
    * @returns The Dataset as it now exists
    * @throws A CKANException if the request fails
    */
    public Dataset createDataset(Dataset dataset) throws CKANException {
    	
        Gson gson = new Gson();
        String data = gson.toJson( dataset );
        System.out.println( data );
        String returned_json = this._connection.Post("/api/action/package_create", data);
        
        //System.out.println( returned_json );
        Dataset.Response r = LoadClass( Dataset.Response.class, returned_json);
        if ( ! r.success ) {
            // This will always throw an exception
            HandleError(returned_json, "createDataset");
        }
        return r.result;
    }


    /**
     * 
     * @param dataset
     * @return
     * @throws CKANException
     */
    public Dataset updateDataset(Dataset dataset) throws CKANException {
    	
        Gson gson = new Gson();
        String data = gson.toJson( dataset );
        System.out.println( data );
        String returned_json = this._connection.Post("/api/action/package_update", data);
        
        //System.out.println( returned_json );
        Dataset.Response r = LoadClass( Dataset.Response.class, returned_json);
        if ( ! r.success ) {
            // This will always throw an exception
            HandleError(returned_json, "updateDataset");
        }
        return r.result;
    }
    
    /**
    * Retrieves a group
    *
    * Retrieves the group with the given name, or ID, from the CKAN
    * connection specified in the Client constructor.
    *
    * @param  name The name or ID of the group to fetch
    * @returns The Group instance for the provided name.
    * @throws A CKANException if the request fails
    */
    public Group getGroup(String name) throws CKANException {
    	
        String returned_json = this._connection.Post("/api/action/group_show",
                                                     "{\"id\":\"" + name + "\"}" );
        
        Group.Response r = LoadClass( Group.Response.class, returned_json);
        if ( ! r.success ) {
            HandleError(returned_json, "getGroup");
        }
        return r.result;
    }

  /**
    * Deletes a Group
    *
    * Deletes the group specified with the provided name/id
    *
    * @param  name The name or ID of the group to delete
    * @throws A CKANException if the request fails
    */
    public void deleteGroup(String name) throws CKANException {
    	
        String returned_json = this._connection.Post("/api/action/group_delete",
                                                     "{\"id\":\"" + name + "\"}" );
        
        Group.Response r = LoadClass( Group.Response.class, returned_json);
        if ( ! r.success ) {
            HandleError( returned_json, "deleteGroup");
        }
    }

    /**
    * Creates a Group on the server
    *
    * Takes the provided Group and sends it to the server to
    * perform an create, and then returns the newly created Group.
    *
    * @param  group A Group instance
    * @returns The Group as it now exists on the server
    * @throws A CKANException if the request fails
    */
    public Group createGroup(Group group) throws CKANException {
    	
        Gson gson = new Gson();
        String data = gson.toJson( group );
        String returned_json = this._connection.Post("/api/action/package_create", data );
        
        Group.Response r = LoadClass( Group.Response.class, returned_json);
        if ( ! r.success ) {
            // This will always throw an exception
            HandleError(returned_json, "createGroup");
        }
        return r.result;
    }

    /**
    * Uses the provided search term to find datasets on the server
    *
    * Takes the provided query and locates those datasets that match the query
    *
    * @param  query The search terms
    * @returns A SearchResults object that contains a count and the objects
    * @throws A CKANException if the request fails
    */
    public Dataset.SearchResults findDatasets(String query) throws CKANException {

        String returned_json = this._connection.Post("/api/action/package_search",
                                                     "{\"q\":\"" + query +"\"}" );
        
        Dataset.SearchResponse sr = LoadClass( Dataset.SearchResponse.class, returned_json);
        if ( ! sr.success ) {
            // This will always throw an exception
            HandleError(returned_json, "findDatasets");
        }
        return sr.result;
    }
}