package fr.pfgen.cgh.client.datasources;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.data.fields.DataSourceEnumField;
import com.smartgwt.client.data.fields.DataSourceFloatField;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;

import fr.pfgen.cgh.client.services.DetectionService;
import fr.pfgen.cgh.client.services.DetectionServiceAsync;
import fr.pfgen.cgh.shared.enums.DetectionQuality;
import fr.pfgen.cgh.shared.records.DetectionRecord;

public class DetectionDS extends GenericGwtRpcDataSource<DetectionRecord, ListGridRecord, DetectionServiceAsync> {

	private static DetectionDS instance;
	
	private DetectionDS(){
	}
	
	public static DetectionDS getInstance(){
		if (instance==null){
			instance = new DetectionDS();
		}
		return instance;
	}
	
	@Override
	public List<DataSourceField> getDataSourceFields() {
		List<DataSourceField> fields = new ArrayList<DataSourceField>();
		
        DataSourceField field;
        field = new DataSourceIntegerField("d_id", "id");
        field.setPrimaryKey(true);
        field.setHidden(true);
        fields.add(field);
        field = new DataSourceIntegerField("d_chr", "chromosome");
        fields.add(field);
        field = new DataSourceIntegerField("d_start", "start");
        fields.add(field);
        field = new DataSourceIntegerField("d_end", "end");
        fields.add(field);
        field = new DataSourceIntegerField("d_size", "size");
        fields.add(field);
        field = new DataSourceIntegerField ("d_probe_number", "probes");
        fields.add(field);
        field = new DataSourceFloatField("d_LR_median", "LR median");
        fields.add(field);
        field = new DataSourceEnumField("d_quality", "quality");
        field.setValueMap("TP","FP","SM","");
        field.setCanEdit(true);
        fields.add(field);
        field = new DataSourceIntegerField("frame_id", "frameid");
        field.setHidden(true);
        fields.add(field);
        field = new DataSourceTextField("frame_name", "frame");
        fields.add(field);
        
        return fields;
	}

	@Override
	public void copyValues(ListGridRecord from, DetectionRecord to) {
		to.setID(from.getAttributeAsInt("d_id"));
		to.setChr(from.getAttributeAsInt("d_chr"));
		to.setStart(from.getAttributeAsInt("d_start"));
		to.setEnd(from.getAttributeAsInt("d_end"));
		to.setProbeNumber(from.getAttributeAsInt("d_probe_number"));
		to.setLRmedian(from.getAttributeAsDouble("d_LR_median"));
		to.setQuality(DetectionQuality.parse(from.getAttributeAsString("d_quality")));
		to.setFrameID(from.getAttributeAsInt("frame_id"));
		to.setFrameName(from.getAttributeAsString("frame_name"));
		to.setUCSCPosition(from.getAttributeAsString("d_position"));
		to.setGenomicBuild(from.getAttributeAsString("genomic_build"));
	}

	@Override
	public void copyValues(DetectionRecord from, ListGridRecord to) {
		to.setAttribute("d_id",	from.getID());
		to.setAttribute("d_chr", from.getChr());
		to.setAttribute("d_start", from.getStart());
		to.setAttribute("d_end", from.getEnd());
		to.setAttribute("d_size", from.getEnd()-from.getStart());
		to.setAttribute("d_probe_number", from.getProbeNumber());
		to.setAttribute("d_LR_median", from.getLRmedian());
		if (from.getQuality()==null){
			to.setAttribute("d_quality", "");
		}else{
			to.setAttribute("d_quality", from.getQuality().toString());
		}
		to.setAttribute("frame_id", from.getFrameID());
		to.setAttribute("frame_name", from.getFrameName());
		to.setAttribute("d_position", from.getUCSCPosition());
		to.setAttribute("genomic_build", from.getGenomicBuild());
	}

	@Override
	public DetectionServiceAsync getServiceAsync() {
		return GWT.create(DetectionService.class);
	}

	@Override
	public ListGridRecord getNewRecordInstance() {
		return new ListGridRecord();
	}

	@Override
	public DetectionRecord getNewDataObjectInstance() {
		return new DetectionRecord();
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
			field.setWidth(10);
			returnFields.add(field);
		}
		return returnFields;
	}
}
