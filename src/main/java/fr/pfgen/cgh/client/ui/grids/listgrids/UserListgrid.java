package fr.pfgen.cgh.client.ui.grids.listgrids;

import java.util.List;

import fr.pfgen.cgh.client.datasources.UserDS;
import fr.pfgen.cgh.client.services.TeamService;
import fr.pfgen.cgh.client.services.TeamServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.SortSpecifier;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.AutoFitWidthApproach;
import com.smartgwt.client.types.Autofit;
import com.smartgwt.client.types.FetchMode;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.types.SortDirection;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;

public class UserListgrid extends ListGrid {

	private TeamServiceAsync teamService = GWT.create(TeamService.class);
	
	public UserListgrid(){
		final UserDS datasource = UserDS.getInstance();
		
		this.setTitle("List of users.");

		this.setEmptyCellValue("--");
		this.setLayoutAlign(Alignment.CENTER);
		
		this.setDataSource(datasource);
		this.setFields(datasource.getDataSourceFieldsAsGridFields().toArray(new ListGridField[datasource.getDataSourceFieldsAsGridFields().size()]));
		
		teamService.getTeamNames(new AsyncCallback<List<String>>() {
			
			@Override
			public void onSuccess(List<String> result) {
				datasource.getField("team_name").setValueMap(result.toArray(new String[result.size()]));
			}
			
			@Override
			public void onFailure(Throwable caught) {
				SC.warn("Failed to fetch team names from server");
			}
		});
		
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
		
		this.addSort(new SortSpecifier("app_id",SortDirection.DESCENDING));
	}
}
