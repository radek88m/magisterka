package simulator.tunnel.mediastream;

import simulator.tunnel.network.UDPSocketInfo;

public class TunnelStreamManager {
	
	private TunnelStreamSettings mSettings;

	public TunnelStreamManager(TunnelStreamSettings settings) {
		mSettings = settings;
	}
	
	
	public void onChangedSDPEvent(UDPSocketInfo prev, UDPSocketInfo changed) {
		
	}
}
