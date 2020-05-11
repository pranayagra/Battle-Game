package effects;
import java.awt.Color;

import soldiers.Soldier;

public class Poison extends Effect{
	public static final Color h = new Color(100,255,100);
	
	public Poison(int strength, int howLong){
		super(strength, howLong, h, true); //can stack
	}
	
	public void eachTurn(){	
		super.eachTurn();//update the timer
		getTarget().setHealth( getTarget().getHealth()-1);
		getTarget().displayDmg(-1);
		if(getTarget().getHealth()<=0)
			getTarget().die();
	}

	@Override
	public void startEffect() {
		if(getTarget()==null || getTarget().isDead())
			return;
		//nothing to do here
		getTarget().getBoard().printToDisplay(getTarget()+" is POISONED!");
	}

	@Override
	public void endEffect() {
		//nothing to do here
	}

	
}
