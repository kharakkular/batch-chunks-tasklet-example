package com.ksk.chunkAndTasklet.tasklets;

import java.time.LocalDate;
import java.time.Period;
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
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;

import com.ksk.chunkAndTasklet.model.Line;

public class LineProcessor implements Tasklet, StepExecutionListener{
	Logger logger = LogManager.getLogger(LineProcessor.class);
	
	private List<Line> lines;
	@Override
	public void beforeStep(StepExecution stepExecution) {
		ExecutionContext context = stepExecution.getJobExecution().getExecutionContext();
		this.lines = (List<Line>) context.get("lines");
		logger.info("Initilized the lines arraylist from the context with length: {}", lines);
	}
	
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		for (Line line : lines) {
			long age =  Period.between(line.getDob(), LocalDate.now()).getYears();
			line.setAge(age);
			logger.info("Calculated age for D.O.B: {} is {} years", line.getDob(), age);
		}
		return RepeatStatus.FINISHED;
	}
	
	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		logger.info("Processing has completed");
		return ExitStatus.COMPLETED;
	}

}
