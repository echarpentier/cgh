package fr.pfgen.cgh.server.utils.files;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import fr.pfgen.cgh.server.utils.IOUtils;

public class ReportQCFile {

	private File file;
	
	public ReportQCFile(File f){
		if (f==null) throw new IllegalArgumentException("file cannot be null");
		if (!f.exists()) throw new RuntimeException("file does not exist: "+f.getAbsolutePath());
		if (!f.canRead()) throw new RuntimeException("file cannot be read: "+f.getAbsolutePath());
		this.file = f;
	}
	
	public Map<String, String> getMapFromFile(){
		Map<String, String> map = new HashMap<String, String>();
		BufferedReader br = null;
		
		try{
			br = IOUtils.openFile(file);
			String[] header = br.readLine().split("\\t");
			String[] qcline = br.readLine().split("\\t");
			br.close();
			if (header.length!=qcline.length) throw new RuntimeException("file is not valid qc report file: "+file.getAbsolutePath());
			for (int i = 0; i < header.length; i++) {
				if (header[i].equals("file")) continue;
				if (header[i].equals("mad.d1r")){ map.put("dlrs", qcline[i]);}
				else if (header[i].equals("RP68")){ map.put("quantile68", qcline[i]);}
				else if (header[i].equals("SD")){ map.put("sd", qcline[i]);}
				else if (header[i].equals("%_inclusion")){ map.put("inclusion", qcline[i]);}
				else{map.put(header[i], qcline[i]);}
			}
			return map;
		}catch (IOException e) {
			e.printStackTrace();
			return null;
		}finally{
			IOUtils.safeClose(br);
		}
	}
}
