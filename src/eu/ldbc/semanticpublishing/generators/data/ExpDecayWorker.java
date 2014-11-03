package eu.ldbc.semanticpublishing.generators.data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
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
 * Each worker will be responsible for generating data for a single "phenomenon" in datasets.
 * e.g. starts with a given date and using exponential decay will produce Creative Works which will tag in
 * their about tags certain entity (URI)
 *
 */
public class ExpDecayWorker extends RandomWorker {
	private List<Long> exponentialDecayIerations;
	private Date startDate;
	private Entity entity;
	private long firstCwId;
	
	protected HashMap <String, String> URIMapping = new HashMap<String, String>();  /*as key we have the initial URI and as value the corresponding one (new or not)*/
	protected Map <String, Transformation> TransformationConf; //key: predicates value: type of transformation(if so)
	protected ArrayList<Double> FTransfArray;
	protected TransformationsCall Transformationscall; //key: id, value: every transformation(lexical, structural or logical)
	protected static RDFFormat rdfFormat;
	protected static Model sesameModel;
	protected static Model sesameModel_2;
	protected static Model sesameModel_GS;
	protected static ArrayList <Model> sesameModelArrayList;
	protected static ArrayList <Model> sesameModel2ArrayList;
	protected String fileName; 
	
	
	public ExpDecayWorker(List<Long> exponentialDecayIerations, long firstCwId, Date startDate, Entity entity, 
						  RandomUtil ru, Object lock, AtomicLong globalFilesCount, long triplesPerFile, long totalTriples, 
						  AtomicLong triplesGeneratedSoFar, String destinationPath, String serializationFormat, boolean silent) {
		super(ru, lock, globalFilesCount, totalTriples, triplesPerFile, triplesGeneratedSoFar, destinationPath, serializationFormat, silent);
		this.exponentialDecayIerations = exponentialDecayIerations;
		this.startDate = startDate;
		this.entity = entity;
		this.firstCwId = firstCwId;
		
		this.Transformationscall = new TransformationsCall(this);
		sesameModelArrayList = new ArrayList<Model>(); //ArrayList with all CWs in D1 as Models
		sesameModel2ArrayList = new ArrayList<Model>(); //ArrayList with all CWs in D2 as Models	
	}
	
	@Override
	public void execute() throws Exception {

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

		long currentFilesCount = filesCount.incrementAndGet();
		fileName = String.format(FILENAME_FORMAT + rdfFormat.getDefaultFileExtension(), destinationPath, File.separator, currentFilesCount);
		String fileName_2 = String.format(FILENAME_FORMAT_D2 + rdfFormat.getDefaultFileExtension(), D2destination, File.separator, currentFilesCount);
		String fileName_3 = String.format(FILENAME_FORMAT_GS + rdfFormat.getDefaultFileExtension(), GSdestination, File.separator, currentFilesCount);
		
		initializeFMapEntry();
				
		//skip data generation if targetTriples size has already been reached 
		if (triplesGeneratedSoFar.get() > targetTriples) {
			System.out.println(Thread.currentThread().getName() + " :: generated triples so far: " + String.format("%,d", triplesGeneratedSoFar.get()) + " have reached the targeted triples size: " + String.format("%,d", targetTriples) + ". Generating is cancelled");
			return;
		}
		
		long creativeWorksForCurrentIteration = 0;
		long iterationStep = 0;
		
		try {
			fos = new FileOutputStream(fileName);	
			fos_2 = new FileOutputStream(fileName_2);
			fos_3 = new FileOutputStream(fileName_3);
		

			for (int i = 0; i < exponentialDecayIerations.size(); i++) {

				creativeWorksForCurrentIteration = exponentialDecayIerations.get(i);
				iterationStep = i + 1;
				for (int j = 0; j < creativeWorksForCurrentIteration; j++) {
					if (currentTriplesCount >= triplesPerFile) {
						if (!silent && cwsInFileCount > 0) {
							System.out.println(Thread.currentThread().getName() + " " + this.getClass().getSimpleName() + " :: Saving file #" + currentFilesCount + " with " + String.format("%,d", cwsInFileCount) + " Creative Works. Generated triples so far: " + String.format("%,d", triplesGeneratedSoFar.get()) + ". Target: " + String.format("%,d", targetTriples) + " triples");
						
						}
							
						cwsInFileCount = 0;
						currentTriplesCount = 0;				

						currentFilesCount = filesCount.incrementAndGet();
						fileName = String.format(FILENAME_FORMAT + rdfFormat.getDefaultFileExtension(), destinationPath, File.separator, currentFilesCount);						
						fileName_2 = String.format(FILENAME_FORMAT_D2 + rdfFormat.getDefaultFileExtension(), D2destination, File.separator, currentFilesCount);
						fileName_3 = String.format(FILENAME_FORMAT_GS + rdfFormat.getDefaultFileExtension(), GSdestination, File.separator, currentFilesCount);
						
						initializeFMapEntry();
												
					}
					
					if (triplesGeneratedSoFar.get() > targetTriples) {
						return;
					}
					
					
					synchronized (lock) {	
						CreativeWorkBuilder creativeWorkBuilder = new CreativeWorkBuilder(firstCwId++, ru);
						creativeWorkBuilder.setDateIncrement(startDate, (int)iterationStep);
						creativeWorkBuilder.setAboutPresetUri(entity.getURI());
						creativeWorkBuilder.setUsePresetData(true);
						sesameModel = creativeWorkBuilder.buildSesameModel();
						//TODO 
						sesameModel_GS =  new LinkedHashModel();
						sesameModel_2 =  TransformateSesameModel(this,sesameModel_GS,fos_3);
						

					
					}
					
					
					Rio.write(sesameModel, fos, rdfFormat);
					Rio.write(sesameModel_2, fos_2, rdfFormat);
					
					cwsInFileCount++;
					currentTriplesCount += sesameModel.size();					

					triplesGeneratedSoFar.addAndGet(sesameModel.size());	
					
					sesameModelArrayList = new ArrayList<Model>();
					sesameModel2ArrayList = new ArrayList<Model>();
					
				}
				
			}
			
			
		} catch (RDFHandlerException e) {
			throw new IOException("A problem occurred while generating RDF data: " + e.getMessage());
		} catch (NoSuchElementException nse) {
			//reached the end of iteration, close file stream in finally section
		} finally {
			flushClose(fos);
		    fos_2.flush();
		    fos_3.flush();
			if (!silent && cwsInFileCount > 0) {
				System.out.println(Thread.currentThread().getName() + " " + this.getClass().getSimpleName() + " :: Saving file #" + currentFilesCount + " with " + String.format("%,d", cwsInFileCount) + " Creative Works. Generated triples so far: " + String.format("%,d", triplesGeneratedSoFar.get()) + ". Target: " + String.format("%,d", targetTriples) + " triples");
			}
		}
	}
	
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