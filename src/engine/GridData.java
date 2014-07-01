package engine;

import java.util.Random;

public class GridData implements Cloneable {

	public static final int GRID_LENGTH = 4;
	public static final int GRID_SIZE = GRID_LENGTH * GRID_LENGTH;
	public static final int INITIAL_TILE_NUM = 9;
	public static final int WHITE_BASE = 3;
	public static final int BONUS_BASE = 6;
	public static final int BONUS_THRESHOLD = 48;
	public static final int BONUS_RATIO = 21;
	public static final int BASIC_TYPE_NUM = 3;
	public static final int WHITE_TYPE_NUM = 12;
	public static final int BAG_SIZE = 12;

	public static final int DIR_NUM = 4;
	public static final int EMPTY = 0;

	public static final int ERROR_KEY = -1;

	private Random random;
	private int emptyBlock = 0;
	private int maxTile = 0;
	private int nSlot = 0;
	private int slot[] = new int[GRID_LENGTH];
	private int grid[] = new int[GRID_SIZE];
	private int grabBag[] = new int[BASIC_TYPE_NUM];

	private int nextTile = 0;

	public enum DIR {
		LEFT, DOWN, RIGHT, UP, INVALID
	}

	public GridData(Random random) {
		this.random = random;
		clear();

	}

	/*************************************************
	 * Math
	 *************************************************/
	// _pow3[i] = 3^(i+1) for i = 0 ~ (WHITE_TYPE_NUM - 1)
	private int _pow3[] = new int[] { 3, 9, 27, 81, 243, 729, 2187, 6561,
			19683, 59049, 177147, 531441 };

	/**
	 * base 2 log function for positive integers
	 * 
	 * @param bits
	 *            positive integer
	 * @return logarithm of bits with respect to base 2
	 */
	public static int log2(int bits) {
		assert (bits > 0);
		return 31 - Integer.numberOfLeadingZeros(bits);
	}

	/**
	 * returns entry value specified by (index) coordinates
	 * 
	 * @param index
	 *            index of entry
	 * @return entry value specified by index
	 */
	public int getValue(int index) {
		if (index < 0 || index > GRID_SIZE - 1) {
			assert (false);
			return -1;
		}
		return grid[index];
	}

	/**
	 * returns entry value specified by (row, col) coordinates
	 * 
	 * @param row
	 *            row number of entry
	 * @param col
	 *            column number 0f entry
	 * @return entry value specified by (row, col) coordinates
	 */
	public int getValue(int row, int col) {
		int index = row * GRID_LENGTH + col;
		if (row < 0 || row > GRID_LENGTH - 1 || col < 0
				|| col > GRID_LENGTH - 1) {

			assert (false);
			return ERROR_KEY;
		}
		return getValue(index);
	}

	/**
	 * sets all grid entries to EMPTY
	 * 
	 */
	public void clear() {
		for (int i = 0; i < GRID_SIZE; i++) {
			grid[i] = 0;
		}
		emptyBlock = GRID_SIZE;
		maxTile = EMPTY;
		nSlot = 0;
		resetGrabBag();
	}

	/**
	 * copies contents of given grid into this grid
	 * 
	 * @return clone object
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		GridData gridData = (GridData) super.clone();
		gridData.grid = this.grid.clone();
		gridData.slot = this.slot.clone();
		return gridData;
	}

	/**
	 * sets value of specified block
	 * 
	 * @param index
	 *            index of entry
	 * @param val
	 *            value 0f entry
	 */
	public void setBlock(int index, int val) {
		if (index < 0 || index > GRID_SIZE - 1) {
			assert (false);
			return;
		}
		if (grid[index] == 0 && val != 0) {
			emptyBlock--;
		} else if (grid[index] != 0 && val == 0) {
			emptyBlock++;
		}
		grid[index] = val;
		if (val > maxTile)
			maxTile = val;
	}

	/**
	 * sets value of specified slot
	 * 
	 * @param num
	 *            number of slot
	 * @param val
	 *            specified value that will be assigned to slot
	 * 
	 * @return TRUE if slot is successfully set, FALSE if given slot number is
	 *         invalid
	 */
	public boolean setSlot(int num, int val) {
		assert (num < nSlot);
		if (num < nSlot) {
			grid[slot[num]] = val;
			emptyBlock--;
			nSlot = 0;
			return true;
		}
		return false;
	}

	/**
	 * returns TRUE if given tiles can be merged, FALSE if not
	 * 
	 * @param a
	 *            first tile
	 * @param b
	 *            second tile
	 * 
	 * @return TRUE if given tiles can be merged, FALSE if not
	 */

	public boolean canMerge(int a, int b) {
		if (a == 6144 || b == 6144)
			return false;
		return (a > 2 && a == b) || (a == 1 && b == 2) || (a == 2 && b == 1);
	}

