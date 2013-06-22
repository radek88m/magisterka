package simulator.tunnel.mediastream;

import java.util.ArrayList;

import simulator.gui.logger.Logger;
import simulator.tunnel.network.UDPSocketInfo;
import simulator.tunnel.signalling.SIPTunnelConfig;

public class TunnelStreamManager {
	
	private SIPTunnelConfig mConfig;
	
	private ArrayList<TunnelStreamHolder> mTunnelMediaHolders;
	
	private int mStartPort;

	public TunnelStreamManager(SIPTunnelConfig config) {
		mConfig = config;
		mStartPort = mConfig.getStreamPortRangeBegin();
		mTunnelMediaHolders = new ArrayList<TunnelStreamHolder>();
	}
	
	
	public int onChangedSDPEvent(String transactionCallId) {
		
		String callLeg = transactionCallId;
		
		TunnelStreamHolder streamHolder;
		int mediaPort; 
		
		if((streamHolder = handlerHasCallLeg(callLeg)) == null) {
			if((streamHolder = getAvailableTunnelStream(callLeg)) == null) {			
				streamHolder = new TunnelStreamHolder(mStartPort, mStartPort+1,
						mConfig.getTunnelStreamSettings());
				
				streamHolder.addCallLeg(callLeg);
				Logger.println("Dodaje stream holdera kurde: call leg:"+callLeg);
				mTunnelMediaHolders.add(streamHolder);
				
				mediaPort = mStartPort;
				
				mStartPort += 2;
				
			} else {
				Logger.println("Dodaje drom nogem: call leg:"+callLeg);
				mediaPort = streamHolder.getRtpPort();
				streamHolder.addCallLeg(callLeg);
				streamHolder.startStreams();
			}
		} else {
			Logger.println("Juz stworzony:"+callLeg);
			mediaPort = streamHolder.getRtpPort();
		}
				
		return mediaPort;
	}
	
	private TunnelStreamHolder handlerHasCallLeg(String legIP) {
		for(TunnelStreamHolder s:mTunnelMediaHolders){
			if(s.hasCallLeg(legIP)) {
				return s;
			}
		}
		return null;
	}

	private TunnelStreamHolder getAvailableTunnelStream(String legIP) {
		for(TunnelStreamHolder s:mTunnelMediaHolders){
			if(!s.hasBothLegs()) {
				return s;
			}
		}
		return null;
	}
	

	public void onByeReceived(String diallogCallId) {
		TunnelStreamHolder streamHolder;
		if((streamHolder = handlerHasCallLeg(diallogCallId)) != null) {
			streamHolder.stopStreams();
			mTunnelMediaHolders.remove(streamHolder);
		}
		
	}

	public int requestPortNumber() {
		return 0;
	}


}
