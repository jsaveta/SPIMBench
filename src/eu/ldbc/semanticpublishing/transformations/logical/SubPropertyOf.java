package eu.ldbc.semanticpublishing.transformations.logical;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.LinkedHashModel;

import eu.ldbc.semanticpublishing.generators.data.AbstractAsynchronousWorker;
import eu.ldbc.semanticpublishing.generators.data.sesamemodelbuilders.SesameBuilder;
import eu.ldbc.semanticpublishing.transformations.InvalidTransformation;
import eu.ldbc.semanticpublishing.transformations.Transformation;

public class SubPropertyOf implements Transformation{
	private static final String cworkNamespace = "http://www.bbc.co.uk/ontologies/creativework/";
	private static final String core = "http://www.bbc.co.uk/ontologies/coreconcepts/";
	private static final String travel = "http://www.co-ode.org/roberts/travel.owl#";

	private Map<String,ArrayList<String>> SubPropertyMap; 
	public SubPropertyOf(AbstractAsynchronousWorker worker){
		SubPropertyMap = new HashMap<String,ArrayList<String>>();
		ArrayList<String> temp = new ArrayList<String>();
		
		temp.add(core + "twitter");
		temp.add(core + "facebook");
		temp.add(core + "oficcialHomepage");
		SubPropertyMap.put(core+"primaryTopicOf",temp);
		
		temp = new ArrayList<String>();
		temp.add(cworkNamespace + "about");
		temp.add(cworkNamespace + "mentions");
		SubPropertyMap.put(cworkNamespace + "tag",temp);
		
		temp = new ArrayList<String>();
		temp.add(travel +"hasArea");
		SubPropertyMap.put(travel +"hasPhysicalProperty",temp);
		
		temp = new ArrayList<String>();
		temp.add(travel +"hasPopulation");
		SubPropertyMap.put(travel +"hasStatistic",temp);
		
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
		String key = "";
		for (Entry<String, ArrayList<String>> entry : SubPropertyMap.entrySet()) {
			 if(entry.getValue().contains(s.getPredicate().stringValue())){
				 key = entry.getKey();
				 model.add(s.getSubject(), SesameBuilder.sesameValueFactory.createURI(key),s.getObject(),s.getContext());
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
