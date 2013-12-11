package fr.pfgen.cgh.client.services;

import java.util.List;

import fr.pfgen.cgh.shared.records.FrameRecord;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("FrameService")
public interface FrameService extends GenericGwtRpcService<FrameRecord>{

	List<String> getQcParamNames();
	String createWigFile(List<Integer> frameIDList, int chr);
	String createGffFile(List<Integer> frameIDList);
}
