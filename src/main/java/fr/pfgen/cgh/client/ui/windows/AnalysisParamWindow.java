package fr.pfgen.cgh.client.ui.windows;

import fr.pfgen.cgh.client.Cgh;
import fr.pfgen.cgh.client.services.AnalysisService;
import fr.pfgen.cgh.client.services.AnalysisServiceAsync;
import fr.pfgen.cgh.shared.enums.DelDupColors;
import fr.pfgen.cgh.shared.enums.YesNo;
import fr.pfgen.cgh.shared.records.AnalysisParamsRecord;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Dialog;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.events.CloseClientEvent;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.SpinnerItem;
import com.smartgwt.client.widgets.layout.HLayout;

public abstract class AnalysisParamWindow extends Dialog {

	private final DynamicForm optionForm = new DynamicForm();
	private final HLayout paramLayout = new HLayout(20);
	private final DynamicForm shortCallParamForm = new DynamicForm();
	private final DynamicForm longCallParamForm = new DynamicForm();
	private IButton saveButton;
	private final AnalysisServiceAsync analysisService = GWT.create(AnalysisService.class);
	private AnalysisParamsRecord analysisParams;
	
	public AnalysisParamWindow(){
		this.setTitle("Analysis parameters");
		this.setAutoSize(true);
		this.setAutoCenter(true);
		this.setIsModal(true);
		this.setShowModalMask(true);
		this.setShowCloseButton(true);
		
		this.addCloseClickHandler(new CloseClickHandler() {
			
			@Override
			public void onCloseClick(CloseClientEvent event) {
				AnalysisParamWindow.this.destroy();
			}
		});

		saveButton = new IButton("Save");
		saveButton.setAutoFit(true);
		saveButton.setShowDisabledIcon(false);
		saveButton.setIcon("icons/Save.png");
		
		final IButton defaultButton = new IButton("Restore default");
		defaultButton.setAutoFit(true);
		defaultButton.setShowDisabledIcon(false);
		defaultButton.setIcon("icons/Undo.png");
		
		defaultButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
					optionForm.reset();
					shortCallParamForm.reset();
					longCallParamForm.reset();
			}
		});
		
		final IButton cancelButton = new IButton("Cancel");
		cancelButton.setAutoFit(true);
		cancelButton.setShowDisabledIcon(false);
		cancelButton.setIcon("icons/Erase.png");
		
		cancelButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				AnalysisParamWindow.this.destroy();
			}
		});
		
		analysisService.getDefaultAnalysisParams(new AsyncCallback<AnalysisParamsRecord>() {

			@Override
			public void onFailure(Throwable caught) {
				SC.warn("Cannot get default values from server for analysis parameters");
			}

			@Override
			public void onSuccess(AnalysisParamsRecord result) {
				if (result==null){
					SC.warn("Cannot get default values from server for analysis parameters");
				}else{
					AnalysisParamWindow.this.analysisParams = result;
					createOptionForm();
					createParamLayout();
					AnalysisParamWindow.this.addItem(optionForm);
					AnalysisParamWindow.this.addItem(paramLayout);
					setToolbarButtons(saveButton,defaultButton,cancelButton);
					show();
				}
			}
		});
	}
	
	private CheckboxItem maskItem;
	private CheckboxItem gcLowessItem;
	private SelectItem delColItem;
	private SelectItem dupColItem;
	
	private void createOptionForm(){
		optionForm.setBrowserSpellCheck(false);
		
		optionForm.setValidateOnChange(true);
		optionForm.setAutoFocus(true);
		
		optionForm.setAutoHeight(); 
		optionForm.setAutoWidth();
		optionForm.setPadding(10);
		optionForm.setLayoutAlign(Alignment.CENTER);
		optionForm.setIsGroup(true);
		optionForm.setBorder("1px solid #C0C0C0");
		optionForm.setGroupTitle("Options");
		
		maskItem = new CheckboxItem("mask", "Masking");
		maskItem.setPrompt("If checked, masking of probes in CNV rich regions will be applied.");
		maskItem.setDefaultValue(YesNo.getBoolean(analysisParams.getMask()));
		maskItem.setLabelAsTitle(true);
		
		gcLowessItem = new CheckboxItem("gclowess", "GC&nbsp;lowess");
		gcLowessItem.setPrompt("If checked, normalization by GC content of probes and 250pb upstream and downstream will be applied to log-ratios");
		gcLowessItem.setDefaultValue(YesNo.getBoolean(analysisParams.getGcLowess()));
		gcLowessItem.setLabelAsTitle(true);
		
		String[] colorsArray = DelDupColors.enumAsStringArray();
		
		delColItem = new SelectItem("delcol", "Deletion&nbsp;color");
		delColItem.setPrompt("Color applied to deletions in gff file");
		delColItem.setValueMap(colorsArray);
		delColItem.setDefaultValue(analysisParams.getDelColor().toString());
		
		dupColItem = new SelectItem("dupcol", "Duplication&nbsp;color");
		dupColItem.setPrompt("Color applied to duplications in gff file");
		dupColItem.setValueMap(colorsArray);
		dupColItem.setDefaultValue(analysisParams.getDupColor().toString());
		
		optionForm.setFields(maskItem,gcLowessItem,delColItem,dupColItem);
		optionForm.rememberValues();
	}
	
	private SpinnerItem s_min_positive_probe_item;
	private SpinnerItem s_min_total_score_item;
	private SpinnerItem s_probes_times_median_item;
	private SpinnerItem s_ratio_probes_over_positive_item;
	private SpinnerItem s_ratio_score_over_probes_item;
	
	private SpinnerItem l_min_positive_probe_item;
	private SpinnerItem l_min_total_score_item;
	private SpinnerItem l_probes_times_median_item;
	private SpinnerItem l_ratio_probes_over_positive_item;
	private SpinnerItem l_ratio_score_over_probes_item;
	private SpinnerItem l_min_median_item;
	
	private void createParamLayout(){
		paramLayout.setAutoHeight();
		paramLayout.setAutoWidth();
		paramLayout.setTop(20);
		
		shortCallParamForm.setBrowserSpellCheck(false);
		shortCallParamForm.setValidateOnChange(true);
		shortCallParamForm.setAutoHeight(); 
		shortCallParamForm.setAutoWidth();
		shortCallParamForm.setPadding(10);
		shortCallParamForm.setLayoutAlign(Alignment.CENTER);
		shortCallParamForm.setIsGroup(true);
		shortCallParamForm.setBorder("1px solid #C0C0C0");
		shortCallParamForm.setGroupTitle("Parameters for short calls");
		
		s_min_positive_probe_item = new SpinnerItem("s_min_positive_probe", "s_min_positive_probe");
		s_min_positive_probe_item.setMax(20);
		s_min_positive_probe_item.setMin(3);
		s_min_positive_probe_item.setStep(1);
		s_min_positive_probe_item.setDefaultValue(analysisParams.getS_min_positive_probes());
		s_min_positive_probe_item.setPrompt("Minimum number of positive probes for a CNV in short calls");
		
		s_min_total_score_item = new SpinnerItem("s_min_total_score","s_min_total_score");
		s_min_total_score_item.setMax(20.0);
		s_min_total_score_item.setMin(5.0);
		s_min_total_score_item.setStep(0.1);
		s_min_total_score_item.setDefaultValue(analysisParams.getS_min_total_score());
		s_min_total_score_item.setPrompt("Minimum total score for a CNV in short calls");
		
		s_probes_times_median_item = new SpinnerItem("s_probes_times_median", "s_probes_times_median");
		s_probes_times_median_item.setMax(15.0);
		s_probes_times_median_item.setMin(2.0);
		s_probes_times_median_item.setStep(0.1);
		s_probes_times_median_item.setDefaultValue(analysisParams.getS_probes_times_median());
		s_probes_times_median_item.setPrompt("number of probes * median in short calls");
		
		s_ratio_probes_over_positive_item = new SpinnerItem("s_ratio_probes_over_positive", "s_ratio_probes_over_positive");
		s_ratio_probes_over_positive_item.setMax(3.0);
		s_ratio_probes_over_positive_item.setMin(1.0);
		s_ratio_probes_over_positive_item.setStep(0.1);
		s_ratio_probes_over_positive_item.setDefaultValue(analysisParams.getS_ratio_probes_over_positive());
		s_ratio_probes_over_positive_item.setPrompt("number of total probes / number of positive probes in short calls");
		
		s_ratio_score_over_probes_item = new SpinnerItem("s_ratio_score_over_probes", "s_ratio_score_over_probes");
		s_ratio_score_over_probes_item.setMax(5.0);
		s_ratio_score_over_probes_item.setMin(0.5);
		s_ratio_score_over_probes_item.setStep(0.1);
		s_ratio_score_over_probes_item.setDefaultValue(analysisParams.getS_ratio_score_over_probes());
		s_ratio_score_over_probes_item.setPrompt("total score / number of probes in short calls");
		
		shortCallParamForm.setItems(s_min_positive_probe_item,s_min_total_score_item,s_probes_times_median_item,s_ratio_probes_over_positive_item,s_ratio_score_over_probes_item);
		shortCallParamForm.rememberValues();
		
		longCallParamForm.setBrowserSpellCheck(false);
		longCallParamForm.setValidateOnChange(true);
		longCallParamForm.setAutoHeight(); 
		longCallParamForm.setAutoWidth();
		longCallParamForm.setPadding(10);
		longCallParamForm.setLayoutAlign(Alignment.CENTER);
		longCallParamForm.setIsGroup(true);
		longCallParamForm.setBorder("1px solid #C0C0C0");
		longCallParamForm.setGroupTitle("Parameters for long calls");

		l_min_positive_probe_item = new SpinnerItem("l_min_positive_probe", "l_min_positive_probe");
		l_min_positive_probe_item.setMax(20);
		l_min_positive_probe_item.setMin(3);
		l_min_positive_probe_item.setStep(1);
		l_min_positive_probe_item.setDefaultValue(analysisParams.getL_min_positive_probes());
		l_min_positive_probe_item.setPrompt("Minimum number of positive probes for a CNV in long calls");
		
		l_min_total_score_item = new SpinnerItem("l_min_total_score","l_min_total_score");
		l_min_total_score_item.setMax(20.0);
		l_min_total_score_item.setMin(5.0);
		l_min_total_score_item.setStep(0.1);
		l_min_total_score_item.setDefaultValue(analysisParams.getL_min_total_score());
		l_min_total_score_item.setPrompt("Minimum total score for a CNV in long calls");
		
		l_probes_times_median_item = new SpinnerItem("l_probes_times_median", "l_probes_times_median");
		l_probes_times_median_item.setMax(15.0);
		l_probes_times_median_item.setMin(2.0);
		l_probes_times_median_item.setStep(0.1);
		l_probes_times_median_item.setDefaultValue(analysisParams.getL_probes_times_median());
		l_probes_times_median_item.setPrompt("number of probes * median in long calls");
		
		l_ratio_probes_over_positive_item = new SpinnerItem("l_ratio_probes_over_positive", "l_ratio_probes_over_positive");
		l_ratio_probes_over_positive_item.setMax(3.0);
		l_ratio_probes_over_positive_item.setMin(1.0);
		l_ratio_probes_over_positive_item.setStep(0.1);
		l_ratio_probes_over_positive_item.setDefaultValue(analysisParams.getL_ratio_probes_over_positive());
		l_ratio_probes_over_positive_item.setPrompt("number of total probes / number of positive probes in long calls");
		
		l_ratio_score_over_probes_item = new SpinnerItem("l_ratio_score_over_probes", "l_ratio_score_over_probes");
		l_ratio_score_over_probes_item.setMax(5.0);
		l_ratio_score_over_probes_item.setMin(0.5);
		l_ratio_score_over_probes_item.setStep(0.1);
		l_ratio_score_over_probes_item.setDefaultValue(analysisParams.getL_ratio_score_over_probes());
		l_ratio_score_over_probes_item.setPrompt("total score / number of probes in long calls");
		
		l_min_median_item = new SpinnerItem("l_min_median", "l_min_median");
		l_min_median_item.setMax(2.0);
		l_min_median_item.setMin(0.2);
		l_min_median_item.setStep(0.1);
		l_min_median_item.setDefaultValue(analysisParams.getL_min_median());
		l_min_median_item.setPrompt("Minimum median value in long calls");
		
		longCallParamForm.setItems(l_min_positive_probe_item,l_min_total_score_item,l_probes_times_median_item,l_ratio_probes_over_positive_item,l_ratio_score_over_probes_item,l_min_median_item);
		longCallParamForm.rememberValues();
		
		paramLayout.addMember(shortCallParamForm);
		paramLayout.addMember(longCallParamForm);
		
		boolean authorized = true;
        switch (Cgh.get().getUser().getAppStatus()) {
			case SIMPLE:
				authorized = false;
				break;
			case RESTRICTED:
				authorized = false;
				break;
			default:
				break;
		}
        
        if (!authorized){
        	paramLayout.disable();
        }
	}

	public IButton getSaveButton() {
		return saveButton;
	}
	
	public AnalysisParamsRecord getValuesAsAnalysisParamsRecord(){
		AnalysisParamsRecord rec = new AnalysisParamsRecord();
		
		rec.setMask(YesNo.setStatus(maskItem.getValueAsBoolean()));
		rec.setGcLowess(YesNo.setStatus(gcLowessItem.getValueAsBoolean()));
		rec.setDelColor(DelDupColors.parse(delColItem.getValueAsString()));
		rec.setDupColor(DelDupColors.parse(dupColItem.getValueAsString()));
		
		rec.setS_min_positive_probes(Integer.parseInt(s_min_positive_probe_item.getValueAsString()));
		rec.setS_min_total_score(Double.parseDouble(s_min_total_score_item.getValueAsString()));
		rec.setS_probes_times_median(Double.parseDouble(s_probes_times_median_item.getValueAsString()));
		rec.setS_ratio_probes_over_positive(Double.parseDouble(s_ratio_probes_over_positive_item.getValueAsString()));
		rec.setS_ratio_score_over_probes(Double.parseDouble(s_ratio_score_over_probes_item.getValueAsString()));
		
		rec.setL_min_positive_probes(Integer.parseInt(l_min_positive_probe_item.getValueAsString()));
		rec.setL_min_total_score(Double.parseDouble(l_min_total_score_item.getValueAsString()));
		rec.setL_probes_times_median(Double.parseDouble(l_probes_times_median_item.getValueAsString()));
		rec.setL_ratio_probes_over_positive(Double.parseDouble(l_ratio_probes_over_positive_item.getValueAsString()));
		rec.setL_ratio_score_over_probes(Double.parseDouble(l_ratio_score_over_probes_item.getValueAsString()));
		rec.setL_min_median(Double.parseDouble(l_min_median_item.getValueAsString()));
		
		return rec;
	}
}
