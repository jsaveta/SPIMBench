package eu.ldbc.semanticpublishing.transformations.logical;

import java.util.ArrayList;

import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.LinkedHashModel;

import eu.ldbc.semanticpublishing.generators.data.AbstractAsynchronousWorker;
import eu.ldbc.semanticpublishing.generators.data.sesamemodelbuilders.SesameBuilder;
import eu.ldbc.semanticpublishing.transformations.InvalidTransformation;
import eu.ldbc.semanticpublishing.transformations.Transformation;

public class FunctionalProperty implements Transformation{
	private static final String core = "http://www.bbc.co.uk/ontologies/coreconcepts/";
	private static final String ldbc = "http://www.ldbc.eu/";

	
	private ArrayList<String> PropertyFunct; 
	public FunctionalProperty(AbstractAsynchronousWorker worker){
		PropertyFunct = new ArrayList<String>();
		PropertyFunct.add(core + "primaryTopic");

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
	    	if(PropertyFunct.contains(s.getPredicate().stringValue())){
	    		String[] new_obj;
	    		if(s.getObject().stringValue().contains("#")){
	    			 new_obj = s.getObject().stringValue().split("#"); 
	    		}
	    		else{
	    			 new_obj = s.getObject().stringValue().split("/"); 
	    		}
				String lastOne = new_obj[new_obj.length-1];
				model.add(s.getSubject(),s.getPredicate(),SesameBuilder.sesameValueFactory.createURI(ldbc+lastOne),s.getContext());
			}else{
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
