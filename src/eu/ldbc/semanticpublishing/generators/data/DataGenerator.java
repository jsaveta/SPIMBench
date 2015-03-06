package eu.ldbc.semanticpublishing.generators.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;

import com.google.common.collect.Iterables;

import eu.ldbc.semanticpublishing.properties.Configuration;
import eu.ldbc.semanticpublishing.properties.Definitions;
import eu.ldbc.semanticpublishing.refdataset.DataManager;
import eu.ldbc.semanticpublishing.refdataset.model.Entity;
import eu.ldbc.semanticpublishing.transformations.CreateFinalGS;
import eu.ldbc.semanticpublishing.util.CosineUtil;
import eu.ldbc.semanticpublishing.util.DivergenceUtil;
import eu.ldbc.semanticpublishing.util.ExponentialDecayNumberGeneratorUtil;
import eu.ldbc.semanticpublishing.util.FileUtils;
import eu.ldbc.semanticpublishing.util.RandomUtil;

/**
 * The class responsible for managing data generation for the benchmark.
 * It is the entry point for any data generation related process.  
 *
 */
public class DataGenerator {
	private RandomUtil ru;
	private Configuration configuration;
	private Definitions definitions;
	private int generatorThreads = 1;
	private long triplesPerFile;
	private long targetedTriplesSize;
	private AtomicLong filesCount = new AtomicLong(0);
	private AtomicLong triplesGeneratedSoFar = new AtomicLong(0);
	private String destinationPath;
	private String serializationFormat;
	private static final long AWAIT_PERIOD_HOURS = 96; 
	private Object syncLock;
	
	//defines quotient for major events for 1M triples - number of major events per million triples
	private static final double EXP_DECAY_MAJOR_EVENTS_QT = 0.1;
	//defines quotient for minor events for 1M triples - number of minor events per million triples
	private static final double EXP_DECAY_MINOR_EVENTS_QT = 2;
	//defines quotient for correlations for 1M triples - number of correlations per million triples
	private static final double CORRELATIONS_QT = 1;
	
	public DataGenerator(RandomUtil ru, Configuration configuration, Definitions definitions, int generatorThreads, long totalTriples, long triplesPerFile, String destinationPath, String serializationFormat) {
		this.ru = ru;
		this.configuration = configuration;
		this.definitions = definitions;
		this.generatorThreads = generatorThreads;
		this.targetedTriplesSize = totalTriples;
		this.triplesPerFile = triplesPerFile;
		this.destinationPath = destinationPath;
		this.serializationFormat = serializationFormat;
		this.syncLock = this;
	}
	
	public void produceData() throws InterruptedException, IOException, RDFHandlerException, RDFParseException {
		produceData(true, true, true, true, false);		
	}
	
