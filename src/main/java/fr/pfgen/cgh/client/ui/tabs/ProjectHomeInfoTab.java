package fr.pfgen.cgh.client.ui.tabs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import fr.pfgen.cgh.client.datasources.ArrayDS;
import fr.pfgen.cgh.client.services.ArrayService;
import fr.pfgen.cgh.client.services.ArrayServiceAsync;
import fr.pfgen.cgh.client.ui.vstacks.GenericVstack;
import fr.pfgen.cgh.client.utils.ProjectRecordList;
import fr.pfgen.cgh.shared.records.ArrayRecord;
import fr.pfgen.cgh.shared.records.ProjectRecord;
import fr.pfgen.cgh.shared.sharedUtils.RecordList.Layout;

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
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.HTMLFlow;
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
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;
import com.smartgwt.client.widgets.tree.Tree;
import com.smartgwt.client.widgets.tree.TreeNode;

public class ProjectHomeInfoTab extends Tab {

	private VLayout tabPane;
	private ProjectRecord project;
	private final ArrayServiceAsync arrayService = GWT.create(ArrayService.class);
	private TabSet projectTopTabset;
	
	public ProjectHomeInfoTab(ProjectRecord project, TabSet projectTopTabset) {
		this.project = project;
		this.projectTopTabset = projectTopTabset;
		
		this.setPrompt("Project informations");
		this.setTitle("&nbsp;"+Canvas.imgHTML("icons/About.png",16,16));
		
		tabPane = new VLayout(40);
		tabPane.setDefaultLayoutAlign(Alignment.CENTER);
		tabPane.setWidth("80%");
		
		HTMLFlow projectName = new HTMLFlow();
		projectName.setStyleName("EC_TabTitle");
		projectName.setContents("Project: "+project.getProjectName());
		
		tabPane.addMember(projectName);
		
		HTMLFlow infoTable = new HTMLFlow();
		String table = new ProjectRecordList(project).createHtmlTable(Layout.HORIZONTAL, "");
		
		infoTable.setContents(table);
		infoTable.setAutoHeight();
		infoTable.setAutoWidth();
		
		tabPane.addMember(infoTable);
		
		tabPane.addMember(createSearchLayout());
		
		setPane(tabPane);
	}
	
	private HLayout createSearchLayout(){
		HLayout searchLayout = new HLayout(10);
		searchLayout.setAutoHeight();
		searchLayout.setAutoWidth();
		
		
		GenericVstack searchByNameStack = new GenericVstack();
		searchByNameStack.addHeaderLabel("Search by name");
		DynamicForm searchByNameForm = new DynamicForm();
		final ComboBoxItem cbItemByName = new ComboBoxItem("Array");  
        cbItemByName.setOptionDataSource(ArrayDS.getInstance());  
        Criteria crits = new Criteria();
        crits.addCriteria("project_id", Integer.toString(project.getProjectID()));
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
				createArrayTabInProject(event.getRecord().getAttributeAsInt("array_id"));
			}
		});
  
        cbItemByName.setPickListProperties(pickListProperties);
        
        searchByNameStack.addMember(searchByNameForm);
        searchLayout.addMember(searchByNameStack);
        
        final Img loadingGif1 = new Img("loadingStar.gif",40,40);
        loadingGif1.setLayoutAlign(Alignment.CENTER);
        
		final GenericVstack searchByDateStack = new GenericVstack();
		searchByDateStack.addHeaderLabel("Search by date");
		searchByDateStack.addMember(loadingGif1);
		
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
						createArrayTabInProject(Integer.parseInt(event.getValue().toString()));
					}
				});
				searchByDateForm.setFields(dateItem);
				searchByDateStack.removeMember(loadingGif1);
				searchByDateStack.addMember(searchByDateForm);
			}
		});
		
		searchLayout.addMember(searchByDateStack);
        
        return searchLayout;
	}
	
	private void createArrayTabInProject(final Integer arrayID){
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
				String tabID = "Array_"+arrayID+"_in_project_"+project.getProjectID();
				if (projectTopTabset.getTab(tabID)!=null){
					projectTopTabset.selectTab(tabID);
				}else{
					Tab newTab = new ProjectArrayTab(array, tabID);
					projectTopTabset.addTab(newTab);
					projectTopTabset.selectTab(newTab);
				}
			}
			
			@Override
			public void onFailure(Throwable caught) {
				SC.warn("Cannot get array id '"+arrayID+"' from database");
			}
		});
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
	
}