	/**
	 * return number of empty blocks in grid
	 * 
	 * @return number of empty blocks in given grid
	 */
	public int getEmptyBlkNo() {
		return emptyBlock;
	}

	/**
	 * return number of slots that can take a new tile after shift
	 * 
	 * @return number of slots
	 */
	public int getSlotNo() {
		return nSlot;
	}

	/**
	 * return index of slot specified by number
	 * 
	 * @return index
	 */
	public int getSlotIndex(int num) {
		if (num < 0 || num >= nSlot) {
			assert (false);
			return ERROR_KEY;
		}
		return slot[num];
	}

	/**
	 * calculate and return score (only white tiles count)
	 * 
	 * @return current score
	 */
	public int getScore() {
		int score = 0;
		for (int i = 0; i < GRID_SIZE; i++) {
			if (grid[i] < WHITE_BASE)
				continue;
			assert (log2(grid[i] / WHITE_BASE) > -1);
			assert (log2(grid[i] / WHITE_BASE) < WHITE_TYPE_NUM);
			score += _pow3[log2(grid[i] / WHITE_BASE)];
		}
		return score;
	}

	/**
	 * get entry of original grid
	 * 
	 * @param row
	 *            row number of entry
	 * @param col
	 *            column number of entry current score in grid
	 * @return entry of original grid
	 */
	private int getEntry(int row, int col) {
		return grid[row * GRID_LENGTH + col];
	}

	private int getEntryIndex(int row, int col) {
		return row * GRID_LENGTH + col;
	}

	private void setEntry(int row, int col, int value) {
		grid[row * GRID_LENGTH + col] = value;
	}

	/**
	 * get entry of horizontally flipped grid
	 * 
	 * @param row
	 *            row number of entry
	 * @param col
	 *            column number of entry current score in grid
	 * @return entry of horizontally flipped grid
	 */
	private int getFlipEntry(int row, int col) {
		return grid[row * GRID_LENGTH + (GRID_LENGTH - 1 - col)];
	}

	private int getFlipEntryIndex(int row, int col) {
		return row * GRID_LENGTH + (GRID_LENGTH - 1 - col);
	}

	private void setFlipEntry(int row, int col, int value) {
		grid[row * GRID_LENGTH + (GRID_LENGTH - 1 - col)] = value;
	}

	/**
	 * get entry of transposed grid
	 * 
	 * @param row
	 *            row number of entry
	 * @param col
	 *            column number of entry current score in grid
	 * @return entry of transposed grid
	 */
	private int getTransEntry(int row, int col) {
		return grid[col * GRID_LENGTH + row];
	}

	private int getTransEntryIndex(int row, int col) {
		return col * GRID_LENGTH + row;
	}

	private void setTransEntry(int row, int col, int value) {
		grid[col * GRID_LENGTH + row] = value;
	}

	/**
	 * get entry of horizontally flipped, transposed grid
	 * 
	 * @param row
	 *            row number of entry
	 * @param col
	 *            column number of entry current score in grid
	 * @return entry of horizontally flipped, transposed grid
	 */
	private int getFlipTransEntry(int row, int col) {
		return grid[(GRID_LENGTH - 1 - col) * GRID_LENGTH + row];
	}

	private int getFlipTransEntryIndex(int row, int col) {
		return (GRID_LENGTH - 1 - col) * GRID_LENGTH + row;
	}

	private void setFlipTransEntry(int row, int col, int value) {
		grid[(GRID_LENGTH - 1 - col) * GRID_LENGTH + row] = value;
	}

	private int getDirEntry(DIR dir, int row, int col) {
		int value = 0;
		if (dir == DIR.LEFT)
			value = getEntry(row, col);
		else if (dir == DIR.DOWN)
			value = getFlipTransEntry(row, col);
		else if (dir == DIR.RIGHT)
			value = getFlipEntry(row, col);
		else
			value = getTransEntry(row, col);
		return value;
	}

	private int getDirIndex(DIR dir, int row, int col) {
		int value = 0;
		if (dir == DIR.LEFT)
			value = getEntryIndex(row, col);
		else if (dir == DIR.DOWN)
			value = getFlipTransEntryIndex(row, col);
		else if (dir == DIR.RIGHT)
			value = getFlipEntryIndex(row, col);
		else
			value = getTransEntryIndex(row, col);
		return value;
	}

	private void setDirEntry(DIR dir, int row, int col, int value) {
		if (dir == DIR.LEFT)
			setEntry(row, col, value);
		else if (dir == DIR.DOWN)
			setFlipTransEntry(row, col, value);
		else if (dir == DIR.RIGHT)
			setFlipEntry(row, col, value);
		else
			setTransEntry(row, col, value);

	}

