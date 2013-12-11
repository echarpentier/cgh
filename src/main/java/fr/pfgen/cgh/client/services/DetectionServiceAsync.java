package fr.pfgen.cgh.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

import fr.pfgen.cgh.shared.records.DetectionRecord;

public interface DetectionServiceAsync extends GenericGwtRpcServiceAsync<DetectionRecord> {
	void setSMForFrame(int frameID, AsyncCallback<Boolean> asyncCallback);
	void setNoSMForFrame(int frameID, AsyncCallback<Boolean> asyncCallback);
}
