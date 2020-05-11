package soldiers;

public class Mage extends Soldier {

	//Constructor
	public Mage(boolean ev) {
		super(50, 0, 20, ev);
	}

	public void attackAction() {
		Soldier s = getBoard().getClosestSoldierInDir(getMySquare(), getDirection()); //Get the soldier closest in my direction
		if(s != null) { //If it is not null
			if(this.isEnemy(s)) { //If it is an enemy
				s.takeDamage(getAttackPower(), this); //Hurt the enemy
			}
		}
	}

	public void moveAction() {
		super.standardMove();
	}


}
