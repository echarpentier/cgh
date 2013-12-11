package fr.pfgen.cgh.shared.records;

import java.io.Serializable;

import fr.pfgen.cgh.shared.enums.DetectionQuality;

@SuppressWarnings("serial")
public class DetectionRecord implements Serializable,Record {

	private int ID;
	private int chr;
	private int start;
	private int end;
	private int probeNumber;
	private DetectionQuality quality;
	private int frameID;
	private String frameName;
	private String UCSCPosition;
	private String genomicBuild;
	private double LRmedian;
	
	public int getID() {
		return ID;
	}
	public void setID(int iD) {
		ID = iD;
	}
	public int getChr() {
		return chr;
	}
	public void setChr(int chr) {
		this.chr = chr;
	}
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public int getEnd() {
		return end;
	}
	public void setEnd(int end) {
		this.end = end;
	}
	public int getProbeNumber() {
		return probeNumber;
	}
	public void setProbeNumber(int probeNumber) {
		this.probeNumber = probeNumber;
	}
	public DetectionQuality getQuality() {
		return quality;
	}
	public void setQuality(DetectionQuality quality) {
		this.quality = quality;
	}
	public int getFrameID() {
		return frameID;
	}
	public void setFrameID(int frameID) {
		this.frameID = frameID;
	}
	public String getFrameName() {
		return frameName;
	}
	public void setFrameName(String frameName) {
		this.frameName = frameName;
	}
	public String getUCSCPosition() {
		return UCSCPosition;
	}
	public void setUCSCPosition(String uCSCPosition) {
		UCSCPosition = uCSCPosition;
	}
	public String getGenomicBuild() {
		return genomicBuild;
	}
	public void setGenomicBuild(String genomicBuild) {
		this.genomicBuild = genomicBuild;
	}
	public double getLRmedian() {
		return LRmedian;
	}
	public void setLRmedian(double lRmedian) {
		LRmedian = lRmedian;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ID;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DetectionRecord other = (DetectionRecord) obj;
		if (ID != other.ID)
			return false;
		return true;
	}
}
