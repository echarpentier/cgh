package fr.pfgen.cgh.client.ui.tabs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fr.pfgen.cgh.client.datasources.FrameDS;
import fr.pfgen.cgh.client.services.ArrayService;
import fr.pfgen.cgh.client.services.ArrayServiceAsync;
import fr.pfgen.cgh.client.ui.vstacks.FrameVstack;
import fr.pfgen.cgh.client.utils.FrameRecordList;
import fr.pfgen.cgh.shared.enums.QCimages;
import fr.pfgen.cgh.shared.records.ArrayRecord;
import fr.pfgen.cgh.shared.records.FrameRecord;
import fr.pfgen.cgh.shared.sharedUtils.RecordList.Layout;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.HTMLFlow;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;

public class ArrayQualityControlsTab extends Tab {

	private final ArrayServiceAsync arrayService = GWT.create(ArrayService.class);
	private VLayout tabPane;
	private ArrayRecord array;
	
	public ArrayQualityControlsTab(ArrayRecord array) {
		this.array = array;
		
		tabPane = new VLayout(10);
		tabPane.setDefaultLayoutAlign(Alignment.LEFT);
		tabPane.setDefaultLayoutAlign(VerticalAlignment.TOP);
		
		setPrompt("Array quality controls");
		setTitle("&nbsp;"+Canvas.imgHTML("workflows/chart.png",16,16));
		
		tabPane.addMember(createCheckboxLayoutForArray());
		
		setPane(tabPane);
	}
	
	private HLayout createCheckboxLayoutForArray(){
		HLayout checkboxLayout = new HLayout(10);
		checkboxLayout.setAutoHeight();
		checkboxLayout.setAutoWidth();

		DynamicForm form = new DynamicForm();
		CheckboxItem qcCB = new CheckboxItem("qcCB", "QC params");
		CheckboxItem FNUoutCB = new CheckboxItem("FNUoutCB", "FNU outliers");
		CheckboxItem BGNUoutCB = new CheckboxItem("BGNUoutCB", "BGNU outliers");
		CheckboxItem MAplotCB = new CheckboxItem("MAplotCB", "MA plots");
		CheckboxItem GClowessCB = new CheckboxItem("GClowessCB", "GC lowess");
		
		form.setFields(qcCB,FNUoutCB,BGNUoutCB,MAplotCB,GClowessCB);
		form.setTitleOrientation(TitleOrientation.TOP);
		form.setNumCols(5);
		form.setAutoWidth();
		form.setAutoHeight();
		checkboxLayout.addMember(form);
		
		qcCB.addChangedHandler(new ChangedHandler() {
			
			@Override
			public void onChanged(ChangedEvent event) {
				String qcID = "QC_"+array.getID()+"_"+tabPane.getID();
				if (tabPane.getMember(qcID)==null){
					tabPane.addMember(constructQCLayout(qcID));
				}else if (tabPane.getMember(qcID).isVisible()){
					tabPane.getMember(qcID).hide();
				}else{
					tabPane.getMember(qcID).show();
				}
			}
		});
		
		FNUoutCB.addChangedHandler(new ChangedHandler() {
			
			@Override
			public void onChanged(ChangedEvent event) {
				String fnuID = "FNU_"+array.getID()+"_"+tabPane.getID();
				if (tabPane.getMember(fnuID)==null){
					tabPane.addMember(constructFNULayout(fnuID));
				}else if (tabPane.getMember(fnuID).isVisible()){
					tabPane.getMember(fnuID).hide();
				}else{
					tabPane.getMember(fnuID).show();
				}
			}
		});
		
		BGNUoutCB.addChangedHandler(new ChangedHandler() {
			
			@Override
			public void onChanged(ChangedEvent event) {
				String bgnuID = "BGNU_"+array.getID()+"_"+tabPane.getID();
				if (tabPane.getMember(bgnuID)==null){
					tabPane.addMember(constructBGNULayout(bgnuID));
				}else if (tabPane.getMember(bgnuID).isVisible()){
					tabPane.getMember(bgnuID).hide();
				}else{
					tabPane.getMember(bgnuID).show();
				}
			}
		});
		
		MAplotCB.addChangedHandler(new ChangedHandler() {
			
			@Override
			public void onChanged(ChangedEvent event) {
				String maID = "MA_"+array.getID()+"_"+tabPane.getID();
				if (tabPane.getMember(maID)==null){
					tabPane.addMember(constructMALayout(maID));
				}else if (tabPane.getMember(maID).isVisible()){
					tabPane.getMember(maID).hide();
				}else{
					tabPane.getMember(maID).show();
				}
			}
		});
		
		GClowessCB.addChangedHandler(new ChangedHandler() {
			
			@Override
			public void onChanged(ChangedEvent event) {
				String gcID = "GC_"+array.getID()+"_"+tabPane.getID();
				if (tabPane.getMember(gcID)==null){
					tabPane.addMember(constructGCLayout(gcID));
				}else if (tabPane.getMember(gcID).isVisible()){
					tabPane.getMember(gcID).hide();
				}else{
					tabPane.getMember(gcID).show();
				}
			}
		});
		
		return checkboxLayout;
	}
	
