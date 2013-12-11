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

import fr.pfgen.cgh.client.services.ProjectService;
import fr.pfgen.cgh.client.services.ProjectServiceAsync;
import fr.pfgen.cgh.client.utils.ClientUtils;
import fr.pfgen.cgh.shared.records.ProjectRecord;

public class ProjectDS extends GenericGwtRpcDataSource<ProjectRecord, ListGridRecord, ProjectServiceAsync> {

	private static ProjectDS instance;
	
	private ProjectDS(){
		
	}
	
	public static ProjectDS getInstance(){
		if (instance==null){
			instance = new ProjectDS();
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
        field = new DataSourceIntegerField("project_id", "id");
        field.setPrimaryKey(true);
        field.setHidden(true);
        fields.add(field);
        field = new DataSourceTextField("project_name", "name");
        field.setRequired(true);
        field.setType(SimpleType.getType("nameType"));
        fields.add(field);
        field = new DataSourceTextField("project_manager", "manager");
        field.setCanEdit(true);
        field.setType(SimpleType.getType("firstLastNameType"));
        fields.add(field);
        field = new DataSourceTextField("project_path", "path");
        field.setHidden(true);
        fields.add(field);
        field = new DataSourceIntegerField("user_id", "user id");
        field.setHidden(true);
        fields.add(field);
        field = new DataSourceTextField("user_name", "created by");
        fields.add(field);
        field = new DataSourceIntegerField ("team_id", "team id");
        field.setHidden(true);
        fields.add(field);
        field = new DataSourceTextField("team_name", "team name");
        field.setHidden(true);
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
	public void copyValues(ListGridRecord from, ProjectRecord to) {
		to.setProjectID(from.getAttributeAsInt("project_id"));
		to.setProjectName(from.getAttributeAsString("project_name"));
		if (from.getAttributeAsString("project_manager")!=null){
			to.setProjectManager(from.getAttributeAsString("project_manager"));
		}
		if (from.getAttributeAsInt("user_id")!=null){
			to.setUserID(from.getAttributeAsInt("user_id"));
		}
		
	}

	@Override
	public void copyValues(ProjectRecord from, ListGridRecord to) {
		to.setAttribute("project_id", from.getProjectID());
		to.setAttribute("project_name", from.getProjectName());
		if (from.getProjectManager()!=null){
			to.setAttribute("project_manager", from.getProjectManager());
		}
		to.setAttribute("project_path",	from.getProjectPath());
		to.setAttribute("user_id", from.getUserID());
		to.setAttribute("user_name", from.getUserName());
		to.setAttribute("team_id", from.getTeamID());
		to.setAttribute("team_name", from.getTeamName());
	}

	@Override
	public ProjectServiceAsync getServiceAsync() {
		return GWT.create(ProjectService.class);
	}

	@Override
	public ListGridRecord getNewRecordInstance() {
		return new ListGridRecord();
	}

	@Override
	public ProjectRecord getNewDataObjectInstance() {
		return new ProjectRecord();
	}
}
