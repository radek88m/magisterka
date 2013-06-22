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
	
	private int byesReceived = 0;
	
	private Logger mLogger;
	
	private boolean isRunning;
	
	public TunnelStreamHolder(int rtpPort, int rtcpPort, TunnelStreamSettings settings, Logger logger) {
		logMessage("Listening: \n   RTP port: "+rtpPort+"\n   RTCP: "+rtcpPort);
		mRtpPort = rtpPort;
		mRtCpPort = rtcpPort;
		mSettings = settings;
		mLogger = logger;
	}
	
	public void startStreams(){
		if(isRunning) return;
		
		logMessage("Start streams");
		
		mRTPStream = new TunnelStream(mRtpPort, mSettings, mLogger);
		mRTPStream.start();
		
		mRTCPStream = new TunnelStream(mRtCpPort, mSettings, mLogger);
		mRTCPStream.start();
		
		isRunning = true;
	}
	
	public void stopStreams(){
		if(!isRunning) return;
		
		logMessage("Stop streams");
		
		if(mRTPStream != null) {
			mRTPStream.stop();
			mRTPStream = null;
		}
		
		if(mRTCPStream != null) {
			mRTCPStream.stop();
			mRTCPStream = null;
		}
		isRunning = false;
	}
	
	public void addCallLeg(String legIP) {
		if(hasFirstCallLegIP == null) {
			hasFirstCallLegIP = legIP;
			logMessage("First Call Leg added: "+legIP);
			return;
		}
		if(hasSecondCallLegIP == null) {
			hasSecondCallLegIP = legIP;
			logMessage("Second Call Leg added: "+legIP);
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
	
	private void logMessage(String str) {
		if(mLogger != null)
			mLogger.println(TunnelStreamHolder.class.getSimpleName().toString()+": "+str);
	}
}
