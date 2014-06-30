package RL;

import java.util.ArrayList;
import java.util.Random;

import engine.GridData.DIR;

public class RLPolicy {
	private final int ACTION = 4; //scroll up, down, left ,right
	private final int UP = 0;
	private final int DOWN = 1;
	private final int LEFT = 2;
	private final int RIGHT = 3;
	final double ALPHA = 0.1;
    final double GAMMA = 0.8;
    private static final int ITERATIONS = 1;
    
    public RLPolicy(Board init_game)
    {
    	mInitBoard = init_game;
    	mCurrentScore = 0;
    	mList = new ArrayList<Board>();
    	episodes(init_game);
    }
    
    public void episodes(Board board)
    {
    	Board status = getBoard(board);
    	Random rand = new Random();
    	for (int i = 0; i < ITERATIONS; i++) {
    		while (!status.isEndBoard()) {
    			int next_status = rand.nextInt(ACTION);
    			Board next_board = getNextBoard(status, next_status);
    			mCurrentScore = status.getScore();
    			double q = Q(status, next_status);
    			double maxQ = MaxQ(next_board);
    			int r = (int) R(status, next_status);
    			double value = q + ALPHA * (r + GAMMA * maxQ - q);
    			status.setQ(next_status, (int)value);
    			addList(status);
    			status = next_board;
    		}
    	}	
    }
    
    private void addList(Board board)
    {
    	for (int i = 0; i < mList.size(); i++) {
    		Board tmp = mList.get(i);
    		if (tmp.isEqual(board)) {
    			return;
    		}
    	}
    	mList.add(board);
    }
    
    private Board getNextBoard(Board status, int action)
    {
    	Board next_board = status.nextBoard(valueof(action));
    	for (int i = 0; i < mList.size(); i++) {
    		Board tmp = mList.get(i);
    		if (tmp.isEqual(status)) {
    			return tmp;
    		}
    	}
    	return next_board;
    }
    
    private Board getBoard(Board board)
    {
    	for (int i = 0; i < mList.size(); i++) {
    		Board tmp = mList.get(i);
    		if (tmp.isEqual(board)) {
    			return tmp;
    		}
    	}
    	return board;
    }
    public int getAction()
    {
    	return mInitBoard.getAction();
    }
    
    private double Q(Board board, int action)
    {
    	return board.getQ(action);
    }
    
    private double MaxQ(Board board)
    {
    	double max1 = Math.max((double)board.getQ(UP), (double)board.getQ(DOWN));
    	double max2 = Math.max((double)board.getQ(LEFT), (double)board.getQ(RIGHT));
    	
    	return max1 > max2 ? max1 : max2;
    }
    
    private double R(Board board, int action)
    {
    	int reward = 0;
    	if(board.isEndBoard())
    		reward -= 100;
    	if (board.getScore(valueof(action)) > mCurrentScore)
    		reward += 10;
    	
    	return reward;
    }
    
    private DIR valueof(int action)
    {
    	switch (action) {
    	case 0:
    		return engine.GridData.DIR.UP;
    	case 1:
    		return engine.GridData.DIR.DOWN;
    	case 2:
    		return engine.GridData.DIR.LEFT;
    	case 3:
    		return engine.GridData.DIR.RIGHT;
    	default:
    		return engine.GridData.DIR.UP;
    	}
    }
    private int mCurrentScore;
    private ArrayList<Board> mList;
    private Board mInitBoard;
}
