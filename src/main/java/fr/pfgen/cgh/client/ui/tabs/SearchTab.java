package fr.pfgen.cgh.client.ui.tabs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import fr.pfgen.cgh.client.Cgh;
import fr.pfgen.cgh.client.datasources.ArrayDS;
import fr.pfgen.cgh.client.services.ArrayService;
import fr.pfgen.cgh.client.services.ArrayServiceAsync;
import fr.pfgen.cgh.client.ui.MainArea;
import fr.pfgen.cgh.client.ui.vstacks.GenericVstack;
import fr.pfgen.cgh.client.utils.ClientUtils;
import fr.pfgen.cgh.shared.records.ArrayRecord;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.util.DateUtil;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.FormItemHoverFormatter;
import com.smartgwt.client.widgets.form.fields.ComboBoxItem;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.IPickTreeItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.grid.HoverCustomizer;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellClickEvent;
import com.smartgwt.client.widgets.grid.events.CellClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tree.Tree;
import com.smartgwt.client.widgets.tree.TreeNode;

public class SearchTab{
	
	private final ArrayServiceAsync arrayService = GWT.create(ArrayService.class);
	
	public SearchTab(String tabID){
		VLayout vlayout = new VLayout(15);
		vlayout.setWidth("80%");
		vlayout.setDefaultLayoutAlign(Alignment.CENTER);
		
		HLayout stacksLayout = new HLayout(20);
		stacksLayout.setAutoHeight();
		stacksLayout.setAutoWidth();
		
		Criteria crits = new Criteria();
        switch (Cgh.get().getUser().getAppStatus()) {
			case ADVANCED:
				crits.addCriteria("team_id", Integer.toString(Cgh.get().getUser().getTeamID()));
				break;
			case SIMPLE:
				crits.addCriteria("user_id", Integer.toString(Cgh.get().getUser().getUserID()));
				break;
			case RESTRICTED:
				crits.addCriteria("user_id", Integer.toString(Cgh.get().getUser().getUserID()));
				vlayout.disable();
				break;
			default:
				break;
		}
		
		GenericVstack searchByNameStack = new GenericVstack();
		searchByNameStack.addHeaderLabel("Search by name");
		DynamicForm searchByNameForm = new DynamicForm();
		final ComboBoxItem cbItemByName = new ComboBoxItem("Array");  
        cbItemByName.setOptionDataSource(ArrayDS.getInstance());  
        cbItemByName.setOptionCriteria(crits);
        cbItemByName.setWidth(150);
        cbItemByName.setDisplayField("array_name");
        cbItemByName.setValueField("array_id");
        searchByNameForm.setFields(cbItemByName);
        
        ListGrid pickListProperties = new ListGrid();
        pickListProperties.setCanHover(true);  
        pickListProperties.setShowHover(true);
        pickListProperties.setEmptyCellValue("--");
        pickListProperties.setHoverWidth(300);
        pickListProperties.setHoverCustomizer(new HoverCustomizer() {  
            @Override  
            public String hoverHTML(Object value, ListGridRecord record, int rowNum, int colNum) {  
                String project = record.getAttribute("project_name");
                if (project==null){ project="--";}
                String user = record.getAttribute("user_name");
                int frames = record.getAttributeAsInt("frame_number");

                String date = DateUtil.formatAsShortDate(record.getAttributeAsDate("scan_date"));
                String build = record.getAttribute("genomic_build");
                String design = record.getAttribute("design_name");
                
                String hoverString = "<table>" +  
                        "<tr><td>project:</td><td>"+project+"</td></tr>" +  
                        "<tr><td>user:</td><td>"+user+"</td></tr>" +  
                        "<tr><td>number of frames:</td><td>"+frames+"</td></tr>" + 
                        "<tr><td>scan date:</td><td>"+date+"</td></tr>" + 
                        "<tr><td>genomic build:</td><td>"+build+"</td></tr>" + 
                        "<tr><td>design name:</td><td>"+design+"</td></tr>" + 
                        "</table>";
                
                return hoverString;
            }  
        });  
        pickListProperties.addCellClickHandler(new CellClickHandler() {
			
			@Override
			public void onCellClick(CellClickEvent event) {
				createArrayTab(event.getRecord().getAttributeAsInt("array_id"));
			}
		});
  
        cbItemByName.setPickListProperties(pickListProperties);
        
        searchByNameStack.addMember(searchByNameForm);   
        
        final Img loadingGif1 = new Img("loadingStar.gif",40,40);
        loadingGif1.setLayoutAlign(Alignment.CENTER);
        
        final Img loadingGif2 = new Img("loadingStar.gif",40,40);
        loadingGif2.setLayoutAlign(Alignment.CENTER);
        
		final GenericVstack searchByDateStack = new GenericVstack();
		searchByDateStack.addHeaderLabel("Search by date");
		searchByDateStack.addMember(loadingGif1);
		
		final GenericVstack searchByProjectStack = new GenericVstack();
		searchByProjectStack.addHeaderLabel("Search by project");
		searchByProjectStack.addMember(loadingGif2);
		
		ArrayDS ds = ArrayDS.getInstance();
		ds.fetchData(crits, new DSCallback() {
			
			@Override
			public void execute(DSResponse response, Object rawData, DSRequest request) {
				List<Record> recList = Arrays.asList(response.getData());
				Tree dateTree = createDateTree(recList);
				final DynamicForm searchByDateForm = new DynamicForm();
				final IPickTreeItem dateItem = new IPickTreeItem("Date");
				dateItem.setCanSelectParentItems(false);
				dateItem.setValueTree(dateTree);
				dateItem.setValueField("array_id");
				dateItem.setItemHoverFormatter(new FormItemHoverFormatter() {
					
					@Override
					public String getHoverHTML(FormItem item, DynamicForm form) {
						return item.getName();
					}
				});
				
				dateItem.addChangedHandler(new ChangedHandler() {
					
					@Override
					public void onChanged(ChangedEvent event) {
						createArrayTab(Integer.parseInt(event.getValue().toString()));
					}
				});
				searchByDateForm.setFields(dateItem);
				searchByDateStack.removeMember(loadingGif1);
				searchByDateStack.addMember(searchByDateForm);
				
				Tree projectTree = createProjectTree(recList);
				final DynamicForm searchByProjectForm = new DynamicForm();
				final IPickTreeItem projectItem = new IPickTreeItem("Project");
				projectItem.setCanSelectParentItems(false);
				projectItem.setValueTree(projectTree);
				projectItem.setValueField("array_id");
				projectItem.setItemHoverFormatter(new FormItemHoverFormatter() {
					
					@Override
					public String getHoverHTML(FormItem item, DynamicForm form) {
						return item.getName();
					}
				});
				
				projectItem.addChangedHandler(new ChangedHandler() {
					
					@Override
					public void onChanged(ChangedEvent event) {
						createArrayTab(Integer.parseInt(event.getValue().toString()));
					}
				});
				searchByProjectForm.setFields(projectItem);
				searchByProjectStack.removeMember(loadingGif2);
				searchByProjectStack.addMember(searchByProjectForm);
			}
		});
		
		stacksLayout.addMember(searchByNameStack);
		stacksLayout.addMember(searchByDateStack);
		stacksLayout.addMember(searchByProjectStack);
		
		vlayout.addMember(stacksLayout);
		
		MainArea.addTabToTopTabset("Search",tabID, vlayout, true);
	}
	
