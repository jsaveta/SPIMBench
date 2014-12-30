Semantic Publishing Instance Matching Benchmark (SPIMBENCH)



Description : 
------------------------------------------------------------------------------
Semantic Publishing Instance Matching Benchmark (SPIMBENCH), is a benchmark for the assessment of Instance Matching techniques 
for RDF data with an associated schema. Essentially, SPIMBENCH implements: (i) a set of test cases based on 
transformations that distinguish different types of matching entities, (ii) a scalable data generator, 
(iii) a gold standard documenting the matches that IM systems should find, and (iv) evaluation metrics. 



Distribution : 
------------------------------------------------------------------------------

The benchmark test driver is distributed as a jar file : semantic_publishing_benchmark-*.jar accompanied by a datasets archive file : reference_knowledge_data.zip
The datasets file contains ontologies and reference datasets (required by the data-generator). Required set of configuration files : test.properties and definitions.properties (can also be found in the jar file) 



How to build the benchmark driver :
-----------------------------------------------------------------------------------------------------------------------------------------------

  Use the Ant with build.xml script. Default Ant task builds the jar and saves it to the 'dist' folder.
  Currently two versions of the Benchmark exist : a base version - containing a reduced query-mix with 9 queries and advanced version with 26 queries,
  use appropriate ant-tasks to build them, e.g.
  > ant build-base-querymix-standard //builds the standard benchmark driver compliant to SPARQL 1.1
  > ant build-full-querymix-standard //builds the standard benchmark driver compliant to SPARQL 1.1 with extended query mix
  > ant build-base-querymix-virtuoso //builds a custom version of the driver customized for Virtuoso's small deviation in SPARQL queries
  > ant build-full-querymix-virtuoso //builds a custom version of the driver customized for Virtuoso's small deviation in SPARQL queries with extended query mix



How to install the benchmark driver :
-----------------------------------------------------------------------------------------------------------------------------------------------

Save the distribution jar and reference knowledge data files to a folder of choice, then extract from both following items :
    - folder data/ from reference knowledge data file - contains required ontologies, knowledge reference data and query templates
    - additional reference datasets (see project ldbc_semanticpub_bm_additional_datasets) - all files of type .ttl and save to data/datasets folder    
    - file test.properties - configuration parameters for running the benchmark, found in distribution folder (also in the benchmark jar file)
    - file definitions.properties - configuration parameters on the benchmark generator, found in distribution folder (also in the benchmark jar file)



Benchmark Phases : 
-----------------------------------------------------------------------------------------------------------------------------------------------

  * The Semantic Publishing Benchmark can be configured to run through these phases ordered by the sequence they should be run : 

    - loadOntologies        		          		: load ontologies (from the 'data/ontologies' folder) into database
    - adjustRefDatasetsSizes    	        		: optional phase, if reference dataset files exist with the extension '.adjustablettl', then for each, a new .ttl file is created with adjusted size depending on the selected size of data to be generated (parameter 'datasetSize' in test.properties file).
    - loadDatasets          		          		: load the reference datasets (from the 'data/datasets' folder) into database
    - generateCreativeWorks 		          		: using uploaded data from previous two phases, generates Creative Works and saves them to files. Generated files need to be loaded into database manually (or automatically if file format is n-quads)
                                              			Note: Requires phases : loadOntologies, loadDatasets.
    - loadCreativeWorks	  		            		: load generated creative works into database (It is advisable to use serialization format : N-Quads)
    - generateQuerySubstitutionParameters 			: Controls generation of query substitution parameters which later can be used during the warmup and benchmark phases. For each query a substitution parameters file is created and saved into 'creativeWorksPath' location. 
                                              			Note : If no files are found at that location, queries executed during warmup and benchmark phases will use randomly generated parameters.
                                              			Note2: Requires phases : loadOntologies, loadDatasets, generateCreativeWorks, loadCreativeWorks.
    - validateQueryResults                			: validate correctness of results for editorial and aggregate operations against a validation dataset.
                                              			Note : Requires phases : loadOntologies, loadDatasets.
    - warmUp                		          		: a series of Aggregation queries are executed for a fixed amount of time.
    - benchmark             		          		: all aggregation and editorial agents are started and kept running for a period of 'benchmarkRunPeriodSeconds'.
    - benchmarkOnlineReplicationAndBackup 			: benchmark is measuring performance under currently ongoing backup process. Verifies that certain conditions are met such as milestone points at which backup has been started. 
                                            			Note : Requires phases : loadOntologies, loadDatasets, generateCreativeWorks, loadCreativeWorks, warmUp (optional). Phases that should be disabled : benchmark.
                                            			Note2: Requires all necessary enterprise script files (data/enterprise/scripts) to have DB Engie's commands added (Commands for : starting, shutting down, backing up, etc).
                                            			Note3: Required to set the full path for property 'enterpriseFeaturesPath' in test.properties file and all scripts need to have an execution permission enabled.  
    - checkConformance                    			: executes predefined queries (from folder 'data/sparql/conformance'. Checking for OWL2-RL : prp-irp, prp-asyp, prp-pdw, prp-adp, cax-dw, cax-adc, cls-maxc1, prp-key, prp-spo2, prp-inv1)  
                                              			Note : Requires phase : loadOntologies.
    - cleanup               		          		: optional, the benchmark can be set to clear all data from database
                                              			Note : all data will be erased from repository
  
    Each of those phases can be configured to run independently or in a sequence by setting appropriate property value in file : test.properties.
 
 
 
