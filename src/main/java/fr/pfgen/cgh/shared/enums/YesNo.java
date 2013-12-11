package fr.pfgen.cgh.shared.enums;

public enum YesNo {
	YES,NO;
	
	public static Boolean getBoolean(YesNo s){
		if (s==null) return null;
		if (s.equals(YES)){
			return true;
		}else if (s.equals(NO)){
			return false;
		}else{
			throw new IllegalArgumentException("Bad enum type: "+s.toString());
		}
	}
	
	public static YesNo setStatus(Boolean b){
		if (b==null) return null;
		if (b){
			return YES;
		}else{
			return NO;
		}
	}
	
	public static YesNo parse(String s){
		if (s==null || s.isEmpty()) return null;
		s=s.toUpperCase();
		for(YesNo s2 : values()){
			if (s2.name().equals(s)) return s2;
		}
		return null;
	}
}
