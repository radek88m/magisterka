package simulator.tunnel.signalling;

import java.net.DatagramPacket;

import simulator.tunnel.mediastream.TunnelStreamManager;

public class SIPInviteSessionHandler {
	
	SIPTunnelConfig mConfig;
	TunnelStreamManager mManager;

	public SIPInviteSessionHandler(SIPTunnelConfig config, TunnelStreamManager manager) {
		mManager = manager;
		mConfig = config;
	}

	public void handleMessage(String msgBuff, DatagramPacket packet) {
		SDPHelper sdp = new SDPHelper(msgBuff);
		
		String connIP = sdp.getConnectionIP();
		int mediaPort = sdp.getMediaPort();
		
		String sipServerIP = mConfig.getSipServerAddress().toString();
		
		if(sipServerIP.equals(connIP)) {
			sdp.replaceConnectionLine(mConfig.getLocalTunnelIPAddress());
		}
	}

}
