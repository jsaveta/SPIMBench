package eu.ldbc.semanticpublishing.generators.data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.openrdf.model.Model;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.Rio;

import eu.ldbc.semanticpublishing.generators.data.sesamemodelbuilders.CreativeWorkBuilder;
import eu.ldbc.semanticpublishing.refdataset.model.Entity;
import eu.ldbc.semanticpublishing.transformations.Transformation;
import eu.ldbc.semanticpublishing.transformations.TransformationsCall;
import eu.ldbc.semanticpublishing.util.RandomUtil;
import eu.ldbc.semanticpublishing.util.SesameUtils;

/**
 * A class for generating Creative Works containing correlations between entities.
 * Currently implemented are correlations between two popular entities and a third entity.
 *
 */
public class CorrelationsWorker extends RandomWorker {

	//first entity to participate in the correlation
	private Entity entityA;
	//second entity to participate in the correlation
	private Entity entityB;
	//third entity to participate in a sparse manner to the correlation - it will participate in a separate correlation separately with A and B and very sparsely with A and B during their overlap
	private Entity entityC;
	
	private long firstCwId;
	private List<Integer> correlationsMagnitudesForSingleIterationList;
	private int dataGenerationPeriodYears = 1;
	private int correlationsMagnitude = 10;
	private int totalCorrelationPeriodDays = 1;
	private double correlationEntityLifespanPercent = 0.4;
	private double correlationDurationPercent = 0.1;
	
	//a distance in days between third entity appearance in a correlation
	private static final int THRID_ENTITY_CORRELATION_DISTANCE = 9;
	
	protected HashMap <String, String> URIMapping = new HashMap<String, String>();  /*as key we have the initial URI and as value the corresponding one (new or not)*/
	protected Map <String, Transformation> TransformationConf; //key: predicates value: type of transformation(if so)
	protected ArrayList<Double> FTransfArray;// = new ArrayList<Integer>(); 
	protected TransformationsCall Transformationscall; //key: id, value: every transformation(lexical, structural or logical)
	protected static RDFFormat rdfFormat;
	protected static Model sesameModel;
	protected static Model sesameModel_2;
	protected static Model sesameModel_GS;
	protected static ArrayList <Model> sesameModelArrayList;
	protected static ArrayList <Model> sesameModel2ArrayList;
	protected String fileName; 
	
	public CorrelationsWorker(RandomUtil ru, Entity entityA, Entity entityB, Entity entityC, long firstCwId, int totalCorrelationPeriodDays, List<Integer> correlationsMagnitudesForSingleIterationList, int dataGenerationPeriodYears, int correlationsMagnitude, 
							  double correlationEntityLifespan, double correlationDuration, Object lock, AtomicLong filesCount, 
							  long totalTriples, long triplesPerFile, AtomicLong triplesGeneratedSoFar, String destinationPath, String serializationFormat, boolean silent) {
		super(ru, lock, filesCount, totalTriples, triplesPerFile, triplesGeneratedSoFar, destinationPath, serializationFormat, silent);
		this.entityA = entityA;
		this.entityB = entityB;
		this.entityC = entityC;
		this.firstCwId = firstCwId;
		this.totalCorrelationPeriodDays = totalCorrelationPeriodDays;
		this.correlationsMagnitudesForSingleIterationList = correlationsMagnitudesForSingleIterationList;
		this.dataGenerationPeriodYears = dataGenerationPeriodYears;
		this.correlationsMagnitude = correlationsMagnitude;
		this.correlationEntityLifespanPercent = correlationEntityLifespan;
		this.correlationDurationPercent = correlationDuration;
		
		this.Transformationscall = new TransformationsCall(this);
		sesameModelArrayList = new ArrayList<Model>(); //ArrayList with all CWs in D1 as Models
		sesameModel2ArrayList = new ArrayList<Model>(); //ArrayList with all CWs in D2 as Models
	}

