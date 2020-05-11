package soldiers;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import effects.Effect;
import board.GameBoard;
import board.Square;
import utilities.ImageObject;
import utilities.PopUpDisplay;


public abstract class Soldier extends ImageObject{
	private boolean evil; //true = evil; false = good
	private int health;	 //current health
	private int attackPower;  //how hard you hit/how much damage you do	
	private int armor;  //percent of damage that you ignore
	private int maxHealth;	
	private int maxAttackPower;
	
	private boolean dead = false;
	private GameBoard board;
	private Square mySquare;
	private boolean selected; //is it my turn or not?
	
	private ArrayList<Effect> activeEffects;
	private Color healthBarColor;	
	private static final Stroke str = new BasicStroke(20); //use for drawing the box when you are selected
	private PopUpDisplay display;	
	private String className;
	
	//animation stuff
	private int animRepeatCount=3;
	private int animRotateAmount = 5;
	private boolean animating=false;

	public abstract void attackAction();
	public abstract void moveAction();
	
	public Soldier(int my_health, int my_armor, int my_attackP, boolean evilOrNot){
		super(evilOrNot);
		activeEffects = new ArrayList();
		className = this.getClass().toString(); // this is "soldiers.<name>";
		if(className.indexOf(".")>=0)//in case someone puts their character in the default package
			className = className.substring(className.indexOf(".")+1);
		
		evil = evilOrNot;
		maxHealth=my_health;
		health = my_health;
		armor = my_armor;
		attackPower = my_attackP;	
		maxAttackPower=2*attackPower;
		if(evil)
			setDirection(GameBoard.EAST);
		else
			setDirection(GameBoard.WEST);
		healthBarColor = new Color(0,255,0);
		display = new PopUpDisplay(this);		
	}
	
	
/**...................MOVEMENT FUNCTIONS..............................................................................**/	
	/*
	 * Preconditions: none
	 * Postconditions:  If the square in front of me is empty, I will move into it
	 * 					If it is filled by a friend, I turn
	 * 					If it is filled with an enemy I do not move or turn
	 * 					If the square in front of me does not exist, I will turn
	 */
	public boolean standardMove(){
		if(dead)
			return false;
		
		
		//no enemy nearby?  try to walk forward
		Square next = getBoard().getSquareInDirection( this.getMySquare(), this.getDirection());
		
		//look forward, forward-left, & forward-right for an enemy
		ArrayList<Square> lineOfSight = getBoard().getConeArea( getMySquare(), getDirection() );
		Square seeEnemyHere=null;
		for(Square s : lineOfSight)
			if( !s.isEmpty() && this.isEnemy(s.getSoldier()) ){
				seeEnemyHere=s;
				break;
			}
		
		if(seeEnemyHere!=null){//see someone, turn & face them
			setDirection( getBoard().getDirectionFrom(this.getMySquare(), seeEnemyHere ) );
			return false;
		}
		else if(next==null){//square doesn't exist
			turn();
			return false;
		}		
		if( next.isEmpty() ){//no one is there
			this.stepInto(next );
			return true;
		}
		else if( this.isEnemy(next.getSoldier())) //facing an enemy, don't move & don't turn
			return false;
		else{//sq is filled w/a friend
			turn();
			return false;
		}
	}
	
	public void stepInto( Square newLoc ){
		if(dead)
			return;
		if(mySquare!=null){
			mySquare.setSoldier(null);//i have vacated the square i was in
			mySquare.paint(mySquare.getGraphics());
		}
		if(newLoc!=null)
			newLoc.setSoldier(this);
	}
	
	//I will turn 45 degrees counter clockwise
	public void turn(){
		if( isDead())
			return;
		setDirection( (getDirection()+45)%360); 
	}
	public void turn( int numDegrees){
		if( isDead())
			return;
		setDirection( (getDirection()+numDegrees)%360);
	}
	

/** #################################........COMBAT FUNCTIONS .....................................########################################### **/
	
	public boolean isEnemy( Soldier other ){
		if(other == null || isDead())
			return false;
		return this.isEvil() != other.isEvil();
	}
	
	public boolean isFriend( Soldier other){
		if(other==null || isDead())
			return false;
		return this.isEvil() == other.isEvil();
	}

