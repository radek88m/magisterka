package simulator.logger;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public final class Logger {
	
	private static LoggerFrame sLoggerFrame;
	
	private static Executor sExecutor = Executors.newSingleThreadExecutor();
	
	public Logger(){
		sLoggerFrame = new LoggerFrame();
		sLoggerFrame.setDefaultCloseOperation(sLoggerFrame.EXIT_ON_CLOSE);
		sLoggerFrame.setLocationRelativeTo(null);
		sLoggerFrame.setVisible(true);
	}
	
	
	public static synchronized void println(final String log) {
		sExecutor.execute(new Runnable() {
			@Override
			public void run() {
				sLoggerFrame.addLog(log+"\n");				
			}
		});
	}
	
	public static synchronized void println(final Object ob) {
		sExecutor.execute(new Runnable() {			
			@Override
			public void run() {
				sLoggerFrame.addLog(ob.toString()+"\n");
			}
		});
	}
	
	public static synchronized void println(final Throwable e) {
		sExecutor.execute(new Runnable() {
			@Override
			public void run() {
				sLoggerFrame.addLog(e.toString()+"\n");
				StackTraceElement[] stackTrace = e.getStackTrace();
				for(StackTraceElement elem : stackTrace){
					sLoggerFrame.addLog("\t"+elem.toString()+"\n");
				}				
			}
		});
	}
}
