package simulator.tunnel.signalling;

import simulator.tunnel.mediastream.TunnelStreamManager;
import simulator.tunnel.network.UDPSocketInfo;

public class SIPInviteSessionHandler {

	SIPTunnelConfig mConfig;
	TunnelStreamManager mManager;

	public SIPInviteSessionHandler(SIPTunnelConfig config, TunnelStreamManager manager) {
		mManager = manager;
		mConfig = config;
	}

	public String handleMessage(String inputMsg, String diallogCallId) {
		try { 
			SDPHelper sdp = new SDPHelper(inputMsg);

			String connIP = sdp.getConnectionIP();
			int mediaPort = sdp.getMediaPort();

			String tunnelIP = mConfig.getLocalTunnelIPAddress();
			

			if(!tunnelIP.equals(connIP)) {
				UDPSocketInfo replacedInfo = new UDPSocketInfo(connIP, mediaPort);

				int localMediaPort = mManager.onChangedSDPEvent(replacedInfo);
				
				String outMsg = sdp.replaceConnectionLine(mConfig.getLocalTunnelIPAddress());
				outMsg = new SDPHelper(outMsg).replaceMediaPort(localMediaPort);
				
				return outMsg;
			}		
		} catch (Exception e) {
		}

		return inputMsg;
	}

}
