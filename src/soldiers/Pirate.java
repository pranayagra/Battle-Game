package soldiers;

import java.awt.Color;
import java.util.ArrayList;

public class Pirate extends Warrior {
	
	public Pirate(boolean ev) {
		super(ev);
		setMaxAttackPower(getAttackPower()*5);
		setHealth(10);
		setMaxHealth(10);
	}
	
	public void attackAction() {
		if(Math.random() < .85) { //85% chance to do superclass attack
			super.attackAction();
		}
		else { //15% chance to hurt every Soldier around me
			int ap = getAttackPower();
			setAttackPower(getAttackPower()*((int)(Math.random()*4+2)));
			ArrayList<Soldier> attack = new ArrayList();
			attack = getBoard().getAllEnemies(this); //ArrayList of all the enemies
			for(int i = 0; i < attack.size(); i++) {
					attack.get(i).takeDamage(this.getAttackPower(), this); //Take Damage
			}
			setAttackPower(ap); //Set my Attack back
		}
	}
	
	public void takeDamage(int dmg, Soldier dudeWhoHitMe) {
		super.takeDamage(dmg, dudeWhoHitMe);
		setMaxHealth(getMaxHealth()+50);
		setHealth(getHealth()+2);
	}

}
