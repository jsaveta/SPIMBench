package eu.ldbc.semanticpublishing.properties;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Random;

import eu.ldbc.semanticpublishing.util.AllocationsUtil;

/**
 * A holder of the benchmark definitions of allocation values.
 * Client is expected to initialize from file definitions.properties first all allocation values. 
 */
public class Definitions {
	public static final String ABOUTS_ALLOCATIONS = "aboutsAllocations";
	public static final String MENTIONS_ALLOCATIONS = "mentionsAllocations";
	public static final String ENTITY_POPULARITY = "entityPopularity";
	public static final String USE_POPULAR_ENTITIES = "usePopularEntities";
	public static final String CREATIVE_WORK_TYPES_ALLOCATION = "creativeWorkTypesAllocation";
	public static final String ABOUT_AND_MENTIONS_ALLOCATION = "aboutAndMentionsAllocation";
	public static final String EDITORIAL_OPERATIONS_ALLOCATION = "editorialOperationsAllocation";
	public static final String AGGREGATION_OPERATIONS_ALLOCATION = "aggregationOperationsAllocation";
	public static final String EXPONENTIAL_DECAY_UPPER_LIMIT_OF_CWS = "exponentialDecayUpperLimitOfCWs";
	public static final String EXPONENTIAL_DECAY_RATE = "exponentialDecayRate";
	public static final String EXPONENTIAL_DECAY_THRESHOLD_PERCENT = "exponentialDecayThresholdPercent";
	public static final String MAJOR_EVENTS_PER_YEAR = "majorEventsPerYear";
	public static final String MINOR_EVENTS_PER_YEAR = "minorEventsPerYear";
	public static final String YEAR_SEED = "seedYear";
	public static final String DATA_GENERATOR_PERIOD_YEARS = "dataGenerationPeriodYears";
	public static final String CORRELATIONS_AMOUNT = "correlationsAmount";
	public static final String CORRELATIONS_MAGNITUDE = "correlationsMagnitude";
	public static final String CORRELATIONS_DURATION = "correlationDuration";
	public static final String CORRELATION_ENTITY_LIFESPAN = "correlationEntityLifespan";
	/*jsaveta start*/
	public static final String THUMBNAIL_ALLOCATION= "thumbnailAllocation";
	public static final String PRODUCT_TYPE_ALLOCATIONS = "productTypesAllocation";
	public static final String CORE_THINGS_SUBCLASS_ALLOCATIONS = "coreThingSubClassAllocation";
	public static final String CORE_PRIMARY_TOPIC_ALLOCATIONS = "corePrimaryTopicAllocation";
	public static final String TRAVEL_ALLOCATIONS = "travelAllocation";
	public static final String TRANSFORMATION_ALLOCATION = "transformationAllocation";
	public static final String LEXICAL_ALLOCATION = "lexicalAllocation";
	public static final String STRUCTURAL_ALLOCATION = "structuralAllocation";
	public static final String LOGICAL_ALLOCATION = "logicalAllocation";
	public static final String SIMPLE_COMBINATION_ALLOCATION = "simpleCombinationAllocation";
	public static final String COMPLEX_COMBINATION_ALLOCATION = "complexCombinationAllocation";
	public static final String FUNCTIONAL_INVERSEFUNCTIONAL_ALLOCATION = "functionalInverseFunctionalAllocation";
	/*jsaveta end*/
	public static final String GEO_MIN_LAT = "minLat";
	public static final String GEO_MAX_LAT = "maxLat";
	public static final String GEO_MIN_LONG = "minLong";
	public static final String GEO_MAX_LONG = "maxLong";
	public static final String MILESTONE_QUERY_POSITION = "mileStoneQueryPosition";


	
	//About tags in Creative Works
	public static AllocationsUtil aboutsAllocations;
	
	//Mentions tags in Creative Works, e.g. 3.81% - 1 mentions tag, 0.93% - 2 mentions tags ... etc. 94.77% - CWs with no mentions tags
	public static AllocationsUtil mentionsAllocations;
	
