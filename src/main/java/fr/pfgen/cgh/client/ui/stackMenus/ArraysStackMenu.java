package fr.pfgen.cgh.client.ui.stackMenus;

import fr.pfgen.cgh.client.Cgh;
import fr.pfgen.cgh.client.ui.MainArea;
import fr.pfgen.cgh.client.ui.tabs.ArrayAnalysisTab;
import fr.pfgen.cgh.client.ui.tabs.SearchTab;
import fr.pfgen.cgh.client.utils.ClientUtils;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.VLayout;

public class ArraysStackMenu extends VLayout {

	public ArraysStackMenu(){
		
		this.setMembersMargin(20);
		this.setLayoutTopMargin(20);
		this.setDefaultLayoutAlign(Alignment.CENTER);
		
		IButton searchButton = new IButton();
		searchButton.setTitle("Search");
		searchButton.setAutoFit(true);
		searchButton.setIcon("icons/Search.png");
		searchButton.setShowDisabledIcon(false);
		
		searchButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				String tabID = "SearchTab";
				if (ClientUtils.tabExists(tabID)){
					MainArea.getTopTabSet().selectTab(MainArea.getTopTabSet().getTab(tabID));
				}else{
					new SearchTab(tabID);
				}
			}
		});
		
		IButton analysisButton = new IButton();
		analysisButton.setTitle("Array analysis");
		analysisButton.setAutoFit(true);
		analysisButton.setIcon("icons/Pinion.png");
		analysisButton.setShowDisabledIcon(false);
		
		analysisButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				String tabID = "ArrayAnalysis";
				if (ClientUtils.tabExists(tabID)){
					MainArea.getTopTabSet().selectTab(MainArea.getTopTabSet().getTab(tabID));
				}else{
					new ArrayAnalysisTab(tabID);
				}
			}
		});
		
		switch (Cgh.get().getUser().getAppStatus()) {
		case RESTRICTED:
			analysisButton.disable();
			break;
		default:
			break;
		}
		
		this.addMember(searchButton);
		this.addMember(analysisButton);
	}
}
