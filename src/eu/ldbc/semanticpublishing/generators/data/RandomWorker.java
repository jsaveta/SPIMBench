package eu.ldbc.semanticpublishing.generators.data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.Rio;

import eu.ldbc.semanticpublishing.generators.data.sesamemodelbuilders.CreativeWorkBuilder;
import eu.ldbc.semanticpublishing.transformations.Transformation;
import eu.ldbc.semanticpublishing.transformations.TransformationsCall;
import eu.ldbc.semanticpublishing.transformations.WriteIntermediateGS;
import eu.ldbc.semanticpublishing.util.RandomUtil;
import eu.ldbc.semanticpublishing.util.SesameUtils;

/**
 * A class for generating Creative Works using components for serializing from the Sesame. 
 *
 */
public class RandomWorker extends AbstractAsynchronousWorker{
	
	protected long targetTriples;
	protected long triplesPerFile;
	protected long totalTriplesForWorker;
	protected String destinationPath;
	protected String serializationFormat;
	protected AtomicLong filesCount;
	protected AtomicLong triplesGeneratedSoFar;
	protected RandomUtil ru;
	protected Object lock;
	protected boolean silent;
	
	protected HashMap <String, String> URIMapping = new HashMap<String, String>(); /*as key we have the initial URI and as value the corresponding one (new or not)*/
	protected ArrayList<Double> FTransfArray;
	protected Map <String, Transformation> TransformationConf; //key: predicates value: type of transformation(if so)
	protected TransformationsCall Transformationscall; //key: id, value: every transformation(lexical, structural or logical)
	protected static RDFFormat rdfFormat;
	protected static Model sesameModel;
	protected static Model sesameModel_2;
	protected static Model sesameModel_GS;
	protected static  ArrayList <Model> sesameModelArrayList;
	protected static  ArrayList <Model> sesameModel2ArrayList;
	protected String fileName;
	

