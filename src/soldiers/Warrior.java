package soldiers;
import java.util.*;

import board.Square;
public class Warrior extends Soldier{
	private double blockChance;

	public Warrior(boolean ev){
		super(150, 20, 10, ev);	
		blockChance = 0.5;
	}
	
	public double getBlockChance(){return blockChance;}
	public void setBlockChance( double bc ){ blockChance=bc; }
	
	//I must write TWO abstract functions!
	public void attackAction(){
		//find the square in front of me
		Square front = getBoard().getSquareInDirection( getMySquare() , getDirection() );
		
		//there's no square in front of me (i'm at the edge of board)
		if(front==null)
			return;
		
		Soldier target = front.getSoldier();
		//if there is someone there AND he's on the other team
		if( target!=null && this.isEnemy(target) ){
			target.takeDamage( this.getAttackPower(), this );
			animate();
			front.highlight(false);//make it red
		}
	}
	
	public void moveAction() {
		if(isDead())
			return;
		super.standardMove();
	}

	public void takeDamage( int dmg, Soldier dudeWhoHitMe ){
		if(!isDead()) {
			boolean blocked=false;
			int directionToAttacker = getBoard().getDirectionFrom( this.getMySquare(), dudeWhoHitMe.getMySquare() );
			if( directionToAttacker == this.getDirection() ){ //if the attacker is in front of me
				if(Math.random() < blockChance){//maybe i can block the attack!
					this.displayText("BLOCK");  //BLOCK with my shield = take no damage!
					blocked=true;
				}
			}
		
			if(!blocked)
				super.takeDamage(dmg, dudeWhoHitMe);
		}
	}

}