	private Tree createDateTree(List<Record> recList) {
		Map<String, Map<String, Map<String, List<Record>>>> map = new LinkedHashMap<String, Map<String,Map<String,List<Record>>>>();
		for (Record rec : recList) {
			String date = DateUtil.formatAsShortDatetime(rec.getAttributeAsDate("scan_date"));
			String year = date.split("/")[2].substring(0, 4);
			String month = date.split("/")[1];
			String day = date.split("/")[0];
			if (map.containsKey(year)){
				Map<String, Map<String, List<Record>>> monthMap = map.get(year);
				if (monthMap.containsKey(month)){
					Map<String, List<Record>> dayMap = monthMap.get(month);
					if (dayMap.containsKey(day)){
						dayMap.get(day).add(rec);
					}else{
						List<Record> newRecList = new ArrayList<Record>();
						newRecList.add(rec);
						dayMap.put(day, newRecList);
					}
				}else{
					Map<String, List<Record>> newDayMap = new LinkedHashMap<String, List<Record>>();
					List<Record> newRecList = new ArrayList<Record>();
					newRecList.add(rec);
					newDayMap.put(day, newRecList);
					monthMap.put(month, newDayMap);
				}
			}else{
				Map<String, Map<String, List<Record>>> newMonthMap = new LinkedHashMap<String, Map<String,List<Record>>>();
				Map<String, List<Record>> newDayMap = new LinkedHashMap<String, List<Record>>();
				List<Record> newRecList = new ArrayList<Record>();
				newRecList.add(rec);
				newDayMap.put(day, newRecList);
				newMonthMap.put(month, newDayMap);
				map.put(year, newMonthMap);
			}
		}
		Tree tree = new Tree();
		tree.setRoot(new TreeNode("root"));
		for (String y : map.keySet()) {
			TreeNode yNode = new TreeNode(y);
			tree.add(yNode, "root");
			for (String m : map.get(y).keySet()){
				TreeNode mNode = new TreeNode(m);
				tree.add(mNode, yNode);
				for (String d : map.get(y).get(m).keySet()) {
					TreeNode dNode = new TreeNode(d);
					tree.add(dNode, mNode);
					for (Record r : map.get(y).get(m).get(d)) {
						TreeNode rNode = new TreeNode(r.getAttribute("array_name"));
						rNode.setAttribute("array_name", r.getAttribute("array_name"));
						rNode.setAttribute("array_id", r.getAttribute("array_id"));
						tree.add(rNode, dNode);
					}
				}
			}
		}
		
		return tree;
	}
	