	/*
	 * Precondition: 0<=dir<360
	 * Postconditions:  returns the Soldier who is closest to me the specified direction
	 * 					this may be a friend OR a foe
	 * 					null is returned if there is not Soldier to be found in that direction
	 */
	public Soldier findClosestTarget(int dir){
		if(isDead())
			return null;
		int r = getMySquare().getRow();
		int c = getMySquare().getCol();
		int dr = getBoard().rowDirection( dir );
		int dc = getBoard().colDirection( dir );
		
		r+=dr;
		c+=dc;
		while( getBoard().isValid(r,c)  ){
			if(getBoard().getSoldierAt(r, c)!=null)
				return getBoard().getSoldierAt(r, c);
			r+=dr;
			c+=dc;
		}
		return null;
	}
	
	/*
	 * Preconditions: dmg>=0 represents how hard someone has hit you
	 * 				  dudeWhoHitMe isthe person how has injured me
	 * Postconditions:  myhealth is decreased by dmg after adjusting for my armor%
	 * 					Pretty floating dmg numbers are displayed
	 * 					If I am now at low health, my health bar changes to red
	 * 					If I am out of health, I am REMOVED from the game
	 */
	public void takeDamage( int dmg, Soldier dudeWhoHitMe ){
		if(isDead())
			return;
		getMySquare().highlight(false);//false=bad (purple) highlight
		
		//i will ignore armor% of this damage
		int realDamage = (int)Math.round(dmg*(1-armor/100.0));
		health -= realDamage;
		//show floating damage numbers
		this.displayDmg(-1*realDamage);
		//if i am almost dead, my health bar will turn red
		if(this.getHealth() < this.getMaxHealth()*0.25)
			healthBarColor = new Color( 200, 100, 0 );
		//if i'm dead, i remove myself from the board
		if(health<=0)
			die();		
	}
	
	/*
	 * Preconditions:  h>=0 and represents the amount i was healed by
	 * Postconditions:  my health has been increased by h points (but will not exceed my maxhealth)
	 * 					pretty green healing numbers are displayed
	 */
	public void beHealed( int amountOfHealing ){
		if( isDead())
			return;
		getMySquare().highlight(true);//true= good (green) highlight
		int missing = maxHealth-health;
		//can't heal you above your maximum health
		int realHeal = Math.min(amountOfHealing, missing);
		health+=realHeal;
		//health bar may need to turn back to green
		if(this.getHealth() > this.getMaxHealth()*0.25)
			healthBarColor = Color.green;
		this.displayDmg(realHeal);//make pretty floating numbers
	}
	
	/*
	 * Preconditions: none
	 * Postconditions: this Soldier is completed removed from the game
	 */
	public void die(){
		if(dead)
			return;
		
		GameBoard tmp = getBoard();//need this to update counts after I'm dead
		mySquare.setSoldier(null);
		mySquare = null;		
		dead = true;
		board = null;		
		
		tmp.updateCounts();
	}
	

	public void displayDmg(int d){	display.setText(d); }  //shows pretty floating numbers (negative numbers in red, positive in green)
	public void displayText(String s){	display.setText(s); } //shows pretty floating text
	
	public String toString(){
		
		String str="";
		if(isEvil())
			str+="Evil ";
		else
			str+="Good ";
		str+= className+"@ "+mySquare;//+": ("+health+", "+armor+", "+attackPower+", "+evil+")";
		if(isDead())
			str+="DEAD!";
		return str;
	}
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~              accessors & mutators                   ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public boolean isDead(){return dead;}
	
	public boolean isEvil(){return evil;}
	public void setEvil(boolean e){
		if( isDead())
			return;
		if(e!=evil)//i'm changing
			pic = super.loadImage(super.getFileName(), e);//need a new picture!
		evil = e;		
	}
	
	public int getHealth(){return health;}
	public int getArmor(){return armor;}
	
	
	public void setHealth(int h){
		health = Math.min(maxHealth, h);
		if( health <= 0 )
			this.die();
	}
	public void setArmor(int a){armor = Math.min(100,a);}
	
	public int getMaxHealth(){return maxHealth;}
	public void setMaxHealth(int m){maxHealth=m;}
	
	public int getAttackPower(){return attackPower;}
	public void setAttackPower( int ap ){attackPower = Math.min(maxAttackPower, ap);}
	
	public int getMaxAttackPower(){return maxAttackPower;}
	public void setMaxAttackPower( int ap ){maxAttackPower = ap;}
	
	public GameBoard getBoard(){
		if(isDead() || board==null)
			throw new IllegalStateException();
		return board;
	}
	public void setBoard( GameBoard b){board=b;}
	
	public Square getMySquare(){
		if(isDead() || board==null || mySquare==null)
			throw new IllegalStateException();
		return mySquare;
	}	
	public void setMySquare( Square s ){
		if( isDead())
			return;
		mySquare = s;
		if(s!=null)
			board = s.getBoard();
		else{
			die();
		}
	}
	
