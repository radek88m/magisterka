package simulator.gui.logger;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

import simulator.gui.logger.Logger.ILoggerPrinter;
import java.awt.BorderLayout;

import javax.swing.JButton;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LogPanel extends JPanel implements ILoggerPrinter {
	
	private static final long serialVersionUID = 1L;
	private JTextArea mTextArea;
	private JButton mClearLogsBtn;
	
	public LogPanel() {
		super();
		initialize();
	}
	
	private void initialize() {
		setLayout(new BorderLayout(0, 0));
		mTextArea = new JTextArea();
		mTextArea.setEditable(false);
	    JScrollPane scrolled = new JScrollPane(mTextArea);
	    scrolled.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
	    scrolled.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	    this.add(scrolled,BorderLayout.CENTER);
	    
	    mClearLogsBtn = new JButton("Clear Logs");
	    mClearLogsBtn.setFont(new Font("Tahoma", Font.PLAIN, 13));
	    mClearLogsBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				clearLogs();
			}
		});
	    this.add(mClearLogsBtn, BorderLayout.PAGE_END);
	}
		
	public void addLog(String log) {
		mTextArea.append(log);
		mTextArea.setCaretPosition(mTextArea.getDocument().getLength());
	}
	
	public void clearLogs() {
		mTextArea.setText("");
	}

	@Override
	public void setPrinterTitle(String title) {
		// TODO Auto-generated method stub		
	}
}
