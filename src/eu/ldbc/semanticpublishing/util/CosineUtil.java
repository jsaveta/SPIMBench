package eu.ldbc.semanticpublishing.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class CosineUtil {
	public CosineUtil(){}
	  public static Map<String, Map<String, Double>> calculateCosine(Map<String, ArrayList<Double>> uriMatrixD1,Map<String, ArrayList<Double>> 
      uriMatrixD2,Map<String, Map<String, Double>> gs){
      //System.out.println("auto einai to map pou exei u,u' kai to cosine similarity pou tora einai 0 "+gs);
      //System.out.println("------------------------------------------------------------" );
      //System.out.println("uriMatrixD1 :" + uriMatrixD1);
      //System.out.println("------------------------------------------------------------" );
      //System.out.println("uriMatrixD2 :" + uriMatrixD2);
      
      ArrayList<Double> uA = null;
      ArrayList<Double> uPrimeA = null;
      Map<String,Double> value = null;
      Map<String, Map<String, Double>> gsWeighted = new HashMap<String, Map<String, Double>>();
      Iterator<String> iteratorGS = gs.keySet().iterator();  
      while (iteratorGS.hasNext()) {  
         String key = iteratorGS.next().toString();
         System.out.println("key : " + key);
         if(uriMatrixD1.containsKey(key)){
        	 uA = uriMatrixD1.get(key);
             value = gs.get(key);
            
	         Iterator<String> internalIteratorGS = value.keySet().iterator();
	         System.out.println("internalIteratorGS : "  +internalIteratorGS);
	         while (internalIteratorGS.hasNext()) {  
	            String internalKey = internalIteratorGS.next().toString();
	            System.out.println("internalKey : " + internalKey);
	            if(uriMatrixD2.containsKey(internalKey)){
			            uPrimeA = uriMatrixD2.get(internalKey);
			            double cosine = cosineSimilarity(uA,uPrimeA);
			            System.out.println("cosine "+cosine);
			            Map<String, Double> temp = new HashMap<String, Double>();
			            temp.put(internalKey, cosine);
			            gsWeighted.put(key, temp);
	            }
	         }
         }
      } 
    System.out.println("auto einai to map pou exei u,u' kai to cosine similarity pou tora DEN einai 0 "+gsWeighted);
    return gsWeighted;    	
    }
    
    public static double cosineSimilarity(ArrayList<Double> vectorA, ArrayList<Double> vectorB) {
	    	double dotProduct = 0.0;
	        double magnitude1 = 0.0;
	        double magnitude2 = 0.0;
	        double cosineSimilarity = 0.0;
	        int loop = vectorA.size();
	        
	        if(vectorA.size() > vectorB.size()){
	        	loop = vectorB.size();
	        }
	        for (int i = 0; i < loop; i++) //docVector1 and docVector2 must be of same length
	        {
	            dotProduct += vectorA.get(i) * vectorB.get(i) ;  //a.b
	            magnitude1 += Math.pow(vectorA.get(i) , 2);  //(a^2)
	            magnitude2 += Math.pow(vectorB.get(i) , 2); //(b^2)
	        }
	 
	        magnitude1 = Math.sqrt(magnitude1);//sqrt(a^2)
	        magnitude2 = Math.sqrt(magnitude2);//sqrt(b^2)
	        
	        //System.out.println("magnitude1 "+ magnitude1);
	        //System.out.println("magnitude2 "+ magnitude2);
	        if (!isInfinite(magnitude1) && !isInfinite(magnitude2) && !isNaN(magnitude1) && !isNaN(magnitude2))
	        {
	            cosineSimilarity = dotProduct / (magnitude1 * magnitude2);
	        } 
	        else
	        {
	            return 0.0;
	        }
	        return cosineSimilarity;
    }
    
    
	public double SquareCosineSimilarity(ArrayList<Double> vectorA, ArrayList<Double>  vectorB) {
	        double dotProduct =  0d;
	        double normA = 0d;
	        double normB = 0d;
	        //TODO check if two arraylists do not have the same size
	        for (int i = 0; i < vectorA.size(); i++) {
	            dotProduct += vectorA.get(i) * vectorB.get(i);
	            normA += Math.pow(vectorA.get(i), 2);
	            normB += Math.pow(vectorB.get(i), 2);
	        }
	        if(Math.sqrt(normA *normB) > 0){
	        	//System.out.println("cos "+(dotProduct / Math.sqrt(normA *normB)));
	        	//System.out.println("square cos "+ Math.pow((dotProduct / Math.sqrt(normA *normB)),2));
	        	return Math.pow((dotProduct / Math.sqrt(normA *normB)),2);
	        }
	        else{return 0.0;}
	   }
	 
	 public Map<String,Double> squareCosineSimilarityEF(Map<String, ArrayList<Double>> Fmap, ArrayList<Double> E){
		 Map<String,Double> squareCosine = new HashMap<String,Double>();
		 for (Entry<String, ArrayList<Double>> entry : Fmap.entrySet()) {
	            ArrayList<Double> value = entry.getValue();
	            squareCosine.put(entry.getKey().replace("./generated\\generatedCreativeWorks", "generatedCreativeWorksGS"),SquareCosineSimilarity(value,E));
//	            System.out.println("key "+entry.getKey());
//	            System.out.println("value "+value);
//	            System.out.println("E "+E);
//	            System.out.println("square cosine : " + SquareCosineSimilarity(value,E));
	        	
	        }
		 //calculate here square cosine (E,F)
		 squareCosine = sortByValues(squareCosine);
		 //System.out.println("squareCosine: "+squareCosine);
		return squareCosine;
		 
	 }
	 
	 public static <K extends Comparable,V extends Comparable> Map<K,V> sortByValues(Map<K,V> map){
	        List<Map.Entry<K,V>> entries = new LinkedList<Map.Entry<K,V>>(map.entrySet());
	      
	        Collections.sort(entries, new Comparator<Map.Entry<K,V>>() {

	            @Override
	            public int compare(Entry<K, V> o1, Entry<K, V> o2) {
	                return o1.getValue().compareTo(o2.getValue());
	            }
	        });
	      
	        //LinkedHashMap will keep the keys in the order they are inserted
	        //which is currently sorted on natural ordering
	        Map<K,V> sortedMap = new LinkedHashMap<K,V>();
	      
	        for(Map.Entry<K,V> entry: entries){
	        	//diki mou if
	        	if(Double.parseDouble(entry.getValue().toString()) != 0.0){
	        		sortedMap.put(entry.getKey(), entry.getValue());
	        	}
	        }
	      
	        return sortedMap;
	    }

	  
	    /**
	     * Answers <code>true</code> iff the given number is infinite (i.e., is
	     * a <code>Float</code> or <code>Double</code> containing one of the
	     * predefined constant values representing positive or negative infinity).
	     *
	     * @param number
	     * @return boolean
	     */
	    public static boolean isInfinite(Number number) {
	      if (number instanceof Double && ((Double)number).isInfinite())
	        return true;
	      if (number instanceof Float && ((Float)number).isInfinite())
	        return true;
	      return false;
	    }

	    /**
	     * Answers <code>true</code> iff the given number is 'not a number'
	     * (i.e., is a <code>Float</code> or <code>Double</code> containing
	     * one of the predefined constant values representing <code>NaN</code>).
	     *
	     * @param number
	     * @return boolean
	     */
	    public static boolean isNaN(Number number) {
	      if (number instanceof Double && ((Double)number).isNaN())
	        return true;
	      if (number instanceof Float && ((Float)number).isNaN())
	        return true;
	      return false;
	    }
	    

}
