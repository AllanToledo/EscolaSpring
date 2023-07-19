package com.allantoledo.escola.configuration;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import com.allantoledo.escola.batch.JobCompletionNotificationListener;
import com.allantoledo.escola.batch.TurmaItemProcessor;
import com.allantoledo.escola.model.Turma;

@Configuration
public class BatchConfigurarion {

	@Bean
	public FlatFileItemReader<Turma> reader() {
		return new FlatFileItemReaderBuilder<Turma>()
				.name("turmaFileReader")
				.resource(new ClassPathResource("turmas.csv"))
				.delimited()
				.names("id", "nome", "numeroAlunos")
				.fieldSetMapper(new BeanWrapperFieldSetMapper<Turma>() {
					{
						setTargetType(Turma.class);
					}
				})
				.build();
	}

	@Bean
	public TurmaItemProcessor processor() {
		return new TurmaItemProcessor();
	}

	@Bean
	public JdbcBatchItemWriter<Turma> writer(DataSource dataSource) {
		return new JdbcBatchItemWriterBuilder<Turma>()
			.itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
			.sql("INSERT INTO turma (id, nome, numero_alunos) VALUES (:id, :nome, :numeroAlunos)")
			.dataSource(dataSource)
			.build(); 
	}
	
	@Bean
	public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager, JdbcBatchItemWriter writer) {
		return new StepBuilder("step1", jobRepository)
			.<Turma, Turma>chunk(10, transactionManager)
			.reader(reader())
			.processor(processor())
			.writer(writer)
			.build();
	} 
	
	@Bean
	public JobCompletionNotificationListener listener(JdbcTemplate jdbcTemplate) {
		return new JobCompletionNotificationListener(jdbcTemplate);
	}
	
	@Bean
	public Job importUserJob(JobRepository jobRepository, JobCompletionNotificationListener listener, Step step1) {
		return new JobBuilder("importUserJob", jobRepository)
			.incrementer(new RunIdIncrementer())
			.listener(listener)
			.flow(step1)
			.end()
			.build(); 
	}
}
