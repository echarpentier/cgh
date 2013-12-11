package fr.pfgen.cgh.shared.records;

import java.io.Serializable;

import fr.pfgen.cgh.shared.enums.DelDupColors;
import fr.pfgen.cgh.shared.enums.YesNo;

@SuppressWarnings("serial")
public class AnalysisParamsRecord implements Serializable {

	private YesNo mask;
	private YesNo gcLowess;
	private DelDupColors dupColor;
	private DelDupColors delColor;
	
	private int s_min_positive_probes;
	private double s_min_total_score;
	private double s_probes_times_median;
	private double s_ratio_probes_over_positive;
	private double s_ratio_score_over_probes;
	
	private int l_min_positive_probes;
	private double l_min_total_score;
	private double l_probes_times_median;
	private double l_ratio_probes_over_positive;
	private double l_ratio_score_over_probes;
	private double l_min_median;
	
	public YesNo getMask() {
		return mask;
	}
	public void setMask(YesNo mask) {
		this.mask = mask;
	}
	public YesNo getGcLowess() {
		return gcLowess;
	}
	public void setGcLowess(YesNo gcLowess) {
		this.gcLowess = gcLowess;
	}
	public DelDupColors getDupColor() {
		return dupColor;
	}
	public void setDupColor(DelDupColors dupColor) {
		this.dupColor = dupColor;
	}
	public DelDupColors getDelColor() {
		return delColor;
	}
	public void setDelColor(DelDupColors delColor) {
		this.delColor = delColor;
	}
	public int getS_min_positive_probes() {
		return s_min_positive_probes;
	}
	public void setS_min_positive_probes(int s_min_positive_probes) {
		this.s_min_positive_probes = s_min_positive_probes;
	}
	public double getS_min_total_score() {
		return s_min_total_score;
	}
	public void setS_min_total_score(double s_min_total_score) {
		this.s_min_total_score = s_min_total_score;
	}
	public double getS_probes_times_median() {
		return s_probes_times_median;
	}
	public void setS_probes_times_median(double s_probes_times_median) {
		this.s_probes_times_median = s_probes_times_median;
	}
	public double getS_ratio_probes_over_positive() {
		return s_ratio_probes_over_positive;
	}
	public void setS_ratio_probes_over_positive(double s_ratio_probes_over_positive) {
		this.s_ratio_probes_over_positive = s_ratio_probes_over_positive;
	}
	public double getS_ratio_score_over_probes() {
		return s_ratio_score_over_probes;
	}
	public void setS_ratio_score_over_probes(double s_ratio_score_over_probes) {
		this.s_ratio_score_over_probes = s_ratio_score_over_probes;
	}
	public int getL_min_positive_probes() {
		return l_min_positive_probes;
	}
	public void setL_min_positive_probes(int l_min_positive_probes) {
		this.l_min_positive_probes = l_min_positive_probes;
	}
	public double getL_min_total_score() {
		return l_min_total_score;
	}
	public void setL_min_total_score(double l_min_total_score) {
		this.l_min_total_score = l_min_total_score;
	}
	public double getL_probes_times_median() {
		return l_probes_times_median;
	}
	public void setL_probes_times_median(double l_probes_times_median) {
		this.l_probes_times_median = l_probes_times_median;
	}
	public double getL_ratio_probes_over_positive() {
		return l_ratio_probes_over_positive;
	}
	public void setL_ratio_probes_over_positive(double l_ratio_probes_over_positive) {
		this.l_ratio_probes_over_positive = l_ratio_probes_over_positive;
	}
	public double getL_ratio_score_over_probes() {
		return l_ratio_score_over_probes;
	}
	public void setL_ratio_score_over_probes(double l_ratio_score_over_probes) {
		this.l_ratio_score_over_probes = l_ratio_score_over_probes;
	}
	public double getL_min_median() {
		return l_min_median;
	}
	public void setL_min_median(double l_min_median) {
		this.l_min_median = l_min_median;
	}
}