	//Determines the popularity of an entity, e.g. 5% - popular, 95% - regular
	public static AllocationsUtil entityPopularity;	
	
	//Determines allocation of popular to regular tagging, e.g. 30% of cases when needed popular entities will be used
	public static AllocationsUtil usePopularEntities;
	
	//Determines allocation of Creative Work Types, e.g. 45% - BlogPost, 35% - NewsItem, 20% - Program
	public static AllocationsUtil creativeWorkTypesAllocation;
	
	/*jsaveta start*/
	//Determines allocation of types of Thumbnail (Creative Work Ontology) , e.g. 30% FixedSize66Thumbnail ..
	public static AllocationsUtil thumbnailAllocation;
	
	//Determines allocation of types of product (Company Ontology) , e.g. 20% Blogs ..
	public static AllocationsUtil productTypesAllocation;
	
	//Determines allocation of subClassOf core:Thing (Concept Ontology) , e.g. 20% Place ..
	public static AllocationsUtil coreThingSubClassAllocation;
		
	//Determines allocation of primaryTopicOf core:Thing (Concept Ontology), e.g. 30% twitter ..
	public static AllocationsUtil corePrimaryTopicAllocation;
	
	//Determines allocation of travel...
	public static AllocationsUtil travelAllocation;
	
	//Determines allocation of transformation
	public static AllocationsUtil transformationAllocation;
	
	//Determines allocation of lexical transformation
	public static AllocationsUtil lexicalAllocation;
	
	//Determines allocation of structural transformation
	public static AllocationsUtil structuralAllocation;
	
	//Determines allocation of logical transformation
	public static AllocationsUtil logicalAllocation;
	
	//Determines allocation of simple combination transformation
	public static AllocationsUtil simpleCombinationAllocation;
	
	//Determines allocation of complex combination transformation
	public static AllocationsUtil complexCombinationAllocation;
	
	//Determines allocation of functional and inverse functional allocation combination transformation
	public static AllocationsUtil functionalInverseFunctionalAllocation;
	
	/*jsaveta end*/

	//Determines the aggregation type, aggregate on about or on mentions property
	public static AllocationsUtil aboutAndMentionsAllocation;
	
	//Determines the editorial operations distribution, e.g. insert - 80%, update - 10%, delete - 10%
	public static AllocationsUtil editorialOperationsAllocation;	
	
	//Determines the aggregation operations distribution, e.g. query1 - 80%, query2 - 20%
	public static AllocationsUtil aggregationOperationsAllocation;
	
	private static final Properties definitionsProperties = new Properties();
	
	private boolean verbose = false;
	
	/**
	 * Load the configuration from the given file (java properties format).
	 * @param filename A readable file on the file system.
	 * @throws IOException
	 */
	public void loadFromFile(String filename, boolean verbose) throws IOException {
		
		InputStream input = new FileInputStream(filename);
		try {
			definitionsProperties.load(input);
		}
		finally {
			input.close();
		}
		this.verbose = verbose;
	}
	
	/**
	 * Read a definition parameter's value as a string
	 * @param key
	 * @return
	 */
	private String getString( String key) {
		String value = definitionsProperties.getProperty(key);
		
		if(value == null) {
			throw new IllegalStateException( "Missing definitions parameter: " + key);
		}
		return value;
	}
	
	/**
	 * Read a configuration parameter's value as an int
	 * @param key
	 * @return
	 */
	public int getInt(String key) {
		String value = getString(key);
		
		try {
			return Integer.parseInt(value);
		}
		catch( NumberFormatException e ) {
			throw new IllegalStateException( "Illegal value for integer configuration parameter: " + key);
		}
	}

	/**
	 * Read a configuration parameter's value as a long
	 * @param key
	 * @return
	 */
	public long getLong(String key) {
		String value = getString(key);
		
		try {
			return Long.parseLong(value);
		}
		catch( NumberFormatException e ) {
			throw new IllegalStateException( "Illegal value for long integer configuration parameter: " + key);
		}
	}
	
