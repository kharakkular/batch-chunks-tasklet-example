package com.ksk.chunkAndTasklet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.ksk.chunkAndTasklet.config.TaskletConfig;

public class SpringBatchChunkAndTaskletApp {
	private static Logger logger = LogManager.getLogger(SpringBatchChunkAndTaskletApp.class);
	public static void main(String[] args) {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		context.register(TaskletConfig.class);
		context.refresh();
		
		JobLauncher launcher = (JobLauncher) context.getBean("jobLauncher");
		Job job = (Job) context.getBean("taskletJob");
		
		JobParameters jobParameters = new JobParametersBuilder().addString("jobId", String.valueOf(System.currentTimeMillis())).toJobParameters();
		

		try {
			JobParameters jobparameters = new JobParametersBuilder().addString("jobId", String.valueOf(System.currentTimeMillis()))
												.toJobParameters();
			final JobExecution execution = launcher.run(job, jobparameters);
			logger.info("Job Status: {}", execution.getStatus());
		} catch( final Exception ex) {
			ex.printStackTrace();
			logger.error("Job failed {}", ex.getMessage());
		}
		
	}

}
