package com.allantoledo.escola.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.allantoledo.escola.model.Turma;


public class JobCompletionNotificationListener implements JobExecutionListener {
	private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);
	private final JdbcTemplate jdbcTemplate;

	@Autowired
	public JobCompletionNotificationListener(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
			log.info("!!! JOB Finalizado!");
			String query = "SELECT * FROM turma;";
			jdbcTemplate.query(query, (rs, row) -> new Turma(rs.getLong(1), rs.getString(2), rs.getInt(3)))
					.forEach(rapadura -> log.info("Selecionado < {} > na base.", rapadura));
		}
	}
}