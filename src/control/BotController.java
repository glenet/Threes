package control;

import engine.Board;
import RL.RLPolicy;
import engine.Game;
import engine.GridData.DIR;

public class BotController implements GameController {
    private Game game;
	public BotController(Game game){
		this.game = game;
		mInitBoard = new Board(game.getCurrentGrid());
		mPolicy = new RLPolicy(mInitBoard);
		
		
	}
	@Override
	public DIR getDirection() {
		// TODO Please input your code here.
		mInitBoard = new Board(game.getCurrentGrid());
		mPolicy.episodes(mInitBoard);
		DIR dir = DIR.INVALID;
		dir = Board.Int2Dir(mInitBoard.getAction());
		return dir;
	}
	
	private Board mInitBoard;
	private RLPolicy mPolicy;
}