	public RandomWorker(RandomUtil ru, Object lock, AtomicLong filesCount, long totalTriples, long triplesPerFile, AtomicLong triplesGeneratedSoFar, String destinationPath, String serializationFormat, boolean silent) {
		this.ru = ru;
		this.lock = lock;
		this.targetTriples = totalTriples;
		this.filesCount = filesCount;
		this.triplesPerFile = triplesPerFile;
		this.triplesGeneratedSoFar = triplesGeneratedSoFar;
		this.destinationPath = destinationPath;
		this.serializationFormat = serializationFormat;
		this.silent = silent;
		
		this.Transformationscall = new TransformationsCall(this);
		//writegs = new WriteGoldStandard(this);
		sesameModelArrayList = new ArrayList<Model>(); //ArrayList with all CWs in D1 as Models
		sesameModel2ArrayList = new ArrayList<Model>(); //ArrayList with all CWs in D2 as Models

	}
	public RandomWorker() {
		// TODO Auto-generated constructor stub
	}
	@Override
	public void execute() throws Exception {
		FileOutputStream fos = null;
		FileOutputStream fos_2 = null;
		FileOutputStream fos_3 = null;
		rdfFormat = SesameUtils.parseRdfFormat(serializationFormat);

		long currentFilesCount = filesCount.incrementAndGet();
		String D2destination = "generatedD2"; // main location for uploads
		String GSdestination = "generatedGS"; // main location for uploads
        File theFile = new File(D2destination); 
        theFile.mkdirs();// will create a folder for the transformed data if not exists
        File theFilegs = new File(GSdestination); 
        theFilegs.mkdirs();// will create a folder for the transformed data if not exists
        
        

        
		fileName = String.format(FILENAME_FORMAT + rdfFormat.getDefaultFileExtension(), destinationPath, File.separator, currentFilesCount);
		String fileName_2 = String.format(FILENAME_FORMAT_D2 + rdfFormat.getDefaultFileExtension(), D2destination, File.separator, currentFilesCount);
		String fileName_3 = String.format(FILENAME_FORMAT_GS + rdfFormat.getDefaultFileExtension(), GSdestination, File.separator, currentFilesCount);
	
		initializeFMapEntry();
		
		int cwsInFileCount = 0;
		int currentTriplesCount = 0;
		
		//skip data generation if targetTriples size has already been reached 
		if (triplesGeneratedSoFar.get() > targetTriples) {
			System.out.println(Thread.currentThread().getName() + " :: generated triples so far: " + String.format("%,d", triplesGeneratedSoFar.get()) + " have reached the targeted triples size: " + String.format("%,d", targetTriples) + ". Generating is cancelled");
			return;
		}
		
		//loop until the generated triples have reached the targeted totalTriples size
		while (true) {
			if (triplesGeneratedSoFar.get() > targetTriples) {					
				break;
			}
			
			try {
					
				fos = new FileOutputStream(fileName);	
				fos_2 = new FileOutputStream(fileName_2);
				fos_3 = new FileOutputStream(fileName_3);

				while (true) {
					if (currentTriplesCount > triplesPerFile) {
						break;
					}					

					if (triplesGeneratedSoFar.get() > targetTriples) {					
						break;
					}
					//using a synchronized block, to guarantee the exactly equal generated data no matter the number of threads
					synchronized(lock) {							
						CreativeWorkBuilder creativeWorkBuilder = new CreativeWorkBuilder("", ru);
						sesameModel = creativeWorkBuilder.buildSesameModel();
					
						//TODO 
						sesameModel_GS = new LinkedHashModel();
						sesameModel_2 =  TransformateSesameModel(this,sesameModel_GS,fos_3);
						
						
			
					}
					
					Rio.write(sesameModel, fos, rdfFormat);
					Rio.write(sesameModel_2, fos_2, rdfFormat);
					
					cwsInFileCount++;
					currentTriplesCount += sesameModel.size();											
					triplesGeneratedSoFar.addAndGet(sesameModel.size());		
					
				}
				
				
				sesameModel_2 =  sesameModel;//new LinkedHashModel();
				sesameModel_GS =  new LinkedHashModel();
				InsertSameAsOrDifferentFromCW(this,sesameModel_GS,fos_3);
				Rio.write(sesameModel_2, fos_2, rdfFormat);

		
				sesameModelArrayList = new ArrayList<Model>();
				sesameModel2ArrayList = new ArrayList<Model>();

				flushClose(fos);
				flushClose(fos_2);
				flushClose(fos_3);

				if (!silent && cwsInFileCount > 0) {
					System.out.println(Thread.currentThread().getName() + " " + this.getClass().getSimpleName() + " :: Saving file #" + currentFilesCount + " with " + String.format("%,d", cwsInFileCount) + " Creative Works. Generated triples so far: " + String.format("%,d", triplesGeneratedSoFar.get()) + ". Target: " + String.format("%,d", targetTriples) + " triples");
				}

				cwsInFileCount = 0;
				currentTriplesCount = 0;

				currentFilesCount = filesCount.incrementAndGet();
				fileName = String.format(FILENAME_FORMAT + rdfFormat.getDefaultFileExtension(), destinationPath, File.separator, currentFilesCount);
				fileName_2 = String.format(FILENAME_FORMAT_D2 + rdfFormat.getDefaultFileExtension(), D2destination, File.separator, currentFilesCount);
				fileName_3 = String.format(FILENAME_FORMAT_GS + rdfFormat.getDefaultFileExtension(), GSdestination, File.separator, currentFilesCount);		
				
				initializeFMapEntry(); //do not remove this
			
			} catch (RDFHandlerException e) {
				flushClose(fos);
				flushClose(fos_2);
				flushClose(fos_3);
				
				throw new IOException("A problem occurred while generating RDF data: " + e.getMessage());
			}
			
			
			
		}
	}
	
	protected synchronized void flushClose(FileOutputStream fos) throws IOException {
		if (fos != null) {
			fos.flush();
			fos.close();
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

