package fr.pfgen.cgh.client.ui.stackMenus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.pfgen.cgh.client.Cgh;
import fr.pfgen.cgh.client.datasources.ProjectDS;
import fr.pfgen.cgh.client.services.ProjectService;
import fr.pfgen.cgh.client.services.ProjectServiceAsync;
import fr.pfgen.cgh.client.ui.MainArea;
import fr.pfgen.cgh.client.ui.tabs.ProjectTab;
import fr.pfgen.cgh.client.ui.windows.NewProjectWindow;
import fr.pfgen.cgh.client.utils.ClientUtils;
import fr.pfgen.cgh.shared.records.ProjectRecord;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Cursor;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.events.CloseClientEvent;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.layout.VLayout;

public class ProjectsStackMenu extends VLayout {
	
	private final ProjectServiceAsync projectService = GWT.create(ProjectService.class);

	public ProjectsStackMenu(){
		this.setMembersMargin(20);
		this.setLayoutTopMargin(20);
		this.setDefaultLayoutAlign(Alignment.CENTER);
		
		Criteria crits = new Criteria();
		boolean authorized = true;
        switch (Cgh.get().getUser().getAppStatus()) {
			case ADVANCED:
				crits.addCriteria("team_id", Integer.toString(Cgh.get().getUser().getTeamID()));
				break;
			case SIMPLE:
				crits.addCriteria("user_id", Integer.toString(Cgh.get().getUser().getUserID()));
				break;
			case RESTRICTED:
				crits.addCriteria("user_id", Integer.toString(Cgh.get().getUser().getUserID()));
				authorized = false;
				break;
			default:
				break;
		}
	
		DynamicForm form = new DynamicForm();
		
		final SelectItem projectCBItem = new SelectItem("My&nbsp;projects");  
        projectCBItem.setOptionDataSource(ProjectDS.getInstance());  
        projectCBItem.setOptionCriteria(crits);
        projectCBItem.setWidth(150);
        projectCBItem.setBrowserSpellCheck(false);
        projectCBItem.setDisplayField("project_name");
        projectCBItem.setValueField("project_id");
        form.setTitleOrientation(TitleOrientation.TOP);
        form.setAutoHeight();
        form.setAutoWidth();
        form.setFields(projectCBItem);
        
        projectCBItem.addChangedHandler(new ChangedHandler() {
			
			@Override
			public void onChanged(ChangedEvent event) {
				createProjectTab(Integer.parseInt(event.getValue().toString()));
				projectCBItem.clearValue();
			}
		});
        
        this.addMember(form);
        
        final IButton addProjectButton = new IButton();
        addProjectButton.setTitle("New Project");
        addProjectButton.setIcon("icons/Create.png");
        addProjectButton.setDisabledCursor(Cursor.NOT_ALLOWED);
        addProjectButton.setAutoFit(true);
        addProjectButton.setShowDisabledIcon(false);
        
        if(!authorized){
        	addProjectButton.setDisabled(true);
        }
        
        addProjectButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				final NewProjectWindow win = new NewProjectWindow(ProjectsStackMenu.this);
				win.show();
				win.addCloseClickHandler(new CloseClickHandler() {
					
					@Override
					public void onCloseClick(CloseClientEvent event) {
						win.destroy();
						addProjectButton.enable();
					}
				});
			}
		});
        
        this.addMember(addProjectButton);
	}
	
	public void createProjectTab(final Integer projectID) {
		Map<String, String> crits = new HashMap<String, String>();
		crits.put("project_id", projectID.toString());
		projectService.fetch(null, null, null, crits, new AsyncCallback<List<ProjectRecord>>() {
			
			@Override
			public void onSuccess(List<ProjectRecord> result) {
				if (result==null || result.isEmpty()){
					SC.warn("Cannot get project informations on server");
					return;
				}
				ProjectRecord project = result.get(0);
				String tabID = "Project_"+projectID;
				if (ClientUtils.tabExists(tabID)){
					MainArea.getTopTabSet().selectTab(tabID);
				}else{
					new ProjectTab(project);
				}
			}
			
			@Override
			public void onFailure(Throwable caught) {
				SC.warn("Cannot get array id '"+projectID+"' from database");
			}
		});
	}
}
