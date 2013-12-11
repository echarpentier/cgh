package fr.pfgen.cgh.client.ui.tabs;

import java.util.Map;

import fr.pfgen.cgh.client.services.ArrayService;
import fr.pfgen.cgh.client.services.ArrayServiceAsync;
import fr.pfgen.cgh.client.services.DetectionService;
import fr.pfgen.cgh.client.services.DetectionServiceAsync;
import fr.pfgen.cgh.client.ui.vstacks.DetectionsVstack;
import fr.pfgen.cgh.shared.records.ArrayRecord;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.events.TabSelectedEvent;
import com.smartgwt.client.widgets.tab.events.TabSelectedHandler;

public class ArrayDetectionsTab extends Tab {

	private final ArrayServiceAsync arrayService = GWT.create(ArrayService.class);
	private final DetectionServiceAsync detectionService = GWT.create(DetectionService.class);
	private HLayout tabPane;
	private ArrayRecord array;
	private boolean firstSeen = true;
	
	public ArrayDetectionsTab(ArrayRecord array) {
		this.array = array;
		
		tabPane = new HLayout(10);
		tabPane.setDefaultLayoutAlign(Alignment.LEFT);
		tabPane.setDefaultLayoutAlign(VerticalAlignment.TOP);
		
		setPrompt("Array detections");
		setTitle("&nbsp;"+Canvas.imgHTML("icons/Cgh_profile.ico",16,16));
		
		
		
		this.addTabSelectedHandler(new TabSelectedHandler() {
			
			@Override
			public void onTabSelected(TabSelectedEvent event) {
				if (firstSeen){
					final VLayout detGridsLayout = createDetectionsLayoutForArray();
					tabPane.addMember(detGridsLayout);
					final VLayout singleDetGridLayout = createSingleGridLayoutForArray();
					
					final IButton oneGridButton = new IButton("As one grid");
					oneGridButton.setAutoFit(true);
					
					oneGridButton.addClickHandler(new ClickHandler() {
						
						@Override
						public void onClick(ClickEvent event) {
							if (oneGridButton.getTitle().equals("As one grid")){
								tabPane.removeMember(detGridsLayout);
								tabPane.addMember(singleDetGridLayout, 0);
								oneGridButton.setTitle("As multi grids");
							}else{
								tabPane.removeMember(singleDetGridLayout);
								tabPane.addMember(detGridsLayout, 0);
								oneGridButton.setTitle("As one grid");
							}
						}
					});
					
					if (ArrayDetectionsTab.this.array.getFrameNumber()>1){
						tabPane.addMember(oneGridButton);
					}
					firstSeen = false;
				}
			}
		});
		
		setPane(tabPane);
	}

	private VLayout createDetectionsLayoutForArray(){
		final VLayout mainDetectionsLayout = new VLayout(10);
		mainDetectionsLayout.setAutoHeight();
		mainDetectionsLayout.setAutoWidth();
		
		arrayService.getFrameIdsForArray(array.getID(), new AsyncCallback<Map<Integer,String>>() {
			
			@Override
			public void onSuccess(Map<Integer,String> result) {
				if (result!=null && !result.isEmpty()){
					for (final Integer frameID : result.keySet()) {
						Criteria crits = new Criteria();
						crits.addCriteria("frame_id", frameID.toString());
						final DetectionsVstack detectionStack = new DetectionsVstack();
						detectionStack.addHeaderLabel(result.get(frameID));
						detectionStack.addGrid();
						detectionStack.getGrid().fetchData(crits);
						
						HLayout buttonLayout = new HLayout(10);
						buttonLayout.setLayoutAlign(Alignment.CENTER);
						buttonLayout.setDefaultLayoutAlign(Alignment.CENTER);
						buttonLayout.setAutoHeight();
						buttonLayout.setAutoWidth();
						
						IButton sexMismatchButton = new IButton("SexMismatch");
						sexMismatchButton.setIcon("icons/male-female.png");
						sexMismatchButton.setIconSize(16);
						sexMismatchButton.setAutoFit(true);
						sexMismatchButton.addClickHandler(new ClickHandler() {
							
							@Override
							public void onClick(ClickEvent event) {
								detectionService.setSMForFrame(frameID, new AsyncCallback<Boolean>() {

									@Override
									public void onFailure(Throwable caught) {
										SC.warn("Failed to change tags for SexMismatch for frame");
									}

									@Override
									public void onSuccess(Boolean result) {
										if (result!=null && result){
											detectionStack.getGrid().invalidateCache();
										}else{
											
										}
									}
								});
							}
						});
						
						IButton sexMatchButton = new IButton("SexMatch");
						sexMatchButton.setIcon("icons/no-male-female.png");
						sexMatchButton.setIconSize(16);
						sexMatchButton.setAutoFit(true);
						sexMatchButton.addClickHandler(new ClickHandler() {
							
							@Override
							public void onClick(ClickEvent event) {
								detectionService.setNoSMForFrame(frameID, new AsyncCallback<Boolean>() {
									@Override
									public void onFailure(Throwable caught) {
										SC.warn("Failed to change tags for SexMismatch for frame");
									}

									@Override
									public void onSuccess(Boolean result) {
										if (result!=null && result){
											detectionStack.getGrid().invalidateCache();
										}else{
											
										}
									}
								});
								
							}
						});
						
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
						
						buttonLayout.addMember(sexMismatchButton);
						buttonLayout.addMember(sexMatchButton);
						buttonLayout.addMember(groupByButton);
						buttonLayout.addMember(ungroupButton);
						detectionStack.addMember(buttonLayout);
						
						mainDetectionsLayout.addMember(detectionStack);
					}
				}else{
					SC.warn("Failed to fetch frames corresponding to this array on server");
				}
			}
			
			@Override
			public void onFailure(Throwable caught) {
				SC.warn("Failed to fetch frames corresponding to this array on server");
			}
		});
		
		return mainDetectionsLayout;
	}
	
	private VLayout createSingleGridLayoutForArray(){
		final VLayout mainDetectionsLayout = new VLayout(10);
		mainDetectionsLayout.setAutoHeight();
		mainDetectionsLayout.setAutoWidth();
	
		Criteria crits = new Criteria();
		crits.addCriteria("array_id", Integer.toString(array.getID()));
		final DetectionsVstack detectionStack = new DetectionsVstack();
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
		
		mainDetectionsLayout.addMember(detectionStack);
		
		return mainDetectionsLayout;
	}
}
