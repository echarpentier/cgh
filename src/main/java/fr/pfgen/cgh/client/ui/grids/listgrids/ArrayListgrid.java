package fr.pfgen.cgh.client.ui.grids.listgrids;

import fr.pfgen.cgh.client.datasources.ArrayDS;

import com.smartgwt.client.data.SortSpecifier;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.AutoFitWidthApproach;
import com.smartgwt.client.types.Autofit;
import com.smartgwt.client.types.FetchMode;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.types.SortDirection;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;

public class ArrayListgrid extends ListGrid {

	public ArrayListgrid(){
		
		ArrayDS datasource = ArrayDS.getInstance();
		
		this.setTitle("List of arrays in database.");
		this.setDataSource(datasource);
		this.setEmptyCellValue("--");
		this.setLayoutAlign(Alignment.CENTER);
		
		this.setFields(datasource.getDataSourceFieldsAsGridFields().toArray(new ListGridField[datasource.getDataSourceFieldsAsGridFields().size()]));
		
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
		
		this.addSort(new SortSpecifier("scan_date",SortDirection.ASCENDING));
	}
}
