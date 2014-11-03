
package eu.ldbc.semanticpublishing.resultanalyzers;

import java.io.IOException;
import java.util.ArrayList;

import eu.ldbc.semanticpublishing.endpoint.SparqlQueryExecuteManager;
import eu.ldbc.semanticpublishing.endpoint.SparqlQueryConnection.QueryType;
import eu.ldbc.semanticpublishing.resultanalyzers.saxparsers.SAXPersonsTransformer;
import eu.ldbc.semanticpublishing.templates.MustacheTemplatesHolder;

/**
 * A class used to extract persons ids from dbpedia reference dataset.
 * Executes a sparql query and parses the result using an implementation of the SAXResultTransformer interface.
 */
public class PersonsAnalyzer {
	private SparqlQueryExecuteManager sparqlQeuryManager;
	private MustacheTemplatesHolder queryTemplatesHolder;
	private SAXPersonsTransformer personsTransformer;
	
	public PersonsAnalyzer(SparqlQueryExecuteManager sparqlQueryExecuteManager, MustacheTemplatesHolder queryTemplatesHolder) throws IOException {
		this.sparqlQeuryManager = sparqlQueryExecuteManager;
		this.queryTemplatesHolder = queryTemplatesHolder;
		StringBuilder query = new StringBuilder(); 
		query.append(queryTemplatesHolder.getQueryTemplates(MustacheTemplatesHolder.SYSTEM).get("analyzepersonsdata.txt"));
		personsTransformer = new SAXPersonsTransformer();
		sparqlQeuryManager.executeSystemQuery(personsTransformer, query.toString(), QueryType.SELECT);
		
	}	
	

	public ArrayList<String> collectPersonsIds() {
		return personsTransformer.getPersonsIds();
	}
	public ArrayList<String> collectPersonsNames() {
		return personsTransformer.getPersonsNames();
	}
	public ArrayList<String> collectPersonsSurnames() {
		return personsTransformer.getPersonsSurnames();
	}
	public ArrayList<String> collectPersonsGivennames() {
		return personsTransformer.getPersonsGivennames();
	}
	public ArrayList<String> collectPersonsDescriptions() {
		return personsTransformer.getPersonsDescriptions();
	}
	public ArrayList<String> collectPersonsBirthdates() {
		return personsTransformer.getPersonsBirthdates();
	}
	public ArrayList<String> collectPersonsBirthplaces() {
		return personsTransformer.getPersonsBirthplaces();
	}
	public ArrayList<String> collectPersonsDeathdates() {
		return personsTransformer.getPersonsDeathdates();
	}
	public ArrayList<String> collectPersonsDeathplaces() {
		return personsTransformer.getPersonsDeathplaces();
	}
	
}


