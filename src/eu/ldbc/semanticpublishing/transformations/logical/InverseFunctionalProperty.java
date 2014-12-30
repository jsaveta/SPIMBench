package eu.ldbc.semanticpublishing.transformations.logical;

import java.util.ArrayList;

import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.LinkedHashModel;

import eu.ldbc.semanticpublishing.generators.data.AbstractAsynchronousWorker;
import eu.ldbc.semanticpublishing.generators.data.sesamemodelbuilders.SesameBuilder;
import eu.ldbc.semanticpublishing.transformations.InvalidTransformation;
import eu.ldbc.semanticpublishing.transformations.Transformation;

public class InverseFunctionalProperty implements Transformation{
	private static final String bbcNamespace = "http://www.bbc.co.uk/ontologies/bbc/";
	private ArrayList<String> PropertyInverseFunct;
	
	public InverseFunctionalProperty(AbstractAsynchronousWorker worker){
		PropertyInverseFunct = new ArrayList<String>();
		PropertyInverseFunct.add(bbcNamespace + "primaryContentOf");
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
			//TODO check if!!!!!!!
			if(PropertyInverseFunct.contains(s.getPredicate().stringValue())){
				//model.add(s); //.indexOf((s.getPredicate().toString()))
				model.add(SesameBuilder.sesameValueFactory.createURI(s.getObject().stringValue()), SesameBuilder.sesameValueFactory.createURI(PropertyInverseFunct.get(PropertyInverseFunct.indexOf(s.getPredicate().stringValue()))),s.getSubject(),s.getContext());
	    		
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