	@Override
	public void execute() throws Exception {
		
		//skip data generation if targetTriples size has already been reached 
		if (triplesGeneratedSoFar.get() > targetTriples) {
			System.out.println(Thread.currentThread().getName() + " :: generated triples so far: " + String.format("%,d", triplesGeneratedSoFar.get()) + " have reached the targeted triples size: " + String.format("%,d", targetTriples) + ". Generating is cancelled");
			return;
		}
		FileOutputStream fos = null;
		FileOutputStream fos_2 = null;
		FileOutputStream fos_3 = null;
		rdfFormat = SesameUtils.parseRdfFormat(serializationFormat);

		String D2destination = "generatedD2"; // main location for uploads
		String GSdestination = "generatedGS"; // main location for uploads
        File theFile = new File(D2destination); 
        theFile.mkdirs();// will create a folder for the transformed data if not exists
        File theFilegs = new File(GSdestination); 
        theFilegs.mkdirs();// will create a folder for the transformed data if not exists
        
        
        int cwsInFileCount = 0;
		int currentTriplesCount = 0;
		int thirdEntityCountdown = 0;
		int thirdEntityOutsideCorrelationCountdown = 0;
		int correlationsMagnitudeForIteration = this.correlationsMagnitude;
		long currentFilesCount = filesCount.incrementAndGet();	
		//System.out.println("filesCount from correlation worker : "+ filesCount);
		
		fileName = String.format(FILENAME_FORMAT + rdfFormat.getDefaultFileExtension(), destinationPath, File.separator, currentFilesCount);
		String fileName_2 = String.format(FILENAME_FORMAT_D2 + rdfFormat.getDefaultFileExtension(), D2destination, File.separator, currentFilesCount);
		String fileName_3 = String.format(FILENAME_FORMAT_GS + rdfFormat.getDefaultFileExtension(), GSdestination, File.separator, currentFilesCount);
	//	fileName_4 = String.format(FILENAME_FORMAT_SIMPLEGS + rdfFormat.getDefaultFileExtension(), GSdestination, File.separator, currentFilesCount);
		
		initializeFMapEntry();

		
		Date startDate;
		int thirdEntityInCorrelationOccurences = (int) ((365 * dataGenerationPeriodYears * correlationDurationPercent) / 10);
		int thirdEntityOutsideCorrelationOccurences = (int) ((365 * dataGenerationPeriodYears * (correlationEntityLifespanPercent * 2 - correlationDurationPercent)) / 10) / 2;
		
		fos = new FileOutputStream(fileName);	
		fos_2 = new FileOutputStream(fileName_2);
		fos_3 = new FileOutputStream(fileName_3);
		
//		File file_4 = new File(fileName_4); 
//		// if file does not exist, then create it
//		if (!file_4.exists()) {
//			file_4.createNewFile();
//		}
//		FileWriter fw_4 = new FileWriter(file_4.getAbsoluteFile());
//		simplegoldStandard = new BufferedWriter(fw_4);
		
		//pick a random date starting from 1.Jan to the value of totalCorrelationPeriodDays
		startDate = ru.randomDateTime(365 * dataGenerationPeriodYears - totalCorrelationPeriodDays);
		thirdEntityCountdown = ru.nextInt((int)(THRID_ENTITY_CORRELATION_DISTANCE * 0.6), THRID_ENTITY_CORRELATION_DISTANCE);
		thirdEntityOutsideCorrelationCountdown = ru.nextInt((int)(THRID_ENTITY_CORRELATION_DISTANCE * 0.6), THRID_ENTITY_CORRELATION_DISTANCE) / 2;
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(startDate);
		try {
			for (int dayIncrement = 0; dayIncrement < totalCorrelationPeriodDays; dayIncrement++) {
				correlationsMagnitudeForIteration = correlationsMagnitudesForSingleIterationList.get(dayIncrement);
				
				boolean thirdEntityForCurrentDaySet = false;				
				
				//generate Creative Works with correlations for that day
				for (int i = 0; i < correlationsMagnitudeForIteration; i++) {
					if (currentTriplesCount >= triplesPerFile) {						
						flushClose(fos);
						if (!silent && cwsInFileCount > 0) {
							System.out.println(Thread.currentThread().getName() + " " + this.getClass().getSimpleName() + " :: Saving file #" + currentFilesCount + " with " + String.format("%,d", cwsInFileCount) + " Creative Works. Generated triples so far: " + String.format("%,d", triplesGeneratedSoFar.get()) + ". Target: " + String.format("%,d", targetTriples) + " triples");
						}
	
						cwsInFileCount = 0;
						currentTriplesCount = 0;
						
						currentFilesCount = filesCount.incrementAndGet();
						//System.out.println("filesCount from correlation worker : "+ filesCount);
						
						fileName = String.format(FILENAME_FORMAT + rdfFormat.getDefaultFileExtension(), destinationPath, File.separator, currentFilesCount);
						fileName_2 = String.format(FILENAME_FORMAT_D2 + rdfFormat.getDefaultFileExtension(), D2destination, File.separator, currentFilesCount);
						fileName_3 = String.format(FILENAME_FORMAT_GS + rdfFormat.getDefaultFileExtension(), GSdestination, File.separator, currentFilesCount);
						//fileName_4 = String.format(FILENAME_FORMAT_SIMPLEGS + rdfFormat.getDefaultFileExtension(), GSdestination, File.separator, currentFilesCount);
					
						initializeFMapEntry();
						
						fos = new FileOutputStream(fileName);	
						fos_2 = new FileOutputStream(fileName_2);
						fos_3 = new FileOutputStream(fileName_3);
						
//						file_4 = new File(fileName_4); 
//						// if file does not exist, then create it
//						if (!file_4.exists()) {
//							file_4.createNewFile();
//						}
//						fw_4 = new FileWriter(file_4.getAbsoluteFile());
//						simplegoldStandard = new BufferedWriter(fw_4);										
					}
					
					if (triplesGeneratedSoFar.get() > targetTriples) {
						return;
					}
					
					synchronized(lock) {
						if (dayIncrement < 365 * dataGenerationPeriodYears * (correlationEntityLifespanPercent - correlationDurationPercent)) {
							if ((thirdEntityOutsideCorrelationCountdown <= 0) && (thirdEntityOutsideCorrelationOccurences > 0) && !thirdEntityForCurrentDaySet) {
								sesameModel = buildCreativeWorkModel(entityA, entityC, null, firstCwId++, true, calendar.getTime(), 0);
								thirdEntityForCurrentDaySet = true;
								thirdEntityOutsideCorrelationOccurences--;
							} else {
								sesameModel = buildCreativeWorkModel(entityA, null, null, firstCwId++, true, calendar.getTime(), 0);
							}
						} else if ((dayIncrement >= 365 * dataGenerationPeriodYears * (correlationEntityLifespanPercent - correlationDurationPercent)) && dayIncrement < (365 * dataGenerationPeriodYears * (correlationEntityLifespanPercent))) {
							//reset for the last third of correlation period
							thirdEntityOutsideCorrelationOccurences = (int) ((365 * dataGenerationPeriodYears * (correlationEntityLifespanPercent * 2 - correlationDurationPercent)) / 10) / 2;
							
							if ((thirdEntityCountdown <= 0) && (thirdEntityInCorrelationOccurences > 0) && !thirdEntityForCurrentDaySet) {
								//introduce a third entity correlation in a tiny amount of all correlations
								sesameModel = buildCreativeWorkModel(entityA, entityB, entityC, firstCwId++, true, calendar.getTime(), 0);
								thirdEntityForCurrentDaySet = true;
								thirdEntityInCorrelationOccurences--;
								thirdEntityCountdown = ru.nextInt((int)(THRID_ENTITY_CORRELATION_DISTANCE * 0.6), THRID_ENTITY_CORRELATION_DISTANCE);
							} else {
								sesameModel = buildCreativeWorkModel(entityA, entityB, null, firstCwId++, true, calendar.getTime(), 0);
							}
						} else if (dayIncrement >= 365 * dataGenerationPeriodYears * correlationEntityLifespanPercent) {
							if ((thirdEntityOutsideCorrelationCountdown <= 0) && (thirdEntityOutsideCorrelationOccurences > 0) && !thirdEntityForCurrentDaySet) {
								sesameModel = buildCreativeWorkModel(entityB, entityC, null, firstCwId++, true, calendar.getTime(), 0);
								thirdEntityForCurrentDaySet = true;
								thirdEntityOutsideCorrelationOccurences--;
							} else {
								sesameModel = buildCreativeWorkModel(entityB, null, null, firstCwId++, true, calendar.getTime(), 0);
							}
						} else {
							sesameModel = buildCreativeWorkModel(entityA, entityC, null, firstCwId++, true, calendar.getTime(), 0);
							if (!silent) {
								System.out.println(Thread.currentThread().getName() + " :: Warning : Unexpected stage in data generation reached, defaulting");
							}
						}
						//TODO 
						sesameModel_GS = new LinkedHashModel();
						sesameModel_2 =  TransformateSesameModel(this,sesameModel_GS,fos_3);
						

					
					}
					
					Rio.write(sesameModel, fos, rdfFormat);
					Rio.write(sesameModel_2, fos_2, rdfFormat);
					//Rio.write(sesameModel_GS, fos_3, rdfFormat);
					
					cwsInFileCount++;
					currentTriplesCount += sesameModel.size();
					//edw na valw to athroisma olwn twn model
					
					triplesGeneratedSoFar.addAndGet(sesameModel.size());				
				}


				sesameModel_2 =  new LinkedHashModel();
				sesameModel_GS =  new LinkedHashModel();
			//	System.out.println("correlation");
				InsertSameAsOrDifferentFromCW(this,sesameModel_GS,fos_3);
				Rio.write(sesameModel_2, fos_2, rdfFormat);

		
				sesameModelArrayList = new ArrayList<Model>();
				sesameModel2ArrayList = new ArrayList<Model>();
				
				//System.out.println("FTransfArray CORRELATION" + FTransfArray);
				//System.out.println("Ftransformations "+ Ftransformations);
				
				thirdEntityCountdown--;
				thirdEntityOutsideCorrelationCountdown--;
				calendar.add(Calendar.DAY_OF_YEAR, 1);
			}
			
			
		
		} catch(RDFHandlerException e) {
			throw new IOException("A problem occurred while generating RDF data: " + e.getMessage());
		} finally {
			flushClose(fos);
			flushClose(fos_2);
			flushClose(fos_3);
			//fw_4.flush();
			//fw_4.close();
			if (!silent && cwsInFileCount > 0) {
				System.out.println(Thread.currentThread().getName() + " " + this.getClass().getSimpleName() + " :: Saving file #" + currentFilesCount + " with " + String.format("%,d", cwsInFileCount) + " Creative Works. Generated triples so far: " + String.format("%,d", triplesGeneratedSoFar.get()) + ". Target: " + String.format("%,d", targetTriples) + " triples");
			}
		}
	}
	