	/**
	 * shift tiles in given direction
	 * 
	 * @param dir
	 *            shift direction
	 * @return returns TRUE if shift is possible, FALSE if not
	 */
	public boolean shift(DIR dir) {
		nSlot = 0;
		boolean isShifted = false;

		for (int i = 0; i < GRID_LENGTH; i++) {
			isShifted = false;
			for (int j = 1; j < GRID_LENGTH; j++) {
				if (getDirEntry(dir, i, j) == EMPTY)
					continue;
				if (getDirEntry(dir, i, j - 1) == EMPTY) {
					setDirEntry(dir, i, j - 1, getDirEntry(dir, i, j));
					setDirEntry(dir, i, j, EMPTY);
					isShifted = true;
				} else if (canMerge(getDirEntry(dir, i, j),
						getDirEntry(dir, i, j - 1))) {
					setDirEntry(
							dir,
							i,
							j - 1,
							(getDirEntry(dir, i, j - 1) + getDirEntry(dir, i, j)));
					if (getDirEntry(dir, i, j - 1) > maxTile) {
						maxTile = getDirEntry(dir, i, j - 1);
					}
					setDirEntry(dir, i, j, EMPTY);
					isShifted = true;
					emptyBlock++;
				}
			}
			if (isShifted == true) {
				slot[nSlot] = getDirIndex(dir, i, GRID_LENGTH - 1);
				nSlot++;
			}
		}
		if (nSlot > 0)
			return true;
		else
			return false;
	}

	/**
	 * return largest tile number in grid
	 * 
	 * @return largest tile number in grid
	 */
	public int getMaxTile() {
		return maxTile;
	}

	protected void print() {
		StringBuffer tilePad = new StringBuffer();
		for (int i = 0; i < GRID_LENGTH; i++) {
			for (int j = 0; j < GRID_LENGTH; j++) {
				tilePad.append(grid[i * GRID_LENGTH + j] + "    ");
			}
			tilePad.append("\n\n");
		}
		System.out.println(tilePad.toString());
		System.out.println("Hint: " + getHint());
	}

	public int getTileByXY(int x, int y) {
		int index = getIndex(x, y);
		return grid[index];

	}

	public int getIndex(int row, int column) {
		return row * GRID_LENGTH + column;
	}

	public boolean isGameOver() {
		if (emptyBlock > 0) {
			return false;
		}
		for (int i = 0; i < GRID_LENGTH; i++) {
			for (int j = 0; j < GRID_LENGTH; j++) {
				if ((i < GRID_LENGTH - 1)
						&& canMerge(getTileByXY(i, j), getTileByXY(i + 1, j))) {
					return false;
				}
				if ((j < GRID_LENGTH - 1)
						&& canMerge(getTileByXY(i, j), getTileByXY(i, j + 1))) {

					return false;
				}
			}
		}
		return true;

	}

	//
	protected void generateNewTile() {
		int nSlot = getSlotNo();
		int randSlot = getRandInt(nSlot);
		boolean success = setSlot(randSlot, getNextTile());
		assert (success);
		setNextTile();
	}

	//
	private int getRandInt(int number) {
		return random.nextInt(number);
	}

	protected void generateInitTile() {
		int randBlk = getRandInt(getEmptyBlkNo()) + 1;
		for (int i = 0; i < GRID_SIZE; i++) {
			if (grid[i] == EMPTY)
				randBlk--;
			if (randBlk == 0) {
				setBlock(i, getNextTile());
				setNextTile();
				return;
			}
		}
		assert (false);

	}

	/**
	 * get the next pre-calculated tile
	 * 
	 * @return next pre-calculated tile
	 */
	protected int getNextTile() {
		return nextTile;
	}

	protected void setNextTile() {
		int maxTile = getMaxTile();
		if (maxTile >= BONUS_THRESHOLD
				&& getRandInt(BONUS_RATIO) == BONUS_RATIO - 1) {
			int n = getRandInt(log2(maxTile / BONUS_THRESHOLD) + 1);
			nextTile = (BONUS_BASE << n);
		} else {
			int tileType;
			int nTile = grabBag[0] + grabBag[1] + grabBag[2];
			assert (nTile > 0);
			int randTile = getRandInt(nTile);
			if (randTile < grabBag[0]) {
				grabBag[0]--;
				tileType = 1;
			} else if (randTile < grabBag[0] + grabBag[1]) {
				grabBag[1]--;
				tileType = 2;
			} else {
				grabBag[2]--;
				tileType = 3;
			}
			if (nTile == 1)
				resetGrabBag();
			nextTile = tileType;
		}
	}

	public String getHint() {
		if (nextTile < BONUS_BASE)
			return String.valueOf(nextTile);
		return "+";
	}

	/**
	 * load grab bag with basic tiles equally
	 * 
	 */
	private void resetGrabBag() {
		grabBag[0] = grabBag[1] = grabBag[2] = BAG_SIZE / BASIC_TYPE_NUM;
	}

}
