package com.ksk.chunkAndTasklet.chunks;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import com.ksk.chunkAndTasklet.model.Line;
import com.ksk.chunkAndTasklet.utils.FileUtils;

public class LineReader implements ItemReader<Line>, StepExecutionListener{
	Logger logger = LogManager.getLogger(LineReader.class);
	
	private FileUtils fileUtils;
	
	@Override
	public void beforeStep(StepExecution stepExecution) {
		fileUtils = new FileUtils("tasklets-vs-chunks.csv");
		logger.info("Initialized fileUtils class for reading the line");
	}
	
	@Override
	public Line read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		Line line = fileUtils.readLine();
		logger.info("Read line: {}", line);
		return line;
	}
	
	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		try {
			fileUtils.closeReader();
			logger.info("Closed fileUtils reader");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ExitStatus.COMPLETED;
	}
}
