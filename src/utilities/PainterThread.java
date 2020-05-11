package utilities;
import board.GameBoard;
import board.Square;


public class PainterThread extends Thread{
	private Square[][] array;

	public PainterThread(Square[][] s){
		array=s;
	}

	public void run() {
		while(!Thread.currentThread().interrupted()){

			try{
				for(int r=0; r<array.length; r++)
					for(int c=0; c<array[r].length; c++)
						array[r][c].paint(array[r][c].getGraphics());
				Thread.sleep( (int)(1000.0/GameBoard.FPS) );
			}
			catch( InterruptedException ex){
				
			}
			catch(NullPointerException np){
				np.printStackTrace();
			}
			catch(Exception ex){ex.printStackTrace();}
		}
	}
}
