package eu.ldbc.semanticpublishing.resultanalyzers;

import java.io.IOException;
import java.util.ArrayList;

import eu.ldbc.semanticpublishing.endpoint.SparqlQueryExecuteManager;
import eu.ldbc.semanticpublishing.endpoint.SparqlQueryConnection.QueryType;
import eu.ldbc.semanticpublishing.resultanalyzers.saxparsers.SAXEventsTransformer;
import eu.ldbc.semanticpublishing.templates.MustacheTemplatesHolder;

/**
 * A class used to extract events ids from dbpedia reference dataset.
 * Executes a sparql query and parses the result using an implementation of the SAXResultTransformer interface.
 */
public class EventsAnalyzer {
	private SparqlQueryExecuteManager sparqlQeuryManager;
	private MustacheTemplatesHolder queryTemplatesHolder;
	private SAXEventsTransformer eventsTransformer;
	
	public EventsAnalyzer(SparqlQueryExecuteManager sparqlQueryExecuteManager, MustacheTemplatesHolder queryTemplatesHolder) throws IOException {
		this.sparqlQeuryManager = sparqlQueryExecuteManager;
		this.queryTemplatesHolder = queryTemplatesHolder;
		StringBuilder query = new StringBuilder(); 
		query.append(queryTemplatesHolder.getQueryTemplates(MustacheTemplatesHolder.SYSTEM).get("analyzeeventsdata.txt"));
		eventsTransformer = new SAXEventsTransformer();
		sparqlQeuryManager.executeSystemQuery(eventsTransformer, query.toString(), QueryType.SELECT);
		
	}	
	
	public ArrayList<String> collectEventsIds() {
		return eventsTransformer.getEventsIds();
	}
	
	public ArrayList<String> collectEventsLabels() {
		return eventsTransformer.getEventLabels();
	}	
	
	public ArrayList<String> collectEventsComments() {
		return eventsTransformer.getEventComments();
	}	
	
	public ArrayList<String> collectEventsCountries() {
		return eventsTransformer.getEventsCountries();
	}
	public ArrayList<String> collectEventsSubjects() throws IOException {
		return eventsTransformer.getEventsSubjects();
	}
}
