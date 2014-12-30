package eu.ldbc.semanticpublishing.transformations.logical;

import java.util.HashMap;
import java.util.Map;

import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.impl.ValueFactoryImpl;

import eu.ldbc.semanticpublishing.generators.data.AbstractAsynchronousWorker;
import eu.ldbc.semanticpublishing.generators.data.sesamemodelbuilders.SesameBuilder;
import eu.ldbc.semanticpublishing.transformations.InvalidTransformation;
import eu.ldbc.semanticpublishing.transformations.Transformation;

public class SubClassOf implements Transformation{
	private static final String cworkNamespace = "http://www.bbc.co.uk/ontologies/creativework/";
	private static final String travel = "http://www.co-ode.org/roberts/travel.owl#";
	private static final String upper = "http://www.co-ode.org/roberts/upper.owl#";
	private static final String core = "http://www.bbc.co.uk/ontologies/coreconcepts/";
	private static final String dbpediaowl = "http://dbpedia.org/ontology/";
	private static final String foaf = "http://xmlns.com/foaf/0.1/";
	private static final String owl = "http://www.w3.org/2002/07/owl#";
	private int depth;
	
	private Map<String, String> ClassHierarchy; 
	public SubClassOf(AbstractAsynchronousWorker worker, int depth){
		this.depth = depth;
		//this map contains the ontology hierarchy. As key is the subclass and as value the superclass
		ClassHierarchy = new HashMap<String, String>();
		ClassHierarchy.put(cworkNamespace + "NewsItem", cworkNamespace + "CreativeWork");
		ClassHierarchy.put(cworkNamespace + "Programme", cworkNamespace + "CreativeWork");
		ClassHierarchy.put(cworkNamespace + "BlogPost", cworkNamespace + "CreativeWork");
		ClassHierarchy.put(core + "Place", core + "Thing");
		ClassHierarchy.put(core + "Theme", core + "Thing");
		ClassHierarchy.put(core + "Event", core + "Thing");
		ClassHierarchy.put(core + "Organisation", core + "Thing");
		ClassHierarchy.put(core + "Person", core + "Thing");
		
		ClassHierarchy.put(core + "Thing", owl + "Thing");
		
		ClassHierarchy.put(dbpediaowl + "Place", core + "Thing");
		ClassHierarchy.put(dbpediaowl + "Event", core + "Thing");
		ClassHierarchy.put(dbpediaowl + "Organisation", core + "Thing");
		ClassHierarchy.put(dbpediaowl + "Sport", core + "Thing");
		ClassHierarchy.put(foaf + "Person", core + "Thing");
		
		ClassHierarchy.put(travel + "AdministrativeDivision", core + "Thing");
		ClassHierarchy.put(travel + "GeographicalFeature", core + "Thing");
		ClassHierarchy.put(travel + "Island", core + "Thing");
		ClassHierarchy.put(travel + "City", core + "Thing");
		ClassHierarchy.put(travel + "Coastline", core + "Thing");
		ClassHierarchy.put(travel + "Country", core + "Thing");
		ClassHierarchy.put(travel + "EuropeanIsland", core + "Thing");
		ClassHierarchy.put(travel + "River", core + "Thing");
		ClassHierarchy.put(travel + "Recognised", core + "Thing");
		ClassHierarchy.put(travel + "TierOneAdministrativeDivision", travel + "AdministrativeDivision");
		ClassHierarchy.put(travel + "bodyOfLand", travel + "GeographicalFeature");
		ClassHierarchy.put(travel + "Continent", travel + "bodyOfLand");
		
		ClassHierarchy.put(upper + "Person_agent", core + "Thing");
		
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
		Boolean subclass = false;
	    if(arg instanceof Statement){
	    	for(int i = 0; i< this.depth; i++){
	    		model = new LinkedHashModel();
	    		if(ClassHierarchy.containsKey(s.getObject().stringValue())){
		    		model.add(s.getSubject(), s.getPredicate(), SesameBuilder.sesameValueFactory.createURI(ClassHierarchy.get(s.getObject().stringValue())),s.getContext());
		    		ValueFactory factory = ValueFactoryImpl.getInstance();
		    		s=factory.createStatement(s.getSubject(), s.getPredicate(), SesameBuilder.sesameValueFactory.createURI(ClassHierarchy.get(s.getObject().stringValue())), s.getContext());
					subclass = true;
	    		}
		    	else{
		    		model.add(s);
		    	}
	    	}
//	    	if(subclass == true){
//	    				model.add(s.getSubject(),SesameBuilder.sesameValueFactory.createURI("http://www.depth") , SesameBuilder.sesameValueFactory.createLiteral( Integer.toString(this.depth)));
//	    	}
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
