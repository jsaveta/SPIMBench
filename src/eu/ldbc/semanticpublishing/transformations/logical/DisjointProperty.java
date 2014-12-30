package eu.ldbc.semanticpublishing.transformations.logical;

import java.util.ArrayList;
import java.util.Random;

import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.LinkedHashModel;

import eu.ldbc.semanticpublishing.generators.data.AbstractAsynchronousWorker;
import eu.ldbc.semanticpublishing.generators.data.sesamemodelbuilders.SesameBuilder;
import eu.ldbc.semanticpublishing.transformations.InvalidTransformation;
import eu.ldbc.semanticpublishing.transformations.Transformation;

public class DisjointProperty implements Transformation{
	private static final String core = "http://www.bbc.co.uk/ontologies/coreconcepts/";

	
	
	private ArrayList<ArrayList<String>> PropertyDisjointness; 
	public DisjointProperty(AbstractAsynchronousWorker worker){
		//this ArrayList contains the property disjointness.
		PropertyDisjointness = new ArrayList<ArrayList<String>>();
		ArrayList<String> temp = new ArrayList<String>();
		temp.add(core + "facebook");
		temp.add(core + "twitter");
		temp.add(core + "officialHomepage");
		PropertyDisjointness.add(temp);
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
	    	for(int i = 0; i < PropertyDisjointness.size(); i++){
	    		ArrayList<String> list = PropertyDisjointness.get(i);
	    		for(int j = 0; j < list.size(); j++){
	    			if(list.contains(s.getPredicate().stringValue())){
	    				Random rand = new Random();
	    				int disj = 0;
	    				while(list.get(disj).equals(s.getPredicate().toString())){disj = rand.nextInt(list.size());}
	    				model.add(s.getSubject(), SesameBuilder.sesameValueFactory.createURI(list.get(disj)),s.getPredicate() ,s.getContext());
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
