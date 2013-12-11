package fr.pfgen.cgh.client.services;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import fr.pfgen.cgh.shared.records.FrameRecord;

public interface FrameServiceAsync extends GenericGwtRpcServiceAsync<FrameRecord> {

	void getQcParamNames(AsyncCallback<List<String>> asyncCallback);

	void createWigFile(List<Integer> frameIDList, int chr, AsyncCallback<String> asyncCallback);
	void createGffFile(List<Integer> frameIDList, AsyncCallback<String> asyncCallback);

}
