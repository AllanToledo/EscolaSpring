package com.allantoledo.escola;

import com.allantoledo.escola.controller.TurmaController;
import com.allantoledo.escola.model.Turma;
import com.allantoledo.escola.repository.TurmaRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class EscolaApplicationTests {

	@Autowired
	MockMvc mockMvc;

	@InjectMocks
	TurmaController turmaController;

	@Autowired
	TurmaRepository turmaRepository;

	@Test
	void contextLoads() {
		System.out.println("Testes estão funcionando!");
	}

	@Test
	void insertTurma() throws Exception {
		Turma turma = new Turma();
		turma.setNome("Turma TESTE");
		turma.setNumeroAlunos(10);
		MvcResult mvcResult = mockMvc.perform(
				MockMvcRequestBuilders.post("/turma")
						.param("nome", turma.getNome())
						.param("numeroAlunos", String.valueOf(turma.getNumeroAlunos()))
		).andExpect(status().isOk()).andReturn();
		var response = mvcResult.getResponse().getContentAsString();
		assertThat(response).isEqualTo("Sucesso na criação da Turma");
		Turma turmaFromDb = turmaRepository.findByNome(turma.getNome()).orElse(null);
		assertThat(turmaFromDb).isNotNull();
		turmaRepository.delete(turmaFromDb);
	}

}