	public Color getHealthBarColor(){ return healthBarColor; }
	public void setHealthBarColor(Color c){ healthBarColor = c; }
	
	public ArrayList<Effect> getActiveEffects(){return activeEffects;}
	
			
	public boolean isSelected(){return selected;}
	
	/*Preconditions: h = true to begin this soldiers turn, false to end it
	 * Postconditions:  the PAUSE occurs in this function (when h==true)
	 * 					the active effects are applied (when h==true)
	 * 					Also, after my turn is over (h==false), the counts are updated and the game may end
	 */
	public void setSelected(boolean h){ 
		if(dead)
			return;
		selected=h;
		getMySquare().needsToRepaint=true;
		if(h==true){//wait so we can see that this person is selected/taking his turn
			getBoard().pause( (int)Math.round(GameBoard.FRAME_PER_TURN*(1000.0/GameBoard.FPS)) );
			this.applyEffects();//poisons & what-not have their hey day
		}
		else{//this is the end of my turn -- see if I killed the last enemy
			getBoard().pause( (int)Math.round(GameBoard.FRAME_PER_TURN*(1000.0/GameBoard.FPS)) );
			getBoard().updateCounts();
			getBoard().checkWinner();
		}
	}
	
	//*****Effect functions*******
	public void addEffect( Effect e){
		if(isDead())
			return;
	
		//if it is not stackable, check to see if you have an instace of this effect already!
		if( !e.isStackable())
			for(Effect currentEffect : this.activeEffects)
				if(e.getClass().getName().equals(currentEffect.getClass().getName()))
					return;
					
		e.setTarget(this);	
		activeEffects.add(e);//add this current effect to my list of things effecting me
		e.startEffect(); //let it do its thing 
		
	}
	public void applyEffects(){
		if( isDead())
			return;
		//let all of my effects hurt/help me
		for(int i=activeEffects.size()-1; i>=0; i--)
			if( !activeEffects.get(i).isActive()) //if it is timed out, get it off of the list
				activeEffects.remove(i);
			else{//let it effect  me
				activeEffects.get(i).eachTurn();
				if(isDead())//that may have killed me
					return;
			}
	}

	public void paint( Graphics g){	
		if(dead)
			return;
		//System.out.println("DRAW "+this);
		//health bar
		double width = ((double)health/maxHealth)*(getMySquare().getWidth()-4);
		double height = getMySquare().getHeight()/20;
		g.setColor(Color.BLACK);
		g.drawRect(2, 2, (int)width, (int)height);
		g.setColor( healthBarColor );
		g.fillRect(2, 2, (int)width, (int)height);
		g.setColor(Color.DARK_GRAY);
		g.drawString(""+health, 25,10);
		
		//normal stuff		
		super.draw(g);
	
		//draw all of my active effects
		for(int i =0; i < activeEffects.size(); i++)
			activeEffects.get(i).paint(g, i);
		
		//draw the blue box if it's my turn
		if(selected){
			g.setColor(new Color(0,0,255,150));
			((Graphics2D)g).setStroke(str);
			g.drawRect(0, 0, getMySquare().getWidth(), getMySquare().getHeight());
		}
		
		//show any text that you are supposed to show
		display.paint(g);
		
		
	}
	
	public void rotate(double theta){
		setDirection( (int)(Math.round(getDirection()+theta)));		
	}
	
	public void animate(){
		animating = true;
		//breaks 1/2 of your turn into enough frames to rotate up x times & back down x times (to center)
		//in milliseconds
		double animPauseAmount=(500.0/(2*animRepeatCount)*GameBoard.FRAME_PER_TURN/GameBoard.FPS );//in millis
		getBoard().pause((int)animPauseAmount);
		int multiplier=1;
		if(getDirection()>=90 && getDirection()<=180)
			multiplier=-1;
		//rotate one direction x times
		for( int i=0; i<animRepeatCount; i++){
			rotate(animRotateAmount*multiplier);			
			getMySquare().needsToRepaint=true;			
			getMySquare().paint(getMySquare().getGraphics());
			getBoard().pause((int)animPauseAmount);
		}	
		//rotate other direction x times (back to center)
		for( int i=0; i<animRepeatCount; i++){
			rotate(animRotateAmount*multiplier*-1);			
			getMySquare().needsToRepaint=true;
			getMySquare().paint(getMySquare().getGraphics());
			getBoard().pause((int)animPauseAmount);
		}

		animating=false;
	}
	
}
