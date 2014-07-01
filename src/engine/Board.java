package engine;

import java.util.ArrayList;
import java.util.Arrays;

import engine.GridData;
import engine.GridData.DIR;

public class Board {

    public final static int MAX_ACTION = 4;

    @SuppressWarnings("serial")
    public final static ArrayList<engine.GridData.DIR> sList = new ArrayList<engine.GridData.DIR>() {
        {
            add(engine.GridData.DIR.UP);
            add(engine.GridData.DIR.DOWN);
            add(engine.GridData.DIR.LEFT);
            add(engine.GridData.DIR.RIGHT);
            add(engine.GridData.DIR.INVALID);
        }
    };
    public Board(GridData grid)
    {
        mCurrentGrid = grid;
        mQ = new int[MAX_ACTION];
        mKey = Arrays.hashCode(rowData());
    }
	
    public double getQ(int action)
    {
        assert(action < MAX_ACTION);
        return mQ[action];
    }
	
    public Board nextBoard(DIR action)
    {
        GridData newGrid = null;
        try {
            newGrid = (GridData)mCurrentGrid.clone();
        } catch (CloneNotSupportedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (!newGrid.shift(action))
            return null;
        newGrid.generateNewTile();

		return new Board(newGrid);
	}
	
	public void setQ(int action, int value)
	{
	    assert(action < MAX_ACTION);
	    mQ[action] = value;
	}
	
	public int getScore(DIR action)
	{
	    //Since the next move, we don't actually know the random value is.
	    //So we list all the probabilities and get the average.
	    assert(Dir2Int(action) < MAX_ACTION);
		
	    GridData newGrid = null;
	    GridData tmpGrid = null;
	    int avrg = 0;
	    try {
			newGrid = (GridData)mCurrentGrid.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	    if (!newGrid.shift(action))
	        return -1;

	    int nSlot = newGrid.getSlotNo();
		
	    for (int i = 0; i < nSlot; i++) {
	        try {
	            tmpGrid = (GridData) newGrid.clone();
	        } catch (CloneNotSupportedException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	        tmpGrid.setSlot(i, newGrid.getNextTile());
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
	    return Arrays.equals(board.rowData(), this.rowData());
	}
	
	public int getAction()
	{
	    //TODO:It's better to random if all values are the same
	    int index = 0;
	    for (int i = 1; i < mQ.length; i++) {
	        if(mQ[i] > mQ[index])
	            index = i;
	    }
		
	    return index;
	}

	public int[] rowData()
	{
	    int data[] = new int[GridData.GRID_SIZE];
	    for (int i = 0; i < GridData.GRID_SIZE; i++)
	        data[i] = mCurrentGrid.getValue(i);
	    return data;
	}

	public int getHashCode()
	{
	    return mKey;
	}

	public void print()
	{
	    mCurrentGrid.print();
	}

	public void printQ()
	{
	    System.out.println("Q: "+ mQ[0] +" "  +mQ[1] +" " + mQ[2] + " " + mQ[3]);
	}

	public static DIR Int2Dir(int action)
	{
	    assert(action < Board.sList.size());
	    return Board.sList.get(action);
	}

	public static int Dir2Int(DIR action)
	{
	    return Board.sList.indexOf(action);
	}

	protected GridData mCurrentGrid;
	private int[] mQ;
	private int mKey;
}