	/**
	 * Read a configuration parameter's value as a Double
	 * @param key
	 * @return
	 */
	public double getDouble(String key) {
		String value = getString(key);
		
		try {
			return Double.parseDouble(value);
		}
		catch( NumberFormatException e ) {
			throw new IllegalStateException( "Illegal value for long integer configuration parameter: " + key);
		}
	}	
	
	public void setLong(String key, long value) {
		definitionsProperties.setProperty(key, Long.toString(value));
	}
	
	public void initializeAllocations(Random random) {
		if (verbose) {
			System.out.println("Initializing allocations...");
		}
		
		initializeAllocation(ABOUTS_ALLOCATIONS, random);
		initializeAllocation(MENTIONS_ALLOCATIONS, random);
		initializeAllocation(ENTITY_POPULARITY, random);
		initializeAllocation(USE_POPULAR_ENTITIES, random);
		initializeAllocation(CREATIVE_WORK_TYPES_ALLOCATION, random);
		initializeAllocation(ABOUT_AND_MENTIONS_ALLOCATION, random);
		initializeAllocation(EDITORIAL_OPERATIONS_ALLOCATION, random);
		initializeAllocation(AGGREGATION_OPERATIONS_ALLOCATION, random);
		/*jsaveta start*/
		initializeAllocation(THUMBNAIL_ALLOCATION, random);
		initializeAllocation(PRODUCT_TYPE_ALLOCATIONS, random);
		initializeAllocation(CORE_THINGS_SUBCLASS_ALLOCATIONS, random);
		initializeAllocation(CORE_PRIMARY_TOPIC_ALLOCATIONS, random);
		initializeAllocation(TRAVEL_ALLOCATIONS, random);
		initializeAllocation(TRANSFORMATION_ALLOCATION, random);
		initializeAllocation(LEXICAL_ALLOCATION, random);
		initializeAllocation(STRUCTURAL_ALLOCATION, random);
		initializeAllocation(LOGICAL_ALLOCATION, random);
		initializeAllocation(SIMPLE_COMBINATION_ALLOCATION, random);
		initializeAllocation(COMPLEX_COMBINATION_ALLOCATION, random);
		initializeAllocation(FUNCTIONAL_INVERSEFUNCTIONAL_ALLOCATION, random);
		
		/*jsaveta end*/
	}
	
	public static void reconfigureAllocations(Random random) {
		aboutsAllocations.setRandom(random);
		mentionsAllocations.setRandom(random);
		entityPopularity.setRandom(random);
		usePopularEntities.setRandom(random);
		creativeWorkTypesAllocation.setRandom(random);
		aboutAndMentionsAllocation.setRandom(random);
		editorialOperationsAllocation.setRandom(random);
		aggregationOperationsAllocation.setRandom(random);
		thumbnailAllocation.setRandom(random);
		productTypesAllocation.setRandom(random);
		coreThingSubClassAllocation.setRandom(random);
		corePrimaryTopicAllocation.setRandom(random);
		travelAllocation.setRandom(random);
		transformationAllocation.setRandom(random);
		lexicalAllocation.setRandom(random);
		structuralAllocation.setRandom(random);
		logicalAllocation.setRandom(random);
		simpleCombinationAllocation.setRandom(random);
		complexCombinationAllocation.setRandom(random);
		functionalInverseFunctionalAllocation.setRandom(random);

	}
	
