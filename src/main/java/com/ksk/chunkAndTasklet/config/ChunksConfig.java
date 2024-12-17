package com.ksk.chunkAndTasklet.config;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.transaction.PlatformTransactionManager;

import com.ksk.chunkAndTasklet.chunks.LineProcessor;
import com.ksk.chunkAndTasklet.chunks.LineReader;
import com.ksk.chunkAndTasklet.chunks.LineWriter;
import com.ksk.chunkAndTasklet.model.Line;

@Configuration
@PropertySource("application.properties")
public class ChunksConfig {
	Logger logger = LogManager.getLogger(ChunksConfig.class);
	
	@Autowired
	private Environment env;
	
	// LineReader
	@Bean
	public ItemReader<Line> getItemReader() {
		return new LineReader();
	}
	
	//LineProcessor
	@Bean
	public ItemProcessor<Line, Line> getItemProcessor() {
		return new LineProcessor();
	}
	
	//LineWriter
	@Bean
	public ItemWriter<Line> getItemWriter() {
		return new LineWriter();
	}
	
	// job launcher
	@Bean(name = "chunkJobLauncher")
	public JobLauncher getJobLauncher(JobRepository jobRepository) throws Exception {
		TaskExecutorJobLauncher launcher = new TaskExecutorJobLauncher();
		launcher.setJobRepository(jobRepository);
		launcher.afterPropertiesSet();
		return launcher;
	}
	
	// JobRepository
	@Bean
	public JobRepository getJobRepository(DataSource dataSource, PlatformTransactionManager transactionManager) throws Exception {
		JobRepositoryFactoryBean factoryBean = new JobRepositoryFactoryBean();
		factoryBean.setDataSource(dataSource);
		factoryBean.setTransactionManager(transactionManager);
		factoryBean.afterPropertiesSet();
		return factoryBean.getObject();
	}
	// job
	@Bean("chunkJob")
	public Job getJob(JobRepository jobRepository, PlatformTransactionManager transactionManager, @Qualifier("step") Step step) {
		return new JobBuilder("chunkJob", jobRepository)
				.preventRestart()
				.start(step)
				.build();
	}
	// step
	@Bean("step")
	public Step step(JobRepository jobRepository, PlatformTransactionManager transactionManager, 
					ItemReader<Line> reader, ItemProcessor<Line, Line> processor, ItemWriter<Line> writer) {
		return new StepBuilder("step", jobRepository)
				.<Line, Line>chunk(2, transactionManager)
				.reader(reader)
				.processor(processor)
				.writer(writer)
				.build();
	}
	// DataSource
	@Bean
	public DataSource getDataSource() {
		BasicDataSource dataSource = new BasicDataSource();
//		dataSource.setDriverClassName("database.driverClassName");
		dataSource.setUrl(env.getProperty("database.url"));
		dataSource.setUsername(env.getProperty("database.username"));
		dataSource.setPassword(env.getProperty("database.password"));
		return dataSource;
	}
	// PlatformTransactionManager
	@Bean
	public PlatformTransactionManager getPlatformTransactionManager() {
		return new ResourcelessTransactionManager();
	}
}
