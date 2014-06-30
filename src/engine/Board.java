package RL;

import java.util.Random;

import engine.Game;
import engine.GridData;
import engine.GridData.DIR;

public class Board {
	public Board(Game game)
	{
		mCurrentGrid = game.getCurrentGrid();
		mQ = new int[4];
	}
	
	public Board(GridData grid)
	{
		mCurrentGrid = grid;
		mQ = new int[4];
	}
	
	public double getQ(int action)
	{
		return mQ[action];
	}
	
	public Board nextBoard(DIR action)
	{
		//TODO sanity check
	 	Random rand = new Random();
		GridData newGrid = null;
		try {
			newGrid = (GridData)mCurrentGrid.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		newGrid.shift(action);
		int nSlot = newGrid.getSlotNo();
		
		//XXX: i don't understand...
		if (nSlot != 0) {
			int randSlot = rand.nextInt(nSlot);
			newGrid.setSlot(randSlot, Integer.valueOf(newGrid.getHint()));
		}
		return new Board(newGrid);
	}
	
	public void setQ(int action, int value)
	{
		mQ[action] = value;
	}
	
	public int getScore(DIR action)
	{
		//Since the next move, we don't actually know the random value is.
		//So we list all the probabilities and get the average.
		
		GridData newGrid = null;
		GridData tmpGrid = null;
		int avrg = 0;
		try {
			newGrid = (GridData)mCurrentGrid.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		newGrid.shift(action);

		int nSlot = newGrid.getSlotNo();
		
		//XXX: i don't understand why
		if (nSlot == 0)
			return 0;
		for (int i =0; i < nSlot; i++) {
			try {
				tmpGrid = (GridData) newGrid.clone();
			} catch (CloneNotSupportedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			tmpGrid.setSlot(i, Integer.valueOf(newGrid.getHint()));
			avrg += tmpGrid.getScore();
		}
		return (int)avrg / nSlot;
	}
	
	public int getScore()
	{
		return mCurrentGrid.getScore();
	}
	
	public boolean isEndBoard()
	{
		return mCurrentGrid.isGameOver();
	}
	
	public boolean isEqual(Board board)
	{
		int[][] bData = board.rowData();
		int[][] myData = this.rowData();
		
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if (bData[i][j] != myData[i][j])
					return false;
			}
		}
		return true;
	}
	
	public int getAction()
	{
		//It's better to random if all values are the same
		int index = 0;
		for (int i = 1; i < mQ.length; i++) {
			if(mQ[i] > mQ[index])
				index = i;
		}
		
		return index;
	}
	public int[][] rowData()
	{
		int[][] data  = new int[4][4];
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++)
				data[i][j] = mCurrentGrid.getValue(i, j);
		}
		return data;
	}
	protected GridData mCurrentGrid;
	private int[] mQ;
}
