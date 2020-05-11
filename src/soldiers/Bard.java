package soldiers;

import java.util.ArrayList;

public class Bard extends Cleric {

	//Constructor
	public Bard(boolean ev) {
		super(ev);
		setArmor(getArmor()*2);
	}
	
	public void attackAction() {
		ArrayList<Soldier> friends = new ArrayList();
		ArrayList<Soldier> enemies = new ArrayList();
		ArrayList<Soldier> both = new ArrayList();
		both = getBoard().getSoldiersAround(getMySquare(), 1); //ArrayList of all the Soldiers around me
		for(int i = 0; i < both.size(); i++) { 
			if(both.get(i).isFriend(this)) { //If the soldier is a friend
				friends.add(both.get(i));
			}
			else { //If the soldier is an enemy
				enemies.add(both.get(i));
			}
		}
		if(friends.size() > enemies.size()) { //more friends than enemies
			super.attackAction();
		}
		else {
			for(int i = 0; i < enemies.size(); i++) { //more enemies than friends OR equal amounts
				enemies.get(i).takeDamage(this.getAttackPower(), this); //take damage
			}
			turn(); //turn a random amount
			super.moveAction();
		}
	}
	
	@Override
	public void turn() {
		turn(45 * (int)(Math.random()*8+1)); // 1 2 3 4 5 6 7 8
	}
}
