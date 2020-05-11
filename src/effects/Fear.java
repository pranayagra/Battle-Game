package effects;

import java.awt.Color;
import board.*;
import soldiers.Soldier;

public class Fear extends Effect{
	private Soldier sourceOfFear;
	private Square origSquare;
	
	//no real strength for fear, just duration
	public Fear(int dur, Soldier scaredOf){
		super(1, dur,Color.RED, false);//can't stack
		sourceOfFear=scaredOf;
	}

	@Override
	public void startEffect() {
		if(getTarget()==null || getTarget().isDead())
			return;
		getTarget().getBoard().printToDisplay(getTarget()+" is scared!");
		origSquare = getTarget().getMySquare(); //remember where i came from
		eachTurn();//go ahead and turn them at the beginning
	}

	@Override
	public void endEffect() {
		// TODO Auto-generated method stub
		if(getTarget()==null || getTarget().isDead())
			return;
		//head back to where you came from
		GameBoard gb = getTarget().getBoard();
		int dir = gb.getDirectionFrom( getTarget().getMySquare(), origSquare);
		getTarget().setDirection(dir);
	}
	
	public void eachTurn(){   //what happens to the soldier each turn -- > may need to override this for things like poison
		super.eachTurn();
		if(sourceOfFear==null || sourceOfFear.isDead()){
			setActive(false);
			return;
		}
		
		//face away from the person i'm scared of
		GameBoard gb = getTarget().getBoard();
		int dir = gb.getDirectionFrom( sourceOfFear.getMySquare(), getTarget().getMySquare());
		getTarget().setDirection(dir);
		
			
	}	
	
}
