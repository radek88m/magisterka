package simulator;

import simulator.logger.Logger;
import simulator.tunnel.mediastream.TunnelStreamSettings;
import simulator.tunnel.signalling.SIPTunnel;
import simulator.tunnel.signalling.SIPTunnelConfig;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		new Logger();
		
		int LOCAL_PORT = 666;
		int PORT_RANGE_BEGIN = 20000;
		int PORT_RANGE_END = 30000;
		
		String SIP_SERVER = "192.168.0.108";
		
		SIPTunnelConfig config = new SIPTunnelConfig(SIP_SERVER, 5060, LOCAL_PORT);	
		config.setStreamPortRange(PORT_RANGE_BEGIN, PORT_RANGE_END);
		
		config.setTunnelStreamSettings(new TunnelStreamSettings());
		
		SIPTunnel tunnel = new SIPTunnel(config);
		
		tunnel.startTunnel();
		
	}

}
