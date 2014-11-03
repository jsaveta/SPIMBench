package eu.ldbc.semanticpublishing.transformations;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.Rio;

import eu.ldbc.semanticpublishing.generators.data.AbstractAsynchronousWorker;
import eu.ldbc.semanticpublishing.generators.data.RandomWorker;
import eu.ldbc.semanticpublishing.generators.data.sesamemodelbuilders.SesameBuilder;
import eu.ldbc.semanticpublishing.util.SesameUtils;

public class WriteIntermediateGS extends RandomWorker {
	Model GSModel = null;
	private String exactmatch;
	private static int count;
	static Throwable t = new Throwable(); 
	static StackTraceElement[] elements = t.getStackTrace(); 

	private AbstractAsynchronousWorker worker_; 
	String value1;String value2; Double weight;String transformation;String property;
	
	public WriteIntermediateGS(AbstractAsynchronousWorker worker){
		super();
		exactmatch = "http://www.w3.org/2004/02/skos/core#exactMatch";
		this.worker_ = worker;
		
	}
	
	public void WriteGSAsTriples(String value1,String value2, Double weight,String transformation,String property, Model sesameModel_GS,FileOutputStream fos_3) throws IOException, RDFHandlerException{
		RDFFormat rdfFormat = SesameUtils.parseRdfFormat("turtle");
		sesameModel_GS = new LinkedHashModel();
		this.value1 = value1;
		this.value2 = value2;
		this.weight = weight;
		this.transformation = transformation;
		this.property = property;
		Resource subject = SesameBuilder.sesameValueFactory.createURI(this.value1);
		URI predicate = SesameBuilder.sesameValueFactory.createURI(exactmatch);
		Value object = SesameBuilder.sesameValueFactory.createURI(this.value2);
		sesameModel_GS.add((Resource)subject,(URI)predicate,(Value)object); //value1 equals value2

		URI transf_u = SesameBuilder.sesameValueFactory.createURI("http://www.trans/"+ Long.toString(count)); //atomiclong
		Resource transf_r = SesameBuilder.sesameValueFactory.createURI("http://www.trans/"+ Long.toString(count)); //atomiclong
		
		URI numOfTrans = SesameBuilder.sesameValueFactory.createURI("http://www.transf_num");
		
		
		URI weight_ = SesameBuilder.sesameValueFactory.createURI("http://www.weight");
		URI type_ = SesameBuilder.sesameValueFactory.createURI("http://www.type");
		URI prop_ = SesameBuilder.sesameValueFactory.createURI("http://www.prop");

		Value transformation_ = SesameBuilder.sesameValueFactory.createLiteral(this.transformation);
		sesameModel_GS.add(subject,numOfTrans,transf_u); // value1 has trans_num transf
		sesameModel_GS.add(transf_r,type_,transformation_); //transf has transfrormation type a number from the map
		
		if(this.worker_.getFtransformations().containsKey(this.worker_.getFileName1())){
			ArrayList<Double> values = this.worker_.getFtransformations().get(this.worker_.getFileName1()); // first, copy out the existing values
			values.set(Integer.parseInt(this.transformation),values.get((int) Double.parseDouble(this.transformation))+ 1);
			this.worker_.getFtransformations().remove(this.worker_.getFileName1());
			this.worker_.getFtransformations().put(this.worker_.getFileName1(), values);

		}

			
		if(this.property  != null && !this.property.equals("")){
			if(property.startsWith("http")) {
				
				URI prop_name_u = SesameBuilder.sesameValueFactory.createURI(property); 
				sesameModel_GS.add(transf_r,prop_, prop_name_u);
			}
			else {
				Value prop_name_v = SesameBuilder.sesameValueFactory.createLiteral(property); 
				sesameModel_GS.add(transf_r,prop_, prop_name_v);
			}
		}
		Value weight_num = SesameBuilder.sesameValueFactory.createLiteral(this.weight.toString());
		sesameModel_GS.add(transf_r,weight_,weight_num); // the above equality has weight weight_num

		Rio.write(sesameModel_GS, fos_3, rdfFormat);
		count++;
	}


}
