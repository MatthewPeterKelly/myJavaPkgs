package mpk_games;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedList;

import javax.swing.JPanel;

import mpk_gui.IO_Double;

public class SnakeGame {

	private final int nRows = 20;
	private final int nCols = 20;

	public IO_Double moves_per_second = new IO_Double(0.2,5,10,"Moves per Second");

	
	
	private boolean torusWorld = false;
	
	

	public SnakeGUI panel;

	private Snake snake;
	private Apple apple;

	private Direction direction = Direction.DOWN;  // Which way the snake is travelling now

	private boolean pause;	
	private boolean alive;

	public SnakeGame(){
		snake = new Snake();
		apple = new Apple();
		panel = new SnakeGUI();
	}

	public void run(){
		alive = true;
		while (alive) {
			if (!pause) update();
			try {
				Thread.sleep((long) (1000/(moves_per_second.get())));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void setDirection(Direction d) {
		if (!direction.opposite(d)){ // Only allow orthogonal turns
			direction = d;
		}
	}

	private void update(){
		/// Compute next cell
		int dRow = direction.dRow();
		int dCol = direction.dCol();
		int newRow = snake.getHead().row + dRow;
		int newCol = snake.getHead().col + dCol;

		if (torusWorld) { // Apply torroidal wrapping
			if (newRow >= nRows) newRow -= nRows;
			if (newRow < 0) newRow += nRows;
			if (newCol >= nCols) newCol -= nCols;
			if (newCol < 0) newCol += nCols;
		}

		Cell newCell = new Cell(newRow,newCol);
		if (!newCell.isValid()){ // out of range - you loose! 
			looseGame();
		} else if(snake.checkSnake(newCell)){
			looseGame();
		} else { // Move the snake and check for apples
			if (apple.checkCell(newCell)){ // Ate an apple!
				snake.addCell(newCell);
				apple.randomPosition();
			} else {
				snake.addCell(newCell);
				snake.removeTail();
			}
		}
		panel.repaint();
	}


	private void looseGame() {
		System.out.println("You Loose!");
		alive = false;

	}


	/** ***************************************************
	 A enum for selecting direction of travel 	   */
	private enum Direction {
		UP,
		DOWN,
		LEFT,
		RIGHT;

		private boolean opposite(Direction d){
			switch (d) {
			case UP: return this==Direction.DOWN;
			case DOWN: return this==Direction.UP;
			case LEFT: return this==Direction.RIGHT;
			case RIGHT: return this==Direction.LEFT;
			default: return false;
			}
		}

		private int dRow(){
			switch (this){
			case LEFT: return -1;
			case RIGHT: return 1;
			default: return 0;
			}
		}

		private int dCol(){
			switch (this){
			case UP: return -1;
			case DOWN: return 1;
			default: return 0;
			}
		}
	}


	/** **********************************************************
	A cell (used for pixels and objects in the game)             */ 
	private class Cell {
		protected int row;
		protected int col;

		public Color color = Color.BLACK;

		private Cell(int row, int col){
			this.row = row;
			this.col = col;
		}

		public boolean isValid() {
			return ((row>=0) && (row<nRows)) &&
					((col>=0) && (col<nCols));
		}

		/** @return true if the cells overlap */
		public boolean checkCell(Cell c){
			return (row == c.row) && (col == c.col);
		}

		public void display(Graphics g){
			int cellSize = panel.cellSize;  // Number of pixels for each cell
			int x = (row)*cellSize;
			int y = (col)*cellSize;
			g.setColor(color);
			//			g.fillRect(x, y, cellSize, cellSize);
			g.fillOval(x, y, cellSize,  cellSize);
		}

	}


	/** ****************************************************
	An inner class for holding information about the snake */
	private class Snake {

		private double initLength = 5;  //

		private Color headColor = new Color(0.3f, 0.5f, 0.6f);
		private Color snakeColor = new Color(0.3f,0.8f,0.3f);

		private LinkedList<Cell> body = new LinkedList<Cell>();	

		private Snake(){

			/// Create the body of the snake
			int row, col;
			Cell cell;
			for (int i = 0;  i<initLength; i++){
				col = i/nCols;
				row = i - col*nCols;
				cell = new Cell(row,col);
				cell.color = snakeColor;
				addCell(cell);
			}

		}

		private Cell getHead(){
			return body.getLast();
		}

		private void addCell(Cell c){
			body.add(c);
		}

		private void removeTail(){
			body.removeFirst();
		}

		/** @return true if the argument cell overlaps with the snake */
		private boolean checkSnake(Cell c){
			for (Cell b : body){
				if (b.checkCell(c)) return true;
			}
			return false;
		}

		public void display(Graphics g) {
			for(Cell c : body){
				if (c == snake.getHead()){
					c.color = headColor;
				} else {
					c.color = snakeColor;
				}
				c.display(g);
			}
		}

	}

	/** ***********************************************************
	An inner class for the apple that the snke is searching for */
	private class Apple extends Cell{

		Color appleColor = new Color(0.8f, 0.3f, 0.3f);

		private  Apple(){
			super(0,0);
			randomPosition(); //Randomly pick a new location
			color = appleColor;
		}

		/** move the apple to a random new position */
		private void randomPosition(){
			while (snake.checkSnake(this)){
				row = (int) (Math.random()*nRows);
				col = (int) (Math.random()*nCols);
			}
		}

	}


	/** ****************************************************
	An inner class for displaying the current state of the game board */ 
	public class SnakeGUI extends JPanel implements KeyListener{

		private Color backgroundColor = new Color(0.8f, 0.8f, 0.8f);

		public int width,height,cellSize;

		private SnakeGUI(){
			super();
		}

		/** Drawing starts here */
		public void paintComponent(Graphics g){

			super.paintComponent(g);

			/// Compute stuff for adjustable window size
			width = getWidth();
			height = getHeight();
			double rowCellSize = ((double) height)/((double) nRows);
			double colCellSize = ((double) width)/((double) nCols);
			cellSize = (int) Math.min(rowCellSize, colCellSize);

			/// Draw a border around the game board.
			int xMax, yMax;
			for (int inset = 0; inset < 5; inset++){
				xMax = cellSize*nCols - inset;
				yMax = cellSize*nRows - inset;
				g.drawLine(inset, inset, inset, yMax);
				g.drawLine(inset, yMax, xMax,yMax);
				g.drawLine(xMax,yMax, xMax, inset);
				g.drawLine(xMax,inset, inset, inset);
			}
			snake.display(g);
			apple.display(g);

		}




		@Override
		public void keyPressed(KeyEvent e) {
			switch (e.getKeyCode()){
			case KeyEvent.VK_UP: setDirection(Direction.UP); break; 
			case KeyEvent.VK_W: setDirection(Direction.UP);	break;
			case  KeyEvent.VK_LEFT:	setDirection(Direction.LEFT); break;
			case KeyEvent.VK_A: setDirection(Direction.LEFT);	break;
			case KeyEvent.VK_RIGHT: setDirection(Direction.RIGHT); break;
			case KeyEvent.VK_D: setDirection(Direction.RIGHT);	break;
			case KeyEvent.VK_DOWN: setDirection(Direction.DOWN); break;
			case KeyEvent.VK_S: setDirection(Direction.DOWN);	break;
			case KeyEvent.VK_SPACE:	pause = !pause;
			break;
			}
		}


		@Override
		public void keyReleased(KeyEvent e) {}


		@Override
		public void keyTyped(KeyEvent e) {}

	}
}
