package fr.pfgen.cgh.client.ui.windows;

import fr.pfgen.cgh.client.datasources.UserDS;

import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Dialog;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.events.ItemChangedEvent;
import com.smartgwt.client.widgets.form.events.ItemChangedHandler;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.PasswordItem;
import com.smartgwt.client.widgets.form.validator.LengthRangeValidator;
import com.smartgwt.client.widgets.form.validator.MatchesFieldValidator;

public class NewUserWindow extends Dialog {

	public NewUserWindow(){
		this.setTitle("New User");
		this.setAutoSize(true);
		this.setAutoCenter(true);
		this.setIsModal(true);
		this.setShowModalMask(true);
		this.setShowCloseButton(true);
		this.setDefaultLayoutAlign(Alignment.CENTER);
		
		final DynamicForm form = new DynamicForm();
		form.setAutoHeight();
		form.setAutoWidth();
		form.setBrowserSpellCheck(false);
		final UserDS datasource = UserDS.getInstance();
		form.setDataSource(datasource);
		form.setValidateOnChange(false);
		form.setAutoFocus(true);
		
		MatchesFieldValidator validator = new MatchesFieldValidator();
	    validator.setOtherField("password");
	    validator.setErrorMessage("Passwords do not match");
	    
	    LengthRangeValidator validator2 = new LengthRangeValidator();
	    validator2.setMin(4);
	    validator2.setMax(15);
	    validator2.setErrorMessage("Password must be at least 4 and at most 15 characters long");

	    final PasswordItem password = new PasswordItem();
	    password.setName("password");
	    password.setTitle("Password");
	    password.setRequired(true);
	    password.setValidators(validator2);

	    PasswordItem password2 = new PasswordItem();
	    password2.setName("password2");
	    password2.setTitle("Password again");
	    password2.setRequired(true);
	    password2.setValidators(validator,validator2);
	    password2.setValidateOnChange(true);
	    
	    final DynamicForm passForm = new DynamicForm();
	    passForm.setAutoHeight();
	    passForm.setAutoWidth();
	    passForm.setBrowserSpellCheck(false);
	    passForm.setValidateOnChange(false);
	    passForm.setAutoFocus(false);
	    
	    passForm.setFields(password,password2);
	    
		this.addItem(form);
		this.addItem(passForm);
		
		final IButton addUserButton = new IButton();
		addUserButton.setTitle("Add");
		addUserButton.setIcon("icons/Create.png");
		addUserButton.setShowDisabledIcon(false);
		addUserButton.disable();
		addUserButton.setAutoFit(true);
		
		for (FormItem item : form.getFields()) {
			item.setValidateOnExit(true);
		}
		
		form.addItemChangedHandler(new ItemChangedHandler() {
			
			@Override
			public void onItemChanged(ItemChangedEvent event) {
				if (form.valuesAreValid(false) && passForm.valuesAreValid(false)){
					addUserButton.enable();
				}else{
					addUserButton.disable();
				}
			}
		});
		
		passForm.addItemChangedHandler(new ItemChangedHandler() {
			
			@Override
			public void onItemChanged(ItemChangedEvent event) {
				if (form.valuesAreValid(false) && passForm.valuesAreValid(false)){
					addUserButton.enable();
				}else{
					addUserButton.disable();
				}
			}
		});
		
		addUserButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if (form.validate() && passForm.validate()){
					addUserButton.disable();
					Record newUser = form.getValuesAsRecord();
					newUser.setAttribute("user_id", -1);
					newUser.setAttribute("app_password", password.getValueAsString());
					datasource.addData(newUser, new DSCallback() {
						
						@Override
						public void execute(DSResponse response, Object rawData, DSRequest request) {
							if (response.getData()[0]==null){
								SC.warn("Could not create user in database");
								addUserButton.enable();
							}else if (response.getData()[0].getAttributeAsInt("user_id")<0){
								SC.warn(response.getData()[0].getAttributeAsString("login_text"));
								addUserButton.enable();
							}else{
								NewUserWindow.this.destroy();
							}
						}
					});
				}
			}
		});
		
		IButton cancelButton = new IButton();
		cancelButton.setIcon("icons/Cancel.png");
		cancelButton.setAutoFit(true);
		cancelButton.setTitle("Cancel");
		
		cancelButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				NewUserWindow.this.destroy();	
			}
		});
		
		this.setToolbarButtons(addUserButton, cancelButton);
	}
}
