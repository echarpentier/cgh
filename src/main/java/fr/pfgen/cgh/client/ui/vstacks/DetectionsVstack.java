package fr.pfgen.cgh.client.ui.vstacks;

import fr.pfgen.cgh.client.ui.grids.listgrids.DetectionListgrid;

public class DetectionsVstack extends GenericVstack {

	private DetectionListgrid grid;
	
	public DetectionsVstack(){
		super();
	}
	
	public void addGrid(){
		if (grid!=null){
			grid.destroy();
		}
		grid = new DetectionListgrid();
		this.addMember(grid);
	}
	
	public DetectionListgrid getGrid(){
		return grid;
	}
}
