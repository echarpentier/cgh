package fr.pfgen.cgh.client.services;

import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import fr.pfgen.cgh.shared.enums.QCimages;
import fr.pfgen.cgh.shared.records.ArrayRecord;

@RemoteServiceRelativePath("ArrayService")
public interface ArrayService extends GenericGwtRpcService<ArrayRecord> {

	String downloadArrayResults(int arrayID);
	List<String> getQCImagesForArray(int arrayID, QCimages image);
	Map<Integer,String> getFrameIdsForArray(int arrayID);
}
