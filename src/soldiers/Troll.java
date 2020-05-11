package soldiers;

import java.util.ArrayList;
import board.Square;

public class Troll extends Warrior {

	//Constructor
	public Troll(boolean ev) {
		super(ev);
		setMaxAttackPower(getAttackPower()*2);
		setAttackPower(getAttackPower()*2);
	}
	
	public void attackAction() {
		if(Math.random() < .5) { //50% chance to do superclass attack
			super.attackAction();
		}
		else { //50% chance to hurt every Soldier around me
			setAttackPower(getAttackPower()/2);
			ArrayList<Soldier> smashed = new ArrayList();
			smashed = getBoard().getSoldiersAround(getMySquare(), 1); //ArrayList of soldiers around me
			for(int i = 0; i < smashed.size(); i++) {
					smashed.get(i).takeDamage(this.getAttackPower(), this); //Take Damage
			}
			setAttackPower(getAttackPower()*2); //Set my Attack back
		}
	}
	
	public void moveAction() {
		super.moveAction();
		if(Math.random() < .5) { //50% chance to turn 45 degrees
			turn(45);
		}
	}
	
	
	
}
