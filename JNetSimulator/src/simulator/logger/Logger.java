package simulator.logger;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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
				sLoggerFrame.addLog(formatLog(log));				
			}
		});
	}
	
	public static synchronized void println(final Object ob) {
		sExecutor.execute(new Runnable() {			
			@Override
			public void run() {
				sLoggerFrame.addLog(formatLog(ob.toString()));
			}
		});
	}
	
	public static synchronized void println(final Throwable e) {
		sExecutor.execute(new Runnable() {
			@Override
			public void run() {
				sLoggerFrame.addLog(formatLog(e.toString()));
				StackTraceElement[] stackTrace = e.getStackTrace();
				for(StackTraceElement elem : stackTrace){
					sLoggerFrame.addLog("\t"+elem.toString()+"\n");
				}				
			}
		});
	}
	
	private static String formatLog(String log) {
		Date date = new Date();
		DateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");
		String dateFormatted = formatter.format(date);
		
		return "["+dateFormatted + "] " + log +"\n";
	}
}
