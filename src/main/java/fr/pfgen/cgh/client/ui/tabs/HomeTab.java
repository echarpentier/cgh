package fr.pfgen.cgh.client.ui.tabs;

import fr.pfgen.cgh.client.ui.MainArea;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.BkgndRepeat;
import com.smartgwt.client.widgets.HTMLFlow;
import com.smartgwt.client.widgets.layout.VLayout;

public class HomeTab {

	public HomeTab(){
		VLayout vlayout = new VLayout(15);
		vlayout.setWidth("80%");
		vlayout.setDefaultLayoutAlign(Alignment.CENTER);
		
		vlayout.setBackgroundImage("logos/pf-genomique.png");
		vlayout.setBackgroundRepeat(BkgndRepeat.NO_REPEAT);
		vlayout.setBackgroundPosition("center");
		
		HTMLFlow htmlFlow = new HTMLFlow();
		htmlFlow.setLayoutAlign(Alignment.CENTER);
        htmlFlow.setWidth100();  
        htmlFlow.setStyleName("EC_TabTitle");  
        //String contents = "<hr><span class='exampleDropTitle'>Ajax  </span> " +  
        //        "<b>A</b>synchronous <b>J</b>avaScript <b>A</b>nd <b>X</b>ML (AJAX) is a " +  
        //        "Web development technique for creating interactive <b>web applications</b>.<hr>";  
        //String contents = "<center><font size=\"5\"><u><b>Welcome to the new aCGH analysis application</b></u></font></center>";
        String contents = "Welcome to the new aCGH analysis application";
        htmlFlow.setBackgroundColor("white");
        htmlFlow.setContents(contents);  
        htmlFlow.draw(); 
        
        vlayout.addMember(htmlFlow);
		
		MainArea.addTabToTopTabset("Home","MainArea", vlayout, false);	
	}
}
