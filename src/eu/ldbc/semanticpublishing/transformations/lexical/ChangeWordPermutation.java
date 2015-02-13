package eu.ldbc.semanticpublishing.transformations.lexical;

import java.util.Collections;
import java.util.List;

import org.openrdf.model.Model;
import org.openrdf.model.Statement;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import eu.ldbc.semanticpublishing.transformations.DataValueTransformation;
import eu.ldbc.semanticpublishing.transformations.InvalidTransformation;

public class ChangeWordPermutation implements DataValueTransformation{
	@SuppressWarnings("finally")
	@Override
	public Object execute(Object arg) {
		
		String f = (String)arg;
		List<String> list = Lists.newArrayList(Splitter.on(" ").split(f));
		
		if(arg instanceof String){
			Collections.swap(list,0,(list.size()-1));

		}else{
			try {
				throw new InvalidTransformation();
			} catch (InvalidTransformation e) {
				e.printStackTrace();
			}finally{
				return list.toString();
			}
		}
		return list.toString().replace("\\", "").replace(",", "").replace("[", "").replace("]", "").replace(";", "");
	}

	@Override
	public String print() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Model executeStatement(Statement statement) {
		// TODO Auto-generated method stub
		return null;
	}

}
