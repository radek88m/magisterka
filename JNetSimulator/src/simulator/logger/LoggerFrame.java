package simulator.logger;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

@SuppressWarnings("serial")
public class LoggerFrame extends JFrame {
	
	private JPanel mPanel = null;
	private JTextArea mTextArea;
	
	LoggerFrame() {
		super();
		initialize();
	}
	
	private void initialize() {
        this.setSize(new Dimension(600, 800));
        this.setContentPane(getJPanel());
        this.setTitle("Alarm System Logger");
	}
	
	private JPanel getJPanel() {
		if (mPanel == null) {
			mPanel = new JPanel();
			mPanel.setLayout(null);
			mPanel.setBackground(Color.YELLOW);
			
			mTextArea = new JTextArea();
			mTextArea.setEditable(false);
			mTextArea.setBounds(10, 11, 564, 740);			

	        JScrollPane scrolled = new JScrollPane(mTextArea);
	        scrolled.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
	        scrolled.setBounds(10, 11, 564, 740);
			
			mPanel.add(scrolled);
		}
		return mPanel;
	}
	
	public void addLog(String log){
		mTextArea.append(log);
		mTextArea.setCaretPosition(mTextArea.getDocument().getLength());
	}
	
	public void clearLogs(){
		mTextArea.setText("");
	}
}
