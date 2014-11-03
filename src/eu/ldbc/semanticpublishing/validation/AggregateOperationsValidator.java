package eu.ldbc.semanticpublishing.validation;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ldbc.semanticpublishing.TestDriver;
import eu.ldbc.semanticpublishing.agents.EditorialAgent;
import eu.ldbc.semanticpublishing.endpoint.SparqlQueryConnection;
import eu.ldbc.semanticpublishing.endpoint.SparqlQueryExecuteManager;
import eu.ldbc.semanticpublishing.endpoint.SparqlQueryConnection.QueryType;
import eu.ldbc.semanticpublishing.properties.Configuration;
import eu.ldbc.semanticpublishing.properties.Definitions;
import eu.ldbc.semanticpublishing.statistics.Statistics;
import eu.ldbc.semanticpublishing.substitutionparameters.SubstitutionParametersGenerator;
import eu.ldbc.semanticpublishing.templates.MustacheTemplate;
import eu.ldbc.semanticpublishing.util.RandomUtil;
import eu.ldbc.semanticpublishing.util.RdfUtils;

public class AggregateOperationsValidator extends Validator {
	private TestDriver testDriver;
	private ValidationValuesManager validationValuesManager;
	private SparqlQueryExecuteManager queryExecuteManager;
	private SparqlQueryConnection connection;
	private RandomUtil ru;
	private HashMap<String, String> aggregateQueryTemplates;
	private Configuration configuration;
	private Definitions definitions;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(EditorialAgent.class.getName());
	private final static Logger BRIEF_LOGGER = LoggerFactory.getLogger(TestDriver.class.getName());	
	
	public AggregateOperationsValidator(TestDriver testDriver, ValidationValuesManager validationValuesManager, SparqlQueryExecuteManager queryExecuteManager, RandomUtil ru, HashMap<String, String> aggregateQueryTemplates, Configuration configuration, Definitions definitions) {
		this.testDriver = testDriver;
		this.validationValuesManager = validationValuesManager;
		this.queryExecuteManager = queryExecuteManager;
		this.connection = new SparqlQueryConnection(queryExecuteManager.getEndpointUrl(), queryExecuteManager.getEndpointUpdateUrl(), queryExecuteManager.getTimeoutMilliseconds(), true);
		this.ru = ru;
		this.aggregateQueryTemplates = aggregateQueryTemplates;
		this.configuration = configuration;
		this.definitions = definitions;
	}
	
	@SuppressWarnings("unchecked")
	public void validate() throws Exception {
		System.out.println("\tValidating AGGREGATE operations...");
		
		//load validation dataset into the database
		loadValidationData();
		
		//refresh statistics
		testDriver.populateRefDataEntitiesLists(false, false, true, "\t");

		Class<SubstitutionParametersGenerator> c = null;
		Constructor<?> cc = null;
		MustacheTemplate queryTemplate = null;
		String queryName = "";
		String queryString = "";
		String queryResult = "";
		QueryType queryType;
		
		for (int i = 0; i < Statistics.AGGREGATE_QUERIES_COUNT; i++) {
			ValidationValues validationValues = validationValuesManager.getValidationValuesFor(i);
			
			c = (Class<SubstitutionParametersGenerator>) Class.forName(String.format("eu.ldbc.semanticpublishing.templates.aggregation.Query%dTemplate", (i + 1)));
			cc = c.getConstructor(RandomUtil.class, HashMap.class, Definitions.class, String[].class);
			queryTemplate = (MustacheTemplate) cc.newInstance(ru, aggregateQueryTemplates, definitions, validationValues.getSubstitutionParameters());
			
			queryType = queryTemplate.getTemplateQueryType();
			queryName = queryTemplate.getTemplateFileName();
			queryString = queryTemplate.compileMustacheTemplate();			
			
			queryResult = queryExecuteManager.executeQuery(connection, queryName, queryString, queryType, false, true);			

			BRIEF_LOGGER.info(String.format("Query [%s] executed, iteration %d", queryName, i));
			LOGGER.info("\n*** Query [" + queryName + "], iteration " + i + "\n" + queryString + "\n---------------------------------------------\n*** Result for query [" + queryName + "]" + " : \n" + "Length : " + queryResult.length() + "\n" + queryResult + "\n\n");
	
			System.out.println(String.format("\tQuery %-1d : ", (i + 1)));
			int errorsForQuery = validateAggregate(queryResult, "AGGREGATE", (i + 1), validationValues.getValidationResultsList(), false);
			System.out.print(String.format("\t\t%d errors\n", errorsForQuery));
		}
	}
	
	private void loadValidationData() throws IOException {
		System.out.println("\tLoading Validation Data...");
		
		String endpoint = configuration.getString(Configuration.ENDPOINT_UPDATE_URL);
		
		File[] files = new File(configuration.getString(Configuration.VALIDATION_PATH)).listFiles();
		
		Arrays.sort(files);
		for( File file : files ) {
			if( file.getName().endsWith(".nq")) {
				InputStream input = new FileInputStream(file);
				RdfUtils.postStatements(endpoint, RdfUtils.CONTENT_TYPE_SESAME_NQUADS, input);
			}
		}
	}
}
