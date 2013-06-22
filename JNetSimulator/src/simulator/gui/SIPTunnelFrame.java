package simulator.gui;

import javax.swing.JFrame;

import simulator.tunnel.mediastream.TunnelStreamSettings;
import simulator.tunnel.signalling.SIPTunnel;
import simulator.tunnel.signalling.SIPTunnelConfig;

public class SIPTunnelFrame extends JFrame {
	
	private static final long serialVersionUID = 1L;

	private SIPTunnelConfig mConfig;
	private SIPTunnel mTunnel;

	public SIPTunnelFrame(SIPTunnelConfig config) {
		mConfig = config;
		
		mConfig.setTunnelStreamSettings(new TunnelStreamSettings());		
		initializeViews();
		startTunnel();
	}

	private void startTunnel() {
		mTunnel = new SIPTunnel(mConfig);
		mTunnel.startTunnel();
	}
	
	private void stopTunnel() {
		if(mTunnel != null) {
			mTunnel.stopTunnel();
		}
	}
	
	private void restart() {
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
		
	}

}
