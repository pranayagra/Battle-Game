package board;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.*;

import soldiers.*;
import utilities.PainterThread;
import utilities.PlayThread;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

public abstract class GameBoard extends JFrame implements ActionListener, WindowListener{
	
	public static String[] soldierTypes = {"Warrior"};
	public static final int NORTH=0, NORTH_EAST=45, EAST=90, SOUTH_EAST=135, SOUTH=180, SOUTH_WEST=225, WEST=270, NORTH_WEST=315;
	public static int ROWS=10,COLS=10;	
	protected Square[][] grid;
	
	
	protected int roundNumber=0;
	private JTextArea display;
	private JScrollPane displayScroller;
	private boolean playing;
	private JButton nextSoldier, nextRound, play,pause;
	private JTextField evilCount, goodCount, info;
	private JComboBox fpsSelector;
	private static int[] possibleFPS = {4,6,12,18,24,30,36,48};
	public static int FPS = 12;
	public static int FRAME_PER_TURN = 5; //how many frames each person is "selected"
	private PainterThread painter;
	private PlayThread player;

	public abstract void eachFrame();
	public abstract void setUpSoldiers();
	
	public void addSoldierToGame( Soldier person, Square place){
		place.setSoldier( person );
		updateCounts();
	}
	
	public GameBoard(int rr, int cc){
		super("Game");
		ROWS=rr;
		COLS=cc;
		playing = false;
		makeControls();
		JPanel holder = new JPanel( new GridLayout(ROWS, COLS) );
		this.addWindowListener(this);
		this.setResizable(false);
		
		Square.setBoard(this);
		grid = new Square[ROWS][COLS];
		for(int r=0; r<ROWS; r++)
			for(int c = 0; c<COLS; c++){
				grid[r][c] = new Square(r,c);				
				holder.add(grid[r][c]);				
			}
		
		setUpSoldiers();
		
		
		JScrollPane mainScroller = new JScrollPane(holder);
		mainScroller.setPreferredSize( new Dimension(10*Square.SIZE, 10*Square.SIZE));		
		this.add(mainScroller, BorderLayout.CENTER);
		
		//display setup
		display=new JTextArea();
		display.setColumns(30);
		display.setEditable(false);
		displayScroller = new JScrollPane(display);
		this.add( displayScroller, BorderLayout.EAST);
		int desiredWidth = 10*Square.SIZE+400;
		int desiredHeight = 30+10*Square.SIZE+50;
		this.setSize(desiredWidth, desiredHeight);
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		for(int r=0; r<ROWS; r++)
			for(int c = 0; c<COLS; c++){
				grid[r][c].setUpImage();
			}
		player = new PlayThread(this);
		player.start();
		painter = new PainterThread(grid);
		painter.start();		
		
		this.paintComponents(this.getGraphics());
		
	}
	
	public static void setSoldierTypes( String[] a){
		soldierTypes = a;
	}
	
	public boolean isPlaying(){
		return playing;
	}
	
