import board.GameBoard;
import soldiers.*;

public class MyBattleField extends GameBoard{
	private static String[] types = { "Warrior","Archer","Cleric", "Mage", "Wizard", "Barbarian", "Rogue", "Witch", "Troll", "Bard", "Paladin", "Pirate"};
	
	public MyBattleField(){
		super(9,9);
		super.setSoldierTypes( types );
	}
	
	
	public void setUpSoldiers(){
		
		for(int row = 0; row < ROWS; row++) {
			Soldier goodSoldier =  this.makeSoldier( types[(int)(Math.random()*types.length)], true);
			Soldier evilSoldier =  this.makeSoldier( types[(int)(Math.random()*types.length)], false);
			this.addSoldierToGame(goodSoldier, grid[row][0]);
			this.addSoldierToGame(evilSoldier, grid[row][COLS-1]);
			Soldier goodSoldier2 =  this.makeSoldier( types[(int)(Math.random()*types.length)], true);
			Soldier evilSoldier2 =  this.makeSoldier( types[(int)(Math.random()*types.length)], false);
		}
		
		
	}
	
	//@Override
	public void eachFrame() {
		roundNumber++;
		printToDisplay("*****Round: "+roundNumber+"*****");
		for(Soldier p : this.getAllSoldiers()){
			if(!p.isDead()){
				p.setSelected(true);
				p.moveAction();
				p.attackAction();
				p.setSelected(false);
			}
		}
	}
	
	public static void main(String[] args) { 
		MyBattleField bf = new MyBattleField();
	}


}
