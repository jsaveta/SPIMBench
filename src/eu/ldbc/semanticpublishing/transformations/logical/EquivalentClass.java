package eu.ldbc.semanticpublishing.transformations.logical;

import java.util.HashMap;
import java.util.Map;

import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.LinkedHashModel;

import eu.ldbc.semanticpublishing.generators.data.AbstractAsynchronousWorker;
import eu.ldbc.semanticpublishing.generators.data.sesamemodelbuilders.SesameBuilder;
import eu.ldbc.semanticpublishing.transformations.InvalidTransformation;
import eu.ldbc.semanticpublishing.transformations.Transformation;

public class EquivalentClass implements Transformation{
	private static final String core = "http://www.bbc.co.uk/ontologies/coreconcepts/";
	private static final String dbpediaowl = "http://dbpedia.org/ontology/";
	private static final String foaf = "http://xmlns.com/foaf/0.1/";
	
	
	private Map<String, String> ClassEquivalence;
	private AbstractAsynchronousWorker worker; 
	public EquivalentClass(AbstractAsynchronousWorker worker){
		this.worker = worker;
		//this map contains the ontology hierarchy. As key is the subclass and as value the superclass
		ClassEquivalence = new HashMap<String, String>();
		ClassEquivalence.put(dbpediaowl + "Place", core + "Place");
		ClassEquivalence.put(dbpediaowl + "Event", core + "Event");
		ClassEquivalence.put(dbpediaowl + "Organisation", core + "Organisation");
		ClassEquivalence.put(foaf + "Person", core + "Person");
		ClassEquivalence.put(dbpediaowl + "Sport", core + "Sport");
		
		ClassEquivalence.put( core + "Place",dbpediaowl + "Place");
		ClassEquivalence.put( core + "Event",dbpediaowl + "Event");
		ClassEquivalence.put( core + "Organisation",dbpediaowl + "Organisation");
		ClassEquivalence.put( core + "Person",foaf + "Person");
		ClassEquivalence.put( core + "Sport",dbpediaowl + "Sport");
		
		
		//na psaksw ola osa einai equivalent metaaksu tous kai na ta prosthesw!
		
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
		@Override
		public Model executeStatement(Statement arg) {
			Statement s = (Statement)arg;
			Model model = new LinkedHashModel();
			if(arg instanceof Statement){
    			if(ClassEquivalence.containsKey(s.getObject().toString())){
    				model.add(s.getSubject(), s.getPredicate(), SesameBuilder.sesameValueFactory.createURI(ClassEquivalence.get(s.getObject().toString())),s.getContext());
    	    	}
    	    	else{
    	    		model.add(s);
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
