package eu.ldbc.semanticpublishing.agents;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ldbc.semanticpublishing.TestDriver;
import eu.ldbc.semanticpublishing.endpoint.SparqlQueryConnection;
import eu.ldbc.semanticpublishing.endpoint.SparqlQueryExecuteManager;
import eu.ldbc.semanticpublishing.endpoint.SparqlQueryConnection.QueryType;
import eu.ldbc.semanticpublishing.properties.Definitions;
import eu.ldbc.semanticpublishing.refdataset.DataManager;
import eu.ldbc.semanticpublishing.statistics.Statistics;
import eu.ldbc.semanticpublishing.templates.MustacheTemplate;
import eu.ldbc.semanticpublishing.templates.editorial.DeleteTemplate;
import eu.ldbc.semanticpublishing.templates.editorial.InsertTemplate;
import eu.ldbc.semanticpublishing.templates.editorial.UpdateTemplate;
import eu.ldbc.semanticpublishing.util.RandomUtil;

/**
 * A class that represents an editorial agent. It executes INSERT, UPDATE, DELETE queries 
 * with a defined distribution, updates query execution statistics.
 */
public class EditorialAgent extends AbstractAsynchronousAgent {
	private final SparqlQueryExecuteManager queryExecuteManager;
	private final RandomUtil ru;
	private final AtomicBoolean benchmarkingState;
	protected final HashMap<String, String> queryTemplates;
	private SparqlQueryConnection connection;
	private Definitions definitions;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(EditorialAgent.class.getName());
	private final static Logger BRIEF_LOGGER = LoggerFactory.getLogger(TestDriver.class.getName());
	
	public EditorialAgent(AtomicBoolean benchmarkingState, SparqlQueryExecuteManager queryExecuteManager, RandomUtil ru, AtomicBoolean runFlag, HashMap<String, String> queryTemplates, Definitions definitions) {
		super(runFlag);
		this.queryExecuteManager = queryExecuteManager;
		this.ru = ru;
		this.benchmarkingState = benchmarkingState;
		this.queryTemplates = queryTemplates;
		this.connection = new SparqlQueryConnection(queryExecuteManager.getEndpointUrl(), queryExecuteManager.getEndpointUpdateUrl(), queryExecuteManager.getTimeoutMilliseconds(), true);
		this.definitions = definitions;
	}
	
	@Override
	public boolean executeLoop() {
		int queryDistribution = Definitions.editorialOperationsAllocation.getAllocation();
		
		long queryId = 0;
		String queryName = "";
		String queryString = "";
		String queryResult = "";
		QueryType queryType = QueryType.INSERT;
		
		try {
			
			switch (queryDistribution) {
				case 0 :
					MustacheTemplate insertQuery = new InsertTemplate("", ru, queryTemplates, definitions);
					
					queryType = insertQuery.getTemplateQueryType();
					queryName = insertQuery.getTemplateFileName();
					queryString = insertQuery.compileMustacheTemplate();
					
					queryId = Statistics.insertCreativeWorksQueryStatistics.getNewQueryId();
					
					break;
				case 1 :
					long cwNextId = ru.nextInt((int)DataManager.creativeWorksNextId.get());
					String uri = ru.numberURI("context", cwNextId, true, true);
								
					MustacheTemplate updateQuery = new UpdateTemplate(uri, ru, queryTemplates, definitions);
					
					queryType = updateQuery.getTemplateQueryType();
					queryName = updateQuery.getTemplateFileName();
					queryString = updateQuery.compileMustacheTemplate();
					
					queryId = Statistics.updateCreativeWorksQueryStatistics.getNewQueryId();
					
					break;
				case 2 :
					MustacheTemplate deleteQuery = new DeleteTemplate(ru, queryTemplates);
					
					queryType = deleteQuery.getTemplateQueryType();
					queryName = deleteQuery.getTemplateFileName();
					queryString = deleteQuery.compileMustacheTemplate();
					
					queryId = Statistics.deleteCreativeWorksQueryStatistics.getNewQueryId();
					
					break;
			}
			
			long executionTimeMs = System.currentTimeMillis();
			
			queryResult = queryExecuteManager.executeQuery(connection, queryName, queryString, queryType, true, false);
			
			updateQueryStatistics(true, queryType, queryName, queryString, queryResult, queryId, System.currentTimeMillis() - executionTimeMs);

		} catch (IOException ioe) {
			String msg = "Warning : EditorialAgent : IOException caught : " + ioe.getMessage() + ", attempting a new connection" + "\n" + "\tfor query : \n" + connection.getQueryString();
			
			System.out.println(msg);
			
			LOGGER.warn(msg);
			
			updateQueryStatistics(false, queryType, queryName, queryString, queryResult ,queryId, 0);
			
			connection = new SparqlQueryConnection(queryExecuteManager.getEndpointUrl(), queryExecuteManager.getEndpointUpdateUrl(), queryExecuteManager.getTimeoutMilliseconds(), true);
		}
		
		return true;
	}
	
