package eu.ldbc.semanticpublishing.transformations;



import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.Rio;
import org.openrdf.rio.UnsupportedRDFormatException;
import org.openrdf.rio.helpers.StatementCollector;
import org.openrdf.rio.turtle.TurtleParser;

import Jama.Matrix;
import eu.ldbc.semanticpublishing.generators.data.sesamemodelbuilders.SesameBuilder;
import eu.ldbc.semanticpublishing.rescal.turtleRescalTripleFinder.RescalStarter;
import eu.ldbc.semanticpublishing.util.CleanZeros;
import eu.ldbc.semanticpublishing.util.FileUtils;
import eu.ldbc.semanticpublishing.util.Matrices;
import eu.ldbc.semanticpublishing.util.SesameUtils;


/*transformationsMap.put("1", "BlankCharsAddition");
transformationsMap.put("2", "BlankCharsDeletion");
transformationsMap.put("3", "RandomCharsAddition");
transformationsMap.put("4", "RandomCharsDeletion");
transformationsMap.put("5", "RandomCharsModifier");
transformationsMap.put("6", "TokenAddition");
transformationsMap.put("7", "TokenDeletion");
transformationsMap.put("8", "TokenShuffle");
transformationsMap.put("9", "NameStyleAbbreviation");
transformationsMap.put("10", "ChangeSynonym");
transformationsMap.put("11", "ChangeNumberFormat"); 
transformationsMap.put("12", "ChangeDateFormat");
transformationsMap.put("13", "ChangeLanguage");
transformationsMap.put("14", "ChangeBooleanValue"); 
transformationsMap.put("15", "ChangeGenderFormat");// not called
transformationsMap.put("16", "ChangeAntonym"); 
transformationsMap.put("17", "KeepRoot");
transformationsMap.put("18", "ChangeSyntheticCompound");// not impemented
transformationsMap.put("19", "ChangeWordPermutation");

// 20 - 22 are structural transformations
transformationsMap.put("20", "AddProperty"); 
transformationsMap.put("21", "DeleteProperty");
transformationsMap.put("22", "ExtractProperty");
transformationsMap.put("23", "AggregateProperties");

//23 - n are logical transformations 
transformationsMap.put("24", "CWSameAs");
transformationsMap.put("25", "SameAsOnExistingCW");
transformationsMap.put("26", "CWDifferentFrom");
transformationsMap.put("27", "EquivalentClass");
transformationsMap.put("28", "EquivalentProperty");
transformationsMap.put("29", "SubClassOf");
transformationsMap.put("30", "DisjointWith"); 
transformationsMap.put("31", "SubPropertyOf"); 
transformationsMap.put("32", "DisjointProperty"); //alldisjointproperties 
transformationsMap.put("33", "FunctionalProperty"); 
transformationsMap.put("34", "InverseFunctionalProperty");
transformationsMap.put("35", "ComplementOf");
transformationsMap.put("36", "IntersectionOf");
transformationsMap.put("37", "UnionOf");
transformationsMap.put("38", "OneOf"); */



