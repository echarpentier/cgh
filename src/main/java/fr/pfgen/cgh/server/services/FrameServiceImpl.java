package fr.pfgen.cgh.server.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import fr.pfgen.cgh.client.services.FrameService;
import fr.pfgen.cgh.server.database.ConnectionPool;
import fr.pfgen.cgh.server.database.DetectionTable;
import fr.pfgen.cgh.server.database.FrameTable;
import fr.pfgen.cgh.server.database.QcParamTable;
import fr.pfgen.cgh.server.utils.DatabaseUtils;
import fr.pfgen.cgh.server.utils.IOUtils;
import fr.pfgen.cgh.shared.enums.DetectionQuality;
import fr.pfgen.cgh.shared.records.DetectionRecord;
import fr.pfgen.cgh.shared.records.FrameRecord;
import fr.pfgen.cgh.shared.sharedUtils.GenericGwtRpcList;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class FrameServiceImpl extends RemoteServiceServlet implements FrameService{

	private ConnectionPool pool;
	private Hashtable<String, File> appFiles;
	
	@SuppressWarnings("unchecked")
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		pool = (ConnectionPool)getServletContext().getAttribute("ConnectionPool");
		appFiles = (Hashtable<String, File>)getServletContext().getAttribute("ApplicationFiles");
	}
	
	@Override
	public List<FrameRecord> fetch(Integer startRow, Integer endRow, String sortBy, Map<String, String> filterCriteria) {
		GenericGwtRpcList<FrameRecord> outList = new GenericGwtRpcList<FrameRecord>();
		
		String query = FrameTable.constructQuery(sortBy,filterCriteria);
		
		outList.setTotalRows(DatabaseUtils.countRowInQuery(pool, query,false));
		
		List<FrameRecord> out = FrameTable.getFrames(pool,query,startRow,endRow);
		
		outList.addAll(out);
    	
    	return outList;
	}

	@Override
	public FrameRecord add(FrameRecord data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FrameRecord update(FrameRecord data) {
		return FrameTable.updateIds(pool, data);
	}

	@Override
	public void remove(FrameRecord data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String download(String sortBy, Map<String, String> filterCriteria) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getQcParamNames() {
		return QcParamTable.getQcParamNames(pool);
	}

	@Override
	public String createWigFile(List<Integer> frameIDList, int chr) {
		StringBuilder sb = new StringBuilder();
		for (Integer frameID : frameIDList) {
			Map<String, String> crits = new HashMap<String, String>(1);
			crits.put("frame_id", frameID.toString());
			String query = FrameTable.constructQuery(null, crits);
			FrameRecord frameRec = FrameTable.getFrames(pool, query, null, null).get(0);
			if (frameRec==null) return null;
			String title;
			String chrForGB;
			if (chr==23){ chrForGB="X";}
			else if (chr==24){ chrForGB="Y";}
			else{chrForGB=String.valueOf(chr);}
			
			if (frameRec.getRefName()!=null && !frameRec.getRefName().equalsIgnoreCase("null") && frameRec.getTestName()!=null && !frameRec.getTestName().equalsIgnoreCase("null")){
				title=frameRec.getTestName()+"_"+frameRec.getRefName();
			}else{
				title=frameRec.getName();
			}
			
			sb.append("track type=wiggle_0 name=\""+title+"_chr"+chrForGB+"\" description=\"chr"+chrForGB+" log2ratio\" color=0,200,0 altColor=200,0,0 yLineOnOff=on windowingFunction=mean visibility=full autoScale=off viewLimits=-2:2\nvariableStep chrom=chr"+chrForGB+" span=60\n");

			BufferedReader br = null;
			File sigFile = new File(frameRec.getResultFolderPath(), frameRec.getName()+"_sigFrame.gff");
			if (!sigFile.exists()) throw new RuntimeException("Cannot find sig file: "+sigFile.getAbsolutePath());
			try{
				br = IOUtils.openFile(sigFile);
				String line;
				int prevStart = 0;
				while((line=br.readLine())!=null){
					String[] linesplit = line.split("\\t");
					int col3 = Integer.parseInt(linesplit[3]);
					if (!linesplit[0].equals("chr"+chr)){
						continue;
					}else if (col3 == prevStart){
						Integer start = col3+1;
						sb.append(start.toString()+"\t"+linesplit[5]+"\n");
						prevStart = start;
					}else{
						sb.append(linesplit[3]+"\t"+linesplit[5]+"\n");
						prevStart = col3;
					}
				}
				br.close();
			}catch (IOException e) {
				e.printStackTrace();
				return null;
			}finally{
				IOUtils.safeClose(br);
			}
		}
		
		try {
			File wigFile = File.createTempFile("PV_"+String.valueOf(chr)+"__", ".wig", appFiles.get("temporaryFolder"));
			IOUtils.stringToFile(sb.toString(), wigFile);
			return wigFile.getAbsolutePath();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public String createGffFile(List<Integer> frameIDList) {
		StringBuilder dupSB = new StringBuilder();
		StringBuilder delSB = new StringBuilder();
		
		String genomicBuild = "";
		for (Integer frameID : frameIDList) {
			Map<String, String> crits = new HashMap<String, String>(1);
			crits.put("frame_id", frameID.toString());
			String frameQuery = FrameTable.constructQuery(null, crits);
			FrameRecord frameRec = FrameTable.getFrames(pool, frameQuery, null, null).get(0);
			if (frameRec==null) return null;
			String title;
			
			if (frameRec.getRefName()!=null && !frameRec.getRefName().equalsIgnoreCase("null") && frameRec.getTestName()!=null && !frameRec.getTestName().equalsIgnoreCase("null")){
				title=frameRec.getTestName();
			}else{
				title=frameRec.getName();
			}
			
			String detQuery = DetectionTable.constructQuery(null, crits);
			List<DetectionRecord> detRecs = DetectionTable.getDetections(pool, detQuery, null, null);
			
			boolean firstDet = true;
			
			for (DetectionRecord detRec : detRecs) {
				if (firstDet){
					genomicBuild = detRec.getGenomicBuild();
					firstDet=false;
				}
				if (detRec.getQuality()!=null && (detRec.getQuality()==DetectionQuality.SM || detRec.getQuality()==DetectionQuality.FP)){
					continue;
				}
				String chrForGB;
				int chr = detRec.getChr();
				if (chr==23){ chrForGB="X";}
				else if (chr==24){ chrForGB="Y";}
				else{chrForGB=String.valueOf(chr);}
				
				double lrMedian = detRec.getLRmedian();
				if (lrMedian > 0){
					dupSB.append("chr"+chrForGB+"\tGain\t"+title+"_"+detRec.getID()+"\t"+detRec.getStart()+"\t"+detRec.getEnd()+"\t.\t.\t.\t"+title+"_"+detRec.getID()+"\n");
				}else{
					delSB.append("chr"+chrForGB+"\tLoss\t"+title+"_"+detRec.getID()+"\t"+detRec.getStart()+"\t"+detRec.getEnd()+"\t.\t.\t.\t"+title+"_"+detRec.getID()+"\n");
				}
			}
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append("track name=\"Duplications\" description=\"gain calls\" color=0,255,0 db="+genomicBuild+"\n");
		sb.append(dupSB);
		sb.append("track name=\"Deletions\" description=\"loss calls\" color=255,0,0 db="+genomicBuild+"\n");
		sb.append(delSB);
		
		try {
			File gffFile = File.createTempFile("CT__", ".gff", appFiles.get("temporaryFolder"));
			IOUtils.stringToFile(sb.toString(), gffFile);
			return gffFile.getAbsolutePath();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
