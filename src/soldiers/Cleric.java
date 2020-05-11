package soldiers;
import java.util.ArrayList;

import effects.Fear;
import effects.Protection;
import board.Square;


public class Cleric extends Soldier{

	public Cleric(boolean ev){
		super(60,10,10,ev);
	}

	
	
	public void attackAction() {
		if(isDead())
			return;
		ArrayList<Square> locs = getBoard().getAllSquaresAround( getMySquare(), 1);
		getBoard().highlightSquares(locs, true);//true means turn them green (false is purple);
		animate();
		ArrayList<Soldier> neighbors = getBoard().getSoldiersAround( getMySquare(), 1 );
		
		for( Soldier otherDude : neighbors )
			if( this.isFriend(otherDude) ){ //don't heal enemies!
				otherDude.beHealed( getAttackPower() );
				otherDude.addEffect( new Protection(1.00,5));
			}
			else if ( otherDude instanceof Skeleton)//scare any undead enemies around you
				otherDude.addEffect(new Fear(5,this));
	}

	public void moveAction() {
		if(isDead())
			return;
		ArrayList<Soldier> peeps = getBoard().getSoldiersAround(getMySquare(),1);
		boolean wantToMove=true;
		//if there is a friend to heal near me, then I won't move
		for(Soldier s : peeps)
			if(this.isFriend(s))
				wantToMove=false;
		if(wantToMove){//no one to heal?  then move
			super.standardMove();
		}
		
	}
}