public class CreateFinalGS {
	 private static ArrayList<ArrayList<Object>> finalGS = new ArrayList<ArrayList<Object>>();
	 private static ArrayList<ArrayList<Object>> GS = new ArrayList<ArrayList<Object>>();
	 private static String exactmatch = "http://www.w3.org/2004/02/skos/core#exactMatch";
	// private static String path = ".\\dist\\generatedGS\\";
	 public static double MACHEPS = 2E-16;
	 
public CreateFinalGS(){
}
	 
public static  List<ArrayList<Object>> GSScores(String file_) throws IOException, RDFHandlerException, RDFParseException {
	String GSfile = file_;	
	String u = "";
	String uPrime = "";
	String source = null, target = null, gold_standard = null;
	if (GSfile.endsWith(".ttl") && GSfile.startsWith("generatedCreativeWorksGS")){
		try {
			File file = new File("generatedGS\\"+GSfile.toString().replace(".ttl", "")+"_final"+ ".txt");	
			BufferedWriter simpleGSfile = new BufferedWriter(new FileWriter(file));
			
			
			RDFParser rdfParser = new TurtleParser();
			final String dir = System.getProperty("user.dir");
		    FileInputStream fileinputstream = new FileInputStream("generatedGS/"+GSfile);
			
		   
			InputStreamReader inputstreamreader  = new InputStreamReader(fileinputstream);
			StatementCollector handler = new StatementCollector();
			rdfParser.setRDFHandler(handler);
			rdfParser.parse(inputstreamreader, "");
			Collection<Statement> col = handler.getStatements();
			ArrayList<Object> IdTemp = new ArrayList<Object>();
			ArrayList<Object> TypeTemp = initializeTypeTempArray();
			for(Iterator<Statement> it = col.iterator(); it.hasNext();){
				Statement st = it.next();
			    if(st.getPredicate().stringValue().equals(exactmatch) && !u.equals(st.getSubject().stringValue())){
			    	if(!u.equals("")){
			    		if((Integer)TypeTemp.get(31) == 0 && (Integer)TypeTemp.get(33) == 0 && !IdTemp.isEmpty()){ //check for disjoint and do not add it 
			    			GS.add(IdTemp);
			    			GS.add(TypeTemp);
				    	}
			    		TypeTemp = initializeTypeTempArray();
			    		IdTemp = new ArrayList<Object>();
			    	}
			    	u = st.getSubject().toString();
					uPrime = st.getObject().toString();
			    }
			    
			    if(st.getPredicate().stringValue().equals("http://www.type")){
			    	int indexToReplace = Integer.parseInt(st.getObject().stringValue())+1;
			    	int transf = (Integer)TypeTemp.get(indexToReplace) +1;
			    	TypeTemp.remove(indexToReplace);
			    	TypeTemp.add(indexToReplace,transf);
			    }
			    if(st.getObject().stringValue().equals("CW_id")){
			    	IdTemp.add(u);
			    	IdTemp.add(uPrime);
			    	try {
						simpleGSfile.write(u+" "+uPrime+"\n");
					} catch ( IOException e ) {
						throw new IOException("Failed to calculate scores for gs file : " + e.getMessage(), e);
					}
			    }
			    
			    
			}
			simpleGSfile.close();
			
			//call rescal
			source = "generated\\"+GSfile.replace("GS", ""); 
			target = "generatedD2\\"+GSfile.replace("GS", "D2");
			gold_standard = file.toString();

			Rescal(source, target, gold_standard);

		}
		 catch (IOException e) {
			  throw new IOException("Failed to read configuration file : " + e.getMessage(), e);
		 }
		 catch (RDFParseException e) {
			throw new RDFParseException("Failed to parse  file: "+ e.getMessage());
         }
         catch (UnsupportedRDFormatException e) {
        	 throw new UnsupportedRDFormatException("UnsupportedRDFormatException : "+ e.getMessage());
         }
    }
	return Collections.synchronizedList(GS);
}

public static ArrayList<Double> calculateSpecificTransfWeights(ArrayList<ArrayList<Object>>finalGS_, Map<String, Map<String, Double>> S) throws RDFParseException, RDFHandlerException, IOException{
	ArrayList<Double> Y = new ArrayList<Double>();
	double[][] M = new double[RescalStarter.getSArrayList().size()][41];
	int j = 0;
	double cos = 0.0;
	Iterator<String> iteratorGS = S.keySet().iterator();
	while (iteratorGS.hasNext()) {
		 String key = iteratorGS.next().toString();
	     //System.out.println("key : " + key);
	    	 for(int i = 0; i < finalGS_.size(); i++) {   
	    			ArrayList<Object> in = finalGS_.get(i);
	    			if(in.contains(key)){
		     	 		//System.out.println("in key " + in.toString());

			     		Map<String,Double> value = S.get(key);
					    Iterator<String> internalIteratorGS = value.keySet().iterator();
					    String internalKey = internalIteratorGS.next().toString();
					    cos = value.get(internalKey);
			     	}
	     			if(cos > 0.0 && in.size() == 41){
		        		for (int k = 0; k < in.size(); k++) {
		        			 M[j][k] = (Double) ((Integer)in.get(k)*1.0);
		           		}
		        		j++;
		        	//System.out.println("cos " + cos);
		     		Y.add(cos);
				    cos = 0.0;
	     			}
			   	}
	    	 }
    	
    	
   /*clean M from columns that contain only zeros*/
    double[][] transposed = CleanZeros.transpose(M);
    double[][] colsCleaned = CleanZeros.cleanZeroRows(transposed);
    double[][] transposedBack = CleanZeros.transpose(colsCleaned);
   
    /*Create Marray*/
    double[][] Marray = new double[Y.size()][transposedBack[0].length];
	for (int x = 0; x < Marray.length; x++)
		  for (int y = 0; y < Marray[0].length; y++)
		    Marray[x][y] = transposedBack[x][y];
	
	/*Create Yarray*/
	double[][] Yarray = new double[Y.size()][1];
	double[] Ytemp = new double[Y.size()];
	for (int k = 0; k < Yarray.length; k++){   
		Yarray[k][0] = Y.get(k);
		Ytemp[k] = Y.get(k);
	}
	/*Convert Y from array to matrix*/
	Matrix Ymatrix = new Matrix(Yarray);
	
	/*pseudo inverse Marray*/
	Matrix InverseMarray = Matrices.pinv2(new Matrix(Marray));
	//Matrix InverseMarray = new Matrix(Marray).inverse();

	
	/*Calculate final weight*/
	Matrix T = InverseMarray.times(Ymatrix); 
	double[] T_array = T.getRowPackedCopy();//row or column, contain the same nums
	
//	System.out.print("\nT\n");
//	for (int i = 0; i < T_array.length; i++) {
//		System.out.print(T_array[i] + " ");
//	}
//	System.out.print("\n");
	
	ArrayList<Double> specificWeights = new ArrayList<Double>();
	specificWeights.add(0,0.0);
	specificWeights.add(1,0.0);
	int zeroIndexes = 0;
	for (int i = 2; i < 41; i++){
		double weight = 0.0;
		if(CleanZeros.indexes.contains(i)){ 
			weight = 0.0;
			zeroIndexes ++;
		} 
		else{
			weight = T_array[i - zeroIndexes];//regression.residuals(i - zeroIndexes);   <-------------
		}
		specificWeights.add(Math.abs(weight));
		//specificWeights.add(weight);
	}
//	for(int i = 0; i < specificWeights.size(); i++) {   
//	    System.out.println("i: "+ i+ "  specificWeight: "+specificWeights.get(i));
//	}

	return specificWeights;
}



public static void writeFinalGSFiles() throws IOException, RDFParseException, RDFHandlerException{
//write here detailed gs(ttl) and also simple one (txt) both wighted
//after finishing delete previous detailes GSs with the wrong weights
ArrayList<Double> weight_per_trans_ =  calculateSpecificTransfWeights(getFinalGS(),RescalStarter.getGsWeighted());
File folder = new File("generatedGS\\");
File[] listOfFiles = folder.listFiles();
	for (File file_ : listOfFiles) {
	    if (file_.isFile() && file_.getName().endsWith("ttl") && !file_.getName().contains("DETAILED")) {
	    	String GSfile = file_.getName();	
	    	FileOutputStream fos = null;
	    	String u = "";
	    	String uPrime = "";
	    	int indexToReplace = 0;
	    	double u_uPrime_weight = 0d;
	    	
	    	try {
	    		File file = new File("generatedGS\\"+GSfile.toString().replace(".ttl", "")+"_final"+ ".txt");	
	    		
	    		//oaei
	    		OAEIRDFAlignmentFormat oaeiRDF = null;
	    		OAEIAlignmentOutput oaei = null;
	    		try {
	    			oaeiRDF = new OAEIRDFAlignmentFormat("OAEIRDFGoldStandards\\"+GSfile.toString().replace( FileUtils.getFileExtension(GSfile), "rdf"), GSfile.toString().replace("goldStandard", "source"), GSfile.toString().replace("goldStandard", "target"));
	    			oaei = new OAEIAlignmentOutput(GSfile.toString().replace(".", "").replace( FileUtils.getFileExtension(GSfile), ""), GSfile.toString().replace("goldStandard", "source"), GSfile.toString().replace("goldStandard", "target"));
	    		} catch (Exception e1) {
					e1.printStackTrace();
				}
	    		BufferedWriter simpleGSfile = new BufferedWriter(new FileWriter(file));
	    		
	    		Model detailedGS = new LinkedHashModel();
	    		String detailedFileName = "generatedGS\\"+GSfile.toString().replace(".ttl", "")+"_DETAILED"+ ".ttl";
	    		FileOutputStream fos_det = new FileOutputStream(detailedFileName);	
	    		
	    		RDFParser rdfParser = new TurtleParser();
	    		final String dir = System.getProperty("user.dir");
	    		FileInputStream fileinputstream = new FileInputStream("generatedGS/"+GSfile);
	    		InputStreamReader inputstreamreader  = new InputStreamReader(fileinputstream);
	    		StatementCollector handler = new StatementCollector();
	    		rdfParser.setRDFHandler(handler);
	    		rdfParser.parse(inputstreamreader, "");
	    		
	    		Collection<Statement> col = handler.getStatements();
	    		ArrayList<Object> IdTemp = new ArrayList<Object>();
	    		ArrayList<Object> ResTemp = new ArrayList<Object>();
	    		ArrayList<Object> TypeTemp = initializeTypeTempArray();
	    		
	    		for(Iterator<Statement> it = col.iterator(); it.hasNext();){
				Statement st = it.next();
				
			    if(st.getPredicate().stringValue().equals(exactmatch) && !u.equals(st.getSubject().stringValue())){
			    	if(!u.equals("")){ //check disjointness again
			    		if((Integer)TypeTemp.get(31) == 0 && (Integer)TypeTemp.get(33) == 0 && !IdTemp.isEmpty()){ //check for disjoint and do not add it 
				    		finalGS.add(IdTemp);
				    		finalGS.add(ResTemp);
				    		finalGS.add(TypeTemp);
				    		
					    	try {
					    		double weight = (1.0 - u_uPrime_weight);
					    		if(weight > 1.0){
					    			weight = 1.0;
					    		}
					    		if((1.0 - u_uPrime_weight) < 0.0){ //we need this
					    			weight = 0.0;
					    		}
								simpleGSfile.write(IdTemp.get(0)+" "+IdTemp.get(1)+" "+weight+"\n"); //made it 1- weight 
					    		u_uPrime_weight = 0d; /////
							
					    		//oaei
								try {
//									oaeiRDF.addMapping2Output(IdTemp.get(0).toString(), IdTemp.get(1).toString(), 0, weight);
//									oaei.addMapping2Output(IdTemp.get(0).toString(), IdTemp.get(1).toString(), 0, weight);
									oaeiRDF.addMapping2Output(IdTemp.get(0).toString(), IdTemp.get(1).toString(), 0, 1.0);
									oaei.addMapping2Output(IdTemp.get(0).toString(), IdTemp.get(1).toString(), 0, 1.0);
									u_uPrime_weight = 0d; /////
									
									
								} catch (Exception e) {
									e.printStackTrace();
								}
					    	
					    	} catch ( IOException e ) {
								   e.printStackTrace();
							}
				    	}
			    		TypeTemp = initializeTypeTempArray();
			    		IdTemp = new ArrayList<Object>();
			    		ResTemp = new ArrayList<Object>(); 

			    	}
			    	u = st.getSubject().toString();
					uPrime = st.getObject().toString();
			    }
			    
			    if(st.getPredicate().stringValue().equals("http://www.type")){
			    	indexToReplace = Integer.parseInt(st.getObject().stringValue())+1;
			    	//System.out.println("indexToReplace " + indexToReplace);
			    	int transf = (Integer)TypeTemp.get(indexToReplace) +1;
			    	TypeTemp.remove(indexToReplace);
			    	TypeTemp.add(indexToReplace,transf);
			    }
			    if(st.getObject().stringValue().equals("CW_id")){
			    	IdTemp.add(u);
			    	IdTemp.add(uPrime);
			    	ResTemp.add(1);
			    }
				  //check weight here
				if(st.getPredicate().stringValue().equals("http://www.weight")){
					double weight = weight_per_trans_.get((indexToReplace));
					detailedGS.add(st.getSubject(),st.getPredicate(),SesameBuilder.sesameValueFactory.createLiteral(weight),st.getContext());
					//keep sum here for final gs
					u_uPrime_weight += weight;
				}
				else{detailedGS.add(st);}
			    
			    
			}
	    	RDFFormat rdfFormat = SesameUtils.parseRdfFormat("turtle");
	    		 
	    	Rio.write(detailedGS, fos_det, rdfFormat);
	    	
	    	try {
				oaeiRDF.saveOutputFile();
				oaei.saveOutputFile();
			} catch (Exception e) {
				e.printStackTrace();
			}
			simpleGSfile.close();	
			fileinputstream.close();
	    	inputstreamreader.close();
	    }
		catch (UnsupportedRDFormatException e) {
			throw new UnsupportedRDFormatException("UnsupportedRDFormatException : "+ e.getMessage());
		}
	    file_.delete(); //DELETE INTERMEDIATE GS
	}
}
	
}


public static  Map<String, Map<String, Double>> Rescal(String source, String target, String gold_standard){
Map<String, Map<String, Double>> map = null ;
try{

	new RescalStarter( source,  target,  gold_standard);
	map = RescalStarter.getGsWeighted();   
}catch(Exception e){
	System.err.println(e.getMessage());
	System.err.println("Problem with java-python bridge occured.");
}
return map;
}


public void sortListOfLists(ArrayList < ArrayList < String >> listOfLists) {

// now sort by comparing the first string of each inner list using a comparator
Collections.sort(listOfLists, new ListOfStringsComparator());
}

public static ArrayList<Object> initializeTypeTempArray(){
	ArrayList<Object> TypeTemp = new ArrayList<Object>();
	TypeTemp.add(1); //extra static constant
//so i have to calculate the T and add them at index i+1
	for(int i=0; i <40; i++){
		TypeTemp.add(0);
	}
	return TypeTemp;		
}

public static ArrayList<ArrayList<Object>> getFinalGS(){
	return GS;
}

}


    

final class ListOfStringsComparator implements Comparator < ArrayList < String >> {

    @
    Override
    public int compare(ArrayList < String > o1, ArrayList < String > o2) {
    	try{// do other error checks here as well... such as null. outofbounds, etc
            return o1.get(2).compareTo(o2.get(2));
         }
    	 catch(IndexOutOfBoundsException e){
    	    System.err.println("IndexOutOfBoundsException: " + e.getMessage());
         }
		return 0;
        
    }

}



