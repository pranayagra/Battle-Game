package board;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.*;

import soldiers.Soldier;


import java.util.*;

public class Square extends JButton implements ActionListener{
	private int row, col;
	public static final int SIZE=60;
	public static int WIDTH, HEIGHT;
	
	private Soldier mySoldier;
	private static GameBoard board;
	
	BufferedImage buff;
	private boolean posLighted=false;
	private boolean negLighted=false;
		
	//for fading highlight
	private final int MAX_ALPHA = 125;
	private int alpha;
	private final int deltaA = 2*MAX_ALPHA/(GameBoard.FRAME_PER_TURN);
	public boolean needsToRepaint;	
		
	public Square(int r, int c){
		needsToRepaint=true;
		row=r;
		col=c;
		mySoldier=null;
		this.addActionListener(this);
		this.setPreferredSize(new Dimension(SIZE,SIZE));
	}
	public void setUpImage(){
		buff = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
		WIDTH = getWidth();
		HEIGHT = getHeight();
	}
	public static void setBoard(GameBoard b){board=b;}	
	public GameBoard getBoard(){return board;}
	
	public int getRow(){return row;}
	public int getCol(){return col;}

	public boolean isEmpty(){
		return mySoldier==null;
	}

	public Soldier getSoldier(){return mySoldier;}
	public void setSoldier(Soldier w){
		//if someone is already here, remove
		if(mySoldier!=null)
			mySoldier.setBoard(null);

		//inform myself AND my new occupant of the change
		mySoldier = w;
		if(mySoldier!=null)
			mySoldier.setMySquare(this);
		
		//repaint to show the change
		if(this.getGraphics()!=null)
			this.paint(this.getGraphics());
		
		needsToRepaint=true;
	}

	public int distance( Square other){
		int dr = Math.abs( this.row - other.row );
		int dc = Math.abs( this.col - other.col );
		return (int)Math.round( Math.sqrt(dr*dr +dc*dc));
	}	
		
	//positive effect = green highlight color;  !positiveEffect = red highlight color
	public void highlight( boolean positiveEffect ){
		if(positiveEffect)
			posLighted=true;
		else
			negLighted=true;
		alpha= MAX_ALPHA;
		needsToRepaint=true;
	}
	

	
	public void paint(Graphics g){	
		
		if(buff==null || buff.getGraphics()==null) return;
		
		if( needsToRepaint==false){	
			g.drawImage(buff, 0, 0, getWidth(), getHeight(), this);
			return;
		}
		needsToRepaint=false;
		
		Graphics crayon = buff.getGraphics();		
		super.paint(crayon);		
		if(mySoldier!=null){		
			mySoldier.paint(crayon);
		}
		
		//highlighted and/or selected == draw a transparent box		
		if(negLighted){
			crayon.setColor(new Color(255,0,255,alpha));
			crayon.fillRect(0, 0, this.getWidth(), this.getHeight());			
		}
		if(posLighted){
			crayon.setColor(new Color(0,200,0,alpha));
			crayon.fillRect(0, 0, this.getWidth(), this.getHeight());			
		}
		
		if(alpha>=0){
			alpha-=deltaA;
			needsToRepaint=true;
		}
		
		if(alpha<=0){
			posLighted=false;
			negLighted=false;
			
		}
		g.drawImage(buff, 0, 0, getWidth(), getHeight(), this);
	}
	
	public String toString(){return "("+row+", "+col+")";}
	
	
	//@Override
	public void actionPerformed(ActionEvent arg0) {		
		//JOptionPane.showMessageDialog(arg0, arg1)
		if(board.isPlaying())//don't let them make changes while things are going
			return;
		
		if(mySoldier==null){
			Object ans = JOptionPane.showInputDialog( this , "What type of Soldier do you want here?", "Select Soldier",
					JOptionPane.PLAIN_MESSAGE, null, GameBoard.soldierTypes, GameBoard.soldierTypes[0]);
			if(ans==null)
				return;
			String[] sides = {"Good","Evil"};
			Object ans2 = JOptionPane.showInputDialog( this , "What Side Does He Fight For?", "Evil or No?",
					JOptionPane.PLAIN_MESSAGE, null, sides, sides[0]);
			if(ans2==null)
				return;
			boolean evil = true;
			if(ans2.equals("Good"))
				evil = false;
			Soldier s = GameBoard.makeSoldier( (String)ans, evil);
			getBoard().addSoldierToGame( s,  this );
		}
		else{//there's a soldier here, let's change his  directions
			Object ans = JOptionPane.showInputDialog( this , "Which direction?", "Change My Direction",
					JOptionPane.PLAIN_MESSAGE, new ImageIcon(mySoldier.getPic()), Soldier.directionNames, Soldier.directionNames[0]);
			if(ans==null)
				return;

			//they picked a name...figure out what degree measure matches that 
			for(int i = 0; i<Soldier.directionNames.length; i++)
				if( ans.equals( Soldier.directionNames[i] )  ){
					mySoldier.setDirection( Soldier.directions[i] );	
					needsToRepaint=true;
					this.paint(this.getGraphics());
				}
			
		}
	}
	
}
