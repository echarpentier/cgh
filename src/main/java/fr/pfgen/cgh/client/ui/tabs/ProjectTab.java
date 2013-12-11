package fr.pfgen.cgh.client.ui.tabs;

import fr.pfgen.cgh.client.ui.MainArea;
import fr.pfgen.cgh.shared.records.ProjectRecord;

import com.smartgwt.client.types.Side;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.TabSet;

public class ProjectTab{

	private final TabSet projectTopTabset;
	private final ProjectRecord project;
	
	public ProjectTab(ProjectRecord project) {
		this.project = project;
		
		projectTopTabset = new TabSet();
		projectTopTabset.setTabBarPosition(Side.TOP);
		projectTopTabset.setDestroyPanes(true);
		projectTopTabset.setWidth100();
		projectTopTabset.setHeight100();
		
		VLayout vlayout = new VLayout(15);
		vlayout.setWidth100();
		vlayout.setHeight100();
		
		vlayout.addMember(constructProjectTabset());
		
		/*
		 * Add layout to mainArea tab
		 */
		
		MainArea.addTabToTopTabset("Project:&nbsp;"+project.getProjectName(),"Project_"+project.getProjectID(), vlayout, true);
	}
	
	private TabSet constructProjectTabset(){
		
		projectTopTabset.addTab(new ProjectHomeTab(project,projectTopTabset));
		
		return projectTopTabset;
	}
}
