package fr.pfgen.cgh.client.ui.tabs;

import fr.pfgen.cgh.shared.records.ProjectRecord;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Side;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;

public class ProjectHomeTab extends Tab {

	private VLayout tabPane;
	private TabSet projectTopTabset;
	private ProjectRecord project;
	private TabSet projectLeftTabset;
	
	public ProjectHomeTab(ProjectRecord project, TabSet mainProjectTabSet){
		this.project = project;
		this.projectTopTabset = mainProjectTabSet;
		this.setPrompt("Project "+project.getProjectName());
		this.setTitle("Project&nbsp;home");
		this.setCanClose(false);
		
		projectLeftTabset = new TabSet();
		projectLeftTabset.setTabBarPosition(Side.LEFT);
		projectLeftTabset.setWidth100();
		projectLeftTabset.setHeight100();
		
		tabPane = new VLayout(20);
		tabPane.setDefaultLayoutAlign(Alignment.CENTER);
		tabPane.addMember(constructProjectLeftTabset());
		setPane(tabPane);
	}
	
	private TabSet constructProjectLeftTabset(){
		
		projectLeftTabset.addTab(new ProjectHomeInfoTab(project, projectTopTabset));
		projectLeftTabset.addTab(new ProjectHomeParamTab(project));
		projectLeftTabset.addTab(new ProjectHomeDetectionsTab(project));
		projectLeftTabset.addTab(new ProjectHomeAnalysisTab(project));
		projectLeftTabset.addTab(new UCSCVisuTab());
		
		return projectLeftTabset;
	}
}
