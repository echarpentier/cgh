package fr.pfgen.cgh.client;

import fr.pfgen.cgh.client.services.UserService;
import fr.pfgen.cgh.client.services.UserServiceAsync;
import fr.pfgen.cgh.client.ui.ApplicationMenu;
import fr.pfgen.cgh.client.ui.HeaderArea;
import fr.pfgen.cgh.client.ui.MainArea;
import fr.pfgen.cgh.client.ui.NavigationArea;
import fr.pfgen.cgh.client.ui.windows.LoginWindow;
import fr.pfgen.cgh.shared.records.UserRecord;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.smartgwt.client.types.DateDisplayFormat;
import com.smartgwt.client.util.DateUtil;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Cgh implements EntryPoint {
	private static Cgh singleton;
	private final UserServiceAsync userService = GWT.create(UserService.class);
	public UserRecord user;

    public static Cgh get() {
    	return singleton;
    }
    
    private static final int HEADER_HEIGHT = 85;
    
    private VLayout mainLayout;
    private HLayout northLayout;
    private HLayout southLayout;
    private VLayout eastLayout;
    private HLayout westLayout;
    
    public void onModuleLoad() {
    	singleton=this;
    	
    	startSession();
    }
    
    public VLayout getMainLayout(){
    	return mainLayout;
    }
    
	public void startSession(){
		//set date time objects for application
		DateUtil.setShortDatetimeDisplayFormat(DateDisplayFormat.TOEUROPEANSHORTDATETIME);
		DateUtil.setShortDateDisplayFormat(DateDisplayFormat.TOEUROPEANSHORTDATE);
		//check if user is already logged in and show login window if necessary
		userService.checkUserSessionOnServer(new AsyncCallback<UserRecord>() {
    		public void onSuccess(UserRecord user) {
				//UserRecord user = (UserRecord) result;
				if (user == null) {
					new LoginWindow();
		        } else {
		        	Cgh.get().setUser(user);
		        	Cgh.get().displayMainScreen();
		        }
			}
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}			
		});
    }
    
    public void setUser(UserRecord user){
    	this.user = user;
    }
    
    public UserRecord getUser(){
    	return user;
    }
    
    public void displayMainScreen(){
    	Window.enableScrolling(false);
        Window.setMargin("0px");
        
        // main layout occupies the whole area
        mainLayout = new VLayout();
        mainLayout.setWidth100();
        mainLayout.setHeight100();

        northLayout = new HLayout();
        northLayout.setHeight(HEADER_HEIGHT);

        VLayout vLayout = new VLayout();
        vLayout.addMember(new HeaderArea());
        vLayout.addMember(new ApplicationMenu());
        northLayout.addMember(vLayout);

        westLayout = new NavigationArea();
        westLayout.setWidth("12%");
        
        eastLayout = new MainArea();
        eastLayout.setWidth("88%");
        
        southLayout = new HLayout();
        southLayout.setMembers(westLayout, eastLayout);

        mainLayout.addMember(northLayout);
        mainLayout.addMember(southLayout);

        // add the main layout container to GWT's root panel
        RootLayoutPanel.get().clear();
        RootLayoutPanel.get().add(mainLayout);
    }
}