	/**
	 * Initialize allocations depending on allocationProperty name
	 */
	private void initializeAllocation(String allocationPorpertyName, Random random) {
		String allocations = getString(allocationPorpertyName);
		String[] allocationsAsStrings = allocations.split(",");
		double[] allocationsAsDoubles = new double[allocationsAsStrings.length];
		
		for (int i = 0; i < allocationsAsDoubles.length; i++) {
			allocationsAsDoubles[i] = Double.parseDouble(allocationsAsStrings[i]);
		}
		
		if (allocationPorpertyName.equals(ABOUTS_ALLOCATIONS)) {
			aboutsAllocations = new AllocationsUtil(allocationsAsDoubles, random);
		} else if (allocationPorpertyName.equals(MENTIONS_ALLOCATIONS)) {
			mentionsAllocations = new AllocationsUtil(allocationsAsDoubles, random);
		} else if (allocationPorpertyName.equals(ENTITY_POPULARITY)) {
			entityPopularity = new AllocationsUtil(allocationsAsDoubles, random);
		} else if (allocationPorpertyName.equals(USE_POPULAR_ENTITIES)) {
			usePopularEntities = new AllocationsUtil(allocationsAsDoubles, random);
		} else if (allocationPorpertyName.equals(CREATIVE_WORK_TYPES_ALLOCATION)) {
			creativeWorkTypesAllocation = new AllocationsUtil(allocationsAsDoubles, random);
		} else if (allocationPorpertyName.equals(ABOUT_AND_MENTIONS_ALLOCATION)) {
			aboutAndMentionsAllocation = new AllocationsUtil(allocationsAsDoubles, random);
		} else if (allocationPorpertyName.equals(EDITORIAL_OPERATIONS_ALLOCATION)) {
			editorialOperationsAllocation = new AllocationsUtil(allocationsAsDoubles, random);
		} else if (allocationPorpertyName.equals(AGGREGATION_OPERATIONS_ALLOCATION)) {
			aggregationOperationsAllocation = new AllocationsUtil(allocationsAsDoubles, random);
			/*jsaveta start*/
		} else if (allocationPorpertyName.equals(TRANSFORMATION_ALLOCATION)) {
			transformationAllocation = new AllocationsUtil(allocationsAsDoubles, random);
		} else if (allocationPorpertyName.equals(LEXICAL_ALLOCATION)) {
			lexicalAllocation = new AllocationsUtil(allocationsAsDoubles, random);
		} else if (allocationPorpertyName.equals(STRUCTURAL_ALLOCATION)) {
			structuralAllocation = new AllocationsUtil(allocationsAsDoubles, random);
		} else if (allocationPorpertyName.equals(LOGICAL_ALLOCATION)) {
			logicalAllocation = new AllocationsUtil(allocationsAsDoubles, random);
		}
		else if (allocationPorpertyName.equals(SIMPLE_COMBINATION_ALLOCATION)) {
			simpleCombinationAllocation = new AllocationsUtil(allocationsAsDoubles, random);
		}
		else if (allocationPorpertyName.equals(COMPLEX_COMBINATION_ALLOCATION)) {
			complexCombinationAllocation = new AllocationsUtil(allocationsAsDoubles, random);
		}
		 else if (allocationPorpertyName.equals(THUMBNAIL_ALLOCATION)) {
			thumbnailAllocation = new AllocationsUtil(allocationsAsDoubles,random);	
		} else if (allocationPorpertyName.equals(PRODUCT_TYPE_ALLOCATIONS)) {
			productTypesAllocation = new AllocationsUtil(allocationsAsDoubles,random);
		} else if (allocationPorpertyName.equals(CORE_THINGS_SUBCLASS_ALLOCATIONS)) {
			coreThingSubClassAllocation = new AllocationsUtil(allocationsAsDoubles,random);
		} else if (allocationPorpertyName.equals(CORE_PRIMARY_TOPIC_ALLOCATIONS)) {
			corePrimaryTopicAllocation = new AllocationsUtil(allocationsAsDoubles,random);
		} else if (allocationPorpertyName.equals(TRAVEL_ALLOCATIONS)) {
			travelAllocation = new AllocationsUtil(allocationsAsDoubles,random);
		}
		else if (allocationPorpertyName.equals(FUNCTIONAL_INVERSEFUNCTIONAL_ALLOCATION)) {
			 functionalInverseFunctionalAllocation = new AllocationsUtil(allocationsAsDoubles,random);
		}
		/*jsaveta end*/
		if (verbose) {
			System.out.println(String.format("\t%-33s : {%s}", allocationPorpertyName, allocations));
		}
	}
}

