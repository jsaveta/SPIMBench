package eu.ldbc.semanticpublishing.transformations.logical;

import java.util.Random;

import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.LinkedHashModel;

import eu.ldbc.semanticpublishing.generators.data.AbstractAsynchronousWorker;
import eu.ldbc.semanticpublishing.generators.data.sesamemodelbuilders.SesameBuilder;
import eu.ldbc.semanticpublishing.transformations.Transformation;

public class CWDifferentFrom implements Transformation{
	AbstractAsynchronousWorker worker;
	private static final String rdfTypeNamespace = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
	private static final String owldifferentFrom = "http://www.w3.org/2002/07/owl#differentFrom";
	private static final String thing = "http://www.bbc.co.uk/things";
//	TransformationsCall transformationscall;
//	private Map <String, Transformation> TransformationConf;
//	private static final String transformationPath =  "eu.ldbc.semanticpublishing.transformations.";
	Random random = new Random();
	
	public CWDifferentFrom(AbstractAsynchronousWorker worker){
		this.worker = worker;
//		this.transformationscall = new TransformationsCall(worker);
		
	}

@Override
	public Object execute(Object arg) {
	return null;
	}

	@Override
	public String print() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Model executeStatement(Statement st) {
		Model differentFromModel =  new LinkedHashModel();
    	Model model = new LinkedHashModel();
		int randomIndexDifferentFrom = 0;
		Value object = null;
		Value o11 = null; 	
		Resource subject_original_cw = null;
		URI predicate_original_cw = null;
    	Resource subject_differentfrom = null;
    	Boolean differentFromStatement = false;
    	if(!worker.getsesameModelArrayList().isEmpty() && worker.getsesameModelArrayList().size()>=2){
    	int times = 0;
    	do{
			while(randomIndexDifferentFrom != 0){ 
				randomIndexDifferentFrom = random.nextInt(worker.getsesameModelArrayList().size()); 
			}
    		differentFromModel = worker.getsesameModel2ArrayList().get(randomIndexDifferentFrom);
			Model s11 = worker.getsesameModelArrayList().get(randomIndexDifferentFrom);
			for (Statement stat : differentFromModel){
				object = stat.getObject();
				subject_original_cw = stat.getSubject();
				predicate_original_cw = stat.getPredicate();
				for (Statement st1 : s11){
					o11 = st1.getObject();
					break;
				}
				break;
			}
			times++;
		}while( (times <  worker.getsesameModelArrayList().size()) && ( !o11.toString().equals(object.toString())|| !subject_original_cw.toString().startsWith(thing) || !predicate_original_cw.toString().equals(rdfTypeNamespace)));

		subject_differentfrom = SesameBuilder.sesameValueFactory.createURI(worker.getru().GenerateUniqueID(subject_original_cw.toString()));
    	
    	}
    	if(subject_differentfrom != null){
	    	for (Statement statement : differentFromModel){
	    		    		
	    		if(!statement.getSubject().toString().equals(subject_original_cw.toString())){
	    			if(!differentFromStatement){
	    				worker.getSesameModel_2().add(subject_differentfrom , SesameBuilder.sesameValueFactory.createURI(owldifferentFrom),subject_original_cw,(Resource)null);
	    				differentFromStatement = true;
	    			}
	    			worker.getSesameModel_2().add((Resource)statement.getSubject(), (URI)statement.getPredicate(),(Value) statement.getObject(), (Resource)statement.getContext());				
				}
				else{
					worker.getSesameModel_2().add(subject_differentfrom, (URI)statement.getPredicate(),(Value) statement.getObject(), (Resource)statement.getContext());		
				}
			}	
	    	if(subject_original_cw!=null){
	    		model.add(subject_original_cw,SesameBuilder.sesameValueFactory.createURI(owldifferentFrom),subject_differentfrom,(Resource)null);
	    	}
    	}
    	return model;
	}

}
