package fr.pfgen.cgh.client.ui.tabs;

import java.util.List;
import java.util.Map;

import fr.pfgen.cgh.client.Cgh;
import fr.pfgen.cgh.client.services.ProjectService;
import fr.pfgen.cgh.client.services.ProjectServiceAsync;
import fr.pfgen.cgh.shared.records.ProjectRecord;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.util.StringUtil;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.HTMLFlow;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.events.TabSelectedEvent;
import com.smartgwt.client.widgets.tab.events.TabSelectedHandler;

public class ProjectHomeParamTab extends Tab {

	private VLayout tabPane;
	private boolean firstSeen = true;
	private final ProjectServiceAsync projectService = GWT.create(ProjectService.class);
	
	public ProjectHomeParamTab(final ProjectRecord project){
		
		this.setPrompt("Project params");
		this.setTitle("&nbsp;"+Canvas.imgHTML("workflows/chart.png",16,16));
		
		tabPane = new VLayout(40);
		tabPane.setDefaultLayoutAlign(Alignment.CENTER);
		tabPane.setWidth("80%");
		
		this.addTabSelectedHandler(new TabSelectedHandler() {
			
			@Override
			public void onTabSelected(TabSelectedEvent event) {
				if (firstSeen){
					tabPane.addMember(createProjectParamsLayout(project));
				}
			}
		});
		
		this.setPane(tabPane);
	}
	
	private VLayout createProjectParamsLayout(ProjectRecord project){
		final VLayout layout = new VLayout(10);
		layout.setAutoHeight();
		layout.setAutoWidth();
		
		projectService.getProjectParams(project.getProjectID(), new AsyncCallback<List<Map<String, String>>>() {

			@Override
			public void onFailure(Throwable caught) {
				SC.warn("Failed to fetch scan parameters for project");
			}

			@Override
			public void onSuccess(List<Map<String, String>> result) {
				if (result==null){
					SC.warn("Failed to fetch scan parameters for project");
					return;
				}else if (result.get(1).isEmpty()){
					SC.say("This project doesn't contain any array at this time");
				}else{
					layout.addMember(constructHtmlTablesForQcParam(result));
					firstSeen = false;
				}
			}
		});
		
		return layout;
	}

