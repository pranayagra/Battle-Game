package soldiers;

public class Paladin extends Warrior {
	private boolean save = true;
	
	//Constructor
	public Paladin(boolean ev) {
		super(ev);
		setBlockChance(getBlockChance()/2);
	}
	
	public void attackAction() {
		if(Math.random() < .25) { //25% chance to be healed
			this.beHealed(this.getAttackPower());
		}
		else //75% chance to do superclass attack
			super.attackAction();
	}
	
	public void die() {
		if(save && Math.random() < .2) { //20% chance to be saved "extra life"
			this.setHealth(this.getMaxHealth()/2);
			save = false;
		}
		else //80% chance to die
			super.die();
	}
}
