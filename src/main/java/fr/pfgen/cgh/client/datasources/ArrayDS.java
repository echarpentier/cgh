package fr.pfgen.cgh.client.datasources;

import java.util.ArrayList;
import java.util.List;

import fr.pfgen.cgh.client.services.ArrayService;
import fr.pfgen.cgh.client.services.ArrayServiceAsync;
import fr.pfgen.cgh.shared.records.ArrayRecord;

import com.google.gwt.core.client.GWT;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.data.fields.DataSourceDateField;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;

public class ArrayDS extends GenericGwtRpcDataSource<ArrayRecord, ListGridRecord, ArrayServiceAsync> {

	private static ArrayDS instance;
	
	private ArrayDS(){
	}
	
	public static ArrayDS getInstance(){
		if (instance==null){
			instance = new ArrayDS();
		}
		return (instance);
	}
	
	@Override
	public List<DataSourceField> getDataSourceFields() {
		List<DataSourceField> fields = new ArrayList<DataSourceField>();
    	
        DataSourceField field;
        field = new DataSourceIntegerField("array_id", "id");
        field.setPrimaryKey(true);
        field.setHidden(true);
        fields.add(field);
        field = new DataSourceTextField("array_name", "name");
        fields.add(field);
        field = new DataSourceIntegerField("frame_number", "frames");
        fields.add(field);
        field = new DataSourceDateField("scan_date", "date");
        fields.add(field);
        field = new DataSourceTextField ("genomic_build", "build");
        fields.add(field);
        field = new DataSourceTextField ("design_name", "design");
        fields.add(field);
        field = new DataSourceIntegerField("project_id", "project id");
        field.setHidden(true);
        fields.add(field);
        field = new DataSourceTextField("project_name", "project");
        fields.add(field);
        field = new DataSourceIntegerField ("user_id", "user id");
        field.setHidden(true);
        fields.add(field);
        field = new DataSourceTextField("user_name", "user");
        fields.add(field);
        
        return fields;
	}

	@Override
	public void copyValues(ListGridRecord from, ArrayRecord to) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void copyValues(ArrayRecord from, ListGridRecord to) {
		to.setAttribute("array_id", from.getID());
		to.setAttribute("array_name", from.getName());
		to.setAttribute("frame_number", from.getFrameNumber());
		to.setAttribute("scan_date", from.getScanDate());
		to.setAttribute("genomic_build", from.getGenomicBuild());
		to.setAttribute("design_name", from.getDesignName());
		if (from.getProjectName()!=null){
			to.setAttribute("project_id", from.getProjectId());
			to.setAttribute("project_name", from.getProjectName());
		}
		to.setAttribute("user_id", from.getUserID());
		to.setAttribute("user_name", from.getUserName());
	}

	@Override
	public ArrayServiceAsync getServiceAsync() {
		return GWT.create(ArrayService.class);
	}

	@Override
	public ListGridRecord getNewRecordInstance() {
		return new ListGridRecord();
	}

	@Override
	public ArrayRecord getNewDataObjectInstance() {
		return new ArrayRecord();
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
}
