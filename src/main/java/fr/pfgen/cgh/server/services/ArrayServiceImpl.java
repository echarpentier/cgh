package fr.pfgen.cgh.server.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import fr.pfgen.cgh.client.services.ArrayService;
import fr.pfgen.cgh.server.database.ArrayTable;
import fr.pfgen.cgh.server.database.ConnectionPool;
import fr.pfgen.cgh.server.database.FrameTable;
import fr.pfgen.cgh.server.utils.DatabaseUtils;
import fr.pfgen.cgh.server.utils.IOUtils;
import fr.pfgen.cgh.shared.enums.QCimages;
import fr.pfgen.cgh.shared.records.ArrayRecord;
import fr.pfgen.cgh.shared.sharedUtils.GenericGwtRpcList;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class ArrayServiceImpl extends RemoteServiceServlet implements ArrayService{

	private ConnectionPool pool;
	private Hashtable<String, File> appFiles;
	
	@SuppressWarnings("unchecked")
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		pool = (ConnectionPool)getServletContext().getAttribute("ConnectionPool");
		appFiles = (Hashtable<String, File>)getServletContext().getAttribute("ApplicationFiles");
	}
	
	@Override
	public List<ArrayRecord> fetch(Integer startRow, Integer endRow, String sortBy, Map<String, String> filterCriteria) {
		GenericGwtRpcList<ArrayRecord> outList = new GenericGwtRpcList<ArrayRecord>();
	    
    	String query = ArrayTable.constructQuery(sortBy,filterCriteria);
    	
    	outList.setTotalRows(DatabaseUtils.countRowInQuery(pool, query,false));
    	
    	List<ArrayRecord> out = ArrayTable.getArrays(pool,query,startRow,endRow);
    	
    	outList.addAll(out);
    	
    	return outList;
		
	}

	@Override
	public ArrayRecord add(ArrayRecord data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayRecord update(ArrayRecord data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void remove(ArrayRecord data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String download(String sortBy, Map<String, String> filterCriteria) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String downloadArrayResults(int arrayID) {
		List<File> resultFolders = ArrayTable.getResultFoldersForArray(pool, arrayID);

		try {
		    // Create the ZIP file
		    File zipArchive = File.createTempFile("results__", ".zip", appFiles.get("temporaryFolder"));
		    ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipArchive));

		    // Compress the files
		    for (File resFolder : resultFolders)
		    	{
		    	File[] filesInFolder = resFolder.listFiles();
		    	for (File f : filesInFolder){
					out.putNextEntry(new ZipEntry(resFolder.getName()+"/"+f.getName()));
					
					FileInputStream in = new FileInputStream(f);
					
					IOUtils.copyTo(in, out);
					in.close();
					out.flush();
					out.closeEntry();
				}
			}
		    out.flush();
		    out.finish();
		    out.close();
		    return zipArchive.getAbsolutePath();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<String> getQCImagesForArray(int arrayID, QCimages image) {
		List<File> resultFolders = ArrayTable.getResultFoldersForArray(pool, arrayID);

		String imageSuffix;
		switch (image) {
		case FNUout:
			imageSuffix = "_FNUout.png";
			break;
		case BGNUout:
			imageSuffix = "_BGNUout.png";
			break;
		case MAplot:
			imageSuffix = "_MAplot.png";
			break;
		case GClowess:
			imageSuffix = "_GC_lowess.png";
			break;
		default:
			imageSuffix = null;
			break;
		}
		
		if (imageSuffix==null) return null;
		
		List<String> images = new ArrayList<String>();
		for (File folder : resultFolders) {
			File imageFile = new File(folder, folder.getName().replaceFirst("results_", "")+imageSuffix);
			if (!imageFile.exists()) return null;
			images.add(imageFile.getAbsolutePath());
		}
		
		return images;
	}

	@Override
	public Map<Integer,String> getFrameIdsForArray(int arrayID) {
		return FrameTable.getFrameIdsForArray(pool,arrayID);
	}
}
