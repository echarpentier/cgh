package fr.pfgen.cgh.client.ui.tabs;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Dialog;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.events.CloseClientEvent;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.PasswordItem;
import com.smartgwt.client.widgets.form.validator.LengthRangeValidator;
import com.smartgwt.client.widgets.form.validator.MatchesFieldValidator;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

import fr.pfgen.cgh.client.services.UserService;
import fr.pfgen.cgh.client.services.UserServiceAsync;
import fr.pfgen.cgh.client.ui.MainArea;
import fr.pfgen.cgh.client.ui.vstacks.TeamVstack;
import fr.pfgen.cgh.client.ui.vstacks.UserVstack;
import fr.pfgen.cgh.client.ui.windows.NewTeamWindow;
import fr.pfgen.cgh.client.ui.windows.NewUserWindow;

public class AdministrationUserTab {
	
	private final UserServiceAsync userService = GWT.create(UserService.class);

	public AdministrationUserTab(String tabID) {
		VLayout vlayout = new VLayout(15);
		vlayout.setWidth("80%");
		vlayout.setDefaultLayoutAlign(Alignment.CENTER);

		final TeamVstack teamVstack = new TeamVstack();
		teamVstack.setDefaultLayoutAlign(Alignment.CENTER);
		teamVstack.setLayoutAlign(Alignment.CENTER);
		teamVstack.addHeaderLabel("Teams");
		teamVstack.addGrid();
		
		IButton newTeamButton = new IButton();
		newTeamButton.setShowDisabledIcon(false);
		newTeamButton.setIcon("icons/Create.png");
		newTeamButton.setTitle("Add team");
		newTeamButton.setAutoFit(true);
		
		newTeamButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				new NewTeamWindow().show();
			}
		});
		
		teamVstack.addMember(newTeamButton);
		
		vlayout.addMember(teamVstack);

		final UserVstack userVstack = new UserVstack();
		userVstack.setLayoutAlign(Alignment.CENTER);
		userVstack.addHeaderLabel("Users");
		userVstack.addGrid();
		userVstack.getGrid().fetchData();
		
		HLayout buttonLayout = new HLayout(20);
		buttonLayout.setAutoHeight();
		buttonLayout.setAutoWidth();
		buttonLayout.setLayoutAlign(Alignment.CENTER);
		
		IButton newUserButton = new IButton();
		newUserButton.setShowDisabledIcon(false);
		newUserButton.setIcon("icons/Create.png");
		newUserButton.setTitle("Add user");
		newUserButton.setAutoFit(true);
		
		newUserButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				new NewUserWindow().show();
			}
		});
		
		IButton changePassButton = new IButton();
		changePassButton.setShowDisabledIcon(false);
		changePassButton.setIcon("icons/Key.png");
		changePassButton.setTitle("Change password");
		changePassButton.setAutoFit(true);
		
		changePassButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				ListGridRecord selectedRec = userVstack.getGrid().getSelectedRecord();
				if (selectedRec==null){
					SC.warn("Please select a user for which you would like to change password");
					return;
				}
				createChangePassWindow(selectedRec);
			}
		});
		
		buttonLayout.addMember(newUserButton);
		buttonLayout.addMember(changePassButton);
		
		userVstack.addMember(buttonLayout);
		
		vlayout.addMember(userVstack);
		
		MainArea.addTabToTopTabset("Users",tabID, vlayout, true);
	}
	
	private void createChangePassWindow(final ListGridRecord user){
		final Dialog win = new Dialog();
		win.setTitle("New password");
		win.setAutoSize(true);
		win.setAutoCenter(true);
		win.setIsModal(true);
		win.setShowModalMask(true);
		win.setShowCloseButton(true);
		
		win.addCloseClickHandler(new CloseClickHandler() {
			
			@Override
			public void onCloseClick(CloseClientEvent event) {
				win.destroy();
			}
		});
		
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
	    
	    win.addItem(passForm);
	    
	    IButton changePassButton = new IButton();
		changePassButton.setShowDisabledIcon(false);
		changePassButton.setIcon("icons/Key.png");
		changePassButton.setTitle("Change password");
		changePassButton.setAutoFit(true);
		
		changePassButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if (passForm.validate()){
					int userID = user.getAttributeAsInt("user_id");
					String newPassword = password.getValueAsString();
					userService.changeUserPassword(userID, newPassword, new AsyncCallback<String>() {

						@Override
						public void onFailure(Throwable caught) {
							SC.warn("Could not change user password on server");
						}

						@Override
						public void onSuccess(String result) {
							if (result != null && !result.isEmpty() && !result.startsWith("Error:")){
								SC.say("Password changed for user "+user.getAttributeAsString("app_id"));
								win.destroy();
							}else{
								SC.warn("Could not change user password on server");
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
				win.destroy();	
			}
		});
		
		win.setToolbarButtons(changePassButton,cancelButton);
		
		win.show();
	}
}
