package fr.pfgen.cgh.client.utils;

import java.util.List;

import com.smartgwt.client.util.StringUtil;

import fr.pfgen.cgh.shared.records.FrameRecord;
import fr.pfgen.cgh.shared.sharedUtils.RecordList;

public class FrameRecordList extends RecordList<FrameRecord> {

	private static final String COLS[]=new String[]{
		"Ref name","Test name","DLRS","Quant 68","SD","green","red","nPval","nFNUout","nFPOPout","nBGNUout","nBGPOPout","nLow","nHigh","% incl" 
	};
	
	public FrameRecordList(FrameRecord...data){
		super(data);
	}
	
	public FrameRecordList(List<FrameRecord> data){
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
	public String getValueOf(FrameRecord frame, int col) {
		switch(col){
			case 0: return StringUtil.asHTML(frame.getRefName(),true);
			case 1: return StringUtil.asHTML(frame.getTestName(),true);
			case 2: return StringUtil.asHTML(frame.getQcResultMap().get("dlrs"),true);
			case 3: return StringUtil.asHTML(frame.getQcResultMap().get("quantile68"),true);
			case 4: return StringUtil.asHTML(frame.getQcResultMap().get("sd"),true);
			case 5: return StringUtil.asHTML(frame.getQcResultMap().get("green"),true);
			case 6: return StringUtil.asHTML(frame.getQcResultMap().get("red"),true);
			case 7: return StringUtil.asHTML(frame.getQcResultMap().get("nPval"),true);
			case 8: return StringUtil.asHTML(frame.getQcResultMap().get("nFNUout"),true);
			case 9: return StringUtil.asHTML(frame.getQcResultMap().get("nFPOPout"),true);
			case 10: return StringUtil.asHTML(frame.getQcResultMap().get("nBGNUout"),true);
			case 11: return StringUtil.asHTML(frame.getQcResultMap().get("nBGPOPout"),true);
			case 12: return StringUtil.asHTML(frame.getQcResultMap().get("nlow"),true);
			case 13: return StringUtil.asHTML(frame.getQcResultMap().get("nhigh"),true);
			case 14: return StringUtil.asHTML(frame.getQcResultMap().get("inclusion"),true);
			default: return "?????"+col;
		}
	}
}
