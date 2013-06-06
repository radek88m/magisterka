package simulator.audio;


public class GSMCodecMode extends AudioCodecMode {
	
	public GSMCodecMode() {		
		super();
		mInputBytesPerFrame = 80;
		mOutputBytesPerFrame = 33;
		mTimePerFrame = 10;
		mFramePtime = 10;
		mFramesPerPacket = 1;
		mClockRate = 8000;
	}
	
}