package simulator.tunnel.mediastream;

import java.net.DatagramPacket;

public class MediaPacket {
	
	byte[] payload;
	long recvTimestamp;
	long sendTimestamp;

	public MediaPacket(DatagramPacket packet, long recvTime) {
		recvTimestamp = recvTime;
		payload = new byte[packet.getLength()];
		System.arraycopy(packet.getData(), 0, payload, 0, packet.getLength());
	}
}
