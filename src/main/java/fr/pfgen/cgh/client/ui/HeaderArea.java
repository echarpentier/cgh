package fr.pfgen.cgh.client.ui;

import fr.pfgen.cgh.client.Cgh;

import com.google.gwt.user.client.Window;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Cursor;
import com.smartgwt.client.types.ImageStyle;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

public class HeaderArea extends HLayout {

    private static final int HEADER_AREA_HEIGHT = 60;
    public static Label signedInUser = new Label();
    public static Label helpLabel = new Label();
    
    public HeaderArea() {

        super();

        this.setHeight(HEADER_AREA_HEIGHT);
        
        Img biogenouest = new Img("logos/biogenouest.png",112,75);
        biogenouest.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				Window.open("http://www.biogenouest.org", "_blank", "");
			}
		});
        biogenouest.setCursor(Cursor.HAND);
        
        Img inserm = new Img("logos/inserm.png",225,60);
        inserm.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				Window.open("http://www.inserm.fr", "_blank", "");
			}
		});
        inserm.setCursor(Cursor.HAND);
        
        Img paysloire = new Img("logos/paysloire.png",225,60);
        paysloire.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				Window.open("http://www.paysdelaloire.fr", "_blank", "");
			}
		});
        paysloire.setCursor(Cursor.HAND);
        
        Img universitenantes = new Img("logos/universitenantes.jpg",120,75);
        universitenantes.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				Window.open("http://www.univ-nantes.fr", "_blank", "");
			}
		});
        universitenantes.setCursor(Cursor.HAND);

        paysloire.setMargin(3);
        inserm.setMargin(3);
        biogenouest.setImageType(ImageStyle.STRETCH);
        inserm.setImageType(ImageStyle.STRETCH);
        paysloire.setImageType(ImageStyle.STRETCH);
        universitenantes.setImageType(ImageStyle.STRETCH);
        
        HLayout centerLayout = new HLayout();
        centerLayout.setAlign(Alignment.LEFT);
        centerLayout.setHeight(HEADER_AREA_HEIGHT);    
        centerLayout.setWidth("70%");
        centerLayout.setLayoutLeftMargin(10);
        centerLayout.setMembersMargin(40);
        centerLayout.addMember(biogenouest);
        centerLayout.addMember(paysloire);
        centerLayout.addMember(inserm);
        centerLayout.addMember(universitenantes);
           
        signedInUser.setContents("User:&nbsp;"+Cgh.get().getUser().getFirstname()+"&nbsp;"+Cgh.get().getUser().getLastname());
        helpLabel.setStyleName("textTitle");
        signedInUser.setAutoHeight();
        helpLabel.setAutoHeight();
        
        HLayout eastLayout = new HLayout();
        eastLayout.setAlign(Alignment.RIGHT);
        eastLayout.setHeight(HEADER_AREA_HEIGHT);
        eastLayout.setWidth("30%");
        eastLayout.setMargin(10);
        
        VLayout eastLayoutVlayout = new VLayout(5);
        eastLayoutVlayout.setAutoHeight();
        eastLayoutVlayout.setAutoWidth();
        eastLayoutVlayout.setDefaultLayoutAlign(Alignment.RIGHT);
        
        eastLayoutVlayout.addMember(helpLabel);
        eastLayoutVlayout.addMember(signedInUser);
       
        eastLayout.addMember(eastLayoutVlayout);
        
        this.addMember(centerLayout);      
        this.addMember(eastLayout);
    }
}