	private Model buildCreativeWorkModel(Entity a, Entity b, Entity c, long firstCwId, boolean aboutOrMentionsB, Date startDate, int dayIncrement) {
		CreativeWorkBuilder creativeWorkBuilder = new CreativeWorkBuilder(firstCwId, ru);
		creativeWorkBuilder.setDateIncrement(startDate, dayIncrement);
		creativeWorkBuilder.setAboutPresetUri(a.getURI());
		if (b != null) {
			if (aboutOrMentionsB) {
				creativeWorkBuilder.setOptionalAboutPresetUri(b.getURI());
			} else {
				creativeWorkBuilder.setMentionsPresetUri(b.getURI());
			}
		}
		if (c != null) {
			creativeWorkBuilder.setOptionalMentionsPresetUri(c.getURI());
		}
		creativeWorkBuilder.setUsePresetData(true);
		return creativeWorkBuilder.buildSesameModel();
	}
	
//	@Override
//	public String getFileName_4() {
//		return fileName_4;
//	}
//	@Override
//	public void addStatementSesameModel_GS(Resource transf, URI weight_, Value weight_num, Resource resource) {
//		sesameModel_GS.add(transf,weight_,weight_num,resource);
//	}

//	@Override
//	public BufferedWriter getSimplegoldStandard() {
//		return simplegoldStandard;
//	}
	public void initializeFMapEntry(){
		if(!getFtransformations().containsKey(fileName)){
			FTransfArray = new ArrayList<Double>();
			for(int i=0; i <39; i++){
				FTransfArray.add(0.0);
			}	
			this.getFtransformations().put(fileName, FTransfArray);
				
		}
	}

	@Override
	public Model getSesameModel() {
		return sesameModel;
	}
	@Override
	public Model getSesameModel_2() {
		return sesameModel_2;
	}
	@Override
	public Model getSesameModel_GS() {
		return sesameModel_GS;
	}
//	@Override
//	public WriteGoldStandard getWriteGoldStandard() {
//		return writegs;
//	}

	@Override
	public RandomUtil getru() {
		return ru;
	}
	@Override
	public ArrayList<Model> getsesameModelArrayList() {
		return sesameModelArrayList;
	}
	@Override
	public ArrayList<Model> getsesameModel2ArrayList() {
		return sesameModel2ArrayList;
	}
	@Override
	public HashMap<String, String> getURIMapping() {
		return URIMapping;
	}
	@Override
	public Map<String, Transformation> getTransformationConf() {
		return TransformationConf;
	}
	@Override
	public String getFileName1() {
		return fileName;
	}
}