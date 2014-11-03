
package eu.ldbc.semanticpublishing.resultanalyzers;

import java.io.IOException;
import java.util.ArrayList;

import eu.ldbc.semanticpublishing.endpoint.SparqlQueryExecuteManager;
import eu.ldbc.semanticpublishing.endpoint.SparqlQueryConnection.QueryType;
import eu.ldbc.semanticpublishing.resultanalyzers.saxparsers.SAXSportsTransformer;
import eu.ldbc.semanticpublishing.templates.MustacheTemplatesHolder;

/**
 * A class used to extract sports ids from dbpedia reference dataset.
 * Executes a sparql query and parses the result using an implementation of the SAXResultTransformer interface.
 */
public class SportsAnalyzer {
	private SparqlQueryExecuteManager sparqlQeuryManager;
	private MustacheTemplatesHolder queryTemplatesHolder;
	private SAXSportsTransformer sportsTransformer;
	
	public SportsAnalyzer(SparqlQueryExecuteManager sparqlQueryExecuteManager, MustacheTemplatesHolder queryTemplatesHolder) throws IOException {
		this.sparqlQeuryManager = sparqlQueryExecuteManager;
		this.queryTemplatesHolder = queryTemplatesHolder;
		StringBuilder query = new StringBuilder(); 
		query.append(queryTemplatesHolder.getQueryTemplates(MustacheTemplatesHolder.SYSTEM).get("analyzesportsdata.txt"));
		sportsTransformer = new SAXSportsTransformer();
		sparqlQeuryManager.executeSystemQuery(sportsTransformer, query.toString(), QueryType.SELECT);
		
	}	
	
	public ArrayList<String> collectSportsIds() {
		return sportsTransformer.getSportsIds();
	}
	
	public ArrayList<String> collectSportsCaptions() {
		return sportsTransformer.getSportsCaptions();
	}	
	
	public ArrayList<String> collectSportsComments() {
		return sportsTransformer.getSportsComments();
	}	
	
	public ArrayList<String> collectSportsEquipments() {
		return sportsTransformer.getSportsEquipments();
	}
	public ArrayList<String> collectSportsOlympics() {
		return sportsTransformer.getSportsOlympics();
	}
	public ArrayList<String> collectSportsTeams() {
		return sportsTransformer.getSportsTeams();
	}
}


