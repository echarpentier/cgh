package fr.pfgen.cgh.client.ui.tabs;

import fr.pfgen.cgh.shared.records.ArrayRecord;

import com.smartgwt.client.types.Side;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;

public class ProjectArrayTab extends Tab{

	private final TabSet studyTabset;
	private final ArrayRecord array;
	
	public ProjectArrayTab(ArrayRecord array, String tabID){
		this.array = array;
		this.setID(tabID);
		this.setTitle("aCGH: "+array.getName());
		this.setCanClose(true);
		
		studyTabset = new TabSet();
		studyTabset.setTabBarPosition(Side.LEFT);
		studyTabset.setWidth100();
		studyTabset.setHeight100();
		
		VLayout vlayout = new VLayout(15);
		vlayout.setWidth100();
		vlayout.setHeight100();
		
		vlayout.addMember(constructStudyTabset());
		
		this.setPane(vlayout);
	}
	
	private TabSet constructStudyTabset(){
		
		studyTabset.addTab(new ArrayInfosTab(array));
		studyTabset.addTab(new ArrayQualityControlsTab(array));
		studyTabset.addTab(new ArrayDetectionsTab(array));
		studyTabset.addTab(new UCSCVisuTab());
		
		return studyTabset;
	}
}