	private Tree createProjectTree(List<Record> recList) {
		Map<String, List<Record>> map = new LinkedHashMap<String, List<Record>>();
		for (Record rec : recList) {
			String project = rec.getAttribute("project_name");
			if (project==null || project.isEmpty() || project.equals("null")){ 
				project = "Others";
			}
			if (map.containsKey(project)){
				map.get(project).add(rec);
			}else{
				List<Record> newRecList = new ArrayList<Record>();
				newRecList.add(rec);
				map.put(project, newRecList);
			}
		}
		
		Tree tree = new Tree();
		tree.setRoot(new TreeNode("root"));
		for (String p : map.keySet()) {
			TreeNode pNode = new TreeNode(p);
			tree.add(pNode, "root");
			for (Record r : map.get(p)) {
				TreeNode rNode = new TreeNode(r.getAttribute("array_name"));
				rNode.setAttribute("array_name", r.getAttribute("array_name"));
				rNode.setAttribute("array_id", r.getAttribute("array_id"));
				tree.add(rNode, pNode);
			}
		}
		return tree;
	}
	
	private void createArrayTab(final Integer arrayID) {
		Map<String, String> crits = new HashMap<String, String>();
		crits.put("array_id", arrayID.toString());
		arrayService.fetch(null, null, null, crits, new AsyncCallback<List<ArrayRecord>>() {
			
			@Override
			public void onSuccess(List<ArrayRecord> result) {
				if (result==null || result.isEmpty()){
					SC.warn("Cannot get array informations from server");
					return;
				}
				ArrayRecord array = result.get(0);
				String tabID = "Array_"+arrayID;
				if (ClientUtils.tabExists(tabID)){
					MainArea.getTopTabSet().selectTab(tabID);
				}else{
					new ArrayTab(array);
				}
			}
			
			@Override
			public void onFailure(Throwable caught) {
				SC.warn("Cannot get array id '"+arrayID+"' from database");
			}
		});
	}
}
