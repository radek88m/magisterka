package simulator.tunnel.mediastream;

import simulator.gui.logger.Logger;

public class TunnelStreamHolder {

	private int mRtpPort;
	private TunnelStream mRTPStream;
	
	private int mRtCpPort;
	private TunnelStream mRTCPStream;
	
	private String hasFirstCallLegIP = null;
	private String hasSecondCallLegIP = null;
	
	TunnelStreamSettings mSettings;
	
	public TunnelStreamHolder(int rtpPort, int rtcpPort, TunnelStreamSettings settings) {
		mRtpPort = rtpPort;
		mRtCpPort = rtcpPort;
		mSettings = settings;
	}
	
	public void startStreams(){
		Logger.println("TunnelStreamHolder start streams");
		mRTPStream = new TunnelStream(mRtpPort, mSettings);
		mRTPStream.start();
		
		mRTCPStream = new TunnelStream(mRtCpPort, mSettings);
		mRTCPStream.start();
	}
	
	public void stopStreams(){
		Logger.println("TunnelStreamHolder stop streams");
		if(mRTPStream != null) {
			mRTPStream.stop();
			mRTPStream = null;
		}
		
		if(mRTCPStream != null) {
			mRTCPStream.stop();
			mRTCPStream = null;
		}
	}
	
	public void addCallLeg(String legIP) {
		if(hasFirstCallLegIP == null) {
			hasFirstCallLegIP = legIP;
			return;
		}
		if(hasSecondCallLegIP == null) {
			hasSecondCallLegIP = legIP;
			return;
		}
	}
	
	public boolean hasBothLegs() {
		return (hasFirstCallLegIP != null && hasSecondCallLegIP != null);
	}
	
	public boolean hasCallLeg(String legIP) {
		
		if(hasFirstCallLegIP != null && hasFirstCallLegIP.equals(legIP)) 
			return true;
		
		if(hasSecondCallLegIP != null && hasSecondCallLegIP.equals(legIP)) 
			return true;
		
		return false;
	}

	public int getRtpPort() {
		return mRtpPort;
	}
}
