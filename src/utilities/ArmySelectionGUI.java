package utilities;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.*;

import soldiers.Soldier;

import board.GameBoard;

public class ArmySelectionGUI extends JFrame implements ActionListener, WindowListener{
	private JComboBox[] goodArmy, evilArmy;
	private JButton ready;
	private GameBoard parent;
	
	public ArmySelectionGUI( GameBoard gb){
		super("Select Your Armies!");
		parent = gb;
		
		JPanel goodPanel = new JPanel( new GridLayout(GameBoard.ROWS+1, 1) );
		JPanel evilPanel = new JPanel( new GridLayout(GameBoard.ROWS+1, 1) );
		goodPanel.add( new JLabel("Good Army"));
		evilPanel.add( new JLabel("Evil Army"));
		
		evilArmy = createBoxes( evilPanel );
		goodArmy = createBoxes( goodPanel);
		
		this.add(evilPanel, BorderLayout.WEST);
		this.add(goodPanel, BorderLayout.EAST);
		
		ready = new JButton("ENTER BATTLE");
		ready.addActionListener(this);
		this.add(ready, BorderLayout.SOUTH);		
		
		this.addWindowListener(this);
		this.setSize(200,GameBoard.ROWS*35+25);
		this.setLocation(parent.getX()+parent.getWidth()/2, parent.getY()+parent.getHeight()/2);
		this.setResizable(false);
		this.setVisible(true);
	}
	
	public JComboBox[] createBoxes( JPanel p){
		JComboBox[] boxes= new JComboBox[GameBoard.ROWS];
		for(int i=0; i<boxes.length; i++){
			boxes[i] = new JComboBox();
			for( int j=0; j<GameBoard.soldierTypes.length; j++)
				boxes[i].addItem( GameBoard.soldierTypes[j] );
			p.add(boxes[i]);
		}
		return boxes;
			
	}
	


	@Override
	public void actionPerformed(ActionEvent arg0) {
		for( int i=0; i<goodArmy.length; i++){
			Soldier s = GameBoard.makeSoldier( ""+goodArmy[i].getSelectedItem(), false );
			parent.getSquareAt(i, GameBoard.COLS-1).setSoldier( s );
		}
		for( int i=0; i<evilArmy.length; i++){
			Soldier s = GameBoard.makeSoldier( ""+evilArmy[i].getSelectedItem(), true );
			parent.getSquareAt(i, 0).setSoldier( s );
		}	
		this.dispose();
		parent.repaint();
		parent.updateCounts();
		
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
		// TODO Auto-generated method stub
		this.dispose();
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		// TODO Auto-generated method stub
		this.dispose();
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		this.dispose();
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		this.dispose();
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
