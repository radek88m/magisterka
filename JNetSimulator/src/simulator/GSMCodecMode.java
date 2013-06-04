package simulator;

public class GSMCodecMode extends AudioCodecMode {
	
	GSMCodecMode() {		
		super();
		mInputBytesPerFrame = 80;
		mOutputBytesPerFrame = 33;
		mSamplesPerFrame = 10;
		mFramePtime = 10;
		mFramesPerPacket = 1;
		mClockRate = 8000;
	}
	
}