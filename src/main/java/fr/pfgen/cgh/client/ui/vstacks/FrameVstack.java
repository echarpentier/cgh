package fr.pfgen.cgh.client.ui.vstacks;

import fr.pfgen.cgh.client.ui.grids.listgrids.FrameListgrid;

public class FrameVstack extends GenericVstack {

	private FrameListgrid grid;
	
	public FrameVstack(){
		super();
	}
	
	public void addGrid(){
		if (grid!=null){
			grid.destroy();
		}
		grid = new FrameListgrid();
		this.addMember(grid);
	}
	
	public FrameListgrid getGrid(){
		return grid;
	}
}
