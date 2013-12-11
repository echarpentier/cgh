package fr.pfgen.cgh.client.ui;

import fr.pfgen.cgh.client.ui.stackMenus.AdministrationStackMenu;
import fr.pfgen.cgh.client.ui.stackMenus.ArraysStackMenu;
import fr.pfgen.cgh.client.ui.stackMenus.ProjectsStackMenu;

import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.VisibilityMode;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.SectionStack;
import com.smartgwt.client.widgets.layout.SectionStackSection;

public class NavigationArea extends HLayout {

	public NavigationArea() {

		super();

		this.setMembersMargin(20);  
		this.setOverflow(Overflow.HIDDEN);
		this.setShowResizeBar(true);

		final SectionStack sectionStack = new SectionStack();
		sectionStack.setShowExpandControls(true);
		sectionStack.setAnimateSections(true);
		sectionStack.setVisibilityMode(VisibilityMode.MUTEX);
		sectionStack.setOverflow(Overflow.HIDDEN);

		/*
		 * Section for management of arrays
		 */
		SectionStackSection arraysSection = new SectionStackSection("Arrays");
		arraysSection.setExpanded(true);
		arraysSection.setItems(new ArraysStackMenu());

		/*
		 * Section for management of projects
		 */
		SectionStackSection projectsSection = new SectionStackSection("Projects");
		projectsSection.setExpanded(false);
		projectsSection.setItems(new ProjectsStackMenu());

		/*
		 * Section for administration tasks
		 * -
		 */
		SectionStackSection administrationSection = new SectionStackSection("Administration");
		administrationSection.setExpanded(false);
		administrationSection.addItem(new AdministrationStackMenu());


		sectionStack.addSection(arraysSection);
		sectionStack.addSection(projectsSection);
		sectionStack.addSection(administrationSection);
		
		this.addMember(sectionStack);
	} 
}
