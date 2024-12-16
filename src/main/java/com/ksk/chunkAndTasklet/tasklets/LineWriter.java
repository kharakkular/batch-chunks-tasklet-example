package com.ksk.chunkAndTasklet.tasklets;

import java.io.IOException;
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
import org.springframework.scheduling.support.TaskUtils;

import com.ksk.chunkAndTasklet.model.Line;
import com.ksk.chunkAndTasklet.utils.FileUtils;

public class LineWriter implements Tasklet, StepExecutionListener {
	Logger logger = LogManager.getLogger(LineReader.class);
	
	private List<Line> lines;
	private FileUtils fileUtils;
	
	@Override
	public void beforeStep(StepExecution stepExecution) {
		lines = (List<Line>) stepExecution.getJobExecution().getExecutionContext().get("lines");
		fileUtils = new FileUtils("output.csv");
		logger.info("Initialized line writers line arraylist and fileutils class");
	}
	
	@Override
	public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) {
		for (Line line : lines) {
			try {
				fileUtils.writeLine(line);
				logger.info("Wrote line: {}", line.toString());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return RepeatStatus.FINISHED;
	}
	
	@Override
	public ExitStatus afterStep(StepExecution execution) {
		try {
			fileUtils.closeWriter();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("Writen to the file");
		return ExitStatus.COMPLETED;
	}
}
