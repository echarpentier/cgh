package fr.pfgen.cgh.client.utils;

import java.util.List;

import com.smartgwt.client.util.DateUtil;
import com.smartgwt.client.util.StringUtil;

import fr.pfgen.cgh.shared.records.ArrayRecord;
import fr.pfgen.cgh.shared.sharedUtils.RecordList;

public class ArrayRecordList extends RecordList<ArrayRecord> {
	
	private static final String COLS[]=new String[]{
		"Frame number","Scan date","Genomic build","Design name","User"
	};
	
	public ArrayRecordList(ArrayRecord...data){
		super(data);
	}
	
	public ArrayRecordList(List<ArrayRecord> data){
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
	public String getValueOf(ArrayRecord array, int col) {
		switch(col){
		case 0: return StringUtil.asHTML(Integer.toString(array.getFrameNumber()),true);
		case 1: return StringUtil.asHTML(DateUtil.formatAsShortDate(array.getScanDate()),true);
		case 2: return StringUtil.asHTML(array.getGenomicBuild(),true);
		case 3: return StringUtil.asHTML(array.getDesignName(),true);
		case 4: return StringUtil.asHTML(array.getUserName(),true);
		default: return "?????"+col;
	}
	}
}
