package simulator.tunnel.mediastream;

import java.util.ArrayList;

import simulator.tunnel.network.UDPSocketInfo;
import simulator.tunnel.signalling.SIPTunnelConfig;

public class TunnelStreamManager {
	
	private SIPTunnelConfig mConfig;
	
	private ArrayList<TunnelStream> mTunnelMediaStreams;

	public TunnelStreamManager(SIPTunnelConfig config) {
		mConfig = config;
		
		mTunnelMediaStreams = new ArrayList<TunnelStream>();
	}
	
	
	public int onChangedSDPEvent(UDPSocketInfo prev) {
		
		TunnelStream stream = getAvailableTunnelStream();
		int mediaPort;
		
		if(stream == null) {
			mediaPort = mConfig.getStreamPortRangeBegin();
			
			stream = new TunnelStream(mediaPort, 
					mConfig.getTunnelStreamSettings());
			
		} else {
			mediaPort = stream.getPort();
			stream.start();
		}
				
		return mediaPort;
	}


	private TunnelStream getAvailableTunnelStream() {
		for(TunnelStream s:mTunnelMediaStreams){
			if(!s.isRunning()) {
				return s;
			}
		}
		return null;
	}


	public int requestPortNumber() {
		return 0;
	}
}
