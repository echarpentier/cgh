package fr.pfgen.cgh.client.ui.grids.listgrids;

import java.util.List;

import fr.pfgen.cgh.client.datasources.FrameDS;
import fr.pfgen.cgh.client.services.FrameService;
import fr.pfgen.cgh.client.services.FrameServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.SortSpecifier;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.AutoFitWidthApproach;
import com.smartgwt.client.types.Autofit;
import com.smartgwt.client.types.FetchMode;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.types.SortDirection;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;

public class FrameListgrid extends ListGrid {
	
	private final FrameServiceAsync frameService = GWT.create(FrameService.class);

	public FrameListgrid(){
		
		FrameDS datasource = FrameDS.getInstance();
		
		this.setTitle("List of frames in database.");
		this.setDataSource(datasource);
		this.setEmptyCellValue("--");
		this.setLayoutAlign(Alignment.CENTER);
		
		final List<ListGridField> gridFields = datasource.getDataSourceFieldsAsGridFields();
		frameService.getQcParamNames(new AsyncCallback<List<String>>() {
			
			@Override
			public void onSuccess(List<String> result) {
				for (String qcName : result) {
					ListGridField field = new ListGridField(qcName, qcName);
					field.setType(ListGridFieldType.TEXT);
					field.setWidth(10);
					field.setAlign(Alignment.CENTER);
					gridFields.add(new ListGridField(qcName, qcName));
				}
				FrameListgrid.this.setFields(gridFields.toArray(new ListGridField[gridFields.size()]));
			}
			
			@Override
			public void onFailure(Throwable caught) {
				SC.warn("Can't retrieve genotyping QC params from server");
			}
		});
		
		this.setFields(gridFields.toArray(new ListGridField[gridFields.size()]));
		
		this.setSelectionType(SelectionStyle.SINGLE);
		this.setAutoFetchData(false);
		this.setDataFetchMode(FetchMode.BASIC);
		this.setAutoFitData(Autofit.BOTH);
		this.setAutoFitMaxRecords(10);
		this.setAutoFitWidthApproach(AutoFitWidthApproach.BOTH);
		this.setAutoFitFieldWidths(true);
		this.setAutoFitFieldsFillViewport(false);
		this.setOverflow(Overflow.AUTO);
		this.setAutoWidth();
		this.setRight(30);
		this.setLeft(20);
		
		this.addSort(new SortSpecifier("frame_name",SortDirection.ASCENDING));
	}
}
