package simulator.gui.logger;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public final class Logger {
	
	public interface ILoggerPrinter {
		void setPrinterTitle(String formatLog);
		void addLog(String formatLog);
		public void clearLogs();
	}
	
	private ILoggerPrinter mLoggerOutput;	
	private Executor mExecutor = Executors.newSingleThreadExecutor();
	
	public Logger(ILoggerPrinter output){
		mLoggerOutput = output;
	}
	
	public Logger(){
		LoggerStandaloneFrame outputFrame = new LoggerStandaloneFrame();
		outputFrame.setLocationRelativeTo(null);
		outputFrame.setVisible(true);
		mLoggerOutput = outputFrame;
	}
	
	public void println(final String log) {
		if(mLoggerOutput == null) return;
		mExecutor.execute(new Runnable() {
			@Override
			public void run() {
				mLoggerOutput.addLog(formatLog(log));				
			}
		});
	}
	
	public void println(final Object ob) {
		if(mLoggerOutput == null) return;
		mExecutor.execute(new Runnable() {			
			@Override
			public void run() {
				mLoggerOutput.addLog(formatLog(ob.toString()));
			}
		});
	}
	
	public void println(final Throwable e) {
		if(mLoggerOutput == null) return;
		mExecutor.execute(new Runnable() {
			@Override
			public void run() {
				mLoggerOutput.addLog(formatLog(e.toString()));
				StackTraceElement[] stackTrace = e.getStackTrace();
				for(StackTraceElement elem : stackTrace){
					mLoggerOutput.addLog("\t"+elem.toString()+"\n");
				}				
			}
		});
	}
	
	public void clearLogs() {
		if(mLoggerOutput == null) return;
		mLoggerOutput.clearLogs();
	}
	
	private static String formatLog(String log) {
		Date date = new Date();
		DateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");
		String dateFormatted = formatter.format(date);
		
		return "["+dateFormatted + "] " + log +"\n";
	}
}