	public void produceData(boolean produceRandom, boolean produceClusterings, boolean produceCorrelations, boolean persistDatasetInfo, boolean silent) throws InterruptedException, IOException, RDFHandlerException, RDFParseException  {
		Map <String,ArrayList<Double>> Fmap = new HashMap<String,ArrayList<Double>>();
		long creativeWorksInDatabase = DataManager.creativeWorksNextId.get();
		
		List<Entity> correlatedEntitiesList = null;
		List<Entity> expDecayingMajorEntitiesList = null;
		List<Entity> expDecayingMinorEntitiesList = null;
		
		if (creativeWorksInDatabase > 0) {
			System.out.println("\t" + creativeWorksInDatabase + " Creative Works currently exist.");
		}

		//Adjust the amount of correlations and clusterings, in relation to the targeted triples size, keeping ratio of 1/3 for each of the motellings in generated data
		if (configuration.getBoolean(Configuration.ALLOW_SIZE_ADJUSTMENTS_ON_DATA_MODELS)) {
			adjustDataAllocations(targetedTriplesSize, definitions);
		}
		
		//create destination directory
		FileUtils.makeDirectories(this.destinationPath);
		
		ExecutorService executorService = null;
		executorService = Executors.newFixedThreadPool(generatorThreads);
		
		long spawnedRuSeed = ru.getSeed() + 1;
		long nextCwId = DataManager.creativeWorksNextId.get();
		
		long currentTime = System.currentTimeMillis();

		//Generate Correlations between entities
		int correlationsAmount = definitions.getInt(Definitions.CORRELATIONS_AMOUNT);
		
		if (produceCorrelations && correlationsAmount > 0) {
			correlatedEntitiesList = buildCorrelationsList(correlationsAmount);
			
			for (int i = 0; i < (correlatedEntitiesList.size() / 3);i++) {
				RandomUtil spawnedRu = ru.randomUtilFactory(spawnedRuSeed++);
				
				Entity entityA = correlatedEntitiesList.get(i * 3);
				Entity entityB = correlatedEntitiesList.get(i * 3 + 1);
				Entity entityC = correlatedEntitiesList.get(i * 3 + 2);		

				long generatedCWsByWorker = 0; 
				int dataGenerationPeriodYears = definitions.getInt(Definitions.DATA_GENERATOR_PERIOD_YEARS);
				int correlationsMagnitude = definitions.getInt(Definitions.CORRELATIONS_MAGNITUDE);
				double correlationEntityLifespanPercent = definitions.getDouble(Definitions.CORRELATION_ENTITY_LIFESPAN);
				double correlationDurationPercent = definitions.getDouble(Definitions.CORRELATIONS_DURATION);
				int totalCorrelationPeriodDays = (int) (365 * dataGenerationPeriodYears * (correlationEntityLifespanPercent * 2 - correlationDurationPercent));
				
				//initialize a list of correlations magnitudes for each day
				List<Integer> correlationsMagnitudesList = new ArrayList<Integer>();
				for (int j = 0; j < totalCorrelationPeriodDays; j++) {
					int nextRandom = spawnedRu.nextInt((int)(correlationsMagnitude * 0.75), correlationsMagnitude);
					generatedCWsByWorker += nextRandom;
					correlationsMagnitudesList.add(nextRandom);
				}
				
				nextCwId = DataManager.creativeWorksNextId.incrementAndGet();				
				DataManager.creativeWorksNextId.addAndGet(generatedCWsByWorker - 1);				
				CorrelationsWorker crw = new CorrelationsWorker(spawnedRu, entityA, entityB, entityC, nextCwId,  totalCorrelationPeriodDays, correlationsMagnitudesList, dataGenerationPeriodYears, 
															    correlationsMagnitude, correlationEntityLifespanPercent, correlationDurationPercent, syncLock, 
															    filesCount, targetedTriplesSize, triplesPerFile, triplesGeneratedSoFar, destinationPath, serializationFormat, silent);
				executorService.execute(crw);
				Fmap = crw.getFtransformations();
			}
		}
		
		ExponentialDecayNumberGeneratorUtil edgu;

		int exponentialDecayUpperLimitOfCws = definitions.getInt(Definitions.EXPONENTIAL_DECAY_UPPER_LIMIT_OF_CWS);
		
		//preinitialize
		if (definitions.getInt(Definitions.MAJOR_EVENTS_PER_YEAR) > 0) {
			expDecayingMajorEntitiesList = new ArrayList<Entity>();
			
			for (int i = 0; i < definitions.getInt(Definitions.MAJOR_EVENTS_PER_YEAR); i++) {
				Entity e = DataManager.popularEntitiesList.get(ru.nextInt(DataManager.popularEntitiesList.size()));
				expDecayingMajorEntitiesList.add(e);				
			}			
		}
		
		if (definitions.getInt(Definitions.MINOR_EVENTS_PER_YEAR) > 0) {
			expDecayingMinorEntitiesList = new ArrayList<Entity>();
			
			for (int i = 0; i < definitions.getInt(Definitions.MINOR_EVENTS_PER_YEAR); i++) {
				Entity e = DataManager.regularEntitiesList.get(ru.nextInt(DataManager.regularEntitiesList.size()));
				expDecayingMinorEntitiesList.add(e);
			}			
		}
		
		//Generate MAJOR EVENTS with exponential decay
		if (produceClusterings && definitions.getInt(Definitions.MAJOR_EVENTS_PER_YEAR) > 0) {			
			for (int i = 0; i < definitions.getInt(Definitions.MAJOR_EVENTS_PER_YEAR); i++) {
				edgu =  new ExponentialDecayNumberGeneratorUtil(/*ru.nextInt(1000, */exponentialDecayUpperLimitOfCws, 
							  									definitions.getDouble(Definitions.EXPONENTIAL_DECAY_RATE), 
							  									definitions.getDouble(Definitions.EXPONENTIAL_DECAY_THRESHOLD_PERCENT));
				RandomUtil spawnedRu = ru.randomUtilFactory(spawnedRuSeed++);
				Date startDate = spawnedRu.randomDateTime();
				Entity e = expDecayingMajorEntitiesList.get(i);
			
				nextCwId = DataManager.creativeWorksNextId.incrementAndGet();
				DataManager.creativeWorksNextId.addAndGet(edgu.calculateTotal() - 1);
				ExpDecayWorker edw = new ExpDecayWorker(edgu.produceIterationStepsList(), nextCwId, startDate, e, spawnedRu, syncLock, filesCount, triplesPerFile, targetedTriplesSize, triplesGeneratedSoFar, destinationPath, serializationFormat, silent);
				executorService.execute(edw);
				Fmap = edw.getFtransformations();
			}
		}

		//Generate MINOR EVENTS with exponential decay
		if (produceClusterings && definitions.getInt(Definitions.MINOR_EVENTS_PER_YEAR) > 0) {			
			for (int i = 0; i < definitions.getInt(Definitions.MINOR_EVENTS_PER_YEAR); i++) {
				edgu =  new ExponentialDecayNumberGeneratorUtil(/*ru.nextInt(1000,*/ exponentialDecayUpperLimitOfCws / 10, 
							  									definitions.getDouble(Definitions.EXPONENTIAL_DECAY_RATE), 
							  									definitions.getDouble(Definitions.EXPONENTIAL_DECAY_THRESHOLD_PERCENT));
				RandomUtil spawnedRu = ru.randomUtilFactory(spawnedRuSeed++);
				Date startDate = spawnedRu.randomDateTime();
				Entity e = expDecayingMinorEntitiesList.get(i);
				
				nextCwId = DataManager.creativeWorksNextId.incrementAndGet();
				DataManager.creativeWorksNextId.addAndGet(edgu.calculateTotal() - 1);
				ExpDecayWorker edw = new ExpDecayWorker(edgu.produceIterationStepsList(), nextCwId, startDate, e, spawnedRu, syncLock, filesCount, triplesPerFile, targetedTriplesSize, triplesGeneratedSoFar, destinationPath, serializationFormat, silent);				
				executorService.execute(edw);
				Fmap = edw.getFtransformations();
			}
		}

		//reset allocations back to initial state by setting back the initial random generator (CreativeWorksBuilder constructor will change random generator with each new instance)
		Definitions.reconfigureAllocations(ru.getRandom());

		//Generate random Creative Works to fill-in with rest of the generated data with randomly distributed tags of creative works, i.e. generate "noise"
		if (configuration.getBoolean(Configuration.USE_RANDOM_DATA_GENERATORS) == false) {
			System.out.println("* Skipping execution of GeneralWorkers in data generation, see test.properties parameter: useRandomDataGenerators");
		}
		if (produceRandom && (triplesGeneratedSoFar.get() < targetedTriplesSize) && configuration.getBoolean(Configuration.USE_RANDOM_DATA_GENERATORS)) {
			for (int i = 0; i < generatorThreads; i++) {				
				RandomWorker rw = new RandomWorker(ru, syncLock, filesCount, targetedTriplesSize, triplesPerFile, triplesGeneratedSoFar, destinationPath, serializationFormat, silent);
				executorService.execute(rw);
				Fmap = rw.getFtransformations();
			}
		}		
		executorService.shutdown();
		executorService.awaitTermination(AWAIT_PERIOD_HOURS, TimeUnit.HOURS);		
		
		//persist information about generated dataset
		String persistFilePath = DataManager.buildDataInfoFilePath(configuration);
		if (persistDatasetInfo && !persistFilePath.isEmpty()) {			
			DataManager.persistDatasetInfo(persistFilePath, correlatedEntitiesList, expDecayingMajorEntitiesList, expDecayingMinorEntitiesList);
		}
		// map that contains file as key and u,u', rescal and transf score as value

		ArrayList<Double> E = new ArrayList<Double>();
		for(int i = 0 ; i < 39; i++){
			E.add(0.0);
		}
		for (Entry<String, ArrayList<Double>> entry : Fmap.entrySet()) {
            ArrayList<Double> value = (ArrayList<Double>)entry.getValue();
            for(int i = 0 ; i < value.size(); i++){
        		double array_value = E.get(i) + value.get(i);
        		E.remove(i);
             	E.add(i, array_value);
            }
        }

		double sumE = 0d;
		for(int i = 0 ; i < E.size(); i++){
			double average_value = E.get(i)/(Fmap.size()-1);
			sumE += average_value;
			E.remove(i);
         	E.add(i, average_value);
		}
		
		
		//calculate square cosine between E[] and Fi[]
		CosineUtil cos = new CosineUtil();
		Map<String,Double> square_cos = cos.squareCosineSimilarityEF(Fmap,E);

		//calculate  Ek an average of the transformations for k files
		ArrayList<Double> Ek = new ArrayList<Double>();
		for(int i = 0 ; i < 39; i++){
			Ek.add(0.0);
		}
		if(Integer.parseInt(configuration.getString(Configuration.FILES_FOR_RESCAL_SAMPLING)) > 0){ //TODO also check if k is too low
			List<String> list = new ArrayList<String>();
			for (Entry<String, Double> cos_entry : square_cos.entrySet()) {
				if (list.size() > Integer.parseInt(configuration.getString(Configuration.FILES_FOR_RESCAL_SAMPLING)) -1) break;
				    else{ 
				    	for (Entry<String, ArrayList<Double>> entry : Fmap.entrySet()) {
				    		if(entry.getKey().endsWith(cos_entry.getKey().replace("GS", ""))){
					            ArrayList<Double> value = (ArrayList<Double>)entry.getValue();
					            for(int i = 0 ; i < value.size(); i++){
					        		double array_value = Ek.get(i) + value.get(i);
					        		Ek.remove(i);
					             	Ek.add(i, array_value);
					            }
					        	list.add(entry.getKey());
				    		}
				        }
				    }
			}
		}
		else{
			System.out.println("\t Please give as k a positive number as rescalSampling.");		
		}
		double sumEk = 0d;
		for(int i = 0 ; i < Ek.size(); i++){
			double average_value = Ek.get(i)/Integer.parseInt(configuration.getString(Configuration.FILES_FOR_RESCAL_SAMPLING));
			sumEk += average_value;
			Ek.remove(i);
         	Ek.add(i, average_value);
		}

		ArrayList<Double> ProbabilisticE = new ArrayList<Double>();
		for(int i = 0 ; i < E.size(); i++){
			ProbabilisticE.add(E.get(i)/sumE);
		}
		
		ArrayList<Double> ProbabilisticEk = new ArrayList<Double>();
		for(int i = 0 ; i < Ek.size(); i++){
			ProbabilisticEk.add(Ek.get(i)/sumEk);
		}
		ArrayList<Double> ProbabilisticEandEkAverage = new ArrayList<Double>();
		for(int i = 0 ; i < Ek.size(); i++){
			ProbabilisticEandEkAverage.add((ProbabilisticEk.get(i) + ProbabilisticE.get(i))/2);
		}
		
		double js = DivergenceUtil.jsDivergence(ProbabilisticEk, ProbabilisticE, ProbabilisticEandEkAverage);
		System.out.println("\n\tIs suggested to select more than 2 files for rescalSampling.\n");	
		if(Integer.parseInt(configuration.getString(Configuration.FILES_FOR_RESCAL_SAMPLING)) > 0){ 
			long triplesPerFile = configuration.getLong(Configuration.GENERATED_TRIPLES_PER_FILE);
			long totalTriples = configuration.getLong(Configuration.DATASET_SIZE_TRIPLES);
			int files = (int) (totalTriples/triplesPerFile);
			//double k = (0.1 * Integer.parseInt(configuration.getString(Configuration.FILES_FOR_RESCAL_SAMPLING)))/js;
			double k = js*files + Integer.parseInt(configuration.getString(Configuration.FILES_FOR_RESCAL_SAMPLING)); //TODO fix this!
			
			int times = Integer.parseInt(configuration.getString(Configuration.FILES_FOR_RESCAL_SAMPLING));
			
			if((int)k <= Integer.parseInt(configuration.getString(Configuration.FILES_FOR_RESCAL_SAMPLING))){
				System.out.println("\tThe rescalSampling you chose is satisfactory.");
			}
			else{
				if(k>files) k = files;
				System.out.println("\tThe suggested rescalSampling is : " + (int)k); 
				times = (int)k; 
			}
			if(Integer.parseInt(configuration.getString(Configuration.FILES_FOR_RESCAL_SAMPLING)) > files){
				times = files;
			}
			List<String> list = new ArrayList<String>();
			if(Integer.parseInt(configuration.getString(Configuration.FILES_FOR_RESCAL_SAMPLING)) > files){
				times = files;
			}
			for (Entry<String, Double> entry : square_cos.entrySet()) {
			  if (list.size() > times -1) {CreateFinalGS.writeFinalGSFiles(); break;}
			  else{  
				  list.add(entry.getKey());
				  try {
					  CreateFinalGS.GSScores(entry.getKey());
				  } catch (RDFParseException e) {
					throw new RDFParseException("RDFParseException " + e.getMessage());
				}
			  }
			}
		}
		else{
			System.out.println("\n\tPlease give a number greater than 2 for rescalSampling.\n");			
		}
	
		System.out.println("\tcompleted! Total Creative Works created : " + String.format("%,d", (DataManager.creativeWorksNextId.get() - creativeWorksInDatabase)) + ". Time : " + (System.currentTimeMillis() - currentTime) + " ms");		
	}
	
