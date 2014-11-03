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

public class IntersectionOf implements Transformation{

	private static final String ldbc = "http://www.ldbc.eu/";
	private Map<String, String> Intersection;

	public IntersectionOf(AbstractAsynchronousWorker worker){
		Intersection = new HashMap<String, String>();
		Intersection.put(ldbc + "Person_Organisation", ldbc + "Individual_Corporation");
		Intersection.put(ldbc + "Event_Place_Theme", ldbc + "Happening_Spot"); //smaller weight

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
			if(Intersection.containsKey(s.getObject().toString())){
				 String[] new_sub = s.getSubject().toString().replace("rdf-schema#", "").split("/");
				 String lastOne_ = new_sub[new_sub.length-1];
					
				 model.add(SesameBuilder.sesameValueFactory.createURI(ldbc+lastOne_), s.getPredicate(), SesameBuilder.sesameValueFactory.createURI(Intersection.get(s.getObject().toString())),s.getContext());
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
