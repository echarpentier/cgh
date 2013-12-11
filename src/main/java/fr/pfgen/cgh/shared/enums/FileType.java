package fr.pfgen.cgh.shared.enums;

public enum FileType {
	JPG,PNG,PDF,ZIP,GZ,TARGZ
		{
			@Override
			protected boolean validExtension(String filename) {
				return filename.endsWith(".TGZ") || filename.endsWith(".TAR.GZ");
			}
		}
	,TXT,TAR;
	
	protected boolean validExtension(String filename) {
		return filename.endsWith("."+this.name());
	}
	
	public static FileType getFileType(String name){
		name=name.toUpperCase();
		for(FileType t:values()) if(t.validExtension(name)) return t;
		return null;
	}
}
