package fr.pfgen.cgh.client.ui.grids.listgrids;

import java.util.List;

import fr.pfgen.cgh.client.datasources.DetectionDS;

import com.smartgwt.client.data.SortSpecifier;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.AutoFitWidthApproach;
import com.smartgwt.client.types.Autofit;
import com.smartgwt.client.types.FetchMode;
import com.smartgwt.client.types.GroupStartOpen;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.types.SortDirection;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.RecordClickEvent;
import com.smartgwt.client.widgets.grid.events.RecordClickHandler;

public class DetectionListgrid extends ListGrid {
	
	public DetectionListgrid(){
		
		DetectionDS datasource = DetectionDS.getInstance();
		
		this.setTitle("List of detections for frame.");
		this.setDataSource(datasource);
		this.setEmptyCellValue("--");
		this.setLayoutAlign(Alignment.CENTER);
		
		List<ListGridField> listgridFields = datasource.getDataSourceFieldsAsGridFields();
		ListGridField ucscField = new ListGridField("ucsc");
		ucscField.setAlign(Alignment.CENTER);
		ucscField.setType(ListGridFieldType.ICON);
		ucscField.setCellIcon("icons/UCSC.gif");
		ucscField.setCellAlign(Alignment.CENTER);
		ucscField.setWidth(50);
		ucscField.setTitle("UCSC");
		
		ucscField.addRecordClickHandler(new RecordClickHandler() {
			
			@Override
			public void onRecordClick(RecordClickEvent event) {
				String pos = event.getRecord().getAttributeAsString("d_position");
				String build = event.getRecord().getAttributeAsString("genomic_build");
				com.google.gwt.user.client.Window.open("http://genome.ucsc.edu/cgi-bin/hgTracks?position="+pos+"&db="+build+"&refGene=full", "_blank", "");
			}
		});
		
		listgridFields.add(ucscField);
		
		this.setFields(listgridFields.toArray(new ListGridField[listgridFields.size()]));
		
		this.setCanGroupBy(true);
		this.setGroupStartOpen(GroupStartOpen.ALL);
		this.setGroupByMaxRecords(50000);
		
		this.setEditOnFocus(true);
		this.setSelectionType(SelectionStyle.SINGLE);
		this.setAutoFetchData(false);
		this.setDataFetchMode(FetchMode.BASIC);
		this.setAutoFitData(Autofit.BOTH);
		this.setAutoFitMaxRecords(15);
		this.setAutoFitWidthApproach(AutoFitWidthApproach.BOTH);
		this.setAutoFitFieldWidths(true);
		this.setAutoFitFieldsFillViewport(false);
		this.setOverflow(Overflow.AUTO);
		this.setAutoWidth();
		this.setRight(30);
		this.setLeft(20);
		
		this.addSort(new SortSpecifier("frame_name",SortDirection.ASCENDING));
		this.addSort(new SortSpecifier("d_chr",SortDirection.ASCENDING));
		this.addSort(new SortSpecifier("d_start",SortDirection.ASCENDING));
		this.addSort(new SortSpecifier("d_end",SortDirection.ASCENDING));
	}
	
	@Override
	protected String getCellStyle(ListGridRecord record, int rowNum, int colNum) {
		if (colNum==8){
			return "EC_pointer";
		}
		return super.getCellStyle(record, rowNum, colNum);
	}
}
