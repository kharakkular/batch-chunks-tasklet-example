package com.ksk.chunkAndTasklet.chunks;

import java.time.LocalDate;
import java.time.Period;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.item.ItemProcessor;

import com.ksk.chunkAndTasklet.model.Line;

public class LineProcessor implements ItemProcessor<Line, Line> {

	Logger logger = LogManager.getLogger(LineProcessor.class);
	
	@Override
	public Line process(Line item) throws Exception {
		LocalDate dob = item.getDob();
		long age = Period.between(dob, LocalDate.now()).getYears();
		logger.info("Age calculated for {} is {}", item.getName(), age);
		item.setAge(age);
		return item;
	}
}