	private VLayout constructQCLayout(String layoutID){
		final VLayout layout = new VLayout(10);
		layout.setID(layoutID);
		layout.setAutoHeight();
		layout.setAutoWidth();
		
		final IButton htmlTableButton = new IButton("As HTML tables");
		htmlTableButton.setAutoFit(true);
		htmlTableButton.setLayoutAlign(Alignment.CENTER);
		htmlTableButton.setDynamicContents(true);
		
		final List<FrameVstack> stackList = new ArrayList<FrameVstack>();
		final List<HTMLFlow> htmlTableList = new ArrayList<HTMLFlow>();
		final HLayout htmlTablesLayout = new HLayout(10);
		htmlTablesLayout.setAutoHeight();
		htmlTablesLayout.setAutoWidth();
		
		arrayService.getFrameIdsForArray(array.getID(), new AsyncCallback<Map<Integer,String>>() {

			@Override
			public void onFailure(Throwable caught) {
				SC.warn("Failed to fetch frame QC for array");
			}

			@Override
			public void onSuccess(Map<Integer,String> result) {
				if (result!=null && !result.isEmpty()){
					for (Integer frameID : result.keySet()) {
						Criteria crits = new Criteria();
						crits.addCriteria("frame_id", frameID.toString());
						final FrameVstack frameStack = new FrameVstack();
						frameStack.addHeaderLabel(result.get(frameID));
						frameStack.addGrid();
						frameStack.getGrid().fetchData(crits);
						stackList.add(frameStack);
						layout.addMember(frameStack);
					}
					layout.addMember(htmlTableButton);
				}else{
					SC.warn("Failed to fetch frame QC for array");
				}
			}
		});
		
		htmlTableButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if (htmlTableButton.getTitle().equals("As HTML tables")){
					layout.removeMember(htmlTableButton);
					htmlTableList.clear();
					for (FrameVstack frameVstack : stackList) {
						FrameRecord frame = new FrameRecord();
						FrameDS.getInstance().copyValues(frameVstack.getGrid().getRecord(0), frame);
						layout.removeMember(frameVstack);
					
						HTMLFlow frameTable = new HTMLFlow();
						String table = new FrameRecordList(frame).createHtmlTable(Layout.VERTICAL, frame.getName());
					
						frameTable.setContents(table);
						frameTable.setAutoHeight();
						frameTable.setAutoWidth();
					
						htmlTablesLayout.addMember(frameTable);
					}
					layout.addMember(htmlTablesLayout);
					htmlTableButton.setTitle("As listgrid");
					layout.addMember(htmlTableButton);
				}else{
					layout.removeMember(htmlTableButton);
					htmlTablesLayout.destroy();
					for (FrameVstack frameVstack : stackList) {
						layout.addMember(frameVstack);
					}
					htmlTableButton.setTitle("As HTML tables");
					layout.addMember(htmlTableButton);
				}
			}
		});
		
		return layout;
	}
	
	private HLayout constructFNULayout(String layoutID){
		final HLayout layout = new HLayout(10);
		layout.setID(layoutID);
		layout.setAutoHeight();
		layout.setAutoWidth();
		
		arrayService.getQCImagesForArray(array.getID(), QCimages.FNUout, new AsyncCallback<List<String>>() {

			@Override
			public void onFailure(Throwable caught) {
				SC.warn("Failed to fetch QC images on server");
			}

			@Override
			public void onSuccess(List<String> result) {
				if (result!=null && !result.isEmpty()){
					HLayout frameLayout = new HLayout(10);
					frameLayout.setBorder("2px solid black");
					for (String frameImg : result) {
						Img image = new Img(GWT.getModuleBaseURL()+"imageProvider?file="+frameImg,800,800);
						image.setBorder("1px solid black");
						frameLayout.addMember(image);
					}
					layout.addMember(frameLayout);
				}else{
					SC.warn("Failed to fetch QC images on server");
				}
			}
		});
		
		return layout;
	}
	
	private HLayout constructBGNULayout(String layoutID){
		final HLayout layout = new HLayout(10);
		layout.setID(layoutID);
		layout.setAutoHeight();
		layout.setAutoWidth();
		
		arrayService.getQCImagesForArray(array.getID(), QCimages.BGNUout, new AsyncCallback<List<String>>() {

			@Override
			public void onFailure(Throwable caught) {
				SC.warn("Failed to fetch QC images on server");
			}

			@Override
			public void onSuccess(List<String> result) {
				if (result!=null && !result.isEmpty()){
					HLayout frameLayout = new HLayout(10);
					frameLayout.setBorder("2px solid black");
					for (String frameImg : result) {
						Img image = new Img(GWT.getModuleBaseURL()+"imageProvider?file="+frameImg,800,800);
						image.setBorder("1px solid black");
						frameLayout.addMember(image);
					}
					layout.addMember(frameLayout);
				}else{
					SC.warn("Failed to fetch QC images on server");
				}
			}
		});
		
		return layout;
	}
	
	private HLayout constructMALayout(String layoutID){
		final HLayout layout = new HLayout(10);
		layout.setID(layoutID);
		layout.setAutoHeight();
		layout.setAutoWidth();
		
		arrayService.getQCImagesForArray(array.getID(), QCimages.MAplot, new AsyncCallback<List<String>>() {

			@Override
			public void onFailure(Throwable caught) {
				SC.warn("Failed to fetch QC images on server");
			}

			@Override
			public void onSuccess(List<String> result) {
				if (result!=null && !result.isEmpty()){
					HLayout frameLayout = new HLayout(10);
					frameLayout.setBorder("2px solid black");
					for (String frameImg : result) {
						Img image = new Img(GWT.getModuleBaseURL()+"imageProvider?file="+frameImg,1300,1000);
						image.setBorder("1px solid black");
						frameLayout.addMember(image);
					}
					layout.addMember(frameLayout);
				}else{
					SC.warn("Failed to fetch QC images on server");
				}
			}
		});
		
		return layout;
	}
	
	private HLayout constructGCLayout(String layoutID){
		final HLayout layout = new HLayout(10);
		layout.setID(layoutID);
		layout.setAutoHeight();
		layout.setAutoWidth();
		
		arrayService.getQCImagesForArray(array.getID(), QCimages.GClowess, new AsyncCallback<List<String>>() {

			@Override
			public void onFailure(Throwable caught) {
				SC.warn("Failed to fetch QC images on server");
			}

			@Override
			public void onSuccess(List<String> result) {
				if (result!=null && !result.isEmpty()){
					HLayout frameLayout = new HLayout(10);
					frameLayout.setBorder("2px solid black");
					for (String frameImg : result) {
						Img image = new Img(GWT.getModuleBaseURL()+"imageProvider?file="+frameImg,1300,1000);
						image.setBorder("1px solid black");
						frameLayout.addMember(image);
					}
					layout.addMember(frameLayout);
				}else{
					SC.warn("Failed to fetch QC images on server");
				}
			}
		});
		
		return layout;
	}
}
