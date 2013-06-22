package simulator;

import simulator.gui.StartFrame;

public class Main {

	public static void main(String[] args) {
		StartFrame startFrame = new StartFrame();
		startFrame.setDefaultCloseOperation(StartFrame.EXIT_ON_CLOSE);
		startFrame.setLocationRelativeTo(null);
		startFrame.setVisible(true);
	}

}
