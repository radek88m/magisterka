package simulator.tunnel.mediastream;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class TunnelStreamManipulator extends Thread {

	private TunnelStreamHandler mStreamHandler;
	private TunnelStreamSettings mSettings;
	private boolean isRunning = true;
	
	private long mPacketDelay;
	private int mPacketLossPercentage;
	
	private Random mRandomGenerator;
	
	private ArrayList<MediaPacket> mPacketsToSend;
	
	public TunnelStreamManipulator(TunnelStreamHandler handler, TunnelStreamSettings settings) {
		mStreamHandler = handler;
		mSettings = settings;
		
		mPacketDelay = mSettings.delay;
		mPacketLossPercentage = mSettings.lossPercentage;
		
		mPacketsToSend = new ArrayList<MediaPacket>();
		mRandomGenerator = new Random(System.currentTimeMillis());
	}

	@Override
	public void run() {
		super.run();
		while(isRunning) {
			processPackets();
		}
	}

	private void processPackets() {
		MediaPacket mediaPacket = mStreamHandler.requestPacketFromQueue();
		if(mediaPacket == null) return;
		
		// Packet dropped
		if(applyPacketLoss()) return;
		
		applyPacketDelay(mediaPacket);
		
		if(applyPacketJitter(mediaPacket))	
			sendPackets();
	}

	private void sendPackets() {
		long currTime = System.currentTimeMillis();
		Iterator<MediaPacket> iter = mPacketsToSend.iterator();
		while(iter.hasNext()) {
			MediaPacket mediaPacket = iter.next();
			if(currTime > mediaPacket.sendTimestamp) {
				mStreamHandler.sendToDestination(mediaPacket.payload);
				iter.remove();
			}
		}
	}

	public void stopRunning() {
		isRunning = false;
	}
		
	private boolean applyPacketLoss() {
		if(mPacketLossPercentage == 0) return false;
		if(mRandomGenerator.nextInt(100) < mPacketLossPercentage) {
			return true;
		} else {
			return false;
		}
	}
	
	private void applyPacketDelay(MediaPacket mediaPacket) {
		mediaPacket.sendTimestamp = mediaPacket.recvTimestamp + mPacketDelay;
	}
	
	private boolean applyPacketJitter(MediaPacket mediaPacket) {
		// TODO
		return mPacketsToSend.add(mediaPacket);
	}
	
	private void applyBandwidthLimit() {
		// TODO
	}
}

