package com.ksk.chunkAndTasklet.config;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.database.support.DataFieldMaxValueIncrementerFactory;
import org.springframework.batch.item.database.support.DefaultDataFieldMaxValueIncrementerFactory;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.transaction.PlatformTransactionManager;

import com.ksk.chunkAndTasklet.tasklets.LineWriter;
import com.ksk.chunkAndTasklet.tasklets.LineProcessor;
import com.ksk.chunkAndTasklet.tasklets.LineReader;

@PropertySource("/application.properties")
@Configuration
public class TaskletConfig {

	@Autowired
	private Environment env;
	
	@Bean
	public LineReader lineReader() {
		return new LineReader();
	}
	
	@Bean
	public LineProcessor lineProcessor() {
		return new LineProcessor();
	}
	
	@Bean
	public LineWriter lineWriter() {
		return new LineWriter();
	}
	
	@Bean
	public DataSource dataSource() {
		BasicDataSource dataSource = new BasicDataSource();
//		dataSource.setDriverClassName("database.driverClassName");
		dataSource.setUrl(env.getProperty("database.url"));
		dataSource.setUsername(env.getProperty("database.username"));
		dataSource.setPassword(env.getProperty("database.password"));
		
		dataSource.setValidationQuery("SELECT 1");
		dataSource.setTestOnBorrow(true);
		return dataSource;
	}
    
    @Bean
    public PlatformTransactionManager transactionManager() {
    	return new ResourcelessTransactionManager();
    }
	
	@Bean
	protected Step readLines(JobRepository jobRepository, PlatformTransactionManager manager) {
		return new StepBuilder("readLines", jobRepository)
				.tasklet(lineReader(), manager)
				.build();
	}
	
	@Bean
	protected Step processLines(JobRepository jobRepository, PlatformTransactionManager manager) {
		return new StepBuilder("processLines", jobRepository)
				.tasklet(lineProcessor(), manager)
				.build();
	}
	
	@Bean
	protected Step writeLines(JobRepository jobRepository, PlatformTransactionManager manager) {
		return new StepBuilder("writeLines", jobRepository)
				.tasklet(lineWriter(), manager)
				.build();
	}
	
	@Bean("taskletJob")
	public Job job(JobRepository jobRepository, PlatformTransactionManager manager) {
		return new JobBuilder("taskletsJob", jobRepository)
				.start(readLines(jobRepository, manager))
				.next(processLines(jobRepository, manager))
				.next(writeLines(jobRepository, manager))
				.build();
	}
	
	@Bean
    public JobRepository jobRepository() throws Exception {
    	JobRepositoryFactoryBean factoryBean = new JobRepositoryFactoryBean();
    	factoryBean.setDataSource(dataSource());
    	factoryBean.setTransactionManager(transactionManager());
//    	factoryBean.setIncrementerFactory(incrementerFactory(dataSource()));
    	factoryBean.afterPropertiesSet();
    	return factoryBean.getObject();
    }
	
	@Bean DataFieldMaxValueIncrementerFactory incrementerFactory(DataSource dataSource) {
		return new DefaultDataFieldMaxValueIncrementerFactory(dataSource);
	}
	
	@Bean("jobLauncher")
    public JobLauncher jobLauncher() throws Exception {
		SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
    	jobLauncher.setJobRepository(jobRepository());
    	return jobLauncher;
    }
	
	@Bean
	public ResourceDatabasePopulator databasePopulator(DataSource datasource) {
		ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
		populator.addScript(new ClassPathResource("schema-all.sql"));
		populator.execute(datasource);
		return populator;
	}
}
