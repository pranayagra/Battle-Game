package utilities;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import soldiers.Soldier;


public class PopUpDisplay {
	private final int timeLimit=100;
	private static Font numberFont = new Font("Arial",Font.BOLD, 20);
	private static Font textFont = new Font("Arial",Font.BOLD, 12);
	private Font currentFont = numberFont;
	
	private int alpha=255;
	private int deltaA=15;
	private int deltaY=8;
	
	private int currX;
	private int currY;
	private int numX = 20;
	private int textX = 10;
	private int Y = 75;
	private Soldier parent;
	
	private boolean positive;
	
	//positive color == BAD thing happened
	private final int nRED=255;//255;
	private final int nGREEN=153;//255;
	private final int nBLUE = 102; //204;
	
	//negative color == GOOD thing happened
	private final int pRED=0;
	private final int pGREEN=255;
	private final int pBLUE = 0;
	
	//used to display damage
	private boolean popup=false; 
	private String text;
	private int displayTimer;
	
	
	
	public PopUpDisplay(Soldier o){
		parent = o;
		text="";
		displayTimer=0;		
		positive=true;
		
	}
	
	
	public void setText(String t){
		text=t;
		currentFont = textFont;
		positive=true;
		reset();	
		currX = textX;
	}
	
	public void setText(int d){
		text=""+d;  
		currentFont = numberFont;
		reset();
		if(d>=0)
			positive = true;
		else
			positive = false;
		
	}
	
	//get ready for a new message
	public void reset(){
		if(parent.isDead())
			return;
		Y = (int)(parent.getMySquare().getHeight()*0.75);
		currX = (int)(parent.getMySquare().getWidth()*0.35);
		currY=Y;
		alpha = 255;
		parent.getMySquare().needsToRepaint=true;//added 3/19
	}
	
	
	public void paint( Graphics g){
		if(g==null || alpha<=0)
			return;
		
		g.setFont(currentFont);
		if(positive)
			g.setColor( new Color(pRED,pGREEN,pBLUE,alpha));
		else
			g.setColor( new Color(nRED,nGREEN,nBLUE,alpha));
		g.drawString(text, currX, currY);
				
		//make it fade before next time
		parent.getMySquare().needsToRepaint=true;
		currY-=deltaY;
		displayTimer++;
		alpha-=deltaA;
	}

}
