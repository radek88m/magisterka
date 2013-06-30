package simulator.tunnel.signalling;

import java.net.DatagramPacket;

public interface ISIPIncomingMessageHandler {
	
	// Returns true if packet can be handled by this object
	boolean onIncomingPacket(SIPTunnel tunnel, String msgBuff, DatagramPacket packet);
	long timeSinceLastHandledMessage(); // Time in [ms]
}
