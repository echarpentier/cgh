package fr.pfgen.cgh.shared.enums;

import java.util.List;

import fr.pfgen.cgh.shared.sharedUtils.ArrayUtils;
import fr.pfgen.cgh.shared.sharedUtils.Transformer;

public enum UserStatus {
	ADMIN,ADVANCED,SIMPLE,RESTRICTED;
	
	public static UserStatus parse(String s){
		if (s==null || s.isEmpty()) return null;
		s=s.toUpperCase();
		for(UserStatus s2 : values()){
			if (s2.name().equals(s)) return s2;
		}
		return null;
	}
	
	public static List<String> toStringList(){
		return ArrayUtils.transform(values(), new Transformer<UserStatus,String>(){
				@Override
				public String transform(UserStatus object) {
					return object.toString();
				}
		});
	}
	
	//enum in DB is "admin,advanced,simple,restricted"
	public static String toDBformat(UserStatus s){
		return s.toString().toLowerCase();
	}
}
