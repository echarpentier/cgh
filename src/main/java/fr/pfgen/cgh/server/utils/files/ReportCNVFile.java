package fr.pfgen.cgh.server.utils.files;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fr.pfgen.cgh.server.utils.IOUtils;
import fr.pfgen.cgh.shared.records.DetectionRecord;

public class ReportCNVFile {

	private File file;
	
	public ReportCNVFile(File f){
		if (f==null) throw new IllegalArgumentException("file cannot be null");
		if (!f.exists()) throw new RuntimeException("file does not exist: "+f.getAbsolutePath());
		if (!f.canRead()) throw new RuntimeException("file cannot be read: "+f.getAbsolutePath());
		this.file = f;
	}
	
	public List<DetectionRecord> getDetectionsFromFile(){
		List<DetectionRecord> list = new ArrayList<DetectionRecord>();
		
		BufferedReader br = null;
		try{
			br = IOUtils.openFile(file);
			String line;
			while((line = br.readLine())!=null){
				if (line.startsWith("file\tindex") || line.isEmpty()) continue;
				String[] linesplit = line.split("\\t");
				if (linesplit.length!=8) throw new RuntimeException("invalid detection line: "+line+" in file: "+file.getAbsolutePath());
				DetectionRecord r = new DetectionRecord();
				r.setChr(Integer.parseInt(linesplit[2].replaceFirst("chr", "")));
				r.setStart(Integer.parseInt(linesplit[3]));
				r.setEnd(Integer.parseInt(linesplit[4]));
				r.setProbeNumber(Integer.parseInt(linesplit[6]));
				r.setLRmedian(Double.parseDouble(linesplit[7]));
				list.add(r);
			}
			br.close();
			return list;
		}catch(IOException e){
			e.printStackTrace();
			return null;
		}finally{
			IOUtils.safeClose(br);
		}
	}
}
