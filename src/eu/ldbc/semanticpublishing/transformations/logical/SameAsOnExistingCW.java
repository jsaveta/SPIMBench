package eu.ldbc.semanticpublishing.transformations.logical;

import java.util.Random;

import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;
import org.openrdf.model.impl.LinkedHashModel;

import eu.ldbc.semanticpublishing.generators.data.AbstractAsynchronousWorker;
import eu.ldbc.semanticpublishing.generators.data.sesamemodelbuilders.SesameBuilder;
import eu.ldbc.semanticpublishing.transformations.Transformation;

public class SameAsOnExistingCW implements Transformation{
	private static final String owlsameAs = "http://www.w3.org/2002/07/owl#sameAs";
	private static final String thing = "http://www.bbc.co.uk/things";
	
	AbstractAsynchronousWorker worker;
	Random random = new Random();
	
	public SameAsOnExistingCW(AbstractAsynchronousWorker worker){
		this.worker = worker;
		
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

@Override
public Model executeStatement(Statement statement) {
	Model return_model = new LinkedHashModel();
	Model sameAs2ExistCWs1 = new LinkedHashModel();
	Model sameAs2ExistCWs2 = new LinkedHashModel();
	
	Resource subject1 = null;
	Resource subject2 = null;
	Value object1 = null;
	Value object2 = null;
	Value o11 = null;
	Value o12 = null;
	int randomIndexSameAs1 = 0;
	int randomIndexSameAs2 = 0;
	if(!worker.getsesameModelArrayList().isEmpty() && worker.getsesameModelArrayList().size() >= 2){
		int times = 0;
	do{
		randomIndexSameAs1 = random.nextInt(worker.getsesameModel2ArrayList().size()); 
		randomIndexSameAs2 = random.nextInt(worker.getsesameModel2ArrayList().size());
		while(randomIndexSameAs1 != 0 && randomIndexSameAs2!=0 && (times <  worker.getsesameModelArrayList().size())){ 
			sameAs2ExistCWs1 = worker.getsesameModel2ArrayList().get(randomIndexSameAs1);
			sameAs2ExistCWs2 = worker.getsesameModel2ArrayList().get(randomIndexSameAs2);
			times++;
		}
		

		Model s11 = worker.getsesameModelArrayList().get(randomIndexSameAs1);
		Model s12 = worker.getsesameModelArrayList().get(randomIndexSameAs2);
		if(sameAs2ExistCWs1 != null && sameAs2ExistCWs2 != null){
			times = 0;
			for (Statement st : sameAs2ExistCWs1){
				subject1 = st.getSubject();
				object1 = st.getObject();
				for (Statement st1 : s11){
					o11 = st1.getObject();
					break;
				}
				break;
			}
		
			for (Statement st : sameAs2ExistCWs2){
				subject2 = st.getSubject();
				object2 = st.getObject();
				for (Statement st1 : s12){
					o12 = st1.getObject();
					break;
				}
				break;
			}
		}
		times++;
		//TODO check this while statement
	}while((times <  worker.getsesameModelArrayList().size()) &&  (subject1.toString().equals(subject2.toString()) || !object1.toString().equals(object2.toString()) || !o11.toString().equals(object1.toString()) ||!o12.toString().equals(object2.toString()) || !(subject1.toString().startsWith(thing)) || !(subject2.toString().startsWith(thing))));//{
	}
	if(subject1 != null && subject2 != null){
	worker.getSesameModel_2().add(subject1, SesameBuilder.sesameValueFactory.createURI(owlsameAs), subject2, (Resource)null);
	return_model.add(subject1, SesameBuilder.sesameValueFactory.createURI(owlsameAs), subject2, (Resource)null);
	}
	return return_model;
}


}
