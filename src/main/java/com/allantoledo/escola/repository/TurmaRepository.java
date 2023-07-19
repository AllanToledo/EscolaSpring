package com.allantoledo.escola.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.allantoledo.escola.model.Turma;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;


public interface TurmaRepository extends JpaRepository<Turma, Long> {
    @Query(
            value = "SELECT * FROM turma WHERE nome = ?1",
            nativeQuery = true)
    Optional<Turma> findByNome(String nome);
}

