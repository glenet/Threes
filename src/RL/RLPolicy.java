package RL;

import java.util.HashMap;
import java.util.Random;

import engine.Board;

public class RLPolicy {
    private final int UP = 0;
    private final int DOWN = 1;
    private final int LEFT = 2;
    private final int RIGHT = 3;
    final double ALPHA = 0.1;
    final double GAMMA = 0.8;
    private static final int ITERATIONS = 100;
    
    public RLPolicy(Board init_game)
    {
        mInitBoard = init_game;
        mCurrentScore = 0;
        mMap = new HashMap<Integer, Board>();
        mAction = 0;
        episodes(init_game);
    }
    
    public void episodes(Board board)
    {
        for (int i = 0; i < ITERATIONS; i++) {
            Board status = getBoard(board);
            while (!status.isEndBoard()) {
                Board next_board = choseNexBored(status);
                mCurrentScore = status.getScore();
                double q = Q(status, mAction);
                double maxQ = MaxQ(next_board);
                int r = (int) R(status, mAction);
                double value = q + ALPHA * (r + GAMMA * maxQ - q);
                status.setQ(mAction, (int)value);
                addList(status);
                status = next_board;
            }
        }
    }
    
    private Board choseNexBored(Board status)
    {
        int [] rand = getRand();
        for (int i = 0; i < Board.MAX_ACTION; i++) {
            Board next_board = getNextBoard(status, rand[i]);
            if (next_board != null) {
                mAction = i;
                return next_board;
            }
        }

        return null;
    }

    private void addList(Board board)
    {
        int key = board.getHashCode();
        if (!mMap.containsKey(key))
            mMap.put(key, board);
    }
    
    private Board getNextBoard(Board status, int action)
    {
        Board next_board = status.nextBoard(Board.Int2Dir(action));
        if (next_board == null) {
            return null;
        }
        int key = next_board.getHashCode();
        if (mMap.containsKey(key))
            return mMap.get(key);
        return next_board;
    }
    
    private Board getBoard(Board board)
    {
        int key = board.getHashCode();
        if (mMap.containsKey(key))
            return mMap.get(key);
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
        if (board == null)
            return 0;

        double max1 = Math.max((double)board.getQ(UP), (double)board.getQ(DOWN));
        double max2 = Math.max((double)board.getQ(LEFT), (double)board.getQ(RIGHT));
    	
    	return max1 > max2 ? max1 : max2;
    }
    
    private double R(Board board, int action)
    {
        int reward = 0;
        int score = board.getScore(Board.Int2Dir(action));
        if(board.isEndBoard())
            reward -= 100;
        if (score > mCurrentScore)
            reward += 10;

        return reward;
    }

    private int[] getRand()
    {
        int[] rand = new int[4];
        for (int i = 0; i < Board.MAX_ACTION; i++) {
            rand[i] = i;
        }

        Random random = new Random();
        for (int i = 0; i < 100; i++) {
            int n1 = random.nextInt(Board.MAX_ACTION);
            int n2 = random.nextInt(Board.MAX_ACTION);
            int temp = rand[n1];
            rand[n1]= rand[n2];
            rand[n2] = temp;
        }

        return rand;
    }
    private int mCurrentScore;
    private int mAction;
    private HashMap<Integer, Board> mMap;
    private Board mInitBoard;
}
