package simulator.audio;

public abstract class AudioCodecMode {
	
	protected int mInputBytesPerFrame;	 // Bytes per frame required on codec input
	protected int mOutputBytesPerFrame;	 // Bytes per frame from codec output
	protected int mTimePerFrame;		 // Number of audio samples per frame [ms]
	protected int mFramePtime;			 // Packet audio time
	protected int mFramesPerPacket;	 	 // Number of codec frames per packet
	protected int mClockRate;  			 // Codec input sample rate [Hz]
	
	AudioCodecMode() {};
	
	AudioCodecMode(int inputBytesPerFrame, int outputBytesPerFrame, 
			int samplesPerFrame, int framePtime, int framesPerPacket, int clockRate) {		
		mInputBytesPerFrame = inputBytesPerFrame;
		mOutputBytesPerFrame = outputBytesPerFrame;
		mTimePerFrame = samplesPerFrame;
		mFramePtime = framePtime;
		mFramesPerPacket = framesPerPacket;
		mClockRate = clockRate;		
	}

	public int getInputBytesPerFrame() {
		return mInputBytesPerFrame;
	}
	
	public int getOutputBytesPerFrame() {
		return mOutputBytesPerFrame;
	}
	
	public int getSamplesPerFrame() {
		return mTimePerFrame;
	}
	
	public int getFramePtime() {
		return mFramePtime;
	}
	
	public int getFramesPerPacket() {
		return mFramesPerPacket;
	}
	
	public int getClockRate() {
		return mClockRate;
	}	
}