	private VLayout constructHtmlTablesForQcParam(List<Map<String, String>> result) {
		VLayout layout = new VLayout(35);
		layout.setDefaultLayoutAlign(Alignment.CENTER);
		layout.setAutoHeight();
		layout.setAutoWidth();
		layout.setLayoutTopMargin(20);
		
		HTMLFlow infoTable = new HTMLFlow();
		StringBuilder table = new StringBuilder("<table class=\"EC_table\">");
		//table.append("<caption><b><u>"+title+"</u></b></caption>");
		table.append("<tr>");
		table.append("<th align=\"center\">");
		table.append(StringUtil.asHTML("Array number", true));
		table.append("</th>");
		table.append("<th align=\"center\">");
		table.append(StringUtil.asHTML("Genomic build", true));
		table.append("</th>");
		table.append("<th align=\"center\">");
		table.append(StringUtil.asHTML("Design Name", true));
		table.append("</th>");
		
		table.append("</tr><tbody>");
		
		table.append("<tr>");	
		table.append("<td align=\"center\"> ");
		table.append(StringUtil.asHTML(result.get(0).get("array_number"), true));
		table.append("</td>");
		table.append("<td align=\"center\">");
		table.append(StringUtil.asHTML(result.get(0).get("genomic_build"), true));
		table.append("</td>");
		table.append("<td align=\"center\">");
		table.append(StringUtil.asHTML(result.get(0).get("design_name"), true));
		table.append("</td>");
		table.append("</tr>");
		table.append("</tbody></table>");
		
		infoTable.setContents(table.toString());
		infoTable.setAutoHeight();
		infoTable.setAutoWidth();
		
		layout.addMember(infoTable);
		
		HTMLFlow optionsTable = new HTMLFlow();
		StringBuilder oHTMLTable = new StringBuilder("<table class=\"EC_table\">");
		oHTMLTable.append("<caption><b><u>"+"Options"+"</u></b></caption>");
		oHTMLTable.append("<tr>");
			oHTMLTable.append("<th align=\"center\">");
				oHTMLTable.append(StringUtil.asHTML("Mask", true));
			oHTMLTable.append("</th>");
			oHTMLTable.append("<td align=\"center\"> ");
				oHTMLTable.append(StringUtil.asHTML(result.get(0).get("mask"), true));
			oHTMLTable.append("</td>");
			oHTMLTable.append("<td align=\"center\"> <i>");
				oHTMLTable.append(StringUtil.asHTML(result.get(1).get("mask"), true));
			oHTMLTable.append("</i></td>");
		oHTMLTable.append("</tr>");
		
		oHTMLTable.append("<tr>");
			oHTMLTable.append("<th align=\"center\">");
				oHTMLTable.append(StringUtil.asHTML("GC Lowess", true));
			oHTMLTable.append("</th>");
			oHTMLTable.append("<td align=\"center\"> ");
				oHTMLTable.append(StringUtil.asHTML(result.get(0).get("gc_lowess"), true));
			oHTMLTable.append("</td>");
			oHTMLTable.append("<td align=\"center\"> <i>");
				oHTMLTable.append(StringUtil.asHTML(result.get(1).get("gc_lowess"), true));
			oHTMLTable.append("</i></td>");
		oHTMLTable.append("</tr>");	
			
		oHTMLTable.append("<tr>");
			oHTMLTable.append("<th align=\"center\">");
				oHTMLTable.append(StringUtil.asHTML("Deletion Color", true));
			oHTMLTable.append("</th>");
			oHTMLTable.append("<td align=\"center\"> ");
				oHTMLTable.append(StringUtil.asHTML(result.get(0).get("col_del"), true));
			oHTMLTable.append("</td>");
			oHTMLTable.append("<td align=\"center\"> <i>");
				oHTMLTable.append(StringUtil.asHTML(result.get(1).get("col_del"), true));
			oHTMLTable.append("</i></td>");
		oHTMLTable.append("</tr>");
		
		oHTMLTable.append("<tr>");
			oHTMLTable.append("<th align=\"center\">");
				oHTMLTable.append(StringUtil.asHTML("Duplication Color", true));
			oHTMLTable.append("</th>");
			oHTMLTable.append("<td align=\"center\"> ");
				oHTMLTable.append(StringUtil.asHTML(result.get(0).get("col_dup"), true));
			oHTMLTable.append("</td>");
			oHTMLTable.append("<td align=\"center\"> <i>");
				oHTMLTable.append(StringUtil.asHTML(result.get(1).get("col_dup"), true));
			oHTMLTable.append("</i></td>");
		oHTMLTable.append("</tr>");
		
		oHTMLTable.append("</table>");
		optionsTable.setContents(oHTMLTable.toString());
		optionsTable.setAutoHeight();
		optionsTable.setAutoWidth();
		
		layout.addMember(optionsTable);
		
		boolean showAnalysisParams;
		switch (Cgh.get().getUser().getAppStatus()) {
			case ADVANCED:
				showAnalysisParams = true;
				break;
			case SIMPLE:
				showAnalysisParams = false;
				break;
			case ADMIN:
				showAnalysisParams = true;
				break;
			case RESTRICTED:
				showAnalysisParams = false;
				break;
			default:
				showAnalysisParams = false;
				break;
		}
		
		if (showAnalysisParams){
			HLayout paramsLayout = new HLayout(20);
			
			HTMLFlow anaParamShort = new HTMLFlow();
			StringBuilder shortHTMLTable = new StringBuilder("<table class=\"EC_table\">");
			shortHTMLTable.append("<caption><b><u>"+"Params for short calls"+"</u></b></caption>");
			shortHTMLTable.append("<tr>");
				shortHTMLTable.append("<th align=\"center\">");
					shortHTMLTable.append(StringUtil.asHTML("s_min_positive_probes", true));
				shortHTMLTable.append("</th>");
				shortHTMLTable.append("<td align=\"center\"> ");
					shortHTMLTable.append(StringUtil.asHTML(result.get(0).get("s_min_positive_probes"), true));
				shortHTMLTable.append("</td>");
				shortHTMLTable.append("<td align=\"center\"> <i>");
					shortHTMLTable.append(StringUtil.asHTML(result.get(1).get("s_min_positive_probes"), true));
				shortHTMLTable.append("</i></td>");
			shortHTMLTable.append("</tr>");
			
			shortHTMLTable.append("<tr>");
				shortHTMLTable.append("<th align=\"center\">");
					shortHTMLTable.append(StringUtil.asHTML("s_min_total_score", true));
				shortHTMLTable.append("</th>");
				shortHTMLTable.append("<td align=\"center\"> ");
					shortHTMLTable.append(StringUtil.asHTML(result.get(0).get("s_min_total_score"), true));
				shortHTMLTable.append("</td>");
				shortHTMLTable.append("<td align=\"center\"> <i>");
					shortHTMLTable.append(StringUtil.asHTML(result.get(1).get("s_min_total_score"), true));
				shortHTMLTable.append("</i></td>");
			shortHTMLTable.append("</tr>");	
				
			shortHTMLTable.append("<tr>");
				shortHTMLTable.append("<th align=\"center\">");
					shortHTMLTable.append(StringUtil.asHTML("s_probes_times_median", true));
				shortHTMLTable.append("</th>");
				shortHTMLTable.append("<td align=\"center\"> ");
					shortHTMLTable.append(StringUtil.asHTML(result.get(0).get("s_probes_times_median"), true));
				shortHTMLTable.append("</td>");
				shortHTMLTable.append("<td align=\"center\"> <i>");
					shortHTMLTable.append(StringUtil.asHTML(result.get(1).get("s_probes_times_median"), true));
				shortHTMLTable.append("</i></td>");
			shortHTMLTable.append("</tr>");
			
			shortHTMLTable.append("<tr>");
				shortHTMLTable.append("<th align=\"center\">");
					shortHTMLTable.append(StringUtil.asHTML("s_ratio_probes_over_positive", true));
				shortHTMLTable.append("</th>");
				shortHTMLTable.append("<td align=\"center\"> ");
					shortHTMLTable.append(StringUtil.asHTML(result.get(0).get("s_ratio_probes_over_positive"), true));
				shortHTMLTable.append("</td>");
				shortHTMLTable.append("<td align=\"center\"> <i>");
					shortHTMLTable.append(StringUtil.asHTML(result.get(1).get("s_ratio_probes_over_positive"), true));
				shortHTMLTable.append("</i></td>");
			shortHTMLTable.append("</tr>");
			
			shortHTMLTable.append("<tr>");
				shortHTMLTable.append("<th align=\"center\">");
					shortHTMLTable.append(StringUtil.asHTML("s_ratio_score_over_probes", true));
				shortHTMLTable.append("</th>");
				shortHTMLTable.append("<td align=\"center\"> ");
					shortHTMLTable.append(StringUtil.asHTML(result.get(0).get("s_ratio_score_over_probes"), true));
				shortHTMLTable.append("</td>");
				shortHTMLTable.append("<td align=\"center\"> <i>");
					shortHTMLTable.append(StringUtil.asHTML(result.get(1).get("s_ratio_score_over_probes"), true));
				shortHTMLTable.append("</i></td>");
			shortHTMLTable.append("</tr>");
			
			shortHTMLTable.append("</table>");
			anaParamShort.setContents(shortHTMLTable.toString());
			anaParamShort.setAutoHeight();
			anaParamShort.setAutoWidth();
			
			paramsLayout.addMember(anaParamShort);
			
			HTMLFlow anaParamLong = new HTMLFlow();
			StringBuilder longHTMLTable = new StringBuilder("<table class=\"EC_table\">");
			longHTMLTable.append("<caption><b><u>"+"Params for long calls"+"</u></b></caption>");
			longHTMLTable.append("<tr>");
				longHTMLTable.append("<th align=\"center\">");
					longHTMLTable.append(StringUtil.asHTML("l_min_positive_probes", true));
				longHTMLTable.append("</th>");
				longHTMLTable.append("<td align=\"center\"> ");
					longHTMLTable.append(StringUtil.asHTML(result.get(0).get("l_min_positive_probes"), true));
				longHTMLTable.append("</td>");
				longHTMLTable.append("<td align=\"center\"> <i>");
					longHTMLTable.append(StringUtil.asHTML(result.get(1).get("l_min_positive_probes"), true));
				longHTMLTable.append("</i></td>");
			longHTMLTable.append("</tr>");
			
			longHTMLTable.append("<tr>");
				longHTMLTable.append("<th align=\"center\">");
					longHTMLTable.append(StringUtil.asHTML("l_min_total_score", true));
				longHTMLTable.append("</th>");
				longHTMLTable.append("<td align=\"center\"> ");
					longHTMLTable.append(StringUtil.asHTML(result.get(0).get("l_min_total_score"), true));
				longHTMLTable.append("</td>");
				longHTMLTable.append("<td align=\"center\"> <i>");
					longHTMLTable.append(StringUtil.asHTML(result.get(1).get("l_min_total_score"), true));
				longHTMLTable.append("</i></td>");
			longHTMLTable.append("</tr>");	
				
			longHTMLTable.append("<tr>");
				longHTMLTable.append("<th align=\"center\">");
					longHTMLTable.append(StringUtil.asHTML("l_probes_times_median", true));
				longHTMLTable.append("</th>");
				longHTMLTable.append("<td align=\"center\"> ");
					longHTMLTable.append(StringUtil.asHTML(result.get(0).get("l_probes_times_median"), true));
				longHTMLTable.append("</td>");
				longHTMLTable.append("<td align=\"center\"> <i>");
					longHTMLTable.append(StringUtil.asHTML(result.get(1).get("l_probes_times_median"), true));
				longHTMLTable.append("</i></td>");
			longHTMLTable.append("</tr>");
			
			longHTMLTable.append("<tr>");
				longHTMLTable.append("<th align=\"center\">");
					longHTMLTable.append(StringUtil.asHTML("l_ratio_probes_over_positive", true));
				longHTMLTable.append("</th>");
				longHTMLTable.append("<td align=\"center\"> ");
					longHTMLTable.append(StringUtil.asHTML(result.get(0).get("l_ratio_probes_over_positive"), true));
				longHTMLTable.append("</td>");
				longHTMLTable.append("<td align=\"center\"> <i>");
					longHTMLTable.append(StringUtil.asHTML(result.get(1).get("l_ratio_probes_over_positive"), true));
				longHTMLTable.append("</i></td>");
			longHTMLTable.append("</tr>");
			
			longHTMLTable.append("<tr>");
				longHTMLTable.append("<th align=\"center\">");
					longHTMLTable.append(StringUtil.asHTML("l_ratio_score_over_probes", true));
				longHTMLTable.append("</th>");
				longHTMLTable.append("<td align=\"center\"> ");
					longHTMLTable.append(StringUtil.asHTML(result.get(0).get("l_ratio_score_over_probes"), true));
				longHTMLTable.append("</td>");
				longHTMLTable.append("<td align=\"center\"> <i>");
					longHTMLTable.append(StringUtil.asHTML(result.get(1).get("l_ratio_score_over_probes"), true));
				longHTMLTable.append("</i></td>");
			longHTMLTable.append("</tr>");
			
			longHTMLTable.append("<tr>");
				longHTMLTable.append("<th align=\"center\">");
					longHTMLTable.append(StringUtil.asHTML("l_min_median", true));
				longHTMLTable.append("</th>");
				longHTMLTable.append("<td align=\"center\"> ");
					longHTMLTable.append(StringUtil.asHTML(result.get(0).get("l_min_median"), true));
				longHTMLTable.append("</td>");
				longHTMLTable.append("<td align=\"center\"> <i>");
					longHTMLTable.append(StringUtil.asHTML(result.get(1).get("l_min_median"), true));
				longHTMLTable.append("</i></td>");
			longHTMLTable.append("</tr>");
			
			longHTMLTable.append("</table>");
			anaParamLong.setContents(longHTMLTable.toString());
			anaParamLong.setAutoHeight();
			anaParamLong.setAutoWidth();
			
			paramsLayout.addMember(anaParamLong);
		
			layout.addMember(paramsLayout);
		}
		
		return layout;
	}
}
