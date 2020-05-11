package effects;

import java.awt.Color;

public class Weaken extends Effect{
	public static final Color h = new Color(255,50,50);
	private int attackPowerLost;
	
	//str = % of attack power lost (as a decimal)
	//    ex:  .25 => target loses 25% of his attack power
	public Weaken(double str, int lng){
		super(str,lng,h, true);//can stack
	}

	@Override
	public void startEffect() {
		if(getTarget()==null || getTarget().isDead())
			return;
		//nothing to do here
		getTarget().getBoard().printToDisplay(getTarget()+" is WEAKENED!");
		attackPowerLost = (int)Math.round(getTarget().getAttackPower()*getStrength());
		getTarget().setAttackPower( getTarget().getAttackPower()-attackPowerLost);
	}

	
	@Override
	public void endEffect() {
		getTarget().setAttackPower( getTarget().getAttackPower()+attackPowerLost);		
	}
}
