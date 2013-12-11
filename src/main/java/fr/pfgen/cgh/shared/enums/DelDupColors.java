package fr.pfgen.cgh.shared.enums;

public enum DelDupColors {
	RED, GREEN, BLUE, YELLOW, CYAN, MAGENTA;
	
	public static DelDupColors parse(String s){
		if (s==null || s.isEmpty()) return null;
		s=s.toUpperCase();
		for(DelDupColors s2 : values()){
			if (s2.name().equals(s)) return s2;
		}
		return null;
	}
	
	public static String[] enumAsStringArray(){
		int i = 0;  
	    String[] result = new String[values().length];  
	    for (DelDupColors value: values()) {  
	        result[i++] = value.name();  
	    }  
	    return result;  
	}
}
