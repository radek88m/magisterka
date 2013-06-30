package simulator.gui;

import javax.swing.JFrame;

import simulator.gui.logger.LogPanel;
import simulator.gui.logger.Logger;
import simulator.tunnel.mediastream.TunnelStreamSettings;
import simulator.tunnel.signalling.SIPTunnel;
import simulator.tunnel.signalling.SIPTunnelConfig;
import javax.swing.JLabel;

import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.Timer;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JCheckBox;

public class SIPTunnelFrame extends JFrame {
	
	private static final long serialVersionUID = 1L;

	private SIPTunnelConfig mConfig;
	private SIPTunnel mTunnel;
	private JTextField mTimeTextField;
	private LogPanel mLoggerPanel;
	private Logger mLogger;
	
	private long mStartTime;
	private Timer mRunningTimer;


	public SIPTunnelFrame(SIPTunnelConfig config) {
		mLoggerPanel = new LogPanel();
		mLogger = new Logger(mLoggerPanel);
		mConfig = config;		
		mConfig.setTunnelStreamSettings(new TunnelStreamSettings());	
		
		initializeViews();
		
		startTunnel();
	}

	protected void handleSipLoggingOptionChanged(boolean enable) {
		mConfig.setTraceSipMessage(enable);
		reloadTunnelConfig();
	}

	protected void handleMediaFlowLoggingOptionChanged(boolean enable) {
		mConfig.setTraceMediaFlow(enable);
		reloadTunnelConfig();
	}

	protected void updateTimeSinceStart() {
		long timeLast = System.currentTimeMillis() - mStartTime;
		
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		c.setTimeInMillis(c.getTimeInMillis() + timeLast);
		DateFormat df = new SimpleDateFormat("HH:mm:ss");
		
		mTimeTextField.setText(df.format(c.getTime()));
	}

	private void startTunnel() {
		mTunnel = new SIPTunnel(mConfig, mLogger);
		mTunnel.startTunnel();
		mStartTime = System.currentTimeMillis();
		mRunningTimer = new Timer(1000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateTimeSinceStart();
			}
		});
		mRunningTimer.start();
	}
	
	private void stopTunnel() {
		if(mTunnel != null) {
			mTunnel.stopTunnel();
		}
		if(mRunningTimer != null){
			mRunningTimer.stop();
			mRunningTimer = null;
		}
	}

	private void reloadTunnelConfig() {
		if(mTunnel != null) {
			mTunnel.reloadConfig(mConfig);
		}
	}
	
	private void restart() {
		if(mLogger != null) mLogger.clearLogs();
		stopTunnel();
		startTunnel();
	}
	
	private void exit() {
		stopTunnel();
		System.exit(0);
	}
	
	public void handleSettingsChangeRequest() {
		MediaSettingsFrame settingsFrame = new MediaSettingsFrame(mConfig);
		settingsFrame.setLocationRelativeTo(null);
		settingsFrame.setVisible(true);
	}
	
	private void initializeViews() {
		getContentPane().setLayout(null);
		
		setTitle("SIP Proxy Simulator");
		this.setSize(new Dimension(900, 640));
		
		JCheckBox sipMessagesLoggingCheckBox = new JCheckBox("SIP Messages Logging");
		sipMessagesLoggingCheckBox.setFont(new Font("Tahoma", Font.PLAIN, 13));
		sipMessagesLoggingCheckBox.setBounds(10, 34, 170, 26);
		sipMessagesLoggingCheckBox.addItemListener(new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				boolean enable = arg0.getStateChange() == ItemEvent.SELECTED;
				handleSipLoggingOptionChanged(enable);
			}
		});
		sipMessagesLoggingCheckBox.setSelected(true);
		getContentPane().add(sipMessagesLoggingCheckBox);
		
		JCheckBox mediaHandlersCheckBox = new JCheckBox("Media Flow Logging");
		mediaHandlersCheckBox.setFont(new Font("Tahoma", Font.PLAIN, 13));
		mediaHandlersCheckBox.setBounds(10, 73, 170, 26);
		mediaHandlersCheckBox.addItemListener(new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				boolean enable = arg0.getStateChange() == ItemEvent.SELECTED;
				handleMediaFlowLoggingOptionChanged(enable);
			}
		});
		mediaHandlersCheckBox.setSelected(true);
		getContentPane().add(mediaHandlersCheckBox);
		
		JButton btnEditMediaSettings = new JButton("Edit Media Settings");
		btnEditMediaSettings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				handleSettingsChangeRequest();
			}
		});
		btnEditMediaSettings.setFont(new Font("Tahoma", Font.PLAIN, 13));
		btnEditMediaSettings.setBounds(10, 162, 170, 31);
		getContentPane().add(btnEditMediaSettings);
		
		JButton btnRestartSipProxy = new JButton("Restart SIP Proxy");
		btnRestartSipProxy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				restart();
			}
		});
		
		btnRestartSipProxy.setFont(new Font("Tahoma", Font.PLAIN, 13));
		btnRestartSipProxy.setBounds(10, 518, 170, 31);
		getContentPane().add(btnRestartSipProxy);
		
		JButton btnShutDown = new JButton("Shut down");
		btnShutDown.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exit();
			}
		});
		btnShutDown.setFont(new Font("Tahoma", Font.PLAIN, 13));
		btnShutDown.setBounds(10, 560, 170, 31);
		getContentPane().add(btnShutDown);
		
		mTimeTextField = new JTextField();
		mTimeTextField.setFont(new Font("Tahoma", Font.PLAIN, 13));
		mTimeTextField.setText("00:00:00");
		mTimeTextField.setHorizontalAlignment(SwingConstants.CENTER);
		mTimeTextField.setBounds(10, 277, 170, 26);
		getContentPane().add(mTimeTextField);
		mTimeTextField.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("Time since started:");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblNewLabel.setBounds(10, 249, 170, 26);
		getContentPane().add(lblNewLabel);
		
		mLoggerPanel.setBounds(188, 11, 686, 580);
		getContentPane().add(mLoggerPanel);
	}
}
