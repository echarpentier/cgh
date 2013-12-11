package fr.pfgen.cgh.client.ui.vstacks;

import fr.pfgen.cgh.client.ui.grids.listgrids.TeamListgrid;

public class TeamVstack extends GenericVstack{

	private TeamListgrid grid;
	
	public TeamVstack(){
		super();
	}
	
	public void addGrid(){
		if (grid!=null){
			grid.destroy();
		}
		grid = new TeamListgrid();
		this.addMember(grid);
	}
	
	public TeamListgrid getGrid(){
		return grid;
	}
}
