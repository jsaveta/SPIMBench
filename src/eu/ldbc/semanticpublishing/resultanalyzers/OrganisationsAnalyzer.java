
package eu.ldbc.semanticpublishing.resultanalyzers;

import java.io.IOException;
import java.util.ArrayList;

import eu.ldbc.semanticpublishing.endpoint.SparqlQueryExecuteManager;
import eu.ldbc.semanticpublishing.endpoint.SparqlQueryConnection.QueryType;
import eu.ldbc.semanticpublishing.resultanalyzers.saxparsers.SAXOrganisationsTransformer;
import eu.ldbc.semanticpublishing.templates.MustacheTemplatesHolder;

/**
 * A class used to extract organisations ids from dbpedia reference dataset.
 * Executes a sparql query and parses the result using an implementation of the SAXResultTransformer interface.
 */
public class OrganisationsAnalyzer {
	private SparqlQueryExecuteManager sparqlQeuryManager;
	private MustacheTemplatesHolder queryTemplatesHolder;
	private SAXOrganisationsTransformer organisationsTransformer;
	
	public OrganisationsAnalyzer(SparqlQueryExecuteManager sparqlQueryExecuteManager, MustacheTemplatesHolder queryTemplatesHolder) throws IOException {
		this.sparqlQeuryManager = sparqlQueryExecuteManager;
		this.queryTemplatesHolder = queryTemplatesHolder;
		StringBuilder query = new StringBuilder(); 
		query.append(queryTemplatesHolder.getQueryTemplates(MustacheTemplatesHolder.SYSTEM).get("analyzeorganisationsdata.txt"));
		organisationsTransformer = new SAXOrganisationsTransformer();
		sparqlQeuryManager.executeSystemQuery(organisationsTransformer, query.toString(), QueryType.SELECT);
		
	}	
	public ArrayList<String> collectOrganisationsIds() {
		return organisationsTransformer.getOrganisationsIds();
	}
	
	public ArrayList<String> collectOrganisationsLabels() {
		return organisationsTransformer.getOrganisationsLabels();
	}	
	public ArrayList<String> collectOrganisationsComments() {
		return organisationsTransformer.getOrganisationsComments();
	}
	public ArrayList<String> collectOrganisationsManagers() {
		return organisationsTransformer.getOrganisationsManagers();
	}
	public ArrayList<String> collectOrganisationsNames() {
		return organisationsTransformer.getOrganisationsNames();
	}
	public ArrayList<String> collectOrganisationsNicknames() {
		return organisationsTransformer.getOrganisationsNicknames();
	}
	public ArrayList<String> collectOrganisationsWebsites() {
		return organisationsTransformer.getOrganisationsWebsites();
	}
	
}


