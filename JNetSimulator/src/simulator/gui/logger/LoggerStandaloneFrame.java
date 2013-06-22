package simulator.gui.logger;

import java.awt.Dimension;

import javax.swing.JFrame;

import simulator.gui.logger.Logger.ILoggerPrinter;

public class LoggerStandaloneFrame extends JFrame implements ILoggerPrinter {
	
	private LogPanel mLogPanel;
	
	public LoggerStandaloneFrame() {

		this.setSize(new Dimension(550, 600));
		mLogPanel = new LogPanel();
		this.add(mLogPanel);
	}

	private static final long serialVersionUID = 1L;

	@Override
	public void addLog(String log) {
		mLogPanel.addLog(log);
	}

	@Override
	public void clearLogs() {
		mLogPanel.clearLogs();
	}

	@Override
	public void setPrinterTitle(String title) {
		setTitle(title);
	}

}
