package effects;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import soldiers.*;

public abstract class Effect {

	private boolean stackable;
	private int duration;
	private double strength;
	private int timer;
	private boolean isActive;
	private Soldier target;
	private Color hue; 
	public static final int DISPLAY_SIZE=8;
	private static final Font font=new Font("Monospaced",Font.BOLD,DISPLAY_SIZE+4);
	
	public Effect(double how_strong, int how_long, Color h, boolean can_stack){
		strength=how_strong;
		duration = how_long;
		timer=0;
		isActive = true;
		hue = h;		
		stackable = can_stack;
		
	}
	
	//Postcondition:  timer is incremented by 1 AND the effect has become inactive if it times out
	public void updateTimer(){
		timer++;
		if(target.isDead()){
			setActive(false);
			return;
		}
		if(timer>=duration){
			isActive=false; //turn it off
			endEffect();  //then maybe you will need to do specific stuff (like restore their armor or something)
		}
	}
	
	//accessors & mutators
	public double getStrength(){return strength;}
	public void setStrength(double s){strength=s;}
	public boolean isActive(){return isActive;}
	public void setActive(boolean b){isActive = b;}
	public Soldier getTarget(){return target;}
	public void setTarget(Soldier t){target=t;}
	public Color getHue(){return hue;}
	public void setHue(Color h){hue=h;}
	public boolean isStackable(){return stackable;}
	
	
	public void paint(Graphics g, int myIndex){
		if(!isActive)
			return;
		g.setColor(hue);
		g.fillOval(myIndex*(DISPLAY_SIZE+2), target.getMySquare().getHeight()-DISPLAY_SIZE-2, DISPLAY_SIZE, DISPLAY_SIZE);
		g.setColor(Color.BLACK);
		g.setFont(font);
		g.drawString((duration-timer)+"", myIndex*(DISPLAY_SIZE+2), target.getMySquare().getHeight());
	}
	
	//Preconditions: target is alive
	//Postcondition:  the timer variable is updated
	public void eachTurn(){   //what happens to the soldier each turn -- > may need to override this for things like poison
		if(!isActive || target.isDead())
			return;		
		updateTimer();
	}
		
	public abstract void startEffect(); //what happens to the soldier when this starts	
	public abstract void endEffect();  //what happens when this effect is over (may need to restore a stat to the previous value)
	
	
}
