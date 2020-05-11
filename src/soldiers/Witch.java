package soldiers;

import java.util.*;
import board.Square;

public class Witch extends Mage {
	
	private ArrayList<Skeleton> skeletons = new ArrayList();
	
	//Constructor
	public Witch(boolean ev) {
		super(ev);
	}

	public void attackAction() {
		Square inFront = getBoard().getSquareInDirection(this.getMySquare(), this.getDirection());
		if(inFront != null && inFront.isEmpty()) {
			skeletons.add(new Skeleton(this.isEvil())); //appends a skeleton to end of ArrayList
			getBoard().addSoldierToGame(skeletons.get(skeletons.size()-1), inFront); //add skeleton to board
			this.setHealth(this.getHealth()-1); //Decrease my health
		}
		else {
			super.attackAction();
		}
	}

	public void moveAction() {
		if(Math.random() < .25) //25% chance to move
			super.moveAction();
	}
	
	public void die(){
		super.die();
		for(int i = skeletons.size()-1; i >= 0; i--) {
			skeletons.get(i).die(); //kill the skeleton if the Witch is dead
		}
	}
	
	
	
	
}
