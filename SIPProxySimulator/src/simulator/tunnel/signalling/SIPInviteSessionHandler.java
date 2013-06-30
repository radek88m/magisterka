package simulator.tunnel.signalling;

import simulator.tunnel.mediastream.TunnelStreamManager;
import simulator.tunnel.network.UDPSocketInfo;
import simulator.tunnel.signalling.SIPMessageScanner.SipMethod;

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

			if(sdp.hasSDP()) {

				String connIP = sdp.getConnectionIP();
				int mediaPort = sdp.getMediaPort();
				
				if(connIP.equals("192.168.0.100")) {
					@SuppressWarnings("unused")
					int i = 0;
					i++;
				}

				String tunnelIP = mConfig.getLocalTunnelIPAddress();

				if(!tunnelIP.equals(connIP)) {
					UDPSocketInfo replacedInfo = new UDPSocketInfo(connIP, mediaPort);

					int localMediaPort = mManager.onChangedSDPEvent(diallogCallId);

					String outMsg = sdp.replaceConnectionLine(mConfig.getLocalTunnelIPAddress());
					outMsg = new SDPHelper(outMsg).replaceMediaPort(localMediaPort);
					try { 
					outMsg = new SDPHelper(outMsg).replaceRTCPMediaLine(mConfig.getLocalTunnelIPAddress(), 
							localMediaPort+1);
					} catch (Exception e) {
						
					}
					return outMsg;
				}		
			} else {
				SIPMessageScanner scanner = new SIPMessageScanner(inputMsg);
				if(scanner.isMethod(SipMethod.BYE)) {
					mManager.onByeReceived(diallogCallId);
				}
			}
		} catch (Exception e) {
		}

		return inputMsg;
	}

}
