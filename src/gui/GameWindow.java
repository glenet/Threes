package gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import engine.Game;
import engine.GridData.DIR;

public class GameWindow extends Game {

	public static final int GRID_LENGTH = 4;
	public static final int GRID_SPACE = 10;
	public static final int GRID_START_X = 10;
	public static final int GRID_START_Y = 10;
	private static final int BLOCKSIZE = 70;
	private static final int FONTSIZE = 24;
	private Canvas canvas;
	protected Shell shell;

	public GameWindow() {

	}

	/**
	 * Open the window.
	 */
	public void open() {
		final Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(750, 638);
		shell.setText("SWT Application");
		shell.setLayout(null);
		shell.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (isGameOver() != -1)
					return;
				if (e.keyCode == SWT.ARROW_UP) {
					insertDirection(DIR.UP);
				} else if (e.keyCode == SWT.ARROW_DOWN) {
					insertDirection(DIR.DOWN);
				} else if (e.keyCode == SWT.ARROW_LEFT) {
					insertDirection(DIR.LEFT);
				} else if (e.keyCode == SWT.ARROW_RIGHT) {
					insertDirection(DIR.RIGHT);
				}
				canvas.redraw();
			}
		});

		draw();
	}

	private void setFontColorByTile(GC gc, int tile) {
		Display display = Display.getDefault();
		Font font = new Font(display, "Arial", FONTSIZE, SWT.BOLD);
		gc.setForeground(display.getSystemColor(SWT.COLOR_WHITE));
		switch (tile) {
		case 0:
			gc.setBackground(new Color(display, 222, 247, 255));
			gc.setForeground(new Color(display, 222, 247, 255));
			break;
		case 1:
			gc.setBackground(new Color(display, 255, 101, 132));
			gc.setForeground(new Color(display, 255, 255, 255));
			break;
		case 2:
			gc.setBackground(new Color(display, 99, 202, 255));
			gc.setForeground(new Color(display, 255, 255, 255));
			break;
		case 3:
		case 6:
			gc.setBackground(new Color(display, 255, 255, 255));
			gc.setForeground(new Color(display, 90, 85, 82));
			break;
		default:
			gc.setBackground(new Color(display, 255, 255, 255));
			gc.setForeground(new Color(display, 239, 105, 132));
			break;
		}
		gc.setFont(font);
		font.dispose();
	}

	private void draw() {
		canvas = new Canvas(shell, SWT.NONE);
		canvas.setBounds(0, 0, 600, 600);
		canvas.setLayout(null);

		canvas.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				GC gc = new GC(canvas);
				FontMetrics fm = gc.getFontMetrics();
				Display display = Display.getDefault();
				Font font = new Font(display, "Arial", FONTSIZE, SWT.BOLD);
				gc.setFont(font);
				gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
				String scoreStr = "Score: " + Integer.toString(getScore());
				gc.drawText(scoreStr, 330, 20);
				String hintStr = "Hint: " + getHint();
				gc.drawText(hintStr, 330, 60);
				if (isGameOver() != -1) {
					String gameStr = "Game Over";
					gc.drawText(gameStr, 330, 100);
				}
				for (int i = 0; i < GRID_LENGTH; i++) {
					for (int j = 0; j < GRID_LENGTH; j++) {

						int number = grid.getTileByXY(i, j);
						setFontColorByTile(gc, number);
						int x = (j * (BLOCKSIZE + GRID_SPACE) + GRID_START_Y);
						int y = (i * (BLOCKSIZE + GRID_SPACE) + GRID_START_X);
						gc.fillRectangle(x, y, BLOCKSIZE, BLOCKSIZE);
						y += ((BLOCKSIZE - fm.getHeight()) / 2) - 7;
						int stringLength = 0;
						String numberStr = String.valueOf(number);
						for (int p = 0; p < numberStr.length(); p++) {
							stringLength += gc.getAdvanceWidth(numberStr
									.charAt(p));
						}
						x += ((BLOCKSIZE - stringLength) / 2);
						gc.drawText(numberStr, x, y, true);
					}
				}
				font.dispose();
				gc.dispose();
			}
		});
		canvas.redraw();
	}

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			GameWindow window = new GameWindow();
			window.open();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
