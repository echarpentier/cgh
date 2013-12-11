package fr.pfgen.cgh.client.ui.stackMenus;

import fr.pfgen.cgh.client.Cgh;
import fr.pfgen.cgh.client.ui.MainArea;
import fr.pfgen.cgh.client.ui.tabs.AdministrationUserTab;
import fr.pfgen.cgh.client.utils.ClientUtils;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.VLayout;

public class AdministrationStackMenu extends VLayout {

	public AdministrationStackMenu(){
		this.setMembersMargin(20);
		this.setLayoutTopMargin(20);
		this.setDefaultLayoutAlign(Alignment.CENTER);
		
		IButton usersButton = new IButton();
		usersButton.setTitle("Users");
		usersButton.setAutoFit(true);
		usersButton.setIcon("icons/People.png");
		usersButton.setShowDisabledIcon(false);
		usersButton.disable();
		
		usersButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				String tabID = "AdministrationUserTab";
				if (ClientUtils.tabExists(tabID)){
					//MainArea.getTopTabSet().selectTab(MainArea.getTopTabSet().getTab(tabID));
					MainArea.getTopTabSet().selectTab(tabID);
				}else{
					new AdministrationUserTab(tabID);
				}
			}
		});
		
		switch (Cgh.get().getUser().getAppStatus()) {
		case ADMIN:
			usersButton.enable();
			break;
		default:
			
			break;
		}
		
		this.addMember(usersButton);
	}
}
