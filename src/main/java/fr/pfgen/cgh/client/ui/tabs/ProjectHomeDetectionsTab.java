package fr.pfgen.cgh.client.ui.tabs;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import fr.pfgen.cgh.client.services.FrameService;
import fr.pfgen.cgh.client.services.FrameServiceAsync;
import fr.pfgen.cgh.client.ui.vstacks.DetectionsVstack;
import fr.pfgen.cgh.client.ui.vstacks.FrameVstack;
import fr.pfgen.cgh.shared.records.ProjectRecord;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.SelectionAppearance;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.events.TabSelectedEvent;
import com.smartgwt.client.widgets.tab.events.TabSelectedHandler;

public class ProjectHomeDetectionsTab extends Tab {

	private VLayout tabPane;
	private boolean firstSeen = true;
	private final FrameServiceAsync frameService =  GWT.create(FrameService.class);
	
	public ProjectHomeDetectionsTab(final ProjectRecord project){
		
		this.setPrompt("Project detections");
		this.setTitle("&nbsp;"+Canvas.imgHTML("icons/Cgh_profile.ico",16,16));
		
		tabPane = new VLayout(20);
		tabPane.setDefaultLayoutAlign(Alignment.CENTER);
		tabPane.setWidth("80%");
		
		this.addTabSelectedHandler(new TabSelectedHandler() {
			
			@Override
			public void onTabSelected(TabSelectedEvent event) {
				if (firstSeen){
					tabPane.addMember(createDetectionGridLayout(project));
					tabPane.addMember(createProfileVisualizeLayout(project));
					firstSeen = false;
				}
			}
		});
		
		this.setPane(tabPane);
	}
	
