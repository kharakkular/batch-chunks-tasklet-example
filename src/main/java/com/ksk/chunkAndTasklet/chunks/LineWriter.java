package com.ksk.chunkAndTasklet.chunks;

import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

import com.ksk.chunkAndTasklet.model.Line;
import com.ksk.chunkAndTasklet.utils.FileUtils;

public class LineWriter implements ItemWriter<Line>, StepExecutionListener {
	Logger logger = LogManager.getLogger(LineWriter.class);

	private FileUtils fileUtils;
	
	@Override
	public void beforeStep(StepExecution stepExecution) {
		fileUtils = new FileUtils("output/output-chunks.csv");
		logger.info("Initialized fileUtils class for writing");
	}
	
	@Override
	public void write(Chunk<? extends Line> chunk) throws Exception {
		List<? extends Line> items = chunk.getItems();
		for (Line line : items) {
			fileUtils.writeLine(line);
			logger.info("Writing to file line: {}", line);
		}
	}
	
	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		try {
			fileUtils.closeWriter();
			logger.info("Closing FileUtils writer");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ExitStatus.COMPLETED;
	}
}
