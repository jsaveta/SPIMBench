package eu.ldbc.semanticpublishing.resultanalyzers;

import java.io.IOException;
import java.util.ArrayList;

import eu.ldbc.semanticpublishing.endpoint.SparqlQueryExecuteManager;
import eu.ldbc.semanticpublishing.endpoint.SparqlQueryConnection.QueryType;
import eu.ldbc.semanticpublishing.resultanalyzers.saxparsers.SAXMusicalartistsTransformer;
import eu.ldbc.semanticpublishing.templates.MustacheTemplatesHolder;

/**
 * A class used to extract musicalartists ids from dbpedia reference dataset.
 * Executes a sparql query and parses the result using an implementation of the SAXResultTransformer interface.
 */
public class MusicalartistsAnalyzer {
	private SparqlQueryExecuteManager sparqlQeuryManager;
	private MustacheTemplatesHolder queryTemplatesHolder;
	private SAXMusicalartistsTransformer musicalartistsTransformer;
	
	public MusicalartistsAnalyzer(SparqlQueryExecuteManager sparqlQueryExecuteManager, MustacheTemplatesHolder queryTemplatesHolder) throws IOException {
		this.sparqlQeuryManager = sparqlQueryExecuteManager;
		this.queryTemplatesHolder = queryTemplatesHolder;
		StringBuilder query = new StringBuilder(); 
		query.append(queryTemplatesHolder.getQueryTemplates(MustacheTemplatesHolder.SYSTEM).get("analyzemusicalartistsdata.txt"));
		musicalartistsTransformer = new SAXMusicalartistsTransformer();
		sparqlQeuryManager.executeSystemQuery(musicalartistsTransformer, query.toString(), QueryType.SELECT);
		
	}	
	
	public ArrayList<String> collectMusicalartistsIds() {
		return musicalartistsTransformer.getMusicalartistsIds();
	}
	public ArrayList<String> collectMusicalartistsComments() {
		return musicalartistsTransformer.getMusicalartistsComments();
	}
	public ArrayList<String> collectMusicalartistsLabels() {
		return musicalartistsTransformer.getMusicalartistsLabels();
	}
	public ArrayList<String> collectMusicalartistsNames() {
		return musicalartistsTransformer.getMusicalartistsNames();
	}
	public ArrayList<String> collectMusicalartistsBirthdates() {
		return musicalartistsTransformer.getMusicalartistsBirthdates();
	}
	public ArrayList<String> collectMusicalartistsBirthplaces() {
		return musicalartistsTransformer.getMusicalartistsBirthplaces();
	}
	public ArrayList<String> collectMusicalartistsGenres() {
		return musicalartistsTransformer.getMusicalartistsGenres();
	}
	public ArrayList<String> collectMusicalartistsHometowns() {
		return musicalartistsTransformer.getMusicalartistsHometowns();
	}
	
}


