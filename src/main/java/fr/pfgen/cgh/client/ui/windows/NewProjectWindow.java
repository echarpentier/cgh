package fr.pfgen.cgh.client.ui.windows;

import java.util.List;

import fr.pfgen.cgh.client.Cgh;
import fr.pfgen.cgh.client.datasources.ProjectDS;
import fr.pfgen.cgh.client.services.ProjectService;
import fr.pfgen.cgh.client.services.ProjectServiceAsync;
import fr.pfgen.cgh.client.ui.stackMenus.ProjectsStackMenu;

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

public class NewProjectWindow extends Dialog {

	private final ProjectServiceAsync projectService = GWT.create(ProjectService.class); 
	
	public NewProjectWindow(final ProjectsStackMenu pStackMenu){
		this.setTitle("New Project");
		this.setAutoSize(true);
		this.setAutoCenter(true);
		this.setIsModal(true);
		this.setShowModalMask(true);
		this.setShowCloseButton(true);
		
		final DynamicForm form = new DynamicForm();
		form.setBrowserSpellCheck(false);
		final ProjectDS datasource = ProjectDS.getInstance();
		datasource.getField("user_name").setHidden(true);
		form.setDataSource(datasource);
		form.setValidateOnChange(true);
		form.setAutoFocus(true);
		
		this.addItem(form);
		
		final IButton addProjectButton = new IButton();
		addProjectButton.setTitle("Add");
		addProjectButton.setIcon("icons/Create.png");
		addProjectButton.setShowDisabledIcon(false);
		addProjectButton.disable();
		
		form.addItemChangedHandler(new ItemChangedHandler() {
			
			@Override
			public void onItemChanged(ItemChangedEvent event) {
				if (form.validate()){
					addProjectButton.enable();
				}else{
					addProjectButton.disable();
				}
			}
		});
		
		addProjectButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if (form.validate()){
					projectService.getProjectNames(new AsyncCallback<List<String>>() {

						@Override
						public void onFailure(Throwable caught) {
							SC.warn("Could not fetch existing projects");
							return;
						}

						@Override
						public void onSuccess(List<String> result) {
							if (result == null){
								SC.warn("Could not fetch existing projects");
								return;
							}
							for (String projectName : result) {
								if (form.getValuesAsRecord().getAttributeAsString("project_name").equalsIgnoreCase(projectName)){
									SC.warn("Project name already exists, please choose another one");
									return;
								}
							}
							Record newProject = form.getValuesAsRecord();
							newProject.setAttribute("project_id", -1);
							newProject.setAttribute("user_id", Cgh.get().getUser().getUserID());
							datasource.addData(newProject, new DSCallback() {
								
								@Override
								public void execute(DSResponse response, Object rawData, DSRequest request) {
									if (response.getData()[0]==null || response.getData()[0].getAttributeAsInt("project_id")<0){
										SC.warn("Could not create project on server");
									}else{
										pStackMenu.createProjectTab(response.getData()[0].getAttributeAsInt("project_id"));
										NewProjectWindow.this.destroy();
									}
								}
							});
						}
					});
					
				}
			}
		});
		
		this.setToolbarButtons(addProjectButton);
	}
}
