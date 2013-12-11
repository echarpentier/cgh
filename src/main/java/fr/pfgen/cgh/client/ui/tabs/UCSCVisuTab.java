package fr.pfgen.cgh.client.ui.tabs;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ContentsType;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.HTMLPane;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.tab.Tab;

public class UCSCVisuTab extends Tab {

	private HLayout tabPane;
	
	public UCSCVisuTab() {
		
		tabPane = new HLayout(10);
		tabPane.setDefaultLayoutAlign(Alignment.LEFT);
		
		setPrompt("UCSC genome browser");
		setTitle("&nbsp;"+Canvas.imgHTML("icons/UCSC.gif",16,16));
		
        final HTMLPane htmlPane = new HTMLPane();  
        htmlPane.setShowEdges(true);  
        htmlPane.setContentsURL("http://genome.ucsc.edu/");  
        htmlPane.setContentsType(ContentsType.PAGE);
        
        tabPane.addMember(htmlPane);
		
		setPane(tabPane);
	}

}
