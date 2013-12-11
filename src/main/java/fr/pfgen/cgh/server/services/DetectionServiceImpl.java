package fr.pfgen.cgh.server.services;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import fr.pfgen.cgh.client.services.DetectionService;
import fr.pfgen.cgh.server.database.ConnectionPool;
import fr.pfgen.cgh.server.database.DetectionTable;
import fr.pfgen.cgh.server.utils.DatabaseUtils;
import fr.pfgen.cgh.shared.enums.DetectionQuality;
import fr.pfgen.cgh.shared.records.DetectionRecord;
import fr.pfgen.cgh.shared.sharedUtils.GenericGwtRpcList;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class DetectionServiceImpl extends RemoteServiceServlet implements DetectionService{

	private ConnectionPool pool;
	private Hashtable<String, File> appFiles;
	
	@SuppressWarnings("unchecked")
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		pool = (ConnectionPool)getServletContext().getAttribute("ConnectionPool");
		appFiles = (Hashtable<String, File>)getServletContext().getAttribute("ApplicationFiles");
	}
	
	@Override
	public List<DetectionRecord> fetch(Integer startRow, Integer endRow, String sortBy, Map<String, String> filterCriteria) {
		GenericGwtRpcList<DetectionRecord> outList = new GenericGwtRpcList<DetectionRecord>();
	    
		String query = DetectionTable.constructQuery(sortBy,filterCriteria);
    	
    	outList.setTotalRows(DatabaseUtils.countRowInQuery(pool, query,false));
    	
    	List<DetectionRecord> out = DetectionTable.getDetections(pool,query,startRow,endRow);
    	outList.setTotalRows(2);
		
    	outList.addAll(out);
    	
    	return outList;
	}

	@Override
	public DetectionRecord add(DetectionRecord data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public synchronized DetectionRecord update(DetectionRecord data) {
		return DetectionTable.updateRecordQuality(pool, data);
		
	}

	@Override
	public void remove(DetectionRecord data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String download(String sortBy, Map<String, String> filterCriteria) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean setSMForFrame(int frameID) {
		return DetectionTable.setQualityToSmForFrame(pool, frameID); 
	}

	@Override
	public boolean setNoSMForFrame(int frameID) {
		return DetectionTable.setQualityToNoSmForFrame(pool, frameID);
	}
}
