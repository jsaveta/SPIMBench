package eu.ldbc.semanticpublishing.transformations.structural;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.LinkedHashModel;

import eu.ldbc.semanticpublishing.generators.data.AbstractAsynchronousWorker;
import eu.ldbc.semanticpublishing.generators.data.sesamemodelbuilders.SesameBuilder;
import eu.ldbc.semanticpublishing.transformations.InvalidTransformation;
import eu.ldbc.semanticpublishing.transformations.Transformation;

public class AggregateProperties implements Transformation{
	private static final String cworkNamespace = "http://www.bbc.co.uk/ontologies/creativework/";
	private static final String core = "http://www.bbc.co.uk/ontologies/coreconcepts/";
	private static final String foaf = "http://xmlns.com/foaf/0.1/";

	private AbstractAsynchronousWorker worker; 
	private Map<String,ArrayList<String>> AggregationMap; 
	
	public AggregateProperties(AbstractAsynchronousWorker worker) {

		this.worker = worker;
		
		//this map contains the property hierarchy. As key is the superproperty and 
			//as value an arraylist with its subproperties
			AggregationMap = new HashMap<String,ArrayList<String>>();
			ArrayList<String> temp = new ArrayList<String>();
			
			temp.add(cworkNamespace + "title");
			temp.add(cworkNamespace + "shortTitle");
			AggregationMap.put(cworkNamespace+"fullTitle",temp);
			
		    temp = new ArrayList<String>();
			temp.add(core + "givenName");
			temp.add(core + "surname");
			AggregationMap.put(core+"fullName",temp);
			
		    temp = new ArrayList<String>(); 
			temp.add(core + "givenName");
			temp.add(foaf + "surname");
			AggregationMap.put(core+"fullName",temp);
			
			 temp = new ArrayList<String>(); 
			 temp.add(foaf + "givenName");
			 temp.add(foaf + "surname");
			 AggregationMap.put(core+"fullName",temp);
			 
	}



	@Override
	public Object execute(Object arg) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String print() {
		// TODO Auto-generated method stub
		return null;
	}
	@SuppressWarnings("finally")
	public Model executeStatement(Statement arg) { 
		Statement s = (Statement)arg;
		Model model = new LinkedHashModel();
		 if(arg instanceof Statement){
			String key = "";
			for (Entry<String, ArrayList<String>> entry : AggregationMap.entrySet()) {
				 if(entry.getValue().contains(s.getPredicate().toString())){
					 key = entry.getKey();
				 }				
			}
			Model temp = worker.getSesameModel(); 
			Iterator<Statement> it = temp.iterator();
			while(it.hasNext())
			{	
				Statement st = it.next();
				if(st.getPredicate().toString().equals(s.getPredicate().toString())){
					if(it.hasNext()){
						st = it.next();
		    			Value o = SesameBuilder.sesameValueFactory.createLiteral( s.getObject().toString().replace("\"", "") + " " + st.getObject().toString().replace("\"", ""));
		    			model.add(st.getSubject(), (URI)SesameBuilder.sesameValueFactory.createURI(key),(Value)o, st.getContext());
	
					}
					else{
						model.add(s);
					}
					
				}
			}

	 }
	else{
		try {
			throw new InvalidTransformation();
		} catch (InvalidTransformation e) {
			e.printStackTrace();
		}finally{
			return model;
		}
	}
	    
 	return model;
	}
		
	
}