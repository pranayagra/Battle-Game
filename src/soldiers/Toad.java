package soldiers;
import java.util.*;

import board.Square;
public class Toad extends Soldier{
	public Toad(boolean ev){
		super(5,0,0,ev);
	}

	@Override
	public void attackAction() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void moveAction() {
		if(getBoard()==null)
			return;
		// TODO Auto-generated method stub
		ArrayList<Square> locs = getBoard().getEmptySquaresAround(getMySquare(), 1);
		if(locs.size()==0)
			return;
		else{
			Square next = locs.get( (int)(Math.random()*locs.size()) );
			this.stepInto(next);
		}
			
	}
	
}
