package mpk_games;

import java.awt.GridLayout;

import javax.swing.JFrame;

public class SnakeGameDriver {

	public static void main(String[] args) {
		
		SnakeGame game = new SnakeGame();
		
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Snake Game");
		
		frame.addKeyListener(game.panel);
		
		frame.setLayout(new GridLayout(1,1));
		frame.add(game.panel);
//		frame.add(game.moves_per_second.slider);
		frame.setSize(616,640);  ////HACK
		frame.setVisible(true);	
		
		game.run();
		
	}

}
