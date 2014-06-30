package engine;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.Random;

import engine.GridData.DIR;

/**
 * Initialize game record & reset game
 * 
 * @author yhchang
 * 
 */
public class Game {
	public static final int STAGE_NUM = 6;
	public static final int FIRST_STAGE = 192;
	public static final int ERROR_KEY = -1;
	protected GridData grid;
	private boolean gameOver = false;
	private int round = 0;
	private int score = 0;
	private int maxScore = 0;
	private int scoreSum = 0;
	private int maxTile = 0;
	private int passCnt[] = new int[STAGE_NUM];
	private int moveCnt = 0;

	private long startTime;
	private long endTime;

	private static final int RAND_MAX = 10;

	private Random rand;

	public Game() {
		this.round = 0;
		this.moveCnt = 0;
		this.score = 0;
		this.maxScore = 0;
		this.scoreSum = 0;
		this.maxTile = 0;
		gameOver = false;
		rand = new Random();
		grid = new GridData(rand);
		grid.setNextTile();
		for (int i = 0; i < GridData.INITIAL_TILE_NUM; i++)
			genInitTile();

		startTime = getCpuTime();
	}

	public void endGame() {
		endTime = getCpuTime();
		updateStats();
		dumpLog("open_src_version.log");
	}

	public void reset() {
		updateStats();
		grid.clear();
		gameOver = false;
		grid.setNextTile();
		for (int i = 0; i < GridData.INITIAL_TILE_NUM; i++)
			genInitTile();
	}

	private void updateStats() {
		round++;
		int score = grid.getScore();
		scoreSum += score;
		if (score > maxScore)
			maxScore = score;
		int maxGridTile = grid.getMaxTile();
		if (maxGridTile > maxTile)
			maxTile = maxGridTile;
		for (int i = 0; i < STAGE_NUM; i++) {
			if (maxGridTile >= (FIRST_STAGE << i))
				passCnt[i]++;
		}
	}

	// genInitTile()
	// Description: generate initial tile in random empty block
	private void genInitTile() {
		grid.generateInitTile();
	}

	// genInitTile()
	// Description: generate initial tile in random empty block
	private void genNewTile() {
		grid.generateNewTile();
	}

	/**
	 * return pseudo random integer value between "0 to RAND_MAX"
	 * 
	 * @return pseudo random integer value between "0 to RAND_MAX"
	 */
	public int getRand() {
		return rand.nextInt(RAND_MAX);
	}

	public void setGameOver() {
		gameOver = grid.isGameOver();
		score = grid.getScore();
	}

	/**
	 * check if game is over (cannot shift in any direction)
	 * 
	 * @return returns score if game is over, return -1 if not over
	 */
	public int isGameOver() {
		if (gameOver) {
			return score;
		} else {
			return -1;
		}
	}

	public String getHint() {
		return grid.getHint();

	}

	/**
	 * 
	 * 
	 * 	
	 */
	public int getScore() {
		return grid.getScore();
	}

	/**
	 * gets the highest score ever achieved
	 * 
	 * @return highest score
	 */
	public int getMaxScore() {
		return maxScore;
	}

	/**
	 * shift tiles in given direction and generate new tile randomly if cannot
	 * be shifted, will not generate new tile
	 * 
	 * @param dir
	 *            shift direction
	 * @return returns TRUE if shift was successful and tile was generated,
	 *         FALSE if not
	 */
	public boolean insertDirection(DIR dir) {
		if (!grid.shift(dir))
			return false;
		genNewTile();
		moveCnt++;
		setGameOver();
		return true;
	}

	/**
	 * copy contents of game grid into given grid
	 * 
	 * 
	 * @return return a cloned object
	 */
	public GridData getCurrentGrid() {
		GridData cloneObj = null;
		try {
			cloneObj = (GridData) grid.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cloneObj;
	}

	/**
	 * prints the game grid
	 * 
	 */
	public void printGrid() {
		grid.print();
	}

	public void dumpLog(String fileName) {
		FileWriter out = null;
		try {
			out = new FileWriter(fileName, true);
			out.write("#Rounds: " + round + '\n');
			out.write("Highest Score: " + maxScore + '\n');
			out.write("Average Score: " + ((double) scoreSum / round) + '\n');
			out.write("Max Tile: " + maxTile + '\n');
			for (int i = 0; i < STAGE_NUM; i++) {
				out.write(String.valueOf((FIRST_STAGE << i)));
				out.write(" Rate: " + ((double) (passCnt[i] * 100 / round))
						+ "%\n");
			}
			out.write("Move Count: " + moveCnt + '\n');
			out.write("Time: " + (endTime - startTime) + '\n');
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Get CPU time in nanoseconds.
	 * 
	 * @return return cpu time
	 */
	public static long getCpuTime() {
		ThreadMXBean bean = ManagementFactory.getThreadMXBean();
		return bean.isCurrentThreadCpuTimeSupported() ? bean
				.getCurrentThreadCpuTime() : 0L;
	}

}
