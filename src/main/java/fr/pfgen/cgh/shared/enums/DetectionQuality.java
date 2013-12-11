package fr.pfgen.cgh.shared.enums;

public enum DetectionQuality {
	TP,FP,SM;
	
	public static DetectionQuality parse(String s){
		if (s==null || s.isEmpty()) return null;
		s=s.toUpperCase();
		for(DetectionQuality s2 : values()){
			if (s2.name().equals(s)) return s2;
		}
		return null;
	}
}
