package fr.pfgen.cgh.client.ui.windows;

import java.util.List;
import fr.pfgen.cgh.client.datasources.TeamDS;
import fr.pfgen.cgh.client.services.TeamService;
import fr.pfgen.cgh.client.services.TeamServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Dialog;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.events.ItemChangedEvent;
import com.smartgwt.client.widgets.form.events.ItemChangedHandler;

public class NewTeamWindow extends Dialog {

	private final TeamServiceAsync teamService = GWT.create(TeamService.class); 
	
	public NewTeamWindow(){
		this.setTitle("New Team");
		this.setAutoSize(true);
		this.setAutoCenter(true);
		this.setIsModal(true);
		this.setShowModalMask(true);
		this.setShowCloseButton(true);
		
		final DynamicForm form = new DynamicForm();
		form.setBrowserSpellCheck(false);
		final TeamDS datasource = TeamDS.getInstance();
		form.setDataSource(datasource);
		form.setValidateOnChange(true);
		form.setAutoFocus(true);
		
		this.addItem(form);
		
		final IButton addTeamButton = new IButton();
		addTeamButton.setTitle("Add");
		addTeamButton.setIcon("icons/Create.png");
		addTeamButton.setShowDisabledIcon(false);
		addTeamButton.disable();
		addTeamButton.setAutoFit(true);
		
		form.addItemChangedHandler(new ItemChangedHandler() {
			
			@Override
			public void onItemChanged(ItemChangedEvent event) {
				if (form.valuesAreValid(false)){
					addTeamButton.enable();
				}else{
					addTeamButton.disable();
				}
			}
		});
		
		addTeamButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if (form.validate()){
					teamService.getTeamNames(new AsyncCallback<List<String>>() {

						@Override
						public void onFailure(Throwable caught) {
							SC.warn("Could not fetch existing teams");
							return;
						}

						@Override
						public void onSuccess(List<String> result) {
							if (result == null){
								SC.warn("Could not fetch existing teams");
								return;
							}
							for (String teamName : result) {
								if (form.getValuesAsRecord().getAttributeAsString("team_name").equalsIgnoreCase(teamName)){
									SC.warn("Team name already exists, please choose another one");
									return;
								}
							}
							Record newTeam = form.getValuesAsRecord();
							newTeam.setAttribute("team_id", -1);
							datasource.addData(newTeam, new DSCallback() {
								
								@Override
								public void execute(DSResponse response, Object rawData, DSRequest request) {
									if (response.getData()[0]==null || response.getData()[0].getAttributeAsInt("team_id")<0){
										SC.warn("Could not create team on server");
									}else{
										NewTeamWindow.this.destroy();
									}
								}
							});
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
				NewTeamWindow.this.destroy();	
			}
		});
		
		this.setToolbarButtons(addTeamButton,cancelButton);
	}
}
