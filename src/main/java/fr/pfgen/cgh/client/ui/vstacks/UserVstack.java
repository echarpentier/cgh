package fr.pfgen.cgh.client.ui.vstacks;

import fr.pfgen.cgh.client.ui.grids.listgrids.UserListgrid;

public class UserVstack extends GenericVstack {

	private UserListgrid grid;
	
	public UserVstack(){
		super();
	}
	
	public void addGrid(){
		if (grid!=null){
			grid.destroy();
		}
		grid = new UserListgrid();
		this.addMember(grid);
	}
	
	public UserListgrid getGrid(){
		return grid;
	}
}
