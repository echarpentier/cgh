package fr.pfgen.cgh.client.datasources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;

import fr.pfgen.cgh.client.services.FrameService;
import fr.pfgen.cgh.client.services.FrameServiceAsync;
import fr.pfgen.cgh.shared.records.FrameRecord;

public class FrameDS extends GenericGwtRpcDataSource<FrameRecord, ListGridRecord, FrameServiceAsync> {

	private static FrameDS instance;
	
	private FrameDS(){
		
	}
	
	public static FrameDS getInstance(){
		if (instance==null){
			instance = new FrameDS();
		}
		return instance;
	}
	
	@Override
	public List<DataSourceField> getDataSourceFields() {
		List<DataSourceField> fields = new ArrayList<DataSourceField>();
    	
        DataSourceField field;
        field = new DataSourceIntegerField("frame_id", "id");
        field.setPrimaryKey(true);
        field.setHidden(true);
        fields.add(field);
        field = new DataSourceTextField("frame_name", "name");
        fields.add(field);
        field = new DataSourceTextField("test_name", "test");
        field.setCanEdit(true);
        fields.add(field);
        field = new DataSourceTextField("ref_name", "ref");
        field.setCanEdit(true);
        fields.add(field);
        field = new DataSourceTextField ("array_name", "array name");
        field.setHidden(true);
        fields.add(field);
        field = new DataSourceIntegerField ("array_id", "array ID");
        field.setHidden(true);
        fields.add(field);
        field = new DataSourceTextField("fe_file_path", "FE path");
        field.setHidden(true);
        fields.add(field);
        field = new DataSourceTextField("result_folder_path", "result path");
        field.setHidden(true);
        fields.add(field);
        
        return fields;
	}

	@Override
	public void copyValues(ListGridRecord from, FrameRecord to) {
		List<String> handledAttributes = new ArrayList<String>();
		
		to.setID(from.getAttributeAsInt("frame_id"));
		handledAttributes.add("frame_id");
		to.setName(from.getAttributeAsString("frame_name"));
		handledAttributes.add("frame_name");
		to.setRefName(from.getAttributeAsString("ref_name"));
		handledAttributes.add("ref_name");
		to.setTestName(from.getAttributeAsString("test_name"));
		handledAttributes.add("test_name");
		to.setArrayID(from.getAttributeAsInt("array_id"));
		handledAttributes.add("array_id");
		to.setArrayName(from.getAttributeAsString("array_name"));
		handledAttributes.add("array_name");
		to.setFEFilePath(from.getAttributeAsString("fe_file_path"));
		handledAttributes.add("fe_file_path");
		to.setResultFolderPath(from.getAttributeAsString("result_folder_path"));
		handledAttributes.add("result_folder_path");
		
		
		HashMap<String, String> qcMap = new LinkedHashMap<String, String>();
        String[] attributes = from.getAttributes();
        for (String att : attributes) {
			if (handledAttributes.contains(att)){
				continue;
			}else{
				qcMap.put(att, from.getAttributeAsString(att));
			}
		}
        to.setQcResultMap(qcMap);
	}

	@Override
	public void copyValues(FrameRecord from, ListGridRecord to) {
		to.setAttribute("frame_id", from.getID());
		to.setAttribute("frame_name", from.getName());
		if (from.getRefName()!=null){
			to.setAttribute("ref_name", from.getRefName());
		}
		if (from.getTestName()!=null){
			to.setAttribute("test_name", from.getTestName());
		}
		to.setAttribute("array_name", from.getArrayName());
		to.setAttribute("array_id", from.getArrayID());
		to.setAttribute("fe_file_path", from.getFEFilePath());
		to.setAttribute("result_folder_path", from.getResultFolderPath());
		for (String qcParam : from.getQcResultMap().keySet()) {
			to.setAttribute(qcParam, from.getQcResultMap().get(qcParam));
		}
	}

	@Override
	public FrameServiceAsync getServiceAsync() {
		return GWT.create(FrameService.class);
	}

	@Override
	public ListGridRecord getNewRecordInstance() {
		return new ListGridRecord();
	}

	@Override
	public FrameRecord getNewDataObjectInstance() {
		return new FrameRecord();
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
