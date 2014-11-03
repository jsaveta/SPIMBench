
package eu.ldbc.semanticpublishing.resultanalyzers;

import java.io.IOException;
import java.util.ArrayList;

import eu.ldbc.semanticpublishing.endpoint.SparqlQueryExecuteManager;
import eu.ldbc.semanticpublishing.endpoint.SparqlQueryConnection.QueryType;
import eu.ldbc.semanticpublishing.resultanalyzers.saxparsers.SAXPlacesTransformer;
import eu.ldbc.semanticpublishing.templates.MustacheTemplatesHolder;

/**
 * A class used to extract places ids from dbpedia reference dataset.
 * Executes a sparql query and parses the result using an implementation of the SAXResultTransformer interface.
 */
public class PlacesAnalyzer {
	private SparqlQueryExecuteManager sparqlQeuryManager;
	private MustacheTemplatesHolder queryTemplatesHolder;
	private SAXPlacesTransformer placesTransformer;
	
	public PlacesAnalyzer(SparqlQueryExecuteManager sparqlQueryExecuteManager, MustacheTemplatesHolder queryTemplatesHolder) throws IOException {
		this.sparqlQeuryManager = sparqlQueryExecuteManager;
		this.queryTemplatesHolder = queryTemplatesHolder;
		StringBuilder query = new StringBuilder(); 
		query.append(queryTemplatesHolder.getQueryTemplates(MustacheTemplatesHolder.SYSTEM).get("analyzeplacesdata.txt"));
		placesTransformer = new SAXPlacesTransformer();
		sparqlQeuryManager.executeSystemQuery(placesTransformer, query.toString(), QueryType.SELECT);
		
	}	
	
	public ArrayList<String> collectPlacesIds() {
		return placesTransformer.getPlacesIds();
	}
	
	public ArrayList<String> collectPlacesNames() {
		return placesTransformer.getPlacesNames();
	}	

	public ArrayList<String> collectPlacesComments() {
		return placesTransformer.getPlacesComments();
	}
	public ArrayList<String> collectPlacesCountries() {
		return placesTransformer.getPlacesCountries();
	}
	public ArrayList<String> collectPlacesGeos() {
		return placesTransformer.getPlacesGeos();
	}
	
}


