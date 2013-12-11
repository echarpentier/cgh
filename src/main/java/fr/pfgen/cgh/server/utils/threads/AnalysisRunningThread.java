package fr.pfgen.cgh.server.utils.threads;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import fr.pfgen.cgh.server.utils.ServerUtils;

public class AnalysisRunningThread extends Thread {
	private ExecutorService threadExecutor;
	private File analysisFolder;
	private File uploadedFile;
	
	public AnalysisRunningThread(ExecutorService threadExecutor, File analysisFolder, File uploadedFile){
		this.threadExecutor = threadExecutor;
		this.analysisFolder = analysisFolder;
		this.uploadedFile = uploadedFile;
	}
	
	@Override
	public void run(){
		try {
			threadExecutor.awaitTermination(4, TimeUnit.HOURS);
		}catch (InterruptedException e) {
			e.printStackTrace();
		}finally{
			ServerUtils.deleteDirectory(analysisFolder);
			uploadedFile.delete();
		}
	}
}
