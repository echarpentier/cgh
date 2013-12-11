package fr.pfgen.cgh.client.utils;

import java.util.List;

import com.smartgwt.client.util.StringUtil;

import fr.pfgen.cgh.shared.records.ProjectRecord;
import fr.pfgen.cgh.shared.sharedUtils.RecordList;

public class ProjectRecordList extends RecordList<ProjectRecord> {

	private static final String COLS[]=new String[]{
		"Project manager","Team","Created by"
	};
	
	public ProjectRecordList(ProjectRecord...data){
		super(data);
	}
	
	public ProjectRecordList(List<ProjectRecord> data){
		super(data);
	}

	@Override
	public int getColumnCount() {
		return COLS.length;
	}

	@Override
	public String getColumnName(int col) {
		return StringUtil.asHTML(COLS[col],true);
	}

	@Override
	public String getValueOf(ProjectRecord project, int col) {
		switch(col){
		case 0: return StringUtil.asHTML(project.getProjectManager(),true);
		case 1: return StringUtil.asHTML(project.getTeamName(),true);
		case 2: return StringUtil.asHTML(project.getUserName(),true);
		default: return "?????"+col;
	}
	}
}
