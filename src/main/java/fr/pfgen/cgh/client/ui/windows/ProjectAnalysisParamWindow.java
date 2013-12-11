package fr.pfgen.cgh.client.ui.windows;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;

import fr.pfgen.cgh.client.services.AnalysisService;
import fr.pfgen.cgh.client.services.AnalysisServiceAsync;
import fr.pfgen.cgh.client.ui.tabs.ProjectHomeAnalysisTab;
import fr.pfgen.cgh.shared.records.AnalysisParamsRecord;
import fr.pfgen.cgh.shared.records.ProjectRecord;

public class ProjectAnalysisParamWindow extends AnalysisParamWindow {

	private final ProjectRecord project;
	private final AnalysisServiceAsync analysisService = GWT.create(AnalysisService.class);
	
	public ProjectAnalysisParamWindow(ProjectRecord project, final ProjectHomeAnalysisTab analysisTabRef) {
		super();
		this.project = project;
		this.setTitle("Analysis parameters for project "+this.project.getProjectName());
		getSaveButton().addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				AnalysisParamsRecord rec = getValuesAsAnalysisParamsRecord();
				
				analysisService.saveProjectConfigFile(ProjectAnalysisParamWindow.this.project.getProjectName(), rec, new AsyncCallback<String>() {

					@Override
					public void onFailure(Throwable caught) {
						SC.warn("Cannot create configuration file for project "+ProjectAnalysisParamWindow.this.project.getProjectName());
					}

					@Override
					public void onSuccess(String result) {
						if (result == null){
							SC.warn("Cannot create configuration file for project "+ProjectAnalysisParamWindow.this.project.getProjectName());
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
