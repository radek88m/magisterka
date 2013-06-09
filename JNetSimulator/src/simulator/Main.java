package simulator;

import simulator.logger.Logger;
import simulator.tunnel.mediastream.TunnelBase;
import simulator.tunnel.mediastream.TunnelSettings;
import simulator.tunnel.mediastream.audio.GSMCodecMode;
import simulator.tunnel.mediastream.dummy.DummyStream;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		new Logger();
		
		int LOCAL_PORT = 666;
		
		int STREAM_PORT_START = 30000;
		
		TunnelSettings settings = new TunnelSettings();
		TunnelBase tunnel = new TunnelBase(LOCAL_PORT, settings);
		
		tunnel.start();
		
		DummyStream stream1 = new DummyStream(STREAM_PORT_START, new GSMCodecMode());
		DummyStream stream2 = new DummyStream(STREAM_PORT_START+1, new GSMCodecMode());
		
		stream1.setRemoteDestination("localhost", LOCAL_PORT);
		stream2.setRemoteDestination("localhost", LOCAL_PORT);
		
		stream1.start();
		stream2.start();
	}

}
