package soldiers;

import java.util.*;
import board.Square;

public class Wizard extends Mage {

	public Wizard(boolean ev) {
		super(ev);
	}
	
	public void moveAction() {
		if(Math.random() < .75) { // 75% to move like a Mage
			super.moveAction();
		}
		else {//25% chance
			ArrayList<Square> empty = new ArrayList(); 
			empty = getBoard().getAllEmptySquares(); //An array of ALL empty Square(s)
			int randomSquare  = (int)(Math.random()*empty.size()); //Pick a randomSquare
			stepInto(empty.get(randomSquare)); //Move to that randomSquare
		}
	}
	
	
	public void attackAction() {
		if(Math.random() < .75) { // 75% to attack like a Mage
			super.attackAction();
		}
		else { //25% chance
			ArrayList<Soldier> enemies = new ArrayList();
			enemies = getBoard().getAllEnemies(this); //An array of ALL the enemies	
			int randomEnemy = (int)(Math.random()*enemies.size()); //Pick a randomEnemy
			Square loc = enemies.get(randomEnemy).getMySquare(); //Find the location of our enemy
			boolean evilness = enemies.get(randomEnemy).isEvil(); //is the enemy evil?
			enemies.get(randomEnemy).die(); //kill the old enemy
			getBoard().addSoldierToGame(new Toad(evilness), loc); //Make it a toad
		}
	}
	
	
}
