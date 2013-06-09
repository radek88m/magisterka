package simulator.tunnel.signalling;

import java.net.InetAddress;
import java.net.UnknownHostException;

import simulator.tunnel.mediastream.TunnelStreamSettings;

public class SIPTunnelConfig {
	
	private String mSipServerDomain;
	private InetAddress mSipServerAddress;
	private int mSipServerPort;
	
	private int mLocalTunnelSipPort;
	private String mLocalTunnelIP;
	private InetAddress mLocalTunnelAddress;
	
	private int mPortRangeBegin;
	private int mPortRangeEnd;
	
	private TunnelStreamSettings mSettings;
	
	public SIPTunnelConfig(String sipServerDomain, int sipServerPort, int localSipPort) {
		try {
			mSipServerDomain = sipServerDomain;
			mSipServerPort = sipServerPort;
			
			mSipServerAddress = InetAddress.getByName(mSipServerDomain);

			mLocalTunnelAddress = InetAddress.getLocalHost();
	        mLocalTunnelIP = mLocalTunnelAddress.getHostAddress();
			
			mLocalTunnelSipPort = localSipPort;
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

	public int getLocalTunnelSipPort() {
		return mLocalTunnelSipPort;
	}

	public void setLocalTunnelSipPort(int localSipPort) {
		this.mLocalTunnelSipPort = localSipPort;
	}

	public int getSipServerPort() {
		return mSipServerPort;
	}

	public void setSipServerPort(int mSipServerPort) {
		this.mSipServerPort = mSipServerPort;
	}
	
	public InetAddress getSipServerAddress() {
		return mSipServerAddress;
	}

	public String getSipServerDomain() {
		return mSipServerDomain;
	}

	public void setSipServerDomain(String mSipServerIP) {
		this.mSipServerDomain = mSipServerIP;
	}

	public TunnelStreamSettings getTunnelStreamSettings() {
		return mSettings;
	}

	public void setTunnelStreamSettings(TunnelStreamSettings mSettings) {
		this.mSettings = mSettings;
	}
}
