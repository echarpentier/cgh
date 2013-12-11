package fr.pfgen.cgh.client.services;

import fr.pfgen.cgh.shared.records.DetectionRecord;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("DetectionService")
public interface DetectionService extends GenericGwtRpcService<DetectionRecord> {
	boolean setSMForFrame(int frameID);
	boolean setNoSMForFrame(int frameID);
}
