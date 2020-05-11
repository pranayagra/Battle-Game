package utilities;
import board.GameBoard;


public class PlayThread extends Thread{
	private GameBoard board;
	public PlayThread( GameBoard gb){
		board = gb;
	}

	@Override
	public void run() {
		while(!Thread.currentThread().interrupted()){
			
			try{
				if(board.isPlaying()){
					board.eachFrame();
				}
				Thread.sleep(10);
			}
			catch( InterruptedException ie){
				
			}
			catch(NullPointerException np){
				np.printStackTrace();				
			}
			catch(Exception ex){ex.printStackTrace();}
		}
	}

}
