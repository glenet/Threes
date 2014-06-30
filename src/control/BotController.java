package control;

import RL.Board;
import RL.RLPolicy;
import engine.Game;
import engine.GridData.DIR;

public class BotController implements GameController {
    private Game game;
	public BotController(Game game){
		this.game = game;
		mInitBoard = new Board(game);
		mPolicy = new RLPolicy(mInitBoard);
		
		
	}
	@Override
	public DIR getDirection() {
		// TODO Please input your code here.
		mPolicy.episodes(new Board(game.getCurrentGrid()));
		DIR dir = DIR.INVALID;
		switch (mInitBoard.getAction()) {
		case 0:
			dir = DIR.UP;
			break;
		case 1:
			dir = DIR.DOWN;
			break;
		case 2:
			dir = DIR.LEFT;
			break;
		case 3:
			dir = DIR.RIGHT;
			break;
		default:
			break;
		}
		return dir;
	}
	
	private Board mInitBoard;
	private RLPolicy mPolicy;
}
