package eu.ldbc.semanticpublishing.resultanalyzers;

import java.io.IOException;

import eu.ldbc.semanticpublishing.endpoint.SparqlQueryExecuteManager;
import eu.ldbc.semanticpublishing.endpoint.SparqlQueryConnection.QueryType;
import eu.ldbc.semanticpublishing.resultanalyzers.saxparsers.SAXCreativeWorksCountTransformer;
import eu.ldbc.semanticpublishing.templates.MustacheTemplatesHolder;

/**
 * A class used to retrieve the greatest ID of CreativeWorks stored in the repository.
 * Executes a sparql query and parses the result using an implementation of the SAXResultTransformer interface.
 */
public class CreativeWorksAnalyzer {
	private SparqlQueryExecuteManager sparqlQeuryManager;
	private MustacheTemplatesHolder queryTemplatesHolder;
	
	public CreativeWorksAnalyzer(SparqlQueryExecuteManager sparqlQueryManager, MustacheTemplatesHolder queryTemplatesHolder) {
		this.sparqlQeuryManager = sparqlQueryManager;
		this.queryTemplatesHolder = queryTemplatesHolder;
	}
	
	public long getResult() throws IOException {
		StringBuilder query = new StringBuilder();
		query.append(queryTemplatesHolder.getQueryTemplates(MustacheTemplatesHolder.SYSTEM).get("analyzecreativeworks.txt"));
		
		SAXCreativeWorksCountTransformer saxCwCounter = new SAXCreativeWorksCountTransformer();
		sparqlQeuryManager.executeSystemQuery(saxCwCounter, query.toString(), QueryType.SELECT);
		return saxCwCounter.getResult();
	}
}
