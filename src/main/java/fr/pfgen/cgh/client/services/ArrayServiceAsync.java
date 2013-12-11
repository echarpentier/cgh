package fr.pfgen.cgh.client.services;

import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;

import fr.pfgen.cgh.shared.enums.QCimages;
import fr.pfgen.cgh.shared.records.ArrayRecord;

public interface ArrayServiceAsync extends GenericGwtRpcServiceAsync<ArrayRecord> {

	void downloadArrayResults(int arrayID, AsyncCallback<String> asyncCallback);
	void getQCImagesForArray(int arrayID, QCimages image, AsyncCallback<List<String>> asyncCallback);
	void getFrameIdsForArray(int arrayID, AsyncCallback<Map<Integer,String>> asyncCallback);
}
