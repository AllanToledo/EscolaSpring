package com.allantoledo.escola.controller;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.allantoledo.escola.model.Turma;
import com.allantoledo.escola.repository.TurmaRepository;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validator;

@Controller
@RequestMapping("/turma")
public class TurmaController {

	@Autowired
	private TurmaRepository turmaRepository;

	@Autowired
	private Validator validator;

	@GetMapping
	public String mostraForm(Turma turma) {
		return "add-turma";
	}

	@PostMapping
	public ResponseEntity<String> addTurma(Turma turma) {
		Set<ConstraintViolation<Turma>> violations = validator.validate(turma);

		if (!violations.isEmpty()) {
			// Com violações de validação, retorna erro
			String menProblema = violations.stream().map(ConstraintViolation::getMessage)
					.collect(Collectors.joining(", "));
			return ResponseEntity.badRequest().body(menProblema);
		}
		turmaRepository.save(turma);
		ModelAndView modelAndView = new ModelAndView("add-turma");
		modelAndView.addObject("mensagem", "Salvo com sucesso!");
		// Sem problemas na validação, então a inserção com sucesso
		return ResponseEntity.ok("Sucesso na criação da Turma");
	}

	@GetMapping("/lista")
	public ModelAndView listaTurma() {
		ModelAndView modelAndView = new ModelAndView("lista");
		List<Turma> turmas = turmaRepository.findAll();
		modelAndView.addObject("turmas", turmas);
		return modelAndView;
	}

	@GetMapping("/remover/{id}")
	public String removerTurma(@PathVariable("id") Long id) {
		turmaRepository.deleteById(id);
		return "redirect:/turma/lista";
	}

	@Autowired
	JobLauncher jobLauncher;

	@Autowired
	Job job;

	@GetMapping("/lerarquivo")
	public ResponseEntity lerArquivo() {
		JobParameters params = new JobParametersBuilder()
				.addString("JobID", String.valueOf(System.currentTimeMillis()))
				.toJobParameters();
		try {
			jobLauncher.run(job, params);
		} catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException
				| JobParametersInvalidException e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().build();
		}
		return ResponseEntity.ok().body("sucess");

	}

}
