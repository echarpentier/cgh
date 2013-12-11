package fr.pfgen.cgh.shared.sharedUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import fr.pfgen.cgh.shared.records.Record;

public abstract class RecordList<T extends Record> implements Iterable<T> {
	
	public enum Layout { VERTICAL, HORIZONTAL};
	
	private List<T> data=null;
	
	public RecordList(T...items){
		data=new ArrayList<T>(Arrays.asList(items));
	}
	
	public RecordList(List<T> data){
		data=new ArrayList<T>(data);
	}
	
	public int size(){
		return data.size();
	}
	
	public T get(int index){
		return data.get(index);
	}
	
	public String createHtmlTable(Layout layout, String title){
		switch(layout) {
			case VERTICAL: return createVerticalHtmlTable(title);
			case HORIZONTAL: return createHorizontalHtmlTable(title);
			default: throw new IllegalStateException();
		}
	}
	
	private String createVerticalHtmlTable(String title){
		StringBuilder table = new StringBuilder("<table class=\"EC_table\">");
		table.append("<caption><b><u>"+title+"</u></b></caption>");
		for(int i=0;i< getColumnCount();++i){
			table.append("<tr>");
			table.append("<th align=\"center\">");
			table.append(getColumnName(i));
			table.append("</th>");
			for(T o:this){	
				table.append("<td align=\"center\">");
				table.append(getValueOf(o,i));
				table.append("</td>");
			}
			table.append("</tr>");
		}
		
		table.append("</table>");
		return table.toString();
	}
	
	private String createHorizontalHtmlTable(String title){
		StringBuilder table = new StringBuilder("<table class=\"EC_table\">");
		table.append("<caption><b><u>"+title+"</u></b></caption>");
		table.append("<tr>");
		for(int i=0;i< getColumnCount();++i){	
			table.append("<th align=\"center\">");
			table.append(getColumnName(i));
			table.append("</th>");
		}
		table.append("</tr><tbody>");
		
		for(T o:this){
			table.append("<tr>");
			for(int i=0;i< getColumnCount();++i){	
				table.append("<td align=\"center\">");
				table.append(getValueOf(o,i));
				table.append("</td>");
			}
			table.append("</tr>");
		}
		table.append("</tbody></table>");
		return table.toString();
	}
	
	public int getRowCount(){
		return data.size();
	}
	
	public abstract int getColumnCount();
	
	public abstract String getColumnName(int col);
	
	public abstract String getValueOf(T object,int col);
	
	@Override
	public Iterator<T> iterator(){
		return data.iterator();
	}
}
