package fr.pfgen.cgh.client.datasources;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.data.SimpleType;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;

import fr.pfgen.cgh.client.services.TeamService;
import fr.pfgen.cgh.client.services.TeamServiceAsync;
import fr.pfgen.cgh.client.utils.ClientUtils;
import fr.pfgen.cgh.shared.records.TeamRecord;

public class TeamDS extends GenericGwtRpcDataSource<TeamRecord, ListGridRecord, TeamServiceAsync> {

	private static TeamDS instance;
	
	private TeamDS(){
	}
	
	public static TeamDS getInstance(){
		if (instance==null){
			instance = new TeamDS();
		}
		return instance;
	}

	@Override
	public List<DataSourceField> getDataSourceFields() {
		List<DataSourceField> fields = new ArrayList<DataSourceField>();
		
		if (SimpleType.getType("nameType")==null){
			SimpleType nameType = new ClientUtils.NameType();
			nameType.register();
		}
		if (SimpleType.getType("firstLastNameType")==null){
			SimpleType firstLastNameType = new ClientUtils.FirstLastNameType();
			firstLastNameType.register();
		}
    	
        DataSourceField field;
        field = new DataSourceIntegerField("team_id", "id");
        field.setPrimaryKey(true);
        field.setHidden(true);
        fields.add(field);
        field = new DataSourceTextField("team_name", "team");
        field.setRequired(true);
        field.setType(SimpleType.getType("nameType"));
        fields.add(field);
        field = new DataSourceTextField("team_leader", "leader");
        field.setRequired(true);
        field.setType(SimpleType.getType("firstLastNameType"));
        fields.add(field);
        
        return fields;
	}

	@Override
	public List<ListGridField> getDataSourceFieldsAsGridFields() {
		List<ListGridField> returnFields = new ArrayList<ListGridField>();
		for (DataSourceField dsField : getDataSourceFields()) {
			ListGridField field = new ListGridField();
			field.setName(dsField.getName());
			field.setTitle(dsField.getTitle());
			field.setHidden(dsField.getHidden());
			field.setAlign(Alignment.CENTER);
			returnFields.add(field);
		}
		return returnFields;
	}

	@Override
	public void copyValues(ListGridRecord from, TeamRecord to) {
		to.setTeamID(from.getAttributeAsInt("team_id"));
		to.setTeamName(from.getAttributeAsString("team_name"));
		if (from.getAttributeAsString("team_leader")!=null){
			to.setTeamLeader(from.getAttributeAsString("team_leader"));
		}
	}

	@Override
	public void copyValues(TeamRecord from, ListGridRecord to) {
		to.setAttribute("team_id", from.getTeamID());
		to.setAttribute("team_name", from.getTeamName());
		to.setAttribute("team_leader", from.getTeamLeader());
	}

	@Override
	public TeamServiceAsync getServiceAsync() {
		return GWT.create(TeamService.class);
	}

	@Override
	public ListGridRecord getNewRecordInstance() {
		return new ListGridRecord();
	}

	@Override
	public TeamRecord getNewDataObjectInstance() {
		return new TeamRecord();
	}
}
