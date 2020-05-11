package effects;

import java.awt.Color;

public class Protection extends Effect{
	private static final Color h = new Color(200,200,0);
	private int armorAdded=0;
	
	public Protection(double str, int lng){
		super(str, lng, h, false); //can't stack
	}
	@Override
	public void startEffect() {
		if(getTarget()==null || getTarget().isDead())
			return;
		armorAdded = (int)Math.round(getTarget().getArmor()*getStrength());
		getTarget().setArmor( getTarget().getArmor() + armorAdded );
		getTarget().getBoard().printToDisplay(getTarget()+" is protected: armor="+getTarget().getArmor());
	}
	@Override
	public void endEffect() {
		if(getTarget()==null || getTarget().isDead())
			return;
		getTarget().setArmor( getTarget().getArmor() - armorAdded );
		getTarget().getBoard().printToDisplay(getTarget()+" protection wore off: armor="+getTarget().getArmor());
	}
	
}
