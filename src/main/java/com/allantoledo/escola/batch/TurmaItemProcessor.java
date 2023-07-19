package com.allantoledo.escola.batch;

import org.springframework.batch.item.ItemProcessor;

import com.allantoledo.escola.model.Turma;

public class TurmaItemProcessor implements ItemProcessor<Turma, Turma> {

	@Override
	public Turma process(Turma item) throws Exception {
		var turmaProcessada = new Turma();
		turmaProcessada.setId(item.getId());
		turmaProcessada.setNome(item.getNome().toUpperCase());
		turmaProcessada.setNumeroAlunos(Math.abs(item.getNumeroAlunos()));
		return turmaProcessada;
	}

}