How to run the benchmark : 
-----------------------------------------------------------------------------------------------------------------------------------------------

  * Prepare and start a new RDF repository. 
  
    - Use rule-set : RDFS
    - Enable context indexing if available
    - Enable geo-spatial indexing if available
  
  * Configure the benchmark driver
  
      Edit file : test.properties, set values for :
  
    - ontologiesPath                    (ontologies path, e.g. "./data/ontologies")
    - referenceDatasetsPath             (reference dataset path, e.g. "./data/datasets")
    - creativeWorksPath                 (generated creative works path, e.g. "./data/generated")
    - queriesPath                       (queries path, e.g. "./data/sparql")
    - definitionsPath                   (definitions path, e.g. "./definitions.properties")
    - endpointURL                       (URL of endpoint, e.g. "http://localhost:8080/openrdf-sesame/repositories/ldbc1")
    - endpointUpdateURL                 (URL of endpoint for executing update queries, e.g. "http://localhost:8080/openrdf-sesame/repositories/ldbc1/statements")
	- wordnetPath						(WordNet path e.g. "C:/Program Files/WordNet/2.1/dict/")
	- rescalSampling					(files that are going to be used for the sampling phase)
    - datasetSize                       (target dataset size in triples, number of triples that will be generated by the data-generator)
    - allowSizeAdjustmentsOnDataModels  (allows the data generator to adjust the amount of correlations, clusterings and randomly generated models (Creative Works) in relation to the 'datasetSize', thus keeping a ratio of 1/3 for each in generated data. Default value is true.)
    - validationPath                    (location where generated and reference data related to validation phase is located)
    - generatedTriplesPerFile           (generated triples per file)
    - queryTimeoutSeconds               (query timeout)
    - systemQueryTimeoutSeconds			    (system queries timeout, default value 1h)
    - warmupPeriodSeconds               (warmup period)
    - benchmarkRunPeriodSeconds         (benchmark run period)
    - generateCreativeWorksFormat       (available options: TriG, TriX, N-Triples, N-Quads, N3, RDF/XML, RDF/JSON, Turtle)
    - aggregationAgents                 (aggregation agents count which will execute mix of aggregation queries concurrently. Query mix can be configured by changing 
                                         parameter aggregationOperationsAllocation in definitions.properties file)
    - editorialAgents                   (editorial agents count which will execute a mix of editorial queries concurrently. Query mix can be configured by changing
                                         parameter editorialOperationsAllocation in definitions.properties file)
    - dataGeneratorWorkers              (number of simultaneously working data generator threads)
    - generatorRandomSeed				        (use it to set the random set for the data generator (default value is 0). e.g. in cases when several benchmark drivers are started in separate
                                         processes to generate data - to be used with creativeWorkNextId parameter)
    - creativeWorkNextId                (set the next ID for the data generator of Creative Works. When running the benchmark driver in separate processes, to guarantee that generated
                                         creative works will not overlap their IDs
                                         e.g. for generating 50M dataset, expected number of Creative Works is ~2.5M and next ID should start at that value)
    - creativeWorksInfo                 (file name, that will be saved in creativeWorksPath and will contain system info about the generated dataset, e.g. interesting entities, etc.)
    - querySubstitutionParameters       (number substitution parameters that will be generated for each query)
    - benchmarkByQueryRuns				(sets the amount of aggregate queries which the benchmark phase will execute. If value is greater than zero then parameter 'benchmarkRunPeriodSeconds' is ignored. e.g. if set to 100, benchmark will measure the time to execute 100 aggregate operations.)
                                         
                                         Note : For optimal results the sum of editorial and aggregation agents should be set to be equal to the number of CPU cores.
	
      (Configure the benchmark phases. One, several or all phases can be enabled to run in a sequence. Running the first three phases is mandatory for the benchmark )
      
    - loadOntologies                    (populate the database with required ontologies, it is possible to manually upload the data stored in all .ttl files at /data/ontologies)
    - loadReferenceDatasets             (populate the database with required reference datasets, it is possible to manually upload the data stored in all .ttl files at /data/datasets)
    - generateCreativeWorks             (using already loaded ontologies and reference datasets, generate the benchmark data (Creative Works) into files)
    - loadCreativeWorks                 (load generated files with Creative Works into repository, optional, tested for N-Quads)
    - warmUp                            (runs the aggregation queries for a configured period of time)
    - runBenchmark                      (runs the benchmark - all aggregation and editorial agents run simultaneously)
    - runBenchmarkOnlineReplicationAndBackup (benchmark is measuring performance under currently ongoing backup process. Verifies that certain conditions are met such as milestone points at which backup has been started. Requires additional implementation of provided shell script files (/data/enterprise/scripts) for using vendor's specific command for backup.)     
    - checkConformance                  (executes a set of queries stored in 'data/sparql/conformance' for testing the inference capabilities of the database engine.
                                        OWL2-RL : prp-irp, prp-asyp, prp-pdw, prp-adp, cax-dw, cax-adc, cls-maxc1, prp-key, prp-spo2, prp-inv1.
                                        Note : execute -loadOntologies phase before running conformance check
    - clearDatabase                     (erases all triples from database)
	 
      Sample of a test.properties file can be found in the distribution jar file.

  * definitions.properties - currently pre-configured and no need to modify. Can be edited to tune various allocations, used in -generateCreativeWorks and -runBenchmark phases.
  
    - aboutsAllocations                 (Defines allocation amount of About tags in Creative Works)
    - mentionsAllocations               (Defines allocation amount of Mention tags in Creative Works)
    - entityPopularity                  (Defines popularity of an entity in the reference datasets)
    - usePopularEntities                (Defines allocation amount of popular entities to be used when tagging in Creative Works or in aggregation queries. Used for generation of Creative Works biased towards popular entities)
    - creativeWorkTypesAllocation       (Defines allocation amount of Creative Work Types : BlogPost, NewsItem, Programme)
    - aboutAndMentionsAllocation        (Defines allocation amount of about or mentions used for the main aggregation query (/data/sparql/aggregation/query1.txt), which one will be used more frequently)
    - editorialOperationsAllocation     (Defines allocation amount of queries in the editorial query mix that each editorial agent will execute. Query mix order : insert.txt, update.txt and delete.txt)
    - aggregationOperationsAllocation   (Defines allocation amount of queries in the aggregation query mix that each aggregation agent will execute. Query mix order : query1.txt, query2.txt... etc)
    - exponentialDecayUpperLimitOfCWs   (Defines the maximum number of creative works that an entity can be tagged about. Exponential decay function will start from the value defined)
    - exponentialDecayRate              (Defines the exponential decay rate. Used values to be in range 0.01 (for gentle slope) to 1 (for steep slope))
    - exponentialDecayThresholdPercent  (Defines the threshold in percents of exponential decay, below that threshold values will be ignored. Threshold is defined as the ratio of : currentExponentialDecayValue / exponentialDecayUpperLimitOfCWs. e.g. 5% threshold will be the value of 0.05)
    - majorEventsPerYear                (Defines the maximum number of 'major' events that could happen in one year. Each major event will be tagged by a number of Creative Works which will decay exponentially in time.)
    - minorEventsPerYear                (Defines the maximum number of 'minor' events that could happen in one year. Each minor event will be tagged by a number of Creative Works which will decay exponentially in time. Value of exponentialDecayUpperLimitOfCWs for minor events will be ten times smaller for them.)
    - seedYear                          (Defines a seed year that will be used for generating the Creative Works. Each Creative Work will have its creation date during that year. All date-range queries will use that value also.)
    - dataGenerationPeriodYears			(Defines the period (in years) of the gnerated data, starting from 'seedYear')
    - correlationsAmount				(Defines the amount of correlations that data generator will model between entities from reference knowledge data. Default value (50) will produce around 15 million triples.)
    - correlationsMagnitude				(Defines maximum amount of Creative Works that will be generated for a particular correlation in a single day.)
    - correlationDuration				(Defines the duration of correlation between two entities as a percent of the total data generation period. Default generation period is one year.)
    - correlationEntityLifespan			(Defines the life span of each entity that participates in a correlation as a percent of the total data generation period.)
    - minLat							(Defines minimum latitude, a geo-spatial property used to configure the geo-spatial search area of queries.)
	- maxLat							(Defines maximum latitude, a geo-spatial property.)
	- minLong							(Defines minimum longtitude, ,a geo-spatial property.)
	- maxLong							(Defines maximum longtitude, a geo-spatial property.)    
  	- mileStoneQueryPosition (Defines the position in terms of percents at which a milestone query is executed (related to Online and Replication Benchmark feature))
    
      Sample definitions.properties file can be found in the distribution jar file.

  * Example benchmark run command : 

  	  java -jar semantic_publishing_benchmark-*.jar test.properties
  	  
  	  Note: appropriate value for java maximum heap size may be required, e.g. -Xmx8G



Results of the benchmark :
-----------------------------------------------------------------------------------------------------------------------------------------------

  * Results are saved to three log files : 
  
    - semantic_publishing_benchmark_queries_brief.log 		- contains a brief information about each executed query, size of returned result, and time to execute.
    - semantic_publishing_benchmark_queries_detailed.log 	- contains a detailed log of each query and its result.
    - semantic_publishing_benchmark_results.log 			    - contains results from the the benchmark, saved each second during the run.
	
------------------------------------------------------------------------------------------------------------------------------------------------

The required dependencies for RESCAL are Numpy >= 1.3, SciPy >= 0.7.
WordNet is also required in order to change synonyms-antonyms in lexical transformation test cases.
