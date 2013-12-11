package fr.pfgen.cgh.client.ui.tabs;

import fr.pfgen.cgh.client.ui.MainArea;
import fr.pfgen.cgh.shared.records.ArrayRecord;

import com.smartgwt.client.types.Side;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.TabSet;

public class ArrayTab {
	
	private final TabSet studyTabset;
	private final ArrayRecord array;
	
	public ArrayTab(ArrayRecord array){
		this.array = array;
		
		studyTabset = new TabSet();
		studyTabset.setTabBarPosition(Side.LEFT);
		studyTabset.setWidth100();
		studyTabset.setHeight100();
		
		VLayout vlayout = new VLayout(15);
		vlayout.setWidth100();
		vlayout.setHeight100();
		
		vlayout.addMember(constructStudyTabset());
		
		/*
		 * Add layout to mainArea tab
		 */
		
		MainArea.addTabToTopTabset("aCGH:&nbsp;"+array.getName(),"Array_"+array.getID(), vlayout, true);
	}
	
	private TabSet constructStudyTabset(){
		
		studyTabset.addTab(new ArrayInfosTab(array));
		studyTabset.addTab(new ArrayQualityControlsTab(array));
		studyTabset.addTab(new ArrayDetectionsTab(array));
		studyTabset.addTab(new UCSCVisuTab());
		
		return studyTabset;
	}
}
