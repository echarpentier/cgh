package fr.pfgen.cgh.client.ui.tabs;

import java.util.Map;

import fr.pfgen.cgh.client.Cgh;
import fr.pfgen.cgh.client.services.AnalysisService;
import fr.pfgen.cgh.client.services.AnalysisServiceAsync;
import fr.pfgen.cgh.client.ui.vstacks.Upload;
import fr.pfgen.cgh.client.ui.vstacks.UploadListener;
import fr.pfgen.cgh.client.ui.windows.ProjectAnalysisParamWindow;
import fr.pfgen.cgh.shared.records.ProjectRecord;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.util.StringUtil;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.HTMLFlow;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.events.TabSelectedEvent;
import com.smartgwt.client.widgets.tab.events.TabSelectedHandler;

public class ProjectHomeAnalysisTab extends Tab {

	private VLayout tabPane;
	private final AnalysisServiceAsync analysisService = GWT.create(AnalysisService.class);
	private final ProjectRecord project;
	
	public ProjectHomeAnalysisTab(final ProjectRecord project){
		this.project = project;
		this.setPrompt("Project analysis");
		this.setTitle("&nbsp;"+Canvas.imgHTML("icons/Pinion.png",16,16));
		
		tabPane = new VLayout(20);
		tabPane.setWidth("80%");
		
		this.addTabSelectedHandler(new TabSelectedHandler() {
			
			@Override
			public void onTabSelected(TabSelectedEvent event) {
				ProjectHomeAnalysisTab.this.updateTab();
			}
		});	
		
		this.setPane(tabPane);
	}
	
	public void updateTab(){
		analysisService.projectConfigFileExists(project.getProjectName(), new AsyncCallback<Boolean>() {

			@Override
			public void onFailure(Throwable caught) {
				SC.warn("Cannot check if configuration file exists for project");
			}

			@Override
			public void onSuccess(Boolean result) {
				if (result==null){
					SC.warn("Cannot check if configuration file exists for project");
					return;
				}
				if (!result){
					createConfigFileLayout();
				}else{
					analysisService.isProjectAnalysisRunning(project.getProjectID(), new AsyncCallback<Boolean>() {

						@Override
						public void onFailure(Throwable caught) {
							SC.warn("Cannot check if a project analysis is running");
						}

						@Override
						public void onSuccess(Boolean result) {
							if (result==null){
								SC.warn("Cannot check if a project analysis is running");
								return;
							}
							if (!result){
								createLaunchAnalysisVlayout();
							}else{
								createRunningAnalysisLayout();
							}
						}
					});
				}
			}
		});	
	}
	