	private VLayout createProfileVisualizeLayout(ProjectRecord project){
		final VLayout layout = new VLayout(10);
		layout.setAutoHeight();
		layout.setAutoWidth();
		
		Criteria crits = new Criteria();
		crits.addCriteria("project_id", Integer.toString(project.getProjectID()));
		
		final FrameVstack frameStack = new FrameVstack();
		frameStack.addHeaderLabel("Generate chromosome log ratio profiles (.wig) or detection profiles (.gff) for UCSC");
		frameStack.addLabel("Choose the frames for which you want to generate profile and the chromosome in case of chromosome log ratio profile.");
		
		
		frameStack.addGrid();
		frameStack.getGrid().setSelectionAppearance(SelectionAppearance.CHECKBOX);
		frameStack.getGrid().setSelectionType(SelectionStyle.SIMPLE);
		frameStack.getGrid().fetchData(crits);
		
		final HLayout buttonLayout = new HLayout(20);
		buttonLayout.setAutoHeight();
		buttonLayout.setAutoWidth();
		buttonLayout.setLayoutAlign(Alignment.CENTER);
		
		final IButton logratioProfileButton = new IButton("Log ratio profile");
		logratioProfileButton.setAutoFit(true);
		logratioProfileButton.setLayoutAlign(Alignment.CENTER);
		
		final IButton detectionProfileButton = new IButton("Detection profile");
		detectionProfileButton.setAutoFit(true);
		detectionProfileButton.setLayoutAlign(Alignment.CENTER);
		
		LinkedHashMap<String, String> valueMap = new LinkedHashMap<String, String>(24);
		for (int i = 1; i < 23; i++) {
			valueMap.put(String.valueOf(i), String.valueOf(i));
		}
		valueMap.put("23", "X");
		valueMap.put("24", "Y");
		
		DynamicForm f = new DynamicForm();
		f.setAutoHeight();
		f.setAutoWidth();
		final SelectItem chrItem = new SelectItem("Chromosome");
		chrItem.setValueMap(valueMap);
		chrItem.setWidth(50);
		
		f.setFields(chrItem);
		
		final Img loadingGif = new Img("loadingStar.gif",40,40);
        loadingGif.setLayoutAlign(Alignment.CENTER);
		
		logratioProfileButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				buttonLayout.disable();
				frameStack.addMember(loadingGif);
				ListGridRecord[] recs = frameStack.getGrid().getSelectedRecords();
				if (recs.length==0){
					SC.warn("Please select frames for which you would like to create a wig file");
					buttonLayout.enable();
					frameStack.removeMember(loadingGif);
					return;
				}
				if (chrItem.getValueAsString()==null || chrItem.getValueAsString().isEmpty()){
					SC.warn("Please select a chromosome for which you would like to create a wig file");
					buttonLayout.enable();
					frameStack.removeMember(loadingGif);
					return;
				}
				List<Integer> frameIDList = new ArrayList<Integer>();
				for (ListGridRecord lgrec : recs) {
					frameIDList.add(lgrec.getAttributeAsInt("frame_id"));
				}
				
				frameService.createWigFile(frameIDList, Integer.parseInt(chrItem.getValueAsString()), new AsyncCallback<String>() {

					@Override
					public void onFailure(Throwable caught) {
						SC.warn("Cannot create wig file on server");
						buttonLayout.enable();
						frameStack.removeMember(loadingGif);
					}

					@Override
					public void onSuccess(String result) {
						if (result==null || result.isEmpty()){
							SC.warn("Cannot create wig file on server");
							buttonLayout.enable();
							frameStack.removeMember(loadingGif);
							return;
						}
						buttonLayout.enable();
						frameStack.removeMember(loadingGif);
						com.google.gwt.user.client.Window.open(GWT.getModuleBaseURL() + "fileProvider?file="+result, "_self", "");
					}
				});
			}
		});
		
		detectionProfileButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				buttonLayout.disable();
				frameStack.addMember(loadingGif);
				ListGridRecord[] recs = frameStack.getGrid().getSelectedRecords();
				if (recs.length==0){
					SC.warn("Please select frames for which you would like to create a gff file");
					buttonLayout.enable();
					frameStack.removeMember(loadingGif);
					return;
				}
				List<Integer> frameIDList = new ArrayList<Integer>();
				for (ListGridRecord lgrec : recs) {
					frameIDList.add(lgrec.getAttributeAsInt("frame_id"));
				}
				frameService.createGffFile(frameIDList, new AsyncCallback<String>() {

					@Override
					public void onFailure(Throwable caught) {
						SC.warn("Cannot create gff file on server");
						buttonLayout.enable();
						frameStack.removeMember(loadingGif);
					}

					@Override
					public void onSuccess(String result) {
						if (result==null || result.isEmpty()){
							SC.warn("Cannot create gff file on server");
							buttonLayout.enable();
							frameStack.removeMember(loadingGif);
							return;
						}
						buttonLayout.enable();
						frameStack.removeMember(loadingGif);
						com.google.gwt.user.client.Window.open(GWT.getModuleBaseURL() + "fileProvider?file="+result, "_self", "");
					}
				});
			}
		});
	
		buttonLayout.addMember(f);
		buttonLayout.addMember(logratioProfileButton);
		buttonLayout.addMember(detectionProfileButton);
		
		frameStack.addMember(buttonLayout);
		
		layout.addMember(frameStack);
		
		return layout;
	}
	
	private VLayout createDetectionGridLayout(ProjectRecord project){
		final VLayout detectionLayout = new VLayout(10);
		detectionLayout.setAutoHeight();
		detectionLayout.setAutoWidth();
		
		Criteria crits = new Criteria();
		crits.addCriteria("project_id", Integer.toString(project.getProjectID()));
        
		final DetectionsVstack detectionStack = new DetectionsVstack();
		detectionStack.addHeaderLabel("Detections in project "+project.getProjectName());
		detectionStack.addGrid();
		detectionStack.getGrid().fetchData(crits);
		
		HLayout buttonLayout = new HLayout(10);
		buttonLayout.setLayoutAlign(Alignment.CENTER);
		buttonLayout.setDefaultLayoutAlign(Alignment.CENTER);
		buttonLayout.setAutoHeight();
		buttonLayout.setAutoWidth();
		
		IButton groupByButton = new IButton("Group by quality");
		groupByButton.setAutoFit(true);
		groupByButton.addClickHandler(new ClickHandler() {  
            public void onClick(ClickEvent event) {  
                detectionStack.getGrid().groupBy("d_quality");
            }  
        });  
  
  
        IButton ungroupButton = new IButton("Ungroup");  
        ungroupButton.setAutoFit(true); 
        ungroupButton.addClickHandler(new ClickHandler() {  
            public void onClick(ClickEvent event) {  
            	detectionStack.getGrid().ungroup();
            }  
        });  
        
		buttonLayout.addMember(groupByButton);
		buttonLayout.addMember(ungroupButton);
		detectionStack.addMember(buttonLayout);
		
		detectionLayout.addMember(detectionStack);
		
		return detectionLayout;
	}
}
