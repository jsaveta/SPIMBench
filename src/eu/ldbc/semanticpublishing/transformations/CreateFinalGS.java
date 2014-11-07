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
	 private static String exactmatch = "http://www.w3.org/2004/02/skos/core#exactMatch";
	 private static String path = ".\\dist\\generatedGS\\";
	 public static double MACHEPS = 2E-16;
	 
public CreateFinalGS(){
}
	 
@SuppressWarnings("deprecation")
public static ArrayList<ArrayList<Object>> GSScores(String file_) throws IOException, RDFHandlerException, RDFParseException {
	String GSfile = path+file_;	
	FileOutputStream fos = null;
	String u = "";
	String uPrime = "";
	String source = null, target = null, gold_standard = null;
	GSfile = GSfile.replace(".\\dist\\generatedGS\\", "");
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
			    if(st.getPredicate().toString().equals(exactmatch) && !u.equals(st.getSubject().toString())){
			    	if(!u.equals("")){
			    		if((Integer)TypeTemp.get(31) == 0 && (Integer)TypeTemp.get(33) == 0 && !IdTemp.isEmpty()){ //check for disjoint and do not add it 
				    		finalGS.add(IdTemp);
				    		finalGS.add(TypeTemp);
				    	}
			    		TypeTemp = initializeTypeTempArray();
			    		IdTemp = new ArrayList<Object>();
			    	}
			    	u = st.getSubject().toString();
					uPrime = st.getObject().toString();
			    }
			    
			    if(st.getPredicate().toString().equals("http://www.type")){
			    	int indexToReplace = Integer.parseInt(st.getObject().toString().replace("\"", ""))+1;
			    	int transf = (Integer)TypeTemp.get(indexToReplace) +1;
			    	TypeTemp.remove(indexToReplace);
			    	TypeTemp.add(indexToReplace,transf);
			    }
			    if(st.getObject().toString().replace("\"", "").equals("CW_id")){
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
			source = "generated\\"+GSfile.toString().replace("GS", ""); 
			target = "generatedD2\\"+GSfile.toString().replace("GS", "D2");
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
	return finalGS;
}


public static ArrayList<Double> calculateSpecificTransfWeights(ArrayList<ArrayList<Object>>finalGS_, double[] S) throws RDFParseException, RDFHandlerException, IOException{

	ArrayList<Double> specificWeights = new ArrayList<Double>();
	int top = 0; 
	if(S.length > (finalGS_.size()/2)){
		top = (finalGS_.size()/2);
	}
	else{
		top = S.length;
	}
	double [] S_ = new double [top]; 
	for(int i = 0; i < top ; i++){
		S_[i] = S[i];
	}
	
	double[][] Marray = new double[40][top];
	int j = 0;
	Iterator<ArrayList<Object>> it = finalGS_.iterator();
	int stop = 0;
	while(stop < top){
    	ArrayList<Object> M = it.next();
    	if(M.size() == 41){	
    		for (int i = 0; i < Marray.length; i++) {
    		    Marray[i][j] = (Double) ((Integer)M.get(i)*1.0);
       		}
    		j++;
    	}
    	stop++;
	}

	double[] Msum = new double[Marray.length];
    for (int i = 0; i < Marray.length; i++){    
    	for (int k = 0; k < Marray[0].length; k++){   
    		 Msum[i] += Marray[i][k];
    	}
    }
   //pseudo inverse Marray because matrix is rank deficient
    Matrix InverseMarray = Matrices.pinv2(new Matrix(Marray));

	Matrix S_matrix = new Matrix(S_,1);

	Matrix T = S_matrix.times(InverseMarray); 

	double[] T_array = T.getRowPackedCopy();
	
	for (int i = 0; i < T_array.length; i++){
		double weight = 0.0;
		if(Msum[i] != 0.0){ weight = T_array[i]/Msum[i];}
		else{weight = 0.0;}
		specificWeights.add(weight);
	}


	return specificWeights;
	
}


public static void writeFinalGSFiles() throws IOException, RDFParseException, RDFHandlerException{
//write here detailed gs(ttl) and also simple one (txt) both wighted
//after finishing delete previous detailes GSs with the wrong weights
ArrayList<Double> weight_per_trans_ =  calculateSpecificTransfWeights(getFinalGS(), RescalStarter.getS());
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
				
			    if(st.getPredicate().toString().equals(exactmatch) && !u.equals(st.getSubject().toString())){
			    	if(!u.equals("")){ //chech disjointness again
			    		if((Integer)TypeTemp.get(31) == 0 && (Integer)TypeTemp.get(33) == 0 && !IdTemp.isEmpty()){ //check for disjoint and do not add it 
				    		finalGS.add(IdTemp);
				    		finalGS.add(ResTemp);
				    		finalGS.add(TypeTemp);
				    		
					    	try {
					    		double weight = (1.0 - u_uPrime_weight);
					    		if((1.0 - u_uPrime_weight) > 1.0){
					    			weight = 1.0;
					    		}
								simpleGSfile.write(IdTemp.get(0)+" "+IdTemp.get(1)+" "+weight+"\n"); //made it 1- weight 
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
			    
			    if(st.getPredicate().toString().equals("http://www.type")){
			    	indexToReplace = Integer.parseInt(st.getObject().toString().replace("\"", ""))+1;
			    	//System.out.println("indexToReplace " + indexToReplace);
			    	int transf = (Integer)TypeTemp.get(indexToReplace) +1;
			    	TypeTemp.remove(indexToReplace);
			    	TypeTemp.add(indexToReplace,transf);
			    }
			    if(st.getObject().toString().replace("\"", "").equals("CW_id")){
			    	IdTemp.add(u);
			    	IdTemp.add(uPrime);
			    	ResTemp.add(1);
			    }
				  //check weight here
				if(st.getPredicate().toString().equals("http://www.weight")){
					double weight = weight_per_trans_.get((indexToReplace));
					detailedGS.add(st.getSubject(),st.getPredicate(),SesameBuilder.sesameValueFactory.createLiteral(weight),st.getContext());
					//keep sum here for final gs
					u_uPrime_weight += weight;
				}
				else{detailedGS.add(st);}
			    
			    
			}
	    	RDFFormat rdfFormat = SesameUtils.parseRdfFormat("turtle");
	    		 
	    	Rio.write(detailedGS, fos_det, rdfFormat);
			simpleGSfile.close();	
			fileinputstream.close();
	    	inputstreamreader.close();
	    }
		catch (UnsupportedRDFormatException e) {
			throw new UnsupportedRDFormatException("UnsupportedRDFormatException : "+ e.getMessage());
		}
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
	return finalGS;
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