	public void pause(int ms){
		try{
			Thread.sleep(ms);
		}
		catch(InterruptedException ie){
			
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void printToDisplay( String info ){
		display.append(info+"\n");
		display.paint(display.getGraphics());
		//scrollToBottom
		JScrollBar vertical = displayScroller.getVerticalScrollBar();
		vertical.setValue( vertical.getMaximum() );
	}
	
	public void checkWinner(){
		int evilNum = this.getTeam(true).size();
		int goodNum = this.getTeam(false).size();
		if(evilNum==0 && goodNum==0){
			JOptionPane.showMessageDialog(this, "Everyone is dead");
			this.windowClosing(null);
		}
		if( evilNum==0){
			JOptionPane.showMessageDialog(this, "The GOOD team has triumphed");
			this.windowClosing(null);
		}
		if( goodNum==0){
			JOptionPane.showMessageDialog(this, "The EVIL team has triumphed");
			this.windowClosing(null);
		}
		
		
	}
	
	public int rowDirection(int d){
		if(d<0)
			d=(360+d);
		d%=360;
		
		if(d==GameBoard.NORTH || d==GameBoard.NORTH_EAST || d==GameBoard.NORTH_WEST) 	
			return -1;
		if(d==GameBoard.SOUTH || d==GameBoard.SOUTH_EAST || d==GameBoard.SOUTH_WEST)	
			return 1;
		return 0;
	}
	
	public int colDirection( int d ){
		if(d<0)
			d=360+d;
		d%=360;
		if(d==GameBoard.NORTH_EAST ||d==GameBoard.EAST || d==GameBoard.SOUTH_EAST)	
			return 1;
		if(d==GameBoard.NORTH_WEST ||d==GameBoard.WEST || d==GameBoard.SOUTH_WEST)	
			return -1;
		return 0;
	}
	

	
	public void highlightSquares( ArrayList<Square> list , boolean good ){
		for( Square s : list )
			if(s!=null)
				s.highlight(good);
	}
	public void highlightSquare( Square s , boolean good ){
		if(s!=null)
			s.highlight(good);
	}
	
	/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ get SQUARE functions ~~~~~~~~~~~~~~~~~~~~~~~*/
	public boolean isValid(int r, int c){
		return r>=0 && r<ROWS && c>=0 && c<COLS;
	}
	
	public Square getSquareAt(int r, int c){
		if(isValid(r,c))
			return grid[r][c];
		else
			return null;
	}

	public Square getSquareInDirection(Square s, int dir){
		dir=dir%360;
		int dr=0, dc=0;
		dr=rowDirection( dir );
		dc=colDirection( dir );
		//System.out.println("dir="+dir+": dr"+dr+": dc:"+dc);
		if( isValid( s.getRow()+dr, s.getCol()+dc))
				return grid[s.getRow()+dr][s.getCol()+dc];
		else
			return null;		
	}
	
	public int getDirectionFrom( Square orig, Square dest ){
		int dr = dest.getRow() - orig.getRow();
		int dc = dest.getCol() - orig.getCol();
		if( dr>0 ){
			if(dc>0)		return SOUTH_EAST;
			else if( dc==0)	return SOUTH;
			else			return SOUTH_WEST; 
		}
		else if(dr<0){
			if(dc>0)		return NORTH_EAST;
			else if( dc==0)	return NORTH;
			else			return NORTH_WEST;			
		}
		else{//dr == 0
			if( dc > 0)		return EAST;
			else			return WEST;
		}
	}
	
	public ArrayList<Square> getAllEmptySquares(){
		ArrayList<Square> ans=new ArrayList();
		for(int r=0; r<ROWS; r++)
			for(int c=0; c<COLS; c++)
				if(grid[r][c].isEmpty())
					ans.add(grid[r][c]);
		return ans;
	}
	
	public ArrayList<Square> getAllOccupiedSquares(boolean evil){
		ArrayList<Square> ans=new ArrayList();
		for(int r=0; r<ROWS; r++)
			for(int c=0; c<COLS; c++)
				if( !grid[r][c].isEmpty() && grid[r][c].getSoldier().isEvil()==evil )
					ans.add(grid[r][c]);
		return ans;
	}
	
	
	public ArrayList<Square> getConeArea( Square sq, int dir){
		ArrayList<Square> ans = new ArrayList();
		int left = (dir-45)%360;
		int right = (dir+45)%360;
		Square[] cone = { getSquareInDirection(sq, dir), getSquareInDirection(sq, right), getSquareInDirection(sq, left)};
		
		
		for(int i=0; i<cone.length; i++)
			if(cone[i]!=null ){
				ans.add(cone[i]);
			}
		return ans;
	}
	
	public ArrayList<Square> getAllSquaresAround( Square s, int radius){
		ArrayList<Square> ans = new ArrayList();
		for(int r=-radius; r<=radius; r++)
			for(int c=-radius; c<=radius; c++){
				int row = s.getRow()+r;
				int col = s.getCol()+c;
				if( isValid( row, col ) && grid[row][col]!=s){
					ans.add( grid[row][col]);
				}
			}
		return ans;
	}
	
	public ArrayList<Square> getEmptySquaresAround( Square s, int radius){
		ArrayList<Square> ans = this.getAllSquaresAround(s, radius);
		for(int i=ans.size()-1; i>=0; i--)
			if( ans.get(i).isEmpty()==false)
				ans.remove(i);
		return ans;
	}
	/** ~~~~~~~~~~~~~~~~~~~~~~~ get People functions  ~~~~~~~~~~~~~~~~~~~~**/
	
	public Soldier getSoldierAt(int r, int c){
		if(isValid(r,c))
			return grid[r][c].getSoldier();
		else
			return null;
	}
	
	public ArrayList<Soldier> getAllSoldiers(){
		ArrayList<Soldier> ans=new ArrayList();
		for(int r=0; r<ROWS; r++)
			for(int c=0; c<COLS; c++)
				if( !grid[r][c].isEmpty() )
					ans.add(grid[r][c].getSoldier());
		return ans;
	}
	
	
	public Soldier getClosestSoldierInDir(Square sq, int dir){
		
		int r = sq.getRow();
		int c = sq.getCol();
		int dr = rowDirection( dir );
		int dc = colDirection( dir );
		
		r+=dr;
		c+=dc;
		while( isValid(r,c)  ){
			if(getSoldierAt(r, c)!=null)
				return getSoldierAt(r, c);
			r+=dr;
			c+=dc;
		}
		return null;
	}
	
	
	public ArrayList<Square> getSquaresInPath(Square startHere, int dir){
		ArrayList<Square> ans = new ArrayList();
		int r = startHere.getRow();
		int c = startHere.getCol();
		int dr = rowDirection( dir );
		int dc = colDirection( dir );
		
		r+=dr;
		c+=dc;
		while( isValid(r,c)  ){
			ans.add( grid[r][c] );
			r+=dr;
			c+=dc;
		}
		return ans;
	}
	
	public ArrayList<Square> getSquaresInPath(Square fromHere, Square toHere){
		if(toHere==null || fromHere==null)
			return new ArrayList();
		ArrayList<Square> ans = new ArrayList();
		int dir = this.getDirectionFrom(fromHere, toHere);
		int r = fromHere.getRow();
		int c = fromHere.getCol();
		int dr = rowDirection( dir );
		int dc = colDirection( dir );
		
		r+=dr;
		c+=dc;
		while( !(r==toHere.getRow() && c==toHere.getCol())  ){
			if( !isValid(r,c))//walked off of the board, just return what you've got!
				return ans;
			ans.add( grid[r][c] );
			r+=dr;
			c+=dc;
		}
		ans.add(grid[r][c]);//that's the actual target
		return ans;
	}
	
	
	//returns an arraylist of all the soldiers (friends and foes) within one square of square s
	//   *the soldier in front of the person in s is placed in index 0 of this array
	public ArrayList<Soldier> getSoldiersAround( Square s, int radius){
		ArrayList<Soldier> ans = new ArrayList();
		ArrayList<Integer> directions = new ArrayList();
		int startDir = 0;
		if(s.isEmpty()==false)//start by looking in front of this soldier
			startDir=s.getSoldier().getDirection();
		directions.add(startDir);
		
		for( int adder=45; adder<=180; adder+=45){//now the remaining 5 directions
			directions.add( (startDir+adder)%360 );
			if(adder!=180)//don't add the place behind you twice
				directions.add( (startDir-adder)%360 );
		}
		//System.out.println(directions);
		for( Integer dir: directions){
			Square sq=getSquareInDirection(s, dir);
			if(sq!=null && !sq.isEmpty()){
				//System.out.println(s+":"+dir+":"+sq);
				ans.add(sq.getSoldier());
			}
		}
		//System.out.println(s+":"+ans);
		return ans;
		
	}
	
	public ArrayList<Soldier> getTeam ( boolean evil){
		ArrayList<Soldier> ans = new ArrayList();
		for(int r=0; r<ROWS; r++)
			for(int c=0; c<COLS; c++)
				if( grid[r][c].getSoldier()!=null && grid[r][c].getSoldier().isEvil()==evil)
					ans.add(grid[r][c].getSoldier());
		return ans;
	}
	
	public ArrayList<Soldier> getAllFriends( Soldier ofWhom){
		return getTeam( ofWhom.isEvil() );
	}
	
	public ArrayList<Soldier> getAllEnemies( Soldier ofWhom){
		return getTeam( !ofWhom.isEvil() );
	}
	/** ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~**/

	public static Soldier makeSoldier(String type, boolean evil){
		//{"Archer","Barbarian","Bard","Cleric","Knight","Mage","Rogue","Witch","Wizard"};
		 Object object = null;
	      try {
	    	  Object[] arguments = {evil};
	          Class classDefinition = Class.forName("soldiers."+type);
	          Constructor con = classDefinition.getConstructor(boolean.class);
	          object = con.newInstance(arguments);
	      } catch (InstantiationException e) {
	          System.out.println(e);
	      } catch (IllegalAccessException e) {
	          System.out.println(e);
	      } catch (ClassNotFoundException e) {
	          System.out.println(e);
	      } catch (Exception e){
	    	  e.printStackTrace();
	      }
	      if(object !=null)
	    	  return (Soldier)object;
	      else//default case
	    	  return new Warrior(evil);
		
	}
	
		
	public JTextField makeField( JPanel p){
		JTextField t = new JTextField();
		t.setEditable(false);
		p.add(t);
		return t;
	}
	
	public JButton makeButton(String text, JPanel p){
		JButton ans = new JButton(text);
		ans.addActionListener(this);
		p.add(ans);
		return ans;
	}
	public void makeControls(){
		//counter text boxes
		JPanel north = new JPanel( new GridLayout(1,2) );
		evilCount = makeField( north);
		//info = makeField( north);
		goodCount = makeField( north);
		this.add(north, BorderLayout.NORTH);
		
		JPanel holder=new JPanel(new GridLayout(1,5));
		pause = makeButton("Pause", holder);
		//nextSoldier = makeButton("Next Soldier", holder);
		nextRound = makeButton("Next Round", holder);
		play = makeButton("Play", holder);
		
		fpsSelector = new JComboBox();
		fpsSelector.addItem("Frames Per Second");
		for( int x : this.possibleFPS)
			fpsSelector.addItem(x);
		fpsSelector.addActionListener(this);		
		holder.add(fpsSelector);
		
		this.add(holder, BorderLayout.SOUTH);
	}
	
	public void updateCounts(){
		evilCount.setText(this.getTeam(true).size()+" Evil Soldiers");
		
		goodCount.setText(this.getTeam(false).size()+" Good Soldiers");
		evilCount.paint(evilCount.getGraphics());
		goodCount.paint(goodCount.getGraphics());
	}
	
	public void actionPerformed(ActionEvent e){
		if(e.getSource()==nextRound){
			playing = false;
			play.setEnabled(false);
			play.paint(play.getGraphics());
			pause.setEnabled(false);
			pause.paint(pause.getGraphics());
			try{
				eachFrame();
			}catch(IllegalStateException npe){System.out.println("I see dead people");}
			
			play.setEnabled(true);
		}
		if(e.getSource()==play){
			playing = true;
			play.setEnabled(false);
			nextRound.setEnabled(false);
			fpsSelector.setEnabled(false);
			pause.setEnabled(true);
			pause.paint(pause.getGraphics());
		}
		if(e.getSource()==pause){
			playing = false;
			nextRound.setEnabled(true);
			fpsSelector.setEnabled(true);
			play.setEnabled(true);
		}
		if(e.getSource()==fpsSelector){
			if(playing)
				return;
			if(fpsSelector.getSelectedIndex()>0)
				this.FPS = (Integer)fpsSelector.getSelectedItem();
		}
		if(e.getSource()==nextSoldier){
			playing = false;
		}
	}
	
	//~~~~~~WindowListener Stuff

	@Override
	public void windowActivated(WindowEvent e) {}
	@Override
	public void windowClosed(WindowEvent e) {}

	@Override
	public void windowClosing(WindowEvent e) {
		//need to kill the threads to avoid memory leaks
		player.interrupt();
		painter.interrupt();		
		System.out.println("Thanks for Playing");
		System.exit(0);
	}
	@Override
	public void windowDeactivated(WindowEvent e) {}
	@Override
	public void windowDeiconified(WindowEvent e) {}
	@Override
	public void windowIconified(WindowEvent e) {}
	@Override
	public void windowOpened(WindowEvent e) {}
	
	
}
