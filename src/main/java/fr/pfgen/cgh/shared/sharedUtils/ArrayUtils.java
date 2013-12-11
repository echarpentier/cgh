package fr.pfgen.cgh.shared.sharedUtils;

import java.util.ArrayList;
import java.util.List;

public class ArrayUtils {
	
	public static <T1,T2> List<T1> transform(T2 array[], Transformer<T2,T1> transformer) {
		List<T1> L = new ArrayList<T1>(array.length);
		for(T2 object:array) L.add(transformer.transform(object));
		return L;
	}
}
