/**
 * 
 */
package eu.ldbc.semanticpublishing.transformations.lexical;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.vocabulary.XMLSchema;

import eu.ldbc.semanticpublishing.generators.data.sesamemodelbuilders.SesameBuilder;
import eu.ldbc.semanticpublishing.transformations.DataValueTransformation;

/**
 * @author Alfio Ferrara, Universita` degli Studi di Milano
 * @date 19/mag/2010
 * 
 * This takes a Date as input and returns a string representing the date according to various formats
 * 
 */
public class ChangeDateFormat implements DataValueTransformation {
	private int format;
	private DateFormat sourceFormat;
	
	public ChangeDateFormat(){}
	
	public ChangeDateFormat(String sourceFormat, int format){
		this.format = format;
		this.sourceFormat = new SimpleDateFormat(sourceFormat, Locale.US);
	}

	public String print(){
		String name = this.getClass().toString().substring(this.getClass().toString().lastIndexOf(".") + 1);
		return name + "\t" + format;
	}

	/* (non-Javadoc)
	 * @see it.unimi.dico.islab.iimb.transfom.Transformation#execute(java.lang.Object)
	 */
	@SuppressWarnings("finally")
	@Override
	public Object execute(Object arg) {
		Locale.setDefault(Locale.ENGLISH);
		String f = arg.toString();
		try {
			Date d = this.sourceFormat.parse(f);
			DateFormat df = DateFormat.getDateInstance(this.format);
			f = df.format(d);
		} catch (ParseException e) {
		} finally{
			return SesameBuilder.sesameValueFactory.createLiteral(f,XMLSchema.DATETIME); 
		}
	}

	@Override
	public Model executeStatement(Statement statement) {
		// TODO Auto-generated method stub
		return null;
	}
  	
//	 public static void main(String args[]) {
//	        // Create Date object.
//	        Date date = new Date();
//	        // Specify the desired date format
//	        String DATE_FORMAT = "yyyy/MM/dd";
//	        // Create object of SimpleDateFormat and pass the desired date format.
//	        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
//	        /*
//	         * Use format method of SimpleDateFormat class to format the date.
//	         */
//	        System.out.println("Today is " + sdf.format(date));
//	        Object arg = sesameValueFactory.createLiteral(sdf.format(date),XMLSchema.DATETIME);
//	        System.out.println("arg " + arg.toString());
//	        
//	        Locale.setDefault(Locale.ENGLISH);
//			String f = arg.toString();
//			DateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
//			try {
//				Date d = sourceFormat.parse(f);
//				DateFormat df = DateFormat.getDateInstance(DateFormat.LONG);
//				f = df.format(d);
//			} catch (ParseException e) {
//			} 
//			
//			  System.out.println("f " + f.toString());
//	 }  
}


//
//package eu.ldbc.semanticpublishing.transformations.lexical;
//
//import java.text.DateFormat;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.Locale;
//
//import org.openrdf.model.Model;
//import org.openrdf.model.Statement;
//import org.openrdf.model.vocabulary.XMLSchema;
//
//import eu.ldbc.semanticpublishing.generators.data.sesamemodelbuilders.SesameBuilder;
//import eu.ldbc.semanticpublishing.transformations.DataValueTransformation;
//
///**
// * @author Alfio Ferrara, Universita` degli Studi di Milano
// * @date 19/mag/2010
// * 
// * This takes a Date as input and returns a string representing the date according to various formats
// * 
// */
//public class ChangeDateFormat implements DataValueTransformation {
//	private int format;
//	private DateFormat sourceFormat;
//	
//	public ChangeDateFormat(){}
//	
//	public ChangeDateFormat(String sourceFormat, int format){
//		this.format = format;
//		this.sourceFormat = new SimpleDateFormat(sourceFormat, Locale.US);
//	}
//
//	public String print(){
//		String name = this.getClass().toString().substring(this.getClass().toString().lastIndexOf(".") + 1);
//		return name + "\t" + format;
//	}
//
//	/* (non-Javadoc)
//	 * @see it.unimi.dico.islab.iimb.transfom.Transformation#execute(java.lang.Object)
//	 */
//	@SuppressWarnings("finally")
//	@Override
//	public Object execute(Object arg) {
//		Locale.setDefault(Locale.ENGLISH);
//		//String f = "2011-01-02 16:29:49.913+02:00";
//		//"2011-03-12T05:53:45.447+02:00"^^<http://www.w3.org/2001/XMLSchema#dateTime>
//		String f = arg.toString();
//		f = f.replace("^^<http://www.w3.org/2001/XMLSchema#dateTime>", "");
//		f = f.replace("T", " ");
//		f = f.replace("\"", " ");
//		//System.out.println("date: " + f);
//		
//		try {
//			Date d = this.sourceFormat.parse(f);
//			DateFormat df = DateFormat.getDateInstance(this.format);
//			f = df.format(d);
//		//	System.out.println("f : "+ f);
//				
//		} catch (ParseException e) {
//		} finally{
//			return SesameBuilder.sesameValueFactory.createLiteral(f.replace("\"", ""),XMLSchema.DATETIME); 
//		}
//	}
//
//	@Override
//	public Model executeStatement(Statement statement) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//    
//}
