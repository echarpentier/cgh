package fr.pfgen.cgh.client.ui.windows;

import fr.pfgen.cgh.client.services.AnalysisService;
import fr.pfgen.cgh.client.services.AnalysisServiceAsync;
import fr.pfgen.cgh.client.ui.tabs.ArrayAnalysisTab;
import fr.pfgen.cgh.shared.records.AnalysisParamsRecord;
import fr.pfgen.cgh.shared.records.UserRecord;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;

public class UserAnalysisParamWindow extends AnalysisParamWindow {

	private final UserRecord user;
	private final AnalysisServiceAsync analysisService = GWT.create(AnalysisService.class);
	
	public UserAnalysisParamWindow(UserRecord user, final ArrayAnalysisTab analysisTabRef) {
		super();
		this.user = user;
		this.setTitle("Analysis parameters for user "+this.user.getAppID());
		getSaveButton().addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				AnalysisParamsRecord rec = getValuesAsAnalysisParamsRecord();
				
				analysisService.saveUserConfigFile(UserAnalysisParamWindow.this.user.getAppID(), rec, new AsyncCallback<String>() {

					@Override
					public void onFailure(Throwable caught) {
						SC.warn("Cannot create configuration file for user "+UserAnalysisParamWindow.this.user.getAppID());
					}

					@Override
					public void onSuccess(String result) {
						if (result == null){
							SC.warn("Cannot create configuration file for user "+UserAnalysisParamWindow.this.user.getAppID());
							return;
						}
						if (result.startsWith("Error:")){
							SC.warn(result);
						}else{
							analysisTabRef.updateTab();
							destroy();
						}
					}
				});
			}
		});
	}
}
