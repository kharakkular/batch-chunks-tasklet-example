package com.ksk.chunkAndTasklet.tasklets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.ksk.chunkAndTasklet.model.Line;
import com.ksk.chunkAndTasklet.utils.FileUtils;

public class LineReader implements Tasklet, StepExecutionListener {
	Logger logger = LogManager.getLogger(LineReader.class);
	private List<Line> lines;
	private FileUtils fileUtils;

	@Override
	public void beforeStep(StepExecution stepExecution) {
		fileUtils = new FileUtils("tasklets-vs-chunks.csv");
		lines = new ArrayList<Line>();
		logger.info("FileUtils class has been initialized");
	}

	@Override
	public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) {
		Line line = fileUtils.readLine();
		while (line != null) {
			lines.add(line);
			logger.info("Read line: {}", line.toString());
			line = fileUtils.readLine();

		}

		return RepeatStatus.FINISHED;
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		try {
			fileUtils.closeReader();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		stepExecution.getJobExecution().getExecutionContext().put("lines", this.lines);
		logger.info("Line reader has ended");
		return ExitStatus.COMPLETED;
	}
}
