package fr.pfgen.cgh.client.ui.tabs;

import fr.pfgen.cgh.client.services.ArrayService;
import fr.pfgen.cgh.client.services.ArrayServiceAsync;
import fr.pfgen.cgh.client.utils.ArrayRecordList;
import fr.pfgen.cgh.shared.records.ArrayRecord;
import fr.pfgen.cgh.shared.sharedUtils.RecordList.Layout;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.HTMLFlow;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;

public class ArrayInfosTab extends Tab {

	private final ArrayServiceAsync arrayService = GWT.create(ArrayService.class);
	private HLayout tabPane;
	private ArrayRecord array;
	
	public ArrayInfosTab(ArrayRecord array) {
		this.array = array;
		
		tabPane = new HLayout(10);
		tabPane.setDefaultLayoutAlign(Alignment.CENTER);
		tabPane.setWidth("80%");
		
		tabPane.addMember(constructInfoTable());
		
		setPrompt("Array informations");
		setTitle("&nbsp;"+Canvas.imgHTML("icons/About.png",16,16));
		
		setPane(tabPane);
	}

	private VLayout constructInfoTable() {
		final VLayout infoTableLayout = new VLayout(40);
		infoTableLayout.setDefaultLayoutAlign(Alignment.CENTER);
		
		HTMLFlow arrayName = new HTMLFlow();
		arrayName.setStyleName("EC_TabTitle");
		arrayName.setContents("aCGH: "+array.getName());
		
		infoTableLayout.addMember(arrayName);
		
		if (array.getProjectName()!=null){
			HTMLFlow projectName = new HTMLFlow();
			projectName.setStyleName("EC_TabSubTitle");
			projectName.setContents("project: "+array.getProjectName());
			
			infoTableLayout.addMember(projectName);
		}
		
	
		HTMLFlow infoTable = new HTMLFlow();
		String table = new ArrayRecordList(array).createHtmlTable(Layout.HORIZONTAL, "");
		
		infoTable.setContents(table);
		infoTable.setAutoHeight();
		infoTable.setAutoWidth();
		
		infoTableLayout.addMember(infoTable);
		
		final IButton dlArrayResultButton = new IButton();
		dlArrayResultButton.setTitle("Download");
		dlArrayResultButton.setPrompt("Download full array result folder");
		dlArrayResultButton.setIcon("icons/Downloads folder.png");
		dlArrayResultButton.setShowDisabledIcon(false);
		dlArrayResultButton.setAutoFit(true);
		
		final Img loadingGif1 = new Img("loadingStar.gif",40,40);
        loadingGif1.setLayoutAlign(Alignment.CENTER);
		
		dlArrayResultButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				dlArrayResultButton.disable();
				infoTableLayout.addMember(loadingGif1);
				arrayService.downloadArrayResults(array.getID(), new AsyncCallback<String>() {

					@Override
					public void onFailure(Throwable caught) {
						SC.warn("Failed to create archive on server");
						dlArrayResultButton.enable();
						infoTableLayout.removeMember(loadingGif1);
					}

					@Override
					public void onSuccess(String result) {
						if (result != null && !result.isEmpty()){
							com.google.gwt.user.client.Window.open(GWT.getModuleBaseURL() + "fileProvider?file="+result, "_self", "");
						}else{
							SC.warn("Failed to create archive on server");
						}
						dlArrayResultButton.enable();
						infoTableLayout.removeMember(loadingGif1);
					}
				});
			}
		});
		
		infoTableLayout.addMember(dlArrayResultButton);
		
		return infoTableLayout;
	}
}
