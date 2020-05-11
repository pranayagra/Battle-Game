package soldiers;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import board.Square;


public class Archer extends Soldier{
	private boolean wantToMove=false;
	
	//Constructor
	public Archer( boolean ev ){
		super(75, 5, 15, ev);
		wantToMove=true;
	}
		
	@Override
	public void attackAction() {
		if(isDead())
			return;
				
		Soldier target = getBoard().getClosestSoldierInDir(this.getMySquare(), this.getDirection());//findClosestTarget( getDirection() );
		if(target==null)//no one there
			wantToMove=true;
		else if( this.isFriend( target ) ) //friendly target--i should move
			wantToMove = true;
		else if( getMySquare().distance( target.getMySquare()) <=7 ){ //must be an enemy...make sure he is in range
			animate();
			//highlight all the squares between me & target
			getBoard().highlightSquares( getBoard().getSquaresInPath(this.getMySquare(), target.getMySquare()),false);
			target.takeDamage( getAttackPower(), this );	
			wantToMove=false;//I want to stand here and shoot forever			
		}
		else //target was too far away
			wantToMove = true;
				
	}

	@Override
	public void moveAction() {
		if(isDead())
			return;
		if(wantToMove) { //moves twice as fast
			standardMove();
			standardMove();
		}
	}
	
	public void takeDamage( int dmg, Soldier dudeWhoHitMe ) {
		super.takeDamage(dmg, dudeWhoHitMe); //take damage
		turn(); //call method turn
		wantToMove = true; //it wants to move
	}
	
	@Override
	public void turn() {
		super.turn(90); 
	}
	
	
	
	
}
