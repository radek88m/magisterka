package simulator.tunnel.signalling;

import java.net.InetAddress;
import java.net.UnknownHostException;

import simulator.tunnel.mediastream.TunnelStreamSettings;
import simulator.tunnel.network.UDPSocketInfo;

public class SIPTunnelConfig {
	
	private UDPSocketInfo mSipServerSocketInfo;
	
	private UDPSocketInfo mLocalSocketInfo;
	
	private int mPortRangeBegin;
	private int mPortRangeEnd;
	
	private TunnelStreamSettings mSettings;

	private boolean sipMessageLoggingEnable;

	private boolean mediaFlowLoggingEnable;
	
	public SIPTunnelConfig(String sipServerDomain, int sipServerPort, int localSipPort) {
		try {
			InetAddress addr = InetAddress.getByName(sipServerDomain);			
			mSipServerSocketInfo = new UDPSocketInfo(addr, sipServerPort);

			
			mLocalSocketInfo = new UDPSocketInfo(InetAddress.getLocalHost(), localSipPort);
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	public void setStreamPortRange(int beginPort, int endPort) {
		mPortRangeBegin = beginPort;
		mPortRangeEnd = endPort;
	}	

	public int getStreamPortRangeBegin() {
		return mPortRangeBegin;
	}
	
	public int getStreamPortRangeEnd() {
		return mPortRangeEnd;
	}
	
	public InetAddress getLocalTunnelInetAddress() {
		return mLocalSocketInfo.getAddress();
	}
	
	public String getLocalTunnelIPAddress() {
		return mLocalSocketInfo.getAddress().getHostAddress();
	}

	public int getLocalTunnelSipPort() {
		return mLocalSocketInfo.getPort();
	}

	public int getSipServerPort() {
		return mSipServerSocketInfo.getPort();
	}

	public InetAddress getSipServerAddress() {
		return mSipServerSocketInfo.getAddress();
	}

	public String getSipServerDomain() {
		return mSipServerSocketInfo.toString();
	}
	
	public UDPSocketInfo getSipServerSocketInfo() {
		return mSipServerSocketInfo;
	}

	public TunnelStreamSettings getTunnelStreamSettings() {
		return mSettings;
	}

	public void setTunnelStreamSettings(TunnelStreamSettings mSettings) {
		this.mSettings = mSettings;
	}

	public void setTraceSipMessage(boolean enable) {
		sipMessageLoggingEnable = enable;
	}
	
	public boolean traceSipMessage() {
		return sipMessageLoggingEnable;
	}
	
	public void setTraceMediaFlow(boolean enable) {
		mediaFlowLoggingEnable = enable;
	}
	
	public boolean traceMediaFlow() {
		return mediaFlowLoggingEnable;
	}
}
