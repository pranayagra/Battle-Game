package soldiers;

import java.awt.Color;

public class Barbarian extends Warrior{
	private boolean isAngry;
	
	//Constructor
	public Barbarian(boolean ev) {
		super(ev);
		setArmor(super.getArmor()/2);
		setBlockChance(super.getBlockChance()/2);
		isAngry = false; //spawned in false
	}
	
	public void moveAction() {
		super.moveAction();
		if(isAngry) { //If I am angry, moveAction again
			super.moveAction();
		}
	}
	
	public void takeDamage(int dmg, Soldier dudeWhoHitMe) {
		if(getHealth() <= getMaxHealth()/2) { //If my health is at or below 50%....
			isAngry = true;
			setHealthBarColor(Color.ORANGE);
			setAttackPower(getMaxAttackPower());
			dmg/=2; //half dmg
		}
		super.takeDamage(dmg, dudeWhoHitMe); //take damage
		setDirection(dudeWhoHitMe.getDirection());
		turn(180); //Face the direction of the person who hit me
	}
	
	public void attackAction() {
		turn(-45);
		super.attackAction();
		turn(45);
		super.attackAction();
		turn(45);
		super.attackAction();
		turn(-45); //Attack everyone in front of me and then face the original direction
	}
}
