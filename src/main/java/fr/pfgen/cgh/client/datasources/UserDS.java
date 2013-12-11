package fr.pfgen.cgh.client.datasources;

import java.util.ArrayList;
import java.util.List;

import fr.pfgen.cgh.client.services.UserService;
import fr.pfgen.cgh.client.services.UserServiceAsync;
import fr.pfgen.cgh.client.utils.ClientUtils;
import fr.pfgen.cgh.shared.enums.UserStatus;
import fr.pfgen.cgh.shared.records.UserRecord;

import com.google.gwt.core.client.GWT;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.data.SimpleType;
import com.smartgwt.client.data.fields.DataSourceEnumField;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourcePasswordField;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;

public class UserDS extends GenericGwtRpcDataSource<UserRecord, ListGridRecord, UserServiceAsync> {

	private static UserDS instance;
	
	private UserDS(){
		
	}
	
	public static UserDS getInstance(){
		if (instance==null){
			instance = new UserDS();
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
		if (SimpleType.getType("officeType")==null){
			SimpleType officeType = new ClientUtils.OfficeType();
			officeType.register();
		}
		if (SimpleType.getType("emailType")==null){
			SimpleType emailType = new ClientUtils.EmailType();
			emailType.register();
		}
		
        DataSourceField field;
        field = new DataSourceIntegerField("user_id", "id");
        field.setPrimaryKey(true);
        field.setHidden(true);
        fields.add(field);
        field = new DataSourceTextField("firstname", "firstname");
        field.setRequired(true);
        field.setCanEdit(true);
        field.setType(SimpleType.getType("firstLastNameType"));
        fields.add(field);
        field = new DataSourceTextField("lastname", "lastname");
        field.setRequired(true);
        field.setCanEdit(true);
        field.setType(SimpleType.getType("firstLastNameType"));
        fields.add(field);
        field = new DataSourceTextField("email", "email");
        field.setRequired(true);
        field.setCanEdit(true);
        field.setType(SimpleType.getType("emailType"));
        fields.add(field);
        field = new DataSourceTextField ("office_number", "office");
        field.setType(SimpleType.getType("officeType"));
        field.setCanEdit(true);
        fields.add(field);
        field = new DataSourceIntegerField("team_id", "team id");
        field.setHidden(true);
        fields.add(field);
        field = new DataSourceEnumField("team_name", "team");
        field.setRequired(true);
        fields.add(field);
        field = new DataSourceTextField ("app_id", "app id");
        field.setRequired(true);
        field.setType(SimpleType.getType("nameType"));
        fields.add(field);
        field = new DataSourcePasswordField("app_password", "password");
        field.setRequired(true);
        field.setHidden(true);
        fields.add(field);
        field = new DataSourceEnumField("app_status", "status");
        field.setRequired(true);
        field.setCanEdit(true);
        field.setValueMap(UserStatus.toStringList().toArray(new String[UserStatus.toStringList().size()]));
        fields.add(field);
        field = new DataSourceTextField("login_text", "text");
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
	public void copyValues(ListGridRecord from, UserRecord to) {
		to.setUserID(from.getAttributeAsInt("user_id"));
		to.setFirstname(from.getAttributeAsString("firstname"));
		to.setLastname(from.getAttributeAsString("lastname"));
		to.setEmail(from.getAttributeAsString("email"));
		if (from.getAttributeAsString("office_number")!=null){
			to.setOffice_number(from.getAttributeAsString("office_number"));
		}
		if (from.getAttributeAsInt("team_id")!=null){
			to.setTeamID(from.getAttributeAsInt("team_id"));
		}
		to.setTeamName(from.getAttributeAsString("team_name"));
		to.setAppID(from.getAttributeAsString("app_id"));
		to.setPassword(from.getAttributeAsString("app_password"));
		to.setAppStatus(UserStatus.parse(from.getAttributeAsString("app_status")));
		to.setLoginText(from.getAttributeAsString("login_text"));
	}

	@Override
	public void copyValues(UserRecord from, ListGridRecord to) {
		to.setAttribute("user_id", from.getUserID());
		to.setAttribute("firstname", from.getFirstname());
		to.setAttribute("lastname", from.getLastname());
		to.setAttribute("email", from.getEmail());
		if (from.getOffice_number()!=null){
			to.setAttribute("office_number", from.getOffice_number());
		}
		to.setAttribute("team_id", from.getTeamID());
		to.setAttribute("team_name", from.getTeamName());
		to.setAttribute("app_id", from.getAppID());
		to.setAttribute("app_password", from.getPassword());
		to.setAttribute("app_status", from.getAppStatus().toString());
		to.setAttribute("login_text", from.getLoginText());
	}

	@Override
	public UserServiceAsync getServiceAsync() {
		return GWT.create(UserService.class);
	}

	@Override
	public ListGridRecord getNewRecordInstance() {
		return new ListGridRecord();
	}

	@Override
	public UserRecord getNewDataObjectInstance() {
		return new UserRecord();
	}
}
