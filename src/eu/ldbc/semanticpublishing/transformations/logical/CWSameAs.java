

package eu.ldbc.semanticpublishing.transformations.logical;

import java.text.DateFormat;
import java.util.Map;
import java.util.Random;

import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.vocabulary.XMLSchema;

import eu.ldbc.semanticpublishing.generators.data.AbstractAsynchronousWorker;
import eu.ldbc.semanticpublishing.generators.data.sesamemodelbuilders.SesameBuilder;
import eu.ldbc.semanticpublishing.transformations.Transformation;
import eu.ldbc.semanticpublishing.transformations.TransformationConfiguration;
import eu.ldbc.semanticpublishing.transformations.TransformationsCall;

public class CWSameAs implements Transformation{
	AbstractAsynchronousWorker worker;
	private static final String rdfTypeNamespace = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
	private static final String owlsameAs = "http://www.w3.org/2002/07/owl#sameAs";
	private static final String thing = "http://www.bbc.co.uk/things";
	private static final String cworkNamespace = "http://www.bbc.co.uk/ontologies/creativework/";
//	TransformationsCall transformationscall;
//	private Map <String, Transformation> TransformationConf;
//	private static final String transformationPath =  "eu.ldbc.semanticpublishing.transformations.";
	Random random = new Random();
	
	public CWSameAs(AbstractAsynchronousWorker worker){
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
//		transformationscall.setTransformationConf();
//		TransformationConf = transformationscall.getTransformationConf();
    	Model model = new LinkedHashModel();
		Model sameAsModel = new LinkedHashModel();
		int randomIndexSameAs = 0;
		Value object = null;
		Value o11 = null; 	
		Resource subject_original_cw = null;
		URI predicate_original_cw = null;
    	Resource subject_sameas = null;
    	Boolean sameAsStatement = false;
    	System.out.println("same as "+ worker.getsesameModelArrayList().size());
    	if(!worker.getsesameModelArrayList().isEmpty() && worker.getsesameModelArrayList().size() >= 2){
    	int times = 0;
    	do{
    		while(randomIndexSameAs != 0){ 
				randomIndexSameAs = random.nextInt(worker.getsesameModelArrayList().size()); 
			}
			sameAsModel = worker.getsesameModel2ArrayList().get(randomIndexSameAs);
			Model s11 = worker.getsesameModelArrayList().get(randomIndexSameAs);
			for (Statement stat : sameAsModel){
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
		//TODO check this while statement
    	}while((times <  worker.getsesameModelArrayList().size()) && !o11.toString().equals(object.toString()) && (!object.toString().contains("NewsItem")||
				!object.toString().contains("Programme")|| 
				!object.toString().contains("BlogPost")||
				!subject_original_cw.toString().startsWith(thing)) && !predicate_original_cw.toString().equals(rdfTypeNamespace));
		subject_sameas = SesameBuilder.sesameValueFactory.createURI(worker.getru().GenerateUniqueID(subject_original_cw.toString()));
    	}
    	if(subject_sameas != null){
	    	for (Statement statement : sameAsModel){
	    		if(!statement.getSubject().toString().equals(subject_original_cw.toString())){
	    			if(!sameAsStatement){
	    				worker.getSesameModel_2().add(subject_sameas , SesameBuilder.sesameValueFactory.createURI(owlsameAs),subject_original_cw,(Resource)null);
	    				sameAsStatement = true;
	    			}
	    			worker.getSesameModel_2().add((Resource)statement.getSubject(), (URI)statement.getPredicate(),(Value) statement.getObject(), (Resource)statement.getContext());				
				}
				else{
					if(statement.getPredicate().toString().equals(cworkNamespace + "title")){
						Model tempModel = TransformationConfiguration.aggregatePROPERTIES(worker).executeStatement(statement);
						if(!tempModel.isEmpty()){ 
							for (Statement tempStatement : tempModel){
								worker.getSesameModel_2().add((Resource)subject_sameas,(URI)tempStatement.getPredicate(),(Value)tempStatement.getObject(),(Resource)statement.getContext());											
							}	
						}
					}
					else if(statement.getPredicate().toString().equals(cworkNamespace + "dateCreated") || statement.getPredicate().toString().equals(cworkNamespace + "dateModified")){
						Object temp = (String) TransformationConfiguration.dateFORMAT("yyyy-MM-dd HH:mm:ss", DateFormat.SHORT).execute(statement.getObject().toString()).toString();
						if (temp instanceof Value){
							worker.getSesameModel_2().add(subject_sameas, (URI)statement.getPredicate(),(Value)temp, (Resource)statement.getContext());
						}
					}
					else if (statement.getPredicate().toString().equals(rdfTypeNamespace)){
						worker.getSesameModel_2().add(subject_sameas, (URI)statement.getPredicate(),(Value) statement.getObject(), (Resource)statement.getContext());		
					}
					else{
						//isws edw mia allagi sta transformation... H' na min ginontai toso manually
						String temp = (String) TransformationConfiguration.deleteRANDOMCHARS(0.2).execute(statement.getObject().toString()).toString();
						worker.getSesameModel_2().add(subject_sameas, (URI)statement.getPredicate(),SesameBuilder.sesameValueFactory.createLiteral(temp.replace("\"", "")), (Resource)statement.getContext());
					}
	
				}
			}
    	}
    	if(subject_original_cw != null){
    		model.add(subject_original_cw,SesameBuilder.sesameValueFactory.createURI(owlsameAs),subject_sameas,(Resource)null);
    	}
		return model;
	}
}
