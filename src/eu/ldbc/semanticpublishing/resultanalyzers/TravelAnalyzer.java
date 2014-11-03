package eu.ldbc.semanticpublishing.resultanalyzers;

import java.io.IOException;
import java.util.ArrayList;

import eu.ldbc.semanticpublishing.endpoint.SparqlQueryExecuteManager;
import eu.ldbc.semanticpublishing.endpoint.SparqlQueryConnection.QueryType;
import eu.ldbc.semanticpublishing.resultanalyzers.saxparsers.SAXTravelsTransformer;
import eu.ldbc.semanticpublishing.templates.MustacheTemplatesHolder;

/**
 * A class used to extract travels ids from dbpedia reference dataset.
 * Executes a sparql query and parses the result using an implementation of the SAXResultTransformer interface.
 *  http://swatproject.org/travelOntology.asp
 */
public class TravelAnalyzer {
	private SparqlQueryExecuteManager sparqlQeuryManager;
	private MustacheTemplatesHolder queryTemplatesHolder;
	private SAXTravelsTransformer travelsTransformer;
	
	public TravelAnalyzer(SparqlQueryExecuteManager sparqlQueryExecuteManager, MustacheTemplatesHolder queryTemplatesHolder) throws IOException {
		this.sparqlQeuryManager = sparqlQueryExecuteManager;
		this.queryTemplatesHolder = queryTemplatesHolder;
		StringBuilder query = new StringBuilder(); 
		query.append(queryTemplatesHolder.getQueryTemplates(MustacheTemplatesHolder.SYSTEM).get("analyzetravelsdata.txt"));
		travelsTransformer = new SAXTravelsTransformer();
		sparqlQeuryManager.executeSystemQuery(travelsTransformer, query.toString(), QueryType.SELECT);
		
	}	
	
	public ArrayList<String> collectTravelsIds() {
		return travelsTransformer.getTravelsIds();
	}
	
	public ArrayList<String> collectTravelsTypes() {
		return travelsTransformer.getTravelsTypes();
	}
	
	public ArrayList<String> collectTravelsSurroundedBy() {
		return travelsTransformer.getTravelsSurroundedBy();
	}	
		
	public ArrayList<String> collectTravelsIsOccupiedBy() {
		return travelsTransformer.getTravelsIsOccupiedBy();
	}

	public ArrayList<String> collectTravelsHasBoundary(){
		return travelsTransformer.getTravelsHasBoundary();
	}
	public ArrayList<String> collectTravelsHasHeadOfState(){
		return travelsTransformer.getTravelsHasHeadOfState();
	}
	public ArrayList<String> collectTravelsOccupies(){
		return travelsTransformer.getTravelsOccupies();
	}
	public ArrayList<String> collectTravelsHasRecognitionStatus(){
		return travelsTransformer.getTravelsHasRecognitionStatus();
	}
	public ArrayList<String> collectTravelsHasPhysicalProperty(){
		return travelsTransformer.getTravelsHasPhysicalProperty();
	}
	public ArrayList<String> collectTravelsHasCapital(){
		return travelsTransformer.getTravelsHasCapital();
	}
	public ArrayList<String> collectTravelsDirectPartOf(){
		return travelsTransformer.getTravelsDirectPartOf();
	}

	public ArrayList<String> collectTravelsHasArea(){
		return travelsTransformer.getTravelsHasArea();
	}

	public ArrayList<String> collectTravelsHasPopulation(){
		return travelsTransformer.getTravelsHasPopulation();
	}
//	public ArrayList<String> collectTravelsHasStatistic(){
//		return travelsTransformer.getTravelsHasStatistic();
//	}
	
}