	private void createConfigFileLayout(){
		for (Canvas member : tabPane.getMembers()) {
			member.destroy();
		}
		VLayout layout = new VLayout(20);
		layout.setLayoutTopMargin(50);
		layout.setLayoutAlign(Alignment.CENTER);
		layout.setAutoHeight();
		layout.setAutoWidth();
		layout.setDefaultLayoutAlign(Alignment.CENTER);
		
		HTMLFlow htmlFlow = new HTMLFlow();
		htmlFlow.setAutoHeight();
		htmlFlow.setAutoWidth();
		htmlFlow.setContents(StringUtil.asHTML("No configuration file for project", true));
		
		layout.addMember(htmlFlow);
		
		IButton createConfigFileButton = new IButton("New config file");
		createConfigFileButton.setAutoFit(true);
		createConfigFileButton.setIcon("icons/Create.png");
		createConfigFileButton.setShowDisabledIcon(false);
		
		createConfigFileButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				new ProjectAnalysisParamWindow(project,ProjectHomeAnalysisTab.this);
			}
		});
		
		layout.addMember(createConfigFileButton);
		
		tabPane.addMember(layout);
	}
	
	private void createLaunchAnalysisVlayout(){
		for (Canvas member : tabPane.getMembers()) {
			member.destroy();
		}
		VLayout layout = new VLayout(20);
		layout.setLayoutAlign(Alignment.CENTER);
		layout.setAutoHeight();
		layout.setAutoWidth();
		layout.setLayoutTopMargin(50);
		layout.setDefaultLayoutAlign(Alignment.CENTER);
		
		final Upload uploadStack = new Upload();
		uploadStack.setHeaderLabel("Launch a new analysis");
		uploadStack.addLabel(new Label(StringUtil.asHTML("You can upload a FE file ('.txt' or '.txt.gz') or an archive ('.zip')", true)));
		uploadStack.setFileItemTitle("FE&nbsp;files");
		uploadStack.setAction(GWT.getModuleBaseURL() + "fileUploader");
		
		final Img loadingGif = new Img("loadingStar.gif",40,40);
		
		uploadStack.getUploadButton().addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if (uploadStack.getFileItem().getValueAsString()==null || uploadStack.getFileItem().getValueAsString().equals("")){
					SC.warn("Please select a file.");
				}else{
					uploadStack.getUploadButton().disable();
					uploadStack.getStack().addMember(loadingGif);
				}
			}
		});
		
		uploadStack.setUploadListener(new UploadListener() {
			
			@Override
			public void uploadComplete(String fileName) {
				if (fileName.startsWith("Error")){
					uploadStack.getStack().removeMember(loadingGif);
					uploadStack.getUploadButton().enable();
					SC.warn(fileName);
				}else{
					analysisService.launchAnalysisForProject(fileName, project, Cgh.get().getUser(), new AsyncCallback<String>() {

						@Override
						public void onFailure(Throwable caught) {
							uploadStack.getStack().removeMember(loadingGif);
							uploadStack.getUploadButton().enable();
							SC.warn(caught.toString());
						}

						@Override
						public void onSuccess(String result) {
							uploadStack.getStack().removeMember(loadingGif);
							uploadStack.getUploadButton().enable();
							if (result==null){
								SC.warn("Cannot launch analysis on server");
								return;
							}
							if (result.startsWith("Error:")){
								SC.warn(result);
								return;
							}
							createRunningAnalysisLayout();
						}
					});
				}
			}
		});
		
		layout.addMember(uploadStack);
		
		tabPane.addMember(layout);
	}
	
	private void createRunningAnalysisLayout(){
		for (Canvas member : tabPane.getMembers()) {
			member.destroy();
		}
		
		final VLayout layout = new VLayout(20);
		layout.setLayoutAlign(Alignment.CENTER);
		layout.setWidth100();
		layout.setHeight100();
		//layout.setAutoHeight();
		//layout.setAutoWidth();
		//layout.setOverflow(Overflow.SCROLL);
		layout.setLayoutTopMargin(20);
		layout.setDefaultLayoutAlign(Alignment.CENTER);
		
		final HTMLFlow flow = new HTMLFlow();
		flow.setHeight100();
		flow.setWidth(300);
		flow.setOverflow(Overflow.AUTO);
		
		final IButton dismissButton = new IButton("Dismiss");
		dismissButton.setAutoFit(true);
		dismissButton.disable();
		
		dismissButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				analysisService.removeProjectAnalysis(project.getProjectID(), new AsyncCallback<Boolean>() {

					@Override
					public void onFailure(Throwable caught) {
						SC.warn("Cannot dismiss project analysis");
					}

					@Override
					public void onSuccess(Boolean result) {
						if (result==null || !result){
							SC.warn("Cannot dismiss project analysis");
							return;
						}
						ProjectHomeAnalysisTab.this.updateTab();
					}
				});
			}
		});
		
		final Timer timer = new Timer() {  
			public void run() {  
				analysisService.projectAnalysisAdvances(project.getProjectID(), new AsyncCallback<Map<String, String>>() {
					
					@Override
					public void onSuccess(Map<String, String> result) {
						if (result != null){
							flow.setContents(StringUtil.asHTML(result.get("log"), true));
							flow.scrollToBottom();
							if (result.get("running").equals("no")){
								dismissButton.enable();
								layout.addMember(dismissButton);
								cancel();
							}
						}else{
							SC.warn("Cannot get analysis advances");
							cancel();
						}
					}
					
					@Override
					public void onFailure(Throwable caught) {
						SC.warn("Cannot get analysis advances");
						cancel();
					}
				});
			}
		};
		
		timer.scheduleRepeating(3000);
		
		layout.addMember(flow);
		tabPane.addMember(layout);
	}
}