	@Override
	public void executeFinalize() {			
		connection.disconnect();
	}
	
	private void updateQueryStatistics(boolean reportSuccess, QueryType queryType, String queryName, String queryString, String queryResult, long id, long queryExecutionTimeMs) {

		String queryNameId = constructQueryNameId(queryName, queryType, id);
		
		//report success
		if (reportSuccess) {
			if (queryType == QueryType.INSERT) {
				if (queryResult.length() >= 0 && benchmarkingState.get()) {
					Statistics.insertCreativeWorksQueryStatistics.reportSuccess(queryExecutionTimeMs);
				}				
			} else if (queryType == QueryType.UPDATE) {
				if (queryResult.length() >= 0 && benchmarkingState.get()) {
					Statistics.updateCreativeWorksQueryStatistics.reportSuccess(queryExecutionTimeMs);
				}								
			} else if (queryType == QueryType.DELETE) {
				if (queryResult.length() >= 0 && benchmarkingState.get()) {
					Statistics.deleteCreativeWorksQueryStatistics.reportSuccess(queryExecutionTimeMs);
				}	
			}

			logBrief(queryNameId, queryType, queryResult, "", queryExecutionTimeMs);
			LOGGER.info("\n*** Query [" + queryNameId  + "], execution time : " + queryExecutionTimeMs + " ms\n" + queryString + "\n---------------------------------------------\n*** Result for query [" + queryNameId + "]" + " : \n" + "Length : " + queryResult.length() + "\n" + queryResult + "\n\n");

		//report failure			
		} else {
			if (queryType == QueryType.INSERT) {
				Statistics.insertCreativeWorksQueryStatistics.reportFailure();
			} else if (queryType == QueryType.UPDATE) {
				Statistics.updateCreativeWorksQueryStatistics.reportFailure();
			} else if (queryType == QueryType.DELETE) {
				Statistics.deleteCreativeWorksQueryStatistics.reportFailure();
			}
			logBrief(queryNameId, queryType, queryResult, ", query has timed out!", queryExecutionTimeMs);
		}
	}
	
	private void logBrief(String queryNameId, QueryType queryType, String queryResult, String appendString, long queryExecutionTimeMs) {
		StringBuilder reportSb = new StringBuilder();
		reportSb.append(String.format("\t[%s] Query executed, execution time : %d ms %s", queryNameId, queryExecutionTimeMs, appendString));
		if (queryType == QueryType.SELECT || queryType == QueryType.CONSTRUCT || queryType == QueryType.DESCRIBE) {
			reportSb.append(", characters returned : " + queryResult.length());
		}
		
		BRIEF_LOGGER.info(reportSb.toString());		
	}	
	
	private String constructQueryNameId(String queryName, QueryType queryType, long id) {
		StringBuilder queryId = new StringBuilder();
		queryId.append(queryName);
		queryId.append(", id:");
		queryId.append("" + id);
		return queryId.toString();
	}
}