	private synchronized ArrayList<Entity> buildCorrelationsList(int correlationsAmount) {
		ArrayList<Entity> arrayList = new ArrayList<Entity>();
		
		for (int i = 0; i < correlationsAmount; i++) {			
			//First main entity in correlation
			Entity e = DataManager.popularEntitiesList.get(ru.nextInt(DataManager.popularEntitiesList.size()));
			arrayList.add(e);
			//Second main entity in correlation
			e = DataManager.popularEntitiesList.get(ru.nextInt(DataManager.popularEntitiesList.size()));
			arrayList.add(e);
			//Third entity which participates sparsely in the correlation period
			e = DataManager.regularEntitiesList.get(ru.nextInt(DataManager.regularEntitiesList.size()));
			arrayList.add(e);
		}
		
		return arrayList;
	}
	
	/**
	 * The method would dynamically 'adjust' the amount of generated data with Clusterings, Correlations and Random Workers depending on the 
	 * targeted triples size. Currently ratio of the three types of modeled data (clusterings, correlations, random) is intended to be 33% : 33% : 33%
	 * 
	 * @param targetTriples - targeted triples size to be generated
	 * @param definitions - definitions properties
	 */
	private void adjustDataAllocations(long targetTriples, Definitions definitions) {
		long majorEvents = (int)(EXP_DECAY_MAJOR_EVENTS_QT * (targetedTriplesSize / 1000000)) > 0 ? (int)(EXP_DECAY_MAJOR_EVENTS_QT * (targetedTriplesSize / 1000000)) : 0;
		long minorEvents = (int)(EXP_DECAY_MINOR_EVENTS_QT * (targetedTriplesSize / 1000000)) > 0 ? (int)(EXP_DECAY_MINOR_EVENTS_QT * (targetedTriplesSize / 1000000)) : 0;
		long correlations = (int)(CORRELATIONS_QT * (targetedTriplesSize / 1000000)) > 0 ? (int)(CORRELATIONS_QT * (targetedTriplesSize / 1000000)) : 0;
		
		definitions.setLong(Definitions.MAJOR_EVENTS_PER_YEAR, majorEvents);
		definitions.setLong(Definitions.MINOR_EVENTS_PER_YEAR, minorEvents);
		definitions.setLong(Definitions.CORRELATIONS_AMOUNT, correlations);
	}
}
