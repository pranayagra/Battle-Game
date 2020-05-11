package soldiers;

import java.util.*;

import board.Square;


public class Rogue extends Warrior {
	
	//Constructor
	public Rogue(boolean ev) {
		super(ev);
		setMaxHealth(getHealth()/2);
		setHealth(getHealth()/2);
		setArmor(getArmor()/2);
		setBlockChance(getBlockChance()/4);
	}
	
	public void moveAction() {
		if (!isDead()) {
			if (Math.random() < .5) { //50% chance to do superclass
				super.moveAction();
			} else { //50% chance to teleport behind a random enemy
					ArrayList<Soldier> enemies = new ArrayList();
					enemies = getBoard().getAllEnemies(this); //ArrayList of enemies
					int randomEnemy = (int) (Math.random() * enemies.size());
					int dir = this.getDirection(); //my direction
					this.setDirection(enemies.get(randomEnemy).getDirection());
 					this.turn(180);
 					Square dest = getBoard().getSquareInDirection(enemies.get(randomEnemy).getMySquare(), this.getDirection()); //dest is my Square
					if( dest != null && dest.isEmpty()) { //if the dest exist and is empty
						this.stepInto(dest); //step into that place
						this.turn(180); //face the soldier
					}
					else {
						this.setDirection(dir); //set my direction back
					}
					attackAction();
			}
		}
	}
	public void takeDamage( int dmg, Soldier dudeWhoHitMe ){
		if(!isDead()) { 
			super.takeDamage(dmg, dudeWhoHitMe);
			if(!isDead() && Math.random() < .5) { //50% chance to attack
				setAttackPower(getAttackPower()/2);			
				super.attackAction();
				setAttackPower(getAttackPower()*2);
			}
		}
	}
	
}